# Servlet 기능 정리

## 서블릿 동작 방식

__그림1__  
![image](https://github.com/9ony/9ony/assets/97019540/f3f983fb-1235-4335-a63c-9ed55c16d552)

그림 1과 같이 서블릿컨테이너 역할을 하는 톰캣이 실행될때 서블릿이 생성된다  

__그림2__  
![image](https://github.com/9ony/9ony/assets/97019540/dc042cdc-7ce7-4cba-b557-81aae96eff03)

1. 클라이언트로부터 요청이 들어오면 request,response객체를 생성한다.  
2. 사용자 요청에 맞는 서블릿을 찾아서 실행한다.  
3. 실행한 결과를 response에 객체에 담아서 클라이언트에 전송한다.

## HttpServletRequest  

### HttpServletRequest 역할  

HTTP 요청 메시지를 개발자가 직접 파싱해서 사용해도 되지만, 매우 불편할 것이다.  
서블릿은 개발자가 HTTP 요청 메시지를 편리하게 사용할 수 있도록 개발자 대신에 HTTP 요청 메시지를 파싱한다  
그리고 개발자는 HttpServletRequest 객체를 이용해서 편리하게 HTTP 응답 메세지를 이용할 수 있게 된다.  

### HttpServletRequest 여러가지 기능  

HttpServletRequest 객체를 통해 HTTP 메시지의 start-line, header 정보, 기타(클라이언트의 주소, 포트)등을 조회할 수 있다.

getMethod() : 메소드 조회  
getProtocol() : 프로토콜 조회  
getRequestURL(),getRequestURI() : url 및 리소스 조회  
request.getLocalAddr():로컬 주소 정보  
request.getRemoteAddr(): 접속자 주소 정보  
등 다양한 조회가 가능하다.   

```java
private void printHeaders(HttpServletRequest request) {
    request.getHeaderNames().asIterator().
    forEachRemaining(headerName -> System.out.println(headerName + ":"+ request.getHeader(headerName)));
}
```
위의 코드를 통해 모든 헤더정보를 출력할 수 있다.  

## HttpServletRequest을 이용해 HTTP요청 정보조회  

클라이언트가 HTTP요청 시 주로 3가지 방법으로 요청한다.  

- GET - 쿼리 파라미터 (/url?username=hello&age=20)  
    메시지 바디 없이, URL의 쿼리 파라미터에 데이터를 포함해서 전달  
    예) 검색, 필터, 페이징등에서 많이 사용하는 방식  
- POST - HTML Form  
    content-type: application/x-www-form-urlencoded  
    <body>  
    username=hello&age=20  
    </body>  
    메시지 바디에 쿼리 파리미터 형식으로 전달  
    예) 회원 가입, 상품 주문, HTML Form 사용  
- HTTP message body에 데이터를 직접 담아서 요청  
    HTTP API에서 주로 사용하고 대표적인 데이터 형식은 JSON, XML, TEXT이 있는데, 거의 대부분 JSON을 사용한다.  
    POST, PUT, PATCH 3가지 메소드를 사용  

> 요청예제를 해보기에 앞서 저는 포트번호를 8096으로 변경해서 진행하고 있습니다.  `default=8080`

### GET 요청

전달 데이터는 username=hello&age=20  
ex) http://localhost:8096/request-param?username=hello&age=20  
    URL:PROT+Path?key=value&key=value  
GET요청은 url에 데이터가 그대로 노출되며 `?` 다음에 파라미터를 입력한다.  
파라미터 형식은 key=value 형식 `&`를 통해 추가 가능하고, 참고로 전송길이에 제한이 있다.  

서버에서는 HttpServletRequest 가 제공하는 다음 메서드를 통해 쿼리 파라미터를 편리하게 조회할 수 있다.  

__GET 예제__  

GET 요청  
```text
http://localhost:8096/request-param?username=Hello&age=20
```

__자바코드 (RequestParamServlet.class)__  

```java
@WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("[전체 파라미터 조회] - start");
        req.getParameterNames().asIterator()
                .forEachRemaining(paramName -> System.out.println(paramName + "=" + req.getParameter(paramName)));
        //getParameterNames = 필드 이름(key)
        //getParameter(key) = 해당 필드이름의 값(value)
        System.out.println("[전체 파라미터 조회] - end");
        System.out.println();
        System.out.println("[단일 파라미터 조회]");
        String username = req.getParameter("username");
        System.out.println("username = " + username);
        String age = req.getParameter("age");
        System.out.println("age = " + age);
        System.out.println();
        System.out.println("[이름이 같은 복수 파라미터 조회]");
        //이름이 같은경우 문자열배열로 getParameterValues로 받는다.
        //만약 파라미터 값이 여러개인데 getparmeter로 받을경우 getParameterValues의 첫번째 값이 나온다. 무조건 키당 1개 나온다는뜻
        String[] usernames = req.getParameterValues("username");
        for (String name : usernames) {
            System.out.println("username=" + name);
        }
        resp.getWriter().write("ok");
    }
}
```

__출력결과__  
```text
[전체 파라미터 조회] - start
username=Hello
age=20
[전체 파라미터 조회] - end

[단일 파라미터 조회]
username = Hello
age = 20

[이름이 같은 복수 파라미터 조회]
username=Hello
```

위 코드에서  
@WebServlet 어노테이션에 urlPatterns가 get요청을 할때 경로가된다.  
RequestParamServlet가 HttpServlet을 상속받는 것을 볼 수있다.  
위 동작과정에서 설명했듯이 클라이언트가 요청이 들어오면 해당 요청에 맞는 서블릿의 service()를 재정의하여 코드를 실행하는것을 보자.  

HttpServletRequest.getParameterNames()을 통해 전체 파라미터를 조회할 수 있다.(7번째줄)  
HttpServletRequest 객체에 getParameter()메서드의 인자에 파라미터 key값(파라미터 이름)을 넣어주면 value가 출력 되는것을 볼 수 있다.  

### POST 요청

__POST 예제__

POST요청은 Postman 앱과 Form을 이용해 테스트 하였다.  
servlet 자바 코드는 GET요청할 때 쓴 것과 동일 이유는 마지막에 설명하겠다.  

__Postman__  
![image](https://github.com/9ony/9ony/assets/97019540/86f31f24-b79b-44be-b3ac-f5d92bf6c15d)

__Form__  
![image](https://github.com/9ony/9ony/assets/97019540/aca71d6c-cc05-4eaa-970e-d11f9e9af31b)


결과값
```text
Postman으로 테스트
[전체 파라미터 조회] - start
username=홍길동
age=21
[전체 파라미터 조회] - end

[단일 파라미터 조회]
username = 홍길동
age = 21

[이름이 같은 복수 파라미터 조회]
username
username=홍길동
----------------
Form 테스트
[전체 파라미터 조회] - start
username=servlet
age=15
[전체 파라미터 조회] - end

[단일 파라미터 조회]
username = servlet
age = 15

[이름이 같은 복수 파라미터 조회]
username
username=servlet
```

코드변경 없이 POST요청도 잘 조회된다.  
왜 잘 될까?? 요청 시 개발자 모드에 들어가서 요청한 페이로드를 한번 보자.  
__페이로드 그림__  
- __GET 요청__  
    ![image](https://github.com/9ony/9ony/assets/97019540/1801e0e5-1b9f-4015-a55f-e0ed42c48483)

- __POST Form 요청__  
    ![image](https://github.com/9ony/9ony/assets/97019540/f43014c8-d828-4a8c-b9f2-ad40ea273b23)


해당 그림을 보면 아까 GET으로 요청할때 `?`뒤에 username=hello&age=20과 POST 페이로드 데이터 형식이 똑같이 같다.  
즉, HttpServletRequest 객체로 GET이든 POST든 서버 입장에서 받은 데이터는 똑같다는 의미이다.  

__GET POST 컨텐츠 타입의 차이__  
![image](https://github.com/9ony/9ony/assets/97019540/11719bdd-a54c-4156-b2fa-7a0b0b68c78b)

그림을 보면 GET요청 헤더에는 Content_type헤더가 없고  
POST로 Form요청 시에는 `application/x-www-form-urlencoded`값이 있는걸 볼 수 있다.  
이렇게 폼으로 데이터를 전송 할 때에는 Content_Type에 `application/x-www-form-urlencoded`를 꼭!! 명시해야 한다.  

### API 메시지 바디 - 단순 텍스트

HTTP message body에 데이터를 직접 담아서 요청해 보겠다.
해당 예제도 Postman을 통해 바디에 텍스트를 넣어서 요청해 보겠다.

__자바코드 (RequestBodyStringServlet.class)__  
```java
@WebServlet(name = "requestBodyStringServlet", urlPatterns = "/request-bodystring")
public class RequestBodyStringServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
        ServletInputStream inputStream = request.getInputStream();
        //ServletInputStream을 이용하면 메세지바디의 내용을 바이트코드로 얻을 수 있음
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        //StreamUtils는 스프링이 제공하는 유틸리티 해당 유틸리티로 바이트코드릉 String으로 변환
        //원래는 번거로운 방법으로 하지만 간단하게 StreamUtils를 사용하였음
        System.out.println("messageBody = " + messageBody);
        response.getWriter().write("ok");
    }
}
```
__Postman 전송 요청__  
![image](https://github.com/9ony/9ony/assets/97019540/cf20e533-c81f-4db7-a4c9-096bd147fdaf)


__결과값__  
```text
messageBody = 안녕하세요!
```

__요청시 클라이언트 헤더 정보__
```text
o.a.coyote.http11.Http11InputBuffer      : Received [POST /request-bodystring HTTP/1.1
Content-Type: text/plain
User-Agent: PostmanRuntime/7.32.3   <--포스트맨으로 요청한걸 알수있슴다!
Accept: */*
Postman-Token: d465fdbf-67d2-4ee2-82fe-316024c06766
Host: localhost:8096
Accept-Encoding: gzip, deflate, br
Connection: keep-alive
Content-Length: 16
```

자바코드부터 우선 보면 GET과 POST와는 다르게 `ServletInputStream`을 통해 메세지 바디 내용을 바이트코드로 얻었다.
그리고 `StreamUtils`는 스프링이 제공하는 라이브러리인데 바이트코드를 String으로 변환시켜주는 것이다.  
byte 코드를 우리가 읽을 수 있는 문자(String)로 보려면 문자표 (Charset)를 지정 해줘야 하는데 해당 코드에선 `UTF_8` Charset을 지정했다.    

그리고 추가로 헤더정보를 보면 Content_Type이 `text/plain`인 것을 볼 수 있다.  

✔ 참고 : 헤더 정보를 콘솔에 출력하려면 application.propertiest 파일에서 logging.level.org.apache.coyote.http11=debug를 추가하면된다.  

### API 메시지 바디 - JSON

HTTP바디에 JSON형식으로 요청하는 예제이다.  

__자바코드(RequestBodyJsonServlet.class)__  
```java
@WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-body-json")
public class RequestBodyJsonServlet extends HttpServlet {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.service(req, resp);

        ServletInputStream inputStream = req.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        System.out.println("messageBody = " + messageBody);
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        System.out.println("helloData.username = " + helloData.getUsername());
        System.out.println("helloData.age = " + helloData.getAge());
        resp.getWriter().write("ok");

    }
}
```

__JSON 형식 DTO생성__
```java
//lombok으로 getter, setter 생성해줌
@Getter
@Setter
public class HelloData {
    private String username;
    private int age;

}
```

Lombok 라이브러리를 이용해 getter,setter를 생성해준다.

__Postman 전송 요청__  

![image](https://github.com/9ony/9ony/assets/97019540/a891b2a3-c1ee-47fe-a889-1103eb24c888)

자바코드를 보면 텍스트 전송시에 messageBody까지 String으로 받는 것은 똑같고,  
JSON형식을 담을 HelloData 객체 클래스를 만들고 objectMapper로 해당 문자열을 바인딩 하여 HelloData에 담아서 출력하는 것을 볼 수 있다.  
이 처럼 json형식의 데이터를 받기 위해서는 바이트코드를 String객체로 변환하는 과정 후 추가적으로 파싱하는 과정을 거쳐야 한다.

❗ 추가적으로 objectMapper는 jackson 라이브러리로 스프링 부트로 Spring MVC를 선택하면 기본으로 제공된다.  

## HttpServletResponse으로 응답

HttpServletResponse을 통해 HTTP 요청에 대한 응답메세지를 어떻게 생성 하고,  
응답코드 지정 및 헤더와 바디 설정 및 Content-Type,쿠키,Redirect 등을 어떻게 처리할지 알아보자.   

요청 URL = http://localhost:8096/response-header

__응답 헤더 설정 코드__  
```java
    resp.setHeader("Content-Type","text/plain;charset=utf-8");
    resp.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
    resp.setHeader("pragma","no-cache");
    resp.setHeader("my-header","hello"); //커스텀 헤더도 가능
```

> resp는 HttpServletResponse객체   

위 코드를 보면 resp에 setHeader를 통해 헤더명과 값들을 넣으면 브라우저가 해당 응답 헤더를 아래처럼 받은것을 볼 수있다.  

__setHeader를 이용한 헤더설정 결과__  
![image](https://github.com/9ony/9ony/assets/97019540/daa7d5fe-b90b-45e9-8f4e-64114a6b71ba)

추가적으로 헤더 설정시 아래 코드와 같이 편의 메서드를 지원한다.  
```java
    //Content-Type: text/plain;charset=utf-8
    //Content-Length: 2
    //Content-Length는 생략하면 자동으로 톰켓(서블릿 컨테이너)에서 자동으로 설정
    //response.setHeader("Content-Type", "text/plain;charset=utf-8");
    //위에 setHeader로 직접 입력해도되지만 설정하고자 하는 헤더를 아래처럼 할수도있다.
    resp.setContentType("text/plain");
    resp.setCharacterEncoding("utf-8");
    //response.setContentLength(2); //(생략시 자동 생성)
```

__쿠키 헤더 설정__  

```java
    //Set-Cookie: myCookie=good; Max-Age=600;
    //response.setHeader("Set-Cookie", "myCookie=good; Max-Age=600");
    //쿠키도 마찬가지로 setHeader를 통해 넣을 수 있고, 아래에 쿠키객체를 생성하여 resp객체에 쿠키를 추가가능
    Cookie cookie = new Cookie("myCookie", "good");
    cookie.setMaxAge(600); //600초
    resp.addCookie(cookie);
```

max-age를 설정 안하면 세션쿠키로 적용되며 세선쿠키는 브라우저 종료 시 만료된다.  

__리다이렉트 헤더 설정__

```java
    //response.setStatus(HttpServletResponse.SC_FOUND); //302
    //response.setHeader("Location", "/basic/hello-form.html");
    //위 코드를 아래코드 한줄로 줄여주는 편의기능 제공 (해당코드로 302상태코드는 기본 설정됨)
    response.sendRedirect("/basic/hello-form.html");
```
해당 코드 실행 시  
/response-header 요청 시 302 상태코드로 응답한 후 Location의 경로로 이동하게 된다.  

### 응답 데이터 보내기

- 텍스트 응답 

```java
    PrintWriter writer = resp.getWriter();
    writer.println("안녕하세요~");
```

__텍스트 결과__  
![image](https://github.com/9ony/9ony/assets/97019540/4e99ac92-112f-4f12-b304-e7ced5582387)

HttpServletResponse는 응답 바디에 텍스트를 써서 보낼 때 PrintWriter 객체를 쓰는 것을 볼 수 있다.  
참고로 PrintWriter는 flush() or close()를 해야 스트림에서 출력되는 걸로 알고있는데 안써도 되는걸까??  
답은 서블릿 컨테이너에서 스트림을 열고 닫는걸 관리한다고 한다.  
생각해보면 writer도 개발자가 생성한것도 아니다. response에서 받아와서 썻을 뿐..


- HTML 응답

```java
response.setContentType("text/html");
response.setCharacterEncoding("utf-8");
PrintWriter writer = response.getWriter();
writer.println("<html>");
writer.println("<body>");
writer.println(" <div>안녕?</div>");
writer.println("</body>");
writer.println("</html>");
```

Content-Type이 `text/html`인걸 꼭 확인하자!

__HTML 결과__  

![image](https://github.com/9ony/9ony/assets/97019540/0493bfe7-f169-44c1-968f-1dac29f308ec)

- JSON 응답

```java
//Content-Type: application/json
response.setHeader("content-type", "application/json");
response.setCharacterEncoding("utf-8");
HelloData data = new HelloData();
data.setUsername("kim");
data.setAge(20);
//{"username":"kim","age":20}
String result = objectMapper.writeValueAsString(data);
response.getWriter().write(result);
```

__JSON 결과__  
![image](https://github.com/9ony/9ony/assets/97019540/a7eacfc3-6acb-47c3-8b50-08b276d2f15c)


## 정리

이렇게 서블릿을 이용하여 HttpServletRequest,HttpServletResponse 객체를 이용하여 클라이언트의 요청 헤더와 데이터를 조회하고 응답 헤더와 데이터를 전송하는 것 까지 알아보았다.  
