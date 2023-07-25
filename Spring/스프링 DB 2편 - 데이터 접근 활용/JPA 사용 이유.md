# JPA를 왜 써야하는가??

## SQL 중심 개발의 문제점

우선 JPA를 소개하기전에 SQL 중심의 개발에 대한 문제점에 대해 알아보자.  

### SQL에 의존적이다!!  

우리가 웹 애플리케이션에서 무언가를 저장할때 결국 SQL문을 접속한 데이터베이스에 보내주어야 할때,  
1.커넥션 연결 및 트랜잭션관리  
2.SQL문 작성  
3.데이터 바인딩  
4.응답 결과 반환  
5.리소스 정리  
이러한 과정들을 거치게 되는데 SQL Mapper기술(JdbcTemplate,Mybatis 등..)을 써서 반복문제를 해결하더라도 `SQL문은 직접 작성`해야 한다.  
즉 우리가 요청받은 데이터를 데이터베이스에 저장하기 위해 객체를 SQL문으로 변환시켜주는 작업을 하는 것이다.  

__예시__  
```java
public class Member{
    long id;
    String name;
    int age;
    String job;
    String phoneNumber; //추가
}
```

위와 같은 회원 객체를 저장한다고 가정할 때 아래와 같은 sql문을 작성한다고 가정해보자.  

```sql
insert into Member(id,'name',age,'job') values(id,'name',age,'job');

select id,'name',age,'job' from Member where id= 조회할id;

delete from member where id = 삭제할id;
```
만약 이때 회원정보에 `휴대폰번호를 추가해서 넣고 싶다는 요구사항`이 들어온다면 결국 `sql문을 전부 수정`해주어야 한다.
즉, SQL에 의존적인것을 피할 수가 없다!!

### 객체지향 언어와 RDB의 패러다임 불일치

애플리케이션 서버를 Spring으로 개발한다고 했을때 해당 웹 애플리이케이션은 자바로 작성되며, 자바는 `객체지향 언어`이다.  
하지만, 데이터가 관계형 데이터베이스에 저장되는데, 이러한 데이터베이스는 SQL문만 이해한다.  

![image](https://github.com/9ony/9ony/assets/97019540/e3a917c2-e99b-4617-bb92-947cf933d254)

위 그림처럼 Top과 Pants는 Clothes를 상속하고 있고 데이터베이스도 TOP,Pants테이블이 Clothes의 기본키가 참조키로 되어있다.  
이러한 구조에서 데이터베이스에 Top을 저장하고 조회할때 어떻게 되는지 알아보자.  

- 데이터베이스 Top 저장  
    Clothes는 추상클래스이므로 인스턴스 생성이 안된다.  
    그래서 DB에 Top을 저장하기 위해서는 Top객체를 분해해서 각 insert 쿼리에 맞는 데이터를 넣어줘야한다.  
    1.Top 객체 분해  
    2.INSERT INTO Clothes ...  SQL문 작성  
    3.INSERT INTO Top …  SQL문 작성  
    4.저장  

- 데이터베이스 Top 조회  
    1.Clothes와 TOP 테이블 `조인 SQL 작성`  
    2.조회한 데이터를 new Top(Clothes정보,Top정보);  
    3.Pants를 받으려면 또 Pants를 받는 쿼리도 작성해야 함.  
    4.각각의 객체를 생성해서 받아야함  
    (매우 복잡하다..)
    
    > 그래서 DB에 저장할 객체에는 상속관계를 사용하지 않는다.  
    게다가 추상클래스는 new를 통한 객체 생성도 안됨...

- 자바 컬렉션에 저장 및 조회 시  

    저장 : list.add(top);  
    조회 : Top top = list.get(cid);  
    조회 (다형성 활용) : Item item = list.get(albumId);  

    > 컬렉션에 저장하고 조회시에는 다형성도 활용할 수 있어서 매우 간편!!

RDB에 넣고 빼려니 너무 복잡함!! (SQL 매핑해주고, 객체로 변환해주고...)

### 연관관계

객체와 테이블의 연관관계의 차이  

![image](https://github.com/9ony/9ony/assets/97019540/46eda072-e00a-4f3e-bbca-e49295e82df0)

`객체는 참조`를 사용 (member.getTeam())  
`테이블은 외래키`를 사용 (JOIN ON M.TEAM_ID = T.TEAM_ID)  
MEMBER에서 TEAM 가고싶다면, MEMBER에 있는 TEAM_ID(FK)와 TEAM의 PK를 JOIN하면 됨.  

객체는 `Member에서 Team`으로는 갈 수 있지만, 역으로 `Team에서 Member로는 가지 못함` (반대방향으로 참조가 없기 때문)  
테이블의 경우 PK와 FK로 JOIN하는 것이기 때문에 `양방향으로 가능`  

### 객체 or 테이블 모델링 예시와 문제점  

이제 객체를 테이블에 맞춰 모델링을 한것과 객체답게 모델링한 것의 차이를 알아보자.  
( ✔ 보통 객체를 테이블에 맞추어서 모델링을 함 )  

- 테이블에 맞춘 모델링  

    ```java
    //객체를 테이블에 맞추어 모델링
    class Member {
        String id; //MEMBER_ID 컬럼 사용
        Long teamId; //TEAM_ID FK 컬럼 사용
        String username; //USERNAME 컬럼 사용
    }

    class Team {
        Long id; //TEAM_ID PK 사용
        String name;  //Name 컬럼 사용
    }
    ```

    ```sql
    INSERT INTO MEMBER(MEMBER_ID, TEAM_ID, USERNAME) VALUES ...  
    ```

    인서트 쿼리에 컬럼과 필드 매핑해서 쭉쭉 넣어주면 됨 (보통 MyBatis 같은 것들이 이러한 작업 편리하게 해줌)

    하지만!! 객체지향이랑은 거리가 멀어보인다.  
    객체지향은 필드에서 참조가 이루어져야 함!!

- 객체지향 모델링
    ```java
    class Member {
        String id; //MEMBER_ID 컬럼 사용
        Team team; // "참조로 연관관계를 맺는다!!!!"
        String username;//USERNAME 컬럼 사용
        
        Team getTeam() {
            return team;
        }
    }

    class Team {
        Long id; //TEAM_ID PK 사용
        String name; //NAME 컬럼 사용
    }
    ```

    ```sql
    INSERT INTO MEMBER(MEMBER_ID, TEAM_ID, USERNAME) VALUES...
    ```
    테이블에 필드 매핑해서 넣어야하는데, 객체 모델링에는 외래키 값을 나타내는 필드가 없음.  
    Team객체에 대한 참조만 존재  
    Team에 대한 참조를 타고가서 Id를 가져와서 FK로 넣어줌. (`member.getTeam().getId();`)   


- 객체지향 모델링의 문제점  

    ```sql
    SELECT M.*, T.*
    FROM MEMBER M
    JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID
    ```
    ```java
    public Member find(String memberId) {
        //SQL 실행
        Member member = new Member();
        //DB에서 조회한 회원 관련 정보를 모두 입력
        Team team = new Team();
        //DB에서 조회한 팀 관련 정보를 모두 입력
        
        //회원과 팀 관계 설정
        member.setTeam(team);
        return member;
    }
    ```

    조인해서 가져온 회원과 팀 각각의 정보를 각각의 객체에 넣어주고, 마지막으로 setTeam(team)으로 연관관계 설정까지 개발자가 해줘야함
    그래서 따로 DTO를 만들어서 MemberTeam같이 필드를 합친 객체를 만들어서 받기도 했다.  

### 객체지향과 RDB의 탐색방식에 따른 문제점  

- 객체 그래프 탐색  

    __객체 그래프 그림__  
    
    ![image](https://github.com/9ony/9ony/assets/97019540/f73313ce-7f97-401f-a945-120de37f9e48)

    회원이 소속된 팀을 조회할 때 참조를 사용해서 연관된 팀을 찾는 것처럼 객체는 마음껏 객체 그래프를 탐색할 수 있어야 한다.
    ```java
    Team team = member.getTeam();
    member.getOrder().getOrderItem()
    ```
    위와 같이 회원을 통해 팀도 조회하고 주문상품도 조회할 수 있고 더나아가 Delivery와 Category까지 접근 가능하다.  
    
    하지만, RDB 개체는 `SQL에 따라 탐색 범위가 결정`됨  
    ```sql
    SELECT M.*, T.*
    FROM MEMBER M
    JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID
    --연관관계 설정을 멤버와 팀만 해둠--
    ```
    위의 SQL을 실행해서 member.getTeam();은 성공함.  
    하지만!! member.getOrder()은 데이터가 없어 탐색이 불가능  

- 신뢰성 문제  

    위에서 SQL에 따라 범위가 달라진다고 했다.  
    그러면 아래와 같이 find()을 통해 Member객체를 받았을 때 해당 메서드만 보고 연관관계를 알 수가 없다!  
    ```java
    class MemberService {
        //...
        public void process() {
            Member member = memberDAO.find(memberId);
            member.getTeam(); //팀이 연관관계 설정이 되있는지??
            member.getOrder().getDelivery(); // 주문과 배달정보는??
        }
    }
    ```
    즉, 서비스 코드만 보고는 전혀 알 수 없기 때문에 `SQL문을 직접 확인`해야함.  
    `SQL에 종속적이기 때문에 발생`하고 진정한 의미의 `계층 분할이 어려움`!!  

- 객체 로딩 문제  

    위 신뢰성 문제 때문에 여러 상황에 따른 조회 메서드가 필요하다.

    ```java
    member.getMember(); //회원조회
    member.getMemberOrder() //회원과 주문..
    //...
    ```
    연관관계를 알 수있게 메서드를 따로 다만들고 sql도 만들어줘야한다.  
    위 `객체 그래프 그림`에 연관관계에 있는 모든 정보를 join한다면 쿼리도 엄청 길어질 것이고,  
    그 상황에 필요없는 정보들까지 넘어오게 되어 또 비효율 적이다.  

- 비교 문제  

    SQL Mapper나 ORM을 쓰면 캐싱기능 때문에 해당 문제가 없겟지만, jdbc를 사용할 경우에는 아래와 같은 문제가 있다.  
    ```java
    int memberId = 0;
    Member member1 = memberDAO.find(memberId);
    Member member2 = memberDAO.find(memberId);
    boolean b = member1 == member // False
    ```

    자바 컬렉션에서 조회할 경우에는  
    ```java
    String memberId = "100";
    Member member1 = list.get(memberId);
    Member member2 = list.get(memberId);
    boolean b = member1 == member // True
    ```

## 결론

객체답게 모델링 할수록 매핑 작업만 늘어난다.  
매핑작업을 줄일려면 객체지향 모델링을 포기해야함.  

- 복잡한 JDBC 코드 
    JDBC를 사용하면 데이터베이스와 상호 작용하기 위해 많은 양의 코드를 작성  
    코드의 가독성과 유지 보수성이 저하됨

- 객체-관계 매핑 (ORM)부재 
    JDBC를 사용하면 객체와 관계형 데이터베이스 간의 매핑을 개발자가 직접 처리(생산성 저하)    
    객체지향 언어인 Java와 관계형 데이터베이스 간의 불일치로 인한 문제   

- 데이터베이스 종속성  
    기존의 EJB CMP 기술은 특정 데이터베이스에 종속  
    데이터베이스를 변경하려면 많은 코드 수정이 필요  

- 성능 문제  
    일부 기존 ORM 프레임워크(EJB 등..)의 성능이 좋지 않음  
    기존 ORM 데이터베이스 액세스 최적화 문제

위와 같은 문제를 해결하기 위해 JPA가 등장하였고 객체 지향적인 방식으로 데이터베이스에 접근할 수 있는 API를 제공한다.  