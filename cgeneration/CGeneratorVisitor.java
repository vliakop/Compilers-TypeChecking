package cgeneration;

import java.io.*;
import java.util.*;
import java.lang.*;
import visitor.*;
import syntaxtree.*;
import tcpackage.*;

public class CGeneratorVisitor extends DepthFirstVisitor{
	
	private String visitingClass_;
	private String visitingMethod_;
	private SymbolTable symbolTable_;
	private ClassesInfo classesInfo_;
	private StringManager strManager_;
	private String outFilename_;

	public CGeneratorVisitor(SymbolTable st, ClassesInfo ci, String fileName) {

		visitingClass_ = null;
		visitingMethod_ = null;
		symbolTable_ = st;
		classesInfo_ = ci;
		strManager_ = new StringManager();
		outFilename_ = fileName;
		//out_ = new FileOutputStream(fileName + ".ll");
	}

	public String typeToPointer(String type) {
		if (type.equals("boolean") == true) {
			return "i1";
		} else if (type.equals("int") == true) {
			return "i32";
		} else {
			return "i8";
		}
	}

	public void vtables() {
		String gDump = "";
		for (Map.Entry<String, ObfuscatedClass> entry : classesInfo_.table_.entrySet()) {
			ObfuscatedClass ocls = entry.getValue();
			String vtableName = ocls.name_ + "_vtable";
			String dump = strManager_.global_.replace("#name", vtableName);

			String size =  String.valueOf(ocls.methodBytes_/8);	//  number of methods
			String type = "i8*";		
			int len = ocls.methodBytes_/8;
//			System.out.println("methods are in total: " + len + " in class " + ocls.name_);
			
			String declarations = "";
/*			System.out.println("For class '" + ocls.name_ + "' there are " + len + " methods. The corresponding field says " + ocls.methods_.size());
				if (ocls.name_.equals("Factorial") == true) {
					continue;
				}
*/			for (int i = 0; i < len; i++) {		// for every method of the class
				
				Method m = ocls.methods_.get(i);
				String typeFrom = this.typeToPointer(m.getReturnType());		// return type
				String args = "(i8*";	// i8* -> this (pointer to self) -  eg args == (i8*, i32, i32, i1)*
				for (Variable v : m.getParameters()) {
					args = args + "," + this.typeToPointer(v.getType());
				}	
				args = args + ")*";
				String name = "@" + ocls.methodInClass_.get(i) + "." + m.getName();		// eg @B.foo 
				String temp = "(" + typeFrom + " " + args;

				String partialDecl = "i8*" + strManager_.bitcast_.replace("#typeFrom", temp).replace("%#reg", name).replace("#typeTo", "i8*)");
				if (i == 0) {
					declarations = declarations + partialDecl;
				} else {
					declarations = declarations + ", " + partialDecl;
				}
				
			}	// Computated the declarations in a global definition
			dump = dump.replace("#size", size).replace("#type", type).replace("#declarations", declarations);
			gDump = gDump + dump;
		}
		strManager_.accumulator_ = strManager_.accumulator_ + gDump + "\n" + strManager_.declarations_;
		System.out.println(strManager_.accumulator_);

	}

	public void definitions() {
		String dump = "";
		for (Map.Entry<String, ObfuscatedClass> entry : classesInfo_.table_.entrySet()){
				ObfuscatedClass ocls = entry.getValue();
				List<Method> omethods = ocls.methods_;
				List<String> owner = ocls.methodInClass_;

				int len = omethods.size();
				for (int i = 0; i < len; i++) {		// If the method is not overriden, the baseclass will write it
					if (ocls.name_.equals(owner.get(i)) == false) {
						continue;
					}
					Method m = omethods.get(i);
					
					String methodString = strManager_.define_;

					String  returnType = ""; // Get the return type string and convert it to the corresponding llvm type
					String methodName = "";
					String methodArgs = "";
					if (m.getName().equals("main") == true){
						methodName = "main";
						returnType = "i32";
					} else {
						methodName = ocls.name_ + "." + m.getName();
						methodArgs = "i8* %this";
						returnType = this.typeToPointer(m.getReturnType());
					}
					List<Variable> params = m.getParameters();
					for (Variable v : params) {
						methodArgs = methodArgs + ", " + this.typeToPointer(v.getType()) + " " + "%." + v.getName(); 
					}
					methodString = methodString.replace("#r", returnType).replace("#fname", methodName).replace("#args", methodArgs).replace("#body", "#body" + methodName);
					dump = dump + methodString;
				}	// for every method

			}
			System.out.println(dump);
	}

	





}
