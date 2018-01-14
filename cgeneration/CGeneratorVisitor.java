package cgeneration;

import java.io.*;
import java.util.*;
import java.lang.*;
import visitor.*;
import syntaxtree.*;
import tcpackage.*;

public class CGeneratorVisitor extends GJDepthFirst<String, String>{
	
	private String visitingClass_;
	private String visitingMethod_;
	private SymbolTable symbolTable_;
	private ClassesInfo classesInfo_;	// it's just a map of Obfuscated classes Objects - works as a wrapper
	private StringManager strManager_;
	private String outFilename_;
	private Map<String, MethodToString> methodDefines_;


	public CGeneratorVisitor(SymbolTable st, ClassesInfo ci, String fileName) {

		visitingClass_ = null;
		visitingMethod_ = null;
		symbolTable_ = st;
		classesInfo_ = ci;
		strManager_ = new StringManager();
		outFilename_ = fileName;
		methodDefines_ = new HashMap<String, MethodToString>();
		
	}



    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */

	public String visit(Goal n, String argu) {
		n.f0.accept(this, null);	//  accept the main class
		n.f1.accept(this, null);	//  accept the user-defined types
		return null;
}


/**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> "public"
   * f4 -> "static"
   * f5 -> "void"
   * f6 -> "main"
   * f7 -> "("
   * f8 -> "String"
   * f9 -> "["
   * f10 -> "]"
   * f11 -> Identifier()
   * f12 -> ")"
   * f13 -> "{"
   * f14 -> ( VarDeclaration() )*
   * f15 -> ( Statement() )*
   * f16 -> "}"
   * f17 -> "}"
   */

	@Override
	public String visit(MainClass n, String argu) {
		if (this.eitherAssigned() == true){
			System.out.println("Mainclass EitherAssigned CheckError");
			System.exit(2);	
		}
		visitingClass_ = n.f1.f0.toString();	//  classname
		visitingMethod_ =  n.f6.toString();		//  always resolves to "main"
		n.f14.accept(this, null);				//  variable declarations
		n.f15.accept(this, null);				//  statements
		visitingClass_ = null;
		visitingMethod_ = null;
		return null;
	}



   /**
   * f0 -> ClassDeclaration()
   *       | ClassExtendsDeclaration()
   */

	@Override
	public String visit(TypeDeclaration n, String argu) {
		n.f0.accept(this, null);
		return null;
	}


  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */

	@Override
	public String visit(ClassDeclaration n, String argu){
		if (eitherAssigned() == true){
			System.out.println("BuildError");
			System.exit(2);	
		}
		visitingClass_ = n.f1.f0.toString();		
//		n.f3.accept(this, null);	It's var declaration - as in field declarations, which I don't need for this part - already have the info required in the symbol table
		n.f4.accept(this, null);	//  The MethodDeclaration I need to visit in order to produce the intermediate code
		visitingClass_ = null;
		visitingMethod_ = null;	
		return null;
	}

   /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "extends"
   * f3 -> Identifier()
   * f4 -> "{"
   * f5 -> ( VarDeclaration() )*
   * f6 -> ( MethodDeclaration() )*
   * f7 -> "}"
   */

	@Override
	public String visit(ClassExtendsDeclaration n, String argu){
		if (eitherAssigned() == true){
			System.out.println("CED EitherAssigned CheckError");
			System.exit(2);
		}
		visitingClass_ = n.f1.f0.toString();	
//		n.f5.accept(this, null);	It's var declaration - as in field declarations, which I don't need for this part - already have the info required in the symbol table
		n.f6.accept(this, null);	//  The MethodDeclaration I need to visit in order to produce the intermediate code
		visitingClass_ = null;
		visitingMethod_ = null;
		return null;	
	}

   /**
   * f0 -> "public"
   * f1 -> Type()
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( FormalParameterList() )?
   * f5 -> ")"
   * f6 -> "{"
   * f7 -> ( VarDeclaration() )*
   * f8 -> ( Statement() )*
   * f9 -> "return"
   * f10 -> Expression()
   * f11 -> ";"
   * f12 -> "}"
   */

	@Override
	public String visit(MethodDeclaration n, String argu){
		if (classIsNull() == true || methodIsNull() == false){
			System.out.println("MD sthsNull CheckError");
			System.exit(2);
		}
		visitingMethod_ = n.f2.f0.toString();			
		n.f7.accept(this, null);	
		n.f8.accept(this, null);	
		String returnValue = n.f10.accept(this, null);	// CHANGED BE NEEDED HERE + THELOUME RETURN NULL?
		//returnValue = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, returnValue); -- NEED TO ACTIVATE EXPRESSION
		visitingMethod_ = null;
		return null;
}

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */

	@Override
	public String visit(VarDeclaration n, String argu){		/* It's only called for VarDeclaration in a method */
		if (classIsNull() == true || methodIsNull() == true){
			System.out.println("VD NullClass Error");
			System.exit(3);
		} 
		System.out.println("Truth is a beautiful thing");
		int which = n.f0.f0.which;
		String varType = this.determineVarType(n, which);
		String varName = n.f1.f0.toString();
		String key;
		if (visitingMethod_.equals("main")){
			key = "main";
		} else {
			key = visitingClass_ + "." + visitingMethod_;
		}
		MethodToString mts = methodDefines_.get(key);
		if(mts == null)  {
			System.out.println("VD mts Not Found");
			System.exit(3);
		}

		String llvmType = typeToPointer(varType);
		String llvmVarDecl = strManager_.alloca_.replace("#optional", "%" + varName + " =").replace("#type", llvmType);
		mts.methodDefinitionTemplate_ = mts.methodDefinitionTemplate_.replace(mts.bodyTemplate_, llvmVarDecl + "\n\t" + mts.bodyTemplate_);
		System.out.println(mts.methodDefinitionTemplate_);

		return null;
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
					if (m.getName().equals("main") == true){	// Calculate the name and the return type of the method - Exclude the main function
						methodName = "main";
						returnType = "i32";
					} else {
						methodName = ocls.name_ + "." + m.getName();
						methodArgs = "i8* %this";
						returnType = this.typeToPointer(m.getReturnType());
					}
					List<Variable> params = m.getParameters();	
					String paramsToLLVM = "";
					for (Variable v : params) {
						String varType = this.typeToPointer(v.getType());
						methodArgs = methodArgs + ", " + varType + " " + "%." + v.getName(); // Create the parameter list like this: <type> %.variable_name
						paramsToLLVM = paramsToLLVM + strManager_.alloca_.replace("#optional", "%" + v.getName() + " = ").replace("#type", varType);
						paramsToLLVM = paramsToLLVM + strManager_.store_.replace("#type", varType).replace("#source", "%." + v.getName()).replace("#target", "%" + v.getName());

					}
					methodString = methodString.replace("#r", returnType).replace("#fname", methodName).replace("#args", methodArgs).replace("#body", paramsToLLVM + "#body" + methodName);
					dump = dump + methodString;
					methodDefines_.put(methodName, new MethodToString(methodName, methodString, "#body" + methodName));
					// System.out.println("Putted");
					// System.out.println(methodName);
					// System.out.println(methodString);
					// System.out.println("#body" + methodName);
				}	// for every method

			} // for every class
			System.out.println(dump);
	}


	public boolean eitherAssigned(){
		if (visitingClass_ != null || visitingMethod_ != null){
			return true;
		} else {
			return false;
		}
	}

	public boolean eitherNull(){
		if (visitingClass_ == null || visitingMethod_ == null){
			return true;
		}
		return false;
}

	
	public boolean classIsNull(){
		if (visitingClass_ == null) {
			return true;
		}
		return false;	
	}
	
	public boolean methodIsNull(){
		if (visitingMethod_ == null){
			return true;
		}
		return false;
}



	public String determineVarType(VarDeclaration n, int which){
		if (which == 0){
			return  "int []"; 
		} else if (which == 1){
			return "boolean"; 
		} else if (which == 2){
			return "int"; 
		} else if (which == 3){
			Node test = n.f0.f0.choice;
			return ((Identifier) test).f0.toString(); 
		} else {
			return "error";
		}
	}

}

