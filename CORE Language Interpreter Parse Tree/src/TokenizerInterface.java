/*
 * Interface that shows methods used by the Tokenizer class.
 * Tokenizer implements this interface
 */
public interface TokenizerInterface {
	
	/**
	 * Returns the current token in the Tokenizer based on the position of the cursor
	 * @return the token at position x is return as String
	 */
	public String currentToken();
	
	/**
	 * Checks if the Tokenizer has another token in waiting, returns false otherwise
	 * @return true or false if another token exists
	 */
	public boolean hasNext();
	
	/**
	 * Reads the file in and processes the code into valid tokens for parsing by the Parser 
	 * @param String that represents the file path for the file to be tokenized.
	 */
	public void tokenize(String file);
	
	/**
	 * Grabs the assigned integer based on the given token. 
	 * @param token is a String that represents the current token being passed in
	 * @return the associated integer for the given token
	 */
	public int tokenAssignedNum(String token);
	
	/**
	 * Returns a string of tokens built by the tokenizer.
	 * @return A string of tokens from the token stream.
	 */
	public String getTokenStream();
	
	/**
	 * Returns the the position of the token from the file
	 * @return integer associated with the line position of the current token
	 */
	public int currentLine();
	
	/**
	 * Returns the type of token (keyword, int, id, etc) based on the enum used for the token
	 * @return the assigned enum of type Type which is the type of token for current token.
	 */
	public Type tokenType();
	
	public static enum Type
	{
		  num,  ID,  EOF;
		
		private Type(){}
	}
	
}
