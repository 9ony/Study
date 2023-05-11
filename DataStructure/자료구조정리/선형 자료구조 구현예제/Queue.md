# Queue 구현 예제

Queue 자료구조를 구현예제 입니다.  
구현 시 배열로도 구현한 것과 LinkedList를 통해서 구현해 볼 것인데, 일반적으로 왜 LinkedList가 Queue를 구현하는데 적합한지 알아보도록 하겠습니다.  
추가적으로 아래에 원형 큐와 덱도 간단히 구현 해보겠습니다!  

- __Queue의 ADT__
    __필드__  
    \- E[] Queue : 데이터를 저장할 배열
    \- int front : 제일 앞쪽의 데이터 포인터
    \- int rear : 제일 뒤쪽의 데이터 포인터
    \- int size : 현재 배열안에 요소의 개수
    \- int capacity : 요소를 담을 수 있는 최대 용량 크기

    __기능__  
    \- enqueue(E e) : 데이터 삽입 O(1)  
    \- dequeue() : 데이터 삭제 O(1)  
    \- peek() : 요소를 반환 O(1)  
    \- size() : 요소 개수 반환  
    \- isEmpty() : 비었는지 boolean 반환  
    

## Queue 구현 예제 (Array)

### Queue 배열로 구현한 코드

```java
public class CustomArrayQueue<E>{
    private static final int DefaultCapacity = 5;
    private E[] Queue;
    private int size;
    private int capacity;
    private int front;
    private int rear;

    public CustomArrayQueue(int capacity){
        QueueInit(capacity);
        Queue = newInstance(capacity);
    }

    public CustomArrayQueue(){
        QueueInit(DefaultCapacity);
        Queue = newInstance(capacity);
    }

    public void enqueue(E e){
        //내부 용량이 꽉 찼을경우
        if(isFull()) {
            resizingUp();
            //크기증가한 배열 생성
            E[] newQueue = newInstance(capacity);
            //크기를 증가한 배열에 현재 Queue 값을 할당
            for (int i = 0; i < size; i++) {
                newQueue[i] = Queue[i];
            }
            //Queue를 새롭게 생성한 Queue(new Queue)로 할당
            Queue = newQueue;
        }
        Queue[rear] = e;
        size++;
        rear++;
    }

    public void dequeue(){
        if(isEmpty()) throw new RuntimeException("큐가 비었습니다.");
        Object e = peek();
        Queue[front] = null;
        size--;
        front++;

    public E peek() {
        return Queue[front];
    }

    //내부 배열 크기(capacity)증가 (크기변경 시 true 반환)
    private boolean resizingUp(){

        if(size==Integer.MAX_VALUE>>2) throw new RuntimeException("내부 용량 초과");

        int newCapacity;

        if(capacity <= (Integer.MAX_VALUE>>3)){
            newCapacity = capacity << 1; //현재 내부 용량 2배증가
        }else newCapacity = Integer.MAX_VALUE>>2;

        //크기가 변하면 true
        if(capacity!=newCapacity){
            capacity = newCapacity;
            return true;
        }
        return false;
    }

    public boolean isFull(){
        return size==capacity;
    }

    public boolean isEmpty(){
        return (size==0) ? true : false;
    }

    protected void QueueInit(int capacity){
        this.capacity = capacity;
        size = 0;
        front = 0;
        rear = 0;
    }

    protected E[] newInstance(int capacity){
        return (E[]) new Object[capacity];
    }
}
```

### 설명

큐 자료구조는 현재 제일 앞에 위치한 인덱스를 저장한 front와 데이터가 들어가야할 인덱스인 rear이 있다.  
데이터를 추가하는 enqueue 메서드는 데이터를 추가할 위치인 rear값에 인덱스에 요소를 추가하는 메서드이다.  
이때 rear이 내부용량과 같을 때는 내부용량을 늘리는 resizingUp()메서드가 실행된 후 데이터를 추가한다.   

데이터 삭제하는 dequeue 메서드도 배열의 front 위치에 데이터를 삭제한 후에 front를 증가 시키는 동작을 한다.   

## LinkedListQueue 구현 예제  

### Queue 연결리스트로 구현한 코드  

> 연결리스트는 직접 구현한 연결리스트를 사용하였습니다.  
[연결리스트 코드 예제]()    

```java
public class CustomLinkedListQueue<E> {

    //private LinkedList<E> list;
    private DoublyLinkedList<E> list;
    private int size;
    private int capacity;

    public CustomLinkedListQueue(){
        //this.list = new LinkedList<E>();
        this.list = new DoublyLinkedList<E>();
        this.size = 0;
        this.capacity = 10;
    }

    public void enqueue(E element){
        list.add(element);
        size++;
        return true;
    }

    public E dequeue(){
        if(size==0){
            throw new RuntimeException("비었습니다");
        }

        E element =  list.getFirst();
        //list.removeFirst();
        list.remove(0);
        size--;
        return element;
    }

    public E peek(){
        return list.getFirst();
    }

    @Override
    public String toString(){
        return list.toString();
    }
}
```

연결리스트로 구현한 큐를 살펴보자.  
연결리스트는 head와 tail을 가지고 있으며, 각각 리스트의 첫요소와 마지막 요소를 가르킨다.  

requeue() 메서드에서 데이터 추가시 파라미터로 넘어온 요소를 add()를 통해 리스트에 추가해주기만 하면된다. 
이때 tail 뒤에 바로 해당 데이터를 연결만 시켜주면 되기 때문에 속도가 빠르다.  

dequeue() 삭제 메서드도 첫번째 요소를 remove(0)을 통해 인덱스를 지정해 줌으로써 이제 head가 첫번째 노드가 아닌 다음 노드를 가르키게 함으로써 첫번째 요소를 아무도 참조하지 않기 때문에 GC에 의해 처리되어 삭제되는 것이다.    

### 비교  
배열을 이용하면 데이터 추가 시 내부 용량이 꽉차면 배열의 크기를 늘려야되는 연산을 해야되고, 또 삭제 시 요소들을 쉬프트하는 동작을 추가적으로 해주어야 한다.  

연결리스트는 물리적으로 연결된 자료구조가 아니기 때문에 추가할 때는 노드를 생성해서 연결하고 삭제 시에는 연결된 노드만 끊으면 되기 때문에 삽입 삭제가 빨라진다.  
필요한 만큼의 노드를 동적으로 생성하므로 크기 조절에 유연하다.  

연결리스트로 구현하는게 적합하다고 볼 수 있을것 같다.   

### 추가 (원형 큐,덱)

원형 큐 자료구조라는 것이 있는데, 이는 특정 고정된 크기를 가지고 기존에 큐에 비해 메모리 공간을 효율적으로 사용하는 구조이다.  

기존에 배열은 데이터를 추가하고 삭제하면 앞에 빈공간이 계속 생기기 때문에 빈공간을 없애주려면 쉬프트 연산을 통해 앞으로 데이터를 다시 밀어넣어서 크기를 조정하는 작업등을 해주어야 한다.  

하지만 원형 큐를 이용하면 해당 빈공간을 다시 사용할 수 있는데, 데이터를 넣고 빼는 포인터 역할을 하는 필드인 front와 rear를 아래 그림처럼 계속 이동하면서 메모리 공간을 효율적으로 활용한다.  

![image](https://github.com/9ony/9ony/assets/97019540/1e6cb656-cfb5-47d1-84c1-c4f212bc1116)

이렇게 특정 크기를 정해놓고 해당 공간 내에서 front와 rear을 이동시키면서 메모리 공간의 낭비를 최소화 할 수 있다.  

- __원형 큐 구현 코드__

    ```java
    public class CustomCircleQueue<E> {

        private static final int DefaultCapacity = 5;
        private E[] Queue;
        private int size;
        private int capacity;
        private int front;
        private int rear;

        public CustomCircleQueue(int capacity){
            QueueInit(capacity);
            Queue = newInstance(capacity);
        }

        public CustomCircleQueue(){
            QueueInit(DefaultCapacity);
            Queue = newInstance(capacity);
        }

        public void enqueue(E e){
            if(isFull()) throw new RuntimeException("큐가 꽉 찼습니다");
            Queue[rear] = e;
            size++;
            rear++;
            if(rear==capacity) rear=0;
            //System.out.println("enqueue 실행 입력 값 = "+ e +" // size = " + size + " front = "+ front + " rear = "+ rear);
        }

        public void dequeue(){
            if(isEmpty()) throw new RuntimeException("큐가 비었습니다.");
            E e = Queue[front];
            Queue[front] = null;
            size--;
            front++;
            if(front==capacity) front=0;
            //System.out.println("dequeue 실행 삭제될 값 = "+ e +" // size = " + size + " front = "+ front + " rear = "+ rear);
        }

        public void enqueue2(E e){
            if(isFull2()) throw new RuntimeException("큐가 꽉 찼습니다");
            Queue[rear] = e;
            rear = (rear+1)%capacity;
            //System.out.println("enqueue 실행 입력 값 = "+ e +" // size = " + size + " front = "+ front + " rear = "+ rear);
        }

        public void dequeue2(){
            if(isEmpty2()) throw new RuntimeException("큐가 비었습니다.");
            E e = Queue[front];
            Queue[front] = null;
            front = (front+1)%capacity;
            //System.out.println("dequeue 실행 삭제될 값 = "+ e +" // size = " + size + " front = "+ front + " rear = "+ rear);
        }

        public boolean isFull(){
            return size==capacity;
        }

        public boolean isEmpty(){
            return (size==0) ? true : false;
        }

        public boolean isFull2(){
            return (rear+1)%capacity == front;
        }

        public boolean isEmpty2(){
            return rear==front;
        }

        public void print(){
            System.out.println(Arrays.toString(Queue));
        }

        public void QueueInit(int capacity){
            this.capacity = capacity;
            size = 0;
            front = 0;
            rear = 0;
        }

        public E[] newInstance(int capacity){
            return (E[]) new Object[capacity];
        }
    }

    ```

> 위 코드처럼 특정 고정된 메모리의 배열을 만들고 해당 메모리 영역내에서 효율적으로 사용해도 되지만, 연결리스트를 활용해서 논리적인 size를 만들어서 관리해도 될 것 같다.  

- __Deque__  

    덱은 일반적으로 이중연결리스트를 사용하는데, 이중연결리스트의 front,tail을 이용하여 양쪽으로 데이터 추가 및 삭제가 가능한 큐와 스택을 합친(?) 자료구조라 보면 된다.  

    > Deque는 정말 간단하게 구현해 보았습니다. (--)(__)

    __Deque ADT__  

    \- push : 데이터 추가   
    \- pushleft : 앞쪽에 데이터 추가  
    \- pop : 데이터 삭제 및 반환  
    \- popleft : 앞쪽에 있는 데이터 삭제 및 반환  
    \- extend : 배열을 입력받아 여러개를 추가  
    \- extendleft : 배열을 입력받아 여러개를 앞에 추가  
    

    ```java
    package linearStructure.deque;

    import java.util.Arrays;
    import java.util.LinkedList;

    public class CustomDeque<E> {

        private LinkedList<E> list;

        public CustomDeque(){
            list = new LinkedList<>();
        }

        public void push(E e){
            list.add(e);
        }
        public void pushleft(E e){
            list.add(0,e);
        }
        public E pop(){
            return list.removeLast();
        }
        public E popleft(){
            return list.remove();
        }
        public void extend(E[] es){
            Arrays.stream(es).forEach(e -> list.add(e));
        }

        public void extendleft(E[] es){
            Arrays.stream(es).forEach(e -> list.add(0,e));
        }

        @Override
        public String toString(){
            return list.toString();
        }
    }

    ```