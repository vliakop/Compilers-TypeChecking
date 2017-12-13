import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.*;
import java.util.*;
import visitor.*;
import syntaxtree.*;
import tcpackage.*;

public class Main{
	
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg + " type checking...");
			File file = new File(arg);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				MiniJavaParser parser = new MiniJavaParser(fis);
				Node root = parser.Goal();
				BuildVisitor bv = new BuildVisitor();
				root.accept(bv);
				String nameMatch = arg.split(".java")[0];
				if (bv.symbolTable_.containsKey(nameMatch) == false) {
					System.out.println("File " + arg + " does not contain a class named '" + nameMatch + "'");
					return;
				}
				bv.printInfo();
				bv.symbolTable_.subclassChecks();
				CheckerVisitor typeChecker = new CheckerVisitor(bv.symbolTable_);
				root.accept(typeChecker, null);
				System.out.println("Succesful type checking for file " + arg + "\n");
			} catch (IOException e) {
				System.out.println(e);
			} catch (ParseException pe) {
				System.out.println(pe);
			}
		}
		return;
	}

} 
