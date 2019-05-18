import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecuteCoreProgram {

	//reads in the user input
    private static BufferedReader readIn = new BufferedReader(new InputStreamReader(System.in));

    //evaluates the factor part of the core grammar
    public static int evalFac(ParseTree pt) throws InterpreterException {
        int result = 0;
        int alt = pt.getAlt();
        pt.moveToChild(0);

       //if the alt is an integer or ID
       if( alt == 1 || alt == 2)
       {
    	   //grab the integer value
    	  if(alt == 1)
    	  {
    		  result = pt.getIdValue();
    	  }
    	  //grab the value of the ID
    	  else
    	  {
    		  //check if the ID is initialized, then get the value if it is initialized
    		  if(pt.isInitialized() == false)
    		  {
    			  throw new IDUsedBeforeAssigned("The ID " + pt.getIdString() + " is not initialized");
    		  }
    		  else
    		  {
    			  result = pt.getIdValue();
    		  }
    	  }
       }
       // must be an expression
       else
       {
    	   result = evalExp(pt);
       }

       //move to parent and return the result
        pt.moveToParent();
        return result;
    }

    //evaluates the term part of the core grammar
    public static int evalTerm(ParseTree pt) throws InterpreterException {

    	//get the alt, move to the child, evalaute the factor and get the result
        int alt = pt.getAlt();
        pt.moveToChild(0);
        int result = evalFac(pt);
        pt.moveToParent();

        //if we're doing a multiplication, move to child, calculate the result and check result can be represented
        if (alt == 2) {
            pt.moveToChild(1);
            try
            {
            	//result *= evalTerm(pt);
            	result = Math.multiplyExact(result, evalTerm(pt));
            }
            catch(ArithmeticException e)
            {
            	throw new OverflowException("The multiplication continuation on " + result + " is resulting in overflow or underflow meaning can't be represented");
            }

            //move back to the parent
            pt.moveToParent();

        }

        //result is returned after calculation
        return result;
    }

    //evaluates the expression part of the core grammar
    public static int evalExp(ParseTree pt) throws InterpreterException {

    	//get the alt, move to child, evalaute term, get result, move back
        int alt = pt.getAlt();
        pt.moveToChild(0);
        int result = evalTerm(pt);
        pt.moveToParent();

        //There's an addition happening, calculate and check if it can be represented
        if (alt == 2) {
            pt.moveToChild(1);
            try
            {
            	//result += evalExp(pt);
            	result = Math.addExact(result, evalExp(pt));
            }
            catch(ArithmeticException e)
            {
            	throw new OverflowException("The addition continuation on " + result + " is resulting in overflow or underflow meaning can't be represented");
            }

            //move back to parent
            pt.moveToParent();

          //this means subtraction is happening, calculate result and check if it can be represented
        } else if (alt == 3) {
            pt.moveToChild(1);
            try
            {
            	//result -=  evalExp(pt);
            	result = Math.subtractExact(result, evalExp(pt));
            }
            catch(ArithmeticException e)
            {
            	throw new OverflowException("The subtraction continuation on " + result + " is resulting in overflow or underflow meaning can't be represented");
            }

            //move back to parent
            pt.moveToParent();
        }

        //return the result
        return result;
    }

    //evaluates the compare part of the core grammar
    public static boolean evalComp(ParseTree pt) throws InterpreterException {

    	//get the two factors and the alt.
        pt.moveToChild(0);
        int fac1 = evalFac(pt);
        pt.moveToParent();
        pt.moveToChild(1);
        int alt = pt.getAlt();
        pt.moveToParent();
        pt.moveToChild(2);
        int fac2 = evalFac(pt);
        pt.moveToParent();
        boolean result = false;

        //Do the comparison based on the alt and assign to result
        switch(alt)
        {
        	case 1:
        		result = fac1 != fac2;
        		break;
        	case 2:
        		result = fac1 == fac2;
        		break;
        	case 3:
        		result = fac1 < fac2;
        		break;
        	case 4:
        		result = fac1 > fac2;
        		break;
        	case 5:
        		result = fac1 <= fac2;
        		break;
        	default:
        		result = fac1 >= fac2;
        		break;
        }

        //return the resulting boolean
        return result;
    }

    //evaluate the condition part of the core grammar
    public static boolean evalCond(ParseTree pt) throws InterpreterException {

    	//get the path, and create a boolean
    	boolean result = false;
        int alt = pt.getAlt();

        //We encountered a comparison not other conditions
        if (alt == 1) {
            pt.moveToChild(0);
            result = evalComp(pt);
            pt.moveToParent();

          //we encountered a !, and, or conditions
        } else {

        	//move to child, get the boolean result, check alt.
        	//if alt 2 then do !cond else do cond and cond, or cond or cond
            pt.moveToChild(0);
            boolean bool2 = evalCond(pt);
            pt.moveToParent();
            if (alt == 2) {
                result = !bool2;
            } else {
                pt.moveToChild(1);
                boolean bool3 = evalCond(pt);
                pt.moveToParent();
                if(alt == 3)
                {
                	result = bool2 && bool3;
                }
                else
                {
                	result = bool2 || bool3;
                }
            }
        }

        //return the result
        return result;
    }

    //used to read in the inputs given by the user when encountering read in a core program
    public static void inputIdList(ParseTree pt) {

    	//move to child, get the ID, then get the integer
        pt.moveToChild(0);
        String currentID = pt.getIdString();
        System.out.print(currentID + " =? ");
        Integer val = 0;

        String userInt = "";

        //check if the input is valid
		try {
			userInt = readIn.readLine();
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		//check if the integer is valid, else catch the error
        try
        {
        	val = Integer.parseInt(userInt);
        }
        catch(Exception e)
        {
        	System.out.println("User number is not a number or too large or too small " + userInt);
        	System.exit(1);
        }

        //set the new ID value, move to parent and check for more ID's
        pt.setIdValue(val);
        pt.moveToParent();
        if (pt.getAlt() == 2) {
            pt.moveToChild(1);
            inputIdList(pt);
            pt.moveToParent();
        }
    }

    //outputs the ID to console based on seeing the write command in the core program
    public static void outputIdList(ParseTree pt) {

    	//move to child, get the ID and its value
        pt.moveToChild(0);
        String currentID = pt.getIdString();
        int check = pt.getIdValue();

        //print the result, move to parent, and check for more ID's to output
        System.out.println(currentID + " = " + check);
        pt.moveToParent();
        if (pt.getAlt() == 2) {
            pt.moveToChild(1);
            outputIdList(pt);
            pt.moveToParent();
        }
    }

    //execute the read statement, read in the input from inputIdList
    public static void execIn(ParseTree pt) {
        pt.moveToChild(0);
        inputIdList(pt);
        pt.moveToParent();
    }

    //execute the write statement, write out the output with outputIdList
    public static void execOut(ParseTree pt) {
        pt.moveToChild(0);
        outputIdList(pt);
        pt.moveToParent();
    }

    //execute the assignment statement using evalExp and setting the result to the current ID
    public static void execAssign(ParseTree pt) throws InterpreterException {
        pt.moveToChild(1);
        int val = evalExp(pt);
        pt.moveToParent();
        pt.moveToChild(0);
        pt.setIdValue(val);
        pt.moveToParent();
    }

    //execute the if statement, eval the condition and execute the statements
    //or execute the else statements based on the condition result
    public static void execIf(ParseTree pt) throws InterpreterException {
        pt.moveToChild(0);
        boolean result = evalCond(pt);
        pt.moveToParent();
        if (result) {
            pt.moveToChild(1);
            execStmtSeq(pt);
            pt.moveToParent();
        } else if (pt.getAlt() == 2) {
            pt.moveToChild(2);
            execStmtSeq(pt);
            pt.moveToParent();
        }
    }

    //execute the while loop statement, run the loop until condition is met
    public static void execLoop(ParseTree pt) throws InterpreterException {
        pt.moveToChild(0);
        boolean result = evalCond(pt);
        pt.moveToParent();
        while (result) {
            pt.moveToChild(1);
            execStmtSeq(pt);
            pt.moveToParent();
            pt.moveToChild(0);
            result = evalCond(pt);
            pt.moveToParent();
        }
    }

    //execute statement. Based on the alt, go to the corresponding statement
    public static void execStmt(ParseTree pt) throws InterpreterException {
        int alt = pt.getAlt();
        pt.moveToChild(0);

        switch(alt)
        {
        	case 1:
        		execAssign(pt);
        		break;
        	case 2:
        		execIf(pt);
        		break;
        	case 3:
        		execLoop(pt);
        		break;
        	case 4:
        		execIn(pt);
        		break;
        	default:
        		execOut(pt);
        		break;
        }

        pt.moveToParent();
    }

    //execute the statement sequence. Move to executing the individual statement, then do more if they exist
    public static void execStmtSeq(ParseTree pt) throws InterpreterException {
        pt.moveToChild(0);
        execStmt(pt);
        pt.moveToParent();
        if (pt.getAlt() == 2) {
            pt.moveToChild(1);
            execStmtSeq(pt);
            pt.moveToParent();
        }
    }

    //execute the program statement, only look at executing statement sequence since parser handles declarations
    public static void execProg(ParseTree pt) throws InterpreterException {
        pt.moveToChild(1);
        execStmtSeq(pt);
        pt.moveToParent();
    }


    //creates and calls tokenizer, creates parse tree to build, then prints tree
	public static void main(String[] args) throws InterpreterException
	{
		//try to create parse tree, then execute it if possible otherwise print error
		try
		{
			ParseTree tree = new ParseTree();
			tree = Parser.parse(args [0]);

			execProg(tree);
		}
		catch(InterpreterException e)
		{
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
