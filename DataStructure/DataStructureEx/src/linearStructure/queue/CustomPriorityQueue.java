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