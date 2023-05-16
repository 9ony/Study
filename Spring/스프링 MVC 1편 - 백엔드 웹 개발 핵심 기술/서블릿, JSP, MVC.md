# 서블릿, JSP, MVC 패턴 실습

서블릿,JSP 를 활용하여 MVC패턴의 회원관리 웹 애플리케이션을 만들어 보자!  

## 서블릿 실습

### 회원 관리 웹 애플리케이션 요구사항

- 회원 정보 (Member)  
    -id:Long (회원번호)  
    -username:String (이름)  
    -age:int (나이)  
    \--------------------------  
    +Member(username,age)


__Member.class 코드__   

```java
@Getter
@Setter
public class Member {

    private Long id;
    private String username;
    private int age;

    public Member(){

    }

    public Member(String username,int age){
        this.username = username;
        this.age= age;
    }
}
```


<br>
<br>

- 회원 저장소 (MemberRepo)  
    -store : Map<Long,Member>[*]  
    -seq : Long  
    <U>-instance{readonly}</U> : MemberRepo  
    \----------------------------------  
    +getInstance() : MemberRepo  
    +save(member:Member) : Member  
    +findById(id:Long) : Member  
    +findMemberAll() : List<Member>[*]  
    +storeClear() : void  
    +update(id:Long,member:Member) : void  

__MemberRepo.class__   

```java
public class MemberRepo {

    private static Map<Long,Member> store = new ConcurrentHashMap<>();
    private static AtomicLong seq = new AtomicLong();

    private static final MemberRepo instance = new MemberRepo();

    public static MemberRepo getInstance(){
        return instance;
    }

    private MemberRepo(){
    }

    public Member save(Member member){
        member.setId(seq.getAndIncrement());
        store.put(member.getId(),member);
        return member;
    }

    public Member findById(Long id){
        return store.get(id);
    }

    public List<Member> findMemberAll(){
        return new ArrayList<>(store.values());
    }

    public void storeClear(){
        store.clear();
    }

    public void update(Long id,Member member){
        member.setId(id);
        store.replace(id,member);
    }
}
```

동시성 문제가 고려되어 있지 않음, 실무에서는 ConcurrentHashMap, AtomicLong 사용 고려  
* 실무에서 HashMap은 멀티쓰레드환경에서 적합하지 않아서 ConcurrentHashMap을 자주 쓴다고 한다.  
* 하지만 예제이므로 로컬에서 진행하기 때문에 HashMap을 쓴다고 하심  
* Long도 마찬가지로 seq값을 계속 증가시킬 것인데,
* seq++ 을한다면 seq를 불러와서 ++연산을 한뒤 그 증가된값을 seq=증가된 seq 3번의 연산이 일어남.  
* 해당 연산도 마찬가지로 멀티 쓰레드환경에 적합하지 않아서 AtomicLong을 사용을 고려하라 하셨다.  
* 저는 ConcurrentHashMap, AtomicLong 사용하였습니다.


<br>
<br>


### 회원 기능 테스트

__MemberRepoTest.class__  

```java
class MemberRepoTest {

    MemberRepo memberRepo = MemberRepo.getInstance();

    @AfterEach
    void afterEach() {
        memberRepo.storeClear();
        //테스트 실행 후 store 초기화
    }
    @Test
    void save() {
        //given
        Member member = new Member("hello",20);
        //when
        Member savedMember = memberRepo.save(member);
        //then
        Member findMember = memberRepo.findById(savedMember.getId());
        Assertions.assertThat(findMember).isEqualTo(savedMember);

    }

    @Test
    void findAll(){
        Member member1 = new Member("member1",18);
        Member member2 = new Member("member2",25);

        memberRepo.save(member1);
        memberRepo.save(member2);

        List<Member> result = memberRepo.findMemberAll();

        // result.stream().forEach(Member ->
        //         System.out.println(Member.getId()+","+Member.getUsername()+","+ Member.getAge()));

        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result).contains(member1,member2);
    }

    @Test
    void updateTest(){
        Member member1 = new Member("member1",20);
        Member member2 = new Member("member2",25);

        memberRepo.save(member1);
        memberRepo.save(member2);

        List<Member> result = memberRepo.findMemberAll();

        Assertions.assertThat(result).contains(member1,member2);

        //멤버 업데이트
        // memberRepo.update(member2.getId(),new Member("updateMember2",21));
        // memberRepo.findMemberAll().stream().forEach(Member ->
        //         System.out.println(Member.getId()+","+Member.getUsername()+","+ Member.getAge()));

        Assertions.assertThat(result).isNotSameAs(memberRepo.findMemberAll());
    }
}
```


<br>
<br>


## Servlet HTTP API 작성 및 HTML 전송

### 회원추가 HTML Form 전송 API  

요청 URL=http://localhost:8096/servlet/members/new-form
input 태그에 입력한 데이터가 `form action=\"/servlet/members/save"` 경로로 `POST`로 전송되고, 저장 결과를 확인할 수 있다.

__MemberFormServlet.class__  

```java
@WebServlet(name="memberFormServlet", urlPatterns = "/servlet/members/new-form")
public class MemberFormServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");

        PrintWriter writer = resp.getWriter();
        writer.write("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                " <meta charset=\"UTF-8\">\n" +
                " <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form action=\"/servlet/members/save\" method=\"post\">\n" +
                " username: <input type=\"text\" name=\"username\" />\n" +
                " age: <input type=\"text\" name=\"age\" />\n" +
                " <button type=\"submit\">전송</button>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>\n");
    }

}
```

<br>
<br>

### 회원저장 + 추가된 회원정보 HTML 응답 API  

전송 클릭 시 input 태그에 적은 username과 age가 서버로 전송된 것을
getParameter()로 받아서 해당 클래스에서 HTML문서로 만들어서 응답해준다.  
age는 String으로 오기 때문에 정수형으로 형변환 해서 member객체로 만들어서 memberRepo 저장소에 저장함.  

__MemberSaveServlet.class 코드__  

```java
@WebServlet(name="memberSaveServlet",urlPatterns = "/servlet/members/save")
public class MemberSaveServlet extends HttpServlet {

    MemberRepo memberRepo = MemberRepo.getInstance();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("MemberSaveServlet.service");
        String username = req.getParameter("username");
        int age = Integer.parseInt(req.getParameter("age"));
        //getParameter로 받아온 값은 String 형태 이므로 Integer로 형변환
        Member member = new Member(username, age);
        System.out.println("member = " + member);
        memberRepo.save(member);
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        PrintWriter writer = resp.getWriter();
        writer.write("<html>\n" +
                "<head>\n" +
                " <meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "성공\n" +
                "<ul>\n" +
                " <li>id="+member.getId()+"</li>\n" +
                " <li>username="+member.getUsername()+"</li>\n" +
                " <li>age="+member.getAge()+"</li>\n" +
                "</ul>\n" +
                "<a href=\"/index.html\">메인</a>\n" +
                "</body>\n" +
                "</html>");
    }
}
```

<br>
<br>

### 전체 회원조회 HTML 응답 API  

요청 URL=http://localhost:8096/servlet/members

__MemberListServlet.class 코드__  

```java
@WebServlet(name="memberListServlet",urlPatterns = "/servlet/members")
public class MemberListServlet extends HttpServlet {
    MemberRepo memberRepo = MemberRepo.getInstance();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        List<Member> members = memberRepo.findMemberAll();
        PrintWriter writer = resp.getWriter();
        writer.write("<html>");
        writer.write("<head>");
        writer.write(" <meta charset=\"UTF-8\">");
        writer.write(" <title>Title</title>");
        writer.write("</head>");
        writer.write("<body>");
        writer.write("<a href=\"/index.html\">메인</a>");
        writer.write("<table>");
        writer.write(" <thead>");
        writer.write(" <th>id</th>");
        writer.write(" <th>username</th>");
        writer.write(" <th>age</th>");
        writer.write(" </thead>");
        writer.write(" <tbody>");

        for (Member member : members) {
            writer.write(" <tr>");
            writer.write("      <td>" + member.getId() + "</td>");
            writer.write("      <td>" + member.getUsername() + "</td>");
            writer.write("      <td>" + member.getAge() + "</td>");
            writer.write(" </tr>");
        }
        //member 수만큼 추가
        writer.write(" </tbody>");
        writer.write("</table>");
        writer.write("</body>");
        writer.write("</html>");

    }

}
``` 

Servlet으로 HTML을 작성해서 제공해주는 것은 너무 불편한것을 느꼇을 것이다.  
이를 위한 해결책으로 뷰 템플릿을 쓰는데 뷰템플릿은 JSP와 Thymeleaf,Freemarker, Velocity등이 있다.  
우선 그중 가장 오래된 기술인 JSP로 실습을 해보자.  


<hr>


## JSP 뷰 템플릿 실습

build.gradle에 jsp 라이브러리 추가  
```text
//JSP 추가 시작
	implementation 'org.apache.tomcat.embed:tomcat-embed-jasper'
	implementation 'javax.servlet:jstl'
//JSP 추가 끝
```

### 회원 등록 폼 JSP

요청 URL=http://localhost:8096/jsp/members/new-form.jsp  
서블릿으로 만든`MemberFormServlet.class`와 동일한 기능  

__new-form.jsp 코드__   

```jsp
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="/jsp/members/save.jsp" method="post">
    username: <input type="text" name="username" />
    age: <input type="text" name="age" />
    <button type="submit">전송</button>
</form>
</body>
</html>
```

jsp를 쓰기위해선 제일 위에 줄에 <%@ page= ~~ %>를 추가해줘야 한다.  
<%@ ~>는 문서의 종류와 인코딩 방식을 정의하기 위해 사용되므로 필수이다.  

__

### 회원 저장기능 JSP

__save.jsp 코드__  

```jsp
<%@ page import="hello.servlet.domain.member.MemberRepo" %>
<%@ page import="hello.servlet.domain.member.Member" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // request, response 사용 가능(JPS -> 서블릿으로 변환되기 때문)
    MemberRepo memberRepo = MemberRepo.getInstance();
    System.out.println("save.jsp");
    String username = request.getParameter("username");
    int age = Integer.parseInt(request.getParameter("age"));
    Member member = new Member(username, age);
    System.out.println("[jsp] member = " + member);
    memberRepo.save(member);
%>
<html>
<head>
    <meta charset="UTF-8">
</head>
<body>
성공
<ul>
    <li>id=<%=member.getId()%></li>
    <li>username=<%=member.getUsername()%></li>
    <li>age=<%=member.getAge()%></li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>
```

JSP는 자바코드를 그대로 사용 가능하다.  
<%@ page import="경로" %> = 자바의 import문  
<% ~~ %> = 자바 코드를 입력  
<%= ~~ %> = 자바 코드를 출력  
<%! ~~ %> = 자바 메소드 정의  
그리고 서블릿의 reqeust(HttpServletRequest)와 response(HttpServletResponse)가 기본적으로 사용 가능  

__members.jsp 코드__  

위 서블릿 자바코드인 MemberListServlet와 같은 기능이다.  

```jsp
<%@ page import="java.util.List" %>
<%@ page import="hello.servlet.domain.member.MemberRepo" %>
<%@ page import="hello.servlet.domain.member.Member" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    MemberRepo memberRepo = MemberRepo.getInstance();
    List<Member> members = memberRepo.findMemberAll();
%>
<html>
<head>
<meta charset="UTF-8">
<title>Title</title>
</head>
<body>
<a href="/index.html">메인</a>
<table>
    <thead>
    <th>id</th>
    <th>username</th>
    <th>age</th>
    </thead>
    <tbody>
    <%
        for (Member member : members) {
        out.println("   <tr>");
        out.println("       <td>" + member.getId() + "</td>");
        out.println("       <td>" + member.getUsername() + "</td>");
        out.println("       <td>" + member.getAge() + "</td>");
        out.println("   </tr>");
        }
    %>
    </tbody>
</table>
</body>
</html>
```


### JSP의 장단점
자바 문법 그대로 사용할 수 있는 장점이 있고 html작성 시 서블릿으로 개발할 때 보다 깔끔하게 된다.  
하지만 jsp안에 비즈니스 로직이 들어가고 만약 서비스가 커져서 코드양도 많아진다면 하나의 jsp파일에 라인수가 엄청나게 많아지고 유지보수도 힘들어 질것이다.  
그래서 MVC패턴을 사용하게 되고 다음 예제부터는 jsp는 오직 뷰에만 관여하고 비즈니스 로직은 서블릿에서 처리할 것이다.  

<hr>

## MVC 패턴  

Model : 뷰에 출력할 데이터를 담아둔다.  
뷰가 필요한 데이터를 모두 모델에 담아서 전달해주는 덕분에 뷰는 비즈니스 로직이나 데이터 접근을 몰라도 되고, 화면을 렌더링 하는 일에 집중함  

View : 모델에 담겨있는 데이터를 사용해서 사용자에게 제공(HTML,JSON,XML 등)  

Controller : HTTP 요청을 받아서 파라미터를 검증하고, 비즈니스 로직을 실행한다.   
그리고 뷰에 전달할 결과 데이터를 조회해서 모델에 담는다.  

### MVC 패턴 - 적용  

서블릿 = 컨트롤러  
JSP = 뷰  
Model = HttpServletRequest 객체를 사용하여 데이터 조회 및 저장  
-> request는 내부에 데이터 저장소를 가지고 있는데, request.setAttribute() , request.getAttribute() 를 사용하면 데이터를 보관하고, 조회 가능  

__MVC 패턴2 그림__  

![image](https://github.com/9ony/9ony/assets/97019540/ccc548f7-b7d1-42a5-a68f-34c63446fbe2)

그 전에는 뷰와 비즈니스로직 부분이 jsp나 서블릿에 전부 들어있었다.  
지금 부터는 위 그림처럼 JSP는 뷰의 역할과 컨트롤러는 서블릿, 모델은 request.setAttribute()를 이용하여 데이터를 보관 및 조회 해보자.

- View (JSP 소스)

    __new-form.jsp__  

    > 파일 경로 : /WEB-INF/views/new-form.jsp  

    ```jsp
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
    <head>
        <meta charset="UTF-8">
        <title>Title</title>
    </head>
    <body>
    <form action="save" method="post">
        username: <input type="text" name="username" />
        age: <input type="text" name="age" />
        <button type="submit">전송</button>
    </form>
    </body>
    </html>
    ```
    
    __save-result.jsp__  

    > 파일 경로 : /WEB-INF/views/save-result.jsp  


    ```jsp
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
    <head>
        <meta charset="UTF-8">
    </head>
    <body>
    성공
    <ul>
        <%-- <%= ((Member)request.getAttribute("member")).getId()%> --> ${member.id}로 줄여짐 --%>
        <%-- ${}는 프로퍼티 표기법으로 member의 id와 username을 가져올 수 있다.--%>
        <%-- setter getter는 잇어야됨 -> get은 생략 후 앞글자 대문자를 소문자로 변경--%>
        <li>id=${member.id}</li>
        <li>username=${member.username}</li>
        <li>age=${member.age}</li>
    </ul>
    <a href="/index.html">메인</a>
    </body>
    </html>
    ```

    __members.jsp__  

    > 파일 경로 : /WEB-INF/views/members.jsp   


    ```jsp
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <html>
    <head>
        <title>Title</title>
    </head>
    <body>
    <a href="/index.html">메인</a>
    <table>
        <thead>
        <th>id</th>
        <th>username</th>
        <th>age</th>
        </thead>
        <tbody>
        <%-- <c:는 jstl문법 반복문,조건문 등등 이용가능 --%>
        <c:forEach var="item" items="${members}">
            <tr>
            <td>${item.id}</td>
            <td>${item.username}</td>
            <td>${item.age}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </body>
    </html>
    ```

    __/WEB-INF/__   
    jsp 경로가 views상위 폴더에 WEB-INF가 있는데 해당 패키지가 안에 JSP가 있으면 외부에서 직접 JSP를 호출할 수 없고 컨트롤러로만 호출할 수 있다.  

- Controller (자바 Servlet 소스)

    __MvcMemberFormServlet.class__  

    /servlet-mvc/members/new-form 요청 시
    new-form.jsp 페이지를 응답(이동)하는 역할의 컨트롤러  

    ```java
    @WebServlet(name = "mvcMemberFormServlet", urlPatterns = "/servlet-mvc/members/new-form")
    public class MvcMemberFormServlet extends HttpServlet {


        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            String viewPath = "/WEB-INF/views/new-form.jsp";
            RequestDispatcher dispatcher = req.getRequestDispatcher(viewPath);
            dispatcher.forward(req, resp);

        }
    }
    ```

    __MvcMemberListServlet.class__  

    /servlet-mvc/members
    members.jsp파일을 보면 members를 위에 설명한 jstl문법을 이용해 반ㄴ복해서 req에서 꺼내어 출력하는것을 볼수 있다.  
    해당 컨트롤러에서 req에 members를 저장해서 전송했기 때문!!   

    ```java
    @WebServlet(name = "mvcMemberListServlet", urlPatterns = "/servlet-mvc/members")
    public class MvcMemberListServlet extends HttpServlet {

        MemberRepo memberRepo = MemberRepo.getInstance();

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            List<Member> members = memberRepo.findMemberAll();
            req.setAttribute("members",members);
            //request에 members를 저장한다. (나중에 jsp에서 조회하여 화면을 구성함)

            String ViewPath = "/WEB-INF/views/members.jsp";

            RequestDispatcher dispatcher = req.getRequestDispatcher(ViewPath);
            dispatcher.forward(req,resp);

        }
    }
    ```

    __MvcMemberSaveServlet.class__    

    new-form에서 회원등록할 이름과 나이를 입력후 전송버튼을 누르면 해당 컨트롤러로 요청을 보낸다.  

    > `<form action="save" method="post">` 상대경로 임을 주의하자!!  


    ```java
    @WebServlet(name = "mvcMemberSaveServlet", urlPatterns = "/servlet-mvc/members/save")
    public class MvcMemberSaveServlet extends HttpServlet {

        private MemberRepo memberRepo = MemberRepo.getInstance();

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String username = req.getParameter("username");
            int age = Integer.parseInt(req.getParameter("age"));

            Member member = new Member(username, age);
            System.out.println("member=" + member);
            memberRepo.save(member);
            //Model에 데이터를 보관한다.
            req.setAttribute("member", member);
            String ViewPath = "/WEB-INF/views/save-result.jsp";
            RequestDispatcher dispatcher = req.getRequestDispatcher(ViewPath);
            dispatcher.forward(req, resp);
        }
    }
    ```

    getParameter()를 통해 이름과 나이를 꺼내서 member객체를 생성하여 MemberRepo에 저장하는 것을 볼 수 있고, 저장한 member객체를 req에 담아서 save-result(결과 페이지)로 이동하는 것을 볼 수 있다.  

    예제에서 HttpServletRequest를 Model로 사용한다.
    request가 제공하는 setAttribute() 를 사용하면 request 객체에 데이터를 보관해서 뷰에 전달할 수 있다.  
    뷰는 request.getAttribute() 를 사용해서 데이터를 꺼내면 된다.  

### redirect 와 forward

`resp.sendredirect(ㅍiewPath)`는 클라이언트에게 위에 설정한 ViewPath 경로를 넘겨줘서 클라이언트가 재요청하면서 경로로 이동하는 것 (상태코드 304응답 시 일어나는 것과 같음)  

`dispatcher.forward(req,resp)`는 클라이언트를 거치지 않고 바로 서버에서 해당 페이지로 이동하는 것인데, forward는 내부 이동이기 때문에 요청정보를 그대로 들고 해당페이지로 이동되며 URL주소도 바뀌지 않는다.  

### 추가!! 서블릿 do + HTTPMethod()

서블릿은 그림과 같이 service()에서 요청 온 HTTPMethod 따라 처리를 다르게 할 수 있다.  
ex) Get요청시 doGet() Post요청 시 doPost()..  

![image](https://github.com/9ony/9ony/assets/97019540/efa87df9-dc2e-4457-a51e-6c37a19caf66)

- doGet,doPost 처리 예제

    __MvcMethodTestServlet.class__  

    ```java
    @WebServlet("/servlet-mvc/methodtest") //name은 생략해도됨
    public class MvcMethodTestServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            System.out.println("get 요청이 왔습니다");
            req.setAttribute("method","GET 입니다");
            endpoint(req,resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            System.out.println("post 요청이 왔습니다");
            req.setAttribute("method","POST 입니다");
            endpoint(req,resp);
        }

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            System.out.println("service() 공통 로직입니다");
            super.service(req, resp);
        }

        private void endpoint(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException{
            String ViewPath = "/WEB-INF/views/domethodcheck.jsp";
            System.out.println(ViewPath+"로 이동합니다.");
            RequestDispatcher disp = req.getRequestDispatcher(ViewPath);
            disp.forward(req,resp);
        }
    }
    ```

    __domethodcheck.jsp__  

    ```jsp
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
    <head>
        <title>어떤 메소드?</title>
    </head>
    <body>
        <h1>요청이 GET인지? POST인지? = ${method}</h1>
    </body>
    </html>
    ```

- Get 요청 및 결과

    __요청 및 반환 HTML소스__  

    ![image](https://github.com/9ony/9ony/assets/97019540/a43ba828-bfed-4d82-82e6-2ecda9417195)

    __콘솔 로그__  

    ```text
    service() 공통 로직입니다  
    get 요청이 왔습니다  
    /WEB-INF/views/domethodcheck.jsp로 이동합니다.  
    ```
- Post 요청 및 결과
    
    
    __요청 및 반환 HTML소스__  

    ![image](https://github.com/9ony/9ony/assets/97019540/a465120c-1da4-4937-bd12-17fd48aef353)


    __콘솔 로그__  

    ```text
    service() 공통 로직입니다  
    post 요청이 왔습니다  
    /WEB-INF/views/domethodcheck.jsp로 이동합니다.  
    ```

위 예제처럼 service에서 공통로직을 처리하고 메소드에 따라 PUT , PATCH , DELETE도 예제에는 없지만 다 따로 가능하다.  
그리고 endpoint 메서드를 만들어서 추가했지만 GET POST마다 이동할 페이지가 다를 수 있기 때문에 doGet(),doPost()에 각각 다르게 포워딩할 주소를 작성하면 될 것 같다.  

### MVC 패턴의 한계

MVC패턴 덕분에 뷰와 컨트롤러 역할이 구분 되어서 코드가 깔끔해졌다.  
이제 MVC패턴으로 뷰는 화면or데이터만 제공을 하고 컨트롤러는 요청에 따른 비즈니스 로직을 수행하여 모델에 데이터를 저장만 하면된다.

하지만 위 MVC패턴도 중복되는 부분이 보인다.  

- 포워드 중복  

    View로 이동하는 코드가 항상 중복 호출이 된다...  
    위에 doGet , doPost 로 할때도 따로 메서드로 만들어서 각각 넣어준 것을 보았을 것이다.  

- 경로 중복  

    파일의 확장자, /WEB-INF/views 등이 중복되고 경로가 바뀐다면? 코드를 다열어서 경로를 다바꿔 줘야 될 것이며 이제 뷰템플릿이 jsp가 아닌 벨로시티,타임리프로 변경하게 된다면 확장자도 다 바꿔야 될 것이다.  

- 테스트 케이스 작성문제

    HttpServletRequest request, HttpServletResponse response는 서블릿 컨테이너가 클라이언트가 요청 시에 생성한다는 걸 앞에서 배웠다.  
    그래서 테스트케이스 작성 시에 service()로직을 테스트 한다면 req,resp객체가 생성되지 않기 때문에 테스트하기 난감하다.  
    대안으로 Mock이 있긴한데 예제에서 안다뤘기 때문에 생략하겠다.  

__결론적으로__  
중복되는 로직을 공통으로 처리하는게 어려워진다.  
하지만 이 문제를 해결하기 위해 프론트 컨트롤러 패턴을 도입하면 이런 문제를 해결할 수 있는데,  
해당 패턴을 적용한 MVC가 스프링 MVC이다. 스프링 MVC의 핵심이 프론트 컨트롤러라고도 볼 수 있다.  


## 정리  
- 서블릿과 JSP로 각각 회원관리 웹 애플리케이션을 만들고 각각 장단점을 학습  
- JSP와 서블릿을 활용하여 MVC2패턴으로 역할을 나눠서 회원관리 웹애플리케이션을 만들어 봄    
- MVC2패턴을 적용해서 뷰와 비지니스로직이 역할이 구분되어 코드가 가독성이 좋아졌음  
- MVC 패턴을 적용했음에도 여전히 중복되는 코드가 존재    
- 해당 중복을 해결하기 위해 Front Controller로 해결방법을 생각해보자.   