package linearStructure.linkedList.linkedListEx;

import linearStructure.linkedList.Node;

public class CircularLinkedList<T> {

    private Node<T> head;
    private int size;
    //tail이라는 포인터를 만들어 줌으로써 요소 추가를 빠르게 가져갈 수 있다.
    private Node<T> tail;

    public CircularLinkedList() {
        this.head = null;
        this.size = 0;
        this.tail = null;
    }

    // 리스트 마지막에 요소 추가
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
            head.setNext(head);
        } else {
            Node<T> current = tail;
            current.setNext(newNode);
            newNode.setNext(head);
            tail = newNode;
        }
        size++;
    }

    // 리스트 중간에 요소 추가
    public void add(T data,int index) {
        Node<T> newNode = new Node<>(data);
        Node<T> current = head;
        Node<T> nextNode;
        if(index==0){
            head = newNode;
            head.setNext(current);
            tail.setNext(head);
            size++;
            return;
        }if(index==size){
            add(data);
            return;
        }
        for(int i = 0 ; i<index-1;i++){
            current = current.getNext();
        }
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
        //이전 노드에서 다음노드 head로 설정
        current.setNext(head);
        tail = current;
        // 마지막 노드는 아무도 참조하지 않아서 gc처리
        size--;
    }

    // 리스트 요소 삭제
    public void remove(int index){
        Node<T> current = head;
        if(index==0){
            head = current.getNext();
            tail.setNext(head);
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
        while(node.getNext()!=head){
            result +=  " Node("+(index++)+") : " + node +" ,";
            node = node.getNext();
        }
        /*for(int i = 0; i<size*2; i++){
            result +=  " Node("+(index++)+") : " + node +" ,";
            node = node.getNext();
            if(index > size) index = 1;
        }*/
        result +=  " Node("+(index)+") : " + node +" ";

        result += " [ size : " + size + " ]";
        return result;
    }

    public T getFirst(){
        return head.getData();
    }
    public T getLast(){
        return tail.getData();
    }
}
