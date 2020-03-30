# unibackend
行业后端
org/fjsei/yewu/config/MyGraphQLWebAutoConfiguration.java GraphqlFieldVisibility 
org/fjsei/yewu/config/GraphqlConfiguration.java

2020-03-30 10:35:53.157  INFO 7320 --- [restartedMain] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'seiPersistenceUnit'
2020-03-30 10:35:54.982  INFO 7320 --- [restartedMain] o.f.yewu.graphql.MyWebAutoConfiguration  : 瀹氫綅class graphql.kickstart.tools.boot.ClasspathResourceSchemaStringProvider鐨剆chema鏁存枃鏈�:type Author {
    id: ID!
    firstName: String!
    lastName: String!
}

#schema鐪佺暐,Query type is presumed to be the query root operation type of the schema鍍忎竴鑸璞′娇鐢�
extend type Query {
    findAllAuthors: [Author]!
    countAuthors: Long!
}

extend type Mutation {
    newAuthor(firstName: String!, lastName: String!) : Author!
}
type Book {
    id: ID!
    title: String!
    isbn: String!
    pageCount: Int
    author: Author
}

#鍏朵粬閮芥槸鎵╁睍鐨�
extend type Query {
    findAllBooks: [Book]!
    countBooks: Long!
}

extend type Mutation {
    newBook(title: String!, isbn: String!, pageCount: Int, author: ID!) : Book!
    deleteBook(id: ID!) : Boolean
    updateBookPageCount(pageCount: Int!, id: ID!) : Book!
}
directive @authr(
    qx: [String] = ["USER"]
) on FIELD_DEFINITION

enum LJ_TYPE { AND, OR, NOT, DSELF }
enum OPERATOR { EQ,NE,GE,LE,GT,LT,BTW,IN,LK,
        EMPTY,NULL,EXI,NEXI,TRUE,FALSE,NNULL,NEMPTY  }

"涓嶆敮鎸佷腑鏂囩殑鍚嶅瓧"
enum AuthorityName {  ROLE_SOMEONE,  ROLE_USER,  ROLE_ADMIN,   ROLE_Manager }


#杈撳叆澶嶅悎瀵硅薄蹇呴』POJO+Query鐨刯ava浠ｇ爜鏈夊湴鏂圭敤鍒颁簡鎵嶇畻
input DeviceCommonInput {cod: String, address: String, oid: String, lat:String
    }

"杩欓噷鐨勭被鍨嬭嚜鎴愪綋绯诲拰java涓嶅悓鐨勩€�"
input WhereLeaf { s:String, o:OPERATOR, l:ExpressItem, r:ExpressItem,
    sv:String, sa:[String], dt:String, dv:BigDecimal, lv:Long,
    dta:[String], da:[BigDecimal], la:[Long]
    }
input WhereTree { lj:LJ_TYPE,  a:WhereLeaf, as:[WhereLeaf],
    w:WhereTree, ws:[WhereTree], sup:WhereTree
 }
input ExpressItem {
    y:String, l:ExpressItem, r:ExpressItem, s:String,
    sub:SubSelect, cwt:[CaseExpression], a:[ExpressItem],
    sv:String, lv:Long, dv:BigDecimal, dt:String,
    bv:Boolean, len:Int
}
input SubSelect {
     s:String, e:ExpressItem,  from:String,
     where: WhereTree,  grp:[ExpressItem], hav:WhereTree
}
input CaseExpression {
   w:ExpressItem,  b: WhereTree,   t:ExpressItem
}

#澶嶅悎浼楀鍙傛暟Input types may be lists of another input type, or a non鈥恘ull variant of any other input type鍍廕S瀵硅薄鏍�
input ComplexInput { username: String, mobile: String, dev:DeviceCommonInput }

type Address{
    id:ID!
    name: String!
    lng: String
    lat: String
    eqps: [EQP]
    ad:  Adminunit
}

type Adminunit{
    id:ID!
    prefix: String!
    areacode: String!
    zipcode: String
    adrs: [Address]
}

extend type Query {
    findAllPositions: [Address]!
    countPositionEQP(id: ID ):Long
}

extend type Mutation {
    newPosition(name: String!, aduId: ID!, lat: String, lng: String) : Address!
    initAdminunit(townId: ID!, prefix: String!, areacode: String!, zipcode: String) : Adminunit

}

type JcAuthor{
    id:ID!
    nickname: String!
    books: [JcBook]
}


extend type Query {
    findAllJcAuthors: [JcAuthor]!
}

extend type Mutation {
    newJcAuthor(nickname: String!) : JcAuthor!
}
type JcBook{
    id: ID!
    title: String!
    isbn: String!
    author: JcAuthor
}


extend type Query {
    findAllJcBooks: [JcBook]!
    countJcBook(author: ID ):Long
}

extend type Mutation {
    newJcBook(title: String!, isbn: String!, author: ID!) : JcBook!
}
directive @uppercase on FIELD_DEFINITION
directive @range(
    min: Float!,
    max: Float!
) on ARGUMENT_DEFINITION

type Query {
    ##hello(value1: String!): String @uppercase
    hello: String
    limitedValue(value: Float! @range(min: 0.00, max: 1.00)): Float
}

#涓嶈兘閲嶅瀹氫箟type鐨�;鍏跺畠Query鍙兘鐢╡xtend type Query锛沞xtend type Mutation涔嬬被鐨勩€�
type Mutation {

}

type Subscription {
  hello: Int
}
type Authority{
    id:ID!
    "鏋氫妇GraphQLEnumType鏄緭鍏ヨ緭鍑洪兘鑳藉仛"
    name: String!
    users: [User]
}

extend type Query {
    findAllAuthority: [Authority]!
}

#缁欏墠绔湅鐨勬ā鍨嬮』鑳藉湪鍚屽悕POJO瀹炰綋绫讳腑鍖归厤getxxx()鏂规硶,娉ㄦ剰涓枃绌烘牸鍜岃嫳鏂囦笉鍚屻€�
type EQP{
    id:ID!
    cod: String!
    oid: String
    type: String!
    ownerUnt: Unit
    pos: Address
    maintUnt: Unit
    instDate: String
    factoryNo: String
    task: [Task]
    isps: [ISP]
    #澶栨ā鍨嬬湅鍒扮殑瀛楁鍜孭OJO鍙笉鍚�
    meDoIsp: [ISP]
    valid: Boolean
}



extend type Query {
    getDevice(id:ID!): EQP
    getDeviceSelf(id:ID!): EQP
    findEQPbyCod(cod: String!): EQP
    #涓嶈兘鐢╰ype瀹氫箟鐨勫璞＄被鍨嬫潵鍋氳緭鍏ュ弬鏁帮紝鎶ラ敊to be a GraphQLInputType#
    findEQPLike(filter: DeviceCommonInput): [EQP]!
    findAllEQPsFilterInput(filter: DeviceCommonInput!, offset:Int!, first:Int=20, orderBy:String, asc:Boolean=true): [EQP]!
    getAllEQP: [EQP]!
    countAllEQP: Long!
    countAllEQPsFilter(filter: DeviceCommonInput!): Long!
    countAllEQPsWhere(where: WhereTree): Long!
    findAllEQPsFilter(where: WhereTree, offset:Int, first:Int, orderBy:String, asc:Boolean): [EQP]!
}

extend type Mutation {
    newEQP(cod: String!, type: String!, oid: String) : EQP!
    "鍥犱负type鏅€氬璞＄洿鎺ョ敤浜庤緭鍏ュ弬鏁扮殑璇�,璇箟姣旇緝妯＄硦,鎵€浠raphQL瑕佹眰鍙﹀鍗曠嫭寤�"
    setEQPPosUnit(id:ID!, pos: ID!, owner: ID!, maint: ID) : EQP!
    #妯″瀷鎺ュ彛鍜宩ava鎵ц鍑芥暟鐨勫弬鏁伴『搴忓拰绫诲瀷鏄叧閿紝鍙傛暟鍚嶅瓧鍗村彲涓嶄竴鑷淬€傛帴鍙ｅ嚱鏁板悕瑕佸敮涓€銆�
    buildEQP(id:ID!, owner: ID!, info: DeviceCommonInput) : EQP
    testEQPModify(id:ID!, oid: String ): EQP
    testEQPFindModify(cod:String!, oid: String ): String
    testEQPStreamModify(cod:String!, oid: String ): String
    testEQPCriteriaModify(cod:String!, oid: String, type: String): EQP
    testEQPcreateQueryModify(cod:String!, oid: String ): String
    invalidateEQP(whichEqp:ID!, reason: String!) : Boolean!
}

type File{
    id:ID!
    url: String!
    creator: User
    anyoneCanSee: Boolean!
}
type Report implements SimpleReport{
    id: ID!
    type: String!
    no: String!
    upLoadDate: String
    path: String
    sign: String
    detail: String
    isp(id: ID ): ISP
    numTest: Float
    modeltype: String!
    modelversion: String!
    data: String
    snapshot: String
    files: [File]
}
#瀹夊叏闈炲畨鍏ㄧ被鍨嬬殑浣跨敤鍘熷垯锛屽敖閲忔妸淇濆瘑瀛楁鎵撳寘鎶界锛岄潪瀹夊叏绫诲瀷鐢ㄤ簬鏅€氬姛鑳芥瘮濡傚叧鑱斿璞�
interface SimpleReport {
    id:ID!
    type: String!
    #鍚庨潰鍔犳潈闄愯璇丂authr(qx : ["USER","ADMIN"])娌¤繖鏉冮檺涔嬩竴鏃犳硶璁块棶銆�
    no: String! @authr(qx : ["USER","ADMIN"])
    upLoadDate: String
    isp(id: ID ): ISP
    path: String
}

#鍑忓皯浣跨敤闈炲畨鍏ㄧ被鍨婻eport鐨勫嚱鏁版帴鍙ｏ紝瀹夊叏绾у埆妫€鏌�
extend type Query {
    findAllReports: [Report]!
    findAllBaseReports: [SimpleReport]!
    countReport(isp: ID ):Long
    getReport(id:ID!): Report
}

extend type Mutation {
    #鏈熬鍔犳潈闄� @authr(qx:["REPCHECKER","ADMIN"])锛屾病鏉冨氨鐢ㄤ笉浜嗘帴鍙ｅ嚱鏁般€�
    newReport(isp: ID!,modeltype: String!, modelversion:String) :Report! @authr(qx:["USER","ADMIN"])
    buildReport(isp: ID!,no: String!,path: String!) : Report!
    modifyOriginalRecordData(id: ID!,operationType:Int!,data: String,deduction: String) : Report
    modifyOriginalRecordFiles(id: ID!, fileIDs: [ID]!) : Report
    deleteReport(repId: ID!, reason: String): Boolean!
}


type Unit{
    id:ID!
    name: String!
    address: String
    linkMen: String
    phone: String
    owns: [EQP]
    maints: [EQP]
}


extend type Query {
    findAllUnits: [Unit]!
}

extend type Mutation {
    newUnit(name: String!, address: String) : Unit!
}

type User implements Person {
    id:ID!
    username: String!
    "瀛楁鍍忓嚱鏁�,涓€鑸敤浣滃唴宓屽璞＄害鏉熷弬鏁帮細top,first,ID?"
    dep(dep: String): String
    mobile: String
    checks: [ISP]
    isp: [ISP]
    #鏉冮檺琛ㄩ潪瓒呯骇鐢ㄦ埛鍙兘鐪嬭嚜宸辩殑浜�
    authorities: [Authority]
    "妯″瀷瀛楁瀹為檯鍚屽嚱鏁颁竴鏍风殑澶勭悊,灞炴€х珶鐒朵篃鍙互鏈夎緭鍏ュ弬鏁�"
    enabled(isUsing: Boolean): Boolean!
    photoURL: String
}

interface Person {
    id:ID!
    username: String!
    mobile: String
}

type Auth {
    id:ID!
    username: String!
    dep: String
    authorities: [Authority]
}


extend type Query {
    findAllUsers: [Person]!
    #鑾峰彇褰撳墠鐢ㄦ埛鐨勬巿鏉冭鑹�
    auth: String!
    checkAuth: User
    findUserLike(username: String): [User]!
    findUserLikeInterface(complex: ComplexInput): [User]!
    userBasic(id: ID!): Person
    findAllUserFilter(where: WhereTree, offset:Int, first:Int, orderBy:String, asc:Boolean): [User]!
}

extend type Mutation {
    newUser(username: String!,password: String!,mobile: String!,external:String,eName:String,ePassword:String) : Boolean!
    authenticate(username: String!, password: String!) : Boolean!
    logout : Boolean!
}

type ISP{
    id: ID!
    dev: EQP!
    task: Task
    ispMen: [User]
    checkMen: User
    nextIspDate: String
    conclusion: String
    #瀹夊叏鑰冭檻鐢⊿impleReport浠ｆ浛Report灏辨棤娉曞唴鐪佹祦鍑轰繚瀵嗗瓧娈�
    reps: [SimpleReport]
}

extend type Query {
    findAllISPs: [ISP]!
    countISP(ispMen: ID ):Long
    getISP(id:ID!): ISP
    "type涓嶅彲浠ュ仛杈撳叆鍙傛暟锛屾姤閿橢xpected a GraphQLInputType,it wasn't! object type incorrectly used as input type"
    getReportOfISP(id:ID!): [SimpleReport]!
    isp(filter: ID!,offset: Int,first: Int=3,orderBy: String): [ISP!]
    findAllISPfilter(where: WhereTree, offset:Int!, first:Int=20, orderBy:String, asc:Boolean=true): [ISP]!
    getISPofDevTask(dev:ID!,task:ID!): ISP
}

extend type Mutation {
    newISP( dev: ID!) : ISP!
    setISPispMen(id:ID!, users : [ID] ) : ISP!
    setISPtask(id:ID!, task:ID! ) : ISP!
    setISPreport(id:ID!, reps:[ID]! ) : ISP!
    buildISP(dev: ID!,task:ID!, username:String!) : ISP!
    abandonISP(isp: ID!, reason: String): Boolean!
}

type Task{
    id: ID!
    devs: [EQP]!
    dep: String
    date: String
    status: String
    #鑳界洿鎺ヨ繖涔堝睆钄絝ee: String
    isps: [ISP]
}

extend type Query {
    findAllTasks: [Task]!
    countTask(dep: String, status: String):Long
    findAllTaskFilter(where: WhereTree, offset:Int, first:Int, orderBy:String, asc:Boolean): [Task]!
}

extend type Mutation {
    newTask( devs: ID!) : Task!
    addDeviceToTask(task: ID!, dev: ID!) : Task!
    dispatchTaskTo(id: ID!,status: String!,dep: String!, date: String!) : Task!
    buildTask(devs: ID!, dep: String!, date: String!) : Task!
    cancellationTask(task: ID!, reason: String): Boolean!
}

type Recipe{
    id: ID!
    title: String!
    plain: String
    author: String
    createdBy: User
    updatedAt: String
    image: String
    ingredients: String
    description: String
}

type Following{
    toUser: User
    fromUser: User
    confirmed: Boolean
}


extend type Query {
    findRecipe(id:ID!):Recipe
    useFollowing(toUser: Boolean): [Following]
    findAllRecipeFilter(where: WhereTree, offset:Int!, first:Int=20, orderBy:String, asc:Boolean=true): [Recipe]!
}

extend type Mutation {
    newRecipe(title: String!, author:String, plain:String, image:String, ingredients:String, description:String) : Recipe!
    #鍙戣捣鍏虫敞
    requestFollow(fromUser: ID!, toUser: ID!): Boolean!
    confirmFollow(userId: ID!): Boolean!
    delRequestFollow(toUser: ID!): Boolean!
    dismissFollowOf(fromUser: ID!): Boolean!
}

type EqpMge{
    id: ID!
    eQPCOD: String!
}
extend type Query {
    #鏃у钩鍙板疄浣撹〃搴斿綋鐩存帴铻嶅悎鎺夛紝杩欓噷鍙彁渚涚储寮曞畾浣嶅姛鑳姐€�
    findEqpMge(id:ID!):EqpMge
}
extend type Mutation {
}


