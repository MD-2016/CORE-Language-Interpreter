

/*
 * Title: ParseTree interface
 * Programmer: Jay
 * Date: 10/12/2018
 * Filename: ParseTreeInterface.java
 * Purpose: The interface for the ParseTree 
 */
public interface ParseTreeInterface {

	/**
	 * Gets the Alt aka choice following the CORE Language
	 * @return integer that is the chosen path
	 */
	int getAlt();
	
	/**
	 * Gets the Node Type based on the rules of the CORE language
	 * @return enum of type NodeType associated with the current node in the tree
	 */
	 ParseTreeInterface.nodeType getNodeType();
	
	/**
	 * Gets the number of children from the current parent (could also be root)
	 * @return integer count of the number of children
	 */
	int getChildCount();
	
	/**
	 * Checks if the current node in the tree has a parent
	 * @return if a parent exists
	 */
	boolean hasParent();
	
	/**
	 * Moves to the parent node from the current node (if it is not the root node)
	 */
	void moveToParent();
	
	/**
	 * Moves to the Child based on the given index (if there is a child)
	 */
	void moveToChild(int index);
	
	/**
	 * Gets the ID string of the ID nodes
	 * @return String of the ID node
	 */
	String getIdString();
	
	/**
	 * Sets the ID string of the ID Node
	 */
	void setIdString(String newIdString);
	
	/**
	 * Sets the ID value of the current ID node based on the given value
	 */
	void setIdValue(int val);
	
	/**
	 * Returns the ID value of the ID node
	 * @return integer value assigned to the current ID Node
	 */
	int getIdValue();
	
	/**
	 * Sets the path based on which Alt is chosen
	 *  
	 */
	void setAlt(int alternative);
	
	/**
	 * Adds a new node to the Parse tree
	 */
	void addChild();
	
	/**
	 * Checks if the ID node is declared
	 * @return true if declared, and false otherwise.
	 */
	boolean isDeclared(String idNodeName);
	
	/**
	 * Checks if the ID Node is initialized
	 * @return true if initialized, and false otherwise.
	 */
	boolean isInitialized(String idNodeName);
	
	/**
	 * Enums used for identifying nodes
	 */
	public static enum nodeType
	{
		Prog, DS, SS, Decl, IDList, Stmt, Assign, If, Loop, In, Out, Cond, Comp, Exp, Term, Fac, CompOp, ID, INT;
		private nodeType(){}
	}

	/**
	 * Sets the Node type for the current node 
	 */
	void setNodeType(nodeType type);
	
}
