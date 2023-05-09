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
        nonPrimitives[0] = new NonPrimitive("test"); //NonPrimitive 객체 주소값이 인덱스 0에 들어감
        
        //인덱스 0에 원시타입 int형 1 할당
        intArr[0]=1;
        //int형 타입의 배열에 Integer를 넣게되면 Integer타입이 언박싱되어 int형으로 들어감
        Integer integer1 = new Integer(1);
        intArr[1]=integer1;

        System.out.println(intArr[0]);
        System.out.println(intArr[1]/*.toString()*/); //원시타입으로 언박싱 -> Object 메서드 사용 불가

        Integer[] integerArr = new Integer[]{1,new Integer(1)}; // 1 -> new Integer(1) 참조형 박싱(Wrapping)
        //Integer(참조형)와 int(원시타입) == 연산자 비교 시 내부적으로 값비교를 함 그래서 true
        //intArr[1] = Integer(1)이지만 언박싱되어 int형이기 떄문에 값 비교를 하는 것임
        System.out.println(integerArr[1]==intArr[1]); 
        //반대로 Integer끼리 비교하면 당연히 주소값이 다르니 false
        System.out.println(new Integer(1) == new Integer(1)); //주소비교 false
        System.out.println(new Integer(1) == 1); //값비교 true

        System.out.println(new String("test") == "test"); //"test"는 원시타입 아님
        System.out.println(Integer.parseInt(new String("1")) == 1);

        System.out.println(integerArr[1].toString());
        
        System.out.println(Arrays.toString(intArr)); //0,0,0,0,0
        System.out.println(Arrays.toString(nonPrimitives)); //객체주소,null,null,null,null

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
