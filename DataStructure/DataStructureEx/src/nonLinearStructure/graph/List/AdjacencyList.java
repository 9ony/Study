package nonLinearStructure.graph.List;

import java.util.*;

public class AdjacencyList<E extends Comparable> {

    int vertexSize;
    //정점의 집합 배열
    ArrayList<Node<E>> vertexList;

    public AdjacencyList(){
        vertexList = new ArrayList<>();
        vertexSize = 0;
    }
    //정점 추가
    public void insertVertex(E data){
        vertexList.add(new Node<E>(data));
        vertexSize++;
    }

    //간선 추가
    public void insertEdge(E v1 , E v2){
        Node<E> vertex = vertexList.stream()
                .filter(a -> a.getData().equals(v1))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정점입니다."));
        //해당 정점 vertext의 next가 null일때 까지 이동 후 next에 정점v2와 같은 데이터값을가진 노드 삽입
        while(vertex.getNext()!=null){
            if(vertex.getData().equals(v2)) throw new IllegalArgumentException("이미 존재하는 간선입니다.");
            vertex = vertex.getNext();
        }
        vertex.setNext(new Node<E>(v2));
    }

    //정점 삭제
    //정점 삭제 시 해당 정점을 포함하는 간선을 모두 삭제해야함
    public void deleteVertex(E data){
        Optional<Node<E>> v = vertexList.stream().filter(a -> a.getData().equals(data)).findAny();
        vertexList.remove(v.get());
        vertexSize--;
        //해당 정점을 포함하는 간선도 삭제
        for(int i = 0 ; i < vertexSize; i++){
            deleteEdge(vertexList.get(i).getData(),data);
        }
    }
    //간선 삭제
    public void deleteEdge(E v1, E v2){
        Node<E> vertex = vertexList.stream().filter(a -> a.getData().equals(v1))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(v1 + "는 존재하지 않는 정점입니다."));
        //해당 정점 vertext의 next가 null일때 까지 이동 후 next에 정점v2와 같은 데이터값을가진 노드 삽입
        Node<E> parent = vertex;
        while(vertex!=null){
            if(vertex.getData().equals(v2)){
                parent.setNext(vertex.getNext());
            }
            parent = vertex;
            vertex = vertex.getNext();
        }
    }

    public boolean isEmpty(){
        return vertexList.isEmpty();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        vertexList.stream().forEach(a -> {
            while(a!=null){
                sb.append(a.getData() +" -> ");
                a = a.getNext();
            }
            sb.append(" null"+"\n");
        });
        return sb.toString();
    }
}
