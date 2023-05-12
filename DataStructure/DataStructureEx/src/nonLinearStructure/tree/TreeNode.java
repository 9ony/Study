package nonLinearStructure.tree;

import java.util.Comparator;

public class TreeNode<E> implements Comparable{
    private E data;
    private TreeNode<E> left; //자식 왼쪽 브랜치
    private TreeNode<E> right; // 자식 오른쪽 브랜치

    public TreeNode(E e){
        this.data = e;
        this.left = null;
        this.right = null;
    }

    public E getData() {

        return this.data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public TreeNode<E> getLeft() {
        return left;
    }

    public void setLeft(TreeNode<E> left) {
        this.left = left;
    }

    public TreeNode<E> getRight() {
        return right;
    }

    public void setRight(TreeNode<E> right) {
        this.right = right;
    }

    @Override
    public int compareTo(Object o) {
        if(this.data instanceof String){
            //data와 o 문자열을 비교하여 data가 사전적으로 빠르면 양수, 아니면 음수 같으면 0
            //같은 경우 다음 문자를 비교하여 반복하는데, 만약 aabb(data) , aa(o)를 비교할 경우 2가 출력됨.
            //즉, o가 더 사전적으로 빠름
            return ((String) this.data).compareToIgnoreCase((String)o);
        }
        if(this.data instanceof Integer){
            if(((Integer) this.data).intValue() < ((Integer) o).intValue()) return 1;
            else if(((Integer) this.data).intValue() == ((Integer) o).intValue()) return 0;
            else return -1;
        }
        return 0;
    }

    @Override
    public String toString(){
        String leftData = (left != null) ? left.getData().toString() : null;
        String rightData = (right != null) ? right.getData().toString() : null;
        return "Data: " + data + " 왼쪽 자식 노드: " + leftData+ " 오른쪽 자식 노드: " + rightData;
    }
}
