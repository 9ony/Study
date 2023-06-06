## Validation (검증)

이때 까지 클라이언트에서 폼에 데이터를 입력해서 서버에 요청할 때 상품명이 비었는지,가격 제한,상품 수량 등등을 체크하는 로직이 없었다.  
아래 요구사항을 토대로 검증하는 절차를 가지려고 한다.  

__요구 사항__  
- 타입검증
    - 숫자를 입력하는 칸에 문자 입력 시 오류 처리
- 필드검증
    - 상품명 ( 필수 , 공백 처리)
    - 1,000 <= 가격 <= 1,000,000
    - 수량은 최대 9999개
- 특정 필드의 범위를 넘어서는 검증
    - 가격 * 수량의 합은 10,000원 이상  

이제 위의 요구사항에 적합하지 않을 시에 우리는 에러페이지가 아니라 `어떤 오류가 발생`했는지 사용자에게 알려주고 입력했던 데이터를 유지한 채로 해당 폼으로 돌려보내야 한다.  
이렇게 하지 않으면 실수로 사용자가 잘못 입력했다면 처음부터 다시 입력해야하기 때문에 사용자 입장에서 너무 불편할 것이다.  

❗ 이러한 검증절차는 서버에서 뿐만 아니라 프론트단에서 자바스크립트등으로 처리를 해줘야 하며, 프론트에서 했다고 해서 백단에서는 하지 않으면 안된다.  

프론트에서만 한다면 폼 입력을 이용안하고도 서버로 데이터를 충분히 보낼 수 있고 자바스크립트로 작성한 검증은 소스에 노출이 되며 예를들어 결제같은 기능에서 금액이나 수량,최종결제액등을 인위적으로 조작 할 수도 있기때문에 보안에 취약하다.  

백엔드(서버)에서만 처리한다면 프론트단에서 데이터를 검증해서 미리 서버로 오는걸 사전에 막아줄수도 있는데 이러한 데이터를 계속 받고 검증을 거친후 오류 표시를 해줘야 하기 때문에 사용성이 많이 떨어진다.  

그래서 반드시 검증절차는 양쪽에서 처리해줘야 한다!!  

`컨트롤러`에서는 이러한 검증절차가 매우 중요하다!  
또 서버가 API방식을 사용한다면 적절한 오류를 응답결과에 잘 넣어줘야 한다.  

### 검증을 직접 개발하여 처리 해보자.  

검증 V1 : 직접 개발하여 검증을 처리  

__상품 등록 검증__  

addItem() 검증 로직 추가

```java
//검증 오류 결과를 보관
Map<String,String> errors = new HashMap<>();

//검증 로직
//상품 이름 공백 검증
if(!StringUtils.hasText(item.getItemName())){
    errors.put("itemName","상품 이름은 필수입니다!.");
}
//상품 가격범위 검증
if(item.getPrice() == null ||item.getPrice()<1000 || item.getPrice()>1000000){
    errors.put("price","상품 가격은 1000원 이상 100만원이하여야 합니다!");
}
//상품 수량 검증
if(item.getQuantity()==null || item.getQuantity()>9999){
    errors.put("quantity","상품수량은 9999개 이하여야 합니다.");
}

//특정 필드가 아닌 복합 필드 검증
if(item.getPrice() != null && item.getQuantity() != null){
    int resultPrice = item.getPrice() * item.getQuantity();
    if(resultPrice < 10000){
        errors.put("globalError","가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 ="+resultPrice);
    }
}

//검즘 실패시 입력 폼
if(!errors.isEmpty()){
    log.info("errors ={}",errors);
    //이후 에러내용을 담은 모델에 데이터를 이용해 HTML에 표시해주자
    model.addAttribute("error",errors);
    return "validation/v1/addForm";
}
```

- 검증 테스트 결과 로그
![image](https://github.com/9ony/9ony/assets/97019540/3ed45a00-4229-4b21-b03b-37daa2fec620)  

- 입력 값
1. 상품이름 : "" , 가격:10000, 수량:1
2. 상품이름 : "" , 가격:1000 , 수량:1
3. 상품이름 : "TEST" , 가격: 10000 , 수량 : 1  

그리고 현재 검증에 실패하여 다시 등록폼으로 돌아왔을 때 입력했던 값이 유지된다.  
이는 처음에 등록폼으로 이동하는 메서드를 실행할때 빈객체인 Item을 넘겨주면서 타임리프에서 렌더링 되기 때문이다.    
이후 해당 검증로직 실패 시 @ModelAttribute로 인해 검증에 실패했던 Item이 model에 담겨서 다시 등록폼으로 돌아가기 때문에 값이 유지가 된다.  

__등록 폼 수정__  

에러 문자를 출력해보자.  

- 글로벌 에러 텍스트(복합 필드검증)
```html
<div th:if="${errors?.containsKey('globalError')}">
<p class="field-error" th:text="${errors['globalError']}">전체 오류 메시지</p>
</div>
```

- 상품명 에러 텍스트  
```html
<input type="text" id="itemName" th:field="*{itemName}" th:class="${errors?.containsKey('itemName')} ? 'form-control field-error' : 'form-control'" class="form-control" placeholder="이름을 입력하세요">

<div class="field-error" th:if="${errors?.containsKey('itemName')}" th:text="${errors['itemName']}">상품명 오류</div>
```

- 가격 , 상품은 위와 유사하므로 생략

- CSS 코드 추가
어떤 인풋태그가 에러인지 표시해주기 위해 CSS를 추가하자  
```css
.field-error {
    border-color: red;
    color: red;
}
```

?. 문법은 errors가 만약 null일 경우 containsKey가 작동하는 시점에 NPE에러가 발생한다.  
이때 ?.을 쓰게되면 NullPointerException 이 발생하는 대신, null 을 반환해준다.  
(Safe Navigation Operator 을 참고하자.)  

Model로 넘어온 errors(Map)에 해당 에러 key가 있으면 그 텍스트(value)를 출력해주는 타임리프를 작성했다.  

__결과 html__  
![image](https://github.com/9ony/9ony/assets/97019540/e2956696-6959-42d6-8378-7a40d19a53e0)

__정리__  
검증 오류가 발생하면 다시 입력 폼으로 돌아감  
검증 오류들을 사용자에게 오류 메세지 출력  
검증 오류가 발생해도 사용자가 입력한 데이터가 유지  

__남은 문제점__  
오류 데이터를 저장한 맵에서 불러오는 중복 코드가 많다.  
ex) ${errors?.containsKey('itemName')} 등..  
Integer를 String으로 입력하던지 같은 타입 오류 처리가 안됨  
이로 인해 입력한 데이터가 바인딩이 안되서 사라진다.  
사용자가 입력한 값도 별도로 관리가 되어야함.  

### BindingResult를 이용한 검증

검증 V2 : 스프링이 제공하는 검증 방법 중 하나를 알아보자. (핵심은 BindingResult)  

- addItemV1() 추가

파라미터에 이제 Model을 없애고 `BindingResult`를 추가하자.  

```java
public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes){
    ...
}
```

❗ BindingResult를 파라미터로 추가할때 중요한 점은 검증될 객체(Item) `바로 뒤`에 위치 시켜야 한다.  
그 이유는 bindingResult에 넣어주게 되는 Error 객체들이 @ModelAttribute의 필드들과 자동 맵핑되기 때문!!  
BindingResult는 Model에 자동으로 포함된다.  

```java
//상품 이름 공백 검증
if(!StringUtils.hasText(item.getItemName())){
    //errors.put("itemName","상품 이름은 필수입니다!.");
    // 필드에러는 new FieldError를 이용하여 담자.
    // FieldError(객체명,객체 필드명,메세지);
    bindingResult.addError(new FieldError("item","itemName","상품 이름은 필수입니다!!"));
}
//...  위와 비슷한 상품수량,가격 생략
//특정 필드가 아닌 복합 필드 검증
if(item.getPrice() != null && item.getQuantity() != null){
    int resultPrice = item.getPrice() * item.getQuantity();
    if(resultPrice < 10000){
        //errors.put("globalError","가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 ="+resultPrice);
        //글로벌 오류는 ObjectError에 담으면 된다.
        //ObjectError(객체명,메세지)
        bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 ="+resultPrice));
    }
}

//검즘 실패시 입력 폼
//bindingResult에 에러가 있다면 로직수행
if(bindingResult.hasErrors()){
    log.info("errors ={}",bindingResult);
    return "validation/v2/addForm";
}
// ...에러없을 시 정상로직 생략
```

필드에러나 객체에대한 에러가 있으면,
bindingResult.addError()를 통해 필드에러,오브젝트에러를 추가하자.  

- FieldError 생성자 파라미터 (String objectName, String field, String defaultMessage)
    1. objectName : @ModelAttribute 이름
    2. field : 오류가 발생한 필드 이름
    3. defaultMessage : 오류 기본 메시지

- ObjectError 생성자 파라미터 (String objectName, String defaultMessage)
    1. objectName : @ModelAttribute 의 이름
    2. defaultMessage : 오류 기본 메시지

__등록 폼 수정__  

ResultBinding에 담긴 에러 문자를 출력해보자.  
타임리프는 스프링의 BindingResult 를 활용해서 편리하게 검증 오류를 표현하는 기능을 제공한다. 

```html
 <div th:if="${#fields.hasGlobalErrors()}">
    <p class="field-error" th:each="err : ${#fields.globalErrors()}"
        th:text="${err}">글로벌 오류 메시지</p>
</div>
```

#fields: 스프링에서 만든 BindingResult를 타임리프에서 접근하기 위해 #fields를 이용한다.  
 
BindingResult에 글로벌에러가 있으면 필드에서 글로벌에러를 꺼내서 err을 추가하는거다.  
여기서 each를 써서 반복 출력한 이유는 글로벌에러가 많을수도 있기 때문이다.  
=> #fields.globalErrors() => List로 반환함  

__V1 에러 출력 타임리프 문법__  
```html
<input type="text" id="itemName" th:field="*{itemName}"
                   th:class="${errors?.containsKey('itemName')} ? 'form-control field-error' : 'form-control'"
                   class="form-control" placeholder="이름을 입력하세요">
<div class="field-error" th:if="${errors?.containsKey('itemName')}" th:text="${errors['itemName']}">
    상품명 오류
</div>
```

__V2 에러 출력 타임리프 문법__  
```html
<input type="text" id="itemName" th:field="*{itemName}"
                   th:errorclass="field-error" class="form-control"
                   placeholder="이름을 입력하세요">
<div class="field-error" th:errors="*{itemName}">
    상품명 오류
</div>
```

V1과 V2문 타임리프 문법을 비교해보자.  
- th:errorclass
    우선 기존 input태그를 보면 th:class에서 model로 넘어온 errors에 itemName이 키값인 데이터가 있다면 class명에 field-eroor를 추가한다.  
    변경된 input 태그를 보면 `th:errorclass`를 사용하고 추가될 클래스명 field-error를 추가했다.  
    이는 th:field 에서 지정한 필드에 오류가 있으면 class정보를 추가하는 것이다.  
- th:erorrs
    이는 th:if의 편의버전인데 명시된 필드 ${item.itemName}(=*{itemName})에 에러가 있을 경우  
    해당 태그가 출력되며 bindingResult설정한 디폴트메세지도 th:text로 출력된다.  
- th:field
    정상 상황에는 모델 객체의 값을 사용하지만,  
    오류가 발생하면 FieldError에서 보관한 값을 사용해서 값을 출력한다

### BindingResult란?

위에서 BindingResult를 이용해 데이터 검증을 해보았다.  
BindingResult에서 자세히 알아보자.  

`BindingResult`는 스프링이 제공하는 검증 오류를 보관하는 객체이고 검증 오류가 발생하면 `BindingResult`에 보관하면 된다.  
BindingResult 가 있으면 @ModelAttribute 에 데이터 바인딩 시 오류가 발생해도 컨트롤러가 호출된다.  

Item에서 price와 quantity는 Integer인데 등록폼에가서 문자를 입력해보면 컨트롤러가 호출 되는것을 볼 수 있다.

__콘솔에 해당 로그 출력됨__  
```text
Field error in object 'item' on field 'price': rejected value [ㅂㅂ]; codes [typeMismatch.item.price,typeMismatch.price,typeMismatch.java.lang.Integer,typeMismatch]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [item.price,price]; arguments []; default message [price]]; default message [Failed to convert property value of type 'java.lang.String' to required type 'java.lang.Integer' for property 'price'; nested exception is java.lang.NumberFormatException: For input string: "ㅂㅂ"]
Field error in object 'item' on field 'itemName': rejected value [null]; codes []; arguments []; default message [상품 이름은 필수입니다!!]
Field error in object 'item' on field 'price': rejected value [null]; codes []; arguments []; default message [상품 가격은 1000원 이상 100만원이하여야 합니다!]
Field error in object 'item' on field 'quantity': rejected value [null]; codes []; arguments []; default message [상품수량은 9999개 이하여야 합니다.]
```

BindingResult를 잠시 메소드 파라미터에서 지운 후에 동작하면 에러페이지가 뜨는것을 볼 수 있다.  
스프링에서 클라이언트에게서 전송받은 데이터를 Item객체로 바인딩하는데 Integer Type이어야 하는데 String Type으로 와서 바로 에러페이지(400 code)를 보내기 때문이다.  
즉, 컨트롤러를 호출하는 도중에 실패하게 된다.  

정리하자면,  
BindingResult 사용 : 오류 발생 시 BindingResult에 오류보관 후 컨트롤러 호출
BindingResult 미사용 : 오류 발생 시 Binding실패 후 400에러 페이지 반환

- BindingResult에 검증 오류를 적용하는 3가지 방법
    - 객체에 타입 오류등으로 바인딩이 실패하는 경우 스프링이 FieldError생성 후 BindingResult에 추가  
    - 개발자가 직접 추가  
        (여기까지 예제를 통해 해보았다.)  
    - Validator 사용

__BindingResult와 Errors__
- org.springframework.validation.Errors
- org.springframework.validation.BindingResult

BindingResult 인터페이스 는 Errors 인터페이스를 상속받고 있다.
실제 넘어오는 구현체는 BeanPropertyBindingResult이다.  
BindingResult 대신에 Errors 를 사용해도 되지만 Errors 인터페이스는 단순한 오류 저장과 조회
기능만을 제공하기 때문에 여기에 더해서 추가적인 기능들을 제공하는 BindingResult을 사용하도록 하자!!  


BindingResult , FieldError , ObjectError 를 사용해서 오류 메시지를 처리하는 방법을 알아보았다.
그런데 `검증 오류가 발생`하는 경우 `사용자가 입력한 내용이 모두 사라지고` 또 `타입 오류 시 다른 에러도 함께 출력`되었는데,  
FieldError , ObjectError에 해당 값이 담겨서 출력되었을것 같으니 이에 대해 자세히 한번 알아보자.  


### FieldError , ObjectError

사용자가 입력한 오류 데이터를 유지되게 만들어 보자.  

위에서 설명한 FieldError 생성자의 파라미터보다 더 많은 정보를 넣을 수 있다.  

```java
public FieldError(String objectName, String field, String defaultMessage);
public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable
Object[] arguments, @Nullable String defaultMessage);

public ObjectError(String objectName, String defaultMessage);
public ObjectError(String objectName, @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage);
```

- __파라미터 목록__
    - objectName : 오류가 발생한 객체 이름 (String)
    - field : 오류 필드 (String)
    - rejectedValue : 사용자가 입력한 값 (Object)
    - bindingFailure : true:타입 오류 같은 바인딩 실패인지, false:검증 실패인지 구분 값 (boolean)
    - codes : 메시지 코드 (String[])
    - arguments : 메시지에서 사용하는 인자 (Object[])
    - defaultMessage : 기본 오류 메시지 (String)

__addItem() 수정__  

```java
// 변경 전
bindingResult.addError(new FieldError("item","itemName","상품 이름은 필수입니다!!"));
...
// 변경 후
bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,null,null,"상품 이름은 필수입니다!!"));
bindingResult.addError(new FieldError("item","price",item.getPrice(),false,null,null,"상품 가격은 1000원 이상 100만원이하여야 합니다!"));
bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,null,null,"상품수량은 9999개 이하여야 합니다."));
```

3번째 파라미터값을 보면 item.getItemName()을 넣는 것을 볼 수 있다.  
그러면 이제 검증에러가 날 경우 현재 파라미터로 들어온 필드에 해당되는 값이 form에 다시 입력되어 있을 것이다.  
즉, 검증 에러가 났을때 사용자 입력값을 저장해주는 파라미터가 (rejectedValue)인걸 알 수 있다.  
그리고 bindingFailure는 타입오류는 아니기 때문에 false로 해주어야 한다.  


__정리__  
이제 검증범위가 넘어가는 값을 입력해도 사용자가 입력된 값이 저장되는 것을 볼 수 있다.  
FieldError 는 오류 발생시 사용자 입력 값을 저장하는 기능을 제공한다  
이제 타입에러 시에 출력되는 오류메시지를 제어해보자.  

### 오류코드와 메세지처리

필드에러를 처리할때 파라미터에 code와 argumnets를 받는것이 존재했다.  
이것을 이용하여 오류메세지도 메세지기능과 국제화기능처럼 properties에 일관성있게 관리할 수 있다.  
codes , arguments가 오류 발생시 오류 코드로 메시지를 찾기 위해 사용된다.  

__오류 메세지 설정 파일 생성__  

- application.properties 추가
```text
spring.messages.basename=messages,errors
```

- errors.properties

> src/main/resources/errors.properties

```text
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
```

codes : String[]{"required.item.itemName"} 를 사용해서 메시지 코드를 지정한다. (여러 메세지를 전달할 수 있기때문에 배열로 받는다.)   
arguments : Object[]{1000, 1000000} 를 사용해서 코드의 {0} , {1} 로 치환할 값을 전달한다.  

__변경 코드__  

- addItemV3()

```java
//상품 이름 공백 검증
if(!StringUtils.hasText(item.getItemName())){

    bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,new String[]{"required.item.itemName"},null,"잘못된 값입니다."));
}
//상품 가격범위 검증
if(item.getPrice() == null ||item.getPrice()<1000 || item.getPrice()>1000000){

    bindingResult.addError(new FieldError("item","price",item.getPrice(),false,new String[]{"range.item.price"},new Object[]{"100","100만원"},"잘못된 값입니다."));
}
//상품 수량 검증
if(item.getQuantity()==null || item.getQuantity()>9999){

    bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{"9999"},"잘못된 값입니다."));
}
if(resultPrice < 10000){
    bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{"10000",resultPrice}, "잘못된 값입니다."));
}
```

codes에 해당되는 메세지 코드가 없으면 이제 defaultMessage가 출력된다.  


### 오류 코드와 메시지 처리2

컨트롤러에서 BindingResult 는 검증해야 할 객체인 Item 바로 다음에 온다.  
따라서 BindingResult 는 이미 본인이 검증해야 할 객체인 target을 알고 있다.  
(BindingResult가 Item뒤에 위치해야할 중요한 이유이다.)  

__rejectValue() , reject()__  

FieldError , ObjectError는 new로 생성해서 addError()에 넣어주기에 너무 번거롭다.  
그래서 이제 BindingResult의 rejectValue() , reject() 메서드를 사용해보자.  

__변경 코드__  

- addItemV4()

```java
//상품 이름 공백 검증
if(!StringUtils.hasText(item.getItemName())){

    bindingResult.rejectValue("itemName", "required");
}
//상품 가격범위 검증
if(item.getPrice() == null ||item.getPrice()<1000 || item.getPrice()>1000000){

    bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
}
//상품 수량 검증
if(item.getQuantity()==null || item.getQuantity()>9999){
    bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
}

//특정 필드가 아닌 복합 필드 검증
if(item.getPrice() != null && item.getQuantity() != null){
    int resultPrice = item.getPrice() * item.getQuantity();
    if(resultPrice < 10000){
        bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
    }
}
```

코드가 훨씬 간결해졌고 객체명인 item도 없어진것을 볼 수 있다.  
왜냐하면 bindingResult는 이미 누가 타겟인지 알고 있기 때문이다.  

그리고 `codes도 풀네임이 아닌 일부분`만 적어 두었다.(이것은 메세지코드가 아니다)  
이 부분은 `MessageCodesResolver와 관련`되어 있는데 바로 알아보도록 하자.  

__MessageCodesResolver__  
- 자세한 오류코드 (코드+객체명+필드명)
```text
required.item.itemName = 상품 이름은 필수 입니다.
range.item.price = 상품의 가격 범위 오류 입니다.
```
- 단순한 오류코드 (코드)
```text
required = 필수 값 입니다.
range = 범위 오류 입니다.
```

단순하게 만들면 범용성이 좋아서 여러곳에서 사용할 수 있지만, 메시지를 세밀하게 작성하기 어렵다.  
반대로 너무 자세하게 만들면 범용성이 떨어진다.  

가장 좋은 방법 : 범용성으로 사용하다가, 세밀하게 작성해야 하는 경우에는 세밀한 내용이 적용되도록 메시지에 단계를 두는 방법

ex) 예를 들어서 required 라고 오류 코드를 사용한다고 가정해보자.  
- required 라는 메시지만 있으면?
```text
required = 필수 값 입니다.
```
 
> `필수 값 입니다.`를 사용  

- required.item.itemName 객체명과 필드명을 조합한 세밀한 메시지 코드와 같이 있다면?
```text
#Level1
required.item.itemName = 상품 이름은 필수 입니다.
#Level2
required = 필수 값 입니다.
```

> `상품 이름은 필수 입니다.`를 사용  

객체명과 필드명을 조합한 메시지가 있는지 우선 확인하고, 없으면 좀 더 범용적인 메시지를 선택하도록 추가 개발을 해야겠지만,  
범용성 있게 잘 개발해두면, 메시지의 추가 만으로 매우 편리하게 오류 메시지를 관리할 수 있을 것이다.  
스프링은 `MessageCodesResolver`라는 것으로 이러한 기능을 지원한다.

### MessageCodesResolver 테스트

MessageCodesResolver(인터페이스)
- 검증 오류 코드로 메시지 코드들을 생성  
- 기본 구현체는 DefaultMessageCodesResolver
- ObjectError , FieldError은 내부적으로 MessageCodesResolver를 사용함  

테스트를 통해 오류코드를 이용해 메세지 코드들을 어떻게 생성하는지 확인해보자.  

__MessageCodesResolverTest__

```java
public class MessageCodesResolverTest {
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject() {
        //생성된 메세지 코드를 반환
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        for (String messageCode : messageCodes) {
            System.out.println("messageCode="+messageCode);
        }
        // 반환된 메세지코드에 "required.item", "required"가 있는지 확인
        //단 containsExactly는 순서까지 고려해서 맞아야한다.
        Assertions.assertThat(messageCodes).containsExactly("required.item", "required");
    }
    @Test
    void messageCodesResolverField() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        for (String messageCode : messageCodes) {
            System.out.println("messageCode="+messageCode);
        }
        //bindingResult.rejectValue("itemName", "required");
        //itemName과 객체명은 이미 target되 있기때문에 알고 있으니 item 그리고 codes required가 들어간다.
        Assertions.assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );
    }
}
```


__DefaultMessageCodesResolver의 기본 메시지 생성 규칙__  

- 객체 오류
    __객체 오류의 경우 다음 순서로 2가지 생성__  
    code + "." + object name  
    code

    ex) codes: required, object name: item  
    우선순위  
    1: required.item  
    2: required  

- 필드 오류
    필드 오류의 경우 다음 순서로 4가지 메시지 코드 생성  
    code + "." + object name + "." + field  
    code + "." + field  
    code + "." + field type  
    code  
    ex) codes: typeMismatch, object name "user", field "age", field type: int  
    우선순위  
    1: typeMismatch.user.age  
    2: typeMismatch.age  
    3: typeMismatch.int  
    4: typeMismatch  

__MessageCodesResolver 동작 방식__  

rejectValue(),reject()는 내부에서 MessageCodesResolver를 사용  
여기에서 위에 객체오류,필드오류 예시처럼 `메시지 코드들을 생성`한다  
FieldError,ObjectError의 생성자를 보면, 오류 코드를 하나가 아니라 여러 오류 코드를 가질 수 있다.  
MessageCodesResolver를 통해서 생성된 순서대로 오류 코드를 보관한다.
보관된 코드를 확인하려면 form에서 타입오류를 내면 로그를통해 codes[...]에 메세지코드를 확인 할 수 있다.  

이렇게 타임리프에서 th:errors가 실행 될 때 오류가 있다면 생선된 오류 메세지 코드를 우선순위대로 돌면서 메세지를 찾는것이다.(없다면 defaultMessage)  

### 오류 코드 관리방법

`MessageCodesResolver`는 required.item.itemName처럼 상세한 거부터 먼저 생성하고 그후 덜 구체적인 required를 가장 늦게 생성한다.  
이렇게 오류코드 전략을 가져가면 메세지와 관련된 공통 전략을 편리하게 도입 가능하다.  

중요하지 않은 메세지는 범용성 있는 requried같은 메시지로,  
중요한 메시지는 구체적으로 적어서 사용하는 방식이 더 효과적  

__오류 코드 전략을 적용해보자!!__  

- errors.properties 수정  
```text
#required.item.itemName=상품 이름은 필수입니다.
#range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
#max.item.quantity=수량은 최대 {0} 까지 허용합니다.
#totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}

#=============================

#ObjectError

#Level1
totalPriceMin.item=상품의 가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}

#Level2 
totalPriceMin=전체 가격은 {0}원 이상이어야 합니다. 현재 값 = {1}

#=============================

#FieldError

#Level1 (오류코드 + 객체명 + 필드명)
required.item.itemName=상품 이름은 필수입니다.
range.item.price=상품 가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=상품 수량은 최대 {0} 까지 허용합니다.

#Level2 (오류코드 + 필드명)
required.itemName=이름은 필수입니다.
range.price=가격은 {0} ~ {1} 까지 허용합니다.
max.quantity=수량은 최대 {0} 까지 허용합니다.

#Level3 (오류코드 + 필드타입)
required.java.lang.String = 필수 문자입니다.
required.java.lang.Integer = 필수 숫자입니다.
min.java.lang.String = {0} 이상의 문자를 입력해주세요.
min.java.lang.Integer = {0} 이상의 숫자를 입력해주세요.
range.java.lang.String = {0} ~ {1} 까지의 문자를 입력해주세요.
range.java.lang.Integer = {0} ~ {1} 까지의 숫자를 입력해주세요.
max.java.lang.String = {0} 까지의 문자를 허용합니다.
max.java.lang.Integer = {0} 까지의 숫자를 허용합니다.

#Level4 (오류코드)
required = 필수 값 입니다.
min= {0} 이상이어야 합니다.
range= {0} ~ {1} 범위를 허용합니다.
max= {0} 까지 허용합니다.
```

객체오류와 필드오류를 레벨별로 작성해 보았다.  
`DefaultMessageCodesResolver`는 위의 Level1 ~ Level4 의 순서대로 메세지 코드를 생성하며,
구체적인 것부터 조회하게 되어있다.  

__결과__  
![image](https://github.com/9ony/9ony/assets/97019540/db107d01-eda4-4250-bdd1-2461c171440b)


__ValidationUtils__  
스프링 검증 유틸리티인 `ValidationUtils`으로 아래와 같이 공백or빈값일 경우의 간단한 검증 처리를 할 수 있다.  
```java
//Validation.rejectIfEmptyOrWhitespace() 메서드
public static void rejectIfEmptyOrWhitespace(
        Errors errors, String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {

    Assert.notNull(errors, "Errors object must not be null");
    Object value = errors.getFieldValue(field);
    if (value == null ||!StringUtils.hasText(value.toString())) {
        errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
    }
}

//addItemV4()
//상품 이름 공백 검증
if(!StringUtils.hasText(item.getItemName())){

    bindingResult.rejectValue("itemName", "required","잘못된 값 입니다.");
}
//위와 아래 동일한 결과
ValidationUtils.rejectIfEmptyOrWhitespace( bindingResult,"itemName","required","잘못된 값 입니다.");
```


__정리__  
ex) bindingResult.rejectValue("quantity", "max", new Object[]{9999}, "잘못된 값 입니다.");
위 코드로 예시를 들겠다.  
1. rejectValue() 호출이 된다.  
2. MessageCodesResolver를 사용해서 검증 오류코드인 로 메시지 코드들을 생성  
    => 검증오류코드("max"),객체(item),객체명("quantity"),type(Integer)을 이용해 생성됨  
3. new FieldError() 를 생성하면서 메시지 코드들을 보관
    => max.item.quantity , max.quantity , max.Integer , max  
4. th:erros 에서 메시지 코드들로 메시지를 순서대로 메시지에서 찾고, 노출
    => erros가 해당 item.quantity가 바인딩된 에러코드가 있으면 3번에서 생성한 메세지코드가 있는지 errors와 messages에서 찾게됨.  
    메세지 코드가 없다면 최종적으로 defaultMessage("잘못된 값 입니다.") 출력  
    ❗ `spring.messages.basename=messages,errors`에 messages도 있기 때문에 messages에서도 찾는다. 헷갈리지말자!! 양쪽에 만약 메세지코드를 겹쳐둔다면, messages에 있는 메세지코드의 텍스트가 출력된다.  


### 타입 오류 메세지 처리  

```text
1.
Field error in object 'item' on field 'price': rejected value [qqq 111]; codes [typeMismatch.item.price,typeMismatch.price,typeMismatch.java.lang.Integer,typeMismatch]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [item.price,price]; arguments []; default message [price]]; default message [Failed to convert property value of type 'java.lang.String' to required type 'java.lang.Integer' for property 'price'; nested exception is java.lang.NumberFormatException: For input string: "qqq111"]
2.
Field error in object 'item' on field 'price': rejected value [null]; codes [range.item.price,range.price,range.java.lang.Integer,range]; arguments [1000,1000000]; default message [잘못된 값 입니다.]
```

위 1번의 로그는 price 필드에 타입에러가 났을때 뜨는 오류 메세제이다.  

codes에 들어가 있는 메세지코드를 보면 `typeMismatch.*`가 붙어있는걸 볼 수 있다.  
그리고 defaultMessage는 우리가 타입을 잘못 입력했을때 아래에 떳던 메세지와 동일하다.  

__스프링은__  
타임에러에 관련된 처리를 스프링이 typeMismatch 라는 오류 코드를 사용하여 오류 메세지를 생성하는 것을 알 수있다.  
타임리프 th:errors가 typeMismatch.*을 메세지 코드를 찾고 없으니까 defaultMessage를 반환하는 것을 알 수 있다.  


__typeMismatch.* 오류 메세지 작성__  

typeMismatch 오류메세지를 작성해보자.  

- errors.properties 추가  
```text
# Type Error
#Level 1
typeMismatch.item.price=상품 가격에는 숫자를 입력해주세요.
typeMismatch.item.quantity=상품 수량에는 숫자를 입력해주세요.

#Level 2
typeMismatch.price=가격에는 숫자를 입력해주세요.
typeMismatch.quantity=수량에는 숫자를 입력해주세요.

#Level 3
typeMismatch.java.lang.Integer=숫자를 입력해 주세요.

#Level 4
typeMismatch = 타입 오류입니다.
```

__결과__  
![image](https://github.com/9ony/9ony/assets/97019540/490fd2cc-015c-473f-a9ba-3f9ac48292b1)

이제 타입처리도 오류메세지 설정만으로 적용된것을 볼 수있다.  
이렇게 소스코드를 건들이지 않고 메시지의 설정만으로 오류메세지 처리를 할 수 있다.  

### Validator

컨트롤러에 비즈니스를 위한 로직과 검증로직이 섞여있어서 가독성이 안좋다.  
검증로직과 컨트롤러의 핵심 로직을 Validator 인터페이스를 이용해 분리시켜보자.  

```java
public interface Validator {
    //
    boolean supports(Class<?> clazz);
    //
    void validate(Object target, Errors errors);
}
```
스프링은 검증에 특화된 Validator 검증기 인터페이스를 제공   
supports() : 해당 검증기가 검증하려는 객체를 지원유무  
validate() : 실제 검증할 객체와 BindingResult 를 넘겨서 검증을 수행하고 오류처리  
(참고 :  Errors 는 BindingResult 의 상위 인터페이스)

- __ItemValidator.class__  
~/web/validation/ItemValidator.class

```java
@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
        // isassignableFrom은  "=="과 달리 clazz가 Item인 경우와 그의 자식클래스도 true로 반환된다.
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;
        BindingResult bindingResult = (BindingResult) errors;

        //검증 로직
        //상품 이름 공백 검증
        if(!StringUtils.hasText(item.getItemName())){

            bindingResult.rejectValue("itemName", "required","잘못된 값 입니다.");
        }
        //상품 가격범위 검증
        if(item.getPrice() == null ||item.getPrice()<1000 || item.getPrice()>1000000){

            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, "잘못된 값 입니다.");
            for(String codes : bindingResult.resolveMessageCodes("range","price")){
                System.out.println("codes = " + codes);
            }
        }
        //상품 수량 검증
        if(item.getQuantity()==null || item.getQuantity()>9999){
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, "잘못된 값 입니다.");
        }

        //특정 필드가 아닌 복합 필드 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, "잘못된 값 입니다.");
            }
        }
    }
}
```
- __addItemV5()__  

```java
//생략...
log.info("objectName={}", bindingResult.getObjectName());
log.info("target={}", bindingResult.getTarget());

//검증로직 분리
itemValidator.validate(item,bindingResult);

//검즘 실패시 입력 폼
if(bindingResult.hasErrors()){
    log.info("errors ={}",bindingResult);
    return "validation/v2/addForm";
}
//생략...
```


`ItemValidator`에 Validator 인터페이스를 상속받아서 컨트롤러 로직과 Item검증로직을 분리해보았다.  
아까 보다 컨트롤러 코드가 훨씬 깔끔해지고 Item을 검증해야되는 다른 메서드에 재사용도 간편하게 할 수 있다.  

ex) 수정할때도 Item검증이 필요하므로 재사용 가능 (타임리프도 수정해야함)

```java
@PostMapping("/{itemId}/edit")
public String edit(@PathVariable Long itemId, @ModelAttribute Item item, BindingResult bindingResult) {
    itemValidator.validate(item,bindingResult);

    //검즘 실패시 입력 폼
    if(bindingResult.hasErrors()){
        log.info("errors ={}",bindingResult);
        return "validation/v2/editForm";
    }
    itemRepository.update(itemId, item);
    return "redirect:/validation/v2/items/{itemId}";
}
```

하지만 꼭 Validator 인터페이스를 상속안받고 itemValidator를 생성해서 분리해도 가능한데,
Validator를 상속받는 이유가 뭘까?  

### @Validated 

스프링이 Validator 인터페이스를 별도로 제공하는 이유는 체계적으로 검증 기능을 도입하기 위해서다.  
또 컨트롤러 로직에서 validate를 호출안하고 생략할 수 있다.  
ex) itemValidator.validate(item,bindingResult)를 호출안해도 됨  
스프링의 추가적인 도움을 받을 수 있다.   
코드로 확인해보자.  

__WebDataBinder 사용__  
`WebDataBinder`는 스프링의 파라미터 바인딩의 역할 및 검증 기능도 내부에 포함한다.  

컨트롤러에 아래 코드 추가  
```java
@InitBinder
public void init(WebDataBinder dataBinder) {
    log.info("init binder {}", dataBinder);
    dataBinder.addValidators(itemValidator);
}
```

@InitBinder 해당 컨트롤러에 적용
=> 전역설정은 따로 설정해야한다.  
WebDataBinder에 검증기를 추가하면 해당 컨트롤러에서는 검증기를 자동으로 적용한다.  

```java
@PostMapping("/add")
public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    //검즘 실패시 입력 폼
    if(bindingResult.hasErrors()){
        log.info("errors ={}",bindingResult);
        return "validation/v2/addForm";
    }
    //위 검증을 다 통과하면 성공 로직
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v2/items/{itemId}";
}
```

- __@Validated__  
    validator 호출 대신에 검증 대상 앞에 @Validated가 붙은걸 볼 수 있다.  
    `( @Validated @ModelAttribute Item item, ...  )`  
    - @Validated는 검증기를 실행하라는 애노테이션  
    - WebDataBinder 에 등록한 검증기를 찾아서 실행한다.  

- __supports()__  
    @Validated를 통해 검증기를 호출하는 부분을 생략했는데, 등록한 검증기가 많다면 어떻게 찾을 수 있을까?  
    이때 아래 메서드가 사용된다.  
    ```java
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
        // isassignableFrom은  "=="과 달리 clazz가 Item인 경우와 그의 자식클래스도 true로 반환된다.
    }
    ```
    `Item.class.isAssignableFrom(clazz)`로 해당 검증기로 처리 유무를 판단하는 것이다.  
    > supports(Item.class) 호출  
    true 이므로 ItemValidator 의 validate() 가 호출


__@InitBinder 전역 설정__

```java
@SpringBootApplication
public class ItemServiceApplication implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApplication.class, args);
    }
    @Override
    public Validator getValidator() {
        return new ItemValidator();
    }
}
```

컨트롤러에 `@InitBinder`를 제거해도 전역 설정으로 인해 정상적으로 동작한다.  
단, 전역설정 시 BeanValidator가 자동 등록되지 않는다.  

참고: 검증시 어노테이션으로 @Validated @Valid 둘다 사용가능하다.  
단 @Validated는 스프링 어노테이션이고 @Valid는 자바 표준 어노테이션이다.  

