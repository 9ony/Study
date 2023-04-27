# OSI 7계층
OSI 7계층은 네트워크에서 통신이 일어나는 과정을 7단계로 나눈 것을 의미함

네트워크 간의 연결에 어려움이 많아 호환성의 결여를 막기 위한 것으로, ISO(국제 표준화 기구)에서는 OSI 참조모델을 제시

계층으로 나눈이유는 서로 상하구조를 가지기 때문이다. 즉, 1계층 동작하지않으면 2계층은 동작할수가없다.(Encapsulation and Decapsulation)

### OSI 7계층 그림

![image](https://user-images.githubusercontent.com/97019540/233357327-06d4fd21-8c82-492c-9a08-bbf1cb1dae14.png)



## 1계층 (Physical Layer)

> 물리적으로 연결된 두 대의 컴퓨터가 데이터를 주고받을 수 있게 해주는 계층

컴퓨터는 0과 1의 나열로 데이터를 정의한다. 그러면 통신하기 위해서 0과 1만 주고받을수 있으면 됩니다.


### 1계층 예제 그림
![image](https://user-images.githubusercontent.com/97019540/233559941-bff70cc4-0d2a-4733-bace-ca91488b2bfc.png)

위 그림처럼 캡슐화된 데이터가 1계층에 도달하면 해당 데이터를 아날로그신호로 인코딩하여 받는 쪽에서는 해당 아날로그신호를 컴퓨터가 알수 있게 해석(디코딩)하는 역할을 합니다. 두 컴퓨터간에 연결은 UTP,STP,광케이블 등등의 유선으로 연결되어 있을 수도있고 무선으로 연결되어 있을수도 있다.


__여러 컴퓨터를 연결하기 위해선?__

- 모든 전선으로 연결 ( 1 + ... + n-1 => n(n-1)/2)

    ![image](https://user-images.githubusercontent.com/97019540/233571535-633cdc20-de84-4527-a222-4dadf7df9936.png)

    <br>

    - 전선의 수가 너무 많아짐

- 허브를 통한 연결 (더미허브)

    ![image](https://user-images.githubusercontent.com/97019540/233574971-94e1ed3a-22b6-44f3-b414-5b400a466edd.png)

    - 전선의 수를 줄이는 데는 성공
    - 하지만 첫번째 컴퓨터에서 같은 네트워크의 네번째 컴퓨터로 데이터를 보내야 되면 해당 데이터가 모든 컴퓨터에 데이터를 주게된다. (원치 않은 브로드캐스팅 문제 , 목적지 구분이 안됨)
    - 보안 이슈
    - 첫번째 컴퓨터가 전송하고 있을때 다른 컴퓨터가 전송하게되면 이미 첫번째 컴퓨터가 전송경로 점유하고 있기때문에 충돌 위험 (반이중 방식)


### 물리계층 장비
- 리피터

    ![image](https://user-images.githubusercontent.com/97019540/233921966-5f1f9169-775c-49a1-bac1-9cb677849b8d.png)

    - 먼 거리를 통신할때 신호를 증폭시켜주는 장비
    - 리피터는 최대 4개로 제한되어 있다.
    - 망내에 잡음도 같이 증폭된다.

- 허브 (더미허브)

    ![image](https://user-images.githubusercontent.com/97019540/233574971-94e1ed3a-22b6-44f3-b414-5b400a466edd.png)

    - 리피터의 기능을 가지고 있음
    - 멀티포트를 지원
    - 네트워크에 컴퓨터가 3대 이상일경우 한 컴퓨터에서 신호를 보내면 같은 네트워크에 모든 컴퓨터들도 다 받게된다.
    - 네트워크내에 장치가 많을수록 부하가 심해진다.

- 랜카드

    ![image](https://user-images.githubusercontent.com/97019540/233925465-afb34a04-a083-4048-87e3-4055708cc871.png)

    - 비트열 (0과 1)을 전기 신호로 변환
    - MAC주소를 가지고 있다.

### 물리계층 역할 정리

- 데이터를 아날로그 신호로 바꾸어 전선으로 흘려 보내줌 (Encoding)

- 아날로그 신호가 들어오면 데이터로 해석함 (Decoding)

- 전송매체는 유선으로는 우리가 잘아는 케이블중 랜선(UTP케이블),광케이블 등이 있고, 광케이블 등등 무선으로는 전자기파가 있다.

- 미디어 타입, 커넥터 타입, 신호표현 방법, 즉 시그널링, 속도 등을 정의


## 2계층 (DataLink Layer)

> 같은 네트워크상에 서로 다른 두대의 컴퓨터가 연결할 수 있게 해주는 계층

1계층에서 허브의 멀티포트를 통해 여러개의 장치가 연결되었는데 한 컴퓨터에서 신호를 보내면 모든 컴퓨터에 신호가 전송되었다.
그러면 수신하는 컴퓨터에 목적지에 데이터를 전송하기 위해서 어떻게 해야될까?

2계층에서는 LAN카드(NIC)에 MAC주소를 이용하여 통신을 한다.

1계층에서 설명했던대로 각각 통신장비에 LAN카드에는 MAC주소를 가지고있다. (MAC주소는 변경이 가능하다.)



### 2계층 예제 그림

![image](https://user-images.githubusercontent.com/97019540/234534358-154ce856-a22f-4734-8482-2e95418af40e.png)


위의 그림대로 2계층에서는 같은 네트워크에 서로 다른 두대의 컴퓨터가 통신을 할수 있게 Mac주소를 이용하여 통신한다.

포트1번에 연결된 컴퓨터가 포트4번에 연견된 컴퓨터에 0011이라는 데이터가 담긴 프레임을 보낸다.

하지만 스위치는 처음에 프레임을 보낼때 출발지 MAC주소가 AA:BB:CC:DD:EE:F1이니까 포트1번에 AA:BB:CC:DD:EE:F1인것만 알고`(Learning)` 목적지주소인 ~:::::F4이 어떤 포트인지는 모른다고 가정할 때
`Flooding`이 동작하는데 이는 Mac 주소 테이블에 포트번호와 맥주소가 없을때 브로드캐스트되서 스위치에 연결된 모든 컴퓨터에 프레임을 보내고 도착지 맥주소가 자기 맥주소와 비교하여 해당 프레임을 무시한다. 즉, 컴퓨터2와 컴퓨터3은 프레임을 받게되지만 무시하게 된다.

위 그림처럼 포트4번이 스위치 MAC 주소 테이블에 저장되어 있을때에는 `Forwarding` 하게되고 도착지 맥주소가 테이블에 저장되어 있기 때문에 포트4번으로만 프레임을 전송(`Filtering`)하게 된다.

또 Mac 주소 테이블에는 `Aging` 기능도 있는데 이는 맥주소를 `Learning` 했을때 저장해놓는 시간을 정합니다. (보통 300초를 저장한다함)
즉, 스위치는 Mac주소를 영구적으로 저장하지는 않는다.


### Frmae이란?

__EthernetII 그림__

![image](https://user-images.githubusercontent.com/97019540/234534538-f1f5d2d2-6705-4633-9b0c-7fdec2b568f0.png)

위 그림처럼 2계층의 데이터단위를 Frame이라고 한다.
2계층 프로토콜로는 IEEE 802.3 , DIX 2.0 , PPP, HDLC 등등이 있는데 LAN 통신 표준은 IEEE 802.3이지만 DIX 2.0(Ethernet II)이 주로 사용되고 있다. WAN 통신 시에는 PPP가 주로 사용된다고 한다.

근거리 네트워크 통신을 LAN이라 하고 원거리 네트워크 통신을 WAN이라고 한다. 나중에 라우터와 라우터사이에 먼 거리를 통신할때 2계층에서 WAN 통신 프로토콜이 사용된다고 볼수있다.

__Ethernet Frame 구조__
- Mac Frame
    - Header
        - Mac dst : 목적지 Mac 주소 
        - Mac src : 출발지 Mac 주소
            > Mac dst와 src는 3byte씩 구분되어있음 (총 6Byte)
            > 앞의 3Byte는 OUI(제조사번호) 뒤 3Byte는 (해당 업체 랜카드 식별변호)<br>
            > OUI 24bit중에 제일 오른쪽 1bit는 Multicast와 Unicast를 표시하기위한 비트인데 `0일시 unicast 1일시 multicast를 의미`한다 (Least Significant Bit 라고함)
            > 목적지 주소 6Byte(=48bit)가 전부 1일경우 Broadcast됨

        - EtherType / Length : 상위계층의 프로토콜 타입을 표시 / LLC(Logical Link Controller) Frame 길이
            - 0000~05dc(10진수로 0~1500)이면 IEEE 802.3 프레임 포맷의 DATA영역의 길이이다
            - 0600부터(DIX2.0 프레임 포맷)는 상위 프로토콜 타입을 의미한다. (`EtherType 참고링크 : https://en.wikipedia.org/wiki/EtherType#cite_note-ethtypes-7`)

    - Data
        - 프레임의 최소크기는 64byte이고 Header의 크기는 14 (목적지주소 6,출발지 6 , type length 2)
            오류제어 필드 4Byte (FCS 4)
        - 그러면 Data영역의 크기는 64-18 = 48Byte가 되고 또 data가 48Byte보다 작을시에 Pad가 추가된다
        - Pad는 0으로 채워진값 (padding) 48Byte보다작을 시 맞추기위해 사용 

    - Trailer (CRC (Cyclic Redundancy Check = FCS))
        - Preamble과 SFD는 제외한다 (Mac Frame이 아니기때문에)
        - DA + SA + Length + DATA 영역을 계산
        - MAC Controller는 Frame을 송신하면서 동시에 CRC를 계산한 후 DATA뒤에 추가
        - 수신쪽 MAC Controller도 수신하면서 동시에 CRC 계산한 후 수신된 CRC가 일치하는지 검사하고 틀리다면 폐기

__Preamble과 SFD??__
 1. Preamble
- 송신측 과 수신측간의 송/수신 속도를 일치시키기 위한 `비트 동기 (Bit Synchronization)`
- 10101010이 7회 연속 반복되는 56bit로 구성 
    > 실제 Ethernet Frame이 시작 되기전에 Preamble을 전송해 clock동기를 맞춥니다.
 2. SFD (Start Frame Delimiter)
- Frame Bit열에서 Byte단위를 식별하는 Byte동기
- 정상적인 프레임의 내용이 시작된다는 사실을 알려주는 Frame동기
- 10101011의 8Bit로 구성
- Preamble 과 SFD는 모두 MAC Controller Chip에서 생성 된다.

> 🎇 EthernetII (DIX 2.0)은 SFD는 없고 Preamble이 64bit(8Byte)로 되어있다!<br>
> ✨ Preamble과 SFD는 Physical Layer(물리계층) Header 이다.


### 데이터링크 장비

- `브릿지` :  콜리전 도메인을 나누어주는 역할
    - 소프트웨어 기반으로 동작함
    - MAC 주소 기반으로 작동
    - 필터링을 통해 Collision Domain (충돌 영역)을 줄여준다.
    - 브릿지도 전송거리를 연장시켜주는데 전기적 신호만을 증폭시키는게 아니라 Frame을 재생성하여 전송해준다.

- `L2 스위치` : 브리지의 기능 + 전이중 통신방식, 즉, 충돌이 일어나지 않는 구조의 통신장비
    - 전이중 통신방식이란 두 대의 단말기가 데이터를 송수신하기 위해 각각 독립된 회선을 사용하는 통신 방식 (포트당 하나의 Collision Domain)
    - 하드웨어 기반으로 동작
    - Mac Address Table에 목적지 주소만 보고 Frame전송
    - 각각의 포트마다 통신속도 설정 가능

### L2 스위치 주요기능
- Mac Address Table : Port번호와 Mac Address를 매핑한 테이블 
- Learnig : 위 그림예제에서 설명했듯이 스위치는 해당 포트에서 프레임이 넘어오면 해당 포트에 연결된 Mac주소를 학습한다
- Flooding : 전송된 프레임에 도착지 Mac주소가 Mac Address Table에 없을 경우 전송받은 포트를 제외한 나머지 포트에 프레임을 전송하는것
- Forwarding : 스위치에 전달받은 도착지 Mac주소가 Mac Address Table에 있을 경우 해당 Mac주소와 일치하는 포트로 프레임을 전달하는것
- Filtering : Forwarding(전송될 포트를 제외한 나머지 포트) 과 Flooding(스위치에 프레임을 보낸 포트) 시 다른포트에 프레임이 가는것을 차단시켜주는것
- Aging : Learnig시에 매핑된 Prot번호와 Mac Address를 영구적으로 저장하지않고 제한시간을 둠 (기본 300초)

> ARP는 3계층에서 다루겠습니다!!

### 데이터링크 계층 역할 정리
<br>

- 프레이밍: 데이터 링크 계층에선 `네트워크 계층에서 받아온 데이터그램`을 `프레임 단위`로 만들고 `헤더와 트레일러를 추가`
- 흐름제어: 송수신자 간 데이터를 처리하는 속도 차이를 해결하기 위한 제어도 담당
- 오류제어: 프레임 전송 시 발생한 오류를 복원하거나 재전송
- 접근제어: 매체 상 통신 장치가 여러 개 존재할 때, 데이터 전송 여부 결정

> 데이터링크 계층 기술도 랜카드에 구현되어 있다.
> 랜카드에서 송신할 데이터를 받아 이더넷헤더와 Trailer를 붙여 MAC Frame을 만들고 동기화 비트 Preamble헤더를 추가하여 전기신호로 만들어 전송한다.

