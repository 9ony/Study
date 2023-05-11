package linearStructure.queue;
/**
 * Queue ADT
 * enqueue(E e) : 데이터 삽입 O(1) //크기 증가 시 O(n)
 * dequeue() : 데이터 삭제 O(1) //제일 먼저 들어간 것
 * peek() : 요소를 반환 O(1)
 * size() : 요소 개수 반환
 * isEmpty() : 비었는지 boolean 반환
 */
public class CustomArrayQueue<E>{
    private static final int DefaultCapacity = 5;
    private E[] Queue;
    private int size;
    private int capacity;
    private int front;
    private int rear;

    public CustomArrayQueue(int capacity){
        QueueInit(capacity);
        Queue = newInstance(capacity);
    }

    public CustomArrayQueue(){
        QueueInit(DefaultCapacity);
        Queue = newInstance(capacity);
    }

    public void enqueue(E e){
        if(isFull()) {
            resizingUp();
            //크기증가한 배열 생성
            E[] newQueue = newInstance(capacity);
            //이것도 동적 배열예제와 같이 직관적으로 보기 위해 for문으로 배열 복사를 했지만
            //Arrays.copyOf() or System.arraycopy를 이용하자!
            for (int i = 0; i < size; i++) {
                newQueue[i] = Queue[i];
            }
            Queue = newQueue;
        }
        Queue[rear] = e;
        size++;
        rear++;
        //System.out.println("enqueue 실행 입력 값 = "+ e +" // size = " + size + " front = "+ front + " rear = "+ rear);
    }

    public void dequeue(){
        if(isEmpty()) throw new RuntimeException("큐가 비었습니다.");
        Object e = peek();
        Queue[front] = null;
        size--;
        front++;
        //System.out.println("dequeue 실행 삭제될 값 = "+ e +" // size = " + size + " front = "+ front + " rear = "+ rear);
    }

    public E peek() {
        return Queue[front];
    }

    //내부 배열 크기(capacity)증가 (크기변경 시 true 반환)
    private boolean resizingUp(){

        if(size==Integer.MAX_VALUE>>2) throw new RuntimeException("내부 용량 초과");

        int newCapacity;

        if(capacity <= (Integer.MAX_VALUE>>3)){
            newCapacity = capacity << 1; //현재 내부 용량 2배증가
        }else newCapacity = Integer.MAX_VALUE>>2;

        //크기가 변하면 true
        if(capacity!=newCapacity){
            capacity = newCapacity;
            return true;
        }
        return false;
    }
    public boolean isFull(){
        return size==capacity;
    }

    public boolean isEmpty(){
        return size==0;
    }

    private void QueueInit(int capacity){
        this.capacity = capacity;
        size = 0;
        front = 0;
        rear = 0;
    }

    private E[] newInstance(int capacity){
        return (E[]) new Object[capacity];
    }
}
