# 스프링 기본 기능

스프링의 요청 매핑을 예시를 통해 학습하고 HTTP 요청,응답에 대한 기능을 알아보고 핸들러 매핑과 어댑터 구조에 대해 알아보자
추가적으로 Thymeleaf의 간단한 사용법도 익혀보도록하자.  

## 요청 매핑


__기본 요청__  
```java
@RestController
public class MappingController {
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 기본 요청
     * 둘다 허용 /hello-basic, /hello-basic/
     * HTTP 메서드 모두 허용 GET, HEAD, POST, PUT, PATCH, DELETE
     */
    @RequestMapping("/hello-basic")
    public String helloSpring(){
        String comment = "hello-basic";
        log.info("info log={}",comment);
        return "hello";
    }
}
```
요청 예시) http://localhost:8096/hello-basic
result body = hello
GET, HEAD, POST, PUT, PATCH, DELETE 전부 요청 가능  
✔ 스프링 부트 3.0 부터는 /hello-basic 과 /hello-basic/은 서로 다른 요청이다.  
즉, 3.0 이전은 끝에 /(slash)를 없애서 처리 3.0 이후는 유지

__다중 요청 경로__  

```java

    /**
     * 대부분의 속성을 배열[] 로 제공하므로 다중 설정이 가능
     */
    @RequestMapping({"/hello-basic2","/hello-basic3"})
    public String helloSpring2(HttpServletRequest req){
        String comment;
        if(req.getRequestURI().equals("/hello-basic2")){
            comment = "hello Basic 2!!";
        }else{
            comment = "hello Basic 3!!";
        }
        log.info("info log={}",comment);
        return comment;
    }
```
요청 예시)
http://localhost:8096/hello-basic2   //result body = hello Basic 2!!  
or  
http://localhost:8096/hello-basic3   //result body = hello Basic 3!!  

매핑 속성을 여러개 주고 예제처럼 요청URI로 구분해서 다른 뷰를 넘기거나 로직을 처리할 수 있다.  

__특정 HTTP 메서드 요청__  

```java
    /**
     * method 특정 HTTP 메서드 요청만 허용
     * GET, HEAD, POST, PUT, PATCH, DELETE
     */
    @RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
    public String mappingGetV1() {
        log.info("mappingGetV1");
        return "ok";
    }

    /**
     * 편리한 축약 애노테이션
     * @GetMapping
     * @PostMapping
     * @PutMapping
     * @DeleteMapping
     * @PatchMapping
     */
    @GetMapping(value = "/mapping-get-v2")
    public String mappingGetV2() {
        log.info("mapping-get-v2");
        return "ok";
    }
```  
요청 예시) http://localhost:8096/mapping-get-v1

기본적으로 RequestMapping에 method를 설정하지 않으면 모든 메서드를 허용한다.   (GET, HEAD, POST, PUT, PATCH, DELETE)  
@GetMapping , @PostMapping 처럼 축약이 가능하다.  

__PathVariable__  

```java
    /**
     * PathVariable 사용
     * 변수명이 같으면 생략 가능
     * ex. @PathVariable("userId") String userId -> @PathVariable userId
     */
    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String data) {
        log.info("mappingPath userId={}", data);
        return "ok";
    }

    /**
     * PathVariable 사용 다중
     * ex. @PathVariable String userId,@PathVariable Long orderId
     */
    @GetMapping("/mapping/users/{userId}/orders/{orderId}")
    public String mappingPath(@PathVariable String userId, @PathVariable Long orderId) {
        log.info("mappingPath userId={}, orderId={}", userId, orderId);
        return "ok";
    }
```
요청 예시) http://localhost:8096/mapping/users/userA/orders/100

HTTP API요청 URI를 만들 때 리소스 경로에 식별자를 넣는 PathVariable을 요즘 많이 사용한다고 한다.  
@RequestMapping 은 URL 경로를 템플릿화 할 수 있는데, @PathVariable 을 사용하면 매칭 되는 부분을 편리하게 조회할 수 있다.  

__params__ 

```java
    /**
     * 파라미터로 추가 매핑
     * params="mode",
     * params="!mode"
     * params="mode=debug"
     * params="mode!=debug" (! = )
     * params = {"mode=debug","data=good"}
     */
    @GetMapping(value = "/mapping-param", params = "mode=debug")
    public String mappingParam() {
        log.info("mappingParam");
        return "ok";
    }
```

요청 예시) http://localhost:8096/mapping-param?mode=debug  

특정 파라미터가 있거나 없는 조건을 추가할 수 있다.  
하지만 잘 사용하지 않는다고 한다.  

__headers__  

```java
    /**
     * 특정 헤더로 추가 매핑
     * headers="mode",
     * headers="!mode"
     * headers="mode=debug"
     * headers="mode!=debug" (! = )
     */
    @GetMapping(value = "/mapping-header", headers = "mode=debug")
    public String mappingHeader() {
        log.info("mappingHeader");
        return "ok";
    }
```

요청 예시) postman 사용  
![image](https://github.com/9ony/9ony/assets/97019540/ea447d25-9d28-42a4-9390-0dc824052903)  


HTTP 헤더를 사용한 조건 추가도 가능하다  

__consumes__  

```java
    /**
     * Content-Type 헤더 기반 추가 매핑 Media Type
     * consumes="application/json"
     * consumes="!application/json"
     * consumes="application/*"
     * consumes="*\/*"
     * MediaType.APPLICATION_JSON_VALUE
     */
    @PostMapping(value = "/mapping-consume", consumes = "application/json")
    public String mappingConsumes() {
        log.info("mappingConsumes");
        return "ok";
    }
```
요청 예시) postman 사용  
![image](https://github.com/9ony/9ony/assets/97019540/272da523-7981-414c-bb89-bb277abd0dfd)

application/json외 다른 콘텐트 타입으로 요청 시 `415 Unsupported Media Type`  

__produces__  

```java
    /**
     * Accept 헤더 기반 Media Type
     * produces = "text/html"
     * produces = "!text/html"
     * produces = "text/*"
     * produces = "*\/*"
     */
    @PostMapping(value = "/mapping-produce", produces = "text/html")
    public String mappingProduces() {
        log.info("mappingProduces");
        return "ok";
    }
```

요청 예시) postman 사용  

![image](https://github.com/9ony/9ony/assets/97019540/49babe54-c4a5-4ed4-9d0d-6762db7ce356)


produces명시된 값과 Accept 설정이 다르게 요청이 오면 406 Not Acceptable 반환

## 요청매핑 API 간단예제
```java
@RestController
@RequestMapping("/mapping/users")
public class MappingClassController {
    /**
     * GET /mapping/users
     */
    @GetMapping
    public String users() {
        return "get users";
    }
    /**
     * POST /mapping/users
     */
    @PostMapping
    public String addUser() {
        return "post user";
    }
    /**
     * GET /mapping/users/{userId}
     */
    @GetMapping("/{userId}")
    public String findUser(@PathVariable String userId) {
        return "get userId=" + userId;
    }
    /**
     * PATCH /mapping/users/{userId}
     */
    @PatchMapping("/{userId}")
    public String updateUser(@PathVariable String userId) {
        return "update userId=" + userId;
    }
    /**
     * DELETE /mapping/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable String userId) {
        return "delete userId=" + userId;
    }
}
```

### 회원 관리 API

- 회원 목록     : GET /users
- 회원 등록     : POST /users
- 회원 조회     : GET /users/{userId}
- 회원 수정     : PATCH /users/{userId}
- 회원 삭제     : DELETE /users/{userId}

이렇게 목록조회와 등록은 같은 url이지만 메소드가 다르기 때문에 동작 로직이 다르다.  
이렇게 행위는 메서드로 구분하고, 접근할 대상인 자원(users or users/{id})은 URI에 명시하고,
회원의 등록에 필요한 정보는 Payload (메세지 바디)에 담아서 전송한다.  

## 스프링 HTTP Header 정보 조회

```java
@Slf4j
@RestController
public class RequestHeaderController {

    @RequestMapping("/headers")
    public String headers(HttpServletRequest req,
                          HttpServletResponse resp,
                          HttpMethod httpMethod,
                          Locale locale,
                          @RequestHeader Map<String,String> headerMap,
                          @RequestHeader("host") String host,
                          @CookieValue(value="myCookie", required = false) String cookie
                          ){
        log.info("request={}", req);
        log.info("response={}", resp);
        log.info("httpMethod={}", httpMethod);
        log.info("locale={}", locale);
        log.info("headerMap={}", headerMap);
        log.info("header host={}", host);
        log.info("myCookie={}", cookie);
        return "ok";
    }
}
```

스프링 핸들러는 아래와 같이 다양한 파라미터를 받을 수 있다.  
headers 메서드에 받은 파라미터 객체를 간단히 알아보자  

- __HttpServletRequest__ : HTTP 요청 정보(클라이언트 요청, 쿠키, 세션 등)를 제공하는 인터페이스  
- __HttpServletResponse__ : HTTP 응답 정보(요청 처리 결과)를 제공하는 인터페이스  
- __HttpMethod__ : HTTP 메서드를 조회한다.(org.springframework.http.HttpMethod)  
- __Locale__ : Locale 정보를 조회한다.(가장 우선순위가 높은 언어가 지정)  
    스프링은 추가적으로 LocaleResolver가 있는데, 여러가지 Locale이 왔을때 어떻게 지정할지 설정할 수 있다.  
    [LocalResolver 참고 글](https://terry9611.tistory.com/304)  
- __@RequestHeader MultiValueMap<String, String> headerMap__  
    모든 HTTP 헤더를 MultiValueMap 형식으로 조회한다.  
    (`MultiValueMap` 아래 간략 설명)  
- __@RequestHeader("host") String host__  
    - 특정 HTTP 헤더를 조회한다.  
    - 속성  
        필수 값 여부: required  
        기본 값 속성: defaultValue  
- __@CookieValue(value = "myCookie", required = false) String cookie__  
    - 특정 쿠키를 조회한다.  
    - 속성  
        필수 값 여부: required  
        기본 값: defaultValue  
- __MultiValueMap__  
    MAP과 유사한데, 하나의 키에 여러 값을 받을 수 있다.  
    HTTP header, HTTP 쿼리 파라미터와 같이 하나의 키에 여러 값을 받을 때 사용한다.  
    ex) keyA=value1&keyA=value2  
```java
    MultiValueMap<String, String> map = new LinkedMultiValueMap();
    map.add("keyA", "value1");
    map.add("keyA", "value2");
    //[value1,value2]
    List<String> values = map.get("keyA");  
```
    ✔ MultiValueMap.get(keyname) 반환 타입은 List이다  

[핸들러 메서드 파라미터 공식 문서](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/arguments.html#page-title)

[핸들러 메서드 반환타입 공식 문서](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/return-types.html)

## HTTP 요청 파라미터 - 쿼리 파라미터, HTML Form , @ModelAttribute

### HTTP 요청 데이터 조회 - 개요  
서블릿에서 학습했던 HTTP 요청 데이터를 조회 하는 방법과 비교하여 스프링이 얼마나 깔끔하고 효율적으로 바꾸어주는지 알아보자.  
HTTP 요청 메시지를 통해 클라이언트에서 서버로 데이터를 전달하는 방법을 알아보자.  
클라이언트가 서버로 요청 데이터를 전달 시 주로 다음 3가지 방법을 사용  
- GET - 쿼리 파라미터  
    /url?username=hello&age=20  
    메시지 바디 없이, URL의 쿼리 파라미터에 데이터를 포함해서 전달  
    예) 검색, 필터, 페이징등에서 많이 사용하는 방식  
- POST - HTML Form
    content-type: application/x-www-form-urlencoded  
    메시지 바디에 쿼리 파리미터 형식으로 전달 username=hello&age=20  
    예) 회원 가입, 상품 주문, HTML Form 사용  
- HTTP message body에 데이터를 직접 담아서 요청  
    HTTP API에서 주로 사용, JSON, XML, TEXT  
    데이터 형식은 주로 JSON 사용  
    POST, PUT, PATCH  

위 전송방식을 조회하는 것을 요청파라미터 조회라 함.  

__RequestParamController.class__  

```java
@Slf4j
@Controller
public class RequestParamController {

    @RequestMapping("/request-param-v1")
    public void requestParamV1(HttpServletRequest req , HttpServletResponse resp) throws IOException, ServletException {
        String url = req.getRequestURI();
        log.info(url);

        String username = req.getParameter("username");
        int age = Integer.parseInt(req.getParameter("age"));
        log.info("RequestParamController.requestParamV1");
        log.info("username={},age={}",username,age);

        resp.getWriter().write("ok");
    }

    @RequestMapping("/request-param-v2")
    @ResponseBody
    public String requestParamV2(@RequestParam("username") String memberName ,
                                 @RequestParam("age") int memberAge)
    {
        log.info("RequestParamController.requestParamV2");
        log.info("username = {} , age = {}",memberName,memberAge);
        System.out.println(memberName.getClass().getName());
        return "ok";
    }

    @RequestMapping("/request-param-v3")
    @ResponseBody
    public String requestParamV3(@RequestParam String username,
                                 @RequestParam int age)
    {
        //@RequestParam의 name(value) 속성이 파라미터 이름으로 사용
        //변수명이 username이아니라 다른 이름이면
        //@RequestParam("username") String othername 이런식으로 바인딩
        log.info("RequestParamController.requestParamV3");
        log.info("username={} , age={}",username,age);

        return "ok";
    }

    @RequestMapping("/request-param-v4")
    @ResponseBody
    public String requestParamV4(String username , int age , boolean check , float f , Long l){
        //기본 자료형이면 @RequestParam도 생략 가능하다.
        log.info("username = {}",username);
        log.info("age = {}",age);
        log.info("boolean check = {}",check);
        log.info("float type = {}",f);
        log.info("Long type = {}",l);
        return "ok";
    }

    /**
     * required = true(null 허용X) , false(null 허용)
     * 주의!
     * ex ) ?username= <- 빈값으로 보내면 공백으로 처리됨
     * 타입변수가 int,float,long 등 기본형 타입은 required = false 시
     * null이 들어오면 에러 발생 , 500 Statecode
     * int -> Integer , long -> Long 등 참조형타입으로 바꿔주자
     */
    @ResponseBody
    @RequestMapping("/request-param-required")
    public String requestParamRequired(
            @RequestParam(required = true) String username,
            @RequestParam(required = false) Integer age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /**
     * defaultValue는 빈 문자의 경우에도 적용 
     * required는 의미가 없다 어차피 기본값 적용됨
     *   /request-param-default?username=
     * 위와 같이 빈문자로 올경우도 defaultValue값 적용
     */
    @ResponseBody
    @RequestMapping("/request-param-default")
    public String requestParamDefault(
            @RequestParam(required = true, defaultValue = "guest") String username,
            @RequestParam(required = false, defaultValue = "-1") int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /**
     * Map, MultiValueMap
     * Map(key=value)
     * MultiValueMap(key=[value1, value2, ...]) ex) (key=userIds, value=[id1, id2])
     * 하나의 key값에 여러 값을 받는게 가능
     * 단,Map일 경우 Value의 데이터타입은 Object or List<data type>으로 받아야함
     * 요청받는 데이터가 key값에 value가 여러개일 경우 MultiValueMap or Map으로 받는데 이때
     * Map에 Values Type은 LinkedHashMap이다.
     */
    @ResponseBody
    @RequestMapping("/request-param-map")
    public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
        log.info("username={}, age={}", paramMap.get("username"),
                paramMap.get("age"));
        System.out.println(paramMap.values().getClass().getName());
        return "ok";
    }
```

__HelloData.class__  
```java
//@Getter
//@Setter
//@EqualsAndHashCode : 참조형 비교 메서드 자동생성
//@RequiredArgsConstructor :  final 혹은 @NotNull이 붙은 필드의 생성자를 자동 생성
@Data
public class HelloData {
    private String username;
    private int age;
}
```
__RequestParamController.class (@ModelAttribute예제 추가)__  

```java
    /**
     * 스프링MVC는 @ModelAttribute 가 있으면 다음을 실행한다.
     * HelloData 객체를 생성
     * 요청 파라미터의 이름으로 HelloData 객체의 프로퍼티를 찾은 후
     * 해당 프로퍼티의 setter를 호출해서 파라미터의 값을 입력(바인딩) 한다.
     * 예) 파라미터 이름이 username 이면 setUsername() 메서드를 찾아서 호출하면서 값을 입력한다.
     * 프로퍼티
     * 객체에 getUsername() , setUsername() 메서드가 있으면, 이 객체는 username 이라는 프로퍼티를 가지고 있다.
     * username 프로퍼티의 값을 변경하면 setUsername()이 호출, 조회하면 getUsername()이 호출
     */
    @ResponseBody
    @RequestMapping("/model-attribute-v1")
    public String modelAttributeV1(@ModelAttribute HelloData helloData){

        log.info("username={} , age={}",helloData.getUsername(),helloData.getAge());

        return "ok";
    }

    /**
     * @RequestParam과 동일하게 @ModelAttribute도 생략 가능
     * String , int , Integer 같은 단순 타입은 @RequestParam
     * 그외 @ModelAttribute (argument resolver 이외 타입)
     */
    @ResponseBody
    @RequestMapping("/model-attribute-v2")
    public String modelAttributeV2(HelloData helloData){

        log.info("username={} , age={}",helloData.getUsername(),helloData.getAge());

        return "ok";
    }
}
```

__@RequestParam @ModelAttribute 간단 정리__  

- @RequestParam  
    - 데이터 조회  
    ex) @RequestParam("username") String name 일때   
        HttpServletRequest.getParameter("username")으로 조회하는 것과
        name으로 조회하는것이 똑같은 효과    
        @RequestParam 인자 값으로 파라미터 key값(or input태그 name값)이 일치해야함  
        변수명이 key값과 같은경우 @RequestParam String username 으로 생략가능  
        파라미터 이름만 있고 값이 없는 경우 빈문자로 통과  
        String, int 등의 단순 타입이면 @RequestParam 도 생략 가능  
    
    - required 옵션  
    ex) required = true 가 기본값이며 null을 허용안함  
        required = flase면 null 허용   
        => 이때 기본형타입은 null이 오면 에러이므로 참조형 타입으로 변경해주자.  
        (int -> Integer , long -> Long 등..) OR defaultValue 사용  
    
    - defaultValue 옵션  
    ex) null값이나 빈문자열이 오면 해당 설정값으로 입력됨  

    - 여러 데이터 받을 시 (Map, MultiValueMap)  
    ex) @RequestParam Map<String,Object> or MiltiValueMap<String,Object>
        Map의 values의 타입은 LinkedHashMap으로 반환된다.  

- @ModelAttribute
    - 객체 생성  
        컨트롤러에서 요청데이터를 받을 때 해당 객체를 생성  
    - 요청 파라미터로 프로퍼티 조회  
        만약 요청 파라미터가 `username`으로 오면 HelloData.`setUsername`()을 찾아서 파라미터 값을 입력한다.  
        ex) http://localhost:8096/model-attribute-v1?username=test&age=10  
            출력 : username=test , age = 10  
            http://localhost:8096/model-attribute-v1?`membername`=test&age=10  
            출력 : username=null , age = 10  
        즉, 객체를 생성해도 getter setter가 없으면 객체에 값이 안들어간다.  
        또 getter setter 메서드를 어노테이션말고 밑에 코드처럼 직접 작성 시에 잘못입력을 해도 값이 안들어가니 조심하자.  
        ```java
        public void setUsernameX(String username) {
        this.username = username;
        }

        public void setAgeX(int age) {
            this.age = age;
        }
        ```

        요약 하자면 객체생성부터 파라미터로 전달받은 값까지 입력하는 과정이 아래코드이고 해당 과정을
        ```java
        public String modelAttributeV1(@RequestParam String username , @RequestParam int age){
            HelloData helloData = new HelloData();
            helloData.setUsername(username);
            helloData.setAge(age);
        }
        ```

        @ModelAttribute로 줄여진 것이다.
        
        ```java
        public String modelAttributeV1(@ModelAttribute HelloData helloData)
        ```
          
    - @ModelAttribute 생략
        modelAttributeV2 메서드처럼 String , int , long 등 단순타입 외 나머지 타입은 @ModelAttribute이고 생략이 가능하다.  

## HTTP 요청 메시지 - 단순 텍스트

__HTTP 요청 메세지란?__  
- HTTP message body에 데이터를 직접 담아서 요청
- HTTP API에서 주로 사용, JSON, XML, TEXT
- 데이터 형식은 주로 JSON 사용
- POST, PUT, PATCH

HTTP 메시지 바디를 통해 데이터가 직접 넘어오는 경우는 @RequestParam , @ModelAttribute를 사용할 수 없다. (HTTP FORM 형식 제외)  

HTTP 메시지 바디의 데이터를 InputStream 을 사용해서 직접 읽을 수 있다!!  

__RequestBodyStringController.class__

```java
@PostMapping("/request-body-string-v1")
public void requestBodyString(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ServletInputStream inputStream = request.getInputStream();
    String messageBody = StreamUtils.copyToString(inputStream,
            StandardCharsets.UTF_8);
    log.info("messageBody={}", messageBody);
    response.getWriter().write("ok");
}
```
request객체에서 바이트기반 스트림을 가져와서 inputStream을 생성하고,  
StreamUtils.copyToString으로 inputStream을 인코딩을 UTF-8로 적용하여 String으로 변환해주면서 messageBody를 읽는다.  

```java
@PostMapping("/request-body-string-v2")
//@ResponseBody
public void requestBodyStringV2(InputStream inputStream, Writer responseWriter) throws IOException{
    String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    //InputStream만 파라미터로 받을 경우 해당메서드가 void로 반환되기 때문에 뷰가 PostMapping 주소의 이름인 템플릿이 있어야한다.
    //그러므로 @ResponseBody를 붙여주거나 request-body-string-v2.html or jsp 등등 파일이 존재해야함
    log.info("messageBody={}", messageBody);
    responseWriter.write("ok");
}
```
스프링은 여러 파라미터를 받을 수 있는데 (`Agrument Resolver 덕에`)  
InputStream과 Writer도 받을 수 있어서 코드가 더 간결해진 것을 볼 수있다.  

```java
@PostMapping("/request-body-string-v3")
public HttpEntity<String> requestBodyStringV3(HttpEntity<String> httpEntity) {
    String messageBody = httpEntity.getBody();
    log.info("messageBody={}", messageBody);

    /*
    MultiValueMap headers = new LinkedMultiValueMap();
    headers.add("Test","mode");
    =
    HttpHeaders headers = new Httpheaders(); //MultiValueMap을 상속받음 헤더를 설정하는 기능이 추가
    headers.set("Test","mode");
    
    HttpEntity entity = new HttpEntity<>("ok",headers);
    //ResponseEntity는 HttpEntity 상속받은 객체인데 상태코드를 추가하는등 다른 부가적인 기능이 추가되어있다.
    HttpEntity entity2 = new ResponseEntity("ok", headers, HttpStatus.CREATED);
    return entity;
        */

    return new HttpEntity<>("ok");
}
```

HttpEntity는 http 헤더와 바디정보를 조회하거나 설정할 때 쓰이는 객체다.  
getBody() 메서드로 간편하게 body정보를 조회할 수 있고, 응답 설정도 전송할 body뿐만아니라 상태코드와 헤더도 설정할 수 있다.(`코드 내 주석참고`)

```java
@PostMapping("/request-body-string-v4")
@ResponseBody
public String reqeustBodyStringV4(@RequestBody String body, @RequestHeader HttpHeaders headers){
    String messageBody = body;
    headers.forEach((k,v)->log.info("header name = {} header value = {}",k,v));
    log.info("messageBody={}", messageBody);
    return "ok";
}
```

@RequestBody 를 사용하면 HTTP 메시지 바디 정보를 편리하게 조회할 수 있다. 참고로 헤더 정보가 필요하다면 HttpEntity 를 사용하거나 @RequestHeader 를 사용하면 된다.  

@ResponseBody 를 사용하면 응답 결과를 HTTP 메시지 바디에 직접 담아서 전달할 수 있다.  
그리고 HttpEntity(ResponseEntity ReqeustEntity도 포함)와 HttpServletResponse, Writer(OutStream) 등을 이용하면 view(viewResolver)를 사용하지 않는다.  
view를 사용하지 않는다는 말은 문자열을 통해 viewresolver로 템플릿파일을 리턴을 하지 않는다는 말이다.  

참고로 메소드 반환타입이 void일 경우 Mapping("path") path값의 이름을 ViewResolver로 넘기게 된다.  
ex) void 시 반환 페이지 확인
templates폴더에 voidPage.html을 생성 후 /voidPage로 Get 요청을 해보자!
```java
@GetMapping("/voidPage")
public void voidPage(){
    log.info("반환타입이 void라면?? 매핑된 경로명을 viewresolver에게 전달!");
}
```

## HTTP 요청 메시지 - JSON

HTTP API에서 주로 사용하는 JSON 데이터 형식을 조회해보자!  

```java
@Slf4j
@Controller
public class RequestBodyJsonController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("request-body-json-v1")
    public void requestBodyJsonV1(HttpServletRequest request , HttpServletResponse response) throws IOException{
        ServletInputStream inputStream = request.getInputStream();
        String messagebody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        log.info("messagebody = {}",messagebody);
        HelloData helloData = objectMapper.readValue(inputStream, HelloData.class);
        //스트림을 바로 넘겨도 자바객체로 변환해준다.
        //HelloData helloData = objectMapper.readValue(inputStream, HelloData.class);

        log.info("json body = {}",helloData);
        response.getWriter().write("ok");
    
        .
        .
        .
    }
}
```

아까 단순 텍스트기반의 요청데이터를 받을 때 바이트기반 스트림을 받아와서 StreamUtils를 이용하여 문자열로 변환했다.  
그 후 JSON 형식의 문자열 데이터를 objectMapper를 사용해서 자바 객체로 변환한다.  

```java
@PostMapping("request-body-json-v2")
@ResponseBody
public String requestBodyJsonV2(@RequestBody String messagebody) throws JsonProcessingException {
    HelloData helloData = objectMapper.readValue(messagebody, HelloData.class);

    log.info("json body = {}",helloData);

    return "ok";
}
/**
 * @RequestBody 생략 불가능(@ModelAttribute 가 적용되어 버림)
 * HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter 
 * (contenttype : application/json)
 * 
 */
@PostMapping("request-body-json-v3")
@ResponseBody
public String requestBodyJsonV3(@RequestBody HelloData helloData){
    log.info("json body = {}",helloData);

    return "ok";
}

@ResponseBody
    @PostMapping("/request-body-json-v4")
    public String requestBodyJsonV4(HttpEntity<HelloData> httpEntity) {
        HelloData data = httpEntity.getBody();
        log.info("username={}, age={}", data.getUsername(), data.getAge());
        return "ok";
    }
```

단순 텍스트를 요청 시 처리할때 처럼 @RequestBody 어노테이션을 이용하여
String으로 요청데이터를 받을 수도 있고 객체 , HttpEntity 로도 가능하다.  

```java
/**
 * 반환 타입이 HelloData객체면 반환시 HTTP메세지 컨버터가  객체를 Json으로 변경하여 응답한다.
 */
@ResponseBody
@PostMapping("/request-body-json-v5")
public HelloData requestBodyJsonV5(@RequestBody HelloData data) {
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return data;
}
```

반환타입을 HelloData 객체로 하면 해당 객체를 Json으로 변경하여 바디에 담아서 응답한다.  
`request-body-json-v3` 위의 주석에도 써놓았지만 @ReqeustBody는 생략을 하게되면 @ModelAttribute가 적용된다.  
그래서 요청파라미터를 처리하게 되고 객체 생성 시에 요청 메세지 바디의 데이터값은 안들어오게 된다.  

## HTTP 응답 - 정적 리소스, 뷰 템플릿

응답 데이터는 이미 앞에서 일부 다룬 내용들이지만, 응답 부분에 초점을 맞추어서 정리해보자.  
스프링에서 응답 데이터를 만드는 방법 3가지
- 정적 리소스  
    예) 웹 브라우저에 정적인 HTML, css, js를 제공할 때는, 정적 리소스를 사용한다.  
- 뷰 템플릿 사용
    예) 웹 브라우저에 동적인 HTML을 제공할 때는 뷰 템플릿을 사용한다.  
- HTTP 메시지 사용  
    HTTP API를 제공하는 경우에는 HTML이 아니라 데이터를 전달해야 하므로, HTTP 메시지 바디에 JSON 같은 형식으로 데이터를 실어 보낸다.  

### 정적 리소스
스프링 부트는 클래스패스의 다음 디렉토리에 있는 정적 리소스를 제공
> /static , /public , /resources , /META-INF/resources
`src/main/resources`는 리소스를 보관하는 곳이고, 또 클래스패스의 시작 경로이다.  
해당 디렉토리에 리소스를 넣어두면 스프링 부트가 정적 리소스로 서비스를 제공  

정적 리소스 경로 : src/main/resources/static

파일 경로 : src/main/resources/static/basic/hello-form.html

실행 : http://localhost:8080/basic/hello-form.html

__정적 리소스는 해당 파일을 변경 없이 그대로 서비스하는 것!!__  

### 뷰 템플릿
뷰 템플릿을 거쳐서 HTML이 생성되고, 뷰가 응답을 만들어서 전달
일반적으로 HTML을 동적으로 생성하는 용도로 사용하고, 다른 편의기능인 자바스크립트에 타임리프 문법도 넣을 수 있고, 공통된 부분을 하나의 템플릿으로 만들어 사용할 수도 있다.  


__뷰 템플릿 경로__ : src/main/resources/templates

__뷰 템플릿 생성__  
hello.html

경로 : src/main/resources/templates/response

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<p th:text="${data}">empty</p>
</body>
</html>
```

타임리프 기능을 사용하기 위해선 html 태그에  
`xmlns:th="http://www.thymeleaf.org"`를 추가해줘야 한다.  

타임리프 스프링 부트 설정  

다음 라이브러리를 추가하면  
build.gradle파일에  
`implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'`  
스프링 부트가 자동으로 ThymeleafViewResolver 와 필요한 스프링 빈들을 등록한다.  

application,properties  
```text
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```
이 설정은 기본값(그대로 쓸경우 안써도 됨) 이기 때문에 변경이 필요할 때만 설정하면 된다.  

__ResponseViewController.class__  
```java
@Controller
public class ResponseViewController {
    @RequestMapping("/response-view-v1")
    public ModelAndView responseViewV1() {
        ModelAndView mav = new ModelAndView("response/hello")
                .addObject("data", "hello!");
        return mav;
    }

    @RequestMapping("/response-view-v2")
    public String responseViewV2(Model model) {
        model.addAttribute("data", "hello!!");
        return "response/hello";
    }

    @RequestMapping("/response/hello")
    public void responseViewV3(Model model) {
        model.addAttribute("data", "hello!!");
    }
}
```
responseViewV1은 ModelAndView를 만들어서 반환하고 있다.  
responseViewV2는 model을 주입받아 model에 데이터를 추가 후 hello.html의 논리적 뷰 이름을 문자열로 반환한다.  
responseViewV3는 반환타입이 void인데 이러면 매핑된 주소가 viewresolver에게 논리적 뷰이름으로 전달한다.  

## HTTP 응답 - HTTP API, 메시지 바디에 직접 입력

HTTP API를 제공하는 경우에는 데이터를 전달해야 하므로, HTTP 메시지 바디에 JSON,xml 등의 형식으로 데이터를 실어 보낸다.  

__ResponseBodyController.class__  
```java
@Controller
@ResponseBody
//@RestController //@Controller + @ResponseBody
public class ResponseBodyController {
    @GetMapping("/response-body-string-v1")
    public void responseBodyStringV1(HttpServletResponse response) throws IOException {
        response.getWriter().write("ok");
    }

    /**
     * HttpEntity or ResponseEntity ("body message","HttpSattus code")
     * @return
     */
    @GetMapping("/response-body-string-v2")
    public ResponseEntity<String> responseBodyStringV2(){
        ResponseEntity<String> message = new ResponseEntity<>("ok", HttpStatus.OK);
        return message;
    }

    @GetMapping("/response-body-string-v3")
    @ResponseBody
    public String responseBodyStringV3(){
        return "ok";
    }

    @GetMapping("/response-body-json-v1")
    public ResponseEntity<HelloData> responseBodyJsonV1() {
        HelloData helloData = new HelloData();
        helloData.setUsername("userA");
        helloData.setAge(20);
        return new ResponseEntity<>(helloData, HttpStatus.OK);
    }
    
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/response-body-json-v2")
    public HelloData responseBodyJsonV2() {
        HelloData helloData = new HelloData();
        helloData.setUsername("userA");
        helloData.setAge(20);
        return helloData;
    }
}
```

responseBodyStringV1 : response객체를 통해 응답  

responseBodyStringV2 : HttpEntity or ResponseEntity를 생성하여 응답  

responseBodyStringV3 : @ResponseBody 어노테이션을 붙이고 String을 반환하면 해당 문자열로 응답됨  

responseBodyJsonV1 : HttpEntity or ResponseEntity를 통해 JSON 객체로 응답  

responseBodyJsonV2 : @ResponseBody 어노테이션을 활용해 HelloData를 반환하고 추가적으로 @ResponseStatus을 이용해 상태코드도 설정 가능하다.  
단, 로직(조건)에따라 상태코드를 부여하고 싶을때는 ResponseEntity를 사용하자.  

❗ 참고 : HttpEntity로 응답 시 상태코드는 설정이 까다로우므로 상태코드를 설정하려면 HttpEntity를 확장한 ResponseEntity를 사용하자.  

__@RestController__ : @Controller 와 @ResponseBody를 같이 쓴 효과를 보이는데 @ResponseBody를 클래스레벨이 붙이면 해당 클래스안에 메소드들은 @ResponseBody가 적용되는 효과가 있다.  

## HTTP 메시지 컨버터

앞서 했던 메세지 바디에 내용을 읽어오거나 응답할때에 바이트기반 스트림을 String으로 변환해서 읽어오는 동작을 어노테이션인 @RequestBody , @ResponseBody등을 이용해서 스프링에서 처리해줫는데 해당 기능을 HTTP 메세지 컨버터가 수행한다.  
원리를 자세히 알아보자.  



### HTTP 메시지 컨버터 인터페이스

다음 코드는 스프링프레임워크의 HttpMessageConverter 인터페이스를 나타낸다.  
```java
package org.springframework.http.converter;

    public interface HttpMessageConverter<T> {
    
      boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);
      
      boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);
      
      List<MediaType> getSupportedMediaTypes();
      
      T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException;
              
      void write(T t, @Nullable MediaType contentType, HttpOutputMessag outputMessage) throws IOException, HttpMessageNotWritableException;
              
}
```

HTTP 메시지 컨버터는 HTTP 요청, HTTP 응답 둘 다 사용된다.  

canRead(), canWrite() : 메시지 컨버터가 해당 클래스, 미디어타입을 지원하는지 체크  

read(), write() : 메시지 컨버터를 통해서 메시지를 읽고 쓰는 기능  

__스프링 부트 기본 메시지 컨버터__  
0 = ByteArrayHttpMessageConverter  
1 = StringHttpMessageConverter  
2 = MappingJackson2HttpMessageConverter  
 > 숫자가 우선순위로 루프가 돈다. ( canRead() , canWrite() )
요청이나 응답이 들어왔을 때 클래스 타입과 미디어 타입 둘을 체크해서 사용 여부를 결정  
조건이 맞는 컨버터를 조회 후 사용  

ByteArrayHttpMessageConverter : byte[] 데이터  
클래스 타입 : byte[], 미디어타입 : */*  
요청 예)@RequestBody byte[] data  
응답 예)ReseponseBody return byte[] 쓰기 미디어타입 application/octet-stream   

StringHttpMessageConverter : String 데이터 처리  
클래스 타입 : String, 미디어타입 : */*  
요청 예)@RequestBody String data  
응답 예)ReseponseBody return "ok" 쓰기 미디어타입 text/plain  

MappingJackson2HttpMessageConverter : application/json 처리  
클래스 타입 : 객체 또는 HashMap, 미디어타입 : application/json 관련  
요청 예)@RequestBody Object data  
응답 예)ReseponseBody return data 쓰기 미디어타입 application/json 관련  


__canRead() 동작과정__  
content-type: application/json 으로 요청  
```java
@RequestMapping
void hello(@RequetsBody `String` data) {} //클래스 타입은 String
```
=> StringHttpMessageConverter read() 호출  

content-type: application/json 요청  
```java
@RequestMapping
void hello(@RequetsBody HelloData data) {} // 객체 타입
```
=> MappingJackson2HttpMessageConverter read() 호출  

content-type: text/html  
```java
@RequestMapping
void hello(@RequetsBody HelloData data) {} // 객체 타입
```
객체 타입이니까 바이트와 스트링 컨버터 안됨  
매핑잭슨2는 가능 but 컨텐트타입이 json이 아님  
=> 호출 가능한 http 메시지 컨버터가 없음 ❗에러  

__canWrite()__  
`canWrite()`는 content-type이 아니라 클라이언트가 응답받을 미디어타입을 지원해야 하니까 Accept를 확인한다.  
이때 전 예제에서 produces를 설정했던 예제를 떠올려보자.   
그때 produces에 설정된 미디어타입을 지원안하면 에러가 났는데  
해당 canWrite() 여부를 결정할때 요청 시 Accept가 json을 지원한다해도 `produces값을 먼저 확인`하기 때문에 에러가난다.  
__참고 코드__  
Accept : */* 인데도 불구하고 에러가 남  
```java
@ResponseBody
@GetMapping(path = "/response-body-json-v2",produces = "text/plain")
public HelloData responseBodyJsonV2() {
    HelloData helloData = new HelloData();
    helloData.setUsername("userA");
    helloData.setAge(20);
    return helloData;
}
```
<br>
<br>
HTTP 메시지 컨버터가 어떤 과정을 걸치면서 반환타입이 변환 되는지 알게 되었다.

HTTP 메세지 컨버터가 동작하는 시점은 핸들러 어댑터에서 핸들러를 호출하기 전에 동작한다.  
그림을 통해 알아보자.  
![image](https://github.com/9ony/9ony/assets/97019540/622b1451-a1c2-4156-ad59-ff6de0e9aae7)

핸들러 어댑터가 컨트롤러를 호출하기 전에 ArgumentResolver를 호출하고 컨트롤러에서 핸들러 어댑터로 반환하기 전에 ReturnValueHandler가 호출되는 것을 볼 수 있다.  
해당 과정에서 메세지 바디에 있는 내용을 가져올 때 메세지 컨버터가 작동하여 객체를 생성해서 가져올 수 있거나 객체를 응답 메세지로 변환할 수 있는지 체크를 한다.  
@RequestBody를 처리하는 `RequestResponseBodyMethodProcessor`는 `AbstractMessageConverterMethodArgumentResolver`와 상속관계를 가지는데 해당 메서드의  
```java
protected <T> Object readWithMessageConverters(HttpInputMessage inputMessage, MethodParameter parameter,Type targetType)  
```
코드의 일부를 보면
```java
for (HttpMessageConverter<?> converter : this.messageConverters) {
    Class<HttpMessageConverter<?>> converterType = (Class<HttpMessageConverter<?>>) converter.getClass();
    GenericHttpMessageConverter<?> genericConverter =
            (converter instanceof GenericHttpMessageConverter ? (GenericHttpMessageConverter<?>) converter : null);
    if (genericConverter != null ? genericConverter.canRead(targetType, contextClass, contentType) :
            (targetClass != null && converter.canRead(targetClass, contentType))) {
        if (message.hasBody()) {
            HttpInputMessage msgToUse =
                    getAdvice().beforeBodyRead(message, parameter, targetType, converterType);
            body = (genericConverter != null ? genericConverter.read(targetType, contextClass, msgToUse) :
                    ((HttpMessageConverter<T>) converter).read(targetClass, msgToUse));
            body = getAdvice().afterBodyRead(body, msgToUse, parameter, targetType, converterType);
        }
        else {
            body = getAdvice().handleEmptyBody(null, message, parameter, targetType, converterType);
        }
        break;
    }
}
```
canRead를 통해 콘텐트 타입과 해당 타겟타입을 비교하는 것을 볼수있다.  

즉,요청의 경우  
@RequestBody 를 처리하는 ArgumentResolver 가 있고, HttpEntity 를 처리하는 ArgumentResolver 가 있다. 이 ArgumentResolver 들이 HTTP 메시지 컨버터를 사용해서 필요한 객체를 생성하는 것이다.

응답의 경우  
@ResponseBody 와 HttpEntity 를 처리하는 ReturnValueHandler 가 있다. 그리고 여기에서 HTTP 메시지 컨버터를 호출해서 응답 결과를 만든다.


__ArgumentResolver(HandlerMethodArgumentResolver)__  
컨트롤러의 요청 파라미터로 어떤 타입들이 올 수 있는지 다시 생각해보면 매우 다양한 파라미터를 사용할 수 있다. HttpServeletRequest, Model, @RequestParam, @ModelAttribute , @RequestBody 등의 어노테이션 및 HttpEntity같은 HTTP 메시지를 처리하는 부분까지 매우 큰 유연함을 가지고 있다.

애노테이션 기반 컨트롤러를 처리하는 RequestMappingHandlerAdaptor 는 바로 이 ArgumentResolver 를 호출해서 컨트롤러(핸들러)가 필요로 하는 다양한 파라미터의 값(객체)을 생성한다.  
그리고 이렇게 파리미터의 값이 모두 준비되면 컨트롤러를 호출하면서 값을 넘겨준다.

스프링은 30개가 넘는 ArgumentResolver를 기본으로 제공한다.
[ArgumentResolver 공식문서](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-annarguments)
```java
public interface HandlerMethodArgumentResolver {

      boolean supportsParameter(MethodParameter parameter);
      
      @Nullable
      Object resolveArgument(MethodParameter parameter, @NullableModelAndViewContainer mavContainer,
              NativeWebRequest webRequest, @Nullable WebDataBinderFactorybinderFactory) throws Exception;
              
}
```
다음 코드를 보면, ArgumentResolver가 어떻게 동작하는지 알 수 있다.  

supportsParameter()를 호출해서 해당 파라미터를 지원하는지 체크한다.  

지원하면 resolveArgument()를 호출해서 실제 객체를 생성한다. 그리고 이렇게 생성된 객체가 컨트롤러 호출 시 넘어가는 것이다.  

그리고 원한다면 직접 이 인터페이스를 확장해서 원하는 ArgumentResolver를 만들 수 있다.  

__ReturnValueHandler__    
요청시에 ArgumentResolver를 사용한다면 응답시에는 ReturnValueHandler(HandlerMethodReturnValueHandler)가 사용된다.

컨트롤러에서 String으로 뷰 이름을 반환해도, 동작하는 이유가 바로 ReturnValueHandler 덕분이다.  

스프링은 10여개가 넘는 ReturnValueHandler 를 지원한다.  
[ReturnValueHandler 공식문서](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-annreturn-types)

예) ModelAndView , @ResponseBody , HttpEntity , String



스프링 MVC는
@RequestBody,@ResponseBody가 있으면 `RequestResponseBodyMethodProcessor` (ArgumentResolver)
HttpEntity가 있으면 `HttpEntityMethodProcessor` (ArgumentResolver)를 사용한다.

__확장__  
스프링은 다음을 모두 인터페이스로 제공한다. 따라서 필요하면 언제든지 기능을 확장할 수 있다.
- HandlerMethodArgumentResolver
- HandlerMethodReturnValueHandler
- HttpMessageConverter

스프링이 필요한 대부분의 기능을 제공하기 때문에 실제 기능을 확장할 일이 많지는 않다.  
기능 확장은 WebMvcConfigurer 를 상속 받아서 스프링 빈으로 등록하면 된다.  

```java
@Bean
public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
        
         @Override
         public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
			//...
         }
 	 @Override
         public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
			//...
         } 
    };
}
```