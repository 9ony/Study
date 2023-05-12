# 그래프 구현(1)

## 베열을 이용한 그래프 구현(인접 행렬)

인접 행렬은 그래프에서 정점이 어떤 간선으로 연결되었는지를 나타내는 행렬로 정점 N개의 그래프에 대해서 N x N 2차원 배열인 행렬을 이용해 구현합니다.  
정점끼리 연결되어 있다면 행렬의 값은 1, 그렇지 않다면 배열의 값을 0으로 설정합니다.  

### 그래프를 배열로 나타낸 예시

- 그래프 그림  

    ![image](https://github.com/9ony/9ony/assets/97019540/e745e297-fb04-4897-b25f-a29ec5eb30e3)  

위와 같은 무방향 그래프는 아래와 같은 2차원 배열로 나타낼 수 있습니다.  

- 그래프 행렬 표시  

    ||1|2|3|4|5|
    |---|---|---|---|---|---|
    1|0|1|1|1|0|
    2|1|0|1|1|0|
    3|1|1|0|0|1|
    4|1|1|0|0|0|
    5|0|0|1|0|0|

    해당 정점인 1에 연결된 정점은 2,3,4이기 때문에 1로 표시되어 있고, 1인 자기자신과 5는 0으로 표시된것을 볼 수 있습니다.  

### 인접행렬 장단점

인접리스트보다 탐색이 빠릅니다.  
adj[i][j]처럼 인덱스를 통해 접근이 가능합니다.  

하지만 다른 정점에 연결되어 있지 않아도 정점의 개수만큼 배열을 생성하므로 미리 메모리에 확보되기 때문에 메모리 낭비가 있을 수 있습니다.   

## 인접행렬 구현

### 인접행렬 ADT

AdjacencyMatrix(int n) : n개의 정점을 가진 그래프를 생성  
insertVertex(int n) : 그래프 g에 n만큼의 정점을 추가  
insertEdge(u, v) : 그래프 g에 간선 (u,v)를 삽입  
deleteVertex(v) : 그래프 g의 정점 v를 삭제  
deleteEdge(u, v) : 그래프 g의 간선 (u,v)를 삭제  
isEmpty(g) : 그래프 g가 공백 상태인지 확인  
adjacent(v) : 정점 v에 인접한 정점들의 리스트를 반환
printMaxtrix() : 그래프를 출력  

### 인접행렬 구현코드

```java
package nonLinearStructure.graph.matrix;

import java.util.Arrays;
import java.util.HashMap;

public class AdjacencyMatrix {

    int[][] g;
    int vertexSize; //정점의 개수


    public AdjacencyMatrix(int n){
        this.g = new int[n][n];
        this.vertexSize = n;
    }

    //파라미터의 수만큼 정점개수 증가
    public void insertVertex(int n){
        this.g = resizingUp(n);
        this.vertexSize = g.length;
    }

    public void insertEdge(int v1,int v2){
        if (!isValidVertex(v1) || !isValidVertex(v2)) {
            throw new IllegalArgumentException("유효하지 않은 정점입니다.");
        }
        if(v1==v2) throw new IllegalArgumentException("연결하려는 정점은 다른 정점이어야 합니다.");
        g[v1][v2] = 1;
        g[v2][v1] = 1;
    }

    public void deleteEdge(int v1,int v2){
        if (!isValidVertex(v1) || !isValidVertex(v2)) {
            throw new IllegalArgumentException("유효하지 않은 정점입니다.");
        }
        g[v1][v2] = 0;
        g[v2][v1] = 0;
    }

    public void deleteVertex(int vertex){
        if(!isValidVertex(vertex)) throw new IllegalArgumentException("해당 정점은 그래프에 포함되어 있지 않은 정점입니다.");
        g = resizingDown(vertex);
        this.vertexSize = g.length;
    }


    public int getVertexSize() {
        return vertexSize;
    }

    public int[] adjacent(int v){
        return g[v];
    }

    public boolean isEmpty(){
        return vertexSize==0;
    }

    public void printMaxtrix(){
        if (g.length==0){
            throw new ArrayStoreException("그래프가 비어있습니다.");
        }
        int i=0;
        while (i<g.length){
            System.out.println(Arrays.toString(g[i]));
            i++;
        }
    }


    //추가할 정점의 개수 n만큼 크기증가
    private int[][] resizingUp(int n){
        int newG[][] = new int[vertexSize+n][vertexSize+n];
        int i = 0 ;
        while(i<vertexSize) {
            System.arraycopy(g[i],0, newG[i], 0, vertexSize);
            i++;
        }
        return newG;
    }
    

    private int[][] resizingDown(int v){
        int newG[][] = new int[vertexSize-1][vertexSize-1];
        int i = 0;
        int j = 0;
        while(i<vertexSize-1) {
            if(i==v) j=1;
            System.arraycopy(g[i+j],0, newG[i], 0, v);
            System.arraycopy(g[i+j],v+1, newG[i], v, vertexSize-(v+1));
            i++;
        }
        return newG;
    }

    private boolean isValidVertex(int v) {
        return v >= 0 && v < vertexSize;
    }

}
```