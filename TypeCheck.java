import java.io.*;
import java.util.*;
import visitor.*;
import syntaxtree.*;
import tcpackage.*;

public class TypeCheck{
	
	public static void main(String[] args){	
		try{
			MiniJavaParser parser = new MiniJavaParser(new FileInputStream("oldSomeTest.java"));
			Node root = parser.Goal();
			BuildVisitor bv = new BuildVisitor();
			root.accept(bv);
			bv.print();
			bv.symbolTable_.subclassChecks();
			CheckerVisitor typeChecker = new CheckerVisitor(bv.symbolTable_);
			root.accept(typeChecker, null);
		} catch (ParseException pe){
			System.out.println(pe);
		} catch (FileNotFoundException fe){
			System.out.println(fe);
		}
	}	
	
}
