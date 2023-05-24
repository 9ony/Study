# 스프링 MVC 구조

__직접 만든 MVC 그림__

![image](https://github.com/9ony/9ony/assets/97019540/f011559f-3163-44ca-aa4a-f4241814a0ac)

__스프링 MVC 그림__  

![image](https://github.com/9ony/9ony/assets/97019540/27a8f877-1913-4032-9f7e-13b320343643)

__동작방식__  
1. 핸들러 조회 : 핸들러 매핑을 통해 URL에 매핑된 핸들러(컨트롤러) 조회
2. 핸들러 어댑터 조회: 핸들러를 실행할 수 있는 핸들러 어댑터 조회
3. 핸들러 어댑터 실행: 핸들러 어댑터 실행
4. 핸들러 실행: 핸들러 어댑터가 실제 핸들러를 실행
5. ModelAndView 반환: 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해 반환.
6. viewResolver 호출: 뷰 리졸버를 찾아 실행한다.  
⇒ JSP: InternalResourceViewResolver가 자등 등록되어 사용된다.
7. View 반환: 뷰 리졸버는 뷰의 논리 이름을 물이 이름으로 바꾸고 렌더링 역할을 담당하는 뷰 객체 반환.  
⇒ JSP: InternalResourceView(JstlView)를 반환하는데, 내부에는 forward() 가 있다.  
8. 뷰 렌더링: 뷰를 통해서 뷰를 렌더링한다.

우리가 직접 만든 MVC와 구조와 동작방식이 비슷하다!

### DispatcherServlet

스프링 MVC도 프론트 컨트롤러 패턴으로 구현됨
스프링 MVC의 프론트 컨트롤러가 바로 디스패처 서블릿(DispatcherServlet)  
디스패처 서블릿이 바로 스프링 MVC의 핵심  

__DispatcherServlet 구조__  
![image](https://github.com/9ony/9ony/assets/97019540/46c2e811-aff2-44fd-bba1-629b7cf06226)

DispacherServlet 도 부모 클래스에서 HttpServlet 을 상속 받아서 사용하고, 서블릿으로 동작한다.  
> DispatcherServlet -> FrameworkServlet -> HttpServletBean -> HttpServlet  

스프링 부트는 DispacherServlet 을 서블릿으로 자동으로 등록하면서 모든 경로( urlPatterns="/" )에 대해서 매핑  
단, 더 자세한 경로(우리가 만든 서블릿)가 우선순위가 높다!  

__DispatcherServlet 요청 흐름__  
1. 서블릿이 호출되면 HttpServlet이 제공하는 serivce()가 호출  
2. 스프링 MVC는 DispatcherServlet의 부모인 FrameworkServlet 에서 service()를 오버라이드  
3. FrameworkServlet.service()를 시작으로 여러 메서드가 호출되면서 DispacherServlet.doDispatch()가 호출됨  


__DispacherServlet의 핵심 doDispatch()코드 분석__  

```java
@SuppressWarnings("deprecation")
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerExecutionChain mappedHandler = null;
		boolean multipartRequestParsed = false;

		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

		try {
			ModelAndView mv = null;
			Exception dispatchException = null;

			try {
				processedRequest = checkMultipart(request);
				multipartRequestParsed = (processedRequest != request);

				// Determine handler for the current request. (핸들러 조회)
				mappedHandler = getHandler(processedRequest);
				if (mappedHandler == null) {
					noHandlerFound(processedRequest, response);
					return;
				}

				// Determine handler adapter for the current request. (어댑터 조회)
				HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

				// Process last-modified header, if supported by the handler. ( last-modified header 수정 캐시이용 시 사용)
				String method = request.getMethod();
				boolean isGet = HttpMethod.GET.matches(method);
				if (isGet || HttpMethod.HEAD.matches(method)) {
					long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
					if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
						return;
					}
				}
                //인터셉터 관련 코드
				if (!mappedHandler.applyPreHandle(processedRequest, response)) {
					return;
				}

				// Actually invoke the handler. (어댑터로 핸들러 호출 후 model과 view 반환)
				mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
                ////비동기 처리라면 종료. WebAsyncManager에서 별도의 스레드가 처리
				if (asyncManager.isConcurrentHandlingStarted()) {
					return;
				}

				applyDefaultViewName(processedRequest, mv);
				mappedHandler.applyPostHandle(processedRequest, response, mv);
			}
			catch (Exception ex) {
				//예외처리 ...
			}
            //핸들러 실행 결과 ModelAndView를 적절한 response형태로 처리
			processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
		}catch (Exception ex) {
			//예외처리 ..
		}
	}
```

```java
private void processDispatchResult(HttpServletRequest request,
HttpServletResponse response, HandlerExecutionChain mappedHandler, ModelAndView mv, Exception exception) throws Exception {
    // 뷰 렌더링 호출
    render(mv, request, response);
}

protected void render(ModelAndView mv, HttpServletRequest request,HttpServletResponse response) throws Exception {
    View view;
    String viewName = mv.getViewName();
    // 6. 뷰 리졸버를 통해서 뷰 찾기, 7. View 반환
    view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
    // 8. 뷰 렌더링
    view.render(mv.getModelInternal(), request, response);
}
```

주석을 단 부분만 보면 인터셉터와 캐시 생명주기 관련 헤더인 (last-modified)부분만 제외하면 흐름이 우리가 만든 프론트 컨트롤러와 비슷한걸 볼 수 있다.  

- 스프링 MVC는 코드 분량도 매우 많고, 복잡해서 내부 구조를 다 파악하는 것은 쉽지 않음  
- 기능을 직접 확장하거나 나만의 컨트롤러를 만드는 일은 없음  
    => 대부분 기능이 다 구현되어 있음  
- 전체적인 구조가 이렇게 되어 있구나 하고 이해가 핵심  
    => 향후 문제가 발생했을 때 어떤 부분에서 문제가 발생했는지 쉽게 파악 및 해결  
    => 확장 포인트가 필요할 때, 어떤 부분을 확장해야 할지 파악

### 핸들러 매핑과 핸들러 어댑터  
 
앞서 우리가 다양한 종류의 컨트롤러를 사용하기 위해서 여러 그에 맞는 어댑터를 사용해야 했는데, 스프링 부트에는 어떤 것들(핸들러 매핑,핸들러 어댑터)이 사용되는지 알아보자!

__Controller 인터페이스를 구현하는 OldController__

경로 : hello/servlet/web/springmvc/old/OldController.class  
실행 : http://localhost:8080/springmvc/old-controller  
```java
@Component("/springmvc/old-controller")
public class OldController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return null;
    }
}
```
- @Controller 애노테이션과 다름 
@Component("/springmvc/old-controller")  
=> /springmvc/old-controller 라는 이름의 스프링 빈으로 등록  
=> 스프링 빈의 이름으로 URL 매핑  

해당 컨트롤러가 호출되려면??  
- 핸들러 매핑에서 이 컨트롤러를 찾을 수 있어야 한다  
- 핸들러 매핑을 통해서 찾은 핸들러를 실행할 수 있는 핸들러 어댑터가 필요  

위 2가지가 필요한데, 스프링은 이미 필요한 핸들러 매핑과 어댑터를 구현해 두었다.  

- HandlerMapping  
    - RequestMappingHandlerMapping : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용  
    - BeanNameUrlHandlerMapping : 스프링 빈의 이름으로 핸들러 매핑  
    > 이외에 ControllerClassNameHandlerMapping,SimpleUrlHandlerMapping 등등 다양한 종류가 있음  

- HandlerAdapter
    - RequestMappingHandlerAdapter : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용  
    - HttpRequestHandlerAdapter : HttpRequestHandler 처리  
    - SimpleControllerHandlerAdapter : Controller 인터페이스(애노테이션X, 과거에 사용) 처리  

각 핸들러 매핑과 어댑터는 자동으로 등록되는 것들이 있는데 위에서 부터 아래로 우선순위가 높다.  
> `@RequestMapping`의 앞글자를 따서 만든 이름의 핸들러 매핑과 어댑터(우선순위 제일 높은 것)
=> RequestMappingHandlerMapping , RequestMappingHandlerAdapter
이것이 바로 지금 스프링에서 주로 사용하는 애노테이션 기반의 컨트롤러를 지원하는 매핑과 어댑터이다.  
`실무에서는 99.9% 이 방식의 컨트롤러를 사용`

__동작 순서__

1. 핸들러 매핑으로 핸들러 조회  
    =>HandlerMapping을 우선순위대로 순서대로 실행하여 핸들러 조회  
2. 핸들러 어댑터 조회  
    =>HandlerAdapter 의 supports() 를 순서대로 호출   
3. 핸들러 어댑터 실행  
    =>디스패처 서블릿이 조회한 SimpleControllerHandlerAdapter를 실행하면서 핸들러 정보도 함께 넘겨준다.  
    =>SimpleControllerHandlerAdapter 는 핸들러인 OldController를 내부에서 실행하고 그 결과를 반환  

OldController 사용 객체  
HandlerMapping = BeanNameUrlHandlerMapping  
HandlerAdapter = SimpleControllerHandlerAdapter  

### 뷰 리졸버 (ViewResolver)

```java
@Component("/springmvc/old-controller")
public class OldController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        //View를 사용하기 위해 코드추가
        return new ModelAndView("new-form");
    }
}
```

`return new ModelAndView("new-form");`추가 후 요청을 보내면 컨트롤러까지는 정상적으로 호출되지만 404에러페이지가 나온다.  
페이지를 찾을 수 없다는 것이다.  
그 전에 했던 ViewResolver()메서드에 접두사 접미사를 넣어 줬던걸 기억할 것이다.  
스프링 부트도 application.properties에 다음 코드를 추가하면 정상적으로 된다.  

```text
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```

__뷰 리졸버 - InternalResourceViewResolver__    

스프링 부트는 InternalResourceViewResolver 라는 뷰 리졸버를 자동으로 등록하는데, 이때 application.properties 에 등록한 spring.mvc.view.prefix , spring.mvc.view.suffix 설정 정보를 사용해서 등록한다.  

__스프링 부트가 자동 등록하는 뷰 리졸버__
- BeanNameViewResolver : 빈 이름으로 뷰를 찾아서 반환   (예: 엑셀 파일 생성 기능에 사용)  
- InternalResourceViewResolver : JSP를 처리할 수 있는 뷰를 반환    

위에서 부터 우선순위가 높음!! 이외에 더 많은 뷰 리졸버가 있다.

__동작 과정__
1. 핸들러 어댑터 호출  
    =>핸들러 어댑터를 통해 new-form 이라는 논리 뷰 이름을 획득한다.  
2. ViewResolver 호출  
    =>new-form 이라는 뷰 이름으로 viewResolver를 순서대로 호출한다.  
    =>BeanNameViewResolver 는 new-form 이라는 이름의 스프링 빈으로 등록된 뷰를 찾아야 하는데 없다.  
    =>InternalResourceViewResolver 가 호출된다.  
3. InternalResourceViewResolver  
    =>이 뷰 리졸버는 InternalResourceView 를 반환한다.  
4. 뷰 - InternalResourceView  
    =>InternalResourceView 는 JSP처럼 포워드 forward() 를 호출해서 처리할 수 있는 경우에 사용한다.  
5. view.render()  
    =>view.render() 가 호출되고 InternalResourceView 는 forward() 를 사용해서 JSP를 실행한다.  

__❗ 참고__  
`InternalResourceViewResolver` 는 만약 `JSTL 라이브러리`가 있으면 InternalResourceView 를 상속받은 `JstlView 를 반환`한다.  
JstlView => JSTL태그 사용시 부가기능이 있음  

JSP의 경우 forward() 통해서 해당 JSP로 이동(실행)해야 렌더링이 된다.  
다른 뷰 템플릿은 해당 과정없이 바로 렌더링 된다.  

뷰 템플릿은 라이브러리로 추가해야 하고 타임리프는 라이브러리 추가 시 스프링 부트에서 해당 resolver를 자동 등록해준다.  

## 스프링 MVC 시작

스프링이 제공하는 컨트롤러는 애노테이션 기반으로 동작해서, 매우 유연하고 실용적  과거 자바 언어에 애노테이션이 없을 땐 스프링도 이런 유연한 컨트롤러를 제공한 것은 아니다.  

지금까지 만들었던 컨트롤러를 @RequestMapping 기반의 스프링MVC로 변경해보자!

### 스프링 MVC로 변경하기

__SpringMemberFormControllerV1.class__  

```java
@Controller
public class SpringMemberFormControllerV1 {
    @RequestMapping("/springmvc/v1/members/new-form")
    public ModelAndView process(){
        System.out.println("SpringMemberFormControllerV1.process");
        return new ModelAndView("new-form");
    }
}
```

@Controller가 붙으면 @Component가 내부적으로 어노테이션이 등록되있어서 빈으로 등록되고 스프링이 컨트롤러로 인식한다.  
@RequestMapping이 붙으면 해당 주소로 요청이오면 해당 메서드가 호출이 된다.  
그리고 컨트롤러와 해당 url주소(요청정보)가 매핑된다.  

우리가 프론트컨트롤러를 만들어서 url주소와 컨트롤러를 담은 Map을 만든걸 한번 생각해보자.  

`handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());`  

위와 같은 방식으로 url,컨트롤러객체로 매핑정보를 생성했는데,  
스프링 MVC를 통해 어노테이션으로 컨트롤러 `SpringMemberFormControllerV1`를 등록하고 @RequestMapping `/springmvc/v1/members/new-form`을 매핑해 준것이다.  
즉, `/springmvc/v1/members/new-form` 요청이오면 핸들러 어댑터가 해당 요청을 처리할 수 있는 어노테이션기반 핸들러인 `SpringMemberFormControllerV1` 컨트롤러(핸들러)를 찾아서 `@RequestMapping("/springmvc/v1/members/new-form")` 붙은 process()메서드를 실행하는 것이다.  

참고로 스프링부트 3.0이전 or 스프링 프레임워크 6.0이전은  
```java
@Component
@RequestMapping
public class SpringMemberFormControllerV1 {
    위와동일
}
```
@Component , @RequestMapping 로 붙여도 컨트롤러로 인식됨  
스프링 부트3.0이상 or 스프링프레임워크6.0 이상 부터는 컨트롤러로 인식하지 않는다.  

__스프링 부트 내부 isHandler()메서드 (컨트롤러 유무)__
```java
@Override
	protected boolean isHandler(Class<?> beanType) {
		return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) || AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class)); //3.0 이전
		
        return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class); //3.0 이후
	}
```
스프링 내부코드인데 Controller 또는 RequestMapping 일경우 true이지만
3.0 이후에는 컨트롤러 어노테이션만 true를 반환한다.!! 

[스프링 프레임워크 변경사항 깃허브 출처](https://github.com/spring-projects/spring-framework/commit/3600644ed1776dce35c4a42d74799a90b90e359e)  

__빈으로 직접 등록__  

경로 : hello/servlet/ServletApplication.class

```java
//직접등록
	@Bean
	SpringMemberFormControllerV1 springMemberFormControllerV1(){
		return new SpringMemberFormControllerV1();
	}
```

해당 클래스를 컴포넌트 스캔없이 빈으로 직접 등록해도 된다.  
하지만 @Controller만 붙이면 빈으로 등록하고 매핑하는 기능도 추가되는데 굳이 이렇게 할 필요는 없다고 생각한다.  


결론은 
@Component는 해당 `클래스`가 스프링 빈으로 생성될 수 있게 해준다.  
@RequestMapping은 해당 클래스가 Controller 클래스의 기능(HTTP 요청 처리 및 매핑)을 수행할 수 있게 해준다. 
@Controller는 위의 2가지 어노테이션을 모두 포함하고 있다.  

__회원 저장 기능__  

```java
@Controller
public class SpringMemberSaveControllerV1 {

    private MemberRepo memberRepo = MemberRepo.getInstance();

    @RequestMapping("/springmvc/v1/members/save")
    public ModelAndView process(HttpServletRequest req) {
        Member member = new Member(req.getParameter("username"),Integer.parseInt(req.getParameter("age")));
        memberRepo.save(member);

        ModelAndView mv = new ModelAndView("save-result");
        mv.addObject("member",member);
        return mv;
    }
}
```

__회원 목록 조회__

```java
@Controller
public class SpringMemberListControllerV1 {

    private MemberRepo memberRepo = MemberRepo.getInstance();

    @RequestMapping("/springmvc/v1/members")
    public ModelAndView process(){
        List<Member> members = memberRepo.findMemberAll();

        ModelAndView mv = new ModelAndView("members");
        mv.addObject("members",members);
        return mv;
    }
}
```

ModelAndView에 객체를 추가할때는 addObject()메서드를 이용한다!!  

### 스프링 MVC 컨트롤러 통합하기

```java
@Controller
@RequestMapping("/springmvc/v2/members")
public class SpringMemberControllerV2 {
    private MemberRepo memberRepo = MemberRepo.getInstance();

    @RequestMapping("/new-form")
    public String newForm(){

        return "new-form";
    }
    @RequestMapping("/save")
    public ModelAndView save(HttpServletRequest req){
        Member member = new Member(req.getParameter("username"),Integer.parseInt(req.getParameter("age")));
        memberRepo.save(member);

        ModelAndView mv = new ModelAndView("save-result");
        mv.addObject(member);

        return mv;
    }
    @RequestMapping
    public ModelAndView members(){

        List<Member> members = memberRepo.findMemberAll();

        ModelAndView mv = new ModelAndView("members");
        mv.addObject(members);

        return mv;
    }
}
```
이전과 달리 컨트롤러를 하나의 클래스안에 통합시켰다.  
보면 RequestMapping을 class단위에서 사용했는데, 이렇게 되면 메서드단위와 주소가 조합된다.  
쉽게 말하면 해당 클래스단위에 있는 ReqeustMapping의 주소가 접두사로 들어가서 
newForm메서드를 실행시키기 위해선 `/springmvc/v2/members(클래스단위)/ + new-form(메서드 단위)로 조합`되는거다.  

### 스프링 MVC 좀 더 실용적인 방식

우리가 프론트 컨트롤러 패턴을 적용할 때 V3에서 V4로 넘어갈때 뷰 이름을 논리적인 이름만 반환했던걸 떠올려보자.  
스프링도 논리적인 이름만 반환할 수 있다.  
해당 방식이 실무에서 가장 많이 쓰는 방식이다.  

```java
@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerV3 {
    private MemberRepo memberRepo = MemberRepo.getInstance();

    //@RequestMapping(value="new-form",method= ReqeustMethod.GET)
    @GetMapping("/new-form")
    public String newForm() {
        return "new-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam("username") String username, @RequestParam("age") int age,
            Model model) {
        Member member = new Member(username, age);
        memberRepo.save(member);
        model.addAttribute("member", member);
        return "save-result";
    }

    @GetMapping
    public String members(Model model) {
        List<Member> members = memberRepo.findMemberAll();
        model.addAttribute("members", members);
        return "members";
    }
}
```

- Model 파라미터  
    ave() , members() 를 보면 Model을 파라미터로 받는 것을 확인할 수 있다. 스프링 MVC도 이런 편의기능을 제공한다.  
- ViewName 직접 반환  
    뷰의 논리 이름을 반환할 수 있다.  
- @RequestParam 사용  
    스프링은 HTTP 요청 파라미터를 @RequestParam 으로 받을 수 있다.  
    @RequestParam("username") 은 request.getParameter("username") 와 거의 같은 코드라 생각하면 된다.  
    물론 GET 쿼리 파라미터, POST Form 방식을 모두 지원한다.  
- @RequestMapping @GetMapping, @PostMapping  
    @RequestMapping 은 URL만 매칭하는 것이 아니라, HTTP Method도 함께 구분할 수 있다.
    ```java
    //ex)
    @RequestMapping(value = "/new-form", method = RequestMethod.GET)
    //위 아래 동일한 기능을 가진다.
    @GetMapping("/new-form")
    ```
    해당 코드를 @GetMapping , @PostMapping 등으로 간단하게 처리하는 거다.
    (내부에 @ReqeustMapping 어노테이션을 가지고 있다.)


__추가 공부 내용__  

메서드의 매개변수와 반환(return) 타입은 어떻게 다르게해도 스프링은 이를 인식하고 처리할까??

- HandlerMethodArgumentResolver
    어노테이션 기반의 핸들러 메서드에는 여러 매개변수가 올 수 있다.  
    HttpServletRequest , ModelAndView , HttpEntity  
    @ReqeusetParam , @ReqeustBody 등..  
    ArgumentResolver는 핸들러 어댑터에서 호출할 핸들러 메서드의 파라미터 정보를 가공하는 역할을 한다.  

- HandlerMethodReturnValueHandler
    어노테이션 기반의 핸들러 메서드는 리턴타입 또한 여러 타입으로 올 수 있다.  
    String ,ModelAndView , ResponseEntity ,@ResponseBody 등..  
    Return Value Handler는 핸들러의 반환 값을 지원하는 반환타입인지 확인 후 반환 값을 처리한다.  