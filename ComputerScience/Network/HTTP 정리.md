# HTTP 정리

[김영한님의 모든 개발자를 위한 HTTP 웹 기본 지식](https://www.inflearn.com/course/http-%EC%9B%B9-%EB%84%A4%ED%8A%B8%EC%9B%8C%ED%81%AC)을 듣고 정리한 내용입니다.

## 인터넷 네트워크란?

HTTP를 알아보기 전에 통신이 어떻게 이루어지는지 간단하게 알아보자.

### 멀리 떨어져있는 기기끼리 어떻게 통신할 수 있을까??

부산에 있는 A씨가 서울에 있는 B씨에게 데이터를 보낸다고 가정하자.<br>
그러면 A씨가 사용하는 컴퓨터에 데이터를 입력하여 B씨의 컴퓨터에 전송하기 위해선 B씨의 컴퓨터를 우선 찾아야 된다.
이때 MAC주소와 IP주소를 사용하게 되고 A씨와 B씨는 물리적으로 연결되어 있는데,
이렇게 먼거리를 통신하려면 중간에 신호를 증폭시켜주는 장치와 도착할 수 있는 최단 경로를 알아내기 위한 장치인 라우터(노드)들을 거치게 되는데 이 중간에 있는 여러 개의 라우터(노드)의 집합을 인터넷이라 부른다.

### MAC과 IP주소란?
- MAC
    - MAC은 기기의 고유주소이다. (거의 유일함)<br>
    - LAN 통신(근거리)에서 MAC주소를 사용한다.<br>
    - 48bit 주소체계 (16진수 12개 and 콜론(`:`)으로 구분 )


- IP주소
    - IP주소는 동적인 IP와 고정IP가 있으며, 사설IP와 공인IP가 구분되어 있다.<br>
    - IPv4 : 32bit 주소체계 (8비트씩 4개 and 옥텟(`.`)으로 구분)
    - IPv6 : 128bit 주소체계 (16진수 4bit 4개의 그룹으로 총 8개 콜론(`:`)으로 구분)
    - 숫자로 구성
        ex) 168.192.10.1 (IPv4)
        ex) 3FFF:1002:FFDD:1258:0000:0000:0000:0000 -> 3FFF:1002:FFDD:1258:: (IPv6)
    - 서브넷마스크 ( 네트워크 주소와 호스트 주소를 나눠주는 역할)

### 공인IP와 사설IP 

- `공인 IP`
    - 인터넷상에서 유일한 값을 가진다.
    - 특정 기업에서 발급한다. (유료)
    - 보안에 각별히 신경써야 한다.

- `사설 IP`
    - 네트워크 상에서 유일한 값을 가진다.<br>
    - 하나의 공인IP를 받아 여러 사설IP를 사용한다.<br>
    - 사설IP는 외부에 노출되지 않고 외부에 데이터를 보낼 시에 NAT을 통해 변환되어 통신한다.

나누는 이유는 다양한 이유가 있지만, 우선 수많은 기기가 각자 고유한 IP를 가진다면 IP개수가 턱없이 부족할 것이다.<br>
사설망에서 데이터를 외부로 보낼때 공인IP를 가지고있는 라우터를 거치게 되는데 이때 사설IP가 공인IP로 바뀌게된다.[Network Acess Translation](https://en.wikipedia.org/wiki/Network_address_translation)<br>

### IP (Internet Protocol)
데이터 단위를 패킷(Packet)이라 부름<br>
패킷은 출발지 , 목적지 , 보낼데이터 등을 담아 보낸다.<br>

요청과 응답시 서로 다른노드를 거쳐 올 경우가 있다.<br>
왜냐하면 중간에 라우터가 데이터를 너무많이 받아서 우회할 경우도 있고,<br>
문제가 발생하여 작동을 안할수도 있기 때문이다.<br>

인터넷 프로토콜은 오로지 목적지를 향해 데이터를 최적의 경로로 송신하는 목적을 가진 프로토콜이다.

### IP 프로토콜의 한계
- 비연결성

    패킷을 받을 대상이 없거나 서비스 불능 상태여도 패킷 전송

- 비신뢰성

    중간에 패킷이 사라지거나 패킷이 순서대로 안오는 케이스들에 대해 대처가 불가능

- 프로그램 구분

    같은 IP를 사용하는 서버에서 통신하는 애플리케이션이 둘 이상이라면 이 애플리케이션들을 구분할 수 없음

> 위의 한계점들을 극복하기 위해 TCP/UDP, PORT가 개발됨.

## TCP와 UDP

IP 프로토콜은 신뢰할수 없는 프로토콜이다.<br>
위에 IP프로토콜의 한계에서 적어놧듯이 IP프로토콜은 상대가 받을수있는 상황이 아니라도 데이터를 전송하고,
또 패킷이 중간에 사라지거나 손실되도 대처가 안되며 순서대로 도착할거라는 보장이 없다.<br>
이를 위해 TCP 프로토콜이 존재한다.

### TCP (RFC 793)

- 신뢰성 있는 통신 프로토콜
- 두 프로세스간에 안정적으로 통신이 가능한지 사전에 가상의 연결을 시도 (3way-HandShake)
- Port Number를 통해 대상 식별함.
- 순서 보장 및 흐름제어, 오류제어, 흐름제어 역할을 함

### UDP

- 간단한 기능만을 제공하는데 IP와 유사하다.
- 기본적으로 TCP와 같이 Port번호를 통해 대상을 식별
- 신뢰성 , 순서보장 , X
- 속도가 빠르다.
- 애플리케이션 즉, 상위계층에서 추가작업 해야함.

## PORT 와 Socket

### PORT

TCP와 UDP를 설명하면서 계속 Port를 언급하였는데 Port에 대해 간단하게 설명하겠습니다.
Port는 대상을 식별하기 위한 0~65535 범위의 번호를 가짐.<br>
Port를 통해 대상을 식별함 = 패킷을 구분지어 어느 프로세스로 갈지 정하기 위한 번호<br>
즉, Port는 해당 프로세스로 가기위한 주소라고 볼수 있다.

### SOCKET

TCP/IP 프로토콜 표준에서 Socket은 IP와PORT를 합친것 이다.<br>
고유한 host주소와 프로세스로 가기 위한 주소 PORT를 합치므로 유니크하게 식별할 수 있다.<br>
즉 , 네트워크 상에있는 프로세스 간 통신의 종착점(End Point)이다.

## DNS란?
우리는 네이버나 구글을 들어갈 때 우리가 쓰는 문자를 주소창에 입력하여 들어가게 되는데,<br>
이는 사실 주소를 입력하게 되면 ip로 바뀌어서 접속하게 된다.<br>

> www.naver.com 입력 -> 223.130.195.95:443 반환 -> 네이버 접속

위의 과정을 거치게 되는데, 이 때 영문자로 주소를 입력하면 ip주소로 반환해 주는 것을
`DNS (Domain Name Service)`라고 한다.

- 호스트 주소를 IP주소로 변환해준다.
- 트리 구조로 구성 및 내부 서브도메인을 가질수도 있음.
- 도메인 네임 스페이스 , 네임 서버, 리졸버로 구성되어 있음

## URI?? URL??

### URI란?

`리소스를 식별하기 위한 정보`를 담은 주소
URL 과 URN 등으로 나뉜다.

    Uniform : 리소스 식별하는 통일된 방식
    Resource : 자원, URI로 식별할 수 있는 모든 것(제한 없음)
    Identifier : 다른 항목과 구분하는데 필요한 정보

- URL(Uniform Resource Location)
    - 리소스가 있는 위치(경로)를 지정
    - 프로토콜을 포함
    - 웹 뿐만이 아니라 컴퓨터 네트워크상의 자원을 모두 나타낼 수 있음
        ex ) file:///C:/HTTP/URLTESTFOLDER/ (컴퓨터 네트워크 사으이 자원을 나타냄)
             https://www.google.com/search?q=http&hl=ko (웹 자원)
    - 문법 방식 

        ![image](https://github.com/9ony/9ony/assets/97019540/6a60b924-bd5b-4b48-b1eb-8a82a4f2acd4)

- URN(Uniform Resource name)
    - 리소스에 이름을 부여
    - 프로토콜을 포함하지 않는다.
    - 이름만으로 실제 리소스를 찾을 수 있음
        ex) urn:isbn:8960777331 (어떤 책의 isbn URN)

## HTTP(Hyper Text Transfer Protocol)란?

### HTTP 역사

- HTTP/0.9 1991년: GET 메서드만 지원, HTTP 헤더X
- HTTP/1.0 1996년: 메서드, 헤더 추가
- HTTP/1.1 1997년: 가장 많이 사용 (대부분의 기능이 들어있음)
- RFC2068 (1997) -> RFC2616 (1999) -> RFC7230~7235 (2014)
- HTTP/2 2015년: 성능 개선
- HTTP/3 진행중: TCP 대신에 UDP 사용(QUIC), 성능 개선

### HTTP 특징

- 클라이언트/서버 구조
- 무상태 (stateless) 지향
- 비연결성
- HTTP 메세지로 통신
- 단순하고 확장에 용이함

### 클라이언트 서버 구조란?

Request(응답)과 Response(요청) 구조<br>

1. 클라이언트가 요청을 보내면 서버에 응답을 기다리게 됨
2. 서버가 클라이언트가 요청한 데이터를 전송(html,css 등)
3. 클라이언트가 서버의 응답 데이터를 받음

### 상태유지와 무상태 ( Stateful,Stateless )

- 상태유지의 특징
    - 서버측에서 클라이언트의 상태를 기억
    - 클라이언트 측에서 보내는 데이터가 적음
    - 수평확장(Scale-out)이 어려움
        > 새롭게 확장된 서버는 이 정보를 알지 못한다. 즉, 상태정보 공유하는 식으로 확장해야함
    - 빠른 처리 속도
    - 서버측 오류로 강제다운 시 데이터 재전송

- 무상태의 특징
    - 클라이언트 상태를 기억하지 않음
    - 클라이언트 측 전송할 데이터가 많아짐
    - 서버 부하가 적음 (세션유지 필요 X)
    - 수평확장에 용이 (Scale-out)

### Stateless의 한계점

로그인,장바구니,채팅 등 사용자 정보를 저장해야 하는 서비스에서는 Stateless로는 구현하기 힘들거나 비효율적인 문제가 많다.
예를 들어 로그인 후 이용가능한 서비스를 Stateless로 구현했을때 문제점을 보자

1. 사용자정보를 계속 포함해서 요청해야함
2. 이로인한 보안문제 (사용자정보가 노출)
3. 요청시 리소스 증가 (로그인정보 + 다른 기능에 대한 추가 정보 계속해서 중첩)

이런 최소한의 정보들은 Stateful하게 구현해야 하는데,<br>
이때 `Cookie` , `Session` , `Token(jwt)`들을 이용하여 상태유지를 한다.

> 웹 개발 중 Cookie , Session , Token등을 사용하면서 http가 왜 무상태 프로토콜인지 헷갈려 할 수 있는데 (나만 그랬나?)<br>
HTTP Header에 cookie 정보를 담아서 서버의 session 정보와 비교하여 상태를 유지하는 것이다.
Cookie Session등 상태유지를 위한 기술들은 WAS에서 지원하는 기능인 점을 알아둬야 한다.

요약 : HTTP는 기본적으로 무상태 프로토콜이고 상태유지를 위해 Cookie,Session,Token 등을 사용한다.

### HTTP 커넥션 관리

__비연결성(Connectionless)__

HTTP는 기본적으로 비연결성 프로토콜임
비연결성은 클라이언트가 서버에 요청을하면 서버가 해당 요청에 대한 응답을 한후 연결을 종료하는 것이다.

- 연결을 유지를 위한 리소스 확보 (서버 자원 절약)
- 요청과 응답 사이에는 독립성이 유지
- 모든 요청 시 재연결하므로 오버헤드가 발생 (TCP의 3way-handshake)


- 비연결성 문제점
    
    웹페이지는 여러개의 파일로 이루어져 있다.<br>
    ex) html,css,js,이미지 파일등..<br>
    해당 데이터를 전부 비연결적인 특징을 이용하여 클라이언트가 받는다고 가정하면<br>
    html,css,js 파일들을 받을때 마다 각각 연결에 필요한 오버헤드가 추가적으로 발생한다.<br>
    이를 해결하기 위해 지속연결(Persistent Connection)을 사용한다. 

__병렬 연결(Parallel Connection)__

병렬 커넥션은 만약 4번의 요청을 처리한다고 가정하면 첫번째 요청에 대한 응답이 올때까지 두번째 요청이 기다리지 않고
커넥션은 여러개 맺어서 한번에 4개 요청을 보내는 것이다.<br>
즉 , 여러 TCP 커넥션을 통한 HTTP 요청이다.

![image](https://github.com/9ony/9ony/assets/97019540/80d0da29-8abf-4748-a683-6bf6bfdacd90)

- 단일 커넥션의 대역폭 제한과 커넥션이 동작하지 않고 있는 시간을 활용
- 남은 대역폭을 사용한 더 빠른 속도 향상
- 제한된 대역폭 내에서는 각 트랜잭션 처리가 느리기 때문에 항상 빠르지는 않음
- 우선순위 처리 어려움
- 여러 개의 TCP 커넥션이 동시에 열리므로, 네트워크 리소스 및 서버 리소스를 더 많이 사용
- 연결의 설정과 해제는 네트워크 오버헤드와 시간 지연을 초래

__지속연결 (Persistent Connection)__

![image](https://github.com/9ony/9ony/assets/97019540/14030aa5-219c-43ea-abf9-abc193cef968)

위 그림과 같이 많은 데이터를 받을 때 비연결성은 데이터의 개수마다 새로 연결을 맺어야 하는 치명적인 단점이 있는데<br>
HTTP/1.0+ 부터는 오른쪽 그림과 같이 지속연결을 통해 문제를 해결하였다.

- 요청과 응답은 같은 TCP 연결을 통해 처리
- 같은 서버에 있는 파일(html,css,image 등) 및 여러 웹 페이지들을 한번의 TCP 연결을 전송
- HTTP 서버는 일정 기간(타임아웃) 사용되지 않으면 연결을 종료
- HTTP1.1 부터 Keep Alive가 기본값으로 설정되어 있다.

     __Keep Alive__<br>
    HTTP/1.0+에 추가된 기능 기존에 연결된 TCP 연결을 재사용 하는것

    __Keep-Alive 지원 시 응답헤더__<br>
    ![image](https://github.com/9ony/9ony/assets/97019540/d2ebcf67-603d-4fde-903a-481718e3a30c)

    __Keep-Alive 미지원 시 응답헤더__<br>
    ![image](https://github.com/9ony/9ony/assets/97019540/fb9ca316-6d63-4381-9937-ee1c54751a5f)


    그림과 같이 서버에서 keep-alive를 지원한다면 응답헤더 Connection옵션에 keep-alive 정보가 담겨서 온다.<br>
    Keep-Alive 옵션으로 max와 timeout이 있다.<br>
    max는 현재 커넥션에 최대 요청개수로 요청마다 max값이 1씩 줄어든다. 0이되면 커넥션을 끊는다.<br>
    timeout(초단위)은 커넥션 유지 시간이다. 해당시간동안 요청이없다면 커넥션을 끊는다.<br>
    

    __Persistent Connection__<br>
    HTTP/1.1 부터 도입된 개념으로 Keep Alive를 기본적으로 사용하고 여러 개의 요청과 응답을 동일한 연결을 통해 처리

    __Pipelining__<br>
    파이프 라이닝은 여러개의 요청을 보낼때 처음 요청이 응답될 때까지 기다리지 않고 바로 요청을 한꺼번에 보내는 것을 의미한다.<br>
    즉, 여러개의 요청을 한꺼번에 보내서 응답을 받음으로서 대기시간을 줄이는 기술이다.<br>
    
    ✔ 이때 병렬 연결과 헷갈릴수 있는데 병렬연결은 여러 TCP 연결을 통해 동시에 처리하는 것이고 파이프 라이닝은 하나의 TCP 연결의 재사용을 활용한 keep-alive를 전제로 한다.<br>
    
    - 요청을 한꺼번에 보냄
    - `요청이 들어온 순서대로(FIFO) 응답`을 반환
    - `응답 순서를 지키기 위해` 응답 처리를 미루기 때문에 Head Of Line Blocking(HOLB) 문제가 발생
    - 현재 대부분의 브라우저들은 파이프라이닝을 사용하지 못하도록 막아 놓음

HTTP/2.0에서는 위의 HTTP/1.1 버전의 Head Of Line Blocking,헤더압축 등의 문제점들은 개선하였다.

[ HTTP/2.0 관련자료 ](https://inpa.tistory.com/entry/WEB-%F0%9F%8C%90-HTTP-20-%ED%86%B5%EC%8B%A0-%EA%B8%B0%EC%88%A0-%EC%9D%B4%EC%A0%9C%EB%8A%94-%ED%99%95%EC%8B%A4%ED%9E%88-%EC%9D%B4%ED%95%B4%ED%95%98%EC%9E%90#http_1.1_%ED%86%B5%EC%8B%A0_%EA%B3%BC%EC%A0%95)


### HTTP 구조와 메세지

__HTTP 메세지의 구조__<br>
![image](https://github.com/9ony/9ony/assets/97019540/d58f7da1-fd72-4f3b-960a-bc5e5b64de9b)

- 시작 라인
    - 요청
    1. HTTP 메서드 
        - 종류 : GET , POST , PUT , DELETE 등..
        - 서버가 수행 해야할 동작을 지정함.
            GET : 조회 , POST : 요청 데이터 처리  , PUT : 데이터 덮어쓰기 or 없으면 생성 등..
    2. 요청 대상 
        - 경로 (절대 경로 , * , 상대경로)
        - \+ ?쿼리
    3. HTTP 버전
        - HTTP/1.1 , HTTP/2 등..
    
    - 응답
    1. HTTP 버전
        - (요청의 HTTP 버전과 동일)
    2. 상태코드
        - 200,400,500번대 코드가 있음
    3. 이유문구
        - 사람이 이해할 수 있는 짧은 설명
        ex) OK , Created , Not Found
- 헤더
    1. 필드 이름 (대소문자 구분 X)
        - Host,Content-Type 등.. (표준헤더가 엄청 많아서 아래 링크에 잘 정리 되있습니다)
        - HTTP 전송에 필요한 모든 부가정보의 이름
    2. 필드 값 (대소문자 구분 O)
        - 해당 필드 부가정보의 값 
    
    ✔ 헤더는 커스텀 헤더가 작성 가능함 (대신 클라이언트도 해당 헤더를 알아야 됨)
    [ 표준 헤더 종류 ](https://developer.mozilla.org/ko/docs/Web/HTTP/Headers)
- 메세지 바디<br>
    실제 전송할 데이터 <br>
    HTML , JSON , IMAGE , 영상등 Byte로 표현할 수 있는 모든 데이터<br>

[RFC7230 참고](https://datatracker.ietf.org/doc/html/rfc7230#section-3)

## HTTP 메서드에 대해 알아보자.

### HTTP 메서드란?

HTTP 메서드란 클라이언트와 서버 사이에 이루어지는 요청(Request)과 응답(Response) 데이터를 전송하는 방식 <br>
즉, 서버가 수행해야 할 동작(조회,수정,삭제 등)을 지정하는 요청을 보내는 방법을 메서드라 한다.

__주요 메소드 5가지__<br>
    GET : 리소스 조회<br>
    POST : 요청 데이터 처리, 주로 데이터 등록에 사용<br>
    PUT : 리소스를 대체, 해당 리소스가 없으면 생성<br>
    PATCH : 리소스를 일부만 변경<br>
    DELETE : 리소스 삭제<br>
__기타 메소드 4가지__<br>
    HEAD: GET과 동일하지만 메시지 부분을 제외하고, 상태 줄과 헤더만 반환<br>
    OPTIONS: 대상 리소스에 대한 통신 가능 옵션을 설명(주로 CORS에서 사용)<br>
    CONNECT: 대상 자원으로 식별되는 서버에 대한 터널을 설정<br>
    TRACE: 대상 리소스에 대한 경로를 따라 메시지 루프백 테스트를 수행<br>

### GET 메서드

- 리소스 조회시 사용
- 전달하고 싶은 데이터는 query(쿼리 파라미터, 쿼리 스트링)를 통해서 전달
- 메세지 바디를 사용할 수 있지만 지원하지 않는 곳이 많아서 거의 사용안함

__GET 예제__

유저 이름으로 회원을 조회했다는 가정을 하면 아래 그림과 같이 요청과 응답데이터가 오는 예제이다.<br>
![image](https://github.com/9ony/9ony/assets/97019540/38b49dc2-7507-4633-b64e-16bf7398c4ff)

__GET 특징__
- 캐시될 수 있음
- 요청 기록이 브라우저에 남음
- 북마크 가능
- 요청길이 제한
- url에 노출됨 (보안에 용이하지 못함)
- 멱등성을 가지고 안전한 메서드이다.

### POST 메서드

- 요청 데이터 처리
- 메시지 바디를 통해 서버로 요청 데이터 전달
- 서버는 요청 데이터를 처리
- 메시지 바디를 통해 들어온 데이터를 처리하는 모든 기능을 수행
- 주로 전달된 데이터로 신규 리소스 등록, 프로세스 처리에 사용
- JSON으로 조회 데이터를 넘겨야 하는 애매한 경우 POST를 사용

__POST예제__

새로운 멤버를 추가하는 예제이다.<br>
![image](https://github.com/9ony/9ony/assets/97019540/7fe50f62-012f-4346-be1b-e899fea31579)

Location : 300번대 응답이나 201 Created 응답(없을 수도 있음)일 때 붙는 필드<br>
    201 : 정상 처리 후 생성된 리소스(자원) 경로를 표시<br>
    3xx : 추가 작업을 위한 페이지 경로(리다이렉트 경로)<br> 

__POST 특징__
- 캐시가 가능하나 거의 사용하지 않음.
- 브라우저 기록에 남지 않음.
- 데이터 길이에 대한 제한이 없음 (전송하는 데이터가 body 담겨서 노출되지 않음)
- 멱등성을 갖지 않음

### PUT 메서드
- 리소스를 있을 시 대체하고 없으면 생성
- 클라이언트가 리소스의 명확한 위치를 알고 URI 지정(POST와 차이점)

__PUT 예제__<br>
1. 리소스가 기존에 있거나 없는 경우 예제<br>
![image](https://github.com/9ony/9ony/assets/97019540/4a5a2b0f-6a92-4aa3-ad7e-07b6e27c365c)

2. 멤버의 정보 일부분인 나이만 변경할 때 주의점 예제<br>
![image](https://github.com/9ony/9ony/assets/97019540/8ab6b4da-570e-47d9-8eeb-6615b90a74c6)

    ✔ 일부분만 변경할 때는 PATCH를 이용하자!

__PUT 특징__
- 캐시되지 않음.
- 브러우저에 기록이 안남음.
- 멱등성을 가진다

### PATCH 메서드
- 리소스를 일부만 수정할 경우 사용된다.
- PUT과 다르게 리소스가 없다면 생성되지 않음.

__PATCH예제__

멤버의 나이 정보인 age필드만 보냈을 때 예제<br>
![image](https://github.com/9ony/9ony/assets/97019540/e23cf55f-c724-4240-8f67-8cbe5eee2807)

    ✔ PUT과 다르게 age필드 부분만 수정된 것을 볼 수 있다.

- 캐시가 됨
- 멱등성을 가지지 않음
    ex ) { "oper": "add", "age": 1"} 이런식으로 나이를 1 추가한다는 요청이 가능하기 때문


### DELETE 메서드

- 리소스 삭제 시 사용
- 요청시 Body, Content-Type이 비어있다.

__DELETE 예제__<br>
id가 5인 멤버를 삭제 예제이다.<br>
![image](https://github.com/9ony/9ony/assets/97019540/7a33484a-dee1-43f0-9a06-858934c25e12)

- 멱등성을 가진다.
- 캐싱되지 않는다.

### HTTP 메서드 속성

__속성 요약표__<br>
![image](https://github.com/9ony/9ony/assets/97019540/0fc84fd3-c623-4199-a07d-d170e9604f44)

[HTTP 메서드 요약 출처](https://ko.wikipedia.org/wiki/HTTP)

- 안전 (Safe)
    - 호출 시 리소스를 변경하지 않는다
    - 포함되는 메서드 : GET

- 멱등성 (Indempotent)
    - 호출 후 계속 호출해도 결과가 변경되지 않음<br>
        (✔ 멱등은 중간에 외부 요인으로 인해 리소스가 변경된 것은 고려하진 않음)<br>
    - 포함되는 메서드 : GET PUT DELETE

    [멱등이란?](https://june0122.github.io/2021/08/05/term-idempotent/)<br>
    연산을 여러 번 적용하더라도 결과가 달라지지 않는 성질, 연산을 여러 번 반복하여도 한 번만 수행된 것과 같은 성질을 의미

- 캐시가능 (Cacheable)
    - 캐시해도 되는 메서드
    - 포함되는 메서드 : GET HEAD POST PATCH

    [웹 캐시란?](https://ko.wikipedia.org/wiki/%EC%9B%B9_%EC%BA%90%EC%8B%9C)<br>
    웹 캐시(또는 HTTP 캐시)는 서버 지연을 줄이기 위해 웹 페이지, 이미지, 기타 유형의 웹 멀티미디어 등의 웹 문서들을 임시 저장하기 위한 기술이다.<br>
    웹 캐시 시스템은 이를 통과하는 문서들의 사본을 저장 후 특정 조건을 충족하는 요청일 경우 캐시화가 가능하다.<br>
    동일한 서버에 다시 접근할 때에는 근처에 있는 프록시 서버의 웹 캐시에 저장된 정보를 불러오므로 더 빠른 열람이 가능하다.<br>

## HTTP 상태코드 종류
### 1XX
 : 요청을 받았으며 프로세스를 계속한다 (처리중)<br>
HTTP/1.0이래로 어떤 1XX 상태 코드들도 정의 되지 않았다.<br>
현재 거의 사용하지 않는 상태코드라고 한다.<br>

### 2XX
 : 요청이 정상 처리됬음을 의미<br>
- 200 (OK) : 요청이 정상적으로 처리

- 201 (Created) : 새로운 리소스 생성 요청이 처리됨
    - Location 필드에 새롭게 생선된 리소스 경로값이 입력된다.

- 202 (Accepted) : 서버가 요청을 접수했지만 아직 처리하지 않았다.
    - Batch 처리 시스템에서 주로 쓰이는 상태코드라고 한다.

- 204 (No Content) : 서버가 요청을 정상적으로 처리했지만, 응답할 페이로드가 없을 때
    - 예를 들어 저장기능같은 경우 수행 후 결과로 아무 내용이 없어도 되고 같은화면을 유지한다.(임시저장같은 것??)

### 3XX
 : 요청을 완료하려면 추가 행동이 필요<br>
3XX번대에 Location헤더가 있으면 Location 값의 경로로 이동한다. 이를 `Redirect`라고 한다.
- 300 (Multiple Choices) : 서버가 사용자 에이전트에 따라 수행할 작업을 선택하거나, 요청자가 선택할 수 있는 작업 목록을 제공

- 301 (Moved Permanently) : 리소스의 URI가 영구적으로 리다이렉트된다. (리다이렉트시 요청 메서드가 GET으로 변하고, 본문이 제거될 수 있다)

- 302 (Found) : 리소스가 일시적으로 변경됨 리다이렉트시 요청 메서드가 GET으로 변할수 있고 본문이 제거 될 수 있다.

- 303 ( See Other ) 302와 같으나 메서드가 GET으로 변경된다.

- 304 ( Not Modified ) : 캐시를 목적으로 사용.<br>
 리소스가 수정되지 않았음을 클라이언트에게 알리고, 로컬 캐시로 리다이렉트하며 로컬캐시를 쓰기 때문에 본문을 포함하면 안된다.<br> 조건부 GET , HEAD 요청 시 사용

- 307 (Temporary Redirect) : 302와 기능은 같음 리다이렉트시 요청 메서드와 본문 유지해야 한다.

- 308 (Permanent Redirect) : 301과 기능은 같지만 리다이렉트시 요청 메서드와 본문이 유지된다.

### 4XX
 : 클라이언트 오류 시 발생되는 코드 (잘못된 문법 등으로 서버가 요청을 처리못함)
- 400 ( Bad Request ) : 클라이언트가 잘못된 요청을 해서 서버가 요청을 처리할 수 없음<br>
    요청 파라미터가 오류 or API 스펙이 맞지 않을 때 발생

- 401 ( Unauthorized ) : 클라이언트가 해당 리소스에 대한 인증or인가 필요<br>
    응답에 WWW-Authenticate 헤더와 함께 인증 방법을 설명<br>
    인증(Authentication): 본인 확인(로그인)<br>
    인가(Authorization): 권한부여 (ADMIN 권한처럼 특정 리소스에 접근할 수 있는 권한

### 5XX
 : 서버 오류 시 발생되는 코드 


### 🔍 리다이렉션 추가!! 3XX번대
- 영구 리다이렉션 ( 301, 308 )
    - 리소스의 URI가 영구적으로 이동
    - 원래의 URL를 사용하지 않을 때, 검색 엔진 등에서도 변경 인지를 원할때 ✔
    ✔ `검색 엔진 등에서도 변경 인지`한다는 것은?<br>
    : 검색엔진은 네이버나 구글 등 검색엔진이 이전 사이트가 새로운 사이트 주소 변경된걸 인지하고 변경된 주소로 리다이렉션 해줄수 있는걸 의미하는것 같다. [관련 글 링크](https://moz.com/learn/seo/redirection)

- 임시 리다이렉션 (302,303,307)
    - 사용자를 임시로 다른 페이지로 보내려고 할 때
    - 사이트에서 제공하는 서비스가 일시적으로 사용할 수 없게 된 경우 (검색 엔진 등에서 URL을 변경하면 안됨)
    
- PRG 패턴

    __PRG 그림 예시__<br>
    ![image](https://github.com/9ony/9ony/assets/97019540/4085cad1-973e-4b2b-9a39-bf05903470e4)  

    PRG 패턴을 사용하는 이유는 만약 결제기능을 수행하는 화면이라는 가정하에  
    1. 클라이언트가 결제 요청  
    ex) POST /order { id:10 , item:book , count : 1 }<br>
    2. 서버가 200 OK 응답  
    3. 클라이언트는 /order 페이지인 상태  
    4. 클라이언트에서 새로고침을 누른다면 다시 결제되는 상황이 발생  

    이때 서버가 200 상태코드가 아닌 302 or 303상태코드로 응답하여 리다이렉션 해준다면?  

    1. 클라이언트가 결제 요청  
    ex) POST /order { id:10 , item:book , count : 1 }<br>
    2. 서버가 302 Found 응답 Location : /order-success  
    3. 클라이언트는 /order-success 페이지로 강제 리다이렉트된 상태  
    4. 클라이언트에서 새로고침을 누른다면 /order-success 페이지로 새로고침 되므로 위 상황 예방  

    즉, PRG 이후 리다이렉트 URL이 이미 POST -> GET으로 리다이렉트 됨 <br>
    새로 고침 해도 GET으로 결과 화면만 조회됨  

[PRG 참고링크](https://en.wikipedia.org/wiki/Post/Redirect/Get)

### ✔ 만약 새로운 상태 코드가 추가된다면?<br>
클라이언트는 디테일한 코드를 이해를 못하면 큰 범위로 처리한다. (299면 2XX로 처리함)

[HTTP 상태코드 출처](https://ko.wikipedia.org/wiki/HTTP_%EC%83%81%ED%83%9C_%EC%BD%94%EB%93%9C)

## HTTP 헤더

HTTP 헤더는 클라이언트와 서버가 요청 또는 응답으로 부가적인 정보를 전송할 수 있도록 해준다.
부가적 정보는 요청자,컨텐트 타입,캐싱 등 여러가지가 존재함.
[HTTP 헤더 관련 링크](https://developer.mozilla.org/ko/docs/Web/HTTP/Headers)

### HTTP 헤더 분류 

__이전 버전 RFC2616__<br>

- General 헤더: 메시지 전체에 적용되는 정보, 예) Connection: close
- Request 헤더: 요청 정보, 예) User-Agent: Mozilla/5.0 (Macintosh; ..)
- Response 헤더: 응답 정보, 예) Server: Apache
- Entity 헤더: 엔티티 바디 정보, 예) Content-Type: text/html, Content-Length: 3423

- HTTP message body
    - 메시지 본문(message body)은 엔티티 본문(entity body)을 전달하는데 사용
    - 엔티티 본문은 요청이나 응답에서 전달할 실제 데이터
    - 엔티티 헤더는 엔티티 본문의 데이터를 해석할 수 있는 정보 제공
    - 데이터 유형(html, json), 데이터 길이, 압축 정보 등등

### RFC723x 변화

엔티티(Entity)에서 표현(Representation)으로 변경
표현은 표현 메타데이터(표현과 관련된 헤더) + 표현 데이터(페이로드=메세지 본문)

![image](https://github.com/9ony/9ony/assets/97019540/22a12495-0d0c-4711-9464-d022dcaf0a99)

- 메시지 본문(message body)을 통해 표현 데이터 전달
- 메시지 본문 = 페이로드(payload)
- 표현은 요청이나 응답에서 전달할 실제 데이터
- 표현 헤더는 표현 데이터를 해석할 수 있는 정보 제공
- 데이터 유형(html, json), 데이터 길이, 압축 정보 등등

✔ 엔티티 -> 표현으로 바뀐걸 볼 수 있다.

### HTTP 헤더 종류

- 표현 헤더<br>
    클라이언트와 서버간에 송/수신할 때 리소스의 표현 요청<br>
    - Content-Type: 표현 데이터의 형식 (미디어 타입, 문자 인코딩)<br>
        ex) text/html; charset=utf-8 , application/json , image/png
    - Content-Encoding: 표현 데이터의 압축 방식 (표현 데이터를 압축하기 위해 사용) <br>
        ex) gzip , deflate , identity
    - Content-Language: 표현 데이터의 자연 언어 (표현 데이터의 자연 언어(한글,영어 등..) 표현)<br>
        ex) ko , en , en-US
    - Content-Length: 표현 데이터의 길이 (바이트 단위 & Transfer-Encoding사용 시 사용하면 안됨)<br>
        ex) Byte 단위 Content-Length: 1021(본문길이)

    ✔ 표현 헤더는 전송,응답 시 둘다 사용할 수 있다!

- 협상 헤더 <br>
    클라이언트가 선호하는 표현 요청<br>
    ✔ 협상 헤더는 요청시에 사용하고 서버가 이를 확인하고
    - Accept: 클라이언트가 선호하는 미디어 타입 전달<br>
        ex) text/html,application/xml;q=0.9,image/avif,image/webp,image/apng
    - Accept-Charset: 클라이언트가 선호하는 문자 인코딩<br>
        ex) utf-8, iso-8859-1;q=0.5, *;q=0.1
    - Accept-Encoding: 클라이언트가 선호하는 압축 인코딩<br>
        ex) gzip, deflate, br
    - Accept-Language: 클라이언트가 선호하는 자연 언어<br>
        ex) ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7

    - __협상 우선순위__<br>
    __인자 가중치 (q: Quality value)__<br>
    헤더가 제시하는 속성이 여러 개일 경우, 인자 가중치 q를 함께 줄 수 있고 q는 0~1 사이의 값<br>
    q 값이 높을수록 해당 속성의 우선순위도 높아진다.<br>
    ex) `헤더 이름: 헤더값1;q=0.6, 헤더값2;q=0.3, 헤더값3;q0.9` 이라면 헤더 값3이 1순위다<br>
    해당 헤더의 속성으로 어떤 값이 와도 상관없을 경우 와일드카드 `*` 가능<br> 
    __구체적인 값__<br>
    속성이 여러개일 경우 구체적인 값들이 협상에서 우선된다는 특징<br>
    ex) `Accept: text/plain,text/plain;format=flowed,text/*,*/*` 이라면 `text/plain;format=flowed가 1순위`

    

- 전송 방식 헤더 <br>
    전송 방식에 대한 정보를 요청<br>
    - Content-Length , Content-Encoding은 표현 헤더에서 다룸 
    - Transfer-Encoding : 콘텐츠를 분할해서 전송<br>
        `Content-Length를 포함 X` (길이를 예측할 수 없기때문)<br>
        ex) `Transfer-Encoding: chunked`<br>
        chunk size(16진수) + CRLF(0a0d) + 내용 + CRLF 이런 형태로 데이터 응답<br>
        > e1 (+CRLF)<br>
          내용 (+CRLF)  (225 byte)<br>
          b7 (+CRLF)<br>
          내용 (+CRLF)  (183 byte)<br>
          0 (+CRLF)   (끝을 의미)<br> 
    - Range, Content-Range : 콘텐츠 범위를 전송 해줌<br> 
        ex) Range : 200-1000 클라이언트가 요청 헤더에 포함 후 요청 <br>
        Content-Range : 200-1000/6000 (현재 데이터의 범위/전체 크기) 서버 헤더에 포함되어 응답<br>

    > 분할전송(chunk)과 범위전송(Content-Range)은 같은 메커니즘의 전송방식(Transfer-Encoding)이며
    분할된 내용의 범위를 구체적 인가 아닌가 정도의 차이

- 일반 정보 헤더 <br>
    일반 정보를 나타내는 요청<br>
    - From: 유저 에이전트의 이메일 정보  
        검색 엔진 같은 곳에서 주로 사용 (일반적으로 잘 사용하지 않음)
    - Referer: 이전 웹 페이지 주소  
        현재 요청된 페이지의 이전 웹 페이지 주소  
        Referer를 사용해서 유입 경로 분석 가능  
        원래 referrer가 맞는데 r이 빠진 오타가 표준이 되어버림  
    - User-Agent: 유저 에이전트 애플리케이션 정보
        클라이언트의 애플리케이션 정보(웹 브라우저 정보, 등등)  
        어떤 종류의 브라우저에서 장애가 발생하는지 파악 가능하다  
    - Server: 요청을 처리하는 오리진 서버의 소프트웨어 정보
        Server: Apache/2.2.22(Debian)  
        오리진 서버이므로 엔드포인트 서버 정보임, 중간에 프록시 서버 등등은 X  
    - Date: 메시지가 생성된 날짜
        Date: Fri, 09 Apr 2021 14:41:31 GMT  
    
    Form,Referer,User-Agent는 요청시 사용 , Server,Date는 응답시 사용  

- 특별한 정보 헤더  
    - Host : 요청한 호스트 정보(도메인) 필수헤더이다  
        하나의 서버가 여러 도메인을 처리할 때 사용  
        하나의 IP주소에 여러 도메인이 적용되어 있을 때 사용  
    - Location : 페이지 리다이렉션 주소, 리소스 주소 정보  
        201(Created): 생성된 리소스 URI  
        3xx(Redirection): 리다이렉션을 위한 리소스 URI  
    - Allow : 서버에서 허용 가능한 HTTP 메서드 정보  
        405 Method Not Allowed 에러를 응답과 같이 Allow : GET,POST 등 지원가능한 메서드 포함  
    - Retry-After : 유저 에이전트가 다음 요청을 하기까지 기다려야 하는 시간 정보  
        503 (Service Unavailable): 서비스가 언제까지 불능인지 알려줄 수 있음  
        ex) Retry-After: Fri, 31 Dec 1999 23:59:59 GMT (날짜 표기)  
            Retry-After: 120 (초단위 표기)  

- 인증 헤더  
    - Authorization : 클라이언트 인증 정보를 서버에 전달  
        ex) Authorization: BASIC xxxxxxxxxxxxxxxxxx  
        다양한 인증방식별(OAuth, OAuth2, SNS로그인 등)로 들어가야하는 값이 다름  
        인증 메커니즘과는 상관없이 헤더를 제공하는 것으로 인증과 관련된 값을 줘야함.  
    - WWW-Authentication : 리소스 접근시 필요한 인증 방법 정의  
        401 Unauthorized 응답과 함께 사용  
        ex) WW-Authentication: Newauth realm="apps", type=1,title="Login to \"apps\"", Basic realm="simple"  
        -> 어떻게 인증을 해야할지를 정의해 알려줌.  

- 쿠키 헤더  
    로그인 정보나 최소한의 정보를 저장하기 위한 용도  
    ex) set-cookie: sessionId=abcde1234; expires=Sat, 26-Dec-2020 00:00:00 GMT; path=/; domain= google.com; Secure  
    
    쿠키 정보는 `항상 서버에 전송`된다  
    네트워크 `트래픽 추가 유발`되며 `최소한의 정보만 사용`(세션 id, 인증 토큰)하는게 좋다    
    
    서버에 전송하지 않고, 웹 브라우저 내부에 데이터를 저장하고 싶다면?  
    [웹 스토리지(localStorage, sessionStorage) 참고](http://www.tcpschool.com/html/html5_api_webStorage)해보자  

    ❗ _보안에 민감한 데이터는 저장 X (주민번호, 신용카드 번호 등등)_

    - 사용처  
        사용자 로그인 세션 관리  
        광고 정보 트래킹

    - 생명주기 (Expires, max-age)  
        Set-Cookie: expires=Sat, 26-Dec-2020 04:39:21 GMT  
            만료일이 되면 쿠키 삭제  
        Set-Cookie: max-age=3600 (3600초)  
            0이나 음수를 지정하면 쿠키 삭제  
        세션 쿠키(session cookie): 만료 날짜를 생략하면 브라우저 종료시 까지만 유지  
        영속 쿠키(persistent cookie): 만료 날짜를 입력하면 해당 날짜까지 유지  

    - 도메인 (Domain)  
        ex) domain=example.org  

        __명시 시 명시한 문서 기준 도메인 + 서브 도메인 포함__  
        domain=example.org를 지정해서 쿠키 생성  
        example.org와 추가적으로 dev.example.org도 쿠키 접근가능  

        __생략 시 현재 문서 기준 도메인만 적용__  
        example.org 에서 쿠키를 생성하고 domain 지정을 생략시  
        example.org에서만 쿠키 접근  
        dev.example.org는 쿠키 접근안함  

    - 경로 (Path)  
    설정한 경로를 포함한 하위 경로 페이지만 쿠키 접근  
    일반적으로 path=/ 루트로 지정  
    ex) path=/home 지정시  
    -> 가능 : /home , /home/level1 , /home/level2/...  
    -> 불가능 : /other  
    
    - 보안 (Secure, HttpOnly, SameSite)  
        __Secure__  
        쿠키는 http, https를 구분하지 않고 전송  
        Secure를 적용하면 https인 경우에만 전송  
        __HttpOnly__  
        XSS 공격 방지  
        자바스크립트에서 접근 불가 (document.cookie)  
        HTTP 전송에만 사용  
        __SameSite__  
        XSRF 공격 방지  
        요청 도메인과 쿠키에 설정된 도메인이 같은 경우만 쿠키 전송  
