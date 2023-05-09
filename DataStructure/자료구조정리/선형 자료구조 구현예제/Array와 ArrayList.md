# 정적배열과 동적배열 예시 코드

> 해당 예시 코드는 자바로 작성됬으며 최대한 API 사용을 지양하였습니다.  

코드가 미흡할 수도 있지만 동적배열이 어떻게 크기를 조정하는지 큰그림을 코드로 작성하면서 이해하려고 작성했습니다.  
왜 요소에 대한 접근은 시간복잡도가 O(1) 이며 추가 삭제와 같은 작업은 O(n)이 나오는지 코드로 알아보았습니다!  

### 정적배열 예시

```java
package linearStructure.arrayList;

import java.util.Arrays;

/**
 * 정적배열의 특징
 * 순차적인 데이터 저장 
 * => 메모리에 순차적으로 저장됨 메모리주소가 0x01일때 int를 담는 배열이면,
 *    int(=4byte) 0x01~0x04 (=index 0) 0x05~0x08(index 1) ... 크기만큼 증가
 *    배열의 메모리주소 + (자료형 크기 * index 번호) = 찾고자 하는 배열의 요소 메모리주소가 나옴
 * 생성 시 고정적인 크기와 데이터 타입을 지정
 * 조회와 데이터 수정이 빠르다.(index를 이용하여서) O(1)
 * 데이터 추가와 삭제는 O(n)
 */
public class ArrayEx {
    public static void main(String[] args) {
        int[] intArr = new int[5]; //이 시점에 5칸에 초기값 0이 삽입된다.

        // 사이즈5의 int(primitive),nonPrimitive(Object) 배열 생성
        NonPrimitive[] nonPrimitives = new NonPrimitive[5];
        nonPrimitives[0] = new NonPrimitive("test");
        System.out.println(Arrays.toString(nonPrimitives)); //객체주소,null,null,null,null
        //객체 주소가 뜨는 이유는 toString이 재구현이 안되있기 때문


        int[][] 다차원배열 = new int[3][3]; //다차원도 가능 (matrix)
        //-------------------------------------------

        //요소 삭제
        int[] beforeIntArr = new int[]{0, 1, 2, 3, 4};
        //2번째 인덱스를 삭제하려면?  
        int index = 2; //삭제할 인덱스

        //배열은 크기가 고정되어 있으므로 4의 크기를 가진 새로운 배열 생성
        int[] afterIntArr = new int[4];

        for(int i = 0, j = 0; i < beforeIntArr.length; i++){
            if(i != index) { //i가 삭제할 인덱스가 아닐경우만 복사
                afterIntArr[j++] = beforeIntArr[i];
            }
        }
        //시간복잡도는 O(n)이다.
        System.out.println(Arrays.toString(afterIntArr)); //0,1,3,4

        //요소 추가 : 삭제와 같이 크기를 늘려서 복사 후 추가할 값 넣기 O(n)
        beforeIntArr = new int[]{0, 1, 2, 3, 4};
        int[] afterIntArr2 = new int[6];
        index = 2;
        int value = 100;
        for(int i = 0 , j=0; i<afterIntArr2.length;i++){
            if(i != index) { //i가 삭제할 인덱스가 아닐경우만 복사
                afterIntArr2[i] = beforeIntArr[j];
                j++;
            }else afterIntArr2[i] = value;
        }
        System.out.println(Arrays.toString(afterIntArr2));

        //요소 접근 및 수정 : O(1) 인덱스만 알면됨
        
        /*
        정리
        조회 (Read): O(1)
        수정 (Update): O(1)
        추가 (Insertion) 또는 삭제 (Deletion): O(n) (새로운 배열을 만들어 복사해야 함)
         */
    }
    //임의의 참조타입 생성
    static class NonPrimitive{
        String name;
        public NonPrimitive(){
        }
        public NonPrimitive(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

```

정적배열을 초기화시 사이즈를 할당하게 되며 할당된 사이즈에 해당 타입의 초기값이 들어가게 된다.  

그리고 배열은 연속적인 메모리 구조를 가지기 때문에 현재 `intArr주소 +(타입의 바이트 수 * 인덱스)`를 하게 되면 요소의 물리주소를 얻을 수 있어서 인덱스를 통해 빠르게 요소에 접근이 가능한 것이다.  

정적배열은 기본적으로 크기조정이 안된다.  
이 말은 크기를 조정하려면 새로운 배열을 메모리에 할당하여 기존배열을 복사하는 작업을 거쳐야되는 것이다.  

배열은 연속적인 메모리 구조를 가지기 때문에 배열의 끝이 아닌 중간요소를 추가하거나 할때 기존에 있던 메모리 주소를 옆으로 밀어내는 작업을 해줘야 해서 배열에 데이터를 추가하거나 삭제 작업을 할때는 시간복잡도가 O(n)이 되는 것이다.
이는 동적배열도 마찬가지이다.  

### 동적배열 예시

- 요소 삽입 (add)  

    ```java
    //요소 삽입
    public boolean add(E element){
        //내부 배열 크기보다 size가 클 경우
        resizingUp();
        array[size++]=element; //현재 size의 인덱스에 요소 추가 후 1 증가
        return true;
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
    ```

    capacity는 현재 동적배열 내부의 실제 크기이다.  
    size는 현재 내부배열이 가지고 있는 요소의 개수로 자바의 ArrayList에서 size() 메서드를 통해 조회하게 되면 해당 size의 값(요소의 개수)이 반환된다.  
    내부 용량(capacity)이 현재 요소 개수(size)와 같거나 크면 배열의 크기를 늘리는 resizingUp() 메서드가 실행되면서 내부 용량인 capacity를 증가시킨다.  
    그 이후에 크기를 증가시킨 새로운 배열인 newArray에 기존 배열을 복사하는 과정을 거치게 되고 현재 size에 add()메서드로 들어온 element요소를 추가하게 되는 것이다.  

- 요소 추가 (insert)
    
    ```java
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
    ```

    insert를 하게 될때도 내부 요소 크기가 내부용량과 같게되면 크기를 늘리는 작업을 하게된다.  
    
    주석처리된 for문을 보게되면 새롭게 생성된 배열에 기존 배열을 복사하고 있는데,  
    이때 요소가 들어갈 index를 건너뛰고 기존 배열값을 할당하고 있다.  
    그 이후에 파라미터로 들어온 element를 해당 index 위치에 할당했다.  

    > copyArray()메서드에서 system.arraycopy()를 사용한 로직도 위와 같은 결과가 나오는 복사과정을 거치게 되는데 네이티브 코드라서 빠르다고 한다.  
    참고로 Arrays.copyOf()의 메서드도 내부에서 system.arraycopy()를 사용한다.  

- 요소 삭제 (remove)

    ```java
    //요소 삭제
    public E remove(int index){
        if (index < 0 || index >= size) {
            throw new IndexOutOfSizeException("IndexOutOfBoundsException 예외와 같음");
        }
        if(size == 0 ) throw new RuntimeException("삭제할 요소가 없습니다.");

        E oldValue = array[index]; //반환할 삭제할 요소
        int lastElementSize = size - (index+1);
        //마지막 요소의 index가 아닐때
        if (lastElementSize > 0) {
            //삭제할 인덱스를 그다음 인덱스값으로 채우고 남은 요소를 마저 복사
            System.arraycopy(array, index + 1, array, index, lastElementSize);
        }
        //마지막 인덱스의 값을 null로 할당
        array[--size] = null;
        resizingDown();
        return oldValue;
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
    ```
    요소를 삭제할때도 삭제할 index가 size보다 크거나 0이하의 값이 들어오면 범위밖이라는 예외를 던진다.  
    또 size가 0일 시 요소가 없다는 뜻이니 삭제할 요소가 없다는 예외를 던진다.  
    요소를 삭제하기 전에 현재 사이즈가 내부 배열의 사이즈보다 작을 때 내부배열 사이즈를 줄여 메모리를 확보하는 작업을 거치게 되는데, 이때 resizingDown()메서드를 보면 현재 요소 개수가 (현재 내부배열의 크기 / 2) 보다 작을 경우 반으로 줄인 배열로 복사되는 것을 볼 수 있다.  

    lastElementSize 변수는 현재 요소 개수에서 인덱스+1을 뺀 값인데,  
    `System.arraycopy(array, index + 1, array, index, lastElementSize);`를 보면,  
    기존 배열의 값의 복사할 index가 index + 1인 것을 볼 수 있다.  
    이는 파라미터로 들어온 index를 건너뛴 다음 인덱스 부터 복사를 시작하기 때문에 1을 더 빼준것이다.  
    만약 건너 뛴 만큼 복사할 반복횟수를 빼주지 않는다면 capacity가 여유있지 않은경우 인덱스용량 초과 예외가 발생 할 것이다.  
    그리고 복사 대상의 index는 파라미터로 들어온 삭제할 index인데, 다음 요소(index + 1) 값으로 덮어씌우면 된다.  
    그리고 `array[--size] = null;`로 마지막 요소를 null로 할당한다.  

    > size-1 = 마지막이었던 인덱스  

    만약 lastElementSize가 0 일경우 index가 마지막 요소이므로 쉬프트작업을 하지 않고 그냥 `array[--size] = null;`을 통해 마지막 요소를 null로 할당한다.  


### 정리

- 추가,삭제 동작 그림
    __마지막 요소에 값 추가__  
    ![image](https://github.com/9ony/9ony/assets/97019540/a4e9105e-e92a-4d5e-a1fd-78a64ee23a66)  
    
    __중간or처음 요소에 값 추가__  
    ![image](https://github.com/9ony/9ony/assets/97019540/5fe94491-e3e8-4303-ac17-02df91c08db1)  

    __중간 요소 삭제__  
    ![image](https://github.com/9ony/9ony/assets/97019540/4f71fc2f-8221-4fb1-bb7c-971511ce038a)

- 정적배열과 동적배열 비교 그림

![image](https://github.com/9ony/9ony/assets/97019540/b792ba6c-f4da-456a-8029-14a2c33a0081)


동적배열은 내부에 배열에 할당되지 않는 공간을 줄이면서 정적 배열보다 메모리 관리에 용이하며,  
데이터가 들어올 공간이 없을 때는 새로운 배열을 만들어서 늘려줌으로써 크기를 확장하는 것이다.  


### ✔ CustomArrayList<E>.class 전체코드   

```java
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
        resizingDown();
        int lastElementSize = size - (index+1);
        //index가 0이 아닐때
        if (lastElementSize > 0) {
            //삭제할 인덱스를 그다음 인덱스값으로 채우고 남은 요소를 마저 복사
            System.arraycopy(array, index + 1, array, index, lastElementSize);
        }
        array[--size] = null;
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

```
