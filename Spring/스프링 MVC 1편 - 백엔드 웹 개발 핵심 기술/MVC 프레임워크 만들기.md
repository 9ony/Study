## MVC 프레임워크 만들면서 이해하기

MVC2 패턴에서 한계점인 공통부분 처리하기가 힘들었다.  
이 부분을 프론트컨트롤러 패턴을 도입하여 해결할 수 있다고 했는데, 스프링MVC에도 프론트컨트롤러 패턴을 사용하는데 DispatcherServlet이 프론트 컨트롤러이다.  
`직접 프론트 컨트롤러를 도입해 MVC 프레임워크를 만들어 스프링이 동작하는 과정을 좀 더 잘 이해`해보자!  

우선 프론트 컨트롤러 도입 시 구조가 어떻게 되는지 보자  

### 프론트 컨트롤러 구조  

![image](https://github.com/9ony/9ony/assets/97019540/6e919499-b3cd-4580-ab68-84fe70fa5555)

클라이언트 요청이 프론트컨트롤러를 가르키고 있고, 프론트 컨트롤러가 각각의 컨트롤러 A,B,C로 연결된다.  
이 말은 클라이언의 요청에 따라 프론트 컨트롤러가 공통로직을 수행하고 그에 알맞는 컨트롤러를 찾아주는 것이다.  
그리고 프론트 컨트롤러에서 서블릿에 관련된 처리를 하기 때문에 다른컨트롤러는 서블릿을 사용하지 않아도 된다.  

## 프론트 컨트롤러 패턴 V1

Version 1 : 기존에 썻던 코드를 최대한 유지하면서 프론트 컨트롤러를 도입하는게 목표  

__V1 구조__  
![image](https://github.com/9ony/9ony/assets/97019540/cc303637-6575-4596-b1ae-a3be75567ad3)

1. 클라이언트가 요청을 하면 프론트컨트롤러인 서블릿이 요청을 받음
2. 컨트롤러와 요청URL이 매핑되어있는 정보를 조회  
3. 해당 컨트롤러 호출  
4. 컨트롤러가 로직을 수행하고 해당 JSP로 forward 전송  
5. jsp는 View를 완성하여 클라이언트에게 응답  

### 컨트롤러V1 인터페이스

__ControllerV1.interface__

경로 : hello/servlet/web/frontcontroller/v1

```java
public interface ControllerV1 {
    void process(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException;
}
```

> 위에서 구현한 Interface를 보면 서블릿의 service()와 닮아있다.  
해당 인터페이스를 이용하여 각 컨트롤러는 이를 구체화하고 프론트 컨트롤러는 이를 호출하여 구현과 관계없이 일관성을 가지게 된다.  


우선 회원정보 생성 폼과 저장 및 회원목록조회 컨트롤러를 ControllerV1 인터페이스를 이용하여 구현해보겠다.  

### 컨트롤러V1 구현

경로 : hello/servlet/web/frontcontroller/v1/controller

__MemberFormControllerV1.class__  

```java
public class MemberFormControllerV1 implements ControllerV1 {
    @Override
    public void process(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String viewPath = "/WEB-INF/views/new-form.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);

    }
}
```

__MemberSaveControllerV1.class__  

```java
public class MemberSaveControllerV1 implements ControllerV1 {

    private MemberRepo memberRepo = MemberRepo.getInstance();

    @Override
    public void process(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String username = req.getParameter("username");
        int age = Integer.parseInt(req.getParameter("age"));

        Member member = new Member(username,age);

        memberRepo.save(member);

        String ViewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher disp = req.getRequestDispatcher(ViewPath);
        disp.forward(req,resp);


    }
}
```

__MemberListControllerV1.class__  

```java
public class MemberListControllerV1 implements ControllerV1 {

    private MemberRepo memberRepo = MemberRepo.getInstance();

    @Override
    public void process(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        List<Member> members = memberRepo.findMemberAll();

        req.setAttribute("members",members);

        String viewPath = "/WEB-INF/views/members";
        RequestDispatcher disp = req.getRequestDispatcher(viewPath);
        disp.forward(req,resp);
    }
}
```

### 프론트 컨트롤러v1 구현

경로 : hello/servlet/web/frontcontroller/v1

__FrontControllerV1.class__

```java
@WebServlet(name="frontControllerV1",urlPatterns = "/front-controller/v1/*")
public class FrontControllerV1 extends HttpServlet{

    Map<String,ControllerV1> controllerV1Map = new HashMap<>();

    public FrontControllerV1(){
        controllerV1Map.put("/front-controller/v1/members/new-form", new MemberFormControllerV1());
        controllerV1Map.put("/front-controller/v1/members/save", new MemberSaveControllerV1());
        controllerV1Map.put("/front-controller/v1/members", new MemberListControllerV1());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        ControllerV1 controller = controllerV1Map.get(requestURI);
        if (controller == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        controller.process(req, resp);
    }
}
```

FrontControllerV1은 이전 컨트롤러 처럼 HttpServlet을 상속받아 확장시켰다.  
그리고 URL을보자 v1 하위에 와일드카드( `*` )를 기입했다.  
이말은 `/front-controller/v1/` 하위 경로로 들어오는 모든 요청은 프론트 컨트롤러가 받게된다.  
이제 어떻게 구동되는지 설명하겠다.  

1. 서버 구동 시 매핑정보가 controllerV1Map에 저장됨  
    -> 스프링 내장톰캣을 사용하기 때문에 WAS가 올라갈때 WebServlet이 붙은 클래스들이
        스프링부트 빈으로 등록되기 때문에 이때 생성자가 호출되면서 매핑 정보가 저장되는것 (원래는 요청 시 최초1회라 요청 시 한번 생성되고 유지)  
2. `/front-controller/v1/*` 요청이 들어옴  
3. requestURI에 요청 한 URI값이 선언됨  
4. controllerV1Map에 저장된 URI값으로 우리가 만든 컨트롤러 객체를 선언
5. controller에 매핑된 정보가 null일 경우 404 Not Found로 응답함
6. 아닐 경후 해당 컨트롤러 로직이 실행됨  

프론트컨트롤러만 도입한거지 아직까지 바뀐건 많이 없다.  
중복코드도 여전히 존재하는데 이제 프론트컨트롤러를 도입해서 어떻게 개선되가지는지 알아보자.  

<br>

## 프론트 컨트롤러 패턴 V2

Version 2 : 중복되는 JSP Forward하는 코드를 처리하기 위해 뷰 객체를 생성해서 처리해 줄 것이다.  

__V2 구조__  

![image](https://github.com/9ony/9ony/assets/97019540/7a781a24-827a-4100-8203-72dd4fdb5862)
 
1. 클라이언트가 요청을 하면 프론트컨트롤러인 서블릿이 요청을 받음
2. 컨트롤러와 요청URL이 매핑되어있는 정보를 조회  
3. 해당 컨트롤러 호출  
4. 컨트롤러가 로직을 수행하고 MyView객체 생성 후 리턴
5. 프론트 컨트롤러에서 해당 요청으로 매핑된 컨트롤러의 myview의 render()호출
6. jsp로 뷰 생성 후 HTML응답 반환

### 컨트롤러V2 인터페이스
__ControllerV2.interface__

경로 : /web/frontcontroller/v2/ControllerV2.interface

```java
public interface ControllerV2 {
    // 모든 ControllerV2를 구현한 컨트롤러 객체는 MyView를 반환 (원래는 jsp를 포워드하는 void였음)
    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
```

ControllerV2 인터페이스를 보면 위 V2구조와 같이 process메서드가 MyView를 반환되게 설계했다.  

### 뷰 처리 객체 생성

__MyView.class__  

경로 : /hello/servlet/web/frontcontroller

> 추후에 계속 사용하기 때문에 frontcontroller 위치에 생성

```java
public class MyView {
    private String viewPath;

    public MyView(String viewPath){
        this.viewPath = viewPath;
    }

    public void render(HttpServletRequest req , HttpServletResponse resp) throws IOException, ServletException{
        RequestDispatcher disp = req.getRequestDispatcher(viewPath);
        disp.forward(req,resp);
    }

}
```

MyView객체를 생성하기 위해선 viewPath인 jsp경로를 인자로 받고,  
render() 메서드 내부구조를 보면 앞서 했던 컨트롤러에서 forward할 때 쓰던 코드가 동작하는 걸 볼 수 있다.  

### 컨트롤러V2 구현

경로 : hello/servlet/web/frontcontroller/v2/controller

__MemberFormControllerV2.class__  

```java
public class MemberFormControllerV2 implements ControllerV2{
    @Override
    public MyView process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        return new MyView("/WEB-INF/views/new-form.jsp");
    }
}
```

__MemberSaveControllerV2.class__  

```java
public class MemberSaveControllerV2 implements ControllerV2 {

    private MemberRepo memberRepo = MemberRepo.getInstance();

    @Override
    public MyView process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String username = req.getParameter("username");
        int age = Integer.parseInt(req.getParameter("age"));
        Member member = new Member(username, age);
        memberRepo.save(member);
        req.setAttribute("member", member);

        return new MyView("/WEB-INF/views/save-result.jsp");
    }
}
```

__MemberListControllerV2.class__  

```java
public class MemberListControllerV2 implements ControllerV2{

    private MemberRepo memberRepo = MemberRepo.getInstance();

    @Override
    public MyView process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<Member> members = memberRepo.findMemberAll();
        req.setAttribute("members",members);

        return new MyView("/WEB-INF/views/members.jsp");
    }
}

```


이전 V1 코드와 비교해보면 컨트롤러에 중복으로 입력한 jsp를 forward하는 로직을 V2는 MyView에서 처리해주기 때문에 훨씬 간결해진걸 볼 수 있다.  

컨트롤러는 이제 필요한 로직 수행 후 MyView객체를 생성해서 반환  

### 프론트 컨트롤러 V2 구현

경로 : /hello/servlet/web/frontcontroller/v2

```java
@WebServlet(name="frontControllerV2",urlPatterns = "/front-controller/v2/*")
public class FrontControllerV2 extends HttpServlet {

    Map<String,ControllerV2> controllerV2Map = new HashMap<>();

    public FrontControllerV2(){
        controllerV2Map.put("/front-controller/v2/members/new-form",new MemberFormControllerV2());
        controllerV2Map.put("/front-controller/v2/members/save",new MemberSaveControllerV2());
        controllerV2Map.put("/front-controller/v2/members",new MemberListControllerV2());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String requestURI = req.getRequestURI();

        ControllerV2 controller = controllerV2Map.get(requestURI);
        if(controller==null){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        //위는 이전 V1과 동일한 구조
        
        MyView myView = controller.process(req,resp); //생성된 MyView객체를 반환
        myView.render(req, resp); //생성된 MyView객체가 렌더링됨
    }
}
```

요청받은 URI를 통해 HashMap에 저장된 컨트롤러 객체를 꺼내서 해당 요청된 URI와 매핑된 컨트롤러가 있는지는 기존과 동일하다.  

이제 V2는 컨트롤러가 MyView를 반환하는데 해당 MyView객체를 render()만 수행해주면 된다.  
MyView가 이제 jsp를 forward해주는 역할을 하기 때문에 컨트롤러의 코드가 간결해 진 것이다.  

<br>

## 프론트 컨트롤러 패턴 V3

Version 3 : 컨트롤러가 서블릿 기술을 전혀 사용하지 않도록 변경 및 View 이름 중복 제거  

그러기 위해 request 객체를 사용하는 Model을 직접 만들고, 뷰이름까지 전달하는 객체(ModelView)를 생성할 것임  

__V3 구조__  

![image](https://github.com/9ony/9ony/assets/97019540/329c4eb3-3490-4087-b498-8b24994c37cd)

1. 프론트컨트롤러로 요청이오면 해당 컨트롤러를 호출
2. 컨트롤러는 ModelView를 반환함  
    ModelView는 Model과 View를 담는데  
    Model은 HashMap Type으로 req.setAttribute에 인자로 들어갈 값을 저장  
    View는 JSP경로를 담는데 prefix와 suffix를 제외한 논리적 주소를 담음
    ex) /WEB-INF/views/new-form.jsp -> new-form
3. 프론트컨트롤러는 생성된 ModelView객체를 전달받고 ModelView에서 View를 viewResolver에 보내서 물리적인 주소를 담은 MyView를 반환받는다.
4. 반환받은 MyView에 Model을 담아 render()해주면 클라이언트가 응답 받는다.  


### Model과 View를 담는 클래스

__ModelView.class__  
```java
public class ModelView {
    private String viewName; //뷰의 논리적 이름
    private Map<String, Object> model = new HashMap<>(); //기존 req.setAttribute에 들어갈 인자("이름",객체)

    public ModelView(String viewName){
        this.viewName = viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setModel(Map<String,Object> model){
        this.model=model;
    }

    public Map<String,Object> getModel(){
        return model;
    }
}
```

ModelView 객체는  
기존 req.setAtrribute에 인자로 들어가는 값을 Map 타입로 저장하는 Model과 뷰의 논리적인 이름을 저장하는 viewName을 가진 객체로 viewName과 Model을 조회하고 설정하는 메소드가 있는걸 볼 수 있다.  

### 컨트롤러V3 인터페이스

__ControllerV3.interface__

경로 : /web/frontcontroller/v3/ControllerV3.interface

```java
public interface ControllerV3 {

    //이제 컨트롤러는 들어온 파라미터만 받으면 되는 구조다.
    ModelView process(Map<String, String> param);
}
```

이전 V2와 다르게 HttpServeltRequest와 Response를 받지않고 Map 컬렉션타입의 param만 인자로 받고있다.   
그리고 해당 인터페이스에 process메서드는 이제 ModelView를 반환한다.

### 컨트롤러V3 구현

경로 : hello/servlet/web/frontcontroller/v3/

__MemberFormControllerV3.class__  

```java
public class MemberFormControllerV3 implements ControllerV3 {
    @Override
    public ModelView process(Map<String, String> param) {
        return new ModelView("new-form"); // new-form만 전달하고 주소의 완성은 책임 X
    }
}
```
modelView를 생성할 때 이제 논리적인 이름만 인자로 들어간다.  

__MemberSaveControllerV3.class__  

```java
public class MemberSaveControllerV3 implements ControllerV3 {

    private MemberRepo memberRepo = MemberRepo.getInstance();

    @Override
    public ModelView process(Map<String, String> param) {

        Member member = new Member(param.get("username"),Integer.parseInt(param.get("age")));
        memberRepo.save(member);

        ModelView mv = new ModelView("save-result");
        mv.getModel().put("member",member);
        return mv;
    }
}
```

__MemberListControllerV3.class__  

```java
public class MemberListControllerV3 implements ControllerV3 {

    private MemberRepo memberRepo = MemberRepo.getInstance();

    @Override
    public ModelView process(Map<String, String> param) {
        List<Member> members = memberRepo.findMemberAll();

        ModelView mv = new ModelView("members");
        mv.getModel().put("members",members);
        return mv;
    }
}
```

회원 등록,저장 및 조회 컨트롤러들 전부다 이제 HttpServlet에 종속성에서 벗어나 순수 자바코드로 구성된다.  

### myView (렌더링 객체)
```java
public class MyView {
    private String viewPath;

    public MyView(String viewPath){
        this.viewPath = viewPath;
    }

    public void render(HttpServletRequest req , HttpServletResponse resp) throws IOException, ServletException{
        RequestDispatcher disp = req.getRequestDispatcher(viewPath);
        disp.forward(req,resp);
    }

    public void render(HttpServletRequest req, HttpServletResponse resp,Map<String, Object> model) throws IOException,ServletException{
        modelToReqestAttributeSet(model,req);
        RequestDispatcher disp = req.getRequestDispatcher(viewPath);
        disp.forward(req,resp);
    }

    //모델에 있는 값을 request에 추가한다.
    public void modelToReqestAttributeSet(Map<String, Object> model, HttpServletRequest req){
        model.forEach((key,val)->req.setAttribute(key,val));
    }
}
```

V3는 렌더링 시 ModelView안에 model을 추가로 받는다.  
그 이유는 컨트롤러에서 req.setAttribute의 저장소가 모델역할을 했지만  
이제 ModelView에서 처리한다.  
하지만 jsp는 request에서 값을 추출하여 렌더링 하며,  
그전에 model값을 request 저장소에 modelToReqestAttributeSet()메서드를 통해 넣어준다. 

### 프론트 컨트롤러 V3 구현

경로 : /hello/servlet/web/frontcontroller/

```java
@WebServlet(name="frontControllerV3",urlPatterns = "/front-controller/v3/*")
public class FrontControllerV3 extends HttpServlet {

    Map<String, ControllerV3> controllerV3Map = new HashMap<>();

    public FrontControllerV3(){
        controllerV3Map.put("/front-controller/v3/members/new-form",new MemberFormControllerV3());
        controllerV3Map.put("/front-controller/v3/members/save",new MemberSaveControllerV3());
        controllerV3Map.put("/front-controller/v3/members",new MemberListControllerV3());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String requestURI = req.getRequestURI();

        ControllerV3 controller = controllerV3Map.get(requestURI);
        if (controller == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        /* 다른로직들은 컨트롤러에서 호출하고 모델뷰에서 호출하고 레벨이큰데
        해당 코드는 디테일하므로 함수화 시키자.
        Map<String,String> param = new HashMap<>();
        req.getParameterNames().asIterator().forEachRemaining(
                ParamName->param.put(ParamName,req.getParameter(ParamName)));
        */

        Map<String,String> param = craeteParamMap(req);

        ModelView mv = controller.process(param);
        MyView myView = new MyView(viewResolver(mv.getViewName()));
        myView.render(req,resp,mv.getModel());
    }
    
    //parammeter Map 생성
    private Map<String,String> craeteParamMap(HttpServletRequest req){
        Map<String,String> param = new HashMap<>();
        req.getParameterNames().asIterator().forEachRemaining(
                ParamName->param.put(ParamName,req.getParameter(ParamName)));
        return param;
    }
    
    //논리이름을 물리이름으로 생성
    private String viewResolver(String viewName){
        String prefix = "/WEB-INF/views/";
        String suffix = ".jsp";
        return  prefix+viewName+suffix;
    }
}
```

URI매핑과 매핑에 결과 유무까지는 현재 같은 이전과 같은 로직이고  
요청으로 들어온 파라미터를 컨트롤러에 Map<String, Object> 타입을 인자로 보내줘야하는데 craeteParamMap()메서드를 통해 parameter Map을 생성해서 컨트롤러에 인자로 넣어준다.  
컨트롤러에서 해당 로직을 실행하고 ModelView를 반환 후 ModelView의 View(논리적 뷰 이름)를 viewResolver를 통해 물리적 주소로 변경 후 myView객체를 생성하고 render()를 할때 Model값을 넣어주어서 reqeust에 저장한다.  

V3의 컨트롤러는 이제 서블릿 종속성에서 벗어낫고 View이름이 중복되는 코드들의 접두사와 접미사를 viewResolver에서 처리함으로써 나중에 경로가 변경되어도 viewResolver의 접두사와 접미사만 변경하면 된다.  


### 프론트 컨트롤러 패턴 V4

Version 4 : 이전 컨트롤러는 ModelView를 생성해서 반환했었다.  
개발하는 입장에서 이러한 부분도 번거로울수 있다. 이러한 부분을 프론트 컨트롤러에서 처리해주고 컨트롤러는 뷰의 논리적인 이름만 반환하게 끔 구성해보자.  

__V4 구조__  

![image](https://github.com/9ony/9ony/assets/97019540/94bfd52c-d0e8-4fe4-b851-3f5ec129cba4)

1. 매핑정보를 조회해 컨트롤러를 찾는 것까지는 이전과 동일
2. 컨트롤러 호출 시에 파라미터 정보와 모델을 넘기는데 이때 모델을 생성만 해서 넘겨주고 컨트롤러는 이 모델에 값을 채워주기만 하면되고, 뷰의 논리적인 이름만 반환함  
3. 컨트롤러가 반환한 뷰네임을 viewResolver를 통해 myView 생성  
4. 렌더링 후 HTML응답

해당 구조는 2번째가 핵심이라 볼 수 있다.  

### 컨트롤러V4 인터페이스

__ControllerV4.interface__

경로 : /web/frontcontroller/v4/ControllerV4.interface

```java
public interface ControllerV4 {

    public interface ControllerV4 {
    
    String process(Map<String,String> paramMap, Map<String,Object> model);
}
}
```


### 컨트롤러V4 구현

경로 : hello/servlet/web/frontcontroller/v4/

__MemberFormControllerV4.class__  

```java
public class MemberFormControllerV4 implements ControllerV4 {
    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        return "new-form";
    }
}
```

위 회원등록 페이지를 반환하는 컨트롤러를 보면 파라미터와 모델을 받고 뷰네임만을 반환한다.  

__MemberSaveControllerV4.class__  

```java
public class MemberSaveControllerV4 implements ControllerV4 {

    private MemberRepo memberRepo = MemberRepo.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        Member member = new Member(paramMap.get("username"),Integer.parseInt(paramMap.get("age")));
        memberRepo.save(member);

        model.put("member",member);
        return "save-result";
    }
}
```

paramMap으로 전달받은 데이터를 이용하여 회원등록을 하고 전달받은 model에 member객체를 넣어주는 것을 볼 수있다.  

__MemberListControllerV4.class__  

```java
public class MemberListControllerV4 implements ControllerV4 {

    private MemberRepo memberRepo = MemberRepo.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        List<Member> members = memberRepo.findMemberAll();
        model.put("members",members);
        return "members";
    }
}
```


### 프론트 컨트롤러 V4 구현

경로 : /hello/servlet/web/frontcontroller/v4/

```java
생성자는 이전과 같은 구조...

@Override
protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    String requestURI = req.getRequestURI();

    ControllerV4 controller = controllerV4Map.get(requestURI);
    if (controller == null) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

    Map<String, String> param = craeteParamMap(req);
    Map<String, Object> model = new HashMap<>(); //모델을 생성

    String viewName = controller.process(param, model); //컨트롤러에 파라미터와 모델을 넘겨준다.
    MyView myView = new MyView(viewResolver(viewName));
    myView.render(req, resp, model);
}
craeteParamMap,viewResolver 메서드도 동일... 
```

V4 컨트롤러를 보면 이제 ModelView를 컨트롤러에서 생성하지 않고 전달받은 model에 jsp 렌더링시 필요한 값들을 넣어주고 뷰이름만을 반환하는 것을 볼 수 있다.  
이전 버전보다 훨씬 개발자 입장에서 편해진 것을 볼 수있다.  

`프레임워크나 공통 기능이 수고로워야 사용하는 개발자가 편리해진다`

## 컨트롤러 패턴 V5

Version V5 : V3와 V4의 컨트롤러는 전혀 다른 인터페이스를 사용하므로 서로 상호 호환이 될수가 없는 구조이다. 그래서 프론트 컨트롤러는 한가지 방식의 컨트롤러만 사용할 수 있는데, `어댑터 패턴을 사용`해서 다양한 방식의 컨트롤러를 처리할 수 있게 바꿔보자.  

__V5 구조__  

![image](https://github.com/9ony/9ony/assets/97019540/f011559f-3163-44ca-aa4a-f4241814a0ac)

1. 클라이언트에게 요청이 들어오면 핸들러(컨트롤러)매핑 정보를 확인한다.  
2. 해당 핸들러를 처리할 수 있는 어댑터 목록을 확인한다.  
3. 그 후 프론트 컨트롤러에서 핸들러 어댑터에게 해당 핸들러를 보내면 핸들러 어댑터가 핸들러를 호출하고 ModelView를 반환받은 것을 프론트 컨트롤러에게 넘겨준다.  
4. ModelView를 이용한 렌더링은 그전 과정과 동일하다.  

### MyHandlerAdapter 인터페이스

```java
public interface MyHandlerAdapter {
    //어댑터가 해당 컨트롤러를 처리할 수 있는지 판단하는 기능
    //여러 컨트롤러를 받기위해 Object Type
    boolean supports(Object handler);
    //MyHandlerAdapter를 통해 실제 컨트롤러를 호출
    ModelView handle(HttpServletRequest req, HttpServletResponse resp,Object handler)
            throws ServletException, IOException;

}
```

- supports() : 핸들러 어댑터가 해당 컨트롤러를 처리할수있는지 유무를 반환하는 메서드
- handle() : 핸들러어댑터가 핸들러를 호출하는 메서드이고 핸들러가 생성한 모델뷰를 반환받는다.  

### ControllerV3HandlerAdapter.class (MyHandlerAdapter 인터페이스 구현체)

```java
public class ControllerV3HandlerAdapter implements MyHandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV3);
    }

    @Override
    public ModelView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws ServletException, IOException {
        ControllerV3 controller = (ControllerV3) handler;

        Map<String,String> param = craeteParamMap(req);
        ModelView mv = controller.process(param);
        return mv;
    }

    //parammeter Map 생성
    private Map<String,String> craeteParamMap(HttpServletRequest req){
        Map<String,String> param = new HashMap<>();
        req.getParameterNames().asIterator().forEachRemaining(
                ParamName->param.put(ParamName,req.getParameter(ParamName)));
        return param;
    }
}
```

- supports()를 구현한 것을 보면 인자로들어오면 handler과 ControllerV3와 일치하는 인스턴스인지 확인하고 그에 따른 True,False를 반환한다.  
- 기존에 프론트 컨트롤러에서 처리하던 작업을 핸들러 어댑터가 하는데,  
handle()인자로 들어온 handler를 ControllerV3로 형변환(캐스팅) 후 controller(ControllerV3)에 파라미터를 생성하여 넘겨줘서 ModelView를 반환받은 것을 반환한다.  
- craeteParamMap() V4에서 request로 들어온 파라미터를 Map으로 생성하는 메서드  

### FrontControllerV5.class 전체 코드

```java
@WebServlet(name = "frontControllerV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerV5 extends HttpServlet {
    //Map<String, ControllerV3> controllerV3Map = new HashMap<>();
    //Map<String, ControllerV4> controllerV4Map = new HashMap<>();
    Map<String, Object> handlerMappingMap = new HashMap<>();
    List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    FrontControllerV5() {
//        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
//        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
//        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
        initHandlerMapping();
        initHandlerAdapters();
//        handlerAdapters.add(new ControllerV3HandlerAdapter());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String requestURI = req.getRequestURI();
        Object handler = handlerMappingMap.get(requestURI);

        if (handler == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyHandlerAdapter myHandlerAdapter = getHandlerAdapter(handler);
        ModelView mv = myHandlerAdapter.handle(req,resp,handler);
        MyView myView = new MyView(viewResolver(mv.getViewName()));
        myView.render(req, resp,mv.getModel());
    }
    private void initHandlerMapping(){
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
    }

    private void initHandlerAdapters(){
        handlerAdapters.add(new ControllerV3HandlerAdapter());
    }

    private String viewResolver(String viewName){
        String prefix = "/WEB-INF/views/";
        String suffix = ".jsp";
        return prefix + viewName + suffix;
    }
    private MyHandlerAdapter getHandlerAdapter(Object handler){
        for(MyHandlerAdapter myHandlerAdapter : handlerAdapters){
            if(myHandlerAdapter.supports(handler)){
                return myHandlerAdapter;
            }
        }
        throw new IllegalArgumentException(handler+"를 수행할 어댑터를 찾을 수 없습니다.");
    }
}
```

- 분할 설명

```java
    //Map<String, ControllerV3> controllerV3Map = new HashMap<>();
    //Map<String, ControllerV4> controllerV4Map = new HashMap<>();
    Map<String, Object> handlerMappingMap = new HashMap<>();
    List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();
```
해당 코드를 보면 기존 controllerVxMap->handlerMappingMap으로 변경되었는데, 그 이유가 이제 V3와 V4 둘다 사용하기 때문에 넓은의미인 handler라는 이름으로 하였고, Map의 Value값 타입도 Object로 변경했다.   
추가로 나중에 컨트롤러들을 처리하는 어댑터가 다수 존재할 것이기 때문에 어댑터를 저장하는 handlerAdapters 생성  

```java
    FrontControllerV5() {
        //handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        //handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        //handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
        //handlerAdapters.add(new ControllerV3HandlerAdapter());
        initHandlerMapping();
        initHandlerAdapters();

    }
    private void initHandlerMapping(){
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
    }

    private void initHandlerAdapters(){
        handlerAdapters.add(new ControllerV3HandlerAdapter());
    }
```

여러 컨트롤러 매핑과 그에 필요한 어댑터를 생성해주는 메서드로 WAS부팅 or 최초요청시 메서드 실행  

```java
    private MyHandlerAdapter getHandlerAdapter(Object handler){
        for(MyHandlerAdapter myHandlerAdapter : handlerAdapters){
            if(myHandlerAdapter.supports(handler)){
                return myHandlerAdapter;
            }
        }
        throw new IllegalArgumentException(handler+"를 수행할 어댑터를 찾을 수 없습니다.");
    }
```
핸들러(컨트롤러)를 처리할 수 있는 어댑터가 있으면 반환하는 메서드

```java
@Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String requestURI = req.getRequestURI();
        Object handler = handlerMappingMap.get(requestURI);

        if (handler == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyHandlerAdapter myHandlerAdapter = getHandlerAdapter(handler);
        ModelView mv = myHandlerAdapter.handle(req,resp,handler);
        MyView myView = new MyView(viewResolver(mv.getViewName()));
        myView.render(req, resp,mv.getModel());
    }
```

- 요청 URI를 이용한 핸들러 조회로직
    ```java
        String requestURI = req.getRequestURI();
        Object handler = handlerMappingMap.get(requestURI);
        if (handler == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    ```
    이전과 거의 동일하지만 반환타입이 `Object`

- 핸들러를 인자로 어댑터 조회 및 호출
    ```java
    MyHandlerAdapter myHandlerAdapter = getHandlerAdapter(handler);
    ModelView mv = myHandlerAdapter.handle(req,resp,handler);
    ```

이렇게 V3 컨트롤러를 사용할 수 있는 어댑터를 생성해 보았다.  

### ControllerV4HandlerAdapter.class (MyHandlerAdapter 인터페이스 구현체)

```java
public class ControllerV4HandlerAdapter implements MyHandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV4);
    }

    @Override
    public ModelView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws ServletException, IOException {
        ControllerV4 controller = (ControllerV4) handler;
        Map<String, String> param = craeteParamMap(req);
        Map<String, Object> model = new HashMap<>();

        String viewName = controller.process(param, model);
        ModelView mv = new ModelView(viewName);
        mv.setModel(model);

        return mv;
    }

    //parammeter Map 생성
    private Map<String,String> craeteParamMap(HttpServletRequest req){
        Map<String,String> param = new HashMap<>();
        req.getParameterNames().asIterator().forEachRemaining(
                ParamName->param.put(ParamName,req.getParameter(ParamName)));
        return param;
    }
}
```

V4컨트롤러는 뷰네임만을 반환한다. 그래서 아래코드와 같이 뷰네임으로 ModelView를 생성하고, 추가로 setModel을 해주는 것이다.
```java
String viewName = controller.process(param, model);
ModelView mv = new ModelView(viewName);
mv.setModel(model);
```

```java
@WebServlet(name = "frontControllerV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerV5 extends HttpServlet {
    //Map<String, ControllerV3> controllerV3Map = new HashMap<>();
    //Map<String, ControllerV4> controllerV4Map = new HashMap<>();
    Map<String, Object> handlerMappingMap = new HashMap<>();
    List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    FrontControllerV5() {
//        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
//        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
//        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
        initHandlerMapping();
        initHandlerAdapters();
//        handlerAdapters.add(new ControllerV3HandlerAdapter());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String requestURI = req.getRequestURI();
        Object handler = handlerMappingMap.get(requestURI);

        if (handler == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyHandlerAdapter myHandlerAdapter = getHandlerAdapter(handler);
        ModelView mv = myHandlerAdapter.handle(req,resp,handler);
        MyView myView = new MyView(viewResolver(mv.getViewName()));
        myView.render(req, resp,mv.getModel());
    }
    private void initHandlerMapping(){
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
        //V4 컨트롤러 추가
        handlerMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members/save", new MemberFormControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members", new MemberFormControllerV4());
    }

    private void initHandlerAdapters(){
        handlerAdapters.add(new ControllerV3HandlerAdapter());
        //V4 어댑터
        handlerAdapters.add(new ControllerV4HandlerAdapter());
    }

    private String viewResolver(String viewName){
        String prefix = "/WEB-INF/views/";
        String suffix = ".jsp";
        return prefix + viewName + suffix;
    }
    private MyHandlerAdapter getHandlerAdapter(Object handler){
        for(MyHandlerAdapter myHandlerAdapter : handlerAdapters){
            if(myHandlerAdapter.supports(handler)){
                return myHandlerAdapter;
            }
        }
        throw new IllegalArgumentException(handler+"를 수행할 어댑터를 찾을 수 없습니다.");
    }
}
```
컨트롤러 V4를 추가하는데 있어서 initHandlerMapping(),initHandlerAdapters()에 컨트롤러와 어댑터를 추가한것 말고는 코드 변경사항이 없다.  
핸들러와 어댑터를 설정하는 것도 만약 외부주입으로 변경한다면 완벽한 OCP(개방폐쇄원칙)을 지킬수 있을 것이다.  

# 정리

- V1 : 프론트 컨트롤러를 도입  
    기존 구조를 최대한 유지하면서 프론트 컨트롤러를 도입  

- V2 : View 분류  
    단순 반복 되는 뷰 로직 분리  

- V3 : Model 추가  
    서블릿 종속성 제거  
    뷰 이름 중복 제거  

- V4 :단순하고 실용적인 컨트롤러  
    v3와 거의 비슷  
    구현 입장에서 ModelView를 직접 생성해서 반환하지 않도록 편리한 인터페이스 제공  

- V5 : 유연한 컨트롤러  
    어댑터 도입  
    어댑터를 추가해서 프레임워크를 유연하고 확장성 있게 설계  

__어노테이션을 활용한 컨트롤러__
    어노테이션을 지원하는 어댑터만 만들면됨!  
    기존 구조를 유지하면서 확장가능  

이렇게 프론트 컨트롤러를 도입해서 ViewResolver, Controller의 구현과 역할 분리, Model객체 추가, 어댑터패턴 등등이 스프링MVC의 핵심구조를 파악하는데 필요한 부분을 학습하였다.  