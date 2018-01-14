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
	boolean needID_;
	String globalStr;

	public CheckerVisitor(SymbolTable symbolTable) {
		symbolTable_ = symbolTable;
		visitingClass_ = null;
		visitingMethod_ = null;
		needID_ = false;
		globalStr = "";
		if (symbolTable_.subclassChecks() == false) {
			System.out.println("subclassChecks failed");
			System.exit(2);
		}
	}	 

	/* All the overriden classes which are common with the BuildVisitor class
	 * don't need to perform exhaustive controls:
	 * If something went wrong, the program would have exited
	 */

    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */

	public String visit(Goal n, String argu) {
		n.f0.accept(this, null);
		n.f1.accept(this, null);
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
		n.f14.accept(this, null);	
		n.f15.accept(this, null);	
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
		n.f3.accept(this, null);	
		n.f4.accept(this, null);	
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
		n.f5.accept(this, null);	
		n.f6.accept(this, null);	
		visitingClass_ = null;
		visitingMethod_ = null;
		return null;	
	}

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */

	@Override
	public String visit(VarDeclaration n, String argu){
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
		visitingMethod_ = n.f2.f0.toString();	
		String returnType = n.f1.accept(this, null);
		n.f4.accept(this, null);		
		n.f7.accept(this, null);	
		n.f8.accept(this, null);	
		String returnValue = n.f10.accept(this, null);
		returnValue = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, returnValue);
		if (symbolTable_.compatible(returnType, returnValue) == false) {
			System.out.println("Expected return type of " + returnType + ". Returned type is " + returnValue + " in method " + visitingClass_ +"::" + visitingMethod_);
			System.exit(2);
		}
		visitingMethod_ = null;
		return null;
	}


   /**
   * f0 -> FormalParameter()
   * f1 -> FormalParameterTail()
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
		if (paramType.equals("int") || paramType.equals("boolean") || paramType.equals("int []")){
			return paramType; //  Mipos return null?
		}
		if (symbolTable_.containsKey(paramType) == false) {
			System.out.println("Unknown type " + paramType + " for parameter '" + n.f1.f0.toString() + "'");
			System.exit(2);
		} else { // Do I need this?
			return paramType;
		}
		return null;
	}

   /**
   * f0 -> (FormalParameterTerm)*
   */

	@Override
	public String visit(FormalParameterTail n, String argu) {
		n.f0.accept(this, null);
		return null;
	}

    /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
	@Override
	public String visit(FormalParameterTerm n, String argu) {
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
		String varName = n.f0.f0.toString();
//		System.out.println("AssignmentStatement in varName is " + varName);
		String varType = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, varName);
		String expType = n.f2.accept(this, null);
//		System.out.println("AssignmentStatement in expType is " + expType);
		if (expType == null) {
			System.out.println("AS extType CheckError");
			System.exit(2);
		}
		if (symbolTable_.primitive(expType) == false){
			if (symbolTable_.containsKey(expType) == false) {	//  cover the A a = new A();
				expType = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, expType);
			}
		}
		if (symbolTable_.compatible(varType, expType) == false) {
			System.out.println("Expected expression of type " + varType + " instead of type " + expType + "in assignment");
			System.exit(2);
		}
		return null;
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

		String varName = n.f0.f0.toString();
		String varType = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, varName);
		if (varType.equals("int []") == false) {
			System.out.println("Expected element of type \"int[]\"");
			System.exit(2);
		}
		String tablePosition = n.f2.accept(this, null);
		if (tablePosition == null) {
			System.out.println("AA tablePosition CheckError");
			System.exit(2);
		}
		if (symbolTable_.primitive(tablePosition) == false){
			//  Got an identifier
			tablePosition = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, tablePosition);
		}
		if (tablePosition.equals("int") == false) {
			System.out.println("Expected position of type int");
			System.exit(2);
		}
		String assignedValue = n.f5.accept(this, null);
		if (assignedValue == null) {
			System.out.println("AA assignedValue CheckError");
			System.exit(2);
		}
		if (symbolTable_.primitive(assignedValue) == false){
			//  Got an identifier
			assignedValue = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, assignedValue);
		}
		if (assignedValue.equals("int") == false) {
			System.out.println("Expected assigned value of type int");
			System.exit(2);
		}
		return null;

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
		if (varExpressionType == null) {
			System.out.println("IF assignedValue CheckError");
			System.exit(2);
		}
		if (symbolTable_.primitive(varExpressionType) == false){
			//  Got an identifier
			varExpressionType = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, varExpressionType);
		}
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
		if (varExpressionType == null) {
			System.out.println("WS assignedValue CheckError");
			System.exit(2);
		}
		if (symbolTable_.primitive(varExpressionType) == false){
			//  Got an identifier
			varExpressionType = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, varExpressionType);
		}
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
		if (varExpressionType == null){
			System.out.println("PrintStatements need int expressions");
			System.exit(2);
		}
		if (symbolTable_.primitive(varExpressionType) == false){
			//  Got an identifier
			varExpressionType = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, varExpressionType);
		}
		if (varExpressionType.equals("int") == false) {
			System.out.println("PrintStatements need int expressions but I got " + varExpressionType);
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
		pe1 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe1);
		String pe2 = n.f2.accept(this, null);
		pe2 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe2);
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
		pe1 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe1);
		String pe2 = n.f2.accept(this, null);
		pe2 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe2);
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
		pe1 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe1);
		String pe2 = n.f2.accept(this, null);
		pe2 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe2);
		if (pe1.equals("int") == false || pe2.equals("int") == false) {
			System.out.println("Expected int in PlusExpression but I got " + pe1 + " ~ " + pe2);
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
		pe1 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe1);
		String pe2 = n.f2.accept(this, null);
		pe2 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe2);
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
		pe1 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe1);
		String pe2 = n.f2.accept(this, null);
		pe2 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe2);
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
		pe1 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe1);
		String pe2 = n.f2.accept(this, null);
		pe2 = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe2);
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
		pe = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, pe);
		if (pe.equals("int []") == false) {
			System.out.println("Expected array variable in ArrayLength");
			System.exit(2);
		}
		return "int";
	}

   /**
   * f0 -> PrimaryExpression() - it will be an identifier (most likely)
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 -> ")"
   */

	@Override
	public String visit(MessageSend n, String argu) {
		if (eitherNull() == true) {
			System.out.println("MS CheckError");
			System.exit(2);
		}
		String objectName = n.f0.accept(this, null);
		String className = objectName;
//		System.out.println("object name in Message is " + objectName);
		if (symbolTable_.containsKey(objectName) == false) {
			className = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, objectName);
		}
//		System.out.println("object class in Message is " + className);
		if (symbolTable_.primitive(className) == true) {
			System.out.println("Expected User-Defined Class. Primitive '" + className + "' given instead");
			System.exit(2);
		}
		Class cls = symbolTable_.getClass(className);
		if (cls == null) {
			System.out.println("Class '" + className + "' does not exist. Cannot call objects of that type");
			System.exit(2);
		}
//		needID_ = true;
		String methodName = n.f2.accept(this, null);
//		needID_ = false;
		Method m = symbolTable_.getMethodFromClass(methodName, className);
		if (m == null) {
			System.out.println("Method '" + methodName + "' of class '" + className + "' wasn't defined.");
			System.exit(2);
		}
		String params = m.parameterTypesToString();
		if (params == null) {
			System.out.println("Unknown MS CheckError #1");
			System.exit(2);
		}
		String paramsGiven = n.f4.accept(this, null);
		if (paramsGiven == null) {
			paramsGiven = "( )";
		}
		if (paramsGiven.equals("( )") == true) {	//  Check for no argus
			if (paramsGiven.equals(params) == true) {
				return m.getReturnType();
			}
		}
//		System.out.println(params + " XXX " + paramsGiven);
		String[] paramsAr = params.replace("(", "").replace(")", "").replace(" ", "").split(",");
		String[] paramsGivenAr = paramsGiven.replace("(", "").replace(")", "").replace(" ", "").split(",");
		for (int i = 0; i < paramsAr.length; i++) {
			String arguType = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, paramsGivenAr[i]);
			String paramType = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, paramsAr[i]);
//			System.out.println("arguType is " + arguType);
//			System.out.println("paramType is " + paramType);
//			System.out.println("paramsGivenAr[i] is " + paramsGivenAr[i]);
			if (symbolTable_.compatible(paramType, arguType) == false) {
				return null;
			}
		}
		return m.getReturnType();
	}

    /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */

	@Override
	public String visit(ExpressionList n, String argu) {
		String expr = n.f0.accept(this, null);
		if (expr == null) {
			return "( )";
		}
		String random = n.f1.accept(this, null);
		if (random == null) {
			globalStr = "";
			return "( " + expr + ", " + ")";
		} else {
			globalStr = "";
			return "( " + expr + ", " + random + ")";
		}
	}

    /**
    * f0 -> ( ExpressionTerm() )*
    */
	@Override
	public String visit(ExpressionTail n, String argu) {
		n.f0.accept(this, null);
		return globalStr;
	}

    /**
    * f0 -> ","
    * f1 -> Expression()
    */

	@Override
	public String visit(ExpressionTerm n, String argu) {
		String expr = n.f1.accept(this, null);
		if (expr != null) {
			globalStr = globalStr + expr + ", ";
			return null;
		} else {
//			System.out.println("hi!");
			globalStr = globalStr + ", ";
			return null;
		}
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
    *  
	* method params > method local vars > class data members > superclass data members
	*
    */	

	@Override
	public String visit(Identifier n, String argu) {
//		System.out.println("Identifier is " + n.f0.toString() + " in class visiting " + visitingClass_);
		return n.f0.toString();
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
//		System.out.println("It's a this expression in class " + visitingClass_);
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
		varExpressionType = symbolTable_.IdentifierToType(visitingClass_, visitingMethod_, varExpressionType);
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
			System.out.println("");
		}
		return "boolean";
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

}