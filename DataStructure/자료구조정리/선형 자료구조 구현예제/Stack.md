# Stack 구현 예제

Stack 자료구조를 구현해 보겠습니다.  
구현 시 배열로도 구현한 것과 LinkedList를 통해서 구현해 볼 것인데, 일반적으로 왜 LinkedList가 Stack을 구현하는데 적합한지 알아보도록 하겠습니다. 

- __Stack의 ADT__
    __필드__  
    \- E[] Stack : 데이터를 저장할 배열
    \- int top : 현재 최상단 요소의 인덱스 값  
    \- int size : 현재 배열안에 요소의 개수
    \- int fixedCapacity : 요소를 담을 수 있는 최대 용량 크기

    __기능__  
    \- push(E element) : 데이터 삽입 O(1)  
    \- pop() : 배열에 top위치의 데이터 삭제 후 반환 O(1)  
    \- peek() : 요소를 반환 O(1)  
    \- size() : 요소 개수 반환  
    \- isEmpty() : 비었는지 boolean 반환  

## Stack 구현 예제 (정적배열)

### Stack 정적배열로 구현한 코드

```java
public class CustomArrayStack<E> {
    private int fixedCapacity; //최대용량
    private int size; //요소 개수
    private int top; //top 인덱스
    private E[] Stack;

    public CustomArrayStack(int fixedCapacity){
        this.fixedCapacity = fixedCapacity;
        this.size=0;
        this.top=0;
        Stack = newInstance(fixedCapacity);
    }

    private E[] newInstance(int capacity){
        return (E[]) new Object[capacity];
    }

    public boolean push(E element){
        if(isFull()) {
            throw new RuntimeException("꽉 참");
        }
        Stack[size]=element;
        size++;
        top=size-1;
        //Stack[size++]=element;
        return true;
    }

    public E pop(){
        if(isEmpty()) {
            throw new RuntimeException("비었음");
        }
        E popElement = peek();
        Stack[top] = null;
        size--;
        top=size-1;
        //Stack[--size]=element;
        return popElement;
    }

    public E peek(){
        return Stack[top];
    }

    public boolean isFull(){
        return size==fixedCapacity;
    }

    public boolean isEmpty(){
        return size==0;
    }

    @Override
    public String toString(){
        String str;
        str = Arrays.toString(Stack);
        return str;
    }
}
```

데이터를 추가하는 push()를 보면 배열에 현재 size값의 인덱스에 데이터를 할당해주고 있습니다.  

데이터를 삭제하는 메서드인 pop()도 현재 top의 위치(size-1)배열의 값을 리턴하기 위해 변수(popElement)에 저장한 후 null로 할당하고 있습니다.  

만약 동적배열로 구현한다면 데이터 추가 삭제 시 마다 배열의 사이즈를 키우고 줄여야하기 때문에 O(n)의 시간 복잡도가 발생할 수 있습니다.  

## Stack 구현 예제 (연결 리스트)

스택 구현 시 크기조정을 자유롭게 하고싶으면 연결리스트를 사용하여 구현하는게 좋습니다.  
연결리스트는 삽입 삭제도 O(1)의 시간복잡도를 가지며 메모리 공간도 동적으로 할당하여 자유롭게 조정이 가능하기 때문입니다.  

### Stack 연결 리스트로 구현한 코드

```java
package linearStructure.stack;

import java.util.Arrays;
import java.util.LinkedList;

public class CustomLinkedListStack<E> {
    private int size; //요소 개수
    private LinkedList<E> list;

    public CustomLinkedListStack(){
        this.size=0;
        this.top=0;
        list = new LinkedList<>();
    }

    public boolean push(E element){
        list.add(element);
        size++;
        return true;
    }

    public E pop(){
        if(isEmpty()) {
            throw new RuntimeException("비었음");
        }
        E popElement = peek();
        list.removeLast();
        size--;
        return popElement;
    }

    public E peek(){
        return list.peekLast();
    }

    public boolean isEmpty(){
        return size==0;
    }

    @Override
    public String toString(){
        String str;
        str = Arrays.toString(list.toArray());
        return str;
    }
}

```