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
   * f15 -> ( Statement() )* -> should be checked
   * f16 -> "}"
   * f17 -> "}"
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
		n.f3.accept(this);	
		n.f4.accept(this);	
		visitingClass_ = null;
		visitingMethod_ = null;	
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
		n.f5.accept(this);	
		n.f6.accept(this);	
		visitingClass_ = null;
		visitingMethod_ = null;	
	}

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */

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
		visitingMethod_ = methodName;	
		n.f4.accept(this);		
		n.f7.accept(this);	
		n.f8.accept(this);	
		// MISSING CONTROL - HANDLE STATEMENTS FIRST
		n.f10.accept(this);		
		visitingMethod_ = null;
	}


   /**
   * f0 -> FormalParameter()
   * f1 -> ( FormalParameterRest() )*
   */

	@Override
	public String visit(FormalParameterList n, String argu) {
		n.f0.accept(this, null);
		n.f1.accept(this, null);
		return null;
	}

   /**
   * f0 -> Type()
   * f1 -> Identifier()
   */

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

   /**
   * f0 -> ","
   * f1 -> FormalParameter()
   */

	@Override
	public String visit(FormalParameterRest n, String argu) {
		n.f1.accept(this, null);
		return null;
	}


   /**
   * f0 -> ArrayType()
   *       | BooleanType()
   *       | IntegerType()
   *       | Identifier()
   */

	@Override
	public String visit(Type n, String Argu) {
		return n.f0.accept(this, null);
	}

   /**
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   */

	@Override
	public String visit(ArrayType n, String argu) {
		return "int []";
	}

   /**
   * f0 -> "boolean"
   */

	@Override
	public String visit(BooleanType n, String argu) {
		return "boolean";
	}

   /**
   * f0 -> "int"
   */

	@Override
	public String visit(IntegerType n, String argu) {
		return "int";
	}


   /**
   * f0 -> Block()
   *       | AssignmentStatement()
   *       | ArrayAssignmentStatement()
   *       | IfStatement()
   *       | WhileStatement()
   *       | PrintStatement()
   */

	@Override
	public String visit(Statement n, String argu) {
		return n.f0.accept(this, null);
	}

   /**
   * f0 -> "{"
   * f1 -> ( Statement() )*
   * f2 -> "}"
   */

	@Override
	public String visit(Block n, String argu) {
		return n.f1.accept(this, null);
	}

   /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Expression()
   * f3 -> ";"
   */

	@Override
	public String visit(AssignmentStatement n, String argu) {
		String varType = n.f0.f0.toString();
		String expType = n.f2.;
		// Things to do are pending
	}

   /**
   * f0 -> Identifier() // should be array type
   * f1 -> "["
   * f2 -> Expression()
   * f3 -> "]"
   * f4 -> "="
   * f5 -> Expression()
   * f6 -> ";"
   */

	@Override
	public String visit(ArrayAssignmentStatement n, String argu) {
		String varType = n.f0.f0.toString();
		// MISSING

	}

   /**
   * f0 -> "if"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   * f5 -> "else"
   * f6 -> Statement()
   */

	@Override
	public String visit(IfStatement n, String argu) {
		String varExpressionType = n.f2.accept(this, null);
		if (varExpressionType.equals("boolean") == false) {
			System.out.println("Ifstatements need boolean expressions");
			System.exit(2);
		}
		n.f4.accept(this, null);
		n.f6.accept(this, null);
		return null;
	}


   /**
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   */

	@Override
	public String visit(WhileStatement n, String argu) {
		String varExpressionType = n.f2.accept(this, null);
		if (varExpressionType.equals("boolean") == false) {
			System.out.println("Whilestatements need boolean expressions");
			System.exit(2);
		}
		n.f4.accept(this, null);
		return null;
	}

   /**
   * f0 -> "System.out.println"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> ";"
   */

	@Override
	public String visit(PrintStatement n, String argu) {
		String varExpressionType = n.f2.accept(this, null);
		if (varExpressionType.equals("int") == false) {
			System.out.println("PrintStatements need int expressions");
			System.exit(2);
		}
		return null;
	}

   /**
   * f0 -> AndExpression()
   *       | CompareExpression()
   *       | PlusExpression()
   *       | MinusExpression()
   *       | TimesExpression()
   *       | ArrayLookup()
   *       | ArrayLength()
   *       | MessageSend()
   *       | PrimaryExpression()
   */

	@Override
	public String visit(Expression n, String argu) {
		return n.f0.accept(this, null);
	}

   /**
   * f0 -> PrimaryExpression()
   * f1 -> "and"
   * f2 -> PrimaryExpression()
   */

	@Override
	public String visit(AndExpression n, String argu) {
		String pe1 = n.f0.accept(this, null);
		String pe2 = n.f2.accept(this, null);
		if (pe1.equals("boolean") == false || pe2.equals("boolean") == false) {
			System.out.println("Expected boolean in AndExpression");
			System.exit(2);
		}
		return "boolean";
	}

   /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
	@Override
	public String visit(CompareExpression n, String argu) {
		String pe1 = n.f0.accept(this, null);
		String pe2 = n.f2.accept(this, null);
		if (pe1.equals("int") == false || pe2.equals("int") == false) {
			System.out.println("Expected int in CompareExpression");
			System.exit(2);
		}
		return "boolean";
	}


   /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   */

	@Override
	public String visit(PlusExpression n, String argu) {
		String pe1 = n.f0.accept(this, null);
		String pe2 = n.f2.accept(this, null);
		if (pe1.equals("int") == false || pe2.equals("int") == false) {
			System.out.println("Expected int in PlusExpression");
			System.exit(2);
		}
		return "int";
	}

   /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  
	@Override
	public String visit(MinusExpression n, String argu) {
		String pe1 = n.f0.accept(this, null);
		String pe2 = n.f2.accept(this, null);
		if (pe1.equals("int") == false || pe2.equals("int") == false) {
			System.out.println("Expected int in MinusExpression");
			System.exit(2);
		}
		return "int";
	}

   /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  
	@Override
	public String visit(TimesExpression n, String argu) {
		String pe1 = n.f0.accept(this, null);
		String pe2 = n.f2.accept(this, null);
		if (pe1.equals("int") == false || pe2.equals("int") == false) {
			System.out.println("Expected int in TimesExpression");
			System.exit(2);
		}
		return "int";
	}

   /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   */

	@Override
	public String visit(ArrayLookup n, String argu) {
		String pe1 = n.f0.accept(this, null);
		String pe2 = n.f2.accept(this, null);
		if (pe1.equals("int []") == false || pe2.equals("int") == false) {
			System.out.println("Error in ArrayLookup");
			System.exit(2);
		}
		return "int";
	}

   /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */

	@Override
	public String visit(ArrayLength n, String argu) {
		String pe = n.f0.accept(this, null);
		if (pe.equals("int []") == false) {
			System.out.println("Expected array variable in ArrayLength");
			System.exit(2);
		}
		return "int";
	}

   /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 -> ")"
   */

	@Override
	public String visit(MessageSend n, String argu) {
		// MISSING
	}

   /**
   * f0 -> Expression()
   * f1 -> ( ExpressionRest() )* // NodeList optional
   */

	@Override
	public String visit(ExpressionList n, String argu) {
		// MISSING
	}

	@Override
	public String visit(ExpressionRest n, String argu) {
		return n.f1.accept(this, argu);
	}

   /**
   * f0 -> IntegerLiteral()
   *       | TrueLiteral()
   *       | FalseLiteral()
   *       | Identifier()
   *       | ThisExpression()
   *       | ArrayAllocationExpression()
   *       | AllocationExpression()
   *       | NotExpression()
   *       | BracketExpression()
   */

	@Override
	public String visit(PrimaryExpression n, String argu) {
		return n.f0.accept(this, argu);
	}

 	/**
   	* f0 -> NTEGER_LITERAL
   	*/

	@Override
	public String visit(IntegerLiteral n, String argu) {
		return "int";
	}

	/**
    * f0 -> "true"
    */	

	@Override
	public String visit(TrueLiteral n, String argu) {
		return "boolean";
	}

	/**
    * f0 -> "false"
    */	

	@Override
	public String visit(FalseLiteral n, String argu) {
		return "boolean";
	}


	/**
    * f0 -> IDENTIFIER
    */	

	@Override
	public String visit(Identifier n, String argu) {
		// MISSING
	}

	/**
    * f0 -> this
    */

	@Override
	public String visit(ThisExpression n, String argu) {
		if (visitingClass_ == null) {
			System.out.println("Nullclass in ThisExpression CheckError");
			System.exit(2);
		}
		return visitingClass_;
	}


    /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */

	@Override
	public String visit(ArrayAllocationExpression n, String argu) {
		String varExpressionType = n.f3.accept(this, null);
		if (varExpressionType.equals("int") == false) {
			System.out.println("Expected int value in array allocation");
			System.exit(2);
		}
		return "int []";
	}

    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    * identifier should be some class 
    * return Identifier
    */

	@Override
	public  String visit(AllocationExpression n, String argu) {
		String cls = n.f1.f0.toString();
		if (symbolTable_.containsKey(cls) == false) {
			System.out.println("class of type " + cls + " does not exist");
			System.exit(2);
		}
		return cls;
	}

    /**
    * f0 -> "!"
    * f1 -> Expression()
    */
    @Override
	public String visit(NotExpression n, String argu) {
		String varExpressionType = n.f1.accept(this, null);
		if (varExpressionType.equals("boolean") == false) {
			System.out.println("")
		}
	}

    /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
	@Override
	public String visit(BracketExpression n, String argu) {
		return n.f1.accept(this, null);
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