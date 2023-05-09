package linearStructure.arrayList;

import java.util.Arrays;

/**
 *
 * @param <E>
 *
 * 동적배열을 배열로 구현
 * ArrayList ADT(Abstract Data Type)
 *  add(E e) : 요소를 삽입 O(1)
 *  insert(E e,int index) : 해당 index에 요소를 추가 O(n)
 *  set(E e,int index) : 해당 index의 요소를 수정 O(1)
 *  remove(i index) : 해당 index 요소 제거 O(n)
 *  get(index i) : 해당 index의 요소를 반환 O(1)
 */
public class CustomArrayList<E>  {
    //private static final int ArrayMaximumSize = Integer.MAX_VALUE>>2;
    private static final int ArrayMaximumSize = 500;
    private static final int DefaultCapacity = 10; //기본 내부용량
    //동적배열 내부 용량
    private int capacity;
    //동적배열 내 포함한 요소 용량
    private int size;

    private E[] array;

    public CustomArrayList(int capacity){
        if(capacity>ArrayMaximumSize) {
            throw new RuntimeException("배열이 최대 사이즈는 "+ArrayMaximumSize +" 입니다.");
        }else if(capacity < 1){
            this.capacity = DefaultCapacity;
        }else this.capacity = capacity;

        this.array = newInstance(this.capacity);
        this.size = 0;
    }

    public CustomArrayList(){
        this.capacity = DefaultCapacity;
        this.array = newInstance(capacity);
        this.size = 0;
    }

    //요소 삽입
    public boolean add(E element){
        //내부 배열 크기보다 size가 클 경우
        resizingUp();
        array[size++]=element; //현재 size의 인덱스에 요소 추가 후 1 증가
        return true;
    }

    //요소 추가
    public boolean insert(E element,int index){
        if(index < 0 ||index > size) {
            throw new IndexOutOfSizeException("IndexOutOfBoundsException 예외와 같음");
        }
        //내부용량 크기 증가 메서드
        resizingUp();
        /*for (int i = 0, j = 0; i < size+1; i++) {
            if (index != i) {
                newArray[i] = Array[j];
                j++;
            } else newArray[i] = element;
        }*/
        System.arraycopy(array, index, array, index+1, size-index);
        array[index] = element;
        size++; // size 증가
        return true;
    }

    //요소 삭제
    public E remove(int index){
        if (index < 0 || index >= size) {
            throw new IndexOutOfSizeException("IndexOutOfBoundsException 예외와 같음");
        }
        if(size == 0 ) throw new RuntimeException("삭제할 요소가 없습니다.");

        E oldValue = array[index]; //반환할 삭제할 요소
        System.out.println("내부배열 크기 삭제 전 정보 : " + size + " / " + capacity);
        int lastElementSize = size - (index+1);
        //마지막 요소의 index가 아닐때
        if (lastElementSize > 0) {
            //삭제할 인덱스를 그다음 인덱스값으로 채우고 남은 요소를 마저 복사
            System.arraycopy(array, index+1, array, index, lastElementSize);
        }
        //마지막 인덱스의 값을 null로 할당
        array[--size] = null;
        resizingDown();
        return oldValue;
    }

    //요소 접근
    public E get(int index){
        if(index < 0 ||index >= size) {
            throw new IndexOutOfSizeException("조회한 인덱스가 배열의 범위를 넘어섰습니다.");
        }
        return array[index];
    }
    
    //요소 수정
    public E set(E element,int index){
        if(index < 0 ||index >= size) {
            throw new IndexOutOfSizeException("조회한 인덱스가 배열의 범위를 넘어섰습니다.");
        }
        E oldValue = array[index];
        array[index] = element;
        return oldValue;
    }

    //요소 개수 조회
    public int size(){
        return this.size;
    }

    //객체 내부 크기 조회 (TEST 용)
    protected int capacity(){
        return this.capacity;
    }

    //내부 배열 크기(capacity)증가 메서드
    private void resizingUp(){
        if(size>=ArrayMaximumSize) throw new SizeOutOfCapacityException("내부 범위 초과");
        if(size>=capacity) {
            //내부 배열크기 증가 단, ArrayMaximumSize보다 크게 설정은 안한다.
            capacity = (capacity << 1 > ArrayMaximumSize) ? ArrayMaximumSize : capacity << 1;

            //Math API를 사용해 축약 가능
            //capacity = Math.min(ArrayMaximumSize,capacity<<1);

            E[] newArray = newInstance(capacity);
            System.arraycopy(array,0,newArray,0,size);
            /*for(int i = 0 ; i < size; i ++) {
                newArray[i] = array[i];
            }*/
            array = newArray;
        }
    }

    //내부 배열 크기감소 메서드
    private void resizingDown(){
        if( size < (capacity>>1) ) {
            //capacity = (capacity >> 1 < DefaultCapacity) ? DefaultCapacity : capacity >> 1;
            capacity = Math.max(capacity >> 1, DefaultCapacity);
            E[] newArray = newInstance(capacity);
            System.arraycopy(array,0,newArray,0,capacity);
            array = newArray;
        }
    }

    //제네릭타입의 배열을 생성
    private E[] newInstance(int capacity){
        return (E[]) new Object[capacity];
    }

    @Override
    public String toString(){
        //배열 출력만 Arrays API를 사용함
        return Arrays.toString(array);
    }

    //사이즈가 초과할때 발생시킬 런타임 Exception
    static class SizeOutOfCapacityException extends RuntimeException{
        SizeOutOfCapacityException(String e){
            super(e);
        }
    }

    //인덱스가 size크기를 초과할때 발생시킬 런타임 Exception
    static class IndexOutOfSizeException extends RuntimeException{
        IndexOutOfSizeException(String e){
            super(e);
        }
    }
}
