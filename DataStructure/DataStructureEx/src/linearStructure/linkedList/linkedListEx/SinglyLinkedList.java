package linearStructure.linkedList.linkedListEx;

import linearStructure.linkedList.Node;

public class SinglyLinkedList<T> {

    private Node<T> head;
    private int size;

    public SinglyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    // 리스트 마지막에 요소 추가
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
        }
        size++;
    }

    // 리스트 중간에 요소 추가
    public void add(T data,int index) {
        Node<T> newNode = new Node<>(data);
        Node<T> current = head;
        Node<T> nextNode;
        if(index==0){
            newNode.setNext(current);
            head=newNode;
            size++;
            return;
        }
        if(index == size){
            add(data);
            return;
        }
        //인덱스 위치의 노드 이전까지 이동
        for(int i = 0 ; i<index-1;i++){
            current = current.getNext();
        }
        //추가된 노드의 next에 해당되는 노드
        nextNode = current.getNext();
        current.setNext(newNode);
        newNode.setNext(nextNode);
        size++;
    }
    // 리스트 요소 삭제
    public void remove(){
        if(size==0){
            throw new RuntimeException("삭제할 요소가 없습니다.");
        }
        if (size==1){
            head = null;
            size--;
            return;
        }
        Node<T> current = head;
        //마지막 노드 이전 노드까지 가도록 getNext() 반복
        for(int i = 0 ; i<size-2; i++){
            current = current.getNext();
        }
        //이전 노드에서 다음노드 null설정
        current.setNext(null);
        // 마지막 노드를 아무도 참조하지 않아서 gc처리
        size--;
    }

    // 리스트 요소 삭제
    public void remove(int index){
        Node<T> current = head;
        if(index==0){
            head = current.getNext();
            size--;
            return;
        }
        if(index == (size-1)){
            remove();
            return;
        }
        //index의 전까지 next()로 이동
        for(int i = 0 ; i<index-1; i++){
            current = current.getNext();
        }
        //이전 노드의 next에 index위치 노드의 다음노드를 할당
        current.setNext(current.getNext().getNext());
        //index위치의 노드는 gc처리된다.
        size--;
    }

    @Override
    public String toString(){
        String result="";
        Node<T> node = head;
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
