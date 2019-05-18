This project includes just the Tokenizer.java file. Below is the description of the program and instructions on running it.

The Tokenizer.java is the only file in that needs to complied. Tokenizer is the file which creates the tokenizer, parses in the text from the file, reads file character by character, builds valid tokens for CORE Language, and prints the assigned integers to each token to the console.

Instructions to run program...

Note: Be sure to unzip before testing.

1. Run the Makefile to compile the Tokenizer.java file to a .class file by using the command 'make' (no quotes). Must be using Linux / MacOS to use make command.

2. Once the the Tokenizer.java becomes Tokenizer.class, you can begin running the program.

3. From a console / terminal window, use java Tokenizer "insert file here"

	Where "insert file here" is the string of the file in quotes "test.core" being passed as a argument to Tokenizer.

4. The console / terminal will print a list of valid tokens (as integers) to the screen which passes the rules for the CORE Language.
   If an error is found, the Tokenizer will display the error and which line it is on, then the Tokenizer will terminate. Only run one file at a time.

5. After running the test cases as arguments into the Tokenizer from the console / terminal (see step 3), 
   use the command 'make clean' to remove the class files once you finish using the Tokenizer. 

Note: Make sure the test files are in the same directory as the .class and .java file, otherwise you'll have to give the full path in double quotes for the test file or Tokenizer.class (aka Tokenizer) or possibly both in order to run. 