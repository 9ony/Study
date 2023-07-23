# MyBatis

## MyBatis란?

MyBatis도 JdbcTemplate처럼 SQL Mapper 기술이다.  
기본적으로 JdbcTemplate이 제공하는 대부분의 기능을 제공하고 더 많은 추가기능이 있다.  
쿼리를 XML or 어노테이션으로 작성하는 기술이다.  
JdbcTemplate보다 동적쿼리 작성에 용이하다.  
[MyBatis 구조 참고 블로그](https://linked2ev.github.io/mybatis/2019/09/08/MyBatis-1-MyBatis-%EA%B0%9C%EB%85%90-%EB%B0%8F-%EA%B5%AC%EC%A1%B0/)

## MyBatis 설정
- build.gradle
    ```gradle
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.0'
    ```
    ❗ Mybatis는 스프링이 공식으로 지원하는 라이브러리가 아니기 때문에 버전을 명시해줘야함.  

    추가되는 라이브러리  
    - mybatis-spring-boot-starter : MyBatis를 스프링 부트에서 편리하게 사용할 수 있게 시작하는 라이브러리
    - mybatis-spring-boot-autoconfigure : MyBatis와 스프링 부트 설정 라이브러리
    - mybatis-spring : MyBatis와 스프링을 연동하는 라이브러리
    - mybatis : MyBatis 라이브러리

- application.properties (main,test 둘다 적용)
    ```properties
    #MyBatis
    mybatis.type-aliases-package=hello.itemservice.domain
    mybatis.configuration.map-underscore-to-camel-case=true
    logging.level.hello.itemservice.repository.mybatis=trace
    ```
    - mybatis.type-aliases-package  
        원래 타입 정보를 사용 시 패키지 명시해야함. (해당 설정으로 명시 시 생략 가능)   
        지정한 패키지와 그 하위 패키지가 자동으로 인식된다. (여러 위치 지정 시 `,` , `;`로 구분)  
            
    - mybatis.configuration.map-underscore-to-camel-case  
        db컬럼명을 카멜표기법으로 인식 가능하게하는 설정  

    - logging.level.hello.itemservice.repository.mybatis=trace  
        MyBatis에서 실행되는 쿼리 로그 설정  

MyBatis는 JdbcTemplate보다 약간의 설정이 필요하고, 동적 쿼리와 복잡한 쿼리가 없다면 JdbcTemplate를 사용하자.  
[MyBatis 가이드](https://mybatis.org/mybatis-3/ko/index.html)

## Mybatis 적용

### @Mapper 인터페이스
- ItemMapper.interface

    ```java
    import org.apache.ibatis.annotations.Mapper;
    import org.apache.ibatis.annotations.Param;

    @Mapper
    public interface ItemMapper {
        void save(Item item);
        void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto updateParam);
        Optional<Item> findById(Long id);
        List<Item> findAll(ItemSearchCond itemSearch);
    }
    ```

    > 참고로 import에 ibatis는 mybatis의 이전 이름이므로 같은것이라 보면된다.  

    @Mapper : XML을 호출해주는 인터페이스이다. 추후 해당 메서드명과 같은 XML에 id의 sql문을 호출한다.  
    @Param : XML 매핑 시 해당 객체의 키 이름 설정 어노테이션

ItemMapper는 인터페이스만 만들고 `구현체는 따로 만들지 않는다`.  
해당 매퍼의 구현체는 Mybatis 연동 모듈에서 `동적프록시(JDK Dynamic Proxy)로 프록시 객체로 생성`해준다.  
> 연동 모듈에서 @Mapper 인터페이스 조회하여 프록시객체 생성 후 스프링컨테이너에 빈으로 등록  

해당 프록시 구현체는 아래와 같은 기능을 가지고 있다.  
\- xml 호출  
\- 예외를 스프링 예외 추상화(DataAccessException)로 변환  
\- 커넥션 , 트랜잭션 동기화(SqlSession)
SqlSession으로 데이터베이스와 연결을 하는데 getConnection(DataSource)와 같은 역할    


###  Mybatis XML 예시
> 파일 경로(중요) : resource/hello/itemservice/repository/mybatis/ItemMapper.xml 

> 참고 - XML 파일 경로 수정하기
XML 파일을 원하는 위치에 두고 싶으면 application.properties 에 다음과 같이 설정하면 된다.  
mybatis.mapper-locations=classpath:mapper/**/*.xml  
이렇게 하면 resources/mapper 를 포함한 그 하위 폴더에 있는 XML을 XML 매핑 파일로 인식한다.   
이 경우 파일 이름은 자유롭게 설정해도 된다.  

- ItemMapper.xml  
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="hello.itemservice.repository.mybatis.ItemMapper">
        <!-- void save(Item item); -->
        <insert id="save" useGeneratedKeys="true" keyProperty="id">
            insert into item (item_name, price, quantity)
            values (#{itemName}, #{price}, #{quantity})
        </insert>

        <!-- void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto updateParam); -->
        <update id="update">
            update item
            set item_name=#{updateParam.itemName},
                price=#{updateParam.price},
                quantity=#{updateParam.quantity}
            where id = #{id}
        </update>

        <!-- Optional<Item> findById(Long id); -->
        <select id="findById" resultType="Item">
            select id, item_name, price, quantity
            from item
            where id = #{id}
        </select>

        <!-- List<Item> findAll(ItemSearchCond itemSearch); -->
        <select id="findAll" resultType="Item">
            select id, item_name, price, quantity
            from item
            <where>
                <if test="itemName != null and itemName != ''">
                    and item_name like concat('%',#{itemName},'%')
                </if>
                <if test="maxPrice != null">
                    and price &lt;= #{maxPrice}
                </if>
            </where>
        </select>
    </mapper>
    ```

insert,delete,update,selelct 제어문 등은  
insert문은 `<insert>` , select문은 `<select>` 등으로 태그를 시작하면 된다.  

- namespace : <mapper namespace="hello.itemservice.repository.mybatis.ItemMapper.java">
    현재 xml과 매핑될 클래스의 경로를 입력해주자.  

- insert id="save" 
    useGeneratedKeys="true" keyProperty="id" 속성을 주면 KeyHolder를 쓴것처럼 keyProperty 해당하는 컬럼의 값을 받을 수 있다.  
    이후 Insert를 하고 파라미터로 넘어온 Item객체의 id에 DB에서 생성된 값이 들어감.  
    ```java
    KeyHolder key = new GeneratedKeyHolder();
    template.update(sql, param, key);
    Long item_id = key.getKey().longValue();
    item.setId(item_id);
    ```
    이렇게 파라미터로온 Item객체에 setId()를 통해 id값을 넣어주는 것까지 해준다고 보면 된다.  

    #{매퍼에서 넘긴 객체의 프로퍼티명}와 같이 입력하면 파라미터로 들어온 객체의 값을 바인딩한다.  

- update id="update"
    파라미터가 2개일땐 해당 @Param("문자열")에 맞춰서 #{문자열.프로퍼티명}으로 값을 바인딩 시켜주어야 한다.  

- select id="findById"
    resultType 속성을 통해 반환 타입을 명시  
    application.properties에서 `mybatis.type-aliasespackage=hello.itemservice.domain 속성을 지정`해서 prefix를 생략가능하다.  
    원래 `resultType=hello.itemservice.domain.Item`으로 명시해야 함
    mybatis.configuration.map-underscore-to-camel-case=true 을 통해 item_name -> itemName 카멜표기법으로 변경해준다.  

- select id="findAll"
    `<where>` , `<if>` 태그로 편리한 동적 쿼리를 작성 가능  
    if는 `test="조건"`이 만족하면 작성한 구문을 추가  
    where는 처음 if 조건에 만족할 시 and가 where로 입력된다.  
    모두 실패 시 SQL where를 만들지 않음  
    
__태그의 속성(일부) 설명__
\- id : 네이스페이스 내 메서드명과 일치 시키는 구분자   
\- resultType : ResultSet으로 만들 객체 반환타입 설정  
\- resultMap : ResultSet으로 사용자가 정의한 규칙에 따라 매핑 
\- parameterType :  sql 실행에 필요한 데이터의 타입(클래스 타입,타입)을 외부로부터 받아야할 때 사용 (생략가능)  
\- useGeneratedKeys : 데이터베이스에서 내부적으로 생성한 키를 받는 JDBC getGeneratedKeys메소드를 사용하도록 설정  
\- keyProperty : getGeneratedKeys 메소드나 insert 구문의 selectKey 하위 엘리먼트에 의해 리턴된 키를 셋팅할 프로퍼티를 지정 ( `,`로 여러개 가능) 
    
[태그의 속성 참고자료](https://mybatis.org/mybatis-3/ko/sqlmap-xml.html)

__XML 특수문자 처리__  
\- CDATA 구문 문법을 사용 예시  
```sql
<![CDATA[
and price <= #{maxPrice}
]]>
```
\- 이스케이핑 사용  
```sql
< : &lt;
> : &gt;
& : &amp;
```

### MyBatisItemRepository

```java
@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisItemRepository implements ItemRepository {

    private final ItemMapper itemMapper;

    @Override
    public Item save(Item item) {
        //프록시 객체 확인
        log.info("ItemMapper = {} ",itemMapper.getClass());
        log.info("before save Item = {}",item);
        itemMapper.save(item);
        log.info("after save Item = {}",item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        itemMapper.update(itemId, updateParam);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemMapper.findById(id);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        return itemMapper.findAll(cond);
    }
}
```

주입받은 itemMapper의 메서드들을 위임해주자.  
save()에서 itemMapper.save() 동작전에 파라미터로 넘어온 Item의 id가 설정되는지 로그를 통해 확인  
(useGeneratedKeys 동작확인)


### 애플리케이션 실행

우선 실행 전에 Configuratiion 설정을 바꿔주자
- MyBatisConfig
    ```java
    @Configuration
    @RequiredArgsConstructor
    public class MyBatisConfig {

        private final ItemMapper itemMapper;

        @Bean
        public ItemService itemService(){
            return new ItemServiceV1(itemRepository());
        }
        @Bean
        public ItemRepository itemRepository() {
            return new MybatisItemRepository(itemMapper);
        }
    }
    ```

    작성한 ItemMapper를 주입받고 MybatisItemRepository 빈을 등록하자.  

- ItemServiceApplication.class
    
    ```java
    @Import(MyBatisConfig.class)
    @Slf4j
    @SpringBootApplication(scanBasePackages = "hello.itemservice.web")
    public class ItemServiceApplication {
    ```

    MyBatisConfig.class 설정 import  

- ItemRepositoryTest3 테스트 실행
    
    ![image](https://github.com/9ony/9ony/assets/97019540/fd8993ea-7b80-4338-90d3-71d131e3e7ae)

    빨간색 박스를 보면 ItemMapper가 JDK Dynamic Proxy 객체로 생성된 것을 볼 수 있다.  
    
    보라색 박스에서 Item객체에 id가 없는데 save 이후 Item객체에 DB에서 생성된 id값이 들어갔다.  

    노란색 박스를 보면 마이바티스 관련 로그인 쿼리문, 바인딩된 파라미터,결과값 등이 출력된다.    
    이 로그를 없애려면 properties파일에서 logging.level.hello.itemservice.repository.mybatis=trace 삭제하자.

## 정리

Mybatis가 JdbcTemplate보다 설정할 부분은 많지만 동적쿼리나 추가 다른기능들을 더 많이 지원한다.  
JdbcTempalte에서 파라미터를 생성해서 넣어주고 ResultSet을 RowMapper를 이용해 결과값을 반환받았다. 
MyBatis는 @Mapper 인터페이스와 xml에서 쿼리만 작성한다면 해당 작업들을 연동모듈에서 생성한 프록시객체에서 자동으로 처리해준다.  

추가로 동적쿼리, 연관관계매핑 , 빌더패턴 적용 등의 정보는 아래 공식문서에 잘 정리되어 있다.   
[동적 쿼리](https://mybatis.org/mybatis-3/ko/dynamic-sql.html)
[매퍼 어노테이션](https://mybatis.org/mybatis-3/ko/java-api.html#mapper%EC%95%A0%EB%85%B8%ED%85%8C%EC%9D%B4%EC%85%98-%EC%98%88%EC%A0%9C)
[관계 매핑](https://mybatis.org/mybatis-3/ko/sqlmap-xml.html#resultmap)

이외에도 [공식문서](https://mybatis.org/mybatis-3/ko/index.html)에 다양한 기능들이 잘 설명되어 있다.  

### 추가

MyBatis와 같은 ORM(객체 관계 매핑) 프레임워크를 사용할 때,  
동일한 프라이머리 키(ID)를 가진 레코드에 대한 조회를 여러 번 수행하면 동일한 객체 인스턴스가 캐싱되어 동일한 참조 주소를 반환  

ex)
Member member1 = memberDAO.select(id=0);
Member member2 = memberDAO.select(id=0);
member1 == member2 (ture);