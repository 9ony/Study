package nonLinearStructure.tree.binarytree;

import nonLinearStructure.tree.TreeNode;

public class BinarySearchTree<E>{
    private TreeNode<E> root;
    private int size;

    public BinarySearchTree(){
        root = null;
        size = 0 ;
    }

    public void add(E element) {
        TreeNode<E> current = root;
        TreeNode<E> newNode = new TreeNode<>(element);
        if(root==null) {
            root = newNode;
            size++;
            return;
        }

        TreeNode<E> parent = null; //초기화
        int compareData = 0; //초기화

        while(current!=null){
            parent = current;
            //추가할 데이터와 현재 노드의 데이터값 비교한 결과값 작으면 음수(-1), 크면 양수(1)
            //노드 객체에 Comparable를 상속받아 구현해야함!
            compareData = current.compareTo(newNode.getData());
            if(compareData<0){
                current = current.getLeft();
            }else{
                current = current.getRight();
            }
        }
        if(compareData < 0) {
            parent.setLeft(newNode);
        }else {
            parent.setRight(newNode);
        }
        size++;
    }

    public E remove(E e) {
        //System.out.println("삭제 메서드 시작");
        TreeNode<E> removeParent = null; // 삭제할 노드의 부모
        TreeNode<E> removeTarget = root; // 삭제할 노드

        while (removeTarget != null) {
            if(removeTarget.compareTo(e) == 0) break;
            removeParent = removeTarget;
            if (removeTarget.compareTo(e) < 0) {
                removeTarget = removeTarget.getLeft();
            } else {
                removeTarget = removeTarget.getRight();
            }
        }

        if (removeTarget == null) {
            //System.out.println("삭제할 노드가 트리에 존재하지 않음");
            return null;
        }
        //System.out.println("삭제할 노드의 부모노드 "+ removeParent);
        
        //삭제할 노드가 root인 경우
        if(removeParent==null){
            root = getSuccessor(removeTarget);
        }else {
            if (removeParent.compareTo(e) > 0) removeParent.setRight(getSuccessor(removeTarget));
            else removeParent.setLeft(getSuccessor(removeTarget));
        }
        size--;
        return e;
    }

    private TreeNode<E> getSuccessor(TreeNode<E> removeTarget) {
        TreeNode<E> successor = null;
        //removeTarget의 자식노드를 변수에 할당
        //추후 후임자의 왼쪽 오른쪽노드에 할당
        TreeNode<E> removeTargetChildLeft = removeTarget.getLeft();
        TreeNode<E> removeTargetChildRight = removeTarget.getRight();
        TreeNode<E> successorParent = null;
        
        //삭제할 노드 왼쪽자식이 null이 아닐 경우
        if(removeTargetChildLeft!=null){
            successorParent=removeTarget;
            successor=removeTargetChildLeft;
            //삭제할 노드 왼쪽 노드를 시작으로 오른쪽 노드가 null이 될때 가지 탐색
            while(successor.getRight()!=null){
                successorParent = successor;
                successor = successor.getRight();
            }
            
            //삭제할 노드의 후임자를 찾았을 경우 왼쪽 자식이 있다면 해당 노드의 부모노드의 오른쪽에 설정
            if(successor!=removeTargetChildLeft) {
                //왼쪽노드가 없을경우 후임자의 부모노드가 null을 가르키게 된다.
                successorParent.setRight(successor.getLeft());
                successor.setLeft(removeTargetChildLeft);
            }
            //삭제할 노드 왼쪽자식이 후임자일 경우 해당 노드의 오른쪽을 삭제할노드 오른쪽자식으로 연결한다.
            successor.setRight(removeTargetChildRight);
        }else {
            //삭제할 노드의 왼쪽 노드가 없을 경우 후임자를 오른쪽 노드로 설정
            successor = removeTarget.getRight();
            //이때 오른쪽도 없을 경우 후임자는 null이 될 것이다.
        }
        
        return successor;
    }


    //전위 순회 preOrder : Root -> Left -> Right
    public void preOrder(TreeNode<E> node) {
        if(node != null) {
            System.out.print(node.getData()+" ");
            if(node.getLeft() != null){
                preOrder(node.getLeft());
            }
            if(node.getRight() != null){
                preOrder(node.getRight());
            }
        }
    }

    //중위 순회 Inorder : Left -> Root -> Right
    public void inOrder(TreeNode<E> node) {
        if(node != null) {
            if(node.getLeft() != null){
                inOrder(node.getLeft());
            }
            System.out.print(node.getData()+" ");
            if(node.getRight() != null){
                inOrder(node.getRight());
            }
        }
    }

    //후위 순회 : Right -> Root -> Left
    public void postOrder(TreeNode<E> node){
        if(node != null){
            if(node.getLeft() != null){
                postOrder(node.getLeft());
            }
            if(node.getRight() != null){
                postOrder(node.getRight());
            }
            System.out.print(node.getData()+" ");
        }
    }

    public TreeNode<E> searchNode(E e,boolean p){
        TreeNode<E> searchNode = root;
        TreeNode<E> searchNodeParent = null;
        while (searchNode != null) {

            if (searchNode.compareTo(e) == 0) {
                return p ? searchNode : searchNodeParent;
            }

            searchNodeParent = searchNode;

            if (searchNode.compareTo(e) < 0) {
                searchNode = searchNode.getLeft();
            } else {
                searchNode = searchNode.getRight();
            }
        }
        return null;
    }

    public TreeNode<E> getRoot(){
        return root;
    }

    public int getSize(){
        return size;
    }
}
