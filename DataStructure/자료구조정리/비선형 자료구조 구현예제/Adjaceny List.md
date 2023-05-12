# 그래프 구현(2)

## 연결리스트를 이용한 그래프 구현(인접 리스트)

### 그래프를 리스트로 나타낸 예시

- 그래프 그림  

    ![image](https://github.com/9ony/9ony/assets/97019540/e745e297-fb04-4897-b25f-a29ec5eb30e3)  

위와 같은 무방향 그래프는 아래와 같은 리스트로 나타낼 수 있습니다.  

- 그래프 리스트 표시  

    정점 1의 인접노드 : 2 -> 3 -> 4  
    정점 2의 인접노드 : 1 -> 3 -> 4  
    정점 3의 인접노드 : 1 -> 2 -> 5  
    정점 4의 인접노드 : 1 -> 2   
    정점 5의 인접노드 : 3  

    해당 정점인 1에 연결된 정점은 2,3,4이기 때문에 정점1은 노드 2,3,4를 연결하고 있는것을 볼 수 있습니다.  

### 인접리스트 장단점

해당 정점에 연결된 정점들(간선) 만큼만 리스트에 담고있어서 메모리가 절약되는 장점이 있습니다.  

하지만 연결된 간선을 탐색할 때에 인접행렬과 같이 인덱스로는 접근할 수 없기때문에 해당 정점리스트에서 정점을 조회한 후 정점이 가지고 있는 간선을 조회해야하여 탐색시간은 인접행렬보다는 느립니다.  

## 인접리스트 구현  

### 인접리스트 ADT

create_graph() : 그래프를 생성  
AdjacencyList() : 그래프 g를 초기화  
insertVertex(v) : 그래프 g에 정점 v를 삽입  
insertEdge(u, v) : 그래프 g에 간선 (u,v)를 삽입  
deleteVertex(v) : 그래프 g의 정점 v를 삭제  
deleteEdge(u, v) : 그래프 g의 간선 (u,v)를 삭제  
isEmpty() : 그래프 g가 공백 상태인지 확인  
adjacent(v) : 정점 v에 인접한 정점들의 리스트를 반환  

### 인접리스트 구현코드

```java
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

```