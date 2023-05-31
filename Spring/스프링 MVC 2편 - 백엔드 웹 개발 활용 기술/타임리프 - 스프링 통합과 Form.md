## 타임리프 - 스프링 통합과 폼

타임리프는 스프링 없이도 동작하지만, 스프링을 위한 편의기능이 많이 존재한다.  
마치 스프링을 위해 나온것 같이..  
그래서 많은 스프링 개발자가 템플릿뷰로 타임리프를 많이 사용한다고 한다.  

__스프링 통합으로 추가되는 기능__
- 스프링의 SpringEL 문법
- 스프링 빈 호출 지원
- 편리한 폼 관리를 위한 추가 속성
    ex) th:object(기능 강화, 폼 커맨드 객체 선택)    
    th:field, th:error, th:errorclass 등..  

- 폼 컴포넌트 기능
    checkbox, radio btn, List 등을 편리하게 사용  
- 스프링의 국제화 기능, 메세지의 편리한 통합
- 스프링 Vailidation, Error 처리 통합
- 스프링 타입 변환에 관련된 컨버전 서비스 지원

스프링 부트와 타임리프 연동 시 설정을 변경할 때  
[스프링 타임리프 설정 가이드](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.templating)

> 이후 진행될 예제는 이전 간단한 프로젝트인 상품관리 웹페이지(item-service)에서 확장시킬것 입니다.  

### 입력 폼 처리

타임리프가 제공하는 입력 폼 기능을 이용하여 폼 관련 설정이 어떻게 편리하게 처리되는지 알아보자  

- __기존 addForm.html(form 부분)__  
```html
<form action="item.html" th:action method="post">
        <div>
            <label for="itemName">상품명</label>
            <input type="text" id="itemName" name="itemName" class="form-control" placeholder="이름을 입력하세요">
        </div>
        <div>
            <label for="price">가격</label>
            <input type="text" id="price" name="price" class="form-control" placeholder="가격을 입력하세요">
        </div>
        <div>
            <label for="quantity">수량</label>
            <input type="text" id="quantity" name="quantity" class="form-control" placeholder="수량을 입력하세요">
        </div>
        ...(생략)
```
- __변경 후__  
```html
<form action="item.html" th:action th:object="${item}" method="post">
        <div>
            <label for="itemName">상품명</label>
            <input type="text" id="itemName" th:field="*{itemName}" class="form-control" placeholder="이름을 입력하세요">
        </div>
        <div>
            <label for="price">가격</label>
            <input type="text" id="price" th:field="*{price}" class="form-control" placeholder="가격을 입력하세요">
        </div>
        <div>
            <label for="quantity">수량</label>
            <input type="text" id="quantity" th:field="*{quantity}" class="form-control" placeholder="수량을 입력하세요">
        </div>
```

`<form action="item.html" th:action method="post">`  
에서 th:object="\${item}"을 추가   
`<form action="item.html" th:action th:object="${item}" method="post">`  

input 태그에 th:field를 추가하자.  
`*` 선택 변수식 사용 (object에 선택된 `${item}` 객체에 접근)  
(object에서 \${item}을 지정했기 때문에 `*`(선택 변수식) 사용가능)  

__input 태그에 추가할 코드__  
-> th:field="\*{itemName}" , th:field="\*{price}" , th:field="\*{quantity}"  

- 렌더링 전 : `<input type="text" th:field="*{itemName}" />`  
- 렌더링 후 : `<input type="text" id="itemName" name="itemName" th:value="*{itemName}" />`  
    - id : th:field 에서 지정한 변수 이름  `id="itemName"`  
    - name : th:field 에서 지정한 변수 이름  `name="itemName" ` 
    - value : th:field 에서 지정한 변수의 값을 사용  `value=""`  

__컨트롤러 상품등록폼 변경 코드__  
```java
@GetMapping("/add")
public String addForm(Model model) {
    model.addAttribute("item", new Item());

    return "form/addForm";
}
```
위 코드가 추가된 이유는  
th:object에 item을 추가하려면 객체의 정보가 필요하기 때문에 빈 Item객체를 addForm에 넘겨주자.  

__빈 객체를 넘겨줌으로써 부가적인 효과는?__

원래는 폼의 name속성의 값을 item객체 프로퍼티에 맞춰서 적고 넘겼었다.  
이때 name속성의 값을 잘못적었을 시 서버에 데이터는 넘어오지만, 따로 공백 또는 NULL에 대한 처리를 안했다면 name속성이 잘못된 필드의 값은 의도치 않게 될 것이다.  
즉, name값이 해당 객체에 프로퍼티가 아니라서 바인딩이 안됨  
하지만 미리 객체를 넘겨받은것을 활용해 추가한다면 렌더링 시 오류페이지가 출력되어 개발자입장에서 좀 더 빨리 캐치할 수 있어서 편해진다.  

또 나중에 검증(Validation)부분에서도 이점을 가질수 있다.

빈 객체를 생성하는 비용은 실제로 매우 적고 해당 비용보다 많은 이점을 얻을 수 있다!!  

__정리__  
th:field 덕분에 렌더링 시 id , name , value 속성을 모두 자동으로 처리해줘서 편리하다.  

### 요구사항 추가  

타임리프를 사용해서 폼에서 체크박스, 라디오 버튼, 셀렉트 박스를 편리하게 사용해보자  

__추가되는 서비스__  

- 판매 여부 : 판매 오픈 여부 체크
- 등록 지역 : 지역별로 체크박스 생성
- 상품 종류 : 도서, 식품 등 체크박스 생성
- 배송 방식 : 빠른 배송, 일반 배송, 느린 배송 (하나만 선택가능)

추가된 요구사항에 해당되는 코드를 추가해보자.  

__ItemType.enum__ : 상품 종류  
```java
public enum ItemType {
    BOOK("도서"), FOOD("식품"), ETC("기타");
    private final String description;
    ItemType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
```

__DelivaryCode.class__ : 배송 방식  
```java
@Data
@AllArgsConstructor
public class DeliveryCode {
    private String code;
    private String displayName;
}

```

__Item.class__ 일부분 추가  
```java
private Boolean open; //판매 여부
private List<String> regions; //등록 지역
private ItemType itemType; //상품 종류
private String deliveryCode; //배송 방식
```

### 스프링MVC 체크박스

__addForm.html__ 추가  
```html
<div>판매 여부</div>
<div>
    <div class="form-check">
        <input type="checkbox" id="open" name="open" class="form-check-input">
        <label for="open" class="form-check-label">판매 오픈</label>
    </div>
</div>
```

__FormItemController__ 추가  
```java
log.info("item.open={}", item.getOpen());
```
값이 잘 넘어오는지 확인해보자!!  

![image](https://github.com/9ony/9ony/assets/97019540/d5e6c6c4-d0f1-4185-adb2-9cfe14ff622f)

전송을 누르면 서버 콘솔에 `item.open=true`이 잘 찍힌다.  
하지만 문제가 있다. 만약 체크를 하지 않는다면 open이라는 필드 자체가 넘어가지 않는다.  
그러므로 item 객체의 open 멤버변수는 null을 가지게 되는데, 이를 해결하기 위해 스프링은 히든 필드를 하나 만들어서,_open 처럼 기존 체크 박스 이름 앞에 언더스코어( `_` )를 붙여서 전송하면 체크를 해제했다고 인식할 수 있다.  

__addForm.html 히든필드 추가__
```html
...
<input type="checkbox" id="open" name="open" class="form-check-input">
<!--체크 해제를 인식하기 위한 히든 필드-->
<input type="hidden" name="_open" value="on"/>
...
```

체크 박스를 체크하면 스프링이 open에 값이 있다면 사용 (_open은 무시)  
체크 박스 미체크 시 _open만 있는 것을 확인 후 false 처리  

### 타임리프 체크박스

타임리프로 체크박스를 처리 하는 방법을 알아보자.  
타임리프로는 아까 했던 히든필드를 자동생성 해주는데 바로 코드로 보자.  

__addForm.html__  
```html
<input type="checkbox" id="open" th:field="*{open}" class="form-checkinput">
<!--<input type="checkbox" id="open" name="open" class="form-check-input">-->
<!--체크 해제를 인식하기 위한 히든 필드-->
<!--<input type="hidden" name="_open" value="on"/>-->
```
삭제할 부분은 주석으로 표시해두었다.  
해당 코드로 변경후 렌더링된 것을 보면 히든필드도 생성된 것을 볼 수 있다.  


> 이제 상세 페이지와 수정 폼에도 판매여부 체크박스를 생성하자.  

__editForm.html__  
```html
<hr class="my-4">

<div>판매 여부</div>
<div>
    <div class="form-check">
        <input type="checkbox" id="open" th:field="*{open}" class="form-checkinput">
        <label for="open" class="form-check-label">판매 오픈</label>
    </div>
</div>
```

상품 수정은 ItemRepository에 update()가 작동하는데  
새로운 요구사항에 대한 수정작업을 안해줬었기 때문에 update메서드를 수정하자.  

__ItemRepostory.class__  
```java
public void update(Long itemId, Item updateParam) {
    Item findItem = findById(itemId);
    findItem.setItemName(updateParam.getItemName());
    findItem.setPrice(updateParam.getPrice());
    findItem.setQuantity(updateParam.getQuantity());
    findItem.setOpen(updateParam.getOpen());
    findItem.setRegions(updateParam.getRegions());
    findItem.setItemType(updateParam.getItemType());
    findItem.setDeliveryCode(updateParam.getDeliveryCode());
}
```

__item.html__  
```html
<hr class="my-4">

<div>판매 여부</div>
<div>
    <div class="form-check">
        <input type="checkbox" id="open" th:field="${item.open}" class="formcheck-input" disabled>
        <label for="open" class="form-check-label">판매 오픈</label>
    </div>
</div>
```
item.html은 th:object를 안쓰기 때문에 `${item.open}`으로 받아야 된다.  
그리고 여부만 확인해야되기 때문에 `disabled`를 추가하자  
또 타임리프가 item.open값이 true false인지에 따라 checked속성을 있게하거나 없게하는걸 해준다.  
(원래는 개발자가 제어해야함.)  

### 멀티 체크박스

이제 체크박스를 여러개 사용해서 하나 이상을 체크해야 되도록 해보자.  

아까 요구사항에 등록지역을 추가 했었다.  
아이템을 등록할 때 등록지역을 선택 할려면 우선 폼에 등록지역정보가 있어야된다.  

__FormItemController.class__ 추가  
```java
@ModelAttribute("regions")
public Map<String, String> regions() {
    Map<String, String> regions = new LinkedHashMap<>();
    regions.put("SEOUL", "서울");
    regions.put("BUSAN", "부산");
    regions.put("JEJU", "제주");
    return regions;
}
```
해당 등록지역을 상세페이지 수정페이지 등록페이지에 다 보여주어야 하는데,
그러면 메서드마다 regions을 생성하여 Model에 넘겨줘야 한다.  
이렇게 각각 사용하는 메서드마다 추가해줘서 넘겨도 되지만, ModelAttribute의 기능을 이용해서 위 코드처럼 컨트롤러에 추가하면 해당 컨트롤러 호출 시 @ModelAttribute의 name이 key가 되고 return 값이 value가 되어 Model에 저장된다.  
즉, 해당 코드가 있는 컨트롤러 호출시 마다 model.addAttribute("regions",regions)가 되어 view템플릿 렌더링에 사용할 수 있는 것이다.  

하지만 동적으로 생성되지 않고 계속 쓰인다면 똑같은 resgions가 반복되서 호출되기 때문에 정적으로 할당하여 불러쓰는 것 등 최적화가 필요하다.  

__addForm.html__ 추가  
```html
<!-- multi checkbox -->
<div>
    <div>등록 지역</div>
    <div th:each="region : ${regions}" class="form-check form-check-inline">
        <input type="checkbox" th:field="*{regions}" th:value="${region.key}" class="form-check-input">
        <label th:for="${#ids.prev('regions')}" th:text="${region.value}" class="form-check-label">서울</label>
    </div>
</div>
```

th:each를 통해 체크박스를 전달받은 regions객체 사이즈의 개수만큼 생성하였고 이때 html은 id가 겹치면 안되는데 타임리프는 each 루프 안에서 반복해서 만들 때 임의로 1,2,3... 숫자를 뒤에 붙여준다.    
`th:for="${#ids.prev('regions')}"`은 input 태그의 th:field를 통해 생성된 id를 보고 인식하여 맞춰 라벨의 for값을 생성해준다.  

__결과__  :
```text
item.open=false
item.regions=[SEOUL, BUSAN]
```

![image](https://github.com/9ony/9ony/assets/97019540/b390d659-8d2a-4605-b4b6-66b54d059f55)

로그도 정상적으로 찍히고 item.html에서 체크유무도 타임리프가 알아서 처리해준다.  

### 라디오 버튼

상품종류 선택 버튼을 라디오버튼으로 만들어보자.  

__FormItemController__ 추가  
```java
@ModelAttribute("itemTypes")
public ItemType[] itemTypes() {
    return ItemType.values();
}
```

__addForm.html__ 추가  
```html
<div>
    <div>상품 종류</div>
        <div th:each="type : ${itemTypes}" class="form-check form-check-inline">
        <input type="radio" th:field="*{itemType}" th:value="${type.name()}" class="form-check-input">
        <label th:for="${#ids.prev('itemType')}" th:text="${type.description}" class="form-check-label"> BOOK</label>
    </div>
</div>
```

상세페이지와 수정페이지 모두 전에 했던 예제처럼 변경하면 되고,
라디오 버튼은 이미 선택이 되어 있다면, 수정시에도 항상 하나를 선택하도록 되어 있으므로 체크
박스와 달리 히든필드를 사용할 필요가 없다!!  

하지만 처음에 등록 시에 선택을 안하고 등록한다면 따로 체크를 해주거나 기본값을 넘겨주는 등 작업이 필요하다.  

### 셀렉트 박스

셀렉트 박스를 자바 객체를 활용해서 개발해보자.  

__컨트롤러__ 에 추가!!  
```java
@ModelAttribute("deliveryCodes")
public List<DeliveryCode> deliveryCodes() {
    List<DeliveryCode> deliveryCodes = new ArrayList<>();
    deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
    deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
    deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
    return deliveryCodes;
}
```

__addForm.html__ 추가  
```html
<div>
    <div>배송 방식</div>
    <select th:field="*{deliveryCode}" class="form-select">
        <option value="">==배송 방식 선택==</option>
        <option th:each="deliveryCode : ${deliveryCodes}" th:value="${deliveryCode.code}"
                th:text="${deliveryCode.displayName}">FAST</option>
    </select>
</div>
```

상세페이지와 수정페이지 모두 전에 했던 예제처럼 변경

라디오버튼과 마찬가지로 히든필드를 생성하지 않기 때문에 개발자가 selected처리를 따로 해줘야 한다.  

