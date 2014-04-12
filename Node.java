import java.util.*;

public class Node{
	private ArrayList<Node> children;
	private Node parent;

	public Node(Node parent){
		this.parent = parent;
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
		children.add(child);
	}
}