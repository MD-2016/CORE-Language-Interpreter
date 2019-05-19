Steps on running Parser

1. Run Makefile to compile all ten files (check Notes on Parser) to a .class file by using command 'make' (no quotes).

2. Once all .java files are .class, you can begin running the Parser.

3. From a console / terminal window use java (or java.exe) Parser "file" where file is the text file to parse

4. The Parser will either print a full core program with proper formatting or report the location of an error in the text file and terminate.

5. After running tests, use command 'make clean' to remove all .class files once you finish using the Parser.

Note: Must be on a linux enviroment 
Note: Make sure the test files are in the same directory as the .class and .java file, otherwise you'll have to give the full path in double quotes for the test file or Parser.class (aka Parser) or possibly both in order to run. 