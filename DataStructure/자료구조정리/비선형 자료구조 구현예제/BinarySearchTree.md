# 이진 탐색 트리 구현 예제

## 이진 탐색 트리란?
우선 이진 탐색 트리는 이진 탐색 알고리즘과 연결리스트를 합친 구조이다.  

이진 탐색 트리는 특정 배열이 정렬되있을 경우 100개의 크기를 가진 배열이 있다고 가정하자.  
우리가 해당 인덱스의 값을 모른다고 가정할 때 특정 값을 찾기위해 0부터 100까지 반복해서 찾을 수도 있지만, `업다운 게임`처럼 처음에 배열의 중간 인덱스를 조회하여 찾고자 하는 값보다 작을 경우 1~50 사이인 25를 검사하고 또 해당 인덱스의 값보다 크거나 작을경우 반을 계속 나눠서 탐색하는 알고리즘입니다.  

이를 연결리스트를 이용하여 트리구조로 구현한게 이진 탐색 트리입니다.  
이진 탐색트리는 아래와 같은 특징을 가집니다.  

### 이진 탐색 트리의 특징

모든 노드의 자식의 개수가 2개 이하이다.    
왼쪽 자식은 부모 노드보다 작고, 오른쪽 자식은 큰값을 가진다.    

하지만 데이터 삽입 시 불균형이 있을 수 있다.  
`BF가 맞지않다`는 의미로 BF는 `Balance Factor`로 루트기준 왼쪽 서브트리의 높이와 오른쪽 서브트리의 높이가 맞지 않는다는 의미이다.  

> 해당 불균형을 보완한 트리가 균형 이진 탐색트리이다.  
그 종류로는 AVL,Red Black Tree,B Tree, B+ Tree 등이 있다.  

### 이진 탐색 트리 사진  

![image](https://github.com/9ony/9ony/assets/97019540/4fe58506-51f1-42de-9b38-35cac49c384a)

이렇게 위 사진처럼 만약 8이란 숫자를 검색한다면 
=> 루트부터 시작해서 7보다 크니까 오른쪽 -> 11보다 작으니 왼쪽 -> 9보다 작으니 왼쪽 -> 8 검색  

### 이진 탐색 트리 동작과정  

- 추가

    이진 탐색 트리에 데이터를 추가하는 과정을 그림으로 표현 해보았습니다. 

    ![image](https://github.com/9ony/9ony/assets/97019540/ea1be3af-fd0c-4647-8b2d-084a234716d0)

    우선 처음 5라는 데이터를 추가할 때 root가 null이기 때문에 최상위 노드의 데이터는 5로 설정됩니다.  
    그 이후 2라는 데이터를 추가하면 root부터 시작하여 비교하게 되는데, 이 때 2는 비교대상인 root 보다 데이터가 작기 때문에 왼쪽 링크에 위치하게 됩니다.   

    ![image](https://github.com/9ony/9ony/assets/97019540/7d8bf916-457b-4f68-b3a7-0b3b78c8b2b8)
    
    그 다음 1이라는 데이터가 추가되면 루트부터 시작해서 5보다 작으므로 왼쪽, 왼쪽 노드에 2가 있으므로 2와 비교하여 1이 더 작으므로 왼쪽에 위치하게 됩니다.  

    ![image](https://github.com/9ony/9ony/assets/97019540/ab42f388-5a5a-4265-a69f-7d44433d91b1)
    
    데이터 3도 마찬가지로 루트비교후 왼쪽으로 가고 2를 비교하여 3이 더크니 오른쪽에 위치하게 됩니다.  



- 삭제

    그 다음은 데이터를 삭제하는 과정입니다.  
    이 경우가 구현 시 제일 복잡한 과정을 거치게 됩니다.  

    - __단말 노드일 경우__  
        우선 단말노드 삭제 시 어떤 과정을 거치는지 그림으로 확인해 보겠습니다.   
        
        ![image](https://github.com/9ony/9ony/assets/97019540/a0922dd3-ff68-4379-ba92-001f39e76194)

        단말 노드일 경우는 해당 삭제노드의 부모가 해당위치의 링크(left or right)를 null로 설정해주면 됩니다.  

    - __자식 노드가 1개일 경우__   

        삭제 대상의 자식이 1개일 경우 입니다.  

        ![image](https://github.com/9ony/9ony/assets/97019540/18bc0151-9b61-418c-ae58-314013312954)

        자식 노드가 1개일 경우 해당 삭제될 위치의 노드에 자식 노드로 교체합니다.  
        이때 해당 노드 자체를 옮길수도 있고, 데이터만 자식노드 데이터로 바꿔도 됩니다.  
        하지만 데이터만 옮기는 경우 자식노드에 더 하위 레벨의 노드가 있다면 연결작업을 해주어야 합니다.  
        데이터만 옮기는 경우 연결정보가 기존 삭제될 노드의 연결 정보를 가지고 있기 때문입니다.  

    - __자식 노드가 2개일 경우__  

        자식 노드가 2개일 경우 후임자 or 선임자를 찾아서 해당 삭제될 위치의 노드로 옮겨주는 동작을 하게 됩니다.  

        ![image](https://github.com/9ony/9ony/assets/97019540/c5bd2be7-b802-43db-9bc3-b401e1dbe217)

        위 그림처럼 22의 데이터를 삭제한다고 가정해보겠습니다.  
        그러면 해당 노드의 대체노드는 해당 노드의 데이터 값보다 그 다음으로 작거나 커야합니다.  
        
        \- successor : 삭제될 노드의 후임자 (작은값) `20`  
        \- predecessor 삭제될 노드의 선임자 (큰값) `23`  

        ![image](https://github.com/9ony/9ony/assets/97019540/90ae319a-2f7d-4ac1-a19a-dcab9457ed33)

        만약 위 그림처럼 대체할 노드의 자식이 있다고 가정해보겠습니다.  
        그러면 `후임자를 대체노드로 설정`할 경우 `삭제될 노드의 왼쪽 자식`이 `첫번째 후임자 후보`가 됩니다.  
        그리고 해당 첫번째 후임자 후보의 오른쪽 노드를 계속해서 탐색하게 됩니다.  
        이렇게 될 경우 위사진처럼 왼쪽 자식노드가 있을 가능성이 있습니다.  
        그럴경우 해당 후임자의 부모노드에 후임자 왼쪽자식 노드를 연결해 주는 추가 작업을 해주어야 합니다.  
        그리고 찾는 후임자 노드를 삭제될 노드로 대체해줍니다.  
        
        > 이때 후임자 데이터를 삭제될 노드에 설정하면 기존 연결정보를 교체하지 않아도 되지만, 노드자체를 변경할 경우 기존 삭제노드의 연결정보를 후임자 노드에 설정해주어야 합니다.  


## 이진 탐색 트리 구현 코드

해당 코드는 삭제 시 노드 자체를 변경해주는 방식으로 코드를 작성하였고, 원할한 흐름분석을 위해 재귀함수를 사용하지 않았습니다.  

- __Binary Search Tree ADT__
    
    \- add(E e) : 노드를 추가  
    
    \- remove(E e) : 노드 삭제  
    
    \- searchNode(E e,boolean b) : 노드 탐색  
    > true 넣을 경우 해당 값 반환, false 일 경우 해당 값의 부모노드 반환
    
    \- getSuccessor(TreeNode<E> removeTarget) : 삭제 대상의 후보자를 찾는 메서드로 대체할 노드를 반환  

    \- preOrder : 전위 순회 및 출력  
    
    \- inOrder : 중위 순회 및 출력  
    
    \- postOrder : 후위 순회 및 출력  



### 이진 탐색 트리 전체 코드
```java
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

    public TreeNode<E> getRoot(){
        return root;
    }


    public TreeNode<E> searchNode(E e,boolean p){
        TreeNode<E> searchNode = root;
        TreeNode<E> searchNodeParent = null;
        while (searchNode != null) {
            //boolean이 false일 경우 검색한 노드가 있으면 해당 노드의 부모를 반환
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

    public int getSize(){
        return size;
    }
}

```

### 정리

이진탐색트리도 자식을 최대 2개가지는 트리구조이다.  
노드의 왼쪽자식은 해당 노드보다 반드시 작아야 한다.  
반대로 오른쪽 자식은 해당 노드보다 큰값을 가진다.   

값이 똑같은 중복되는 키를 가진 노드를 허용하지 않아야 된다.  
> 만약 허용하게 된다면 트리의 높이가 지나치게 커짐으로써 비효율적이게 되며, 만약 허용할 경우 노드의 필드에 카운트를 추가하여 카운팅을 해주는 방식으로 설계하자.  


이진탐색트리의 시간복잡도는 O(트리의 높이) 이다.  
> 루트노드의 데이터와 찾는 데이터 비교 후 루트데이터보다 작다면 왼쪽자식 노드 방문, 크다면 오른쪽 자식노드 방문을 하면서 마지막 노드까지 탐색하기 때문

해당 이진트리의 단점으로는 루트노드 기준으로 한쪽으로 편향될 가능성이 있다.  
> 만약 루트노드의 데이터가 숫자 10이라고 가정하면 이후 추가되는 수가 9,8,7...이라 하자.
그러면 만약 1을 검색하게 된다면 해당 이진탐색트리는 높이가 10이기 때문에 최악의 경우 O(N)의 시간복잡도를 가지게 된다.  

해당 단점을 보완한 균형이진트리인 AVL Tree, Red-Black Tree등이 있다.  

