# QueryDSL

QueryDSL에 대해 간략하게 알아보자.  

## QueryDSL이란??  

QueryDSL은 SQL, JPQL 등의 쿼리 언어를 자바 코드로 작성할 수 있도록 도와주는 라이브러리이다.  
애플리케이션 단에서 문자열로 SQL or JPQL을 작성한 후 해당 쿼리를 날리면 아래와 같은 문제점이 있다.  

### 기존 SQL 및 JPQL 문제점  

\- 런타임 시 오류 발생(쿼리 작성 과정에서 오타나 잘못된 필드 또는 엔티티 참조로 인한 오류) 
\- SQL, JPQL은 문자, Type-check 불가능 
\- 문자열 작성으로 인해 가독성이 안좋다.  
\- JPQL로는 동적쿼리나 복잡한 쿼리 작성이 복잡하다. (ORM , SQLMapper 기술)

이 외에 다른 문제점이 많겠지만 QueryDSL을 이용하면 위와 같은 단점들이 보완된다.   

- JPQL
    ```java
    TypedQuery<Member> jpqlQuery = entityManager.createQuery(
    "SELECT m FROM Member m WHERE m.age > :age AND m.team.name = :teamName",Member.class)
    .setParameter("age",25)
    .setParameter("teamName","TeamA")
    .getResultList();
    ```

- Criteria API
    ```java
    //Criteria 쿼리를 생성하기 위한 빌더객체 생성
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    //쿼리의 반환 타입(Member.class)을 설정하는데 사용
    CriteriaQuery<Member> query = builder.createQuery(Member.class);
    // 엔티티 클래스의 루트를 지정 후 이를 기반으로 쿼리를 작성
    Root<Member> root = query.from(Member.class);

    // Predicate: 쿼리의 조건을 지정하기 위한 객체 (여러 조건을 조합가능)
    //age는 25이상 , teamName은 TeamA 지정
    Predicate agePredicate = builder.gt(root.get("age"), 25);
    Predicate teamNamePredicate = builder.equal(root.get("team").get("name"), "TeamA");

    //쿼리에 조건을 적용 ( 두 개의 조건을 AND 연산자로 결합 )  
    query.where(builder.and(agePredicate, teamNamePredicate));
    //타입이 지정된 쿼리를 생성
    TypedQuery<Member> typedQuery = entityManager.createQuery(query);
    //쿼리를 실행하고 결과를 리스트로 반환
    List<Member> result = typedQuery.getResultList();
    ```

- MetaModel Criteria API
    ```java
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Member> query = builder.createQuery(Member.class);
    Root<Member> root = query.from(Member.class);
    //Member_.age : Member.class의 age필드에 25값을 지정
    Predicate agePredicate = builder.gt(root.get(Member_.age), 25);
    Predicate teamNamePredicate = builder.equal(root.get(Member_.team).get(Team_.name), "TeamA");

    query.where(builder.and(agePredicate, teamNamePredicate));

    TypedQuery<Member> typedQuery = entityManager.createQuery(query);
    List<Member> result = typedQuery.getResultList();
    ```

참고로 JPA에서 지원하는 Criteria API와 MetaModel Criteria API는 JPQL을 자바코드로 작성해주는 라이브러리이다.  
해당 라이브러리로 위의 문제점인 타입 안정성이 보장되지만, Criteria API를 쓰더라도 문자열을 사용할 수 밖에 없는데  
이 문제를 MetaModel Criteria API사용해 해결 가능하다.  
단, 사용하기 너무 복잡하고 가독성도 좋지 않아서 별로 사용되지 않는다. 

### QueryDSL 장점  

\- 문자가 아닌 코드로 작성  
\- 컴파일 시점에 문법 오류 발견  
\- 코드 자동완성(IDE 도움)  
\- 단순하고 쉬움: 코드 모양이 JPQL과 거의 비슷  
\- 동적 쿼리  
\- 다양한 데이터베이스 지원 및 변경 최소화  

## QueryDSL 예제 작성

QueryDSL을 사용해서 객체지향쿼리를 작성해 보자.

### QueryDSL 의존성 추가

```gradle
//Querydsl 라이브러리 추가
implementation 'com.querydsl:querydsl-jpa'
//APT(annotation processing toll)로 querydsl-apt가 @Entity가 붙은 클래스들을 Q클래스(검증용)을 생성해준다.  
annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jpa"
annotationProcessor "jakarta.annotation:jakarta.annotation-api"
annotationProcessor "jakarta.persistence:jakarta.persistence-api"

//QueryDSL 사용 시 자동생성된 메타데이터(Q클래스)를 gradle clean으로 제거
clean {
delete file('src/main/generated')
}
```

### 메타데이터 Q클래스 생성

- 빌드툴 별 메타데이터 생성 설정 (Gradle, IntelliJ)

    ![image](https://github.com/9ony/9ony/assets/97019540/f1d360d3-c7c1-4039-9913-54323cbd76e8)

    - Gradle Q클래스 생성  
        gradle -> Task -> build -> clean 후 gradle -> Task -> other -> complieJava 

        ![image](https://github.com/9ony/9ony/assets/97019540/bc28e0a4-842f-4a8f-8c24-629f2eeabbc6)
         
    - 생성된 Q클래스 확인  

        build/generated/sources/annotaionProcessor/프로젝트경로    
        
        ![image](https://github.com/9ony/9ony/assets/97019540/7f2b9054-049a-4f25-9003-368343bcbf74)
    
    - IntelliJ Q클래스 생성  

        IntelliJ 탭에서 Build -> `Build Project` or `Rebuild Project` or `애플리케이션 실행(main()) or 테스트실행`

        ![image](https://github.com/9ony/9ony/assets/97019540/d96474e0-2adb-49e5-98f6-acf35e374c36)


    - 생성된 Q클래스 확인

        ![image](https://github.com/9ony/9ony/assets/97019540/91bf8d46-b6a5-4bf0-88a4-243c7b0b71b1)


### JpaItemRepositoryV3.class

JpaItemRepositoryV3를 생성하여 QueryDSL을 사용하여 동적쿼리를 작성해보자.  
save,update,findById는 기존과 동일하게 사용

```java
@Repository
@Transactional
public class JpaItemRepositoryV3 implements ItemRepository {

    private final SpringDataJpaItemRepository repository;
    private final JPAQueryFactory query;

    public JpaItemRepositoryV3(SpringDataJpaItemRepository repository,EntityManager em){
        this.repository = repository;
        //JPAQueryFactory 생성 시 EntityManager가 필요
        this.query = new JPAQueryFactory(em);
    }
    //...save,update,findbyid 생략
    
    //이전 JPQL로 작성한 findAll()코드와 비교해보자.  
    public List<Item> findAllOld(ItemSearchCond itemSearch) {
        String itemName = itemSearch.getItemName();
        Integer maxPrice = itemSearch.getMaxPrice();
        QItem item = QItem.item;
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(itemName)) {
            builder.and(item.itemName.like("%" + itemName + "%"));
        }
        if (maxPrice != null) {
            builder.and(item.price.loe(maxPrice));
        }
        List<Item> result = query
                .select(item)
                .from(item)
                .where(builder)
                .fetch();
        return result;
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();
        List<Item> result = query
                .select(item)
                .from(item)
                //.where(likeItemName(itemName).and(maxPrice(maxPrice))
                .where(likeItemName(itemName), maxPrice(maxPrice))
                .fetch();
        return result;
    }

    private BooleanExpression likeItemName(String itemName) {
        if (StringUtils.hasText(itemName)) {
            return item.itemName.like("%" + itemName + "%");
        }
        return null;
    }

    private BooleanExpression maxPrice(Integer maxPrice) {
        if (maxPrice != null) {
            return item.price.loe(maxPrice);
        }
        return null;
    }
```

SpringDataJpaItemRepository,JPAQueryFactory을 주입 해주었다.  

save,update,findById 메서드는 기존 Spring Data JPA를 사용한 것과 동일하다.  
JPAQueryFactory는 JPQL을 사용하기 때문에 생성 시 EntityManager가 필요하다.  

- findAllOld
    Querydsl을 사용해서 동적 쿼리 문제를 해결했다.  
    BooleanBuilder를 사용해서 원하는 where조건들을 넣어주고,
    자바 코드로 작성하기 때문에 동적 쿼리를 매우 편리하게 작성할 수 있다.  

- findAll
    BooleanBuilder를 사용하지 않고 where()에 들어갈 조건을 함수로 만들었다.  
    해당 조건을 재사용이 가능해졌다. 이는 자바 코드로 쿼리를 작성하기 때문에 얻을 수 있는 장점이다.  
    .where()절 조건에 `,`로 조건을 구분하게되면 and조건으로 들어간다.(주석과 같음)  

- item.itemName.like("%" + itemName + "%")
    item_name like %파라미터값% 쿼리를 생성  
    반환타입은 BooleanExpression  

- item.price.loe(maxPrice)  
    price < maxPrice 쿼리를 생성  
    반환타입은 BooleanExpression  

- where()
    JpqQueryFactory를 통해 쿼리를 생성 시 조건을 입력하는 메서드  
    해당 파라미터에 null이 들어가면 해당 조건은 무시된다.  
    ex) .where(likeItemName(itemName), maxPrice(maxPrice))  
    위 where 절에서 likeItemName이 null이 반환된다면 maxPrice조건만 적용됨.  
    둘다 null이면 쿼리에 where절 자체를 추가 안함  

 
### QueryDslConfig.class

```java
@Configuration
@RequiredArgsConstructor
public class QueryDslConfig {

    private final EntityManager em;
    private final SpringDataJpaItemRepository springDataJpaItemRepository;

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV3(springDataJpaItemRepository,em);
    }
}

```

JpaItemRepositoryV3는 생성자로 SpringDataJpaItemRepository와 EntityManager가 필요하므로 파라미터에 추가해주자.  
JPAQueryFactory 생성 시 EntityManager가 필요하므로 em을 파라미터로 넘겨준다.  

### QueryDSL의 목적  

예제에서 봣듯이 일반적인 CURD는 JPA를 사용하거나 Spring Data JPA를 사용해서 처리하고,  
복잡한 동적쿼리를 QueryDSL을 사용하였다.  
QueryDSL은 아래와 같은 구조를 가진다.  

![image](https://github.com/9ony/9ony/assets/97019540/c69372b1-a811-4eb8-bcbe-162773cd4732)

이렇게 QueryDSL의 사용 목적은 JPQL을 자바코드로 작성하기 위함이고 이는 타입안전성과 컴파일이전에 쿼리오류를 알 수 있는 등 많은 장점을 가진다.  
또 JPQL로 복잡한 쿼리를 작성할 때 코드가 매우 길어지는데, QueryDSL이 다양한 기능을 지원하기 때문에 해당 부분도 완화된다.  

## 정리
Querydsl을 사용해서 자바 코드로 쿼리를 작성해보았다.  
동적 쿼리 문제도 해결하고, @Entity가 붙은 클래스를 메타모델인 Q클래스를 생성한다.  
이 메타모델을 활용하여 쿼리작성 시 타입 안전성도 보장하며 관계를 활용하여 조인 쿼리도 작성한다.  
Querydsl은 이 외에도 DTO로 편리하게 조회할 수 있는 기능 등 수 많은 편리한 기능을 제공한다.  
JPA를 사용한다면 스프링 데이터 JPA와 QueryDSL을 이용해 다양한 문제들을 편리하게 해결할 수 있다.  