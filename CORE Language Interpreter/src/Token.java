/*
 * Token class used to assign tokens a token type and keep track of token from file
 */
public class Token {

	//Name of the token, the line the token is one, and token's type.
	private String name;
	private int line;
	private TokenizerInterface.Type type;
	
	//Constructor for assigning the token a name and a position (line number)
	public Token(String newName, int newLine)
	{
		this.name = newName;
		this.line = newLine;
	}
	
	//Grabs the name of the token and returns it
	public String getTokenName()
	{
		String currentName = name;
		return currentName;
	}
	
	//Sets the passed name as the name of the token
	public void setTokenName(String newName)
	{
		name = newName;
	}
	
	//Grabs the position where the current token was found from file
	public int getCurrentLine()
	{
		int currentLineNum = line;
		return currentLineNum;
	}
	
	//Sets the line of the current token to be what is passed in
	public void setCurrentLine(int newLine)
	{
		line = newLine;
	}
	
	//sets the token to be the type passed in
	public void setType(TokenizerInterface.Type newType)
	{
		type = newType;
	}
	
	//Returns the type of the current token (CORE keyword, symbols, ID, etc).
	public TokenizerInterface.Type getType()
	{
		TokenizerInterface.Type current = type;
		return current;
	}
}
