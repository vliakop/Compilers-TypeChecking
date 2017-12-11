import java.io.*;
import java.util.*;
import visitor.*;
import syntaxtree.*;
import tcpackage.*;

public class TypeCheck{
	
	public static void main(String[] args){	
		String s1 = "someMethod";
		String s2 = "someMethod";
		System.out.println(s1.equals(s2));
		try{
			MiniJavaParser parser = new MiniJavaParser(new FileInputStream("SomeTest.java"));
			Node root = parser.Goal();
			BuildVisitor bv = new BuildVisitor();
			root.accept(bv);
			bv.print();
		} catch (ParseException pe){
			System.out.println(pe);
		} catch (FileNotFoundException fe){
			System.out.println(fe);
		}
	}	
	
}
