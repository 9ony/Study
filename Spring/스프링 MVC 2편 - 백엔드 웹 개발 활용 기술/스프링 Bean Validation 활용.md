## Bean Validation

검증 기능을 아래와 같이 매번 작성하는 것은 매우 번거롭다.  

```java
//상품 이름 공백 검증
if(!StringUtils.hasText(item.getItemName())){
    errors.put("itemName","상품 이름은 필수입니다!.");
}
//상품 가격범위 검증
if(item.getPrice() == null ||item.getPrice()<1000 || item.getPrice()>1000000){
    errors.put("price","상품 가격은 1000원 이상 100만원이하여야 합니다!");
}
```
특히 일반적인 검증로직은 특정 필드에 대한 부분이 빈값인지 특정 범위를 초과하는지 검증하는 일반적인 로직이다.  

이런 일반적인 검증 로직을 모든 프로젝트에 적용할 수 있게 공통화 및 표준화한것이 Bean Validation이다.  

Bean Validation을 활용하면, 어노테이션 하나로 검증로직을 편리하게 이용 가능하다.  

ex)
```java
@NotBlank
private String itemName;

@NotNull
@Range(min = 1000, max=100000)
private Integer price;

@NotNull
@Max(9999)
private Integer quantity;
```

> 해당 어노테이션은 의존관계 설정 후 사용가능

__BeanValidation이란?__  

검증 로직을 모든 프로젝트에 적용할 수 있게 공통화 및 표준화한 것  
Validation은 자바에서 지원하는 기술 표준이다.  
즉, 검증어노테이션과 여러 인터페이스 모임  

일반적으로 사용하는 구현체는 Hibernate Validator이다.  

- __하이버네이트 Validator 관련 링크__  
    - [공식 사이트](http://hibernate.org/validator/)
    - [공식 메뉴얼](https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/#validator-gettingstarted-createproject)
    - [검증 애노테이션 모음](https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/#validator-defineconstraints-spec)
    참고 : 위 링크는 6.2버전이며, 현재 8.0버전까지 나왔다.  

### Bean Validation 사용해보기

우선 테스트를 통해 순수 Bean Validation을 사용해보자.  

__gradle에 Bean Validation 추가__  
```text
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

```java
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@NotBlank
private String itemName;
@NotNull
@Range(max = 1000000,min = 1000)
private Integer price;
@NotNull
@Max(9999)
private Integer quantity;
```
- @NotBlank : 빈값 + 공백만 있는 경우를 허용하지 않는다.
- @NotNull : null 을 허용하지 않는다.
- @Range(min = 1000, max = 1000000) : 범위 안의 값이어야 한다.
- @Max(9999) : 최대 9999까지만 허용한다.

Range는 자바표준 어노테이션이 아니고 hibernate validator 구현체의 어노테이션이다.  
=> `org.hibernate.validator.`로 시작한다.  
실무에서 대부분 하이버네이트 validator를 사용  


__Bean Validation 테스트 코드 작성__   

- BeanValidationTest.class
src : hello.itemservice.validation  
```java
@Test
void beanValidation(){
    //검증기 생성
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.getValidator();
    
    //테스트 Item객체 생성
    Item item = new Item();
    item.setItemName(" "); //blank 공백
    item.setPrice(0); //range 초과
    item.setQuantity(10000); // max 초과

    //검증오류 출력
    Set<ConstraintViolation<Item>> violations = validator.validate(item);
    //violations = ConstraintViolation이라는 검증오류들을 담은 컬렉션 객체
    //validator.validate(item)가 Item의 검증오류 ConstraintViolation를 반환한다.
    for(ConstraintViolation<Item> violation : violations){
        System.out.println("violation = "+ violation);
        System.out.println("violation message = "+ violation.getMessage());
    }
}
```

검증기를 생성한 후 테스트를 위한 Item객체를 생성했다.  
이때 검증기를 생성하는 과정은 이후 스프링에서 사용할때는 직접 코드를 작성하지 않고 사용한다.  

유효성 검증을 하기 위해서는 Validator가 필요했다.  
이러한 Validator를 사용하기 위해선 ValidatorFactory가 Validator들을 관리하는데,  
ValidatorFactory에서 Validator를 가져와서 사용하는 것이다.  

Validator를 이용하여 유효성을 검증할때에는 고려할 부분이 많아서 필요한 객체가 상당히 많은데 검증하는 순간마다 해당 객체들을 생성하는것은 비용이 많이 들기 때문에 ValidatorFactory에서 Validator 인스턴스의 생성 및 초기화, 캐싱을 맡기는 것이다.  

[ValidatorFactory,Validator 참조 블로그](https://m.blog.naver.com/aservmz/222825933867)


validator.validate(item) : 검증기의 validate()메서드에 해당 객체를 넣으면 객체생성시 발생했던 ConstraintViolation이라는 검증오류들을 반환한다.  
이외에 아래와 같은 검증 메서드도 있다.  
validateProperty() : 객체의 특정 프로퍼티에 대해 유효성 검증  
validateValue() : 객체의 특정 프로퍼티의 특정 값에 대해 유효성 검증  

- 출력결과
```text
violation = ConstraintViolationImpl{interpolatedMessage='1000에서 1000000 사이여야 합니다', propertyPath=price, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{org.hibernate.validator.constraints.Range.message}'}
violation message = 1000에서 1000000 사이여야 합니다
violation = ConstraintViolationImpl{interpolatedMessage='9999 이하여야 합니다', propertyPath=quantity, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{javax.validation.constraints.Max.message}'}
violation message = 9999 이하여야 합니다
violation = ConstraintViolationImpl{interpolatedMessage='공백일 수 없습니다', propertyPath=itemName, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{javax.validation.constraints.NotBlank.message}'}
violation message = 공백일 수 없습니다
```

이렇게 ConstraintViolation를 조회하여 객체 생성시 발생한 오류정보들을 조회할 수 있다.  

> 참고 : 생성된 메세지(violation message)는 `Hibername validator가 기본적으로 제공하는 오류 메세지`(수정 가능)  


### 스프링 Bean Validation

- ItemSeviceController.class

```java
@PostMapping("/add")
public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    //검즘 실패시 입력 폼
    if(bindingResult.hasErrors()){
        log.info("errors ={}",bindingResult);
        return "validation/v3/addForm";
    }
    //위 검증을 다 통과하면 성공 로직
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v3/items/{itemId}";
}
```

html코드는 [Validation](./Validation%20%EA%B8%B0%EC%B4%88.md)에 했던 코드와 같다.  

이전에 했던 Validator를 등록하는 @InitBinder 와 전역설정을한 코드들은 충돌이 나기 때문에 주석처리하거나 지워주자  

- 아래 코드 주석 or 제거 필수!!

```java
Controller.class
//WebDataBinder에 검증기 등록
/*@InitBinder
public void init(WebDataBinder dataBinder) {
    log.info("init binder {}", dataBinder);
    dataBinder.addValidators(itemValidator);
}*/

Application.class
@SpringBootApplication
public class ItemServiceApplication{

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

    // implements WebMvcConfigurer 인터페이스 상속 후
    //Validator 전역설정
//	@Override
//	public Validator getValidator() {
//		return new ItemValidator();
//	}
}
```

해당 코드들을 주석처리 후 한번 실행해보자  

- __결과__

![image](https://github.com/9ony/9ony/assets/97019540/4e46ec39-ed6a-40ea-91de-c30bea7d31b7)

메세지는 우리가 이전에 설정했던 것과는 다르지만 검증이 정상적으로 처리된다.  
그 이유는 우리가 이전에 만든 `ItemValidator`를 사용하지 않고 스프링에서 등록한 Validator를 사용하기 때문이다.  

__검증기를 등록한적이 없는데 어디서 등록된걸까??__  

스프링 부트는 우리가 `build.gradle에 spring-boot-starter-validation 라이브러리를 넣으면 자동으로 Bean Validator를 인지하고 스프링에 통합`되며 이는 우리가 @SpringBootApplication에 전역설정한 것과 같이 `글로벌 Validator로 등록`된다.  
그래서 위에 글로벌 Validator를 등록하는 코드와 컨트롤러에 @InitBinder 부분을 주석처리 하거나 제거한 것이다.  


__검증 순서__  
1. @ModelAttribute 각각의 필드에 타입 변환 시도
    1. 성공하면 다음으로
    2. 실패하면 typeMismatch로 FieldError 추가 (바인딩 실패)
2. Validator 적용 (바인딩에 성공한 필드만 Bean Validation 적용)

BeanValidator는 바인딩에 실패한 필드는 BeanValidation을 적용하지 않는다.
(Type에러 시에 바인딩과정에서 실패했기 때문에 BeanValidation를 통한 검증은 의미가 없기 때문)  

- BeanValidation 적용 예시
    - itemName에 문자 "TEST" 입력  
        > 타입 변환 성공 itemName 필드에 BeanValidation 적용  
    - price에 문자 "qq" 입력  
        > "qq"를 숫자 타입 변환 시도 실패 typeMismatch FieldError 추가  
        price 필드는 BeanValidation 적용 X  


### Bean Validation - 오류 코드,오류 메세지

```java
log.info("bindingResult codes = {}",bindingResult.getFieldError());
```
컨트롤러 addItem 메서드에 해당 코드를 추가해서 출력해보자.  

❗ 입력데이터는 아무것도 넣지않고 요청함.

```text
- quantity
Field error in object 'item' on field 'quantity': rejected value [null]; codes [NotNull.item.quantity,NotNull.quantity,NotNull.java.lang.Integer,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [item.quantity,quantity]; arguments []; default message [quantity]]; default message [널이어서는 안됩니다]
- price 필드
Field error in object 'item' on field 'price': rejected value [null]; codes [NotNull.item.price,NotNull.price,NotNull.java.lang.Integer,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [item.price,price]; arguments []; default message [price]]; default message [널이어서는 안됩니다]
-ItemName 필드
Field error in object 'item' on field 'itemName': rejected value []; codes [NotBlank.item.itemName,NotBlank.itemName,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [item.itemName,itemName]; arguments []; default message [itemName]]; default message [공백일 수 없습니다]
```

codes가 어떻게 생성됬는지 보자.  

__Bean Validation codes__ 을 보면 해당 에러가 난 필드의 어노테이션을 주목해보자.  
  
- itemName 필드  
    @NotBlank에서 걸리므로 NotBlank.item.itemName,NotBlank.itemName ....
- price 필드
    @NotNull NotNull.item.price, ...생략
- quantity (price와 필드명 외에 동일)  
- @Range나 @Max에서 걸렸다면 Range.~ , Max.~  

마치 typeMismatch와 유사하게 생성해준다.  
 
> 이렇게 DefaultMessageCodesResolver가 어노테이션명과 객체명 필드명으로 메세지 코드를 작성해준다.

__오류 메시지 등록__  

errors.properties에 메세지를 추가해보자.  

```text
#Bean Validation 추가
NotBlank={0} 공백X
Range={0}, {2} ~ {1} 허용
Max={0}, 최대 {1}
```

❗ 참고로 Bean Validation은 errorArgs배열의 첫번째 값(`{0}`)은 필드이름이다.  

__BeanValidation 메시지 우선순위__  
1. 생성된 메시지 코드 순서대로 messageSource에서 메시지 조회
2. 애노테이션의 message 속성 사용 -> @NotBlank(message = "공백! {0}")
3. 라이브러리가 제공하는 기본 값 사용 공백일 수 없습니다.

### Bean Validation - Objejct Error

필드에 어노테이션을 붙여서 특정 필드에러는 처리를 하였다.  

__복합 필드 처리__
- 입력
![image](https://github.com/9ony/9ony/assets/97019540/205f3932-8608-4940-bcd3-b9e7c946cc2e)
- 결과
![image](https://github.com/9ony/9ony/assets/97019540/d0c6be08-b80f-46f8-85cd-35dc0ad2de2f)

> 복합필드 처리가 안된다.
당연한 결과 이때까지 ItemValidator에서 처리해줬기 때문

복합 필드는 처리는 @ScriptAssert()를 사용하거나 위와 같이 컨트롤러나 별도로 메서드를 만들어서 직접 작성하면 된다.  

- __@ScriptAssert 사용__
    
    - item.class에 @ScriptAssert어노테이션 추가
    ```java
    @Data
    @ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000")
    public class Item {
        //생략
    }
    ```

    - errors.properties 추가
    ```text
    # Object Error (ScriptAssert)

    ScriptAssert.item = 가격 * 수량은 10000원 이상 가능합니다22
    ScriptAssert = 가격 * 수량은 10000원 이상 가능합니다11  
    ```

    - __결과__  
    ![image](https://github.com/9ony/9ony/assets/97019540/41a500fb-7f47-4246-916b-62683218f880)  

- __bindingResult.reject() - 직접 작성__  
    - Controller addItem()에 해당 코드 추가
    ```java
    if (item.getPrice() != null && item.getQuantity() != null) {
        int resultPrice = item.getPrice() * item.getQuantity();
        if (resultPrice < 10000) {
        bindingResult.reject("totalPriceMin", new Object[]{10000,resultPrice}, null);
        }
    }
    ```
    > @ScriptAssert 어노테이션을 제거하자. 안한다면 에러 텍스트가 둘다 출력된다.  

    - errors.properties
    ```text
    totalPriceMin = 가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
    ```

    - __결과__
    ![image](https://github.com/9ony/9ony/assets/97019540/a756b530-8447-4fe7-9b67-a0aefa06c6b8)

실무에서 ObjectError는 제약사항이 많고 객체의 범위를 종종 넘어서는 경우도 있어서 @ScriptAssert보다 자바코드로 직접 작성하는 경우가 많다고 한다.

__editForm.html 과 edit() 수정 메서드 수정__  

수정 부분에서 이전에 사용했던 itemValidator가 남아있다.  
현재 itemValidator는 사용하지 않으니 `itemValidator.validate(item,bindingResult);`를 삭제하고 @Validated 어노테이션을 붙여주자.

```java
@PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId,@Validated @ModelAttribute Item item, BindingResult bindingResult) {
        //itemValidator.validate(item,bindingResult); // 주석 or 제거 


        //검즘 실패시 입력 폼
        if(bindingResult.hasErrors()){
            log.info("errors ={}",bindingResult);
            return "validation/v3/editForm";
        }
        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }
```

### Bean Validation의 한계

등록시 기존 요구사항  
- 타입 검증
    - 가격, 수량에 문자가 들어가면 검증 오류 처리
- 필드 검증
    - 상품명: 필수, 공백X
    - 가격: 1000원 이상, 1백만원 이하
    - 수량: 최대 9999
- 특정 필드의 범위를 넘어서는 검증
    - 가격 * 수량의 합은 10,000원 이상

수정시 요구사항  
- 타입 검증
    - 가격, 수량에 문자가 들어가면 검증 오류 처리
- 필드 검증
    - id: 필수X -> `필수, 등록할때 id와 수정시 id가 같아야함`
    - 상품명: 필수, 공백X
    - 가격: 1000원 이상, 1백만원 이하
    - 수량: 최대 9999 -> `제한없음`
- 특정 필드의 범위를 넘어서는 검증
    - 가격 * 수량의 합은 10,000원 이상

수정 시 요구사항으로 인해 Item 필드를 다음과 같이 수정해보자
1. id : @NotNull 추가  
2. quantity : @Max(9999) 제거

이러면 정상 동작을 하지만 등록에서 문제가 발생할 것이다.  
(등록시 id값이 없고 또 수량제한도 걸 수 없기 때문에)  


### Bean Validation - groups

동일한 모델 객체를 등록할 때와 수정할 때 각각 다르게 검증하려면
1. groups기능을 이용하는 방법
2. Item 객체를 직접 사용하지 않고 폼 전송을 위한 별도의 모델 객체를 만들어서 사용

__groups 적용__  
 
우선 인터페이스 2개를 만들자.  

- SaveCheck.interface
```java
public interface SaveCheck {
    //저장용 groups 생성
}
```

- UpdateCheck.interface
```java
public interface UpdateCheck {
    //수정용 groups 생성
}
```

이후 기존 addItem과 edit의 매핑어노테이션을 주석처리해주고 아래와 같이 새로 만들자.  

- addItemV2()
```java
@PostMapping("/add")
    public String addItemV2(@Validated(SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.info("bindingResult codes = {}",bindingResult.getFieldError());
        if (item.getPrice() != null && item.getQuantity() != null) {
        //...생략
        }

        //...생략
    }
```
- editV2()
```java
 @PostMapping("/{itemId}/edit")
    public String editV2(@PathVariable Long itemId, @Validated(UpdateCheck.class) @ModelAttribute Item item, BindingResult bindingResult) {
        //itemValidator.validate(item,bindingResult);
        
        //...생략
    }
    
```

등록과 수정 메서드의 @Validated 어노테이션에 SavedCheck 클래스와 UpdateCheck 클래스를 추가해주었다.  

- @Validated 
```java
public @interface Validated {
    
    //...

	Class<?>[] value() default {};
}
```

> Validated 어노테이션을 들어가보면 클래스를 받는 부분이 있다.  
❗ 참고: @Valid 에는 groups를 적용할 수 있는 기능이 없다.  
따라서 groups를 사용하려면 @Validated를 사용해야 한다.  

- Item.class  

Item 객체 클래스에도 해당 코드로 변경해보자.  

```java
//....
 @NotNull(groups = UpdateCheck.class) //수정시에만 적용
private Long id;

@NotBlank(message = "공백! {0}", groups = {SaveCheck.class, UpdateCheck.class})
private String itemName;

@NotNull(groups = {SaveCheck.class, UpdateCheck.class})
@Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})
private Integer price;

@NotNull(groups = {SaveCheck.class, UpdateCheck.class})
@Max(value = 9999, groups = SaveCheck.class) //등록시에만 적용
private Integer quantity;
//...
```

수정에만 쓰는경우는 groups의 값에 UpdateCheck.class  
등록에만 쓰는경우는 groups의 값에 SaveCheck.class
둘다 쓰는경우는 두개 다 배열로 묶어서 작성해주었다.  

하지만 실무에서 이렇게 인터페이스를 나눠서 gruops를 별도로 나누는 기능은 잘 사용하지않는다.  
왜냐하면 특정 객체를 등록할때와 수정할때는 다른 데이터를 받기 때문에 객체를 별도로 분리한다.  


### 전송객체 분리


우선 v3 템플릿 폴더와 컨트롤러를 v4로 복사하자.  
v3/~.html -> v4/~.html 복사  
ValidationItemControllerV3 -> ValidationItemControllerV4 복사  

실무에서 groups를 잘 사용하지 않는 이유로 등록할때의 데이터와 수정할때의 데이터의 차이가 나기 때문이라고 설명했다.  
ex)  
회원등록 시 주민등록번호,id,약관동의 등의 데이터를 전송    
회원수정 시 주민등록번호,id 등의 정보는 전송하지 않음(수정불가)    

- 폼 데이터 전달에 Item 도메인 객체 사용 흐름
> HTML Form -> Item -> Controller -> Item -> Repository  

- 폼 데이터 전달을 위한 별도의 객체 사용 흐름
> HTML Form -> ItemSaveForm || ItemUpdateForm -> Controller -> Item 생성 -> Repository  

즉, 수정과 등록시 받는 객체를 따로 분리해야 하고 객체가 분리되었기 때문에 각각 검증하는 객체가 달라서 groups를 사용할 일이 거의 없다.  

- Item 수정
```java
//객체 분리 등록: ItemSaveForm , 수정: ItemUpdateForm
//검증은 해당 분리된 객체를 통해 이루어짐
//@NotNull(groups = UpdateCheck.class) //수정시에만 적용
private Long id;

//@NotBlank(message = "공백! {0}", groups = {SaveCheck.class, UpdateCheck.class})
private String itemName;

//@NotNull(groups = {SaveCheck.class, UpdateCheck.class})
//@Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})
private Integer price;

//@NotNull(groups = {SaveCheck.class, UpdateCheck.class})
//@Max(value = 9999, groups = SaveCheck.class) //등록시에만 적용
private Integer quantity;
```

기존에 Item 필드의 검증 어노테이션 삭제  

- ItemSaveForm, ItemUpdateForm 추가

경로 : hello.itemservice.web.validation.form

__ItemSaveForm.class__   
```java

@Data
public class ItemSaveForm {

    @NotBlank(message = "공백! {0}")
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull
    @Max(value = 9999)
    private Integer quantity;
}
```

__ItemUpdateForm.class__  
```java
@Data
public class ItemUpdateForm {

    @NotNull
    private Long id;

    @NotBlank(message = "공백! {0}")
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull
    private Integer quantity;
}
```

- ValidationItemControllerV4 수정

```java
@PostMapping("/add")
public String addItem(Model model,@Validated @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    //log.info("bindingResult codes = {}",bindingResult.getFieldError());
    log.info("form = {}" , form);
    if (form.getPrice() != null && form.getQuantity() != null) {
        int resultPrice = form.getPrice() * form.getQuantity();
        if (resultPrice < 10000) {
            bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }
    //검즘 실패시 입력 폼
    if(bindingResult.hasErrors()){
        log.info("errors ={}",bindingResult);
        return "validation/v4/addForm";
    }

    //위 검증을 다 통과하면 성공 로직
    Item item = new Item();
    item.setItemName(form.getItemName());
    item.setPrice(form.getPrice());
    item.setQuantity(form.getQuantity());

    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v4/items/{itemId}";
}


@PostMapping("/{itemId}/edit")
public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form, BindingResult bindingResult) {
    //itemValidator.validate(item,bindingResult);

    //검즘 실패시 입력 폼
    if(bindingResult.hasErrors()){
        log.info("errors ={}",bindingResult);
        return "validation/v4/editForm";
    }

    Item item = new Item();
    item.setItemName(form.getItemName());
    item.setPrice(form.getPrice());
    item.setQuantity(form.getQuantity());

    itemRepository.update(itemId, item);
    return "redirect:/validation/v4/items/{itemId}";
}
```

- 정리

1. ItemSaveForm와 ItemUpdateForm 폼에서 받을 객체를 분리 (검증 분리)

2. @ModelAttribute가 model.add를 자동으로 해줄때 객체이름을 모델의 키값으로 하기때문에 name값을 item으로 설정해주자.  
-> @ModelAttribute("item")  

3. 폼 객체의 데이터를 기반으로 Item 객체를 생성  

> Form 전송 객체 분리해서 등록과 수정에 딱 맞는 기능을 구성하고, 검증도 명확히 분리했다


### Bean Validation - HTTP 메시지 컨버터

@Valid,@Validated를 HttpMessageConverter(@RequestBody)에 적용해보자.  

- ValidationItemApiController
```java
@Slf4j
@RestController
@RequestMapping("/validation/api/items")
    public class ValidationItemApiController {
    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form,
    BindingResult bindingResult) {
        log.info("API 컨트롤러 호출");
        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생 errors={}", bindingResult);
            return bindingResult.getAllErrors();
        }
        log.info("성공 로직 실행");
        return form;
    }
}
```

__Postman을 사용해서 테스트__  

> ❗ API의 경우 3가지 경우를 나누어 생각해야 한다.  
성공 요청: 성공 (위와 같은 결과)  
실패 요청: JSON을 객체로 생성하는 것 자체가 실패함  
검증 오류 요청: JSON을 객체로 생성하는 것은 성공했고, 검증에서 실패함  


- 성공 요청

요청 : POST `http://localhost:8080/validation/api/items/add`  
전송 데이터 : {"itemName":"hello", "price":1000, "quantity": 10}  

![image](https://github.com/9ony/9ony/assets/97019540/55d54672-fada-4e5e-b994-15e1adbf7d3f)


- 실패 요청

요청 : POST `http://localhost:8080/validation/api/items/add`  
전송 데이터 : {"itemName":"hello", "price":"A", "quantity": 10}  

![image](https://github.com/9ony/9ony/assets/97019540/f007a435-b6dd-4e08-8077-ac70406de083)

__출력된 로그__
```text
Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Cannot deserialize value of type `java.lang.Integer` from String "A": not a valid `java.lang.Integer` value; nested exception is com.fasterxml.jackson.databind.exc.InvalidFormatException: Cannot deserialize value of type `java.lang.Integer` from String "A": not a valid `java.lang.Integer` value<EOL> at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: 3, column: 14] (through reference chain: hello.itemservice.web.validation.form.ItemSaveForm["price"])]
```

> ItemSaveForm 객체를 만들지 못하기 때문에 컨트롤러 자체가 호출되지 않고 그 전에 예외가 발생

- 검증 오류 요청

요청 : POST `http://localhost:8080/validation/api/items/add`  
전송 데이터 : {"itemName":"hello", "price":"5000", "quantity": 10000}  

![image](https://github.com/9ony/9ony/assets/97019540/9d2cee7e-45cb-4373-8160-84dd07d642f9)

객체를 만드는데 성공했으나 만들면서 검증오류가 발생함.  
실제로는 해당 검증오류에 대한 응답코드를 그대로 보내는게 아니라 따로 클라이언트쪽에서 필요한 부분만 튜닝하여 전송해줘야함.  

__정리__

HTTP API를 통해 ReqeustBody의 데이터를 검증할때에는 메시지 컨버터의 작동이 성공해서 ItemSaveForm객체를 만들어야 @Valid,@Validated 가 적용된다.

@ModelAttribute는 각각의 필드 단위로 세밀하게 적용된다. 그래서
특정 필드에 타입이 맞지 않는 오류가 발생해도 나머지 필드는 정상 처리할 수 있었다.  

HttpMessageConverter는 @ModelAttribute 와 다르게 각각의 필드 단위로 적용되는 것이 아니라, 전체 객체 단위로 적용된다.  

즉,타입오류시에 객체를 생성하지 못해서 HttpMessageConverter에서 예외가 발생하는데 그 예외를 처리하는 과정이 필요하다.
