package linearStructure.queue;

import linearStructure.linkedList.linkedListEx.DoublyLinkedList;

public class CustomLinkedListQueue<E> {

    //private LinkedList<E> list;
    private DoublyLinkedList<E> list;
    private int size;

    public CustomLinkedListQueue(){
        //this.list = new LinkedList<E>();
        this.list = new DoublyLinkedList<E>();
        this.size = 0;
    }

    public void enqueue(E element){
        list.add(element);
        size++;
    }

    public E dequeue(){
        if(size==0){
            throw new RuntimeException("비었습니다");
        }

        E element =  list.getFirst();
        //list.removeFirst();
        list.remove(0);
        size--;
        return element;
    }

    public E peek(){
        return list.getFirst();
    }

    @Override
    public String toString(){
        return list.toString();
    }
}
