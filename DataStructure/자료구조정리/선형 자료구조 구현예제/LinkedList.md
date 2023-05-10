# LinkedList 구현 예제와 설명

연결리스트는 데이터필드와 다음 주소를 가르키는 주소필드를 갖는다.  

__단일 연결리스트 그림 구조__  

![image](https://github.com/9ony/9ony/assets/97019540/891d718e-c83b-456f-8980-60d2b6078ae2)

노드의 링크가 다음 노드를 참조한다.  

__원형 연결리스트 그림 구조__   
![image](https://github.com/9ony/9ony/assets/97019540/957feb46-7b75-417b-8ee3-5a044adac442)  

마지막으로 추가된 노드의 링크가 head를 가르키는 구조로 순환된다.  

__이중 연결리스트 그림 구조__   

![image](https://github.com/9ony/9ony/assets/97019540/a973f0a7-5c88-4c93-87a9-60815908d734)

각 노드가 이전 노드도 가르키게 된다.  
즉, 이전 노드가 없으면 head (시작)  
다음 노드가 없으면 tail (끝)  

## Node 객체

### Node.Class 코드

```java
public class Node<T> {
    private T data;
    private Node<T> next;

    public Node(T data){
        this.data = data;
        this.next = null;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString(){
        return (String)this.data;
    }
}
```

노드는 제네릭 타입의 데이터와 다음 노드를 가르킬 next 변수를 가진다.  
각각의 필드는 setter와 getter를 가지고 있다.  


## 단일 연결리스트

단일 연결리스트는 각 노드에서 다음 노드에 대한 주소를 가지는 next를 가지고 있지만  이전 노드로 이동할 수는 없는 리스트이다.  
그리고 첫번째 노드를 가르키는 head로 구성되어 있다.  
마지막 노드를 가르키는 `tail`필드를 가질 수도 있지만 없으면 어떤 단점이 있는지 보기 위해 해당 필드는 아래 원형 연결리스트에서 사용해보도록 하겠다.  

- __Singly LinkedList ADT__
    __필드__  
    \- Node<T> head : 현재 제일 앞쪽 노드를 가르키는 포인터  
    \- int size : 현재 요소의 개수   

    __기능__  
    \- add(T t) : 데이터 삽입 O(n)  
    \- add(T t ,int index) : 데이터 삭제 O(n)    
    \- remove() : 요소를 삭제 O(n)  
    \- remove(int index) : 요소를 삭제 O(n)  
  

### 단일 연결 리스트 예제 코드

```java
package linearStructure.linkedList.linkedListEx;

import linearStructure.linkedList.Node;

public class SinglyLinkedList<T> {

    private Node<T> head;
    private int size;

    public SinglyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    // 리스트 마지막에 요소 추가
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
        }
        size++;
    }

    // 리스트 중간에 요소 추가
    public void add(T data,int index) {
        Node<T> newNode = new Node<>(data);
        Node<T> current = head;
        Node<T> nextNode;
        if(index==0){
            newNode.setNext(current);
            head=newNode;
            size++;
            return;
        }
        if(index == size){
            add(data);
            return;
        }
        //인덱스 위치의 노드 이전까지 이동
        for(int i = 0 ; i<index-1;i++){
            current = current.getNext();
        }
        //추가된 노드의 next에 해당되는 노드
        nextNode = current.getNext();

        current.setNext(newNode);
        newNode.setNext(nextNode);
        size++;
    }
    // 리스트 요소 삭제
    public void remove(){
        if(size==0) {
            throw new RuntimeException("삭제할 요소가 없습니다.");
        }
        if (size==1){
            head = null;
            size--;
            return;
        }
        Node<T> current = head;
        //마지막 노드 이전 노드까지 가도록 getNext() 반복
        for(int i = 0 ; i<size-2; i++){
            current = current.getNext();
        }
        //이전 노드에서 다음노드 null설정
        current.setNext(null);
        // 마지막 노드를 아무도 참조하지 않아서 gc처리
        size--;
    }

    // 리스트 요소 삭제
    public void remove(int index){
        Node<T> current = head;
        if(index==0){
            head = current.getNext();
            size--;
            return;
        }
        if(index == (size-1)){
            remove();
            return;
        }
        //index의 전까지 next()로 이동
        for(int i = 0 ; i<index-1; i++){
            current = current.getNext();
        }
        //이전 노드의 next에 index위치 노드의 다음노드를 할당
        current.setNext(current.getNext().getNext());
        //index위치의 노드는 gc처리된다.
        size--;
    }
// toString 등 생략..
}
```

### 설명

단일연결리스트의 필드 변수를 보자.  
첫 번째 노드를 가르킬 head, 그리고 연결리스트의 총 노드의 개수를 담을 size 변수가 있다.  

- add(T data) , add(T data, int index)
    추가하는 기능을 살펴보자.  
    우선 add(T data)는 next()를 통해서 다음 노드가 없을때 까지 순회하는 작업을 한다.  
    이후 다음 노드가 없는 노드가 할당되었으면 해당 노드의 next에 newNode를 할당한다.  
    연결리스트 마지막에 노드를 추가하는 메서드이다.  
    추가하는 작업의 시간복잡도는 O(n)이 된다.  

    인덱스를 설정해서 노드를 추가하는 메서드를 보자.  
    추가할 곳 인덱스 이전 노드까지 next()를 통해 이동한다.  
    그 후에 이전 노드에 setNext()를 통해 새로운 노드를 추가하고, 새로운 노드가 다음 노드를 가르키도록 하면 된다.  

    ![image](https://github.com/9ony/9ony/assets/97019540/6215631f-53d6-452f-8450-81b5fd337f7d)

    위와 같은 구조로 추가되는 것이다.  

- remove() , remove(int index)
    삭제는 추가보다 비교적 간단하다.  
    우선 삭제할 노드 이전까지 next()를 통해 이동한다.  
    그후 해당 노드에서 setNext()를 통해 다음 노드를 null로 설정해주면 끝이다.  

    인덱스를 설정해서 노드를 삭제하는 메서드도 추가했던 것과 같이 삭제할 이전노드 까지 이동한다.  
    그 후 도착한 노드에서 next를 다음노드가 아닌 한칸 더 이동한 노드로 설정해주면 된다.  
    그러면 다음노드는 더이상 참조되지 않기 때문에 GC에 의해 처리되는 것이다.  

## 원형 연결 리스트

원형 연결리스트는 마지막 노드가 다시 head를 가르키는 구조이다.  
즉, 마지막 노드가 null이 아닌 첫번째 노드를 가르킨다.  

- __Circular LinkedList ADT__
    __필드__  
    \- Node<T> head : 현재 제일 앞쪽 노드를 가르키는 포인터   
    \- int size : 현재 요소의 개수  
    \- Node<T> head : 현재 제일 뒤쪽 노드를 가르키는 포인터   

    __기능__  
    \- add(T t) : 데이터 삽입 O(1)  
    \- add(T t ,int index) : 데이터 삭제 O(n)    
    \- remove() : 요소를 삭제 O(n)   
    \- remove(int index) : 요소를 삭제 O(n)  

### 원형 연결 리스트 예제 코드

```java
package linearStructure.linkedList.linkedListEx;

import linearStructure.linkedList.Node;

public class CircularLinkedList<T> {

    private Node<T> head;
    private int size;
    //tail이라는 포인터를 만들어 줌으로써 요소 추가를 빠르게 가져갈 수 있다.
    private Node<T> tail;

    public CircularLinkedList() {
        this.head = null;
        this.size = 0;
        this.tail = null;
    }

    // 리스트 마지막에 요소 추가
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
            head.setNext(head);
        } else {
            Node<T> current = tail;
            current.setNext(newNode);
            newNode.setNext(head);
            tail = newNode;
        }
        size++;
    }

    // 리스트 중간에 요소 추가
    public void add(T data,int index) {
        Node<T> newNode = new Node<>(data);
        Node<T> current = head;
        Node<T> nextNode;
        if(index==0){
            head = newNode;
            head.setNext(current);
            tail.setNext(head);
            size++;
            return;
        }if(index==size){
            add(data);
            return;
        }
        for(int i = 0 ; i<index-1;i++){
            current = current.getNext();
        }
        nextNode = current.getNext();
        current.setNext(newNode);
        newNode.setNext(nextNode);
        size++;
    }
    // 리스트 요소 삭제
    public void remove(){
        if(size==0) {
            throw new RuntimeException("삭제할 요소가 없습니다.");
        }
        if (size==1){
            head = null;
            size--;
            return;
        }
        Node<T> current = head;
        //마지막 노드 이전 노드까지 가도록 getNext() 반복
        for(int i = 0 ; i<size-2; i++){
            current = current.getNext();
        }
        //이전 노드에서 다음노드 head로 설정
        current.setNext(head);
        tail = current;
        // 마지막 노드는 아무도 참조하지 않아서 gc처리
        size--;
    }

    // 리스트 요소 삭제
    public void remove(int index){
        Node<T> current = head;
        if(index==0){
            head = current.getNext();
            tail.setNext(head);
            size--;
            return;
        }
        if(index == (size-1)){
            remove();
            return;
        }
        //index의 전까지 next()로 이동
        for(int i = 0 ; i<index-1; i++){
            current = current.getNext();
        }
        //이전 노드의 next에 index위치 노드의 다음노드를 할당
        current.setNext(current.getNext().getNext());
        //index위치의 노드는 gc처리된다.
        size--;
    }

    @Override
    public String toString(){
        String result="";
        Node<T> node = head;
        int index = 1;
        while(node.getNext()!=head){
            //for(int i = 0; i<size; i++){
            result +=  " Node("+(index++)+") : " + node +" ,";
            node = node.getNext();
            if(index>size*2) break;
        }
        result +=  " Node("+(index)+") : " + node +" ";

        result += " [ size : " + size + " ]";
        return result;
    }

    public T getFirst(){
        return head.getData();
    }
    public T getLast(){
        return tail.getData();
    }
}
```

### 설명

이중 연결리스트는 첫번째 노드를 가르키는 head라는 포인터와 마지막 노드인 tail이라는 포인터도 필드로 존재한다.
이 tail을 이용하면 삽입 시 어떤 장점이 있는지 바로 알아보자.  

- add(T data) , add(T data , int index)
    
    데이터를 추가할때 tail이 가르키는 노드를 current 변수에 담았다.  
    그 이후에 추가된 newNode를 setNext로 추가해주고 해당 newNode를 이제 tail 포인터로 가르키기만 하면 추가가 된다.  
    
    위에서 구현했던 단일 연결리스트에서는 head 밖에 없었기 때문에 반복을 통해 노드 끝까지 이동해서 추가했어야 했는데, tail이라는 포인터를 통해 빠른 데이터 추가가 가능해졌다.  
    그리고 원형 연결리스트이기 때문에 tail의 next는 항상 head를 가르킨다.  

    index를 통해 추가하는 작업은 이전과 동일하다.
    
- remove(T data) , remove(T data, int index)

    삭제는 단일 연결리스트와 같고 단지 마지막 노드를 지울때에는 그 이전노드까지 탐색 후 next를 null로 설정하고 난후 tail이 가르키는 주소를 해당 노드로 해줘야한다.  
    

## 이중 연결 리스트

이중 연결리스트는 이전 노드와는 다르게 다음 노드만을 가르키지 않고 이전 노드도 가르킨다.  
해당 특징을 이용해서 이제 검색할 인덱스를 찾기 위해 처음부터 순회하지 않고 인덱스가 뒤에서 검색하면 빠를 시에는 tail부터 뒤로 순회하게 된다.  
그리고 삭제시에도 용이한데 바로 코드를 통해 알아보자.  

- __Doubly LinkedList ADT__
    __필드__  
    \- Node<T> head : 현재 제일 앞쪽 노드를 가르키는 포인터   
    \- int size : 현재 요소의 개수  
    \- Node<T> head : 현재 제일 뒤쪽 노드를 가르키는 포인터   

    __기능__  
    \- add(T t) : 데이터 삽입 O(1)  
    \- add(T t ,int index) : 데이터 삭제 O(n)    
    \- remove() : 요소를 삭제 O(1)   
    \- remove(int index) : 요소를 삭제 O(n)  

### 이중 연결리스트 노드
```java
package linearStructure.linkedList;

public class DoubleNode<T> {
    private T data;
    private DoubleNode<T> next;
    private DoubleNode<T> prev;

    public DoubleNode(T data){
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public DoubleNode getNext() {
        return next;
    }

    public void setNext(DoubleNode next) {
        this.next = next;
    }

    public DoubleNode getPrev() {
        return prev;
    }

    public void setPrev(DoubleNode prev) {
        this.prev = prev;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString(){
        return (String)this.data;
    }
}
```

✔ 이중 연결리스트의 노드객체는 이전 노드를 가르키는 prev라는 필드도 추가되어 있다.  

### 이중 연결리스트 코드

```java
package linearStructure.linkedList.linkedListEx;

import linearStructure.linkedList.DoubleNode;

public class DoublyLinkedList<T> {
    private DoubleNode<T> head;
    private DoubleNode<T> tail;
    private int size;

    public DoublyLinkedList(){
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // 리스트 마지막에 요소 추가
    public void add(T data) {

        DoubleNode<T> newNode = new DoubleNode<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            DoubleNode<T> prevNode = tail;
            prevNode.setNext(newNode);
            newNode.setPrev(prevNode);
            tail=newNode;
        }
        size++;
    }

    // 리스트 중간에 요소 추가
    public void add(T data,int index) {
        DoubleNode<T> newNode = new DoubleNode<>(data);
        DoubleNode<T> current = head;
        DoubleNode<T> prevNode;
        if(index==0){
            head = newNode;
            head.setNext(current);
            size++;
            return;
        }
        if(index==size) {
            add(data);
            return;
        }
        current = findNode(index);
        prevNode = current.getPrev();

        newNode.setNext(current);
        newNode.setPrev(prevNode);
        prevNode.setNext(newNode);
        current.setPrev(newNode);
        size++;
    }
    // 리스트 요소 삭제
    public void remove(){
        if(size==0) {
            throw new RuntimeException("삭제할 요소가 없습니다.");
        }
        if (size==1){
            head = null;
            tail = null;
            size--;
            return;
        }
        //양방향 이므로 tail에서 이전노드를 current에 설정 후
        DoubleNode<T> current = tail.getPrev();
        //current의 다음 노드를 null로 할당
        current.setNext(null);
        tail = current;
        // 마지막 노드를 아무도 참조하지 않아서 gc처리
        size--;
    }

    // 리스트 요소 삭제
    public void remove(int index){
        DoubleNode<T> current = head;
        DoubleNode<T> prevNode;
        DoubleNode<T> nextNode;
        if(index==0){
            head=current.getNext();
            head.setPrev(null);
            size--;
            return;
        }else if(index==(size-1)) {
            remove();
            return;
        }
        //index의 전까지 next()로 이동
        current = findNode(index);

        //preNode에 index 이전 노드 할당
        prevNode = current;
        //이전 노드의 next에 현재 index노드의 next노드를 할당
        nextNode = current.getNext().getNext();
        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);
        //결론적으로 index위치의 노드는 gc처리
        size--;
    }

    private DoubleNode<T> findNode(int index){
        DoubleNode<T> resultNode = head;
        boolean odd = (size%2==0) ? true : false; //짝수인가?
        int coverage = (odd) ? size/2-1 : (size/2); // 범위 설정 (순회 방향 기준)
        if(index>coverage){
            resultNode = tail;
            for(int i = 0 ; i < size-index; i++){
                resultNode = resultNode.getPrev();
            }
            return resultNode;
        }
        for(int i = 0 ; i < index; i++){
            resultNode = resultNode.getNext();
        }
        return resultNode;
    }

    //toString 및 검색 등 메서드 생략..
}

```

### 설명  

이제 추가와 삭제를 보자.  
추가 시 로직은 이전과 거의 비슷하다.  
하지만 추가했을때 이제 해당 마지막 노드가 이전노드를 설정해주는 것이 추가 됬을 뿐이다.  

마지막 노드 삭제 시 head부터 마지막 노드 전까지 순회하여 노드를 삭제해야 했지만,
이제 이전 노드를 가져올 수 있는 getPrev()덕분에 삭제시 tail.getPrev()를 통해 삭제할 노드 이전 노드에서 setNext(null)을 통해 삭제 연산이 빨라졌다.  

그리고 findNode() 메서드를 보면 파라미터로 들어온 index가 현재 사이즈에서 앞에서 부터 순회할지 뒤에서 부터 순회할지 결정한 후에 반복횟수가 적은 쪽으로 순회하여 노드를 반환한다.  

하지만 해당 노드객체를 보면 중간에 삽입하는 과정이나 삭제하는 과정에서 이전 노드를 설정하는 등 단일 연결리스트의 노드보다 메모리 사용량이 많으므로 주의해야 한다.  
삽입 삭제 검색 등의 기능을 수행할때 그 만큼 빠르게 동작할 수 있기 때문에 상황에 따라 어떻게 구현해서 사용할지 선택하면 된다!!
> 자바의 경우에는 Node가 이전노드도 포함하는 필드를 기본적으로 가지고 있다.  

## 연결리스트의 핵심!  

연결 리스트의 핵심은 head(or first 등등..)라는 포인터가 첫 노드를 가르키며 해당 노드가 다음 생성되는 노드를 가르키므로써 `노드를 생성하거나 삭제 시에 동적으로 메모리 공간을 확보하거나 줄이게 된다`.  
배열과는 다르게 미리 크기를 컴파일 단계에서 정하지 않는다.  
`물리적인 메모리가 연속적이지 않아서` 찾고자하는 논리적 인덱스의 값에 접근하려면 O(n)의 시간이 걸린다.  
추가,삭제에 있어서는 추가할 노드가 해당 위치의 다음 노드를 가르키고 이전 노드가 현재 추가될 노드를 가르키기만 하면 되기 때문에 배열과 같이 shift 연산을 할 필요가 없어서 배열보다 추가,삭제 작업이 빠르다.  

추가적으로 마지막 노드를 가리키는 포인터(tail or last 등등..)이 구현될 수 있으며, 이중 연결리스트의 노드는 다음 노드 뿐만아니라 이전 노드도 가르키기 때문에 검색 효율이 더 좋아진다.  

이렇게 배열과는 다르게 연결리스트의 장점은 삽입 삭제가 빠르며, 메모리 공간을 동적으로 할당하기 때문에 메모리 관리가 용이하다.  
단, 물리적 연속성을 가지는 배열은 인덱스를 통해 개체 접근이 빠른 반면, 연결리스트는 요소에 접근이 느리다는 단점이 있다.  