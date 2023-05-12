# 힙 구현 예제

## 힙이란?  

완전 이진 트리의 일종인 자료구조이다.  

> 완전 이진 트리는 마지막을 제외한 모든 노드의 자식들이 꽉 채워진 이진 트리로 우선순위 큐를 위하여 만들어진 자료구조이다.  

여러 개의 값들 중에서 최댓값/최솟값을 빠르게 찾을때 사용하기 좋은 자료구조다.  

힙의 종류에는 2가지가 있는데,  

\- 최대 힙 : 부모노드가 자식노드보다 큰값을 만족하는 완전 이진 트리   

\- 최소 힙 : 최대힙의 반대로 부모노드가 자식노드보다 잡은 값을 만족하는 완전 이진 트리   

느슨한 정렬 상태(반정렬 상태) 유지  

> 반정렬 상태란 배열로 출력했을 때 정렬되지 않은 상태이다.  

이진 탐색 트리와 달리 중복 값 허용  


## 우선순위 큐 구현 예제

우선순위 큐를 구현 할 때 힙트리를 사용하여 구현하는게 일반적이고, 구현하면서 힙트리와 우선순위 큐가 어떤 것인지 알아보자.  

- __Priority Queue ADT__
    
    \- add(E e) : 요소를 추가  
    
    \- poll() : 최대값 or 최소값 삭제 및 반환
    
    \- peek() : 최대값 or 최소값 반환

    \- isEmpty() : 힙 내에 요소 존재 유무(boolean) 반환  

    \- avobeHeapify(int index) : 해당 index 부터 부모요소와 비교하여 정렬(오름차순or내림차순)

    \- belowHeapify(int index) : 해당 index 부터 자식요소를 비교하여 정렬(오름차순or내림차순)

### 힙트리 구현 코드

```java
package nonLinearStructure.tree.binarytree;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * 힙 트리는 연결리스트가 아닌 배열을 사용하여 구현하는게 효과적이다.
 * 그 이유는 연결리스트로 구현한 트리를 사용할 경우 마지막노드를 찾기 힘들기 때문이다.
 * 힙 트리는 부모노드가 자식트리보다 항상 크거나 작은 값을 가지는 이진트리 자료구조이며,
 * 우선순위 큐는 해당 힙 트리를 이용하여 우선순위가 높은 값을 빠르게 입출력하는 목적을 가지는 추상화된 자료형이다.
 */
public class HeapTree<E extends Comparable> {
    ArrayList<E> array;
    int size;
    //오름차순이면 양수 (최대 힙), 내림차순이면 음수(최소 힙)
    int check = 1;

    public HeapTree(){
        /*
        프로그램언어 내에 동적배열을 사용하지 않고 정적배열을 사용한다면,
        배열 사이즈를 변경하는 메서드를 추가적으로 작성해 주어야 한다.
        */
        array = new ArrayList<>();
        //0번째 index는 사용하지 않음
        //부모 인덱스 * 2 = 왼쪽 자식
        //부모 인덱스 * 2 + 1 = 오른쪽 자식
        array.add(0,null);
        size = 0;
    }

    public void add(E e){
        //우선 요소를 추가한다.
        array.add(e);
        int index = array.size()-1;
        
        //데이터 추가 시 해당 요소와 부모 요소를 비교하여 스왑하는 메서드 실행
        upHeapify(index);
        //이후 요소에 들어간 index값을 가져와서 부모노드와 비교한다.
        size++;
    }

    public E remove(){
        if(isEmpty()) throw new NoSuchElementException("힙이 비어있습니다.");
        //삭제될 요소 저장
        E e = array.get(1);
        //사이즈가 1보다 작을 경우 요소가 없거나 1개이므로 heap정렬 X
        if(size>1) {
            //마지막 요소를 삭제하고 삭제시 반환되는 요소를 루트로 설정
            array.set(1, array.remove(size));
            size--;
            //그 후 하향식 정렬 (오름차순or내림차순)
            downHeapify(1);
        }else {
            array.remove(size);
            size--;
        }
        return e;
    }

    //단말노드부터 루트노드까지 힙구조로 정렬 (상향식)
    private void upHeapify(int startIndex){
        E lastElement = array.get(startIndex);
        //부모값 저장
        E parentElement = array.get(startIndex/2);

        if(parentElement == null || lastElement.compareTo(parentElement) * check <=0){
            return;
        }

        //오름차순이면 부모가 작을경우 스왑(최대 힙), 내림차순이면 부모가 클 경우 스왑(최소 힙)
        if(lastElement.compareTo(parentElement) * check > 0){
            array.set(startIndex,parentElement);
            array.set(startIndex/2,lastElement);
        }
        upHeapify(startIndex/2);
    }
    
    //루트노드부터 단말노드까지 힙구조로 정렬 ( 하향식 )
    private void downHeapify(int startIndex){
        if(size<=1) return;
        E parentData = array.get(startIndex);
        int currentIndex = startIndex;
        int leftIndex = currentIndex*2;
        int rightIndex = leftIndex+1;
        
        //자식 왼쪽인덱스가 현재 배열의 사이즈보다 크면 단말노드임.
        if(leftIndex > size) return;

        E leftChild = array.get(leftIndex);
        E rightChild = rightIndex > size ? null : array.get(rightIndex);

        // 우측 자식이 없다면 좌측 자식만 비교
        if(rightChild == null){
            if(leftChild.compareTo(parentData) * check > 0) {
                array.set(currentIndex,leftChild);
                array.set(leftIndex,parentData);
            }
            //오른쪽 자식이 없으면 다음 비교대상은 없다.
            return;
        }else {
            E swapTarget = leftChild.compareTo(rightChild) * check > 0 ? leftChild : rightChild;
            int swapIndex = leftChild.compareTo(rightChild) * check > 0 ? leftIndex : rightIndex;

            if (swapTarget.compareTo(parentData) * check > 0) {
                array.set(currentIndex, swapTarget);
                array.set(swapIndex, parentData);
            }

            currentIndex = swapIndex;
        }
        downHeapify(currentIndex);
    }


    /*
    힙의 특성을 변경하기 위해선
    존재하는 단말노드를 상향식 정렬을 해주어야함.
    maxHeap과 minHeap은 기존에 heap의 유형을 바꾸므로 재배열시간이 오래걸리므로 사용에 있어서 주의해야한다.
     */
    //최대힙으로 설정
    public void maxHeap(){
        if(this.check==-1){
            this.check = 1;
            reOrder(size);
        }

    }
    //최소힙으로 설정
    public void minHeap(){
        if(this.check==1) {
            this.check = -1;
            reOrder(size);
        }
    }
    //재정렬 메서드
    private void reOrder(int i){
        //재정렬
        if(i*2<size) return;
        upHeapify(i);
        reOrder(i-1);
    }

    public E peek(){
        return array.get(1);
    }

    private boolean isEmpty(){
        return size==0;
    }

    public int size(){
        return size;
    }

    @Override
    public String toString(){
        return array.toString();
    }
}
```

### Heap트리를 이용한 PriorityQueue(우선순위 큐) 구현

heap트리를 이용하여 우선순위 큐를 작성하면 구현은 간단하다.  
힙 트리에 데이터를 추가하고 꺼내오기만 하면된다.  

```java
package linearStructure.queue;

import nonLinearStructure.tree.binarytree.HeapTree;

import java.util.Comparator;
import java.util.NoSuchElementException;

public class CustomPriorityQueue<E extends Comparable>{

    private HeapTree<E> heap;
    private int size=0;

    public CustomPriorityQueue(){
        this.heap = new HeapTree<E>();
        size = 0;
    }

    //false일 경우 최소힙으로 정렬
    public CustomPriorityQueue(boolean b){
        this.heap = new HeapTree<E>();
        size = 0;
        if(!b){
            heap.minHeap();
        }
    }

    public void add(E e){
        heap.add(e);
        size++;
    }


    public E poll(){
        if(isEmpty()) throw new NoSuchElementException("큐가 비어있습니다.");
        E e = heap.remove();
        size--;
        return e;
    }

    public E peek(){
        if(isEmpty()) throw new NoSuchElementException("큐가 비어있습니다.");
        return heap.peek();
    }

    public boolean isEmpty(){
        return size==0;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(heap);
        str.delete(1,7); //null, 까지 삭제한 String을 반환
        if(str.length()<2) return null;
        return str.toString();
    }


}
```

- 데이터 추가 흐름
    
    ![image](https://github.com/9ony/9ony/assets/97019540/9379c31c-a5a1-44f8-bf86-f1407d04051f)  

    31이라는 데이터를 추가했다.  
    힙 트리는 완전 이진트리 이므로 마지막 단말 노드를 제외한 노드의 자식이 반드시 2개가 있어야 한다.  

    ![image](https://github.com/9ony/9ony/assets/97019540/767cd011-2ba9-430c-b50f-bfe683bb0e86)  

    부모노드가 현재 추가된 자식노드보다 작으므로 스왑된다.  
    ✔ 최소힙일 경우 반대다.   

    ![image](https://github.com/9ony/9ony/assets/97019540/a642066d-5c68-461f-b829-07881da2db33)  

    스왑된 위치의 또 부모노드를 비교하여 부모노드가 더 크므로 정렬을 중지하여, 힙 트리의 특성을 유지한다.  

- 데이터 삭제 흐름  

    ![image](https://github.com/9ony/9ony/assets/97019540/5df4f5cc-c4f9-4d03-8682-a3d0f94b7efa)  

    데이터 삭제라고 표현했지만 우선순위 큐에서 poll()메서드를 실행할때 발생하는 과정이라 봐도 무방하다.  
    루트노드는 항상 우선순위가 가장 높은 값이다.  

    ![image](https://github.com/9ony/9ony/assets/97019540/77861809-2c6f-4634-94a0-4c1fb7efad56)  

    해당 값을 삭제하면 최하단 단말노드의 오른쪽노드가 루트노드 값으로 스왑된다.  

    ![image](https://github.com/9ony/9ony/assets/97019540/02418cdb-8e6c-4eb3-95b6-db5f45c200aa)  

    이후 해당 루트노드의 자식노드 둘을 비교하여 큰값과 스왑한다.   
    ✔ 최소힙일 경우 반대다.   
    
    ![image](https://github.com/9ony/9ony/assets/97019540/1bac29f6-43bb-4479-9e43-11ca8b8ad790)  

    자식 노드가 현재 노드보다 작으므로 스왑을 중지하여 힙의 특성을 유지한다.  

### 정리  

우선순위 큐는 일반적인 큐와 다르게 선입선출의 개념이 아니라 우선순위를 정하여 우선순위가 높은 데이터가 먼저 빠져나오는 `추상적인 자료형`이다.  

우선순위 큐 구현을 위한 자료구조로 힙 트리를 사용하는 것이고 힙 트리는 루트노드가 자식노드보다 큰 최대힙과 그 반대인 최소힙이 있다.

힙 트리는 우선순위가 높은 값을 찾을때 이진트리의 특성 상 탐색 시 자식노드와 비교하여 우선순위가 높은 값을 찾기 때문에 O(logn)의 시간복잡도를 가지게 되는 것이고, 일반 배열이나 연결리스트를 사용하여 구현할 경우 우선순위가 높은 값들을 찾기 위해서는 최악의 경우 요소들을 전부 탐색해야 할 경우도 있기 때문에 O(n)의 시간복잡도를 가지게 된다.  

위 구현 예제에서는 힙트리를 이용하여 우선순위큐를 구현함으로써 우선순위가 높은 값을 조회할때는 O(1)의 시간복잡도를 가지며, 삽입과 삭제 시에는 힙의 특성이 깨짐으로 데이터를 정렬하는 과정이 추가되기 때문에 O(logn)을 가지게 되었다.  

힙 자료구조 자체는 정렬되지 않은 데이터이지만 우선순위 큐는 내부 힙 트리 내에서 루트노드를 계속해서 출력하기 때문에 우선순위가 높은 순으로 정렬된 데이터를 출력하게 된다.   

해당 힙트리와 우선순위 큐는 밀접한 관계가 있으며, 힙트리 구현 예제를 통해 우선순위 큐도 같이 알아보았다.  

### ✔ 왜 배열을 사용하나?  

만약 연결리스트로 힙트리를 구현했다고 가정하자.  
그러면 값을 추가할때 부모노드가 빈곳도 탐색해야 하며, 삭제 할때도 마지막 노드를 탐색해서 스왑하는 과정을 거쳐야 한다.  
논리적인 인덱스가 있다한들 해당 인덱스의 수 만큼 노드를 거쳐가서 접근하기 때문에 비효율 적인 것이다.  
반면에 배열은 만약 요소를 추가한다 했을때 해당 요소의 인덱스의 나누기 2만 해주면 바로 부모인덱스를 찾을 수 있다.  
삭제도 마찬가지로 루트노드를 삭제한후 마지막요소는 배열의 마지막 인덱스로 바로 접근하면 되기 때문에 배열이 효과적이다.  


