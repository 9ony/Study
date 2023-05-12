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
