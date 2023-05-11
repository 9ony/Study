package linearStructure.deque;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Deque ADT
 *
 * push : 데이터 추가
 * pushleft : 앞쪽에 데이터 추가
 * pop : 데이터 삭제 및 반환
 * popleft : 앞쪽에 있는 데이터 삭제 및 반환
 * extend : 배열을 입력받아 여러개를 추가
 * extendleft : 배열을 입력받아 여러개를 앞에 추가
 */
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
