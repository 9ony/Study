# 영속성 컨텍스트(Peresitence Context)

영속성 컨텍스트란?  
Entity를 저장하는 영역이라고 보면 된다.  
JPA는 일반적으로 데이터를 조회할때 영속성 컨텍스트에서 조회가 이루어진다.  
영속성 컨텍스트는 해당 트랜잭션 내(동일한 쓰레드에서만 유효)에 메모리라고 생각하면 편하다.  
영속성 컨텍스트는 EntityManager를 통해 관리되는데, 우선 엔티티 생명주기에 대해 알아보자.  

## 영속성 컨텍스트 생명주기

![image](https://github.com/9ony/9ony/assets/97019540/3372119c-1ce8-4bf6-b7af-89a1bae91d45)

- new Entity : 현재 해당 엔티티는 비영속 상태이다.  

- persist(Entity) : 파라미터로 들어온 Entity를 영속성 컨텍스트에 저장  
    만약 해당 Entity id값 설정이 `@GeneratedValue(strategy = GenerationType.IDENTITY)`이라면 insert를 보낸다.  
    왜냐하면 DB에 저장된 ID값을 Entity에도 넣어줘야 하기 때문  
    ID설정도 안하고 Identity 생성 전략도 설정안했다면 `IdentifierGenerationException` 발생  

- clear(): 영속성 컨텍스트 내 모든 엔티티를 준영속 상태로 전환한다.  
- close(): 영속성 컨텍스트 자체를 닫는다.  
- detach(Entity) : 영속성 컨텍스트 내 해당 엔티티를 `준영속 상태로 전환`한다.  

- merge(Entity) : 파라미터로 들어온 Entity값으로 병합하여 반환  
    이때 파라미터로들어간 Entity는 해당 값만 이용하기 때문에 다른 인스턴스이다.  
    기존에 영속성컨텍스트에 있던 인스턴스와는 같은 메모리주소를 가진다.  

- remove(Entity) : 해당 엔티티를 영속성 컨텍스트 및 DB에서도 삭제한다.  
    이때 DB에서 삭제한다는 말은 만약 insert를 했으면 해당 세션에는 반영되있기 때문에 해당 세션에 반영된 DB의 값을 삭제한다는 뜻이다.  
    즉, remove() 했으면 다시 persist()를 통해 생성해야함.  

- flush() : 현재 1차캐시와 메모리 내 엔티티를 비교하여 생성된 쓰기지연 SQL 저장소의 SQL을 전송하여 영속성 컨텍스트의 변경 내용을 데이터베이스에 동기화.  
❗ 단, 영속성 컨텍스트에 보관된 엔티티를 지우는 것이 아님.  

## 영속성 컨텍스트 구조

![image](https://github.com/9ony/9ony/assets/97019540/bf87aff9-8c3c-430c-a5b0-049b011df573)  

- 1차 캐시 : 엔티티가 영속성 컨텍스트에 관리될때 동안의 추적 값(임시 DB라 생각하면 될 것 같다!)   
    - 식별자 : 영속화 시 식별자가 반드시 필요하다. 영속성 컨텍스트에서 식별자를 통해 인스턴스를 구별하기 때문!!  
    - 인스턴스 : 해당 식별자에 대한 인스턴스가 저장되어 있다. 즉, 메모리에서 생성한 인스턴스와 같은 메모리 주소를 가진다.  
    - Snapshot : 엔티티가 준영속 또는 비영속 상태에서 영속성 컨텍스트에 들어왔을때의 값이 스냅샷에 저장  
- 쓰기지연 저장소 : 스냅샷에 저장된 값과 영속화 되어있는 인스턴스를 비교하여 sql구문을 생성하여 저장해둔 값  

### 그림 설명

1. item1 Entity를 생성하는데 생성시점에는 해당 Entity는 비영속 상태이다.  
    이후 해당 Entity를 엔티티매니저를 통해 persist,find 한다. (영속화)  
    item1은 애플리케이션에서 생성하여 영속화시켰고,  
    item2는 DB에서 조회한 Entity인데 조회할때 영속성 컨텍스트에 캐싱한다.  

2. 해당 Entity들이 영속화되면 영속화된 시점의 Entity정보가 `Snapshot`에 저장된다.  
    Entity들의 변경사항을 영속성 컨텍스트에서 관리하여 추적한다  

3. item2 준영속화함.  
인스턴스를 영속성 컨텍스트에서 관리를 안하는 상태를 준영속이라 한다.  
item2는 더이상 영속성 컨텍스트에서 관리를 안하기 때문에 `1차캐시`에서 해당 캐시값들을 삭제함.  
애플리케이션 메모리에는 여전히 남아있음을 인지하자.  
(만약 이때 다시 영속화하 기 위해 병합(merge)하게 된다면 해당 병합시점의 값이 스냅샷에 기록되며 다시값을 추적한다.)  

4. Item1을 애플리케이션 내에서 수정하게 되면 영속성 컨텍스트에서 관리하는 Item1에 대한 스냅샷과 비교하여 price가 변경되었기 때문에 추후 커밋 시 해당 값을 변경하는 구문을 SQL 쓰기지연 저장소에 추가하게 된다.  

5. 이제 커밋하게되면 이때 영속성 컨텍스트의 스냅샷과 캐시값들을 비교(`Dirty-Checking`)하여 쿼리를 생성 후 DB에 쿼리를 전송한다.  
    즉, 영속성 컨텍스트로 관리되기 때문에 해당 트랜잭션 내에서 최소한의 SQL을 전송하여 성능이 향상된다.  

### ✔ 준영속과 비영속의 차이  

준영속과 비영속의 차이점은 준영속은 해당 엔티티가 식별자(id)를 가지고 있는 상태이고, 
비영속은 식별자를 가지고 있을수도 있고, 없을수도 있는 상태이다.  

> ex) Item item = new Item("item1",100,1); //이상태는 비영속 상태 id가 없기때문  

이때 JPA를 사용하면 무조건 Id를 설정해주어야 함  
즉, EntituManager.persist(item); 을 하게되면 현재 item은 id가 없지만,  
Item.class객체 내에서 DB에서 생성된 id를 넣는 설정을 해주었으면 식별자가 생기면서 영속화가됨.  
그이후 detach()를 통해 영속화를 해제하면 해당 item 객체는 db에서 생성했던 id값을 가지고 있기 때문에 준영속이라 볼 수 있다.  

## EntityManager 생성 방식

스프링은 JPA사용시 EntityManagerFacotory를 빈으로 자동으로 설정하고 등록해준다.  
해당 EntityManagerFactory에서 EntityManager가 어떻게 생성되고 사용되는지,  
왜 트랜잭션을 사용중일때만 EntityManager가 사용가능한지 알아보자.  

### __EntityManager 생성 구조__  
![image](https://github.com/9ony/9ony/assets/97019540/b048f431-3dfb-4866-9b1b-acf66a50fdff)

- 간단한 설명
    1. 요청이 올때 해당 요청 ThreadLocal에 EntityManager를 생성하여 저장한다.  
    2. 해당 쓰레드 내에서는 쓰레드로컬에 있는 EntityManager를 사용한다.  
    3. 스프링에서 서비스나 리포지토리에 주입(싱글톤)된 EntityManager는 프록시 객체이다.  
    4. 해당 EntityManager는 트랜잭션 내부에서만 작동(find()와 같은 일부메서드 제외)한다.  
    5. 쓰레드마다 Proxy.EntityManager는 같은 객체이지만 결국 기능을 호출(invoke)하는 EntityManger는 쓰레드별로 생성된 EntityManager를 사용하기 때문에 Thread-Safe한 것이다.  

    > 요청 -> emf가em(EntityManager) 생성 ->  쓰레드로컬에 em 저장 -> 서비스는 (Proxy)em.persist(entity) 요청 -> 쓰레드 로컬에 em.persist(entity) 가 수행

### 트랜잭션 범위가 아닐때 EntityManager 동작

스프링에서 EntityManager를 주입하면 그림과 같이 `SharedEntityManagerCreator`가 생성하고 관리하는 EntityManager(Proxy)가 주입되어 있다.

![image](https://github.com/9ony/9ony/assets/97019540/3798d5aa-5f67-4ee9-a189-5ac6181a6817)

이제 애플리케이션에서 해당 주입받은 EntityManager의 메서드를 호출하게되면 해당 `SharedEntityManagerCreator를 거쳐서 실제 EntityManager가 호출`하게 되는데, 이 프록시객체가 메서드를 호출할때 트랜잭션 범위 안인지 검사한다.  

![image](https://github.com/9ony/9ony/assets/97019540/9dfc6363-4f85-421f-8398-1c0a809e819c)

위 코드로 트랜잭션 범위가 아닌 컨트롤러 단에 EntityManager를 사용해보았을때 어떻게 작동하는지 보겠다.  

- 트랜잭션 내부에서 EntityManager를 호출하는지 검사하는 과정

    ![image](https://github.com/9ony/9ony/assets/97019540/6e3c72fa-1e4b-4514-90eb-53066caf2366)

    위 코드는 실제 target인 EntityManager를 생성하는 메서드이다.  
    EntityManager 생성할때 아래 코드로 현재 트랜잭션이 사용되고 있는지 검사한다.  

    ![image](https://github.com/9ony/9ony/assets/97019540/11fa8504-8d7c-460d-9871-d122b863965b)

    TransactionSynchronizationManager로부터 현재 트랜잭션 범위 내에서 사용되고 있는 EntityManagerHolder를 가져오는데,  
    현재 트랜잭션이 없거나 트랜잭션과 관련이 없는 요청인 경우 null을 반환하게 된다.  

    > 이때 트랜잭션 범위 안이라면 EntityManagerHolder를 반환하고 EntityManagerHolder에 관리되고 있는 EntityManager를 통해 호출한 메서드를 수행하게 된다.  
    EntityManagerHolder는 TransactionSynchronizationManager을 이용하여 쓰레드로컬에서 관리하는 EntityManager를 가지고 올 수 있다.    
    즉 EntityManagerHolder는 해당 쓰레드 로컬에서 공유되는 EntityManager를 관리하고 있다.  

- 트랜잭션 필수인 메서드 검사

    ![image](https://github.com/9ony/9ony/assets/97019540/3a2d2dcb-831b-42ee-9e53-54ef99fdfe09)  

    __메서드 목록__  
    ![image](https://github.com/9ony/9ony/assets/97019540/60b2cc94-9c42-4f81-9389-ae4cfbf2999e)

    추가적으로 해당 메서드가 트랜잭션이 필수적인 EntityManger의 메서드인지 확인하는데, persist()는 트랜잭션이 필요한 메서드 이므로 TrasactionRequeiredException이 발생하는 것이다.  

    만약 이때 트랜잭션이 필요한 메서드가 아닐 경우 target에 들어갈 `엔티티매니저를 새로 생성`해서 해당 메서드를 수행후 close()를 통해 리소스를 제거하는 동작을 취한다.  

## EntityManager 테스트 

참고로 테스트 클래스에 @Transactional이 붙어져있다.  

### persist()

```java
@Test
@DisplayName("Item.class identity 설정 테스트")
void persist(){
    Item item = new Item("idenX",1000,1);
    //item.setId(1L);
    em.persist(item);
    //id 설정 안해주면 IdentifierGenerationException 발생
    //Identity 전략일때 setId로 해주면 충돌 PersistentObjectException
    //자동생성한 id값에 대한 식별자가 이미 DB에 있으면 중복키오류발생
}
```

- Item 식별자에 @GeneratedValue(strategy = GenerationType.IDENTITY) 설정
    insert 수행 후 DB에서 생성된 값의 식별자를 Entity에 넣고 캐싱한다.  
    자동 생성 전략일때 setId로 해주면 충돌 PersistentObjectException  
    자동생성한 id값에 대한 식별자가 이미 DB에 있으면 중복키오류발생  

- Item 식별자에 직접 생성 전략  
    id를 애플리케이션에서 직접 설정  
    ex) Item.setId(1L); 
    id 설정 안해주면 IdentifierGenerationException 발생  

    ❗ 직접 생성 전략시 주의점
    해당 식별자가 DB에는 있는데 컨텍스트 내에 캐시되지 않았을때  
    JPA특성상 persist()를 직접생성전략을 이용하면 캐시에 저장하고 DB에 바로 insert하지 않음  
    그래서 정상 수행된다.  
    추후 flush()를 할때 중복키 에러가 발생함.  

> 두설정다 선작업으로 해당 파라미터의 Entity와 같은 Entity가 영속성 컨텍스트에 있는지 확인한다.  

### merge()
```java
@Test
@DisplayName("병합")
void merge(){
    Item item = new Item("item",500,1);
    item.setId(1L);
    em.persist(item);
    Item item2 = new Item("item2",1000,10);
    item2.setId(1L);
    Item mergeItem = em.merge(item2);
    log.info("{} , {}",mergeItem == item,mergeItem == item2); //true,false
    log.info("item = {}",em.find(Item.class,1L)); //Item(id=1, itemName=item2, price=1000, quantity=10)
}
```
  
영속화된 item에 item2의 값으로 병합한다.  
기존 item과 병합하여 반환된 item은 같은 인스턴스이고 병합에 사용된 item2는 다른 인스턴스이다.  
item정보가 item2의 정보로 변경된것을 볼 수 있다.  

캐시조회 -> 해당 식별자의 엔티티를 파라미터의 엔티티로 덮어씌움  
SQL 지연쓰기 저장소에 update 쿼리 저장   


- 비영속 Entity를 merge()할때 동작  

    ```java
    @Test
    @DisplayName("비영속 병합")
    void mergeTest(){
        Item item = new Item("testItem",100,1);
        //@GeneratedValue(strategy = GenerationType.IDENTITY) 설정 상태
        item.setId(1L); //id 수동 생성
        Item detachMergeItem = em.merge(item); // OK
        log.info("detachMergeItem = {}",detachMergeItem);
        Item detachMergeItem1 = em.merge(item); // OK
        Item detachMergeItem2 = em.merge(item); // OK
        log.info("detachMergeItem == detachMergeItem1 :  {}",detachMergeItem == detachMergeItem1); //true
        log.info("detachMergeItem1 == detachMergeItem2 :  {}",detachMergeItem1 == detachMergeItem2); //false
    }
    ```

    해당 식별자의 데이터가 영속성 컨텍스트에 없는 경우  
    캐시조회 -> 없음 -> DB조회 -> 없음 -> persist(엔티티) 수행  
    SQL 지연쓰기 저장소에 insert 쿼리 저장 

    해당 식별자의 데이터가 영속성 컨텍스트에 없는데 DB엔 있는 경우  
    캐시조회 -> 없음 -> DB조회 -> 조회한 값 캐싱 -> 파라미터 엔티티 덮어씌움    
    SQL 지연쓰기 저장소에 update 쿼리 저장  

- @GeneratedValue(strategy = GenerationType.IDENTITY) 설정 상태  

    > 해당 설정에 원래 setId를 하면 안되지만 궁금해서 테스트해보았다.  

    캐시조회(setId(1L)로 설정한 id값으로 조회함) -> 없음 -> DB조회(select) -> 없음  
    -> persist(엔티티) 수행 (❗ 이때 id값은 DB에서 생성한 id값)  
    -> 저장된 Entity 캐싱 수행   
    SQL 지연쓰기 저장소에 insert 쿼리 저장  

    즉, DB나 캐시에 설정한 식별자가 있으면 그대로 merge()를 수행하고 없을 경우 persist()를 수행하게 된다.  
    이때 persist 시 indentity설정이면 id값이 db에서 생성한 값으로 변경하게 되는 것이다.    

### detach()
- detach()  
    detach() 메서드는 파라미터로 넘어온 Entity를 영속성 컨텍스트에서 분리한다.  
    (  ❗ 해당 엔티티에 관한 SQL 쓰기지연 저장소는 그대로 남아있다.  )  

    ```java
    @Test
    void defach(){
        Item item = new Item("item",1000,1);
        item.setId(1L);
        em.persist(item);
        log.info("item = {}",em.find(Item.class,item.getId()));
        //em.flush();
        em.detach(item);
        log.info("item = {}",em.find(Item.class,item.getId())); //null
    }
    ```

    persist후 detach를 통해 준영속 시키고 해당 item을 조회하면 null이 반환되는 것을 볼 수 있다.  

- __detach() 주의할점__  

    persist로 DB에도 없는 새로운 Entity를 영속화 한후에 다시 detach로 준영속 시켰다면 해당 트랜잭션은 종료하는게 좋다.  
    그이유는 하이버네이트 자체 버그때문인데, 원인은 정확하게 모르겠으나 코드를 통해 테스트 해본 결과를 적어보겠다.  
    
    __발생 에러 로그__  
    ```log
    HHH000099: an assertion failure occurred (this may indicate a bug in Hibernate, but is more likely due to unsafe use of the session): org.hibernate.AssertionFailure: possible non-threadsafe access to session
    ```

    ```java
    @DisplayName("persist후 detach 버그")
    @Commit
    void detachAndPersist(){
        Item item = new Item("item",100,1);
        item.setId(1L);
        log.info("------item persist-------");
        em.persist(item); //item 메모리값 영속성컨텍스트에 캐시저장 후 sql지연쓰기 저장
        log.info("item = {}",em.find(Item.class,item.getId())); //true
        log.info("------item detach-------");
        em.detach(item);
    }
    ```

    ![image](https://github.com/9ony/9ony/assets/97019540/b335a70c-3c85-492f-97bb-1b681786d2c5)


    새로운 item1을 영속화 시켰다.  
    그 이후에 item1을 준영속(detach)하고 테스트를 실행하면 예상대로 item이 insert되지 않는다.  
    하지만 다른 Entity를 영속화하면 버그가 발생하게 된다.  

    ```java
    @Test
    @DisplayName("persist후 detach 버그")
    @Commit
    void detachAndPersist(){
        Item item = new Item("item",100,1);
        item.setId(1L);
        log.info("------item persist-------");
        em.persist(item); //item 메모리값 영속성컨텍스트에 캐시저장 후 sql지연쓰기 저장
        log.info("item = {}",em.find(Item.class,item.getId())); //true
        //em.flush(); //identity 생성 전략처럼 바로 insert 
        log.info("------item detach-------");
        em.detach(item);
        //추가
        log.info("------newItem persist-------");
        Item newItem = new Item("newitem",500,2);
        newItem.setId(2L);
        em.persist(newItem);
        //이전 item insert문이 남아있다.
    }
    ```

    ![image](https://github.com/9ony/9ony/assets/97019540/248f5192-c653-4a7e-a473-89b332b7d3d0)

    로그를 보면 이전 Item("item",100,1)의 insert가 되는것을 볼 수 있다.  
    분명이 detach()로 준영속을 시켰는데 해당 insert쿼리가 나가고 있다.  
    이러한 문제는 만약 수동 생성 전략으로 item을 persist를 했을 경우 SQL 지연쓰기 저장소에 insert쿼리가 들어가있고, 영속성 컨텍스트에 entity가 있으면 flush()가 동작하고 `Dirty-Checking` 후 SQL 지연쓰기 저장소에 쿼리들을 전송하여 남아있던 insert 쿼리가 나가면서 생기는 버그인것 같다.  
    이때 영속성컨텍스트에는 item이 없는데 insert쿼리가 나가면서 세션과 영속성 컨텍스트에 동기화가 되지 않아 버그가 발생하는것 같다.  

    > 수동으로 식별자를 생성해서 persist를 했을때 detach하는 경우는 거의 없고 해당 Entity의 추적을 없애려면 그냥 remove하자.  
    \+ 반대로 remove로 인한 delete쿼리도 위 상황과 동일하며 에러가 발생한다.  

    ✔ detach로 영속성 컨텍스트에서 관리를 해제할때는 `조회한 Entity`의 대상으로 사용하자.  
    - 요약
        1. persist(수동 식별자 설정 조건 시)와 remove는 즉시 SQL 지연쓰기 저장소에 insert or delete 쿼리가 반영된다.  
            (세션에 적용X, 지연쓰기에 쿼리를 등록하는 것)  

        2. Dirty-Checking 시 영속성 컨텍스트에 Entity가 있다면 변경여부 확인 후 수정쿼리 작성 + SQL 저장소에 생성된 SQL 실행  
            이때 persist(or remove)로 저장된 insert 쿼리가 전송된다.  

        3. 쓰기지연 저장소 쿼리가 전송됬을때 세션의 상태와 영속성 컨텍스트가 다르다면 버그발생(flush는 동기화하는 목적이기 때문에 버그)  
            그래서 persist나 remove는 detach하기전에 flush()로 미리 세션에 반영하면 버그발생을 하지않음  

    indentity 전략 사용 시에는 세션에 바로 insert되므로 바로 동기화가 되어 상관없다.  
    다른 생성 전략일경우에는 어떤 동작방식인지 아직은 모르겠으나 수동생성전략처럼 insert문을 SQL 쓰기지연 저장소에 반영하는 경우 모두 위와 같은 상황에 주의해야한다.  

    - __에러 예방법__

        detach 후 merge하여 준영속된 엔티티를 다시 영속화 시킬 때 이전에 해당 엔티티를 persist나 remove 했다면 detach() 전에 적절하게 clear() or flush()를 해주자!

        detach는 persist나 remove한 Entity는 사용을 지양하고 DB에서 조회한 Entity를 대상으로 사용하자!

### clear()

- clear() 

    clear()는 영속성 컨텍스트를 초기화시킨다.  

    ```java
    @Test
    void clear(){
        Item item = new Item("item",1000,1);
        item.setId(1L);
        em.persist(item);
        log.info("item = {}",em.find(Item.class,item.getId()));
        //em.flush();
        em.clear();
        log.info("item = {}",em.find(Item.class,item.getId())); //null

    }
    ```
    
### close()  

- close()
    ```java
    @Test
    void close(){
        Item item = new Item("item",1000,1);
        item.setId(1L);
        em.persist(item);
        log.info("item = {}",em.find(Item.class,item.getId()));
        em.close();
        //스프링에서 관리하는 EntityManager는 close()시 아무 동작도 하지않는다.  
        //이는 SharedEntityManagerCreator 코드를 보면 알 수 있다.  
        //실제 엔티티매니저가 리소스를 정리하는 close()는 트랜잭션이 종료하는 시점에 작동하게 된다.  
        //즉, 스프링의 트랜잭션매니저가 리소스를 처리해주는것
        log.info("em2 item = {}",em.find(Item.class,item.getId())); //조회됨  
    }

    @Test
    void close2(){
        //ThreadLocal에서 관리하는 em이 아닌 새로운 EntityManager를 생성해서 테스트!
        EntityManager newEm = emf.createEntityManager();
        Item item = new Item("item",1000,1);
        item.setId(1L);
        newEm.persist(item);
        log.info("item = {}",newEm.find(Item.class,item.getId()));
        newEm.close();
        //newEm = emf.createEntityManager();
        log.info("item = {}",newEm.find(Item.class,item.getId())); //Session/EntityManager is closed
    }
    ```

    스프링에서 관리하는 EntityManager는 close()시 아무 동작도 하지않는다.  
    이는 위에서 설명했듯이 SharedEntityManagerCreator 프록시 객체의 코드를 보면 알 수 있다.  
    실제 엔티티매니저가 리소스를 정리하는 close()는 트랜잭션이 종료하는 시점에 작동하게 된다.  
    즉, 스프링의 트랜잭션매니저(`JpaTransactionManager`)가 리소스를 처리(`EntityManager.close()`)해주는것  

    close2()에서는 직접 EntityManagerFactory를 통해 EntityManager를 생성하여 테스트했다.  
    이렇게 하면 close() 호출 시 해당 EntityManager는 `ExtendedEntityManagerCreator` 프록시 객체를 통해 close()가 호출되는데 이때는 해당 EntityManager의 리소스를 바로 정리한다.  
    그래서 `//newEm = emf.createEntityManager();을 주석`을 해놓으면 영속성컨텍스트가 닫혀있기 때문에 에러가 발생한다.  

__❗ 참고__  

해당 `close2()` 예제에서는 새로운 EntityManager를 생성할때 ExtendedEntityManagerCreator 프록시 객체를 통해 호출된다고 했었다. 이 객체는 스프링 기반의 트랜잭션 범위 외에 다른 트랜잭션에서 사용할때 사용되는것 같다.  
그리고 트랜잭션 내부가 아니라도 DB접근이 필요하지 않은 경우라면 영속성 컨텍스트에 등록(persist), 삭제(remove), 준영속화(detach) 등의 메서드가 사용가능하다.  
즉, 다른 영속성 컨텍스트를 만들어서 엔티티를 관리할때 주로 사용되는 객체인것같다.  
( 정확하지 않은 정보입니다. 추후 틀리다면 수정하겠습니다. )

### remove

```java
@Test
@Commit
void remove(){
    em.remove(em.find(Item.class,1L));
}
```
remove는 해당 Entity를 삭제하는 메서드이다.  
코드에서 보다싶이 삭제할 Entity를 파라미터로 넘겨주어야 한다.  
SQL 쓰기 저장소에 delete SQL이 저장된다.  

### find

find는 우선 영속성 컨텍스트에서 먼저 조회한 후 없다면 DB에 접근하여 조회한다.  
이때 아래예시 처럼 조회할 Entity 타입과 id값을 파라미터로 설정해야 한다.  

```java
@Test
void find(){
    Item item = em.find(Item.class,1L);
}
```

find()시 DB를 통해 Item Entity를 조회할 경우 영속성 컨텍스트에 해당 Entity를 영속화한다.  