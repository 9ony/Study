# Spring Data JPA

스프링 데이터 JPA를 학습하기에 앞서 해당 기술은 JPA를 깊게 이해해야 한다.  
JPA를 기반으로 더욱 편리하게 사용할 수 있는 기술이기 때문에 JPA를 잘 이해하고 있어야 한다.
Spring Data JPA를 간략하게 알아보고 예제도 작성해보자!!

## Spring Data JPA란?

등장이유는 간단하게 JPA를 좀더 쉽게 사용하기 위함인데, 스프링 데이터 JPA는 스프링 데이터의 확장판이라고 보면됩니다.  
Spring에서 JPA를 사용할 때는 이 구현체들을 더 쉽게 사용할 수 있게 추상화시킨 Spring Data JPA 이용합니다.
스프링 데이터는 CRUD 처리를 위한 공통 인터페이스 제공하고 Spring Data JPA도 이를 이용해 CRUD작업이 더욱 간편화 됩니다.  
또 스프링 시큐리티,배치 등등의 다양한 모듈과도 통합이 쉬워집니다.  

## Spring Data JAP 등장배경

### EJB의 EntityBean  

스프링 이전에 EJB가 있었습니다.  
그리고 EJB에도 ORM기술인 Entity Bean이라는 기술이 존재했는데,  
해당 기술이 너무 복잡하고 성능도 잘나오지 않았다고 합니다.  
그래서 하이버네이트라는 ORM 기술이 나왔는데, 해당 기술로 인해 Entity Bean은 이제 거의 사용되지 않고 SQL 작성도 다른 라이브러리를 사용하는게 더욱 개발할때 편하고 성능도 더 잘나왔다고 합니다.  
그래서 이제 스프링이 나올때 JPA라는 표준인터페이스를 정의했는데 이게 하이버네이트를 기반으로 만든 표준이 됬습니다.  

### Spring Data

스프링 데이터란 기존에 RDB인 MySQL,Oracle 등등이 존재했지만 추후에 NoSQL,메세징시스템 등등의 다른 데이터베이스도 많이 개발되었습니다.  
그래서 CRUD같은 기능은 DB마다 거의 비슷하기 때문에 CRUD작업을 추상화 시켜서 개발자에게 제공해주는 기술입니다.  
Spring Data는 각 데이터베이스에 대한 일관된 추상화 계층을 제공하고 개발자는 CRUD를 빠르게 작성하고 변경할 수 있습니다.  
Spring과의 향상된 통합과 애플리케이션과의 호환성이 좋고 Spring Boot와 함께 사용하면 애플리케이션 설정을 간소화할 수 있는 장점이 있습니다.  

Spring Data 모듈의 주요 기능  
\- CRUD + 쿼리  
\- 동일한 인터페이스  
\- 페이징 처리  
\- 메서드 이름으로 쿼리 생성  
\- 스프링 MVC에서 id값만 넘겨도 도메인 클래스로 바인딩  

주요 모듈의 종류로는 Spring Data + JPA, JDBC, REST, Redis 등등이 있다.  

## 다른 데이터 접근 기술과 비교

스프링 데이터 JPA를 사용하면 코드가 얼마나 간소화되는지 알아보자!!   

- JDBCTemplate
    ```java
    public Member findOne(Long id){
        String sql = "select MEMBER_ID as id, USERNAME, PHONE_NUMBER from MEMBER where id = ?"; 
        Member member = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Member>(), id);
        return member;
    }
    ```

- JPA
    ```java
    @Repository
    @RequiredArgsConstructor
    public class MemberRepository{
        
        private final EntityManager em;

        public Long save(Memeber memeber){
            em.persist(member);
            return member.getId();
        }
    }
    ```

- Spring Data JPA
    ```java
    public interface MemberRepository extends JpaRepository<Member,Long>{ 
        //인터페이스만 정의
    }
    ```
JDBCTemplate, JPA는 직접 데이터베이스에 엑세스하는 코드를 작성해야 하지만,  
스프링 데이터 JPA는 인터페이스만 정의하면 JpaRepsository가 제공하는 인터페이스를 통해 CRUD기능 등을 가진 프록시객체가 생성되어 손쉬운 데이터 엑세스가 가능하다!

## 스프링 데이터 JPA 주요 기능  

스프링 데이터 JPA는 JPA를 편리하게 사용할 수 있도록 도와주는 라이브러리이다.  
수많은 편리한 기능을 제공하지만 가장 대표적인 기능은 다음과 같다.  

\- 쿼리 메서드 기능  
\- 공통 인터페이스 기능  

### 공통 인터페이스

![image](https://github.com/9ony/9ony/assets/97019540/d72d7f69-ecd5-47a4-9f96-5e38e61e13a5)

\- JpaRepository 인터페이스를 통해서 기본적인 CRUD기능 제공한다.  
\- 공통화 가능한 기능이 거의 모두 포함되어 있다.  

- 제네릭 타입  
    T: 엔티티  
    ID: 식별자 타입  
    S: 엔티티와 그 자식 타입  

- 주요 메서드  

    save(S): 새로운 엔티티는 저장하고 이미 있는 엔티티는 병합  

    delete(T): 엔티티 하나를 삭제한다. 내부적으로 em.remove()를 호출  

    findById(ID): 엔티티 하나를 조회한다. 내부적으로 em.find()를 호출  

    getOne(ID): 엔티티를 프록시로 조회한다. 내부적으로 em.getReference()를 호출  
    (Reference는 캐시값까지만 조회한다.)  

    findAll(...): 모든 엔티티를 조회한다. 정렬(Sort)이나 페이징(Pageable) 조건을 파라미터로 설정 가능하다.    

### 스프링 데이터 JPA가 구현 클래스를 생성

![image](https://github.com/9ony/9ony/assets/97019540/e5d9a6f0-3ba2-4440-9444-bcb4317f96d8)

JpaRepository인터페이스를 상속받으면 스프링 데이터 JPA가 프록시 기술로 구현 클래스를 만들어준다.  
그리고 생성된 구현 클래스의 인스턴스를 만들어서 스프링 빈으로 등록한다.  
구현 클래스없이 인터페이스만 만들면 기본적인 CRUD기능을 사용할 수 있다.  

### 쿼리메소드 기능

메서드 이름을 분석해서 쿼리를 자동으로 만들고 실행해주는 기능을 제공한다.

- JPA
    ```java
    public List<Member> findByUsernameAndAge(String username, int age) {
        return em.createQuery("select u from Member m where m.username = :username and m.age = :age")
        .setParameter("username", username)
        .setParameter("age", age)
        .getResultList();
    }
    ```
    
    해당 JPQL 쿼리를 스프링 데이터 JPA를 통해 메서드명만 작성하면 자동으로 쿼리를 작성해준다.  

- 스프링 데이터 JPA
    ```java
    public interface MemberRepository extends JpaRepository<Member, Long> {

        List<User> findByUsernameAndAge(String username, int age);
    }
    ```

    위 JPA 작성한 쿼리와 동일한 기능을 가진다.  

쿼리메서드 기능은 메서드이름 만으로 쿼리를 자동으로 생성해준다.  
단, 규칙이 존재하므로 아래 공식문서를 통해 확인하자.  

[Spring Data JPA 쿼리 메서드](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)  
[Spring Data JPA 페이징 관련 메서드](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.limit-query-result)

### @Query

`@Query`를 이용하여 JPQL을 직접 사용할 수도 있따.  

```java
    public interface MemberRepository extends JpaRepository<Member, Long> {

        List<User> findByUsernameAndAge(String username, Integer age);

        @Query("select i from Member m where m.username like :username and m.age <= :age")
        List<Item> findMembers(@Param("username") String username, @Param("age") Integer age);
    }
```

findMembers 메서드처럼 JPQL을 사용하고 싶을 때는 @Query와 함께 JPQL을 작성 (메서드 이름으로 실행하는 규칙은 무시)  
스프링 데이터 JPA는 네이티브 쿼리 기능도 지원하는데, SQL도 직접 작성할 수 있다.  

## Spring Data JPA 적용 예제

우선 gradle에 JPA라이브러리를 추가해주자.   

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
```

### 인터페이스 작성

```java
public interface SpringDataJpaItemRepository extends JpaRepository<Item,Long> {
    //이름으로 상품 조회
    List<Item> findByItemNameLike(String itemName);
    //조회한 설정 가격보다 낮은 상품 조회
    List<Item> findByPriceLessThanEqual(Integer price);
    //이름 + 설정 가격 이하 상품 조회
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);
    //쿼리 직접 실행 (findByItemNameLikeAndPriceLessThanEqual와 같은 기능)
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);
}
```

이렇게 인터페이스에 JpaRepository<T,ID>를 상속받아서 메서드명 or @Query로 직접 쿼리를 작성해주기만 하면 구현체는 Spring Data JPA가 프록시 기술로 만들어주기 때문에 간편하게 끝낼 수 있다.  
그리고 빈 등록도 자동으로 해준다. ( @Repsository 등의 어노테이션 생략가능 )
save,findById,findAll 등의 기본적인 메서드는 제공하므로 작성안해도 된다.  

__@Query 참고__  
@Query를 사용할 때 메서드에 들어가는 파라미터에 `@Param` 어노테이션을 반드시 붙여주어야 한다.  
즉, @Query 사용 시 @Param으로 명시적으로 파라미터 바인딩을 해주어야 한다.  
@Param은 Spring Data의 Param이다. (`org.springframework.data.repository.query.Param`)
@Param을 안넣어주게 되면 자바 빌드환경에 따라 다를 수 있겠지만 안 될수도 있기 때문에 반드시 넣어주자.  


### JpaItemRepositoryV2.class

- ItemService.class

    ```java
    @Service
    @RequiredArgsConstructor
    public class ItemServiceV1 implements ItemService {

        private final ItemRepository itemRepository;
        private final SpringDataJpaItemRepository springDataJpaItemRepository;
        //save 등의 서비스 로직...
    }
    ```

    이렇게 서비스 계층에 바로 SpringDataJpaItemRepository를 주입받아서 써도 될것 같지만, 이렇게 되면 문제점이 있다.  

    __문제점__  
    \- 기존의 서비스계층 코드를 변경해야 함  
    \- SpringDataJpaItemRepository를 ItemRepository를 주입받은게 아니기 때문에 Config도 설정을 바꿔줘야함.  
    \- 서비스 계층을 분리한 의도는 순수한 자바로직으로 유지하기 위함인데 해당 의도에서 벗어남.  
    \- 추후 유지보수하기도 불편해짐.  

    즉, 위의 문제점을 해결하기 위해 ItemRepository를 상속받은 JpaItemRepositoryV2를 만들어서 해당 구현체에서 SpringDataJpaItemRepository을 사용하면 해결됨.  

- JpaItemRepositoryV2.class
    
    > @Transcational을 잊지말자 JPA 기능는 트랜잭션 단위에서 실행한다!!  
    Service 레이어에 @Transactional을 해놨다면 상관없다.  

    ```java
    @Repository
    @RequiredArgsConstructor
    @Transactional
    public class JpaItemRepositoryV2 implements ItemRepository {

        private final SpringDataJpaItemRepository repository;

        @Override
        public Item save(Item item) {
            return repository.save(item);
            //JpaRepository -> CrudRepository에서 제공되는 메서드  
            //내부적으로 persist()를 사용한다.  
        }

        @Override
        public void update(Long itemId, ItemUpdateDto updateParam) {
            Item item = repository.findById(itemId).orElseThrow();
            //orElseThrow : 없을 경우 NoSuchElementException("No value present") 발생
            item.setItemName(updateParam.getItemName());
            item.setPrice(updateParam.getPrice());
            item.setQuantity(updateParam.getQuantity());
        }

        @Override
        public Optional<Item> findById(Long id) {
            return repository.findById(id);
        }

        @Override
        public List<Item> findAll(ItemSearchCond cond) {
            String itemName = cond.getItemName();
            Integer maxPrice = cond.getMaxPrice();
            if (StringUtils.hasText(itemName) && maxPrice != null) {
                //return repository.findByItemNameLikeAndPriceLessThanEqual("%" + itemName +"%", maxPrice);
                return repository.findItems("%" + itemName + "%", maxPrice);
            } else if (StringUtils.hasText(itemName)) {
                return repository.findByItemNameLike("%" + itemName + "%");
            } else if (maxPrice != null) {
                return repository.findByPriceLessThanEqual(maxPrice);
            } else {
                return repository.findAll();
            }
        }
    }
    ```
    __JpaItemRepositoryV2 메서드 설명__  

    \- save  
    JpaRepository 상위에 CrudRepository에서 save메서드를 지원함.  
    해당 메서드로 item 저장  

    \- update  
    findById 메서드를 통해 item을 조회하여 Jpa에서 수정하듯이 영속성 컨텍스트에서 관리하니까 set으로 item을 수정한다.  

    \- findById  
    JpaRepository에서 제공하는 메서드findById도 결국 내부적으로 앞에서 사용했던 em.find(Item.class,itemId)를 사용한다.  

    \- findAll  
    각 조건만다 분기를 둬서 해당 조건에 맞는 SpringDataRepository의 메서드를 호출했다.  
    
    > 이름+가격 검색, 이름검색, 가격검색, 전체상품 검색으로 각각의 메서드를 조건에 맞으면 호출(비효율적)  

    스프링 데이터 JPA는 동적쿼리 부분이 지원을 거의 안해주는데, 이러한 부분은 QueryDSL로 해결이 가능하다.  

### ItemServiceApplication 및 Config 설정 후 테스트  

- 설정 코드 생략  
    SpringDataJpaConfig.class를 생성하여 itemRepository 빈의 구현체를 JpaItemRepositoryV2로 설정해주자.  
    그리고 Application에 @import로 적용시켜주자.  

- 테스트 및 애플리케이션 실행 결과   
    __테스트 결과__  

    ![image](https://github.com/9ony/9ony/assets/97019540/702a97b0-c22e-44c4-9b64-7bb5f544f398)

    __애플리케이션 작동확인__  
    
    ![image](https://github.com/9ony/9ony/assets/97019540/9ac4da84-af00-431f-8495-db470a2662d2)

    정상적으로 작동한다.  

## 정리
스프링 데이터 JPA의 일부 주요 기능들을 예제를 통해 학습해보았다.  
예제 이 외에도 정말 수 많은 편리한 기능을 제공하고 페이징기능도 제공한다.    
코드를 통해 똑같은 코드로 중복 개발하는 부분을 개선해주는 것을 확인할 수 있었다.  
이렇게 편리한 기능을 이용하기 위해서는 결국 JPA를 정확히 알아야 나중에 오류등이 일어났을때 분석할 수 있고 성능개선도 어떻게 해야할지 알 수 있을것 같다.  