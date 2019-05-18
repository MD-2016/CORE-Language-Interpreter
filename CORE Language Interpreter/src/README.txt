Instructions on running the Execution for the Core Interpreter

1. Run the Makefile in the terminal (on linux machine) by using the command make (make sure you're in the directory CORE_Language_Interpreter).

2. After running make, use the command java ExecuteCoreProgram.java "insert file here" where the insert file here means put the correct file path for the file that contains the core code.

3. If read statements exist in the given core file, the user will need to input an integer (can be positive or negative). The ID before the "=?" will be assigned the integer entered by the user. If write statements are in the core program, the ID's will be displayed (terminal window, Java IDE console, etc) on the screen.

4. The execution will evaluate the core program and do calculations and do Step 3.

5. After the results are displayed on the screen, the program has ended.

6. Once you are finished running the core programs, use make clean to remove the .class files

Note: Must have JDK 8, Java SE 8 to run ExecuteCoreProgram since it uses methods from the Java 8 libraries.