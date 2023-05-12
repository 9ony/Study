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
