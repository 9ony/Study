package linearStructure.stack;

import java.util.Arrays;
import java.util.LinkedList;

public class CustomLinkedListStack<E> {
    private int size; //요소 개수
    private int top; //top 인덱스
    private LinkedList<E> list;

    public CustomLinkedListStack(){
        this.size=0;
        this.top=0;
        list = new LinkedList<>();
    }

    public boolean push(E element){
        list.add(element);
        size++;
        top=size-1;
        return true;
    }

    public E pop(){
        if(isEmpty()) {
            throw new RuntimeException("비었음");
        }
        E popElement = peek();
        list.removeLast();
        size--;
        top=size-1;
        return popElement;
    }

    public E peek(){
        return list.peekLast();
    }

    public boolean isEmpty(){
        return (size==0) ? true : false;
    }

    @Override
    public String toString(){
        String str;
        str = Arrays.toString(list.toArray());
        return str;
    }
}
