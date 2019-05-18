import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

	//creates tokenizer, indent for printing, id and digit checks for meeting core language
	public static Tokenizer t;
	private static int indent = 0;
	private static String idCheck = "[A-Z][A-Z]*[0-9]*";
	private static String digitCheck = "[0-9][0-9]*";

	//parse method that parsing the file passed in and builds the parse tree
	public static ParseTree parse(String file) throws InterpreterException
	{
		t = new Tokenizer();
		t.tokenize(file);
		ParseTree tree = new ParseTree();
		parseProgram(tree);
		matchConsume("EOF");
		return tree;
	}

	//prints the parse tree with proper formatting
	public static void printTree(ParseTree pt)
	{
		printProgram(pt);
	}

	//parses the program node which includes program, begin, and end
	public static void parseProgram(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.Prog);
		matchConsume("program");
		pt.addChild();
		pt.moveToChild(0);
		parseDS(pt);
		pt.moveToParent();

		matchConsume("begin");
		pt.addChild();
		pt.moveToChild(1);
		parseSS(pt);
		pt.moveToParent();

		matchConsume("end");
	}

	//parses the declaration sequence following rules of core grammar
	public static void parseDS(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.DS);
		pt.addChild();
		pt.moveToChild(0);
		parseDecl(pt);
		pt.moveToParent();
		if(t.currentToken().equals("int"))
		{
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(1);
			parseDS(pt);
			pt.moveToParent();
		}
	}

	//parses the statement sequence following rules of core grammar
	public static void parseSS(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.SS);
		pt.addChild();
		pt.moveToChild(0);
		parseStmt(pt);
		pt.moveToParent();

		String currentToken = t.currentToken();

		if((currentToken.equals("end")) || (currentToken.equals("else")))
		{
			pt.setAlt(1);
		}
		else
		{
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(1);
			parseSS(pt);
			pt.moveToParent();
		}
	}
	//parses the declaration node following rules of core grammar
	public static void parseDecl(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.Decl);
		matchConsume("int");
		pt.addChild();
		pt.moveToChild(0);
		parseIDList(pt, true);
		pt.moveToParent();
		matchConsume(";");
	}

	//parses the ID list node following rules of core grammar
	public static void parseIDList(ParseTree pt, boolean inTable)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.IDList);
		pt.addChild();
		pt.moveToChild(0);
		parseID(pt, inTable);
		pt.moveToParent();

		String currentToken = t.currentToken();
		if(currentToken.equals(","))
		{
			matchConsume(",");
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(1);
			parseIDList(pt, inTable);
			pt.moveToParent();
		}
		else
		{
			pt.setAlt(1);
		}
	}

	//parses the statement node following rules of core grammar
	public static void parseStmt(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.Stmt);
		String currentToken = t.currentToken();

		pt.addChild();
		pt.moveToChild(0);

		//checks for the being if statement, assignment, while loop, or I/O operations, or returns error
		if(currentToken.matches(idCheck))
		{
			parseAssign(pt);
			pt.moveToParent();
			pt.setAlt(1);
		}
		else if(currentToken.equals("if"))
		{
			parseIf(pt);
			pt.moveToParent();
			pt.setAlt(2);
		}
		else if(currentToken.equals("while"))
		{
			parseLoop(pt);
			pt.moveToParent();
			pt.setAlt(3);
		}
		else if(currentToken.equals("read"))
		{
			parseIn(pt);
			pt.moveToParent();
			pt.setAlt(4);
		}
		else if(currentToken.equals("write"))
		{
			parseOut(pt);
			pt.moveToParent();
			pt.setAlt(5);
		}
		else
		{
			throw new UnexpectedToken("[Line " + t.currentLine() + "] token " + currentToken + " found while parsing stmt");
		}
	}

	//parses the Assign node following core grammar rules
	public static void parseAssign(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.Assign);
		pt.addChild();
		pt.moveToChild(0);
		parseID(pt, false);
		pt.moveToParent();

		matchConsume("=");

		pt.addChild();
		pt.moveToChild(1);
		parseExp(pt);
		pt.moveToParent();

		matchConsume(";");
	}

	//parses the if node following core grammar rules
	public static void parseIf(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.If);
		pt.setAlt(1);

		matchConsume("if");
		pt.addChild();
		pt.moveToChild(0);
		parseCond(pt);
		pt.moveToParent();

		matchConsume("then");
		pt.addChild();
		pt.moveToChild(1);
		parseSS(pt);
		pt.moveToParent();

		//found this to be an if/else condition, now parsing else
		if(t.currentToken().equals("else"))
		{
			matchConsume("else");
			pt.addChild();
			pt.moveToChild(2);
			parseSS(pt);
			pt.moveToParent();
			pt.setAlt(2);
		}
		matchConsume("end");
		matchConsume(";");

	}

	//parses the while loop node following core language rules
	public static void parseLoop(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.Loop);
		matchConsume("while");
		pt.addChild();
		pt.moveToChild(0);
		parseCond(pt);
		pt.moveToParent();

		matchConsume("loop");
		pt.addChild();
		pt.moveToChild(1);
		parseSS(pt);
		pt.moveToParent();

		matchConsume("end");
		matchConsume(";");
	}

	//parses the input node following core grammar rules
	public static void parseIn(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.In);
		pt.setAlt(1);
		matchConsume("read");
		pt.addChild();
		pt.moveToChild(0);
		parseIDList(pt, false);
		pt.moveToParent();

		matchConsume(";");
	}

	//parses the output node following core grammar rules
	public static void parseOut(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.Out);
		pt.setAlt(1);
		matchConsume("write");
		pt.addChild();
		pt.moveToChild(0);
		parseIDList(pt, false);
		pt.moveToParent();

		matchConsume(";");
	}

	//parses the condition node following core grammar rules
	public static void parseCond(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.Cond);

		String currentToken = t.currentToken();
		if(currentToken.equals("!"))
		{
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(0);
			parseCond(pt);
			pt.moveToParent();
		}
		else if(currentToken.equals("["))
		{
			matchConsume("[");
			pt.addChild();
			pt.moveToChild(0);
			parseCond(pt);
			pt.moveToParent();

			String nextToken = t.currentToken();

			if(nextToken.equals("and"))
			{
				pt.setAlt(3);
				matchConsume("and");
			}
			else if(nextToken.equals("or"))
			{
				pt.setAlt(4);
				matchConsume("or");
			}

			pt.addChild();
			pt.moveToChild(1);
			parseCond(pt);
			pt.moveToParent();

			matchConsume("]");
		}
		else
		{
			pt.setAlt(1);
			pt.addChild();
			pt.moveToChild(0);
			parseComp(pt);
			pt.moveToParent();
		}
	}

	//parses the comparison node following core grammar rules
	public static void parseComp(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.Comp);
		matchConsume("(");
		pt.addChild();
		pt.moveToChild(0);
		parseFac(pt);
		pt.moveToParent();

		pt.addChild();
		pt.moveToChild(1);
		parseCompOp(pt);
		pt.moveToParent();

		pt.addChild();
		pt.moveToChild(2);
		parseFac(pt);
		pt.moveToParent();

		matchConsume(")");
	}

	//parses the expression node following core grammar rules
	public static void parseExp(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.Exp);
		pt.addChild();
		pt.moveToChild(0);
		parseTerm(pt);
		pt.moveToParent();

		String currentToken = t.currentToken();

		if(currentToken.equals("+"))
		{
			matchConsume("+");
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(1);
			parseExp(pt);
			pt.moveToParent();
		}
		else if(currentToken.equals("-"))
		{
			matchConsume("-");
			pt.setAlt(3);
			pt.addChild();
			pt.moveToChild(1);
			parseExp(pt);
			pt.moveToParent();
		}
		else
		{
			pt.setAlt(1);
		}
	}

	//parses the term node following core grammar rules
	public static void parseTerm(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.Term);
		pt.addChild();
		pt.moveToChild(0);
		parseFac(pt);
		pt.moveToParent();

		String currentToken = t.currentToken();

		if(currentToken.equals("*"))
		{
			matchConsume("*");
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(1);
			parseTerm(pt);
			pt.moveToParent();
		}
		else
		{
			pt.setAlt(1);
		}
	}

	//parses the factor node following core grammar rules, checks for illegal token
	public static void parseFac(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.Fac);

		String currentToken = t.currentToken();

		if(currentToken.matches(digitCheck))
		{
			pt.setAlt(1);
			pt.addChild();
			pt.moveToChild(0);
			parseInt(pt);
			pt.moveToParent();
		}
		else if(currentToken.matches(idCheck))
		{
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(0);
			parseID(pt, false);
			pt.moveToParent();
		}
		else if(currentToken.equals("("))
		{
			matchConsume("(");
			pt.setAlt(3);
			pt.addChild();
			pt.moveToChild(0);
			parseExp(pt);
			pt.moveToParent();
			matchConsume(")");
		}
		else
		{
			throw new UnexpectedToken("[Line: " + t.currentLine() + "] invalid token found while parsing fac: "  + currentToken);
		}
	}

	//parses the comparison operator node following core grammar rules
	public static void parseCompOp(ParseTree pt)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.CompOp);
		String currentToken = t.currentToken();
		if(currentToken.equals("!="))
		{
			pt.setAlt(1);
		}
		else if(currentToken.equals("=="))
		{
			pt.setAlt(2);
		}
		else if(currentToken.equals("<"))
		{
			pt.setAlt(3);
		}
		else if(currentToken.equals(">"))
		{
			pt.setAlt(4);
		}
		else if(currentToken.equals("<="))
		{
			pt.setAlt(5);
		}
		else if(currentToken.equals(">="))
		{
			pt.setAlt(6);
		}
		t.nextToken();
	}

	//parses the ID node and checks for ID being declared
	public static void parseID(ParseTree pt, boolean inTable)throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.ID);
		String currentToken = t.currentToken();
		t.nextToken();
		if((inTable) && (pt.isDeclared(currentToken)))
		{
			throw new IDRedeclared("[Line " + t.currentLine() + "] " + currentToken + " is already declared");
		}
		if((!inTable) && (!pt.isDeclared(currentToken)))
		{
			throw new IDUndeclared("[Line " + t.currentLine() + "] " + currentToken + " is not declared");
		}
		if(currentToken.matches(digitCheck))
		{
			throw new UnexpectedToken("[Line " + t.currentLine() + "] token " + currentToken + " found while parsing ID");
		}
		if(!currentToken.matches(idCheck))
		{
			throw new UnexpectedToken("[Line " + t.currentLine() + "] token " + currentToken + " found while parsing ID");
		}
		pt.setIdString(currentToken);
	}

	//parses the integer from the file to be a node in the tree
	public static void parseInt(ParseTree pt) throws InterpreterException
	{
		pt.setNodeType(ParseTreeInterface.nodeType.INT);
		pt.setAlt(1);
		String currentToken = t.currentToken();
		int currentTokenIntVal = Integer.parseInt(currentToken);
		pt.setIdValue(currentTokenIntVal);
		String currentInt = t.currentToken();
		matchConsume(currentInt);
	}

	//checks for expected tokens in core language, and reports error otherwise
	public static void matchConsume(String expected) throws InterpreterException
	{
		String currentToken = t.currentToken();
		if((!expected.equals("EOF")) && (!expected.equals(currentToken)))
		 {
				throw new UnexpectedToken("Syntax Error Found: [Line " + t.currentLine()+ "] Expected: " + expected + ", Found: " + currentToken);
		 }
		 if((expected.equals("EOF")) && (t.tokenAssignedNum(currentToken) != 33))
		 {
			 throw new UnexpectedToken("Syntax error found after keyword end: [Line " + t.currentLine() + "] Expected: " + expected + ", Found: " + currentToken);
		 }
		 t.nextToken();
	}

	//indents nodes while printing the parse tree
	public static void indent()
	{
		for(int i = 0; i < indent * 2; i++)
		{
			System.out.print(' ');
		}
	}

	//Printing section

	//prints the keyword or symbol of core language
	private static void printCoreKey(String coreLanguageKeyword)
	{
		System.out.print(coreLanguageKeyword);
	}

	//prints the program node from the parse tree
	public static void printProgram(ParseTree pt)
	{
		printCoreKey("program \n");
		indent++;
		pt.moveToChild(0);
		printDS(pt);
		pt.moveToParent();

		indent();
		printCoreKey("begin\n");
		indent++;
		pt.moveToChild(1);
		printSS(pt);
		pt.moveToParent();
		indent--;
		indent();
		printCoreKey("end\n");
	}

	//prints the declaration sequence node from the parse tree
	public static void printDS(ParseTree pt)
	{
		pt.moveToChild(0);
		printDecl(pt);
		pt.moveToParent();
		int alt = pt.getAlt();

		//more than one declaration
		if(alt == 2)
		{
			pt.moveToChild(1);
			printDS(pt);
			pt.moveToParent();
		}
	}

	//prints the statement sequence node from parse tree
	public static void printSS(ParseTree pt)
	{
		pt.moveToChild(0);
		printStmt(pt);
		pt.moveToParent();

		int alt = pt.getAlt();

		//more statements
		if(alt == 2)
		{
			pt.moveToChild(1);
			printSS(pt);
			pt.moveToParent();
		}
	}

	//prints the declaration node from parse tree
	public static void printDecl(ParseTree pt)
	{
		indent();
		printCoreKey("int ");
		pt.moveToChild(0);
		printIDList(pt);
		pt.moveToParent();
		printCoreKey(";\n");
	}

	//prints the id list node from parse tree
	public static void printIDList(ParseTree pt)
	{
		pt.moveToChild(0);
		printID(pt);
		pt.moveToParent();

		int alt = pt.getAlt();

		//more ID's
		if(alt == 2)
		{
			printCoreKey(", ");
			pt.moveToChild(1);
			printIDList(pt);
			pt.moveToParent();
		}
	}

	//prints the statement node from parse tree
	public static void printStmt(ParseTree pt)
	{
		indent();
		int alt = pt.getAlt();
		pt.moveToChild(0);

		//prints either assign, if, while loop, or I/O nodes
		if(alt == 1)
		{
			printAssign(pt);

		}
		else if(alt == 2)
		{
			printIf(pt);
		}
		else if(alt == 3)
		{
			printLoop(pt);
		}
		else if(alt == 4)
		{
			printIn(pt);
		}
		else if(alt == 5)
		{
			printOut(pt);
		}

		pt.moveToParent();
	}

	//prints the assign node from the parse tree
	public static void printAssign(ParseTree pt)
	{
		pt.moveToChild(0);
		printID(pt);
		pt.moveToParent();
		printCoreKey(" = ");
		pt.moveToChild(1);
		printExp(pt);
		pt.moveToParent();
		printCoreKey(";\n");
	}

	//prints the if node from the parse tree
	public static void printIf(ParseTree pt)
	{
		printCoreKey("if ");
		pt.moveToChild(0);
		printCond(pt);
		pt.moveToParent();
		printCoreKey(" then\n");
		indent++ ;
		pt.moveToChild(1);
		printSS(pt);
		pt.moveToParent();
		indent--;

		int alt = pt.getAlt();

		//an else node is encountered
		if(alt == 2)
		{
			indent();
			printCoreKey("else\n");
			indent++;
			pt.moveToChild(2);
			printSS(pt);
			pt.moveToParent();
			indent--;
		}

		indent();
		printCoreKey("end;\n");
	}

	//prints the while loop node from the parse tree
	public static void printLoop(ParseTree pt)
	{
		printCoreKey("while ");
		pt.moveToChild(0);
		printCond(pt);
		pt.moveToParent();
		printCoreKey(" loop\n");
		indent++;
		pt.moveToChild(1);
		printSS(pt);
		pt.moveToParent();
		indent--;
		indent();
		printCoreKey("end;\n");
	}

	//prints the input node from the parse tree
	public static void printIn(ParseTree pt)
	{
		printCoreKey("read ");
		pt.moveToChild(0);
		printIDList(pt);
		pt.moveToParent();
		printCoreKey(";\n");
	}

	//prints the output node from parse tree
	public static void printOut(ParseTree pt)
	{
		printCoreKey("write ");
		pt.moveToChild(0);
		printIDList(pt);
		pt.moveToParent();
		printCoreKey(";\n");
	}

	//prints the condition node from the parse tree
	public static void printCond(ParseTree pt)
	{
		int alt = pt.getAlt();

		//checks for comparison, not condition, and/or condition nodes
		if(alt == 1)
		{
			pt.moveToChild(0);
			printComp(pt);
			pt.moveToParent();
		}
		else if(alt == 2)
		{
			printCoreKey("!");
			pt.moveToChild(0);
			printCond(pt);
			pt.moveToParent();
		}
		else
		{
			printCoreKey("[ ");
			pt.moveToChild(0);
			printCond(pt);
			pt.moveToParent();

			if(alt == 3)
			{
				printCoreKey(" and ");
			}
			else if (alt == 4)
			{
				printCoreKey(" or ");
			}
			pt.moveToChild(1);
			printCond(pt);
			pt.moveToParent();
			printCoreKey(" ]");
		}
	}

	//prints the comparison node from the parse tree
	public static void printComp(ParseTree pt)
	{
		printCoreKey("( ");
		pt.moveToChild(0);
		printFac(pt);
		pt.moveToParent();
		pt.moveToChild(1);
		printCompOp(pt);
		pt.moveToParent();
		pt.moveToChild(2);
		printFac(pt);
		pt.moveToParent();

		printCoreKey(" )");
	}

	//prints the expression node from the parse tree
	public static void printExp(ParseTree pt)
	{
		pt.moveToChild(0);
		printTerm(pt);
		pt.moveToParent();

		int alt = pt.getAlt();

		//Encountered a + or - meaning another node
		if(alt > 1)
		{
			if(alt == 2)
			{
				printCoreKey(" + ");
			}
			else
			{
				printCoreKey(" - ");
			}

			pt.moveToChild(1);
			printExp(pt);
			pt.moveToParent();
		}
	}

	//prints the term node from the parse tree
	public static void printTerm(ParseTree pt)
	{
		pt.moveToChild(0);
		printFac(pt);
		pt.moveToParent();
		int alt = pt.getAlt();

		//found a multiplication operation indicating another node is present
		if(alt == 2)
		{
			printCoreKey(" * ");
			pt.moveToChild(1);
			printTerm(pt);
			pt.moveToParent();
		}
	}

	//prints the factor node from the parse tree
	public static void printFac(ParseTree pt)
	{
		int alt = pt.getAlt();

		pt.moveToChild(0);

		//found an integer, ID, or a expression
		if(alt == 1)
		{
			printInt(pt);
		}
		else if(alt == 2)
		{
			printID(pt);
		}
		else
		{
			printCoreKey("( ");
			printExp(pt);
			printCoreKey(" )");
		}

		pt.moveToParent();
	}

	//prints the compare operator node from parse tree
	public static void printCompOp(ParseTree pt)
	{
		List<String> compSymbols = new ArrayList<String>();
		compSymbols.add("");
		compSymbols.add(" != ");
		compSymbols.add(" == ");
		compSymbols.add(" < ");
		compSymbols.add(" > ");
		compSymbols.add(" <= ");
		compSymbols.add(" >= ");

		int alt = pt.getAlt();

		printCoreKey(compSymbols.get(alt));
	}

	//prints the ID node from the parse tree
	public static void printID(ParseTree pt)
	{
		String currentID = pt.getIdString();
		printCoreKey(currentID);
	}

	//prints the integer from the parse tree
	public static void printInt(ParseTree pt)
	{
		int currentInt = pt.getIdValue();
		System.out.print(currentInt);
	}

	//creates and calls tokenizer, creates parse tree to build, then prints tree
	public static void main(String[] args) throws InterpreterException
	{
		try
		{
			Parser programToParse = new Parser();
			ParseTree tree = new ParseTree();
			tree = programToParse.parse(args[0]);
			programToParse.printTree(tree);
		}
		catch(InterpreterException exceptionFound)
		{
			System.err.println(exceptionFound.getMessage());
			System.exit(1);
		}
	}
}
