package tcpackage;

import java.io.*;
import java.util.*;
import java.lang.*;
import visitor.*;
import syntaxtree.*;

public class CheckerVisitor extends GJDepthFirst<String, String> {
	
	private SymbolTable symbolTable_;
	private String visitingClass_;
	private String visitingMethod_;

	public CheckerVisitor(SymbolTable symbolTable) {
		symbolTable_ = symbolTable;
		visitingClass_ = null;
		visitingMethod_ = null;
		if (symbolTable_.subclassChecks() == false) {
			System.exit(2);
		}
	}	 

	/* All the overriden classes which are common with the BuildVisitor class
	 * don't need to perform exhaustive controls:
	 * If something went wrong, the program would have exited
	 */

	@Override
	public String visit(MainClass n, String argu) {
		if (this.eitherAssigned() == true){
			System.out.println("Mainclass EitherAssigned CheckError");
			System.exit(2);	
		}
		visitingClass_ = n.f1.f0.toString();
		visitingMethod_ =  n.f0.toString();	
		n.f14.accept(this);	
		n.f15.accept(this);	
		visitingClass_ = null;
		visitingMethod_ = null;
		return null;
	}

	@Override
	public String visit(TypeDeclaration n, String argu) {
		n.f0.accept(this, null);
		return null;
	}

	@Override
	public String visit(ClassDeclaration n, String argu){
		if (eitherAssigned() == true){
			System.out.println("BuildError");
			System.exit(2);	
		}
		visitingClass_ = n.f1.f0.toString();		
		n.f3.accept(this);	
		n.f4.accept(this);	
		visitingClass_ = null;
		visitingMethod_ = null;	
	}

	@Override
	public String visit(ClassExtendsDeclaration n, String argu){
		if (eitherAssigned() == true){
			System.out.println("CED EitherAssigned CheckError");
			System.exit(2);
		}
		visitingClass_ = n.f1.f0.toString();	
		n.f5.accept(this);	
		n.f6.accept(this);	
		visitingClass_ = null;
		visitingMethod_ = null;	
	}

	@Override
	public void visit(VarDeclaration n, String argu){
		if (classIsNull() == true){
			System.out.println("VD NullClass Error");
			System.exit(2);
		}
		int which = n.f0.f0.which;
		String varType = this.determineVarType(n, which);
		if (this.varIsIdentifier(varType) == true) {
			if (symbolTable_.containsKey(varType) == false) {
				System.out.println("Unknown type " + varType + " for variable '" + n.f1.f0.toString() + "'");
				System.exit(2);
			}

		}
		return null;
	}


	@Override
	public String visit(MethodDeclaration n, String argu){
		if (classIsNull() == true || methodIsNull() == false){
			System.out.println("MD sthsNull CheckError");
			System.exit(2);
		}
		visitingMethod_ = methodName;	
		n.f4.accept(this);		
		n.f7.accept(this);	
		n.f8.accept(this);	
		// MISSING CONTROL - HANDLE STATEMENTS FIRST
		n.f10.accept(this);		
		visitingMethod_ = null;
	}

	@Override
	public String visit(FormalParameterList n, String argu) {
		n.f0.accept(this, null);
		n.f1.accept(this, null);
		return null;
	}

	@Override
	public String visit(FormalParameter n, String argu){
		if (eitherNull() == true){		
			System.out.println("FP EitherNull CheckError");
			System.exit(2);
		}
		int which = n.f0.f0.which;
		String paramType = determineParamType(n, which);
		if (symbolTable_.containsKey(paramType) == false) {
			System.out.println("Unknown type " + paramType + " for parameter '" + n.f1.f0.toString + "'");
		}
	}

	@Override
	public String visit(FormalParameterRest n, String argu) {
		n.f1.accept(this, null);
		return null;
	}

	@Override
	public String visit(Type n, String Argu) {
		return n.f0.accept(this, null);
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

	public boolean varIsIdentifier(String varType) {
		if (varType.equals("int []") == false && varType.equals("boolean") == false && varType.equals("int") == false) {
			return true;
		} else {
			return false;
		}
	}

	public String determineParamType(FormalParameter n, int which){
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
	public String determineRetVal(MethodDeclaration n, int which){
		if (which == 0){
			return  "int []"; 
		} else if (which == 1){
			return "boolean"; 
		} else if (which == 2){
			return "int"; 
		} else if (which == 3){
			Node test = n.f1.f0.choice;
			return ((Identifier) test).f0.toString(); 
		} else {
			return "error";
		}
	}


}