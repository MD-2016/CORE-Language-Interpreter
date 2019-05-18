FLAGS = -g
JC = javac
.SUFFIXES:	.java	.class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = TokenizerInterface.java \
					Tokenizer.java \
					Token.java \
					ParseTreeInterface.java \
					ParseTree.java \
					InterpreterException.java \
					IDUndeclared.java \
					IDRedeclared.java \
					UnexpectedToken.java \
					InvalidToken.java \
					Parser.java       \
					ExecuteCoreProgram.java


default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
