all: compile

compile:
	java -jar ./jtb132di.jar minijava.jj
	java -jar ./javacc5.jar minijava-jtb.jj

clean:
	rm -f *.class *~
