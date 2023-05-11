package linearStructure.stack;

import java.util.Arrays;

/**
 *  Stack ADT
 *  push(E e) : 요소를 삽입 O(1)
 *  pop() : 요소를 제거 및 반환 O(1)
 *  peek() : 요소를 반환 O(1)
 *  isEmpty() : 비어있으면 true 아니면 false
 */
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
        /*Stack[size]=element;
        size++;
        top=size-1;*/
        Stack[size++]=element;
        return true;
    }

    public E pop(){
        if(isEmpty()) {
            throw new RuntimeException("비었음");
        }
        E popElement = peek();
        /*Stack[top] = null;
        size--;
        top=size-1;*/
        Stack[--size]=null;
        return popElement;
    }

    public E peek(){
        //return Stack[top];
        return Stack[size-1];
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
