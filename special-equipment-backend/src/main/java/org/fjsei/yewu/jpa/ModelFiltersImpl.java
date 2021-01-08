package org.fjsei.yewu.jpa;


import org.fjsei.yewu.input.*;
import org.hibernate.Metamodel;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


//学着spring-data-jpa-2.1.4.RELEASE-sources.jar!/org/springframework/data/jpa/domain/Specifications.java来搞
//先来简单点的，预留扩展可能。


//@SuppressWarnings("deprecation")
public class ModelFiltersImpl<T> implements ModelFilters<T>, Serializable {
    private EntityManager entityManager;      //不能跨越数据库。
    //整个根查询内的
    //private static final long serialVersionUID = 1L;
    //private final @Nullable Specification<T> spec;
    private Specification<T> spec;
    private CriteriaQuery<?> query;
    private WhereTree where;
    private CriteriaBuilder cb;
    //子查询引入： A.B.#1 往前回退一层subquery对应的上层查询内的可识别属性。
    public   ArrayList<QueryDomain>    querys=new ArrayList<QueryDomain>();      //嵌套的语句:　支持子查询去关联更上面x一级别的语句。

   //每个层次专用的部分

    private class JoinInfomation{
        Join<?, ?> join;
        //String     entity;        没用过;
        public JoinInfomation(Join<?, ?> join){
            this.join=join;
            //this.entity=entity;
        }
    }

    //A.B.c 子语句和Root主语句上面的含义可能不同了，join就不同；
    //子查询subquery的属性可能引用到上级语句的字段，#1.A.b, #2.A.b往前跳出嵌套1次,2次去匹配哪儿的响应属性引用reference。
    //帮助类，分析属性名字串。
    private class ShuXingComprise {
        //A.B.#1 往前回退一层subquery对应的上层查询内的可识别属性。
        //A.B.#2，要剔除.#2；
        List<String>   ofs=new LinkedList<>();
        //    String      extend;     //.#SetSize作废了
        int     qt=0;    //往前嵌套回退几层，用在嵌套的子查询。
        String      sx;     //原始名字；

        //初始化
        public ShuXingComprise(String sx)
        {
            this.sx=sx;
            //无法分割，因为"." 、"\"、“|”是特殊字符，需要转义"\\." 、"\\\"、“\\|”。
            String[] arr = sx.split("\\.");
            for (String a : arr) {
                if (a.charAt(0) == '#') {
                    String  extend = a.substring(1);
                    qt=Integer.valueOf(extend);
                    return;
                } else {        //.#??? 抛弃
                    ofs.add(a);
                }
            }
        }
        public String subSxString(int toIndex)
        {
            int all=ofs.size();
            String sub="";
            for(int i=0; i<all &&  i<=toIndex; i++)
            {
                if(i!=0)    sub+=".";
                sub+=ofs.get(i);
            }
            return sub;
        }
        //部分的属性字符串
        public String subSxString444(int fromIndex,int toIndex) {
            return  subSxString(toIndex);
        }
    }


   public ModelFiltersImpl(@Nullable Specification<T> spec) {
        this.spec = spec;
    }

   public void  initialize(@Nullable Specification<T> spec, EntityManager entityManager) {
        this.spec = spec;
        this.entityManager=entityManager;
    }


    //??有重复join

    //过滤执行：正常的路径第一步应该运行到这里！
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if(spec == null)    return null;
        //this.root= root;
        this.query= query;
        this.cb= cb;

        Predicate predicate = cb.conjunction();
        List<Expression<Boolean>>    expressions=predicate.getExpressions();
        querys.add(new QueryDomain(root));

        //首先处理我这where逻辑。
        if(where!=null) {
            expressions = processDigui(where, expressions);
        }
         //      return spec == null ? null : spec.toPredicate(root, query, builder);
        //最后：去使用接口的特别定制代码。
        Predicate predicate_Specification= spec.toPredicate(root, query, cb);
        //折回来了：
        if(predicate_Specification!=null)
            expressions.add(predicate_Specification);
        return  predicate;
    }


    //具体对WhereTree的翻译映射处理：
    //这个对象实例，一次性的，只能支持一次JPA查询使用。
    public ModelFilters<T> effectWhereTree(WhereTree where) {
        this.where= where;
        return this;
    }
    //限制可以使用的模型类type?。
    public ModelFilters<T> effectCount(int rownum){
        return this;
    };

    //深度优先，广度优先。
    //Date =类型?     暂时递归方法。
    private List<Expression<Boolean>>   processDigui(WhereTree node, List<Expression<Boolean>> expFather) {
        //NOT只能有一个下一层节点，DSELF特别；
        if(node.getLj()==null || node.getLj()== LJ_TYPE_Enum.AND || node.getLj()== LJ_TYPE_Enum.OR)
        {
            Predicate myLogic=null;
            if(node.getLj()== LJ_TYPE_Enum.OR)
               myLogic= cb.disjunction();
            else
               myLogic= cb.conjunction();
            //缺省的也算作　AND     ；
            List<Expression<Boolean>>  myLogicExpr= myLogic.getExpressions();
            //支持多节点或独立节点的组合都允许，： a， as[], w ，ws[]；
            if(node.getA()!=null)
                myLogicExpr = processOneLeaf(node.getA(), myLogicExpr);
            if(node.getAs()!=null) {
                for(WhereLeaf ason: node.getAs()) {
                    //这里不能改成stream, 变量相互引用myLogicExpr的，递归， ！无法并行处理。
                     myLogicExpr = processOneLeaf(ason, myLogicExpr);
                }
            }
            if(node.getW()!=null)
                myLogicExpr= processDigui( node.getW() ,myLogicExpr);
            if(node.getWs()!=null) {
                for(WhereTree ason : node.getWs()) {
                    myLogicExpr = processDigui(ason, myLogicExpr);
                }
            }
            expFather.add(myLogic);
            return  expFather;
        }
        else  if(node.getLj()== LJ_TYPE_Enum.NOT)
        {
            //需要从顶层节点往后面累加，。。 ( ( () () ) () ); 对not逻辑JPA会直接转换没not掉了。
            //对接上层逻辑， 但是还要兜住  本节点的 底下的各个节点逻辑。
            Predicate myLogic= cb.conjunction();
            List<Expression<Boolean>>  myLogicExpr= myLogic.getExpressions();

            if(node.getA()!=null) {
                myLogicExpr = processOneLeaf(node.getA(), myLogicExpr);
            }
            else if(node.getW()!=null) {
                myLogicExpr= processDigui( node.getW() ,myLogicExpr);
            }
            myLogic= myLogic.not();
            //NOT 必须是 a, w当中一个；　否则逻辑= False;
            expFather.add(myLogic);
            return  expFather;
        }
        else if(node.getLj()== LJ_TYPE_Enum.DSELF)
        {
            int  level=querys.size();
            //自反符DSELF，就要自动添加嵌套的subQuery;
            Class<?>  zfClass=querys.get(level-1).root.getJavaType();
            Subquery<?> subquery=null;
            subquery = query.subquery(zfClass);
            Root<?> subRoot = subquery.from(zfClass);
            From<?, ?> subEntity=subRoot;
            QueryDomain  queryDomain=new QueryDomain();
            querys.add(queryDomain);
            queryDomain.setRoot(subEntity);
            subquery.select( (Expression)subEntity );
            //subquery.select(subEntity.get("id"));
            Predicate p = cb.conjunction();
            List<Expression<Boolean>>    expressions;
            expressions = p.getExpressions();
            level=querys.size();
            expressions.add(  cb.equal(subEntity, querys.get(level-2).root ) );
            //递归，内部处理子语句
            //  queryDomain.expressions.add( (Expression) cb.literal(" limit 11 ") );
            expressions= processDigui( node.getSup() ,expressions);
            subquery.where(p);

            level=querys.size();
            expFather.add(  cb.not( cb.exists(subquery) ) );
            //递归，内部处理结束；出来了，子语句终结，清理。
            querys.remove(level-1);
            return  expFather;
        }
        return  expFather;
    }
    //逻辑c：
    //逻辑树的最基本叶子,  运算函数表达式也位于它的下面。
    private List<Expression<Boolean>>   processOneLeaf(WhereLeaf yz, List<Expression<Boolean>> expFather) {
        int  level=querys.size();
    //    Join<?, ?> joinBase;
        //左右两手边的，　更底层的函数／数学运算／子语句／Case表达式。
        Expression<?> left=null;
        Expression<?> right=null;
        if(yz.getL()!=null)
            left=processExpressItem(yz.getL());
        if(left==null) {
            if( !StringUtils.isEmpty(yz.getS()) ) {
                left =genMathFuncSx(yz.getS());
            }
        }
        //右手边的内容。
        if(yz.getR()!=null)
            right=processExpressItem(yz.getR());
        //subQuery子语句能输出多行记录，所以很多操作符可能运行报错: "could not extract ResultSet;"。
        //集合字段是PluralAttributePath，非集合的属性是SingularAttributePath，子查询不是PluralAttributePath；
        //常见的操作符；   　有些操作符号是给集合字段　或　非集合字段专用的， 还有subQuery专用的。
        if(yz.getO()== OPERATOR_Enum.EQ || yz.getO()== OPERATOR_Enum.GE || yz.getO()== OPERATOR_Enum.LE
                  || yz.getO()== OPERATOR_Enum.NE || yz.getO()== OPERATOR_Enum.GT || yz.getO()== OPERATOR_Enum.LT)
        {
            if(left!=null) {
                if(right == null) {
                    //可以和模型实体直接比较。  expFather.add(cb.equal(left,  new Book() ) );
                    //必须正确设置类型 否则无法执行。 客户要设置对象标志　驱动的数据类型识别。
                    if(yz.getDt()!=null) {
                        //日期转去和Double大小比较，必须LiteralExpression<Date>，若直接用(Expression)Date，尽管骗过编译器，运行时报错。
                        right= new LiteralExpression<Date>( (CriteriaBuilderImpl)cb, yz.getDt() );
                    }else if(yz.getLv()!=null) {
                        right = new LiteralExpression<Long>((CriteriaBuilderImpl) cb, yz.getLv());
                    }else if(yz.getDv()!=null) {
                        right = new LiteralExpression<Double>((CriteriaBuilderImpl) cb, yz.getDv());
                    }else if(yz.getSv()!=null) {
                        right = new LiteralExpression<String>((CriteriaBuilderImpl) cb, yz.getSv());
                    }
                }
                if(right != null) {
                    if(yz.getO()== OPERATOR_Enum.EQ)
                        expFather.add(cb.equal(left, right));
                    else if(yz.getO()== OPERATOR_Enum.GE)
                        expFather.add(cb.greaterThanOrEqualTo((Expression) left, (Expression) right) );
                    else if(yz.getO()== OPERATOR_Enum.LE)
                        expFather.add(cb.lessThanOrEqualTo((Expression) left, (Expression) right) );
                    else if(yz.getO()== OPERATOR_Enum.NE)
                        expFather.add(cb.notEqual(left, right));
                    else if(yz.getO()== OPERATOR_Enum.GT)
                        expFather.add(cb.greaterThan((Expression) left, (Expression) right) );
                    else if(yz.getO()== OPERATOR_Enum.LT)
                        expFather.add(cb.lessThan((Expression) left, (Expression) right) );
                }
            }
            return expFather;
        }
        else if(yz.getO()== OPERATOR_Enum.LK) {
            //模式匹配 _ and % as wildcards. _ 单字符 and % . 若想查找 Dr_开头的, 模式串必须是 'Dr?_%' 这里的'?'=转义符号；
            //字符串的，Like相似'自带通配符'  单个字符 _ 任意个字符 %
            //转义符号，考虑直接定死 ，用 ? 就是。 都隐含采纳?符号转义针对_或%
            if(right!=null)
                expFather.add(cb.like((Expression)left,  (Expression)right, '?') );
            else
                expFather.add(cb.like((Expression)left,  yz.getSv(), '?') );
            return expFather;
        }
        else if(yz.getO()== OPERATOR_Enum.TRUE) {
            expFather.add(cb.isTrue( (Expression)left ) );
        }
        else if(yz.getO()== OPERATOR_Enum.FALSE) {
            expFather.add(cb.isFalse( (Expression)left ) );
        }
        else if(yz.getO()== OPERATOR_Enum.BTW){
            if(left!=null) {
                //这4种数据类型不同，分开。　　   支持模式之一:　l between (基本[0] , 基本[1])。
                //能支持　r下层对象 表达式与基本数据类型配合的模式：如，　l between (r , 基本)。
                //数据类型优先级顺序 date, long, double, string　若有一个忽略剩下的;
                if(yz.getDta().size() >= 2) {
                    //between 日期类型
                    Expression<Date> result1 = new LiteralExpression<Date>((CriteriaBuilderImpl) cb, yz.getDta().get(0));
                    Expression<Date> result2 = new LiteralExpression<Date>((CriteriaBuilderImpl) cb, yz.getDta().get(1));
                    //必须设置Date类型, 否则SQL可能是无效果的。
                    expFather.add(cb.<Date>between((Expression<Date>) left, result1, result2));
                }else if(yz.getLa().size() >= 2) {
                    Expression<Long> result1 = new LiteralExpression<Long>((CriteriaBuilderImpl) cb, yz.getLa().get(0));
                    Expression<Long> result2 = new LiteralExpression<Long>((CriteriaBuilderImpl) cb, yz.getLa().get(1));
                    expFather.add(cb.<Long>between((Expression<Long>) left, result1, result2));
                }else if(yz.getDa().size() >= 2) {
                    Expression<Double> result1 = new LiteralExpression<Double>((CriteriaBuilderImpl) cb, yz.getDa().get(0));
                    Expression<Double> result2 = new LiteralExpression<Double>((CriteriaBuilderImpl) cb, yz.getDa().get(1));
                    expFather.add(cb.<Double>between((Expression<Double>) left, result1, result2));
                }else if(yz.getSa().size() >= 2) {
                    Expression<String> result1 = new LiteralExpression<String>((CriteriaBuilderImpl) cb, yz.getSa().get(0));
                    Expression<String> result2 = new LiteralExpression<String>((CriteriaBuilderImpl) cb, yz.getSa().get(1));
                    expFather.add(cb.<String>between((Expression<String>) left, result1, result2));
                }else if(right!=null){
                    if(yz.getDt()!=null) {
                        Expression<Date> result2 = new LiteralExpression<Date>((CriteriaBuilderImpl) cb, yz.getDt());
                        expFather.add(cb.<Date>between((Expression<Date>) left, (Expression<Date>) right, result2));
                    }else if(yz.getLv()!=null) {
                        Expression<Long> result2 = new LiteralExpression<Long>((CriteriaBuilderImpl) cb, yz.getLv());
                        expFather.add(cb.<Long>between((Expression<Long>) left, (Expression<Long>) right, result2));
                    }else if(yz.getDv()!=null) {
                        Expression<Double> result2 = new LiteralExpression<Double>((CriteriaBuilderImpl) cb, yz.getDv());
                        expFather.add(cb.<Double>between((Expression<Double>) left, (Expression<Double>) right, result2));
                    }else if(yz.getSv()!=null) {
                        Expression<String> result2 = new LiteralExpression<String>((CriteriaBuilderImpl) cb, yz.getSv());
                        expFather.add(cb.<String>between((Expression<String>) left, (Expression<String>) right, result2));
                    }
                }
                return expFather;
            }
        }
        else if(yz.getO()== OPERATOR_Enum.IN)
        {
            //IN [最少1个]； 数据类型 优先顺序。  客户端指定 数据类型存放 标记位置。
            //日期格式支持之二： "2019-43-59T22:33:00.222Z"；
            CriteriaBuilder.In  inExpr= cb.in(left);
            if(yz.getDta()!=null)
                yz.getDta().stream().forEach(each->{
                    inExpr.value( each );
                });
            else if(yz.getLa()!=null)
                yz.getLa().stream().forEach( each->{  inExpr.value( each );
                } );
            else if(yz.getDa()!=null)
                yz.getDa().stream().forEach( each->{  inExpr.value( each );
                } );
            else if(yz.getSa()!=null)
                yz.getSa().stream().forEach( each->{  inExpr.value( each );
                } );
            else if(right!=null)
                inExpr.value( right );
            expFather.add( inExpr );
            return expFather;
        }
        else if(yz.getO()== OPERATOR_Enum.EXI)
        {
            //这是对 子语句的；
            expFather.add(cb.exists( (Subquery<?>)left ));
            return expFather;
        }
        else if(yz.getO()== OPERATOR_Enum.NEXI)
        {
            //not exists(集合属性字段) 处理？
            expFather.add(cb.not( cb.exists( (Subquery<?>)left )  ) );
            //泛型例子；　expFather.add(cb.or(  cb.not( cb.<Set<Long>>isNull(  (Subquery<Set<Long>>)left  ) ) ));
            return expFather;
        }
        else if(yz.getO()== OPERATOR_Enum.EMPTY){
            if(left!=null) {
                //isEmpty(要求是集合，而不是子语句或模型实体)， List/Set/..
                expFather.add(cb.<Set>isEmpty( (Expression<Set>)left));
                //不支持子语句的， 必须转变为 exists ( select isps2_.id  ；   要转EXI才能搞。
                //直接选集合，报错 IllegalArgumentException: unknown collection expression type ；改成选择id is ;
                return expFather;
            }
        }
        else if(yz.getO()== OPERATOR_Enum.NEMPTY){
            if(left!=null) {
                //isNotEmpty(要求是集合，而不是子语句或模型实体)， List/Set/..
                expFather.add(cb.isNotEmpty( (Expression)left));
                return expFather;
            }
        }
        else if(yz.getO()== OPERATOR_Enum.NULL){
            if(left!=null) {
                //isEmpty(要求是集合，而不是子语句或模型实体)， List/Set/..
                expFather.add(cb.isNull( left));
                //若用subQuery， NULL不支持 能输出多个记录的。
                return expFather;
            }
        }
        else if(yz.getO()== OPERATOR_Enum.NNULL){
            if(left!=null) {
                expFather.add(cb.isNotNull( left));
                return expFather;
            }
        }

        return  expFather;
    }

    //［0］主查询语句，　[1] [2]嵌套的subQuery; 但若是平行=同级别的也2个，后面接替覆盖掉前一个的,上一条的子SQL完结了。
    //限定本语句内的使用的信息：　子查询分开独立；
    public class QueryDomain {
        //各个subQuery可以不一样， 可能要安全控制， 可能遭遇见存在性测试的漏洞导致私密信息猜测问题。
        //主查询时=Root<T>  root；
        public From<?, ?> root;
        //给本查询的join准备，避免多余重复的join; 主查询和各个subQuery都各自独立的。
        public     Map<String, JoinInfomation>  joinStore = new HashMap<>();
        //别名alias实际上无法引用。  暂时给subQuery做一个。
        public Expression<?> selectExpression;     //别名的唯一一个单列。每一个子语句都只允许一个别名代表；
       // public     List<Expression<Boolean>>    expressions;
        //Case Having 都会有 Expression<Boolean> 的。 3个场合都重用WhereTree处理函数。
        //case可以独立当场嵌套处理

        public QueryDomain(Root<T> root) {
            this.root=root;
        }
        public QueryDomain() {
        }
        public void  setRoot(From<?, ?> root) {
            this.root=root;
        }
        //根据环境建立join; 具体条件等待进一步研究。 sx类似: ispMen.checks等，集合，子查询，exists, setMax,;
        // .A.B. 取前几个. 表示取前面的endIndex+1个,  索引序号=0开始;
        public Join<?, ?> prepareJoin(ShuXingComprise  sx, int endIndex)
        {
            //对于同一个查询/或subQuery ：避免重复，复用之前生成过的join
            String  sxOrg =sx.subSxString(endIndex);
            //是否已经存在join
            if(joinStore.get(sxOrg)==null) {
                Join<?, ?> join =null;
                //1，2，3，更多层级如何 join.;倒过来查找，有没有已经准备过的级别的join; A.B.C.x ;
                //.#Set?表示最后属性是集合操作，其它.#??特殊操作最后属性是基础数据类型;其它都是基础类型放在末尾的.A.B.x。
               //倒序 嵌套 准备。
                    //往前试探 ,嵌套递归
                if(endIndex>0) {
                    Join<?, ?> joinBase =prepareJoin(sx, endIndex - 1);
                    //在更上一级的基础之上做 join;
                    //JoinType.LEFT  确保级联的主表上级实体 能够显示。
                    join = joinBase.join(sx.ofs.get(endIndex), JoinType.LEFT);
                }
                else {
                    //最后一级的属性字段?  集合 或是 基本数据类型 或是 非集合关联对象;
                    //最后关联接的模型
                    join = root.join(sxOrg, JoinType.LEFT);
                }
                JoinInfomation  infomation = new JoinInfomation(join);
                //join新生成一个，成功哦，应该保存，以便后面可能 复用它的；
                joinStore.put(sxOrg, infomation);
                return  join;
            }
            else
                return  joinStore.get(sxOrg).join;
        }
        public Join<?, ?> prepareJoin(String sxDotNames)
        {
            ShuXingComprise shuXingComprise=new ShuXingComprise(sxDotNames);
            int jilianLevel =shuXingComprise.ofs.size();
            if(jilianLevel>=1)
                return prepareJoin(shuXingComprise,jilianLevel-1);
            else
                return null;
        }
    }

    //subQuery无法限制输出记录条数，所以有的运算不匹配就报错。
    //函数 数值表达式 处理。
    private <R> Expression<?> processExpressItem(ExpressItem item){
        Expression<?> expression=null;
        Expression<?> left=null;
        Expression<?> right=null;
        //数值型+ - * / % 五种运算符号;
        //y运算操作符=null 表示=运算叶子结点？ subquery;或者直接选出单个属性。
        String  op=item.getY();
        if(item.getL()!=null) {
            left = processExpressItem(item.getL());
        }else if(item.getSub()!=null){
            //子查询的结果也当成了普通字段属性的，可作为表达式。
            left =doSubquery(item.getSub());
            //Class  originalType =left.getJavaType();
        }else if(!StringUtils.isEmpty(item.getS()) ) {
            left =genMathFuncSx(item.getS());
        }
        //Case的处理 单列
        if(item.getCwt()!=null) {
            if(op!=null  &&  op.equals("sCase")) {
                //简单模式case處理。
                expression= getSimpleCase(item.getCwt(), left );
            }else {
                //太灵活的，布尔模式case處理。
               expression= getBooleanCase(item.getCwt() );
            }
            //if(!StringUtils.isEmpty(item.getAlia()) )        expression.alias(item.getAlia() );
            return expression;
        }
        //特殊！操作符=null 运算符 subquery;或者直接选出单个属性。
        if(StringUtils.isEmpty(op)){
            if(left!=null)
                return  left;    //深度优先，左边必有用。
            else if(item.getDt()!=null)
                return (Expression)(new LiteralExpression<Date>( (CriteriaBuilderImpl)cb, item.getDt() ) );
            else if(item.getLv()!=null)     //有需求
                return (Expression)(new LiteralExpression<Long>( (CriteriaBuilderImpl)cb, item.getLv() ) );
            else if(item.getDv()!=null)
                return (Expression)(new LiteralExpression<Double>( (CriteriaBuilderImpl)cb, item.getDv() ) );
            else if(item.getSv()!=null)
                return (Expression)(new LiteralExpression<String>( (CriteriaBuilderImpl)cb, item.getSv() ) );
            else if(item.getBv()!=null)
                return (Expression)(new LiteralExpression<Boolean>( (CriteriaBuilderImpl)cb, item.getBv() ) );
            return  null;
        }
        //运算有些需要右部的？
        if(item.getR()!=null)
            right=processExpressItem(item.getR());
        //数据类型 不适合 运行将报错。
        //有些函数 都可不需要left的。
        if(op.equals("+")){
            if(right!=null)
                cb.sum((Expression<Number>)left, (Expression<Number>)right);
            else if(item.getLv()!=null)
                cb.sum((Expression<Number>)left, item.getLv());
            else if(item.getDv()!=null)
                cb.sum((Expression<Number>)left, item.getDv());
        }else if(op.equals("-")){
            if(right!=null)
                cb.diff((Expression<Number>)left, (Expression<Number>)right);
            else if(item.getLv()!=null)
                cb.diff((Expression<Number>)left, item.getLv());
            else if(item.getDv()!=null)
                cb.diff((Expression<Number>)left, item.getDv());
        }else if(op.equals("*")){
            if(right!=null)
                cb.prod((Expression<Number>)left, (Expression<Number>)right);
            else if(item.getLv()!=null)
                cb.prod((Expression<Number>)left, item.getLv());
            else if(item.getDv()!=null)
                cb.prod((Expression<Number>)left, item.getDv());
        }else if(op.equals("Max")){         //数值类型的
            if(left!=null)
                expression =cb.max((Expression) left);
        }else if(op.equals("Min")){
            if(left!=null)
                expression =cb.min((Expression) left);
        }else if(op.equals("greatest")){        //非数值的类型
            if(left!=null)
                expression =cb.greatest((Expression) left);
        }else if(op.equals("least")){
            if(left!=null)
                expression =cb.least((Expression) left);
        }else if(op.equals("size")){           //对　集合属性字段的
            //Subquery无法做size      Subquery test =(Subquery)left;  test.as(HashSet.class)
            expression=cb.size( (Expression) left );
            //cb.size( (Expression<Collection>)left);
        }else if(op.equals("addDate")){
            //Date 转成Number的返回后去比较, 日期; 相差数天 ,diff转成sum;    日期无法相减！。
            if(left!=null)
                expression=cb.sum( (Expression) left , item.getDv() );
            //强制转换，运行时报错！java.sql.Date cannot be cast to javax.persistence.criteria.Expression
        }else if(op.equals("concat")){
            if(left!=null && right!=null)
                expression =cb.concat((Expression<String>)left, (Expression<String>)right);
            else if(left!=null && item.getSv()!=null)
                expression =cb.concat((Expression<String>)left, item.getSv());
            else if(right!=null && item.getSv()!=null)
                expression =cb.concat(item.getSv(), (Expression<String>)right);
        }else if(op.equals("lower")){           //字符串
            expression = cb.lower( (Expression)left );
        }else if(op.equals("Sum")){
            //聚合函数 <N extends Number> Expression<N> sum(Expression<N> x);　针对某个属性字段&&数值类型！
            if(left!=null)
                expression=cb.sum((Expression) left);
        }else if(op.equals("count")){
            if(left!=null)
                expression=cb.countDistinct(left);
            else
                expression=cb.countDistinct(genMathFuncSx( "id") );
        }
        else if(op.equals("trimB") || op.equals("trimT") || op.equals("trimH"))
        {
            CriteriaBuilder.Trimspec trimspec= CriteriaBuilder.Trimspec.BOTH;
            if(op.equals("trimH"))
                trimspec= CriteriaBuilder.Trimspec.LEADING;
            else if(op.equals("trimT"))
                trimspec= CriteriaBuilder.Trimspec.TRAILING;
            char blankchar=' ';
            //置换空格 符号
            if(!StringUtils.isEmpty(item.getSv()))
                blankchar =item.getSv().charAt(0);
            expression =cb.trim(trimspec, blankchar, (Expression)left );
        }else if(op.equals("Avg")){
            if(left!=null)
                expression=cb.avg((Expression) left);
        }
        else if(op.equals("subOut"))
        {
            //当前子查询的输出单个列(/多条的)。
            //expression =cb.literal(item.getSv());     文本只能 当成了 参数，无法做成属性字段/别名。　　其他办法？
            int  level=querys.size();
            expression= querys.get(level-1).selectExpression;
            return  expression;
              //return (Expression)(new LiteralExpression<>( (CriteriaBuilderImpl)cb, item.getSv() ) );
        }else if(op.equals("Coalesce")){
            //若coalesce不同类型数据，sql可能报错。
            CriteriaBuilder.Coalesce  coalesce= cb.coalesce();
            item.getA().stream().map(each->coalesce.value(processExpressItem(each)) )
                    .collect(Collectors.toList());
            expression= coalesce;
        }else if(op.equals("abs")){
            expression=cb.abs( (Expression<Integer>)left);
        }else if(op.equals("neg")){
            expression=cb.neg( (Expression<Integer>)left);
        }else if(op.equals("currentDate")){
            expression =cb.currentDate();       //无需参数的
        }else if(op.equals("all")){
            //骗过编译器，动态执行层次去保证 参数 类型符合性。
            if(left!=null  && item.getSub()!=null)
                expression =cb.all((Subquery) left);
        }else if(op.equals("some")){
            //any = some等价的
            if(left!=null  && item.getSub()!=null)
                expression =cb.some((Subquery) left);
        }else if(op.equals("substr")){
            if(left!=null) {
                Expression<Integer> start = null;
                //允许r下级对象
                if (right != null)
                    start = (Expression<Integer>) right;
                else if (item.getLv() != null)
                    start = (Expression) (new LiteralExpression<>((CriteriaBuilderImpl) cb, item.getLv()));
                Expression<Integer> length = (Expression) (new LiteralExpression<>((CriteriaBuilderImpl) cb, item.getLen()));
                if(item.getLen() > 0)
                    expression = cb.substring((Expression) left, start, length);
                else
                    expression = cb.substring((Expression) left, start);
            }
        }else if(op.equals("locate")){
            if(left!=null) {
                Expression<String> pattern=null;
                if(item.getSv()!=null)
                    pattern =(Expression)(new LiteralExpression<>( (CriteriaBuilderImpl)cb, item.getSv() ) );
                else
                    pattern= (Expression<String>)right;
                Expression<Integer> position=null;
                if(item.getLv()!=null)
                    position =(Expression)(new LiteralExpression<>( (CriteriaBuilderImpl)cb, item.getLv() ) );
                else if(item.getSv()!=null)       //搜索优先，起点位置殿后
                    position = (Expression<Integer>)right;
                if(position!=null)
                    expression = cb.locate((Expression) left, pattern, position);
                else
                    expression = cb.locate((Expression) left, pattern );
            }
        }else if(op.equals("strLen")){
            expression = cb.length( (Expression)left );
        }else if(op.equals("/")){
            if(right!=null)
                cb.quot((Expression<Number>)left, (Expression<Number>)right);
            else if(item.getLv()!=null)
                cb.quot((Expression<Number>)left, item.getLv());
            else if(item.getDv()!=null)
                cb.quot((Expression<Number>)left, item.getDv());
        }else if(op.equals("%")){
            if(right!=null)
                cb.mod((Expression<Integer>)left, (Expression<Integer>)right);
            else if(item.getLv()!=null)
                cb.mod((Expression<Integer>)left, item.getLv().intValue());
        }
        else if(op.equals("upper")){
            expression = cb.upper( (Expression)left );
        }else if(op.equals("literal")){
            //如何用？ 明文 字面上 <T> Expression<T> literal(T value);
            expression = cb.literal( left );
        }
        //root？subquery？from？别名 literal if(!StringUtils.isEmpty(item.getAlia()) )        expression.alias(item.getAlia() );
        return expression;
    }

    //运算叶子最终的语义结点--属性字段
    private Expression<?> genMathFuncSx(String shuxing){
        //level表示当前的subQuery语句的嵌套深度。
        int  level=querys.size();            //querys当前会话的全局分析储备数据区。
        //分析器帮助类。
        ShuXingComprise  sx=new ShuXingComprise(shuxing);
        //A.B.c ;级联深度jilianLevel=整个属性名字串的关联对象深度；
        int jilianLevel=sx.ofs.size();        //A.B.c ; A.B.Set
        if(jilianLevel>1  &&  !StringUtils.isEmpty( sx.ofs.get(jilianLevel-1) ) )
        {
            //这里将会递归获得　早先准备的或者新建立的 join; 关联必须join;  本体模型简单类型字段就没有了。
            Join<?, ?> join1 =querys.get(level-1-sx.qt).prepareJoin(sx,jilianLevel-2);
            //在上级join基础上 获取下级属性字段的 ?  singular / PluralAttributePath ；
            return  join1.get( sx.ofs.get(jilianLevel-1) );
        }
        else
        {      //只有唯一的　一层的简单属性字段，可能是对象集合属性　或简单数据类型的；
            return  querys.get(level-1-sx.qt).root.get(sx.ofs.get(0) );
        }
    }

    //满足安全策略下可使用的，在JPA子语句能够支持的SQL范围内。不支持Union;
    //Subquery<T> extends  Expression<T>; 只支持子查询的结果集合只有一个列，单字段单列的。关联上级的，非关联的也支持；
    private Expression<?> doSubquery(SubSelect sub){
        Subquery<?> subquery=null;      //T是主语句的模型类，子语句就不同了，？表示某个类型。
        QueryDomain  queryDomain=new QueryDomain();
        querys.add(queryDomain);
        //若是有关联的subquery; 　某个字段做关联的。   Isp.字段.#1
        //关联子查询没有用from()的，使用correlate()，需要从关联字段的基础上再去向着目标from模型类join;
        //关联子查询query.subquery()的模型类型是关联字段的模型类，注意它不是目标的from模型类；　关联必须从底下连接处找起的，相对定位；子语句属性名如何分别？
        //关联子查询不需要人工指出模型类{JPA自动晓得}, 关联的也可转化成非关联的不用correlate()模式做。
        //用关联模式写，不需要添加where的实体等价条件，但是属性名称必须按照关联点的模型类为参照物来定义，属性名修饰前缀要多点。抛弃correlate()用法;
        //统一都采用from()非关联模式写法，手动添加的关联where条件 X.chemen=Y.ispMen.#1 ; ispMen.#1 代表上1级的SQL语句的属性;
        Metamodel metamodel=(Metamodel)entityManager.getMetamodel();
        EntityType<?> entityType= metamodel.entity(metamodel.getImportedClassName(sub.getFrom()));
        Class  subClass=entityType.getJavaType();
        subquery = query.subquery(subClass);
        //非关联添加的
        Root<?> subRoot = subquery.from(subClass);
        From<?, ?> subEntity=subRoot;
        queryDomain.setRoot(subEntity);
        //       Join<?, ?>  subRoot = subquery.correlate(join1);        //关联必须从底下连接处找起的，相对定位；子语句属性名如何分别？
        //关联子查询没有用from()的，使用correlate()，需要从关联字段的基础上再去向着目标from模型类join;
        //关联子查询query.subquery()的模型类型是关联字段的模型类，注意它不是目标的from模型类；
        //      Join<?, ?>  subEntity = subRoot.join("checks"); //底下端那个字段
        //       Bindable<?> bindable= subEntity.getModel();     //这个是SET类型name="checks",这里declaringType=User;
        //Isp==>""ispMen"其中一个人" user2_.id="USER."checks 他做的所有检验"""Isp"""   "
        Expression<?> expressionSelect=null;
        if(sub.getE()!=null)
            expressionSelect=processExpressItem(sub.getE());
        else if(sub.getS()!=null) {
            expressionSelect =genMathFuncSx(sub.getS());
        }else{
            expressionSelect =subEntity;      //.get("id");
        }

        subquery.select((Expression) expressionSelect);
        //给alias随后去引用来的。
        queryDomain.selectExpression= expressionSelect;
        Predicate p = cb.conjunction();
        List<Expression<Boolean>>    expressions= p.getExpressions();
        //非关联添加的        ispMen.#1 代表上１级的SQL内的属性;
        //子语句自己的条件逻辑过滤。
        expressions = processDigui(sub.getWhere(), expressions);

        subquery.where(p);
        if(sub.getGrp()!=null) {
            List<Expression<?>> groupList =sub.getGrp().stream()
                    .map(item ->processExpressItem(item) )
                    .collect( Collectors.toList() );
            subquery.groupBy(groupList);
        }
        if(sub.getHav()!=null) {
         //Having 也是独立的一组Bool表达式，针对聚合输出项的过滤，和where无关但是类似。
            Predicate predicate = cb.conjunction();
            List<Expression<Boolean>>  expressionsHaving = predicate.getExpressions();
            expressionsHaving = processDigui(sub.getHav(), expressionsHaving);
            subquery.having(predicate );
        }
       // querys.get(0).expressions.add(cb.or(  cb.not( cb.exists(subquery) ) ));
      //本子语句已经处理完成了，该清除querys.add(queryDomain);
        int  level=querys.size();       //目前处于那一个层级的；主语句算１级的。
        querys.remove(level-1);
        return  subquery;
    }

    private Expression<?> getBooleanCase(List<CaseExpression>  list)
    {
        CriteriaBuilder.Case   caseExpression= cb.selectCase();
        for(CaseExpression cw : list) {
            if(cw.getB()!=null) {
                //  Expression<Boolean> condition = getBooleanCase();
                Predicate predicate = cb.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                expressions = processDigui(cw.getB(), expressions);

                Expression<?> result = processExpressItem(cw.getT());
                //Expression<? extends R>  result= (Expression)(new LiteralExpression<>( (CriteriaBuilderImpl)cb, cw.getT().getSv() ) );
                caseExpression.when(predicate, result);
            }else{
                Expression<?> result = processExpressItem(cw.getT());
                return   caseExpression.otherwise(result);
            }
        }
        return  caseExpression.otherwise(null);
    }
    //突破<C,　R>的错误枷锁。没必要在编译时刻检查太严格了。
    //针对不同的数据类型。
    @SuppressWarnings({ "unchecked" })
    private  <C, R> Expression<?> getSimpleCase(List<CaseExpression> list, Expression<?> obj)
    {
        Class<?>  caseClass =obj.getJavaType();
            //private  <C, R>   Expression<?>  getSimpleCase(List<CaseExpression> list, Class<C> caseClass)
        CriteriaBuilder.SimpleCase   simpleCase=null;
            //  CriteriaBuilder.SimpleCase<String,R>   stringCase=null;
        simpleCase =cb.<C,R>selectCase( (Expression)obj );
        //从最上层注入泛型类型，需要明确泛型C, 而R底层已经在api多重载和支持,否则编译错误。
        for(CaseExpression cw : list) {
            if(cw.getW()!=null) {
                //Expression<? extends R>  result= (Expression)(new LiteralExpression<>( (CriteriaBuilderImpl)cb, cw.getT().getSv() ) );
                Expression<?> result = processExpressItem(cw.getT());
                //case <XX> When () : 简单的EQ等价判定 逻辑， 数据对比是简单的数据类型而且只有单列的。
                if(caseClass==String.class)
                    simpleCase.<C,R>when( cw.getW().getSv(),  result );
                else if(caseClass==Double.class)
                    simpleCase.<C,R>when( cw.getW().getDv(),  result );
                else if(caseClass==Long.class)
                    simpleCase.<C,R>when( cw.getW().getLv(),  result );
                else if(caseClass==Date.class)
                    simpleCase.<C,R>when( cw.getW().getDt(),  result );
                else if(caseClass==Boolean.class)
                    simpleCase.<C,R>when( cw.getW().getBv(),  result );
                else
                    simpleCase.<C,R>when( cw.getW().getSv(),  result );
            }else{
                Expression<?> result = processExpressItem(cw.getT());
                simpleCase.otherwise(  result );
            }
        }
        return simpleCase;
    }

}




//Java同名函数+参数都相同，光光返回值类型不相同是不允许的。 需转去搞 <T> 泛型类型

