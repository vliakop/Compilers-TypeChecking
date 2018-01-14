all: compile

compile:
	java -jar ./jtb132di.jar minijava.jj
	java -jar ./javacc5.jar minijava-jtb.jj
	javac ./tcpackage/*.java
	javac ./cgeneration/*.java
	javac ./Main.java

clean:
	rm -f *.class *~ ./tcpackage/*.class
