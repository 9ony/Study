# JPA 예제 

## JPA 설정

우선 JPA를 사용하기 위해서 의존성을 추가해야 한다.  

```properties
//JPA, 스프링 데이터 JPA 추가
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
//implementation 'org.springframework.boot:spring-boot-starter-jdbc'
```

> jpa에 jdbc도 포함되어 있기 때문에 jdbc는 의존성을 제거해주어도 된다.

- 추가된 라이브러리
    
    [jpa 2.7.14 link](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/2.7.14)  
    
    ![image](https://github.com/9ony/9ony/assets/97019540/c16ccfc0-8647-4c06-8c90-533ed9295952)

    Hibernate : JPA 구현체  
    persistence-api : JPA 인터페이스  
    spring-data-jpa : 스프링 데이터 JPA 라이브러리  

- properties 설정 (테스트도 같이 추가하자.)  

    ```properties
    #JPA LOG 출력
    logging.level.org.hibernate.SQL=DEBUG
    logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
    ```
    - __org.hibernate.SQL=DEBUG__  
        하이버네이트가 생성하고 실행하는 SQL을 확인(logger)  
    - __org.hibernate.type.descriptor.sql.BasicBinder=TRACE__  
        SQL에 바인딩 되는 파라미터를 확인  
    - __spring.jpa.show-sql=true__  
        `System.out` 콘솔을 통해서 SQL이 출력 (미권장)  
        (둘다 켜면 logger , System.out 둘다 로그가 출력되어서 같은 로그가 중복해서 출력됨)  

## JPA 적용하기

### Item.class 수정

❗ 이전 Item을 jpax.ItemV0.class로 변경하였다 (남겨두기 위함)  

- Item.class
    ```java
    import javax.persistence.*;

    @Data
    @Entity
    //@Table(name="Item") //객체명과 테이블명이 같다면 생략가능
    public class Item {

        //해당 필드가 pk이고 Identity전략 사용 (DB에서 생성한 id값을 적용)
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        //@Column 어노테이션을 통해 테이블의 컬럼명을 명시해줌
        //단 카멜 표기법을 스네이크(`_`) 표기법으로 자동 변경해준다. 즉, 생략해도됨
        @Column(name= "item_name", length = 10)
        private String itemName;
        private Integer price;
        private Integer quantity;

        public Item() {
        }

        public Item(String itemName, Integer price, Integer quantity) {
            this.itemName = itemName;
            this.price = price;
            this.quantity = quantity;
        }
    }
    ```

    Item 객체에 `@Entity 어노테이션을 명시`해줌으로써 `Item객체와 RDB의 Item 테이블이 매핑`된다.  

    - @Id
        단 `Entity는 속성이 변하는 객체`이기 때문에 `구별하기 위한 Id가 필요`한데 이는 매핑할 객체에도 당연히 명시해주어야 한다.  
        @Id를 통해 해당 어노테이션을 적용한 필드가 pk임을 명시한다.   

    - @GeneratedValue
        해당 값에 어느전략을 사용할지 설정하는 어노테이션  
        예제에는 Identity전략을 사용함으로써 DB에서 생성된 id값을 해당 필드에 설정한다.  
    
    - @Column
        Item테이블의 컬럼명은 item_name이다.  
        Item객체의 필드명은 itemName이므로 @Column(name="컬럼명")을 통해 설정했다.  
        단 해당필드가 `카멜표기법으로 되어있으면 해당 필드명을 스네이크 표기법으로 전환`하여 컬럼명을 찾아주기 때문에 생략해도 된다.  
        

### JpaItemRepositoryV1.class

- 트랜잭션 설정 및 EntityManager 주입

    ```java
    @Slf4j
    @Repository
    @RequiredArgsConstructor
    @Transactional
    public class JpaItemRepositoryV1 implements ItemRepository {

        private final EntityManager em;

        //CRUD 메서드..
    }    
    ```

    __@Transactional__  
    JPA는 하나의 세션(트랜잭션)안에서 작동하기 때문에 반드시 @Transactional내에서 동작해야 하므로, Service계층에 트랜잭션이 적용이 안되었다면 Repsoitory에 트랜잭션을 적용해주어야 한다.    

    __EntityManager__  
    EntityManager는 원래 EntityManagerFactory를 통해 DataSource를 주입하고 트랜잭션 설정등을 거쳐야 되는데 이를 스프링부트는 자동으로 처리해주기 때문에 주입받아서 사용하면 된다.  

- save
    ```java
    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }
    ```
    
    EntityManager.persist() 메서드를 통해 Item객체를 저장한다.  
    persist의 의미는 영구히 보관한다는 의미  

- update
    ```java
    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item item = em.find(Item.class,itemId);
        item.setItemName(updateParam.getItemName());
        item.setPrice(updateParam.getPrice());
        item.setQuantity(updateParam.getQuantity());
    }
    ```
    JPA를 통한 업데이트는 기존 JDBC와는 다르게 동작한다.  
    JPA는 기본적으로 영속성 컨텍스트를 이용한다.  
    우선 em.find를 통해 파라미터로 들어온 id를 통해 Item객체를 조회한다.  
    영속성 컨텍스트에 item이 있다면 DB에서 조회하지 않고 영속성 컨텍스트에 캐싱된 Item을 가져온다.  
    이후 해당 객체를 setter를 통해 수정하여, 해당 트랜잭션이 커밋될때 영속성 컨텍스트에 Entity와 기존 스냅샷을 비교해 변경사항이 생겼기 때문에 JPA에서 update를 수행하는 것이다.  


- findById
    ```java
     @Override
    public Optional<Item> findById(Long id) {
        /*try {
            Item item = em.find(Item.class, id);
            return Optional.of(item);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }*/

        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }
    ```

    id로 Item을 조회하는 메서드이다.  
    영속성 컨텍스트에서 id를 조회한 후 없으면 db에서 해당 데이터를 조회한다.  

- findAll
    ```java
     @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String jpql = "select i from Item i";
        //jpql의 Item이 테이블이아니라 Item 객체를 가르킨다.
        /*
        //동적 쿼리가 아닐경우 아래와 같이 조회한 Item객체들을 넘겨준다.  
        List<Item> result = em.createQuery(jpql,Item.class).getResultList();
        return result;
        */
        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }
        log.info("jpql={}", jpql);
        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();
    }
    ```

    JPQL(Java Persistence Query Langauge)는 객체지향 쿼리 언어이다.  
    JPQL은 엔티티 객체를 대상으로 SQL을 실행한다.  
    해당 String jpql에서 Item은 테이블명이 아닌 우리가 만든 객체명(`대소문자 유의`)이다.  
    JPQL을 이용하여 작성하는 이유는 간단하게 설명하면 만약 해당 쿼리에 Item객체를 이용해 바인딩하는 이유는 JPA를 거쳐서 매핑과정을 거친 후에 SQL쿼리를 반환하기 때문이다.  
    
    > ❗ JPQL쿼리는 영속성컨텍스트에서 조회하는 것이아닌 DB에서 조회한다.  
    그래서 기본옵션으로 JPQL 쿼리를 실행하기전에 영속성 컨텍스트의 변경사항을 DB에 반영(flush함)하는데,  
    그 이유는 이전에 변경한 객체가 있으면 반영시켜서 동기화를 해줘야지 알맞는 조회결과가 나오기 때문이다.  

    > TypedQuery로 실행된 쿼리는 두번쨰 인자로 주어진 클래스를 반환   
    Query는 타입이 명확하지 않을때 사용하고 조회 컬럼이 1개 이상일 경우 Object[], 1개일 경우 Object를 반환   

    해당 메서드로직을 보면 JPA를 적용하기 전 로직과 유사한 부분이 많다.  
    여전히 동적쿼리 문제가 존재하기 때문인데, 이는 QueryDSL로 해결이 가능하다.  
    그래서 실무에서는 보통 JPA+QueryDSL 조합을 많이 이용한다.  

### 결과
- 테스트
    ```java
    @Test
    void save() {
        //given
        Item item = new Item("itemA", 10000, 10);

        //when
        Item savedItem = itemRepository.save(item);

        //then
        Item findItem = itemRepository.findById(item.getId()).get();
        assertThat(findItem).isEqualTo(savedItem);
    }
    ```
- 로그

    ![image](https://github.com/9ony/9ony/assets/97019540/956e955a-7040-4381-9dfb-8e276fc55713)


로그를 보면 insert쿼리를 수행한 것을 볼 수 있지만 select문은 없다.  
이는 영속성 컨텍스트에 이미 insert할때 이미 해당 entity가 영속상태이기 때문에 select할때 영속성 컨텍스트에서 조회하기 때문에 DB에서 가져오는게 아닌 영속성 컨텍스트에서 가지고 와서 SQL문을 실행하지 않는 것이다.  
그러면 DB에 저장된 값으로 불러올려면 어떻게 해야될까??  

- 테스트 코드 수정  
    테스트에 EntityManager를 주입해주자.  
    ```java
    @Autowired
    EntityManager em;

    @Test
    void save() {
        //given
        Item item = new Item("itemA", 10000, 10);

        //when
        Item savedItem = itemRepository.save(item); // item Managed(영속)됨
        em.detach(savedItem); // item 준영속 시킴 테스트이므로 em.clear()도 가능
        //then
        Item findItem = itemRepository.findById(item.getId()).get(); //item을 준영속시켰다면 select쿼리로 조회됨
        assertThat(findItem).isEqualTo(savedItem);
    }
    ```

- 로그  

    ![image](https://github.com/9ony/9ony/assets/97019540/a673fcfd-f723-4d0f-ae19-2456f6e9e4d3)

    EntityManager.detach()는 Managed(영속)상태인 개체를 준영속(Detached)상태로 만든다.  
    그래서 findById를 통해 해당 개체를 찾을때 영속상태에 해당 개체가 없기때문에 select 쿼리를 날려서 실제 DB에서 조회한다.  

## JPA 예외
EntityManager는 JPA기술이다. 해당 EntityManager메서드에서 예외가 발생하면 JPA관련 예외를 던진다.  
해당 JPA예외도 런타임예외(UnCheckExcpetion)이다.  
하지만 SQLMapper인 JdbcTempalte,Mybatis를 적용했던 코드를 보면 Template과 프록시로 생성된 Mapper객체 내에서 예외가 발생하면 해당 예외를 스프링 데이터 접근 예외로 변환시켜서 Repository로 넘어왔었는데, JPA코드를 보면 Repository내에서 실행되기 때문에 따로 JPA 관련된 예외처리가 필요하다.  
즉, 스프링 예외 변환기가 적용되지 않기 때문에 서비스계층에 스프링 예외가 아닌 JPA 예외가 넘어가게 된다.  

### JPA 예외 발생

실제 예외를 발생시켜보자.  
findAll() 메서드의 jpql문자열을 아래와 같이 변경해서 테스트를 실행해보자.  
```java
String jpql = "selectBad i from Item i";
```

![image](https://github.com/9ony/9ony/assets/97019540/1171e981-76ce-4191-9c5a-c038ca71cbbb)

하지만 예상과 다르게 `InvalidDataAccessApiUsageException` 스프링 예외가 출력되었다.  
그 이유는 `@Repository`기능에 있다.  

- @Repository 기능  
    @Repository 가 붙은 클래스는 컴포넌트 스캔의 대상이 된다.  
    @Repository 가 붙은 클래스는 `예외 변환 AOP의 적용 대상`이 된다.(프록시 적용됨)  
    스프링과 JPA를 함께 사용하는 경우 스프링은 JPA 예외 변환기(`PersistenceExceptionTranslator`)를 등록한다.  
    예외 변환 AOP 프록시는 `JPA 관련 예외가 발생`하면 JPA 예외 변환기를 통해 발생한 예외를 `스프링 데이터 접근 예외로 변환`한다.  

![image](https://github.com/9ony/9ony/assets/97019540/e6edfa14-25fc-46f7-a683-e2267b2801e8)

- 참고  
> 스프링 부트는 PersistenceExceptionTranslationPostProcessor를 자동으로 등록하는데, 여기에서
@Repository를 AOP 프록시로 만드는 어드바이저가 등록된다.

> 실제 JPA 예외를 스프링 데이터 접근 예외 변환 할 때 복잡한 과정을 변환한다.  
실제 변환하는 코드는 EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible()이다.  
해당 코드를 들어가보면 다양한 JAP예외를 스프링 데이터 접근 예외로 변경하는것을 볼 수 있다.  