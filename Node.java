import java.util.*;
/*
William Frazee
#1355-5441
This class is responsible for representing the nodes in the tree
*/
public class Node{
	private ArrayList<Node> children;
	private Node parent;
	private String text;

	public Node(Node parent, String text){
		this.parent = parent;
		this.text = text;
		children = new ArrayList<Node>();
	}

	public Node(String text){
		this.parent = null;
		this.text = text;
		children = new ArrayList<Node>();
	}

	public Node(Node parent){
		this.parent = parent;
		this.text = "No text";
		children = new ArrayList<Node>();
	}

	public Node(){
		children = new ArrayList<Node>();
	}

	public Node getParent(){
		return parent;
	}
	public void setParent(Node parent){
		this.parent = parent;
	}

	public ArrayList<Node> getChildren(){
		return children;
	}

	public Node getChildAtIndex(int i){
		return children.get(i);
	}

	public int getIndexOfChild(Node child){
		for(int i = 0; i<children.size(); i++){
			if(children.get(i)==child)
				return i;
		}
		return -1;//child is not in the list
	}

	public int getNumChildren(){
		return children.size();
	}

	public boolean isLeaf(){
		if(children.size()==0)
			return true;
		else
			return false;
	}

	public void addChild(Node child){
		child.setParent(this);
		children.add(child);
	}

	public void addSubTree(Tree subTree){
		//this is one of the important methods. it appends the subtree to the leftmost position of the arraylist
		subTree.getRoot().setParent(this);
		children.add(0,subTree.getRoot());
	}
	public void traverseSubTree(int level){
		//this method travels through the tree. The int level tells us which level the node is on so that we know how many dots to print out
		for(int i = 0; i < level; i++)
			System.out.print(". ");
		System.out.println(text+"("+children.size()+")");
		if(!isLeaf()){
			for(Node child : children)
				children.get(children.indexOf(child)).traverseSubTree(level+1);
		}
	}
}