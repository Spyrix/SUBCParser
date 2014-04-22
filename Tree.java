import java.util.*;

public class Tree {
	private Node root;

	public Tree(Node root){
		this.root=root;
	}
	public Tree(){
		this.root=null;
	}
	public Node getRoot(){
		return root;
	}

	public void traverseTree(){
		root.traverseSubTree(0);
	}
}