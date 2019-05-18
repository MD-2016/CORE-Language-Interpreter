import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseTree implements ParseTreeInterface { 
	
	//private class called node which is uded for creating the node of the forming parse tree
	private static class Node<T>
	{
		/*alt is path of tree, nodetype is the type of node, nodes children,
		* nodes parent, the id value if it's id node, and id name 
		*/
		int alt;
		ParseTreeInterface.nodeType nodeType;
		List<Integer> childrenNodes;
		int parentNode;
		int idValue;
		String idName;
		
		//private constructor for assigning a default path, and empty set of children
		private Node()
		{
			alt = 1;
			childrenNodes = new ArrayList<Integer>();
		}
	}
	
	//index is cursor for the list, nodelist is the parse tree, and symbol table is for ID's
	public int index;
	public List<Node> nodeList;
	public Map<String, Integer> symbolTable;
	
	//Constructor that creates the table, creates list, adds default node, and sets index to 0
	public ParseTree()
	{
		index = 0;
		nodeList = new ArrayList<Node>();
		nodeList.add(new Node());
		symbolTable = new HashMap();
	}
	
	//returns the size of the parse tree based on the count of the nodes
	public int treeSize()
	{
		int size = nodeList.size();
		return size;
	}
	
	//Gets the alt to determine the path the tree takes based on core language
	@Override
	public int getAlt() {
		int currentAlt = nodeList.get(index).alt;
		return currentAlt;
	}
	
	//sets the alt for the current node
	@Override
	public void setAlt(int newAlt)
	{
		nodeList.get(index).alt = newAlt;
	}
	
	//gets the value assigned to the ID node, or gets it from the table
	@Override
	public int getIdValue()
	{		
		int val = 0;
		
		if(getNodeType() == ParseTreeInterface.nodeType.valueOf("INT"))
		{
			val = nodeList.get(index).idValue;
		}
		else
		{
			String getID = getIdString();
			val = (Integer) symbolTable.get(getID).intValue();
		}
		
		return val;
	}
	
	//sets the ID node with the assigned value, or updates the table
	@Override
	public void setIdValue(int newVal)
	{
		 if(getNodeType() == ParseTreeInterface.nodeType.valueOf("INT"))
		 {
			 nodeList.get(index).idValue = newVal;
		 }
		 else
		 {
			 //add to table
			 String id = getIdString();
			 symbolTable.put(id, newVal);
		 }
	}
	
	//gets the type of the current node (decl, ss, ds, etc).
	@Override
	public ParseTreeInterface.nodeType getNodeType()
	{
		ParseTreeInterface.nodeType current = nodeList.get(index).nodeType;
		return current;
	}
	
	//Sets the type for the node
	@Override
	public void setNodeType(ParseTreeInterface.nodeType type)
	{
		nodeList.get(index).nodeType = type;
	}
	
	//gets the name of the ID node
	@Override
	public String getIdString()
	{
		String current = nodeList.get(index).idName;
		return current;
	}
	
	//Moves the cursor to the child node of the current parent if the node has children
	@Override 
	public void moveToChild(int childPosition)
	{
		index = (Integer) nodeList.get(index).childrenNodes.get(childPosition);
	}
	
	//Moves the cursor to the parent node of the current node
	@Override
	public void moveToParent()
	{
		index = nodeList.get(index).parentNode;
	}
	
	//gets the count of children the current node has (if any exist)
	@Override
	public int getChildCount()
	{
		int count = nodeList.get(index).childrenNodes.size();
		return count;
	}
	
	//checks if the ID node is declared
	@Override
	public boolean isDeclared(String currentIdDeclared)
	{
		boolean declared = symbolTable.containsKey(currentIdDeclared);
		return declared;
	}
	
	//checks if the ID node is initialized
	@Override
	public boolean isInitialized(String currentId)
	{
		boolean initialized = symbolTable.containsKey(currentId);
		return initialized;
	}
	
	//checks if the current node has a parent
	@Override
	public boolean hasParent()
	{
		boolean isThereAParent = nodeList.get(index).parentNode >= 0;
		return isThereAParent;
	}

	/*creates a new node, gives the node a parent,
	 *assigns parent node the new child, then adds child to the tree.
	 */
	@Override
	public void addChild() {
		Node newChild = new Node();
		newChild.parentNode = index;
		int newChildIndex = nodeList.size();
		nodeList.get(index).childrenNodes.add(newChildIndex);
		
		nodeList.add(newChild);
	}
	
	/*assigns the ID node the passed in name, and checks if it already exists in table.
	 *if not, it is added to the table
	 * */
	@Override 
	public void setIdString(String newIdString)
	{
		nodeList.get(index).idName = newIdString;
		
		if(!symbolTable.containsKey(newIdString))
		{
			symbolTable.put(newIdString, 0);
		}
	}

}