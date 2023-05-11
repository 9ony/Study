package linearStructure.queue;

import java.util.Arrays;

public class CustomCircleQueue<E> {

    private static final int DefaultCapacity = 5;
    private E[] Queue;
    private int size;
    private int capacity;
    private int front;
    private int rear;

    public CustomCircleQueue(int capacity){
        QueueInit(capacity);
        Queue = newInstance(capacity);
    }

    public CustomCircleQueue(){
        QueueInit(DefaultCapacity);
        Queue = newInstance(capacity);
    }

    public void enqueue(E e){
        if(isFull()) throw new RuntimeException("큐가 꽉 찼습니다");
        Queue[rear] = e;
        size++;
        rear++;
        if(rear==capacity) rear=0;
        //System.out.println("enqueue 실행 입력 값 = "+ e +" // size = " + size + " front = "+ front + " rear = "+ rear);
    }

    public void dequeue(){
        if(isEmpty()) throw new RuntimeException("큐가 비었습니다.");
        E e = Queue[front];
        Queue[front] = null;
        size--;
        front++;
        if(front==capacity) front=0;
        //System.out.println("dequeue 실행 삭제될 값 = "+ e +" // size = " + size + " front = "+ front + " rear = "+ rear);
    }

    public void enqueue2(E e){
        if(isFull2()) throw new RuntimeException("큐가 꽉 찼습니다");
        Queue[rear] = e;
        rear = (rear+1)%capacity;
        //System.out.println("enqueue 실행 입력 값 = "+ e +" // size = " + size + " front = "+ front + " rear = "+ rear);
    }

    public void dequeue2(){
        if(isEmpty2()) throw new RuntimeException("큐가 비었습니다.");
        E e = Queue[front];
        Queue[front] = null;
        front = (front+1)%capacity;
        //System.out.println("dequeue 실행 삭제될 값 = "+ e +" // size = " + size + " front = "+ front + " rear = "+ rear);
    }

    public boolean isFull(){
        return size==capacity;
    }

    public boolean isEmpty(){
        return size==0;
    }

    public boolean isFull2(){
        return (rear+1)%capacity == front;
    }

    public boolean isEmpty2(){
        return rear==front;
    }

    public void print(){
        System.out.println(Arrays.toString(Queue));
    }

    public void QueueInit(int capacity){
        this.capacity = capacity;
        size = 0;
        front = 0;
        rear = 0;
    }

    public E[] newInstance(int capacity){
        return (E[]) new Object[capacity];
    }
}
