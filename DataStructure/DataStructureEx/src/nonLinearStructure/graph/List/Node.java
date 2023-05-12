package nonLinearStructure.graph.List;

public class Node<E extends Comparable> {
    private E data;
    private Node next;

    public Node(){
        data = null;
        next = null;
    }

    public Node(E data) {
        this.data = data;
        next=null;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    @Override
    public String toString(){
        return this.data.toString();
    }

}
