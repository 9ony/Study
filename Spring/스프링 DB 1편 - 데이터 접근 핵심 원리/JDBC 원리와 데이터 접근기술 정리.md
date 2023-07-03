## JDBC란?

### JDBC의 등장 이유

애플리케이션에서 중요한 데이터는 대부분 데이터베이스에 보관된다.  
그리고 애플리케이션은 보관된 데이터를 조회하거나 쓰기, 삭제 등등의 과정을 거치면서 사용자에 요청에 따라 응답 결과를 제공하게 된다.  

![image](https://github.com/9ony/9ony/assets/97019540/4b9229ec-661a-4505-ad68-3663e73d4f98)

위 사진 처럼 WAS와 DB는 아래와 같은 과정을 거치면서 사용자에게 결과를 응답하게 된다.
1. 커넥션 : 주로 TCP/IP를 사용해서 연결  
2. SQL 전달 : WAS는 DB가 이해할 수 있는 SQL을 연결된 커넥션을 통해 DB에 전달
3. 응답 : DB는 전달된 SQL을 수행 후 결과를 응답 후 WAS는 해당 응답결과를 활용하여 요청한 클라이언트에게 최종적으로 응답

여기서 문제점은 rdbms의 종류로는 너무나 많은 데이터베이스가 존재한다.  
그리고 DB의 종류별로 커넥션하는 방법이 각각 다르다  
이를 위해 자바표준 인터페이스인 데이터베이스 커넥션을 위한 API인 `JDBC가 등장`하였다.  

### JDBC 표준 인터페이스  

JDBC를 사용하면 기본적으로 아래와 같은 구조가 된다.  

![image](https://github.com/9ony/9ony/assets/97019540/6d2eea64-6c67-4809-8f16-2f1e14f05e16)


위 그림 처럼 애플리케이션 로직은 이제 JDBC 표준 인터페이스에만 의존한다.  
JDBC 인터페이스를 통해 해당 데이터베이스 드라이버를 등록 후 JDBC 표준 인터페이스를 통해 DB에 관련된 처리를 해주고, DB 변경 시에도 코드를 그대로 유지하면서 드라이버만 변경해주면 된다.  
 > JDBC 드라이버란 해당 DB 회사에서 제공하는 라이브러리로 커넥션 관리,SQL 전달,결과 처리 등이 구현되어 있음
그러면 개발자는 JDBC 표준 인터페이스 사용법만 익혀두면 JDBC에서 지원하는 DB일 경우에는 해당 DB를 각각 학습할 필요 없이 커넥션 연결,SQL 전달,결과 처리 방법등을 새로 학습할 필요가 없어진다.  

### JDBC 표준화의 한계  

하지만 JDBC 표준화를 통해 커넥션,SQL 전달,응답 결과 처리 등은 해결을 하였지만,  
데이터베이스 별로 데이터타입과 문법 등은 다르다. 이를 위해 ANSI SQL이라는 표준이 있지만 일반적인 부분만 공통화 했기 때문에 한계가 있다.  
즉, 커넥션관리,SQL 전달, 결과 처리 등등은 해결되었지만 각각 요청 시 SQL문은 해당 데이터베이스에 맞게 변경시켜주어야 하는데, 이러한 부분은 대표적으로 `JPA`같은 ORM기술로 해결이 가능하다.  
그리고 JDBC의 반복 코드(커넥션,sql 전달및 결과처리 등)가 존재하게되는데 이를 SQL Mapper기술을 통해 제거해준다.  

## 데이터 접근 기술  
JDBC는 첫 출시가 1997년이 된 오래된 기술이고, 사용법도 매우 복잡하다.  
최근에는 이러한 JDBC를 또 편리하게 사용하도록 다양한 기술들이 존재한다.  
ex) MyBatis,JdbcTemplate,JAP,QueryDSL,EclipseLink 등..
위의 기술들을 `Persistence Framework`에 속하며 대표적으로 SQL Mapper와 ORM 기술로 나눌 수 있다.  

### SQL Mapper
SQL Mapper는 SQL 응답 결과를 개체로 변환해준다.  
클라이언트가 데이터 요청 시에 우리가 만든 객체로 변환시켜서 핸들러에 전달 하듯이,  
애플리케이션 로직단에서 SQL을 전달하면 응답받은 데이터를 객체로 변환해서 받는것과 비슷한 개념이라 보면 될 것이다.  
JDBC를 사용했을 때 커넥션 열고 닫는 관리 작업과 응답받은 결과를 우리가 사용할 객체로 변경하는 반복 작업들을 줄여주는 것이다.  
하지만 SQL Mapper는 SQL을 직접 작성해야 한다. 당연히 SQL을 직접 작성하여 전달해야 하는것이지만, 이후 설명할 ORM기술은 이러한 기본적인 SQL문을 작성하지 않아도 된다.  

- SQL Mapper
    - 장점 
        - SQL 응답결과를 객체로 변환
        - 커넥션 관리, 응답결과 객체 변환 작업 등 반복코드 제거
        - 복잡한 쿼리를 처리 가능
    - 단점 
        - 간단한 CRUD SQL문을 직접 작성하여 반복작업이 많아짐
        - SQL에 의존하기 때문에 결국 DB를 바꾸면 문법수정이 필요함

### ORM

ORM 기술은 객체를 데이터베이스(관계형) 테이블과 매핑해주는 기술이다.  
해당 기술 덕분에 개발자는 SQL문을 직접 작성하지 않고도 ORM기술을 통해 SQL문을 동적으로 생성해 준다.  
데이터베이스별로 SQL문법이 다른 부분도 해결이 가능하다.  
하지만 복잡한 쿼리나 데이터베이스와 추상화된 계층구조가 상호작용을 하기 때문에 성능이슈가 발생할 수 있으며, 그 만큼 복잡성이 증가한다.  

데이터베이스와 추상화된 계층구조가 상호작용한다는 의미는 만약 회원정보가 있다고 가정하면 거기서 주소,전화번호 등도 해당 데이터베이스 테이블에 같이있다고 가정해보자.  
하지만 자바 객체에서는 회원정보에는 이름과 나이 등만있고 주소같은 것은 다른객체로 관리되어 계층화된 구조를 가진다면 처리가 힘들것이다.  

- ORM
    - 장점 
        - 객체간의 관계를 매핑해서 테이블로 생성
        - 개발자는 비즈니스 로직에 더 집중이 가능
        - 재사용성과 유지보수가 좋아짐 (객체지향적이기 때문)
        - SQL 의존도가 낮아짐
    - 단점 
        - 복잡한 시스템일수록 아래와 같은 이유로 ORM을 사용하기 어려워지고 설계부분에서 복잡도가 증가한다.  
        - [개체 관계 임피던스 불일치 문제](https://en.wikipedia.org/wiki/Object%E2%80%93relational_impedance_mismatch)
            - 자바와 데이터베이스간의 계층구조 불일치(DB는 상속 개념이 없음)   
            - DB는 PK로 동일성 구분 , 언어는 내용과 참조 주소로 동일성 구분   
            - DB는 참조키를 통해 연관(Join) , 언어는 단방향 설계 (순환참조 방지)   
            - DB와 자바언어간의 객체를 접근하는 탐색하는 방식이 다르다.  
            - DB에 프로시저가 많은 시스템에서 ORM을 사용하기 힘들다.  


### 데이터 접근 기술 정리

SQL Mapper와 ORM 기술 각각 장단점이 있다.  
SQL Mapper는 직접 SQL을 작성해야 하는 단점이 있지만 복잡한 쿼리를 직접 작성할 수 있으므로 단점으로 보기는 어렵다. 하지만 일반적인 CRUD를 작성하는 반복작업을 해야하는데 이러한 문제는 ORM으로 해결이 가능하며, ORM은 복잡한 쿼리나, 시스템 복잡도가 올라갈수록 스키마와 객체의 관계가 불일치함으로써 성능저하나 이를 위한 러닝커브가 발생한다.  
이러한 장단점들을 활용하여 간단한 CRUD작업 및 객체와 테이블을 매핑하기 쉬운 문제는 ORM을 통해서 해결하고 복잡한 쿼리나 JOIN등이 필요한 경우는 SQL Mapper기술을 사용하여 해결하는 등 각각의 기술의 장단점을 활용하여 적절히 사용하는 것이 중요할 것 같다.  
또 이러한 기술들은 모두 JDBC를 기반으로 사용하기 때문에 JDBC의 기본원리를 알아두어야 하므로 꼭 학습해야 한다.  

## JDBC를 활용

### 데이터베이스 연결

애플리케이션과 데이터베이스를 JDBC를 이용하여 연결해보자.

- ConnectionConst.class
    데이터베이스 연결정보 상수를 선언한 클래스

    ```java
    public final class ConnectionConst {
    // 커넥션 상수 생성
        public static final String URL = "jdbc:h2:tcp://localhost/~/test";
        public static final String USERNAME = "sa";
        public static final String PASSWORD = "";
    }
    ```

- DBConnectionUtil.class

    ```java
    import static hello.jdbc.connection.ConnectionConst.*;

    @Slf4j
    public class DBConnectionUtil {

        public static Connection getConnection() {
            try {
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                //상수로 정의한 연결정보를 getConnection 인자에 추가
                //DriverManager를 통해 드라이버를 찾고 커넥션을 반환.
                log.info("connection = {} , class = {}",connection,connection.getClass());
                //Connection의 구현체가 무엇일지 로그로 확인하자.
                return connection;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    ```

     DriverManager를 통해 ConnectionConst에 선언된 연결정보를 이용하여 해당 정보에 맞는 드라이버의 커넥션을 가져오는 메서드 생성  

### DriverManger getConnection 내부 코드

- DriverManager.getConnection()

    파라미터로 들어온 정보를 통해 실제 드라이버의 커넥션을 가져오는 getConnection() 호출하는 코드

    ```java
    @CallerSensitive
    public static Connection getConnection(String url,
        String user, String password) throws SQLException {
        java.util.Properties info = new java.util.Properties();

        if (user != null) {
            info.put("user", user);
        }
        if (password != null) {
            info.put("password", password);
        }

        return (getConnection(url, info, Reflection.getCallerClass()));
    }
    ```

    리플렉션을 통해 getConnection을 호출한 클래스정보와 user와 password를 프로퍼티지로 생성한 info에 넣어주어서 실제 커넥션을 반환하는 메서드를 호출한다.  

- getConnection()

    실제 드라이버의 커넥션을 반환하는 내부 메서드
    ( 주석을 통해 간단한 설명 )

    ```java
    private static Connection getConnection(
        String url, java.util.Properties info, Class<?> caller) throws SQLException {
        ClassLoader callerCL = caller != null ? caller.getClassLoader() : null;
        //호출한 클래스가 null일 경우 외부 jdbc 드라이버를 호출
        if (callerCL == null) {
            callerCL = Thread.currentThread().getContextClassLoader();
        }
        if (url == null) {
            throw new SQLException("The url cannot be null", "08001");
        }
        println("DriverManager.getConnection(\"" + url + "\")");
        ensureDriversInitialized(); //드라이버 초기화
        SQLException reason = null; 

        for (DriverInfo aDriver : registeredDrivers) {
            if (isDriverAllowed(aDriver.driver, callerCL)) {
                //해당 클래스로더 정보로 현재 순회중인 드라이버를 호출할 권한(?)이 있으면 true 
                try {
                    println("    trying " + aDriver.driver.getClass().getName());
                    //url과 info정보를 통해 해당 드라이버의 커넥션 반환 
                    Connection con = aDriver.driver.connect(url, info);
                    if (con != null) {
                        // 연결 성공 시 해당 커넥션 반환
                        println("getConnection returning " + aDriver.driver.getClass().getName());
                        return (con);
                    }
                } catch (SQLException ex) {
                    //실패
                    if (reason == null) {
                        reason = ex;
                    }
                }

            } else {
                // 순회중인 해당 드라이버에 호출 권한이없을 경우 스킵
                println("    skipping: " + aDriver.getClass().getName());
            }

        }
        // 연결 실패시 예외 + reason 반환 ...
    }
    ```
- getConnection() 내부에서 실제 드라이버 connect() 호출코드

    ```java
    //h2.driver.connect()
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (url == null) {
            throw DbException.getJdbcSQLException(ErrorCode.URL_FORMAT_ERROR_2, null, Constants.URL_FORMAT, null);
        } else if (url.startsWith(Constants.START_URL)) {
            return new JdbcConnection(url, info, null, null, false);
        } else if (url.equals(DEFAULT_URL)) {
            return DEFAULT_CONNECTION.get();
        } else {
            return null;
        }
    }
    ```
    return new JdbcConnection(url, info, null, null, false)  
    해당 드라이버의 커넥션을 반환  

### DBConnectionUtil 테스트 코드

- DBConnectionUtilTest.class

    ```java
    @Test
    void connection(){
        Connection connection = DBConnectionUtil.getConnection();
        Assertions.assertThat(connection).isNotNull();
    }
    ```

- 결과

    ```log
    connection = conn0: url=jdbc:h2:tcp://localhost/~/test user=SA , class = class org.h2.jdbc.JdbcConnection
    ```

    Connection구현체가 H2 전용 커넥션 `org.h2.jdbc.JdbcConnection`인것을 확인 가능하다.  
    해당 커넥션은 jdbc 표준 인터페이스인 java.sql.Connection를 구현하고 있다.  



### 커넥션 연결 흐름

__JDBC 커넥션 인터페이스와 구현__

![image](https://github.com/9ony/9ony/assets/97019540/95a176fc-065c-4060-8165-4fa5c7abd76e)

JDBC는 java.sql.Connection 표준 커넥션 인터페이스를 정의한다.  
각각의 DB Driver는 JDBC Connection인터페이스를 구현한 JdbcConnection구현체를 제공한다.  

__JdbcConnection 반환 과정__  

![image](https://github.com/9ony/9ony/assets/97019540/a3becf28-294a-4ac9-bedb-51a6d96531ce)

DriverManager는 라이브러리에 등록된 드라이버들을 관리한다. 

1. getConnection()이 호출된다면 파라미터로 들어온 요청정보를 통해 해당 요청정보로 호출할 수 있는 드라이버의 connect()를 호출한다.  
만약 해당 요청정보에 맞지않은 드라이버일 경우 스킵되며 다음 드라이버로 넘어가면서 라이브러리에 등록된 드라이버들을 순회하는 것이다.    
2. connect() 호출이 되어 해당 실제 데이터베이스에 연결해서 커넥션을 획득한다
3. 획득한 Connection인터페이스 구현체인 JdbcConnection가 반환된다.  


## JDBC 예제

### 데이터 저장

- MemberRepositoryV0.class
    ```java
    @Slf4j
    public class MemberRepositoryV0 {

        public Member save(Member member) throws SQLException {
            String sql = "insert into member(member_id, money) values (?,?)";

            Connection con = null;
            PreparedStatement pstm = null;

            try {
                con = DBConnectionUtil.getConnection();
                pstm = con.prepareStatement(sql);

                pstm.setString(1,member.getMemberId());
                pstm.setInt(2,member.getMoney());
                pstm.executeUpdate();
                return member;
            }catch (SQLException e){
                e.printStackTrace();
                throw e;
            }finally {
                close(con,null,pstm);
            }
        }
        
        private void close(Connection con, ResultSet rs,PreparedStatement pstm)
                throws SQLException {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    log.info("error", e);
                }
            }
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (SQLException e) {
                    log.info("error", e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    log.info("error", e);
                }
            }
        }
    }
    ```

    - save() : 멤버정보를 DB에 저장하는 메서드
        - PreparedStatement : sql문 `?`에 데이터를 바인딩해주고 sql전달 및 준비작업을 위한 인터페이스
        - Connection : DBConnectionUtil을 통해 H2 커넥션을 가져온다.  
        - executeUpdate() : 해당 메서드 준비된 pstm의 sql을 실행하는 메서드이다. 반환값은 숫자인데 해당 sql 결과의 row값의 개수를 반환한다.  

    - close() : 자원 반납 메서드 
        DB 커넥션을 가져오고 sql을 실행하고 결과값을 받는 이러한 행위는 외부 리소스를 통해 실행되기 때문에 반드지 닫아주어야 하고 만약 해당 리소스가 반납이 안되면 커넥션이 쌓이면서 서버에 장애가 일어날 수 있다.  
    

### 데이터 조회

- MemberRepositoryV0.class 추가

    DB에서 회원을 조회하는 기능  
    ```java
    public Member findById(String memberId) throws SQLException {
        //sql문
        String sql = "select * from member where member_id = ?";
        //con,pstm,rs init
        Connection con = null;
        PreparedStatement pstm = null;
        //조회한 결과값을 담는 컬렉션
        ResultSet rs = null;
        try {
            con = DBConnectionUtil.getConnection();
            pstm = con.prepareStatement(sql);
            pstm.setString(1, memberId);
            rs = pstm.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("해당 MemberId에 맞는 회원을 찾을 수 없습니다. =" + memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, rs, pstm);
        }
    }
    ```

- findByMember() 테스트코드
    ```java
    @Test
    void findById() throws SQLException{
        Member member = new Member("member", 10000);
        repository.save(member);

        Member findMember = repository.findById("member");
        log.info("member = {} , findMember = {}",member,findMember);
        Assertions.assertThat(member).isEqualTo(findMember);
    }
    ```

- 테스트 결과
    ```log
    hello.jdbc.repository.MemberRepositoryV0Test - member = Member(memberId=member, money=10000) , findMember = Member(memberId=member, money=10000)
    ```

findById()메서드를 통해 파라미터로 들어온 memberId값을 PreparedStatement를 통해 sql문에 바인딩 해주면서 회원을 조회한다.  
조회할 sql문을 executeQuery()메서드로 실행하고 ResultSet 타입의 반환값을 가지는데, 해당 쿼리를 입력했을때 조회되는 데이터가 여러개 일수도 있기 때문에 ResultSet으로 반환하는 것이다.  

### ResultSet  
ResultSet은 쿼리 결과를 받는 객체인데, 보통 결과값을 가지는 조회기능에서만 사용한다.  
ResultSet은 cursor를 가지고 있다. cursor가 가르키는 위치에 데이터가 있는지 boolean으로 반환받게 되는데 이 메서드가 ResultSet.next()이다.  

__member table__   

|cursor 0|member_id|money|  
|:---:|:---:|:---:|
|cursor 1|member|10000|  
|cursor 2|member1|20000|  
|cursor 3|member2|15000|  
|...|...|...|

위 테이블처럼 결과값이 나온다면 cursor의 초기 위치는 cursor 0을 가르킨다고 보면되고 next()메서드를 통해 이제 1번위치로 가게되고 true를 반환하면 ResultSet.getString(컬럼명)을 통해 해당 인덱스의 데이터를 member 인스턴스에 설정하여 최종적으로 member 인스턴스를 반환하는 것이다.  

위 예제에서는 where를 pk값을 통해 조회가 1개만되거나 없는경우만 나오는데 만약 pk값이아닌 money를 조건으로 한다면 while(ResultSet.next())를 하여 반복을 통해 조회하면서 객체를 반환시키면 된다.  

- __ResultSet의 주요 메서드__  
    - next() : 다음커서 위치에 row가 있는지 boolean 타입 반환  
    - absoulte(int index) : index위치의 데이터가 잇는지 boolean 타입반환  
    - getString(String columnName) : 해당 row에 있는 column이름에 일치하는 데이터를 문자열로 반환  
    - close() : ResultSet 자원을 해제  
    - previous() : 현재 커서에서 이전의 row가 있는지 bolean 반환  
    [ResultSet 공식문서](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)

### 데이터 수정 및 삭제

- MemberRepositoryV0.class 추가

    해당 회원 아이디의 금액을 수정하는 메서드

    ```java
    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DBConnectionUtil.getConnection();
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, money);
            pstm.setString(2, memberId);
            // 수정이 반영된 row 개수 반환
            int resultSize = pstm.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, null ,pstm);
        }
    }
    ```

    memberId에 해당하는 row 삭제  

    ```java
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DBConnectionUtil.getConnection();
            pstm = con.prepareStatement(sql);
            pstm.setString(1, memberId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, null, pstm);
        }
    }
    ```

- 수정, 삭제 테스트코드
    
    ```java
    @Test
    void update() throws SQLException {
        Member findMember = repository.findById("member2");
        log.info("조회한 Member = {}", findMember);
        repository.update(findMember.getMemberId(), 15000);
        Member updatedMember = repository.findById(findMember.getMemberId());
        log.info("수정된 Member = {}", updatedMember);
        Assertions.assertThat(updatedMember.getMoney()).isEqualTo(15000);
    }
    ```

    ```java
    @Test
    void delete() throws SQLException {
        Member findMember = repository.findById("member2");

        //delete
        repository.delete(findMember.getMemberId());
        //NoSuchElementException가 발생하면 테스트 통과
        Assertions.assertThatThrownBy(() -> repository.findById(findMember.getMemberId())).isInstanceOf(NoSuchElementException.class);
    }
    ```

## JDBC 정리

JDBC를 이용하여 데이터를 생성하고 조회,수정,삭제등을 예제를 통해 개발해보았다.  
JDBC를 사용한다면 다른 DB를 사용할때도 DBConnectionUtil에 필요한 ConnectionConst 정보를 변경해주고, 각각의 sql구문만 변경해주면 사용가능하고 해당 ConnectionConst 정보들로 Driver을 선택해서 DB와 연결되어 사용가능하다.  

하지만 여전히 아래와 같은 반복되는 작업들이 존재한다.  
1. DB 연결을 위한 Connection을 생성  
2. sql바인딩을 위한 PreparedStatement객체를 생성 후 바인딩 작업  
3. 결과값을 받는 ResultSet객체를 이용하여 Member객체 생성  
4. 자원들을 사용하고 마지막에 해제해주는 작업  

해당 작업들을 SQL Mapper와 ORM 기술등을 이용하여 반복되는 작업들을 해결해준다.  
이러한 기술들은 결국 Low Level에서는 JDBC를 이용하기 때문에 위와 같은 예제를 통해서 JDBC를 이용한 CRUD 기능을 개발해보았다.  

