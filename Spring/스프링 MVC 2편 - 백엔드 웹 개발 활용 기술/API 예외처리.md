# API 예외처리

예외처리를 할때 상태코드에 따라 오류페이지를 보여주는 방식은 뷰를 미리 만들어 놓고 해당 페이지를 반환하면 된다.  
하지만 API일 경우 단순히 프론트에서 요청이 와서 예외가 일어났다면 오류화면 구성을 위한 데이터를 보내주면 되지만,  
만약 기업간의 통신 또는 하나의 서비스안에서 여러 애플리케이션끼리(MSA)도 API 통신을 통해 데이터를 교환 할수도 있는데,  
이때 해당 API요청에 별로 상세한 오류코드나, 해당 오류에 대처하기 위한 데이터들이 필요할 것이다.  
그리고 각각의 메서드마다 API오류 코드도 상세히 다뤄줘야 한다. 어떤것 때문에 api요청이 안됬는지 등..  
즉, API는 각 오류 상황에 맞는 오류 응답 스펙을 정하고, JSON으로 데이터를 내려주어야 한다.  


## API 예외 처리 - 서블릿

우선 서블릿 에러페이지를 등록하자.  
- WebServerFactoryCustomizer 구현 (이전 예제와 동일)
    ```java
    @Component
    public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");
        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page/500");
        factory.addErrorPages(errorPage404, errorPage500, errorPageEx);
    }
    ```
- ApiExceptionController 추가  
    요청 시 멤버의 아이디와 이름을 응답하고 경로변수가 ex일경우 런타임에러가 발생
    ```java
    @RestController
    @RequestMapping(("/api"))
    public class ApiExceptionCotroller {

        @GetMapping("/members/{id}")
        public MemberDto getMember(@PathVariable String id){

            if(id.equals("ex")){
                throw new RuntimeException("잘못된 사용자 입니다.");
            }

            return new MemberDto(id,"kim"+id);
        }
    }
    ```

- PostMan 호출 결과
    - 정상 호출  
    ![image](https://github.com/9ony/9ony/assets/97019540/9ca79d8d-9719-49b5-943a-9df17cea8489)

    - ex로 호출  
    ![image](https://github.com/9ony/9ony/assets/97019540/ce137545-c5cb-44ee-8fbf-c3f52945e98d)

    정상호출을 우리가 예상하는대로 왔다. 하지만 에러가 발생할 경우엔 WAS에서 해당 에러를 감지 후 `RunTimeException`이기 때문에 위 customizer에 한 설정대로 error-page/500으로 요청할 것이다.  

- error-page/500 매핑된 컨트롤러

    ```java
    @Controller
    @RequestMapping("/error-page")
    public class ErrorPageController {
    //...
        @GetMapping("/500")
        public String errorPage500(HttpServletRequest request, HttpServletResponse response) {
            log.info("errorPage 500");
            return "error-page/500";
        }
    //...
    }
    ```
    
    해당 error-page/500 매핑된 컨트롤러의 메서드는 페이지를 반환하고 있다.  
    우리가 원하는건 API요청이기 때문에 JSON형식으로 받아야 한다.  

- error-page/500 API 응답 추가

    ```java
    @RequestMapping(value = "/500",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> errorPage500Api(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 500");
        Map<String, Object> result = new HashMap<>();
        Exception ex = (Exception) request.getAttribute(ERROR_EXCEPTION);
        result.put("status", request.getAttribute(ERROR_STATUS_CODE));
        result.put("message", ex.getMessage());
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return new ResponseEntity<>(result,HttpStatus.valueOf(statusCode));
        // 원하는 상태코드로 변경가능
    }
    ```

    `produces`로 클라이언트 Accept헤더가 Json일 경우 해당 메서드가 동작한다.  
    ResponseEntity를 사용하여 HTTP 상태 코드를 함께 전달해주자.   
    @ResponseBody를 사용하거나 @RestController 어노테이션을 추가하여 Map으로 보내도된다.  
    하지만 이럴경우 상태코드를 추가하는게 힘들다.  

    - 결과  
        ![image](https://github.com/9ony/9ony/assets/97019540/b5c470ad-f37e-4ef1-8bcb-9ace4f4a1efa)


## API 예외 처리 - 스프링 부트

### BasicErrorController API예외 처리  

API 예외처리도 스프링부트가 제공하는 기본 BasicErrorController가 API 예외처리를 해준다.  
테스트하기전에 서블릿 에러페이지 설정인 `WebServerCustomizer` 빈 등록을 해제해주자!!  

- PostMan 테스트
    요청 시 Accpet : application/json 설정 필수!!  
    ![image](https://github.com/9ony/9ony/assets/97019540/2959eef1-2d00-4733-b6d9-937034e42876)

    - application.properties 설정 값
        ```text
        server.error.include-exception=true
        server.error.include-message=always
        server.error.include-stacktrace=never
        server.error.include-binding-errors=always
        ```

    - BasicErrorController 기본 경로 : /error
        application.properties에 아래처럼 기본경로 변경가능  
        - application.properties  
            ```text
            server.error.path=/error-basic
            ```
        - BasicErrorController Mapping값  
            ```java
            //설정값이 있으면 설정한 에러 경로 : 아니면 /error
            @RequestMapping("${server.error.path:${error.path:/error}}")
            public class BasicErrorController extends AbstractErrorController {
            ```

### Html 페이지 vs API 오류  

- BasicErrorController : HTML 화면을 처리할 때 주로 사용
- @ExceptionHandler: API 오류 처리는 사용

BasicErrorController를 확장 시 기본 JSON메시지도 변경 가능하다.  
하지만 API오류는 @ExceptionHandler가 더 많은 기능을 제공한다.  
API 마다, 각각의 컨트롤러나 예외마다 서로 다른 응답 결과를 출력해야 할 수도 있다.  
예를 들어서 회원과 관련된 API에서 예외가 발생할 때 응답과, 상품과 관련된 API에서 발생하는 예외에 따라 그 결과가 달라질 수 있다.  
즉 API 오류 처리는 결과적으로 매우 세밀하고 복잡하다.

## HandlerExceptionResolver 

예외가 발생하면 서블릿을 넘어 WAS까지 가게되면 500에러가 발생한다.  
해당 에러를 500에러코드가 아닌 다른 에러코드로 처리하고 싶을때 해당 리졸버를 사용한다.  

### HandlerExceptionResolver 동작과정

- HandlerExceptionResolver 적용 전  
    ![image](https://github.com/9ony/9ony/assets/97019540/169a2357-9b19-4f35-8e43-95abfce96042)

- HandlerExceptionResolver 적용 후  
    ![image](https://github.com/9ony/9ony/assets/97019540/c661db8a-9030-44cc-8502-94c9df19f28b)

HandlerExceptionResolver 적용 전과 후를 비교해보자.  
ExceptionResovler를 적용하기 전에는 에러가 발생하면 해당 에러가 WAS까지 전송되어서
WAS에서 예외가 터진것을 감지한 후 해당 예외에 대한 에러페이지를 재요청한다.  
하지만 적용 후에는 HandlerExceptionResolver가 컨트롤러에서 발생한 예외를 받아서 처리한 후 WAS는 정상동작으로 인지한다.   
하지만 sendError로 상태코드 400을 보냈기 때문에 해당 에러에 대한 응답을 해야되서 재요청을 하는건 같다.  

### HandlerExceptionResovler 예제

위 동작을 코드를 통해 알아보자!

- MyHandlerExceptionResolver,MvTestHandlerExceptionResolver 생성  
    - MyHandlerExceptionResolver
        ```java
        @Slf4j
        public class MyHandlerExceptionResolver implements HandlerExceptionResolver {
            @Override
            public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

                log.info("call resovler = ", ex);

                try {
                    if (ex instanceof IllegalArgumentException) {
                        log.info("IllegalArgumentException resolver to 400");
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                ex.getMessage());
                        return new ModelAndView();
                    }
                } catch (IOException e) {
                    log.error("resolver ex", e);
                }
                return null;
            }
        }
        ```
    - MvTestHandlerExceptionResolver
        ```java
        @Slf4j
        public class MvTestHandlerExceptionResolver implements HandlerExceptionResolver {
            @Override
            public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                log.info("MvTestHandlerExceptionResolver 호출됨");
                try {
                    if (ex instanceof MyException) {
                        log.info("MyException resolver to ModelAndView");
                        //response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,ex.getMessage());
                        //sendError를 같이 설정 시 WAS에서 상태코드가 200이 아니므로 ErrorPage 재요청을 해버림 우리가 원하던 ModelAndView가 의미가 없어진다
                        Map<String, Object> map = new HashMap<>();
                        map.put("test", "MyException");
                        return new ModelAndView("returnModelAndView", map);
                    }
                }catch (Exception e){
                    log.error("MvTestHandlerExceptionResolver Exception",e);
                }
                return null;
            }
        }
        ```
    - UserHandlerExceptionResolver
        ```java
        @Slf4j
        public class UserHandlerExceptionResolver implements HandlerExceptionResolver {
            private final ObjectMapper objectMapper = new ObjectMapper(); //자바객체를 json으로 직렬화
            @Override
            public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

                try {
                    if (ex instanceof UserException) {
                        log.info("UserException resolver to 400");
                        String acceptHeader = request.getHeader("accept");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        if ("application/json".equals(acceptHeader)) {
                            Map<String, Object> errorResult = new HashMap<>();
                            errorResult.put("ex", ex.getClass());
                            errorResult.put("message", ex.getMessage());
                            String result = objectMapper.writeValueAsString(errorResult);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("utf-8");
                            response.getWriter().write(result);
                            return new ModelAndView();
                        } else {
                            return new ModelAndView("error/500");
                        }
                    }
                } catch (IOException e) {
                    log.error("resolver ex", e);
                }
                return null;
            }
        }
        ```

- WebConfig에 ExceptionResolver 추가

    `ExceptionResolver 추가 시` 아래 두 가지 메서드를 오버라이드하여 추가할 수 있는데,  
    각각 차이가 있다.  

    - extendHandlerExceptionResolvers : 기본 리졸버에 추가

        ```java
        @Override
        public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
            resolvers.add(new MyHandlerExceptionResolver());
        }
        ```

        ![image](https://github.com/9ony/9ony/assets/97019540/af90f5f4-2a57-4543-a133-c67e5f0defdc)

        > 3가지의 기본 ExceptionResolver는 아래에서 다루겠습니다.

    - configureHandlerExceptionResolvers : 기존 리졸버를 제외한 후 해당 메서드에서 추가된 리졸버만 추가됨
            
        ExceptionReolver를 추가할때 configureHandlerExceptionResolvers를 사용하게 될 경우  
        기존에 있는 Resolver는 제외되고 해당메서드에서 추가된 ExceptionResolver만 추가된다.

        ```java
        @Override
        public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
            //configureHandlerExceptionResolvers로 예외처리핸들러를 등록하면 기본핸들러들은 제외된다.
            //즉, 해당메서드로 추가된 핸들러만 남아있다. 기본핸들러를 제외하고 커스텀한 핸들러를 등록해야 할때 사용하자.
            resolvers.add(new MyHandlerExceptionResolver());
            resolvers.add(new UserHandlerExceptionResolver());
        }
        ```

        ![image](https://github.com/9ony/9ony/assets/97019540/bdc852e3-9d70-4c95-8d4e-ab927507242f)

- ApiExceptionCotroller 수정
    ```java
    @GetMapping("/members/{id}")
    public MemberDto getMember(@PathVariable String id){
    //...
        if(id.equals("bad")){
            throw new IllegalArgumentException("잘못된 입력 입니다");
        }
        if(id.equals("mvTest")){
            throw new MyException("TEST : return ModelAndView");
        }if(id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }
    //...
    }
    ```
    경로변수가 bad일때 IllegalArgumentException가 발생하는 코드를 추가해주자   
    \+ ModelAndView 반환 시 동작도 알아보자


- __테스트 결과__

    - api/members/bad 요청

        ![image](https://github.com/9ony/9ony/assets/97019540/d6187592-ed7e-42ab-8176-ec578ae2e1dc)
        
        __콘솔 로그__  

        ```text
        INFO  16844 --- [nio-8096-exec-2] hello.exception.filter.LogFilter         : [ Filter ] REQUEST [70943934-122c-4413-9ffd-3594ea5b1050][REQUEST][/api/members/bad]
        INFO  16844 --- [nio-8096-exec-2] h.e.interceptor.LogInteceptor            : [Interceptor] REQUEST [da955186-34b1-42b2-bf80-3e8035b37c7f][REQUEST][/api/members/bad][class org.springframework.web.method.HandlerMethod]
        INFO  16844 --- [nio-8096-exec-2] h.e.r.MyHandlerExceptionResolver         : call resovler = 
        java.lang.IllegalArgumentException: 잘못된 입력 입니다
            ...생략  
        INFO  16844 --- [nio-8096-exec-2] h.e.r.MyHandlerExceptionResolver         : IllegalArgumentException resolver to 400
        INFO  16844 --- [nio-8096-exec-2] h.e.interceptor.LogInteceptor            : [Interceptor] RESPONSE [da955186-34b1-42b2-bf80-3e8035b37c7f][REQUEST][/api/members/bad][hello.exception.api.ApiExceptionCotroller#getMember(String)]
        INFO  16844 --- [nio-8096-exec-2] hello.exception.filter.LogFilter         : [ Filter ] RESPONSE [70943934-122c-4413-9ffd-3594ea5b1050][REQUEST][/api/members/bad]
        INFO  16844 --- [nio-8096-exec-2] hello.exception.filter.LogFilter         : [ Filter ] REQUEST [92253cc8-f018-4237-ae87-4d322648c291][ERROR][/error]
        INFO  16844 --- [nio-8096-exec-2] h.e.interceptor.LogInteceptor            : [Interceptor] REQUEST [7096dd39-d80f-4883-a54e-d522d317bfb3][ERROR][/error][class org.springframework.web.method.HandlerMethod]
        INFO  16844 --- [nio-8096-exec-2] h.e.interceptor.LogInteceptor            : [Interceptor] postHandle(Not Exception) mv = [null]
        INFO  16844 --- [nio-8096-exec-2] h.e.interceptor.LogInteceptor            : [Interceptor] RESPONSE [7096dd39-d80f-4883-a54e-d522d317bfb3][ERROR][/error][org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController#error(HttpServletRequest)]
        INFO  16844 --- [nio-8096-exec-2] hello.exception.filter.LogFilter         : [ Filter ] RESPONSE [92253cc8-f018-4237-ae87-4d322648c291][ERROR][/error]
        ```

    - api/members/mvTest 요청

        ![image](https://github.com/9ony/9ony/assets/97019540/905018e7-8138-4938-83c5-9549c17ceee2)

        __콘솔 로그__  

        ```text
        INFO  22140 --- [nio-8096-exec-2] hello.exception.filter.LogFilter         : [ Filter ] REQUEST [e2ffb042-b4c9-4603-b129-cb6be13e10eb][REQUEST][/api/members/mvTest]
        INFO  22140 --- [nio-8096-exec-2] h.e.interceptor.LogInteceptor            : [Interceptor] REQUEST [5a27ca19-67e2-49d0-98de-0413ac55e3ef][REQUEST][/api/members/mvTest][class org.springframework.web.method.HandlerMethod]
        INFO  22140 --- [nio-8096-exec-2] h.e.r.MyHandlerExceptionResolver         : call resovler = 
        hello.exception.MyError.MyException: TEST : return ModelAndView ...생략
        INFO  22140 --- [nio-8096-exec-2] h.e.r.MvTestHandlerExceptionResolver     : MvTestHandlerExceptionResolver 호출됨
        INFO  22140 --- [nio-8096-exec-2] h.e.r.MvTestHandlerExceptionResolver     : MyException resolver to ModelAndView
        INFO  22140 --- [nio-8096-exec-2] h.e.interceptor.LogInteceptor            : [Interceptor] RESPONSE [5a27ca19-67e2-49d0-98de-0413ac55e3ef][REQUEST][/api/members/mvTest][hello.exception.api.ApiExceptionCotroller#getMember(String)]
        INFO  22140 --- [nio-8096-exec-2] hello.exception.filter.LogFilter         : [ Filter ] RESPONSE [e2ffb042-b4c9-4603-b129-cb6be13e10eb][REQUEST][/api/members/mvTest]
        ```
        
        
    - api/members/user-ex (text or json)

        ![image](https://github.com/9ony/9ony/assets/97019540/d209a22b-65ff-48ca-b5de-b0368413437b)


### HandlerExceptionResovler 정리
- 반환 값에 따른 동작 방식
    - new ModelAndView()  
    컨트롤러에서 발생한 오류를 ExceptionResolver가 처리할 수 있다면, 해당 Resolver가 예외를 
    response.sendError(xxx)호출로 변경(`예외 -> 정상` 흐름으로 변경)해서 서블릿에서 상태 코드에 따른 오류를 처리하도록 한다.  
    그러면 WAS는 sendError()에 있는 상태코드에 따른 오류페이지를 호출한다.  

    - new ModelAndView() + sendError X  
    sendError를 안보내고 new ModelAndView()를 반환하면 정상호출이기 때문에 빈화면을 전달받는다.  
    이때 response.getWriter()등을 이용하여 바디에 원하는 데이터를 추가할 수 있다.  
    즉, sendError를 보내지않으면 예외를 ExceptionResolver에서 바로 처리해서 응답하는 형식이라 보면된다.  
    ( ❗ 상태코드를 변경 시 setStatus()를 이용하자! sendError()는 위와 같이 WAS추가 동작 ) 
    
    - ModelAndView 지정  
    ModelAndView 에 View , Model 등의 정보를 지정해서 반환하면 뷰를 렌더링 한다.
    이때 sendError를 전송한 것이 아닌 정상호출이기 때문에 WAS는 해당 View를 바로 렌더링함.  

    - null  
    null 을 반환 시 해당 ExceptionResolver는 예외를 처리하지 못하는걸로 인지하고 다른 ExceptionResolver를 찾아서 실행한다.  
    이때 처리할 수 있는 ExceptionResolver가 없으면 예외 처리가 안되고, 기존에 발생한 예외를 서블릿 밖으로 던진다. 그러면 500에러가 발생하고 해당 오류 페이지가 있으면
    
중요한점은 컨트롤러에서 예외가 발생하면 ExceptionResolver가 해당 예외를 인지해서 정상흐름으로 바꿀 수 있다는게 중요하다!  
그리고 user-ex같은 경우 ExceptionResolver에서 예외를 바로 처리해서 넘겨주어서 WAS에서 재요청

## 스프링이 제공하는 ExceptionResolver  

위에서 봤듯이 `extendHandlerExceptionResolvers`로 ExceptionHandler 추가할때 스프링에 기본적으로 등록되어있는 ExceptionHandler 3가지가 있었다.  

1. ExceptionHandlerExceptionResolver  
    @ExceptionHandler 을 처리한다. API 예외 처리는 대부분 이 기능으로 해결한다. 조금 뒤에 자세히
    설명한다.  
2. ResponseStatusExceptionResolver  
    HTTP 상태 코드를 지정해준다.  
    예) @ResponseStatus(value = HttpStatus.NOT_FOUND)  
3. DefaultHandlerExceptionResolver  
    스프링 내부 기본 예외를 처리한다.  

위 3가지 Resolver를 알아보자.  

### ResponseStatusExceptionResolver

ResponseStatusExceptionResolver는 예외에 따라서 HTTP 상태코드와 메세지등을 지정해주는 역할

- ApiExceptionController 추가
    ```java
    @GetMapping("/response-status-ex1")
    public String responseStatusEx1() {
        throw new BadRequestException();
    }
    ```

- BadRequestException.class
    
    ```java
    //@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "잘못된 요청 오류") //상태코드 + 오류 메세지
    //or
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error.bad") //상태코드 + MessageSource키 값
    public class BadRequestException extends RuntimeException {

    }
    ```
- messages.properties

    ```text
    error.bad=잘못된 요청입니다 (Message Source)
    ```

- 테스트 결과
    - 요청 : /api/response-status-ex1   
    ![image](https://github.com/9ony/9ony/assets/97019540/6d707a3d-1c3e-45df-a681-1a3e98509bab)

    - 콘솔 로그  
    ![image](https://github.com/9ony/9ony/assets/97019540/8c442c85-96b6-47cb-b41f-fbf03249fb85)


__정리 및 ResponseStatusExceptionResolver 내부동작__

`/response-status-ex1 요청`을 보내서 `BadRequestException이 발생`했고 해당 예외를 `해결할 Resolver를 찾게`된다.(없다면 500 Error로 WAS까지 전달)  
@ResponseStatus가 달려있기 때문에 ResponseStatusExceptionResolver가 해당 예외를 처리하게 되고 어노테이션 속성값에 들어있는 값으로 상태값과 Message or MessageSource값을 message로 설정해준다.  
이때 설정된 값으로 sendError를 보내게되고 WAS는 sendError()로 온 상태코드를 basicController or WebServerFactoryCustomizer로 설정된 ErrorPage로 재요청하여 클라이언트에 응답한다.   

ResponseStatusExceptionResolver코드를 통해 내부 동작하는 과정을 살펴보자.  

```java
@Override
@Nullable
protected ModelAndView doResolveException(
        HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {

    try {
        if (ex instanceof ResponseStatusException) {
            return resolveResponseStatusException((ResponseStatusException) ex, request, response, handler);
        }

        ResponseStatus status = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class); //ResponseStatus어노테이션이 붙은 클래스를 찾음
        if (status != null) {//status BadRequestException.class의 정보가 담겨있음 true
            return resolveResponseStatus(status, request, response, handler, ex); 
        }
        //생략
    }
    catch (Exception resolveEx) {
        //생략
    }
    return null;
}
//...
protected ModelAndView resolveResponseStatus(ResponseStatus responseStatus, HttpServletRequest request,HttpServletResponse response, @Nullable Object handler, Exception ex) throws Exception {
		int statusCode = responseStatus.code().value(); //상태코드
		String reason = responseStatus.reason(); //오류메세지 or MessageSource 값
		return applyStatusAndReason(statusCode, reason, response);
	}
//...
protected ModelAndView applyStatusAndReason(int statusCode, @Nullable String reason, HttpServletResponse response)
			throws IOException {

		if (!StringUtils.hasLength(reason)) {//어노테이션 reason값이 없으면.
			response.sendError(statusCode); //어노테이션으로 설정된 상태코드로 sendError를 보냄
		}
		else {
			String resolvedReason = (this.messageSource != null ?
					this.messageSource.getMessage(reason, null, reason, LocaleContextHolder.getLocale()) :
					reason);//messageSource값이면 해당 키값의 Value를 message로 설정하고 아니면 있는 그대로 String을 message값으로 설정
			response.sendError(statusCode, resolvedReason);//상태코드 + reason값 sendError
		}
		return new ModelAndView();
	}
```

applyStatusAndReason메서드를 보면 결국 우리가 HandlerExceptionResolver를 구현할때와 비슷하게 sendError()로 상태코드와 함께 response에 보내고 빈 ModelAndView를 반환해서 정상흐름으로 바꿔준다.

추가적으로 코드에서 봣듯이 어노테이션 말고 커스텀 예외를 만들때 ResponseStatusException를 상속받아서 만들어도 해당 ResponseStatusExceptionResolver가 동작한다.  

### DefaultHandlerExceptionResolver  

스프링 내부에서 발생하는 스프링 예외를 해결한다.  
예를들어 컨트롤러에서 바인딩과정에서 타입에러 시 TypeMismatchException 발생해서 500에러 코드가 발생해야 한다.  
하지만 이런 경우 클라이언트가 요청을 잘못했기 때문에 상태코드 400을 사용하도록 되어있다.  
DefaultHandlerExceptionResolver가 이런 오류를 500이아니라 400오류로 변경해준다.  
스프링 내부 오류를 어떻게 처리할지 수 많은 내용이 정의되어 있다.  

- 컨트롤러 추가 코드

    ```java
    @GetMapping("/default-handler-ex")
    public String defaultException(@RequestParam Integer data) {
        return "ok";
    }
    ```

- /api/default-handler-ex 요청
    - DefaultHandlerExceptionResolver 적용  
    ![image](https://github.com/9ony/9ony/assets/97019540/643660a0-f096-4ef3-8980-db8f21691963)

    - DefaultHandlerExceptionResolver 미적용  

    configureHandlerExceptionResolvers를 통해 DefaultHandlerExceptionResolver등록을 제외된 상태로도 요청해보자.   
    ```java
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        //..resolver등록
    }
    ```

    ![image](https://github.com/9ony/9ony/assets/97019540/ae953cfc-e25c-4606-8b12-c3789c833962)

- DefaultHandlerExceptionResolver.class  

    ![image](https://github.com/9ony/9ony/assets/97019540/b4b18d0c-d363-41ff-9495-7d311395ddbf)

    DefaultHandlerExceptionResolver.class 내부 코드를 확인해 보면 위 사진에 Exception들에 맞는 상태코드를 반환해주는 로직을 가지고 있다.  
    DefaultHandlerExceptionResolver는 여러 Exception들을 HTTP스펙에 맞는 상태코드로 반환해준다.  


### ExceptionHandlerExceptionResolver

위에서 API응답 예외를 처리할때 직접 서블릿 response객체에 json객체를 만들어서 넣어주면서 응답을 했었다.  
이러한 과정은 매우 번거롭고, API 예외는 어떤 기능(컨트롤러)에서 예외가 터지냐에 따라서도 응답을 다르게 해줘야되기 복잡하다.  
BasicErrorController와 HandlerExceptionResolver로 직접 각각의 API 예외를 구현하기도 쉽지 않다.  

스프링은 위와 같은 API 예외 처리 문제를 해결하기 위해 `@ExceptionHandler`라는 애노테이션을 사용하는 매우 편리한 예외 처리 기능을 제공한다.  
이것이 바로 `ExceptionHandlerExceptionResolver` 이고, 기본으로 제공하는 ExceptionResolver 중에 우선순위도 가장 높다.

- ErrorResult 객체 생성
    에러코드와 에러메세지를 정보를 담는 객체
    ```java
    @Data
    @AllArgsConstructor
    public class ErrorResult {
        private String code;
        private String message;
    }
    ```

- ApiExceptionV2Controller 생성

    ```java
    @Slf4j
    @RestController
    public class ApiExceptionV2Controller {

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(IllegalArgumentException.class)
        public ErrorResult illegalExHandle(IllegalArgumentException e) {
            log.error("[exceptionHandle] ex", e);
            return new ErrorResult("BAD", e.getMessage());
        }

        @ExceptionHandler
        public ResponseEntity<ErrorResult> userExHandle(UserException e) {
            log.error("[exceptionHandle] ex", e);
            ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
            return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
        }

        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ExceptionHandler
        public ErrorResult exHandle(Exception e) {
            log.error("[exceptionHandle] ex", e);
            return new ErrorResult("EX", "내부 오류");
        }
        
        @GetMapping("/api2/members/{id}")
        public MemberDto getMember(@PathVariable("id") String id) {
            if (id.equals("ex")) {
                throw new RuntimeException("잘못된 사용자");
            }
            if (id.equals("bad")) {
                throw new IllegalArgumentException("잘못된 입력 값");
            }
            if (id.equals("user-ex")) {
                throw new UserException("사용자 오류");
            }
            return new MemberDto(id, "hello " + id);
        }
    }
    ```

    - 결과

    ![image](https://github.com/9ony/9ony/assets/97019540/0fbcd3a8-1260-4777-8301-5d6ecbd4a78b)

    ![image](https://github.com/9ony/9ony/assets/97019540/b272ea85-240f-4130-b488-62802412afcf)

    ![image](https://github.com/9ony/9ony/assets/97019540/5ab264b0-45fa-4a22-81cf-4deddc8ac0f2)

### @ExceptionResolver 동작 흐름

1. 컨트롤러를 호출 시 예외가 발생해 컨트롤러 밖으로 던져짐   
(Interceptor preHandle()에서 핸들러가 작동되기 때문에 예외가 터지면 postHandle은 작동안됨)  

2. HandlerExceptionResolver가 담긴 리스트를 루프 하는데,  
    가장 우선순위가 높은 ExceptionHandlerExceptionResolver 먼저 실행  
    (없다면 다른 ExceptionResolver에게 예외를 넘긴다.)  

3. 스프링 부트가 동작하면서 초기에 ExceptionResolver에 key,value값으로
key에는 클래스 , value에는 해당 클래스에서 예외를 처리할 수 있는 메서드들이 `exceptionHandlerCache`에 등록된다.  

4. ExceptionHandlerExceptionResolver에 exceptionHandlerCache를 확인하여 해당 예외를 처리할 수 있는 메서드가 있으면 해당 예외를 처리  
( ❗ 만약 exceptionHandlerCache에 없다면 @ControllerAdvice가 붙은 ExceptionResolver를 찾는데 이는 바로 다음예제를 통해 확인하겠다.  )

5. 예외처리가 ExceptionResolver에서 바로 되므로 WAS까지 예외가 전달되지 않고 바로 처리됨  

__exceptionHandlerCache 등록된 클래스와 해당 처리할 수 있는 예외와 그에 대응된 메서드__  

![image](https://github.com/9ony/9ony/assets/97019540/fa020382-89d6-40a4-847d-046b0b317456)


### @ExceptionResolver 정리
 
- @ExceptionHandler애노테이션을 선언 후 해당 컨트롤러에서 처리하고 싶은 예외를 지정  
    ❗ 해당 컨트롤러 안에서 발생한 예외에서 작동    

- 지정된 예외 발생 시 해당 메서드가 실행되고, 우선순위는 상세한 것이 우선권이 높다.    
    ex) UserException > RunTimeException > Exception   
    => UserException은 RunTimeException을 상속받음 (자식 예외)  

- @ExceptionHandler에 예외를 지정하지 않을 시 파라미터의 예외를 사용  
    ex) @ExceptionHandler  
        public String method(RuntimeException e){} => RuntimeException을 사용   

- 다양한 파라미터 타입과 반환 값을 지원한다.   
    [스프링 공식 메뉴얼](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html#mvc-ann-exceptionhandler-args)  
    => ResponseEntity, 자바객체 뿐만아니라 ModelAndView 등 반환값을 기존 컨트롤러 못지않게 지원    

## @ControllerAdivce

@ControllerAdvice 또는 @RestControllerAdvice를 사용하면 예외 처리 코드를 컨트롤러에서 분리 시킬 수 있는데, 바로 예제로 확인해보자.  

컨트롤러에 있던 `@ExceptionHandler`를 붙은 메서드를 아래와 같이 다른 클래스에 분리시키자.  
기존 컨트롤러에 있는 @ExceptionHandler는 주석처리  
만약 @ExceptionHandler가 있으면 컨트롤러에 있는게 먼저 실행되므로 주의하자.  

```java
@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandle(IllegalArgumentException e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }
    //생략...
}
```

이렇게 @ExceptionHandler를 컨트롤러 로직과 분리해서 관리할 수 있고,  
@RestControllerAdvice에 특정 패키지, 어노테이션, 클래스 등을 아래와 같이 범위로 지정할 수도 있다.  

- 대상 컨트롤러 or 패키지 등을 지정
```java
// @RestController 어노테이션이 붙은 컨트롤러를 지정
@ControllerAdvice(annotations = RestController.class)
public class ExampleAdvice1 {}

// org.example.controllers패키지 안에 포함된 컨트롤러를 지정
@ControllerAdvice("org.example.controllers")
public class ExampleAdvice2 {}

// 특정 클래스를 여러개 지정
@ControllerAdvice(assignableTypes = {ControllerInterface.class, AbstractController.class})
public class ExampleAdvice3 {}
```

[@ControllerAdvice 자바 문서 참고](https://docs.spring.io/spring-framework/docs/6.0.11/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html)


### @ControllerAdvice 정리
- @ControllerAdvice는 대상으로 지정한 여러 컨트롤러에 @ExceptionHandler , @InitBinder 기능을
부여해주는 역할
- @ControllerAdvice에 대상을 지정하지 않을 시 모든 컨트롤러에 적용 (글로벌 적용)
- @RestControllerAdvice = @ControllerAdvice + @ResponseBody 
    => @Controller , @RestController 의 차이와 같음  