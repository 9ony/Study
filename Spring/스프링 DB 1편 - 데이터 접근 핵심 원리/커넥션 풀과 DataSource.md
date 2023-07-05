# 커넥션 풀

## 커넥션 풀이란?

매번 커넥션을 생성하고 사용할때에 리소스가 많이 소요된다.  
이러한 커넥션을 미리생성해두고 보관하여 클라이언트에서 커넥션을 사용할때 커넥션 풀에 미리 생성되어있는 커넥션을 조회하여 사용하는것

### 커넥션을 생성하는 과정

![image](https://github.com/9ony/9ony/assets/97019540/a4af707e-5e11-4441-8185-4210c8173a5b)

위와 같은 그림처럼 커넥션을 생성하는데 아래와 같은 과정을 거친다.  
1. DriverManager를 통한 Driver라이브러리를 파라미터 정보를 통해 조회하여 Driver선택
2. 해당 드라이버를 통해 DB를 TCP 연결(3way-Handshake) 
3. 연결 후 부가정보 전달 후 인증
4. 인증 성공 시 커넥션을 생성 후 생성완료 응답
5. Driver에서 커넥션 생성완료 응답을 받으면 커넥션을 생성해서 반환함

이렇게 커넥션을 생성하는 과정은 매우 복잡하다.  

### 커넥션 풀

애플리케이션을 시작하는 시점에 커넥션 풀은 필요한 만큼 커넥션을 미리 확보해서 커넥션 풀에 보관한다.  
서비스의 특징과 서버 스펙에 따라 커넥션 개수를 늘리거나 줄이지만 보통 기본값은 보통 10개이다.  

__커넥션 생성 및 연결유지__   

![image](https://github.com/9ony/9ony/assets/97019540/7263b73f-2853-4485-9cf5-af42562225cd)

위 그림은 초기에 애플리케이션이 실행될때 커넥션을 초기화한 상태를 표현한 그림이다.  
애플리케이션 서버에서 설정한 커넥션 풀의 커넥션 개수만큼 Driver를 통하여 커넥션을 생성해둔다.  

__커넥션 풀 사용__  

![image](https://github.com/9ony/9ony/assets/97019540/67d644e4-ad61-4022-8717-852d846a6c58)

그림과 같이 애플리케이션 로직에서는 커넥션풀에 생성된 커넥션을 가져와서 사용한다.  
가져온 커넥션을 통해 DB에 SQL문을 전달하여 결과값을 응답받는다.  
커넥션을 사용하면 자원을 해제하는게 아닌 열어둔 상태에서 커넥션풀에 반환하는 구조이다.  

### 커넥션 풀 정리

커넥션 풀을 통해 커넥션 생성에 필요한 자원들을 절약하는 할수있다.    
이렇게 커넥션풀을 사용하면 커넥션을 가져오는데 드는 시간도 절약가능하고, 미리 생성된 개수를 제한해두면 무한정 생성되어 서버나 DB에서 오류가 발생하는 문제도 예방가능하다.  
커넥션 풀을 관리하는 것을 직접 만들어서 사용할 수도 있지만 커넥션풀을 관리하는 오픈소스들이 존재한다.  
커넥션 풀 오픈소스 라이브러리 종류는 commons-dbcp2, tomcat-jdbc pool, HikariCP 등이 있다.  

__커넥션 풀 오픈소스 벤치마크__  
https://github.com/brettwooldridge/HikariCP#checkered_flag-jmh-benchmarks  

![커넥션 풀 오픈소스 벤치마크](https://raw.githubusercontent.com/wiki/brettwooldridge/HikariCP/HikariCP-bench-2.6.0.png)

스프링 부트 2.0부터 기본 커넥션 풀로 hikariCP 를 제공한다.  
성능, 편의성, 안전성 측면에서 이미 검증이 되었기 때문에 커넥션풀을 사용할 때는 고민할 것 없이 `hikariCP`를 사용하면 된다.

## DataSource

DataSource란 위의 커넥션풀을 사용하기위한 다양한 방법들을 추상화 시킨 인터페이스이다.  
DriverManager를 통해 커넥션풀을 직접 만들수도 있고 오픈소스를 이용하여 커넥션풀을 사용할 수도 있는데, DriverManager를 이용해서 커넥션풀을 사용하다가 변경하게 된다면 코드수정이 불가피하다.  
이러한 문제를 커넥션을 획득하는 방법을 추상화시킨 DataSource를 이용하여 해결할 수 있다.  

![image](https://github.com/9ony/9ony/assets/97019540/3374ccfa-194a-4fd7-bf1a-6a3ac53d0317)

위 처럼 DataSource를 통해 커넥션을 가져오는 것을 추상화했다.  

> DriverManager는 getConnection()시 마다 매번 url과 정보를 파라미터로 받은 상태로 생성하기 때문에 이를 해결하기 위해 스프링 프레임워크에서 DataSource를 상속받는 `DriverManagerDataSource를 지원`한다.  

### DriverManagerDataSource로 커넥션하는 과정 코드

- DrevierManagerDataSource생성 시 정보값 세팅
    ```java
    //DriverManagerDataSource 생성 시 url과 정보를 설정해줌
    //DriverManagerDataSource는 AbstractDriverBasedDataSource 상속받음
    public DriverManagerDataSource(String url, String username, String password) {
        setUrl(url);
        setUsername(username);
        setPassword(password);
    }
    ```

- getConnection() 호출 시 getConnectionFromDriver(getUsername(), getPassword())가 호출됨

    getConnection()만 해도 초기에 객체생성과정에서 들어온 파라미터를 통해 커넥션을 생성하는 것

    ```java
    // AbstractDriverBasedDataSource 클래스는 AbstarctDataSource 상속받았음
    // DataSource의 getConnection()을 구체화
    @Override
    public Connection getConnection() throws SQLException {
        return getConnectionFromDriver(getUsername(), getPassword());
    }
    ```
- getConnectionFromDriver(getUsername(), getPassword()) 호출 시 내부 흐름

    ```java
    //mergedProps 프로퍼티지 값을 DriverManagerDataSource() 생성 시 파라미터로 들어온 정보로 세팅해줌
    protected Connection getConnectionFromDriver(@Nullable String username, @Nullable String password) throws SQLException {
        Properties mergedProps = new Properties();
        Properties connProps = getConnectionProperties();
        if (connProps != null) {
            mergedProps.putAll(connProps);
        }
        if (username != null) {
            mergedProps.setProperty("user", username);
        }
        if (password != null) {
            mergedProps.setProperty("password", password);
        }
        
        Connection con = getConnectionFromDriver(mergedProps);
            // getConnectionFromDriver(mergedProps) 로직 실행
            ```java
            // DriverManagerDataSource.getgetConnectionFromDriver(props) 실행        
            @Override
            protected Connection getConnectionFromDriver(Properties props) throws SQLException {
            String url = getUrl();
            Assert.state(url != null, "'url' not set");
            if (logger.isDebugEnabled()) {
                logger.debug("Creating new JDBC DriverManager Connection to [" + url + "]");
            }
            return getConnectionFromDriverManager(url, props);
                // getConnectionFromDriverManager(url, props) 로직 실행
                ```java
                //DriverManager.getConnection(url, props); 호출하여 Connection 반환함
                protected Connection getConnectionFromDriverManager(String url, Properties props) throws SQLException {
                    return DriverManager.getConnection(url, props);
                }
                ``` 
            }
        if (this.catalog != null) {
            con.setCatalog(this.catalog);
        }
        if (this.schema != null) {
            con.setSchema(this.schema);
        }
        return con;
    }
    ```

위와 같은 과정을 통해 DriverManagerDataSource 생성 시에 url과 user,password등을 파라미터로 넘겨준 다음에 getConnection()을 통해서 커넥션을 가져올 수 있다.  

### 설정과 사용의 분리

- 설정: DataSource를 만들고 필요한 속성들을 사용해서 URL , USERNAME , PASSWORD 같은 정보를 입력하
는 것을 말한다. 이렇게 설정과 관련된 속성들은 한 곳에 있는 것이 향후 변경에 더 유연하게 대처할 수 있다.  

- 사용: 설정은 신경쓰지 않고, getConnection()만 호출해서 사용하면 된다.  

설정과 사용을 분리함으로써 추후에 DriverManagerDataSource를 Hikari or dbcp2 등 다른 DataSource를 상속받은 클래스로 교체하더라도 getConnection()을 통해 커넥션을 가져오는 로직은 변경할 필요가 없어지면서 설정하는 부분과 사용하는 부분의 명확한 분리가 가능하다.  

## DataSource 예제

DataSource 표준 인터페이스를 통해 기존과 다르게 커넥션을 어떻게 얻어오는지,  
설정과 사용을 분리함으로써 어떤 장점이 있는지 예제를 통해 알아보자.

### DriverManagerDataSource

```java
//DriverManager 사용
@Test
void driverManager() throws SQLException{
    Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    log.info("connection={}, class={}", con1, con1.getClass());
    log.info("connection={}, class={}", con2, con2.getClass());
}

//DriverManagerDataSource 사용
@Test
void dataSourceDriverManager() throws SQLException {
    DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    // dataSource.getConnection()는 내부적으로
    // DriverManager.getConnection(DriverManagerDataSource 생성 시 파라미터 값);을 호출한다
    Connection con1 = dataSource.getConnection();
    Connection con2 = dataSource.getConnection();
    log.info("connection={}, class={}", con1, con1.getClass());
    log.info("connection={}, class={}", con2, con2.getClass());
}
```

앞서 설명한대로 DriverManager와 DriverManagerDataSource의 커넥션을 얻어오는 방식에 차이가 보인다.  
DriverManager는 커넥션을 얻어올때 마다 파라미터에 정보값을 주어서 얻어오지만,  
DriverManagerDataSource는 생성자를 통해 객체를 만들때 설정정보를 추가하고,  
커넥션을 가지고올때는 생성한 dataSource만 의존하여 컬렉션을 반환받는다.  

### HikariDataSource

```java
@Test
void dataSourceConnectionPool() throws SQLException, InterruptedException {
    //커넥션 풀링: HikariProxyConnection(Proxy) -> JdbcConnection(Target)
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);
    dataSource.setMaximumPoolSize(10);
    dataSource.setPoolName("MyPool");
    useDataSource(dataSource);
    Thread.sleep(1000); //커넥션 풀에서 커넥션 생성 시간 대기
}
```

HikariDataSource도 DataSource를 생성할때 setter를 사용하는것만 다르고 사용하는 부분과 설정하는 부분이 분리되어있다.  
그리고 HikariDataSource를 이용하면 내부적으로 초기에 생성된 커넥션 풀에서 가져온 커넥션을 사용하기 때문에 풀사이즈(기본값 = 10)을 세팅해주는것도 볼 수 있다.  

__로그 결과__  

![image](https://github.com/9ony/9ony/assets/97019540/b8a6d057-547f-442a-a89e-15ab8d4c4874)

`MyPool connection adder`로그와 함께 설정한 풀네임의 풀에 coon0~9까지 10개가 추가되는 것을 볼 수있고,  
커넥션풀에 등록될때는 다른 쓰레드를 통해 커넥션을 추가하기 때문에 sleep()을 하지않으면 추가되는 로그가 안찍힌다.  

다른 쓰레드를 통해 채우는 이유는 커넥션을 생성해서 풀에 추가하는 것은 오래걸리는 작업이기 때문에 애플리케이션을 실행하는 쓰레드단위에서 해당 작업을 하게된다면 실행시간이 늦어지게 될 것이다.  

마지막으로 `After adding stats (total=10, active=2, idle=8, waiting=0)` 로그가 찍히는 것을 볼 수 있는데,2개가 사용(active)되고 8개가 대기 상태(idle)인것을 볼 수 있다.  

### DataSource 적용

JDBC로 접근했던 예제를 DataSource를 적용해보자.  

- MeberRepositoryV1
    ```java
    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    //...
    //저장,조회,삭제,수정은 유지
    //단 기존에 DBConnectionUtil.getConnection()을 getConnection()으로 변경해주자.

    // JdbcUtils를 통해 자원 반납
    private void close(Connection con, ResultSet rs,  PreparedStatement stmt) {
        /*try{
            Thread.sleep(10000);
        }catch (Exception e){
            e.printStackTrace();
        }*/
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }
    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
    ```

    저장,조회,삭제,수정은 주석에 써놧듯이 기존것을 getConnection()메서드를 작성하고 DataSource를 사용하기 때문에 `DBConnectionUtil.` 부분을 없애주자.  
    이렇게 MemberRepositoryV1은 Hikari를 사용하든 dbcp2 등 어떠한 커넥션풀 라이브러리를 사용해도 DataSource를 구현한 라이브러리라면 코드를 수정할 필요가 없어진다.  

- 테스트 코드
    ```java
    @Slf4j
    class MemberRepositoryV1Test {

        MemberRepositoryV1 repository;

        @BeforeEach
        void beforeEach() throws Exception {
            //DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(URL);
            dataSource.setUsername(USERNAME);
            dataSource.setPassword(PASSWORD);
            repository = new MemberRepositoryV1(dataSource);
        }
    ```
    `@BeforeEach`에 있는 로직은 DataSource에서 커넥션을 가져오는데 필요한 정보들을 사전에 설정해주는것으로 보면된다.   
    DriverManagerDataSource를 DataSource 구현체로 사용하게되면 매번 커넥션 사용시마다 커넥션을 생성하게 된다.  
    HikariDataSource 사용 시 이전 `DataSource 비교`에서 봤듯이 애플리케이션 실행시 생성된 커넥션풀에 커넥션을 가져와서 사용하게 된다.  
    또 이렇게 초기에 DataSource에 커넥션 생성에 필요한 사전작업만 해준다면 Repository에서는 DataSource.getConnection()을 통해 커넥션을 가져오기만 하면 된다. 

    ```java
        @Test
        void crud() throws SQLException {
            log.info("start");

            //save
            Member member = new Member("memberV0", 10000);
            repository.save(member);

            //findById
            Member memberById = repository.findById(member.getMemberId());
            Assertions.assertThat(memberById).isNotNull();

            //update: money: 기존값 -> 20000
            repository.update(member.getMemberId(), 20000);
            Member updatedMember = repository.findById(member.getMemberId());
            Assertions.assertThat(updatedMember.getMoney()).isEqualTo(20000);

            //delete
            repository.delete(member.getMemberId());
            Assertions.assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                    .isInstanceOf(NoSuchElementException.class);

        }
    }
    ```
    
    DataSource 구현체를 Repository에 주입하면 해당 crud()는 변경할 사항이 없어지고,  
    DriverManagerDataSource와 HikariDataSource로 각각 테스트를 실행해보면 DriverManagerDataSource는 커넥션을 매번 생성하고, HikariDataSource는 conn0번째를 재사용하는것을 볼 수 있다.  
    이때 HikariCP의 커넥션은 래핑되어 있는데 해당 래핑된객체에서 close()가 호출되면 커넥션을 해제하는게 아닌 recycle()이 되어 커넥션풀에 반납되게 된다.  
    
### 정리  

앞서 애플리케이션 로직에서 종류가 많은 데이터베이스의 드라이버를 접근하기 위해 JDBC 인터페이스를 통해 접근하였는데, 커넥션풀을 사용하기 위해서 이제 DataSource 인터페이스를 통해 다양한 종류의 커넥션풀의 커넥션을 얻어오는 방법을 일관성있게 사용할 수 있게됬다.  
커넥션 풀은 초기 애플리케이션이 실행될때 커넥션을 생성하여 풀에 보관하는 시간은 소요되지만 데이터베이스에 접근할 때마다 많은 리소스가드는 커넥션을 가져오는 시간과 자원을 절약할수 있고, 적절한 커넥션 개수의 제한을 둠으로써 서버나 DB에서 오류가 발생하는 문제도 예방 가능하다.  

즉, jdbc를 이용하여 DB 접근 시 매번 커넥션을 생성하는 비용을 절약하기 위해 커넥션풀을 이용하는데 이때 커넥션풀 라이브러리가 다양하다. 커넥션풀에서 커넥션을 가져오는 방법을 추상화시킨 DataSource를 사용하여 일관성있게 커넥션을 애플리케이션 로직에서 가져다 쓸 쑤 있다.