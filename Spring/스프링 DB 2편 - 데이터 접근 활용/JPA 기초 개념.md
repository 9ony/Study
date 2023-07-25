# JPA 기초

## JPA(Java Persistence API)란?
[JPA 사용 이유](링크)는 해당 글에서 서술했듯이 SQL에 의존적인 개발에 대한 문제점과 패러다임 차이, 그리고 기존 ORM 기술(EJB 등..)의 성능적인 부분을 개선하기 위한 기술입니다.  
JPA란 자바 진영에서 ORM(Object-Relational Mapping) 기술 표준으로 사용되는 `인터페이스의 모음`!!    
JPA를 구현한 `대표적인 오픈소스로는 Hibernate`가 있다.  
(이외에 EclipceLink,DataNucleus 등이 있다)  

JPA는 스프링 만큼이나 방대하고, 학습해야할 분량도 많다.  
하지만 데이터 접근 기술에서 매우 큰 생산성 향상을 얻을 수 있다.  
JPA를 사용하면 SQL도 JPA가 대신 작성하고 처리해준다.  
실무에서는 JPA를 더욱 편리하게 사용하기 위해 스프링 데이터 JPA와 Querydsl이라는 기술을 함께 사용한다.  
Querydsl은 JPA를 편리하게 사용하도록 도와주는 도구라 생각하면 된다.   

### ORM의 역할

- Object-relational mapping(객체 관계 매핑)  
- 객체는 객체대로 관계형 데이터베이스는 관계형 데이터베이스대로 설계  
- ORM 프레임워크가 중간에서 매핑처리(패러다임 불일치를 해결해줌)   
- 대중적인 언어에는 대부분 ORM 기술이 존재  

### JPA 동작 과정

![image](https://github.com/9ony/9ony/assets/97019540/b656c089-fcf7-421a-ba60-34eb55a70ac1)

애플리케이션에서 JPA를 쓰면 JPA가 객체를 데이터베이스에 맞게 매핑작업 후 JDBC를 통해 SQL 쿼리를 전송하는 것이다.  
반대로 결과를 받는것도 해당 결과를 객체에 맞게 JPA가 반환해 준다.  

- 저장
    ![image](https://github.com/9ony/9ony/assets/97019540/3b116c98-0ba8-4355-8566-990c4a506205)

    1.애플리케이션에서 객체를 JPA 넘김(Persist)  
    2.객체 분석후 SQL 생성  
    3.JDBC를 사용해서 쿼리 전송  

- 조회
    ![image](https://github.com/9ony/9ony/assets/97019540/9e21c492-14a1-4052-b5eb-31a551f767bd)

    1.memberDAO.find(id) 메서드 호출
    2.Select 쿼리 생성 후 전송(jdbc사용)
    3.ResultSet 매핑 등.. 작업 후 Member객체 반환

애플리케이션 객체와 DB에 저장과 조회과정에서 `패러다임 불일치를 해결`해준다.

## JPA 장점

### 생산성 및 유지보수

- CRUD
    \- 저장: jpa.persist(member)  
    \- 조회: Member member = jpa.find(memberId)  
    \- 수정: member.setName("변경할 이름")  
    \- 삭제: jpa.remove(member)  

- 유지보수

    ```java
    public class Member{
        long id;
        String name;
        int age;
        String job;
        String phoneNumber; //추가
    }
    ```
    위와 같은 회원 객체에 phoneNumber를 추가하게 되면?  
    ```sql
    insert into Member(id,'name',age,'job') values(id,'name',age,'job');

    select id,'name',age,'job' from Member where id= 조회할id;

    delete from member where id = 삭제할id;
    ```
    원래는 Member에 관련된 쿼리에 컬럼추가 or 값을 추가해주어야 하는데

    > JPA는 쿼리를 객체에 필드에 따라 처리해주기 때문에 수정할 필요가 없다!!

## 패러다임의 불일치 해결

### 상속문제

[이 전글]()에서 확인했듯이 패러다임 불일치에 원인으로 객체의 상속과 RDB의 슈퍼타입 서브타입의 관계에는 차이가 있었다.  

![image](https://github.com/9ony/9ony/assets/97019540/e3a917c2-e99b-4617-bb92-947cf933d254)

위와 같은 구조에서 TOP에 데이터를 넣거나 조회할때 객체를 따로 생성해서 insert쿼리를 날려주고 SQL조인문을 작성 후 복잡한 과정을 통해서 객체를 조회하는 등..  

이러한 문제를 JPA를 사용하면 컬렉션에 저장된 객체를 꺼내듯이 사용가능하다.  
```java
//저장
jpa.persist(top);
//조회
Top top = jpa.find(Top.class, cid);
```

### 연관관계 설정

![image](https://github.com/9ony/9ony/assets/97019540/46eda072-e00a-4f3e-bbca-e49295e82df0)

- 연관관계 저장
    ```java
    member.setTeam(team);
    jpa.persist(member);
    ```
- 객체 그래프 탐색
    ```java
    Member member = jpa.find(Member.class, memberId);
    Team team = member.getTeam();
    ```

이렇게 RDB에서 조회한 데이터를 컬렉션에서 꺼내듯이 조회할 수 있기 때문에 해당 개체에 대한 신뢰성을 보장한다.  

```java
Member member = memberDAO.find(memberId);
member.getTeam(); //Team을 가져옴
member.getOrder().getDelivery(); 
```

> JPA를 쓰지 않았을 때는 아래 로직에서 Team과 Order에대한 데이터가 있는지 작성한 SQL쿼리를 확인해 봐야함.  

## JPA의 성능 최적화 기능

JPA를 통해 어떤 성능 최적화 기능이 있는지 간단하게 알아보자.  

> 아래 최적화 기능들을 자세하게 알아보기 위해 `영속성 컨텍스트`에 관련된 자료를 참고하자!  

### 캐싱

1. JPA에서 같은 트랜잭션 안에서는 같은 Entity를 반환한다.  
    즉 캐시를 통해 애플리케이션에서 member 테이블에서 id가 1번인 개체를 조회할 때, 해당 개체의 조회가 여러번 일어나도 캐시에서 반환하기 때문에 빠르고 DB접속도 할필요가 없어진다.  

2. DB에서 격리수준이 Read Commited여도 격리수준이 Repeatable Read가 보장된다.    
    캐시를 사용하기 때문에 DB에 직접적인 접근이 이루어지지 않고 애플리케이션 내 캐시에서 조회한 값을 가져오기 때문에 중간에 다른 트랜잭션에서 해당 개체에 변화를 주더라도 동일성이 보장되는 것이다.  
    또, JPA에서는 엔티티의 변경을 추적하기 위해 버전 관리를 지원한다.  
    버전관리를 통해 이후에 변경된 버전은 읽지 않아서 반복읽기가 보장되는 것이다.  

### 쓰기지연

1. 트랜잭션 시작 후 INSERT SQL을 Commit전까지 모은다.  
2. JDBC BATCH SQL기능을 사용해서 한번에 쿼리 전송  

ex)
```java
//트랜잭션 시작
begin();
pertist(member1);
pertist(member2);
pertist(member3);
// 쿼리를 모으고 있다. (전송 X)
commit();
//커밋이 호출되면 실제로 커밋이 일어나기전에 모아둔 쿼리를 전송  
```

### Lazy(지연)로딩과 즉시(EAGER) 로딩
지연 로딩: 객체가 실제 사용될 때 로딩  
즉시 로딩: JOIN SQL로 한번에 연관된 객체까지 미리 조회  

- 지연 로딩 예시
    ```java
    Member member = memberDAO.find(memberId); //SELECT * FROM MEMBER
    Team team = member.getTeam(); 
    String teamName = team.getName(); //SELECT * FROM TEAM
    ```
    
    위 예제처럼 해당 객체를 실제로 사용할때 select * from team을 통해 가져온다.  
    Team의 이름을 조회할때 Team을 DB에서 꺼내오는 것이다!  

- 즉시 로딩 예시
    ```java
    Member member = memberDAO.find(memberId);
    /*
    SELECT M.*, T.*
    FROM MEMBER
    JOIN TEAM …
    */
    Team team = member.getTeam();
    String teamName = team.getName();
    ```

    즉시 로딩을 JOIN을 통해 한번에 데이터를 가져온다.  

JPA는 데이터를 가지고 올때 이렇게 지연 로딩과 즉시로딩을 활용하여 상황에 맞게 필요한 데이터를 한번에 받거나 필요할때에 특정 엔티티를 받아 올 수 있다.  


### 결론  
JPA를 사용하면 생산성과 유지보수성도 높아지고, 반복쿼리도 매우 줄어든다.  
또 핵심적으로 객체지향과 RDB사이에서 둘의 패러다임 차이점을 해결해준다.  
하지만 쿼리를 직접짜지 않는다고 해서 RDB를 모르고 사용한다면 예상치 못한 부분에서 성능 이슈가 발생할 수 있는 것이다.  
이는 결국 JPA도 해당 객체를 RDB에 맞게 매핑해주는 것이기 때문에 객체에 대한 연관관계를 설정하거나 그에 맞는 RDB 도메인 설계도 매우 중요하기 때문이다.     
즉, ORM기술을 제대로 사용하려면 객체지향뿐만 아니라 RDB에 대해서 깊게 이해하고 있어야 한다.  
