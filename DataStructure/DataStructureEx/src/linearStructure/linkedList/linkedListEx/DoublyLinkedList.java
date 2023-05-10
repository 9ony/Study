package linearStructure.linkedList.linkedListEx;

import linearStructure.linkedList.DoubleNode;

public class DoublyLinkedList<T> {
    private DoubleNode<T> head;
    private DoubleNode<T> tail;
    private int size;

    public DoublyLinkedList(){
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // 리스트 마지막에 요소 추가
    public void add(T data) {

        DoubleNode<T> newNode = new DoubleNode<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            DoubleNode<T> prevNode = tail;
            prevNode.setNext(newNode);
            newNode.setPrev(prevNode);
            tail=newNode;
        }
        size++;
    }

    // 리스트 중간에 요소 추가
    public void add(T data,int index) {
        DoubleNode<T> newNode = new DoubleNode<>(data);
        DoubleNode<T> current = head;
        DoubleNode<T> prevNode;
        if(index==0){
            head = newNode;
            head.setNext(current);
            size++;
            return;
        }
        if(index==size) {
            add(data);
            return;
        }
        current = findNode(index);
        prevNode = current.getPrev();

        newNode.setNext(current);
        newNode.setPrev(prevNode);
        prevNode.setNext(newNode);
        current.setPrev(newNode);
        size++;
    }
    // 리스트 요소 삭제
    public void remove(){
        if(size==0) {
            System.out.println("없음");
            return;
        }
        if (size==1){
            head = null;
            tail = null;
            size--;
            System.out.println("마지막노드였음");
            return;
        }
        //양방향 이므로 tail에서 이전노드를 current에 설정 후
        DoubleNode<T> current = tail.getPrev();
        //current의 다음 노드를 null로 할당
        current.setNext(null);
        tail = current;
        // 마지막 노드를 아무도 참조하지 않아서 gc처리
        size--;
    }

    // 리스트 요소 삭제
    public void remove(int index){
        DoubleNode<T> current = head;
        DoubleNode<T> prevNode;
        DoubleNode<T> nextNode;
        if(index==0){
            head=current.getNext();
            head.setPrev(null);
            size--;
            return;
        }else if(index==(size-1)) {
            remove();
            return;
        }
        //index의 전까지 next()로 이동
        current = findNode(index);

        //preNode에 index 이전 노드 할당
        prevNode = current;
        //이전 노드의 next에 현재 index노드의 next노드를 할당
        nextNode = current.getNext().getNext();
        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);
        //결론적으로 index위치의 노드는 gc처리
        size--;
    }

    /*
     * 총 사이즈가 10
     * 삭제할 인덱스가 6
     * 삭제할 인덱스 > size/2
     * tail에서 시작하여 nextPrev로 역순 탐색 (더 빠르니까)
     * 반복 범위 i = 0 ; i < size-index; i++; tail.nextprev()
     * */
    private DoubleNode<T> findNode(int index){
        DoubleNode<T> resultNode = head;
        boolean odd = (size%2==0) ? true : false; //짝수인가?
        int coverage = (odd) ? size/2-1 : (size/2); // 범위 설정 (순회 방향 기준)
        if(index>coverage){
            resultNode = tail;
            for(int i = 0 ; i < size-index; i++){
                resultNode = resultNode.getPrev();
            }
            return resultNode;
        }
        for(int i = 0 ; i < index; i++){
            resultNode = resultNode.getNext();
        }
        return resultNode;
    }

    public String toStringReverse(){
        String result="";
        DoubleNode<T> node = tail;
        int index = 1;
        while(node.getPrev()!=null){
            //for(int i = 0; i<size; i++){
            result +=  " Node("+(index++)+") : " + node +" ,";
            node = node.getPrev();
        }
        result +=  " Node("+(index)+") : " + node +" ";
        result += " [ size : " + size + " ]";
        return result;
    }

    @Override
    public String toString(){
        String result="";
        DoubleNode<T> node = head;
        int index = 1;
        while(node.getNext()!=null){
            //for(int i = 0; i<size; i++){
            result +=  " Node("+(index++)+") : " + node +" ,";
            node = node.getNext();
        }
        result +=  " Node("+(index)+") : " + node +" ";
        result += " [ size : " + size + " ]";
        return result;
    }

    public T getFirst(){
        return head.getData();
    }
}
