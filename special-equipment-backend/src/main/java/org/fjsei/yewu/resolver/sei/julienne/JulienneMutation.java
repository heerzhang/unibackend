package org.fjsei.yewu.resolver.sei.julienne;

import graphql.kickstart.tools.GraphQLMutationResolver;
import md.system.AuthorityRepository;
import md.system.User;
import md.system.UserRepository;
import md.cm.unit.UnitRepository;
import md.specialEqp.*;
import md.specialEqp.inspect.ISPRepository;
import md.specialEqp.inspect.TaskRepository;
import md.julienne.Following;
import md.julienne.Recipe;
import md.julienne.RecipeRepository;
import org.fjsei.yewu.security.JwtTokenUtil;
import org.fjsei.yewu.security.JwtUser;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;


//实际相当于controller;
//这个类名字整个软件之内范围 不能重复 简名字！ baseMutation 是组件注入的名字。
//Mutation/Query本组内函数名字不能同名， 底层上做了HashMap找接口函数。
//GraphQL有非常重要的一个特点：强类型,自带graphQL基本类型标量Int, Float, String, Boolean和ID。　https://segmentfault.com/a/1190000011263214
//乐观锁确保任何更新或删除不会被无意地覆盖或丢失。悲观锁会在数据库级别上锁实体会引起DB级死锁。 https://blog.csdn.net/neweastsun/article/details/82318734

@Component
public class JulienneMutation implements GraphQLMutationResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private EQPRepository eQPRepository;
    @Autowired
    private ISPRepository iSPRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private AuthorityRepository authorityRepository;


    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;                //EntityManager相当于hibernate.Session：

    @Autowired
    private final JwtTokenUtil jwtTokenUtil=new JwtTokenUtil();

    private User checkAuth() {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        if(auth==null)  return null;
        Object principal=auth.getPrincipal();
        if(principal instanceof JwtUser){
            Long  userid=((JwtUser) principal).getId();
            //   UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(name);
            //Long userId=Long.valueOf(12);
            return userRepository.findById(userid).orElse(null);
        }else{
            //没有登录的也Authenticated! 有anonymousUser 有ROLE_ANONYMOUS；
            return null;
        }
    }

    @Transactional
    public Recipe newRecipe(String title, String author, String plain, String image, String ingredients, String description) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        User user= checkAuth();
        if(user==null)   return null;
        Recipe recipe = new Recipe(title, author, plain, image, ingredients, description);
        recipe.setCreatedBy(user);

        recipeRepository.save(recipe);
        return recipe;
    }
    //发起关注
    @Transactional
    public boolean requestFollow(Long fromUser,Long toUser) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        User user= checkAuth();
        if(user==null)   return false;
        if(user.getId()!=fromUser)  return false;
        User userTo= userRepository.findById(toUser).orElse(null);
        if(userTo==null)   return false;
        //多对多分解中间表添加字段模式：单方面维护关系，但是要new BookPublisher()中间表;
        Following following=new Following();
        following.setFromUser(user);
        following.setToUser(userTo);
        following.setConfirmed(false);
        user.getIFollowing().add(following);    //单方面维护关系
         //    userTo.getBeFollowed().add(following);   //?
        //违反唯一性报错 A different object with the same identifier value was already associated with the session
        emSei.persist(following);     //中间表必须单独先保存
           //     userRepository.save(user);
        return true;
    }
    @Transactional
    public boolean confirmFollow(Long userId) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        User user= checkAuth();
        if(user==null)   return false;
        User userTo= userRepository.findById(userId).orElse(null);
        if(userTo==null)   return false;
        user.getBeFollowed().stream().filter(
                following -> following.getFromUser().getId()==userId
        ).forEach(following -> {
            following.setConfirmed(true);
        });
                //.collect(Collectors.toList());
        emSei.flush();
        return true;
    }
    @Transactional
    public boolean delRequestFollow(Long userId) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        User user= checkAuth();
        if(user==null)   return false;
        User userTo= userRepository.findById(userId).orElse(null);
        if(userTo==null)   return false;

        Set<Following>   followingRemoved=new HashSet<Following>();
        user.getIFollowing().stream().filter(
                following -> following.getToUser().getId()==userId
        ).forEach(following -> {
            followingRemoved.add(following);
            emSei.remove(following);
        });

        emSei.flush();
        return  !followingRemoved.isEmpty();
    }
    @Transactional
    public boolean dismissFollowOf(Long userId) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        User user= checkAuth();
        if(user==null)   return false;
        User userTo= userRepository.findById(userId).orElse(null);
        if(userTo==null)   return false;

        Set<Following>   followingRemoved=new HashSet<Following>();
        user.getBeFollowed().stream().filter(
                following -> following.getFromUser().getId()==userId
        ).forEach(following -> {
            followingRemoved.add(following);
            emSei.remove(following);
        });

        emSei.flush();
        return  !followingRemoved.isEmpty();
    }
}



/*
加了cache缓存后，为了在事务中读取数据库最新数据：emSei.find(EQP.class,id)或eQPRepository.findById(id)或eQPRepository.getOne(id)或findAll()；
                或eQPRepository.findAll(new Specification<EQP>() {@Override },pageable);
必须加  emSei.setProperty(JPA_SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
        emSei.setProperty(JPA_SHARED_CACHE_STORE_MODE, CacheStoreMode.REFRESH); 加了这2条才能从DB去取最新数据。
而这些方法无需添加也能去数据库取最新数据：eQPRepository.findByCod(cod)或emSei.createQuery("")
*/

