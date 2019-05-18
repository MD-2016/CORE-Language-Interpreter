import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Tokenizer class for reading in characters from a file for the CORE Language
public class Tokenizer implements TokenizerInterface {
	
	/* List of tokens that are legal to the Core language. 
	 * Stored in the list for further processing (printing integer assigned to the tokens)
	 */
	
	private static List<Integer> printTokens = new ArrayList<Integer>();
	
	
	/* Map of the legal core language reserves, symbols, ID, INT, and EOF. 
	 * This map is compared to the token stream and used for getting the integer to print to screen.
	 */
	public static Map<String,Integer> coreMap  = new HashMap<String,Integer>();
	
	/*List of whitespace characters which are \r, \t, and ' '. 
	 * Used to skip over those in the file while being read in
	 */
	private static List<Character> whiteSpace;
	
	//index used for the current token and next token methods
	private static int index;
	
	/*the current character is used to be read in from the file for processing. 
	 * This is how the token stream is built
	 */
	private static Character currentChar;
	
	/*the line number aka position of the given line while reading the file in. 
	 * This is used for reporting errors.
	 */
	public static int lineNum;
	
	//used with currentChar to build up the string to be sent to the token stream.
	private static StringBuilder tokenBuild;
	
	//regular expression used for checking digits to valid the integer type for CORE
	private static String digitCheck;
	
	//regular expression used for checking the proper ID of the CORE language.
	private static String idCheck;
	
	//The string made by String Builder to be placed in the token stream list.
	private static String token;
	
	//private static List<String> coreTokens;
	
	//Stream used to hold tokens of type token gathered from the file
	private static List<Token> tokenStream;
	
	
	//creates the tokenizer object and assigns values to the above primitive and object types
	Tokenizer()
	{
		
		tokenStream = new ArrayList<Token>();
		index = 0;
		currentChar = null;
		lineNum = 1;
		token = "";
		
		whiteSpace = new ArrayList<Character>();
		
		whiteSpace.add(Character.valueOf(' '));
		whiteSpace.add(Character.valueOf('\t'));
		whiteSpace.add(Character.valueOf('\r'));
		
		digitCheck = "[0-9][0-9]*";
		idCheck = "[A-Z][A-Z]*[0-9]*";
		mapFill();
	}
	
	//returns the assigned integer value for the token in question
	public int tokenAssignedNum(String token)
	{
		//checks first character and declares the integer return value
		char firstChar = token.charAt(0);
		int assignedNum;
		
		//checks for the case of integer
		if(token.equals(Character.isDigit(firstChar)))
		{
			assignedNum = 31;
		}
		
		//checks for the case of being an ID
		else if(token.equals(Character.isUpperCase(firstChar)))
		{
			assignedNum = 32;
		}
		
		//checks for the case of being at the end of file
		else if(token.equals("EOF"))
		{
			assignedNum = 33;
		}
		
		//checks the assigned integer value from the map
		else
		{
			assignedNum = coreMap.get(token);
		}
		
		return assignedNum;
	}
	
	//checks if there exists another token in the stream
	public boolean hasNext()
	{
		boolean anotherToken = index < this.tokenStream.size();
		return anotherToken;
	}
	
	/*populates the map with the reserved words, symbols, INT placeholder, ID placeholder, and 
	 * EOF placeholder. This map is used to compare against the token stream and get the 
	 * assigned integers */
	public static void mapFill()
	{
				//reserve words are added and assigned the proper ints
				coreMap.put("program", 1);
				coreMap.put("begin", 2);
				coreMap.put("end", 3);
				coreMap.put("int", 4);
				coreMap.put("if", 5);
				coreMap.put("then", 6);
				coreMap.put("else", 7);
				coreMap.put("while", 8);
				coreMap.put("loop", 9);
				coreMap.put("read", 10);
				coreMap.put("write", 11);
				coreMap.put("and", 12);
				coreMap.put("or", 13);
				
				//Special symbols and reserve words are added and assigned the proper ints
				coreMap.put(";", 14);
				coreMap.put(",", 15);
				coreMap.put("=", 16);
				coreMap.put("!", 17);
				coreMap.put("[", 18);
				coreMap.put("]", 19);
				coreMap.put("(", 20);
				coreMap.put(")", 21);
				coreMap.put("+", 22);
				coreMap.put("-", 23);
				coreMap.put("*", 24);
				coreMap.put("!=", 25);
				coreMap.put("==", 26);
				coreMap.put(">=", 27);
				coreMap.put("<=", 28);
				coreMap.put(">", 29);
				coreMap.put("<", 30);
				
	}
	
	
	/*private method used to grab the next character in the file. Converts the integer obtained from
	 * read() by the reader into the proper ascii character. 
	 * Checks for read errors while reading from the file. */
	private Character nextChar (BufferedReader reader)
	{
	    Character next = null;
	    try
	    {
	    	int asciiCode = reader.read();
	    	if(asciiCode != -1)
	    	{
	    		next = Character.valueOf((char) asciiCode);
	    	}
	    }
	    catch(IOException g)
	    {
	    	g.printStackTrace();
	    }
	    return next;
	}
	
	//grabs the current token from the token stream list.
	public String currentToken()
	{
		String grabbedToken = tokenStream.get(index).getTokenName();
		//.get(index);
		return grabbedToken;
	}
	
	//moves the index up in the token stream list.
	public void nextToken()
	{
		index++;
	}
	
	/*reads line by line character by character from the file, builds the string to compare to the
	* CORE language rules, if string passes, then it sends the string to the token stream.
	* The token stream is passed into a print method to print the integers of the valid tokens */
	public void tokenize(String file)
	{
		//checks if the file can be read
	   	try
	   	{
	   		BufferedReader coreFileReader = new BufferedReader(new FileReader(new File(file)));
	   		
	   		
	   		//read character by character and do comparisons for the correct CORE Rules
	   		currentChar = nextChar(coreFileReader);
	   		
	   		//While characters remain, read char by char.
	   		while(currentChar != null )
	   		{
	   			/*checks if the gathered characters form an integer that meets the CORE language rules. 
	   			 * Sending the resulting string to the token stream if it passes. */
	   		   if(Character.isDigit(currentChar.charValue()))
	   		   {
	   			   tokenBuild = new StringBuilder();
	   			   while(Character.isDigit(currentChar.charValue()) != false)
	   			   {
	   				   tokenBuild.append(currentChar);
	   				   currentChar = nextChar(coreFileReader);
	   				   
	   				   if(Character.isAlphabetic(currentChar))
	   				   {
	   					   errorFound("invalid numeric constant");
	   				   }
	   				   
	   				   
	   			   }
	   			   token = tokenBuild.toString();
	   			   if((!token.matches(digitCheck)) || (token.length() > 8))
	   			   {
	   				   errorFound("not a CORE Integer: Must be 8 digits in length and no other characters " + token);
	   			   }
	   			   tokenStream.add(new Token(token, lineNum));
	   		   }
	   		   
	   		   /*checks if the char is a candidate for ID. 
	   		    * If it passes, 
	   		    * 	builds the ID string, 
	   		    * 	and sends it to the token stream if it passes. 
	   		    */
	   		   else if(Character.isUpperCase(currentChar.charValue()))
	   		   {
	   			   tokenBuild = new StringBuilder();
	   			   while(Character.isLetterOrDigit(currentChar.charValue()) != false)
	   			   {
	   				   tokenBuild.append(currentChar);
	   				   currentChar = nextChar(coreFileReader);
	   				   
	   				   
	   			   }
	   			   token = tokenBuild.toString();
	   			   if((!token.matches(idCheck)) || token.length() > 8 )
	   			   {
	   				   errorFound("Invalid CORE Identifier: Must not be longer than 8 characters and start with uppercare character " + token);
	   				   
	   			   }
	   			   tokenStream.add(new Token(token, lineNum));
	   		   }
	   		   
	   		   /* checks the current char against whitespace characters. 
	   		    * Bypasses them in the file
	   		    */
	   		   else if(whiteSpace.contains(currentChar))
	   		   {
	   			   currentChar = nextChar(coreFileReader);
	   		   }
	   		   
	   		   /*checks if the char read in is the new line char. Reads in the next character 
	   		    * on the next line and increments the line count by 1.
	   		    */
	   		   else if(currentChar.charValue() == '\n')
	   		   {
	   			   currentChar = nextChar(coreFileReader);
	   			   lineNum += 1;
	   		   }
	   		   
	   		   /*the current char is a candidate for a reserved word in the CORE language, 
	   		    * Checks the built string against the coreMap to see if it is a valid reserve word.
	   		    * sends string made to the token stream */
	   		   else if(Character.isLowerCase(currentChar.charValue()))
	   		   {
	   			   tokenBuild = new StringBuilder();
	   			   while(currentChar != null && Character.isLetterOrDigit(currentChar.charValue()))
	   			   {
	   				   tokenBuild.append(currentChar);
	   				   currentChar = nextChar(coreFileReader);
	   				  
	   			   }
	   			   token = tokenBuild.toString();
	   			   if(!coreMap.containsKey(token))
	   			   {
	   				   errorFound("Invalid reserve word " + token);
	   			   }
	   			   tokenStream.add(new Token(token, lineNum));
	   		   }
	   		   
	   		   //checks the special case with the ! character. sends the result to the token stream 
	   		   else if(currentChar.charValue() == '!')
	   		   {
	   			   Character charAfter = nextChar(coreFileReader);
	   			   if(charAfter.charValue() == '=')
	   			   {
	   				   tokenStream.add(new Token("!=", lineNum));
	   				   currentChar = nextChar(coreFileReader);
	   			   }
	   			   else
	   			   {
	   				   tokenStream.add(new Token("!", lineNum));
	   				   currentChar = charAfter;
	   			   }
	   		   }
	   		   
	   		   //checks the special case with = character. sends result to the token stream
	   		   else if(currentChar.charValue() == '=')
	   		   {
	   			   Character charAfter = nextChar(coreFileReader);
	   			   if(charAfter.charValue() == '=')
	   			   {
	   				   tokenStream.add(new Token("==", lineNum));
	   				   currentChar = nextChar(coreFileReader);
	   			   }
	   			   else
	   			   {
	   				   tokenStream.add(new Token("=", lineNum));
	   				   currentChar = charAfter;
	   			   }
	   		   }
	   		   
	   		   //checks the special case with < character. sends result to the token stream
	   		   else if(currentChar.charValue() == '<')
	   		   {
	   			   Character charAfter = nextChar(coreFileReader);
	   			   if(charAfter.charValue() == '=')
	   			   {
	   				   tokenStream.add(new Token("<=", lineNum));
	   				   currentChar = nextChar(coreFileReader);
	   			   }
	   			   else
	   			   {
	   				   tokenStream.add(new Token("<", lineNum));
	   				   currentChar = charAfter;
	   			   }
	   		   }
	   		   
	   		   //checks the special case with > character. sends result to the token stream
	   		   else if(currentChar.charValue() == '>')
	   		   {
	   			   Character charAfter = nextChar(coreFileReader);
	   			   if(charAfter.charValue() == '=')
	   			   {
	   				   tokenStream.add(new Token(">=", lineNum));
	   				   currentChar = nextChar(coreFileReader);
	   			   }
	   			   else
	   			   {
	   				   tokenStream.add(new Token(">", lineNum));
	   				   currentChar = charAfter;
	   			   }
	   		   }
	   		   
	   		   
	   		   //checks for the other symbols in the core language. Reports an error otherwise
	   		   else 
	   		   {
	   			   token = "" + currentChar;
	   			   if(!coreMap.containsKey(token))
	   			   {
	   				   errorFound("Invalid symbols " + token);
	   			   }
	   			   tokenStream.add(new Token(token, lineNum));
	   			   currentChar = nextChar(coreFileReader);
	   		   }
	   		}
	   		
	   		//tries to close the buffered reader since the file is fully read
	   		try
	   		{
	   			coreFileReader.close();
	   		}
	   		
	   		//handles errors with buffered reader, program ends.
	   		catch(IOException h)
	   		{
	   			System.out.println("Can't close the buffered reader");
	   			System.out.println("\n");
	   			h.printStackTrace();
	   			System.exit(1);
	   		}
	   	}
	   	
	   	//handles the file not found error. Program ends.
	   	catch(FileNotFoundException f)
	   	{
	   		System.err.println("The file " + file + " is not found!");
	   		f.printStackTrace();
	   		System.exit(1);
	   	}
	   	
	   	//moves the token stream for printing to the screen.
	   	finally
	   	{
	   		//checkAndPrintValid();
	   		tokenStream.add(new Token("EOF", lineNum));
	   	}
	   	
	}
	
	private void printCodes(Tokenizer t)
	{
		while(t.hasNext())
		{
			String next = t.currentToken();
			int current = t.tokenAssignedNum(next);
			System.out.println(current);
			t.nextToken();
		}
	}
	
	/* reads in the token stream and begins checking each token against the CORE Map. 
	 * if it's in the map, the assigned integer is returned and stored in the list. 
	 * otherwise, it's a special case for integers, identifiers, and end of file */
	private void checkAndPrintValid(List<String> tokens)
	{
		//List<Integer> printTokens = new ArrayList<Integer>();
		int currentToken = 0;
		for(String token : tokens)
		{
			if(coreMap.containsKey(token))
			{
				currentToken = coreMap.get(token);
				printTokens.add(currentToken);
			}
			else
			{
				if(token.matches(digitCheck))
				{
					currentToken = 31;
					printTokens.add(currentToken);
				}
				if(token.matches(idCheck))
				{
					currentToken = 32;
					printTokens.add(currentToken);
				}
			}
		}
		
		//adds the integer for the end of file to the list
	    currentToken = 33;
		printTokens.add(currentToken);
		
	}
	
	//returns the position of the current token from the file.
	public int currentLine()
	{
		int current = tokenStream.get(index).getCurrentLine();
		return current;
	}
	
	
	/* prints the error found during the tokenize stage. 
	 * Prints the error message and line where the error occured. */
	public void errorFound(String message)
	{
		System.out.println("Error: [Line " + lineNum + "] " + message);
		System.exit(1);
	}
	
	//Returns a stream of tokens of type string
	public String getTokenStream()
	{
		String stream = tokenStream.toString();
		return stream;
	}
	
	//returns the type of token based on the current token
	public Type tokenType() {
		TokenizerInterface.Type current;
		current = tokenStream.get(index).getType();
		return current;
	}
}
