package linearStructure.arrayList;

import java.util.*;

/**
 * 동적배열
 * 정적배열은 크기가 고정이었지만 동적배열은 크기를 늘릴 수 있는 기능을 가졌다.
 * 내부적으로는 고정적인 크기를 가지고 있으며, 해당 크기를 capacity라고 한다.
 * 외부적으로 현재 capacity안에 포함되어 있는 요소들의 개수를 size라고 한다.
 * (자바 기준)
 * size가 capacity의 크기만큼 할당되고 있는중에 요소를 추가(add)하게 되면 resizing이 되며 capacity를 늘린다.
 * capacity가 증가한 배열을 생성하고 기존(old)에 정적배열의 요소들을 복사한다.
 * 동적배열은 원시타입을 저장하지 못한다.
 *
 */
public class DynamicArrayEx {

    public static void main(String[] args) {

        /*CustomArrayList<String> test = new CustomArrayList<>(50);
        test.add("첫번째 요소");
        test.add("두번째 요소");
        test.add("네번째 요소");
        String setValue = test.set("세번쨰 요소",2);
        
        test.add("네번째 요소");
        test.remove(3);
        test.remove(1);
        test.add("test");
        test.insert("a",1);
        
        System.out.println(setValue);
        System.out.println("현재 커스텀 동적배열 내부 크기 = "+test.capacity());
        System.out.println("현재 커스텀 동적배열 요소 개수 = "+test.size());
        System.out.println("현재 커스텀 동적배열 요소 출력 = "+ test);*/
        
        //제네릭은 원시타입을 받을 수 없음 기존 자바의 Collection (ArrayList포함)은 제네릭으로 런타임시 동적으로 타입을 정함
        //CustomArrayList<int> test2 = new CustomArrayList<>(2);

        //내부용량 예외 테스트
        //Integer.MAX_VALUE>>2 이상으로 배열을 생성하면 힙 메모리 초과 발생
        /*CustomArrayList<Integer> test2 = new CustomArrayList<>(Integer.MAX_VALUE>>2);
        for(int i = 0; i<Integer.MAX_VALUE;i++){
            test2.add(1);
        }
        System.out.println(Integer.MAX_VALUE>>2);
        System.out.println("test size = "+test2.size());*/

        CustomArrayList<Integer> test2 = new CustomArrayList<>(21);
        for(int i=0; i<21; i++){
            test2.add(i);
        }
        System.out.println("size => " + test2.size());
        System.out.println(test2);
        test2.remove(20);
        test2.remove(19);
        //test2.remove(18);
        System.out.println("삭제후 size => "+ test2.size());
        System.out.println(test2);
        /*CustomArrayList<Integer> test2 = new CustomArrayList<>();
        for(int i = 0; i<10; i++){
            test2.add(0);
            test2.insert(1,0);
        }
        System.out.println("test size = "+test2.size());
        System.out.println(test2);
        */
    }
}
