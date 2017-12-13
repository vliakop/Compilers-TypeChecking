package tcpackage;
import java.lang.*;
import java.util.*;
import visitor.*;
import syntaxtree.*;


public class BuildVisitor extends DepthFirstVisitor{
	
	private String visitingClass_;
	private String visitingMethod_;	
	public SymbolTable symbolTable_;
	private int globalOffset_;
	private int methodOffset_;
	private boolean extention_; //  if a class extends some other

	public BuildVisitor(){
		
		visitingClass_ = null;
		visitingMethod_ = null;
		symbolTable_ = new SymbolTable();	
		globalOffset_ = 0; // for Data Members
		methodOffset_ = 0; // for methods
		extention_ = false;

	}
	
	

	@Override
	public void visit(MainClass n) {
		if (this.eitherAssigned() == true){
			System.out.println("BuildError");
			System.exit(1);	
		}
		visitingClass_ = n.f1.f0.toString();
		boolean flag = symbolTable_.put(visitingClass_);
		if (flag == false) {
			System.out.println("BuildError");
			System.exit(1);	
		}
		visitingMethod_ =  n.f0.toString();
		Class cls = symbolTable_.getClass(visitingClass_);
		if (cls == null){
			System.out.println("BuildError");
			System.exit(1);	
		}
		if (cls.addMethod(new Method("void", visitingMethod_ )) == false){
			System.out.println("BuildError");
			System.exit(1);	
		}
		n.f0.accept(this);	
		n.f1.accept(this);	
		n.f2.accept(this);	
		n.f3.accept(this);	
		n.f4.accept(this);	
		n.f5.accept(this);	
		n.f6.accept(this);	
		n.f7.accept(this);	
		n.f8.accept(this);	
		n.f9.accept(this);	
		n.f10.accept(this);	
		n.f11.accept(this);	
		n.f12.accept(this);	
		n.f13.accept(this);	
		n.f14.accept(this);	
		n.f15.accept(this);	
		n.f16.accept(this);	
		n.f17.accept(this);	
		visitingClass_ = null;
		visitingMethod_ = null;
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
	public void visit(ClassDeclaration n){
		if (eitherAssigned() == true){
			System.out.println("BuildError");
			System.exit(1);	
		}
		visitingClass_ = n.f1.f0.toString();
		boolean flag = symbolTable_.put(visitingClass_);
		if (flag == false) {
			System.out.println("BuildError");
			System.exit(1);	
		}
		extention_ = false;
		n.f0.accept(this);	
		n.f1.accept(this);	
		n.f2.accept(this);	
		n.f3.accept(this);	
		n.f4.accept(this);	
		n.f5.accept(this);	
		visitingClass_ = null;
		visitingMethod_ = null;	
	}	
	
	@Override
	public void visit(ClassExtendsDeclaration n){
		if (eitherAssigned() == true){
			System.out.println("BuildError");
			System.exit(1);
		}
		visitingClass_ = n.f1.f0.toString();
		String baseClass = n.f3.f0.toString();
		Class bcls = symbolTable_.getClass(baseClass);
		if (bcls == null) {
			System.out.println("Class " + visitingClass_ + " extends class " + baseClass + ", which has not been declared yet");
			System.exit(1);
		}
		boolean flag = symbolTable_.put(baseClass, visitingClass_);
		if (flag == false) {
			System.out.println("BuildError");
			System.exit(1);	
		}
		extention_ = true;
		n.f0.accept(this);	
		n.f1.accept(this);	
		n.f2.accept(this);	
		n.f3.accept(this);	
		n.f4.accept(this);	
		n.f5.accept(this);	
		n.f6.accept(this);	
		n.f7.accept(this);	
		visitingClass_ = null;
		visitingMethod_ = null;	
	}

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */

	@Override
	public void visit(VarDeclaration n){
		if (classIsNull() == true){
			System.out.println("BuildError");
			System.exit(1);
		}
		Class cls = symbolTable_.getClass(visitingClass_);
		if (cls == null){
			System.out.println("BuildError");
			System.exit(1);
		}
		String varName = n.f1.f0.toString();
		int which = n.f0.f0.which;
		String varType = this.determineVarType(n, which);
		//String varType = n.f0.f0.toString();
		boolean flag;
		int offset;
		if (methodIsNull() == true){	//  It's a data member variable declaration
			Variable var = new Variable(varName, varType);
			flag = cls.addDataMember(var);	//  Adding a data member, need to set the offset
			if (flag == false) {
				System.out.println("BuildError");
				System.exit(1);
			}
			if (extention_ == false) {	// if it is not a subclass just assign the current offset and update it
				offset = globalOffset_;
				this.updateOffset(varType);
			} else {
				offset = calculateVarOffset(varType, varName);	// else calculate the offset
				if (offset == globalOffset_){	// if it is not different than the current (lower) - it's a new data member
					this.updateOffset(varType);
				}
			}
			var.setOffset(offset);
		} else {
			Method m = cls.getMethod(visitingMethod_);
			if (m == null){
				System.out.println("BuildError");
				System.exit(1);
			}
			flag = m.addLocalVariable(new Variable(varName, varType));
			if (flag == false) {
				System.out.println("BuildError");
				System.exit(1);
			}
		}
		n.f0.accept(this);	
		n.f1.accept(this);	
		n.f2.accept(this);	
	}	
	
	@Override
	public void visit(MethodDeclaration n){
		if (classIsNull() == true || methodIsNull() == false){
			System.out.println("BuildError");
			System.exit(1);
		}
		Class cls = symbolTable_.getClass(visitingClass_);
		if (cls == null){
			System.out.println("BuildError");
			System.exit(1);
		}
		int which = n.f1.f0.which;
		String retType = determineRetVal(n, which);
		String methodName = n.f2.f0.toString();
		visitingMethod_ = methodName;
		Method m = new Method(retType, methodName);
		boolean flag = cls.addMethod(m);
		if (flag == false) {
			System.out.println("BuildError");
			System.exit(1);
		}
		int offset;
		if (extention_ == false) {
			offset = methodOffset_;
			this.updateMethodOffset();
		} else {
			offset = calculateMethodOffset(methodName);
			if (offset == methodOffset_) {
				this.updateMethodOffset();
			}
		}
		m.setOffset(offset);
		n.f0.accept(this);	
		n.f1.accept(this);	
		n.f2.accept(this);	
		n.f3.accept(this);	
		n.f4.accept(this);	
		n.f5.accept(this);	
		n.f6.accept(this);	
		n.f7.accept(this);	
		n.f8.accept(this);	
		n.f9.accept(this);	
		n.f10.accept(this);	
		n.f11.accept(this);	
		n.f12.accept(this);	
		visitingMethod_ = null;
	}
	
	@Override
	public void visit(FormalParameter n){
		if (eitherNull() == true){		
			System.out.println("BuildError");
			System.exit(1);
		}
		Class cls = symbolTable_.getClass(visitingClass_);
		if (cls == null){
			System.out.println("BuildError");
			System.exit(1);
		}
		Method m = cls.getMethod(visitingMethod_);
		if (m == null){
			System.out.println("BuildError");
			System.exit(1);
		}
		String paramName = n.f1.f0.toString();
		int which = n.f0.f0.which;
		String paramType = determineParamType(n, which);
		boolean flag = m.addParameter(new Variable(paramName, paramType));			   if (flag == false){
			System.out.println("BuildError");
			System.exit(1);
		}
		n.f0.accept(this);	
		n.f1.accept(this);	
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

	public boolean eitherNull(){
		if (visitingClass_ == null || visitingMethod_ == null){
			return true;
		}
		return false;
	}
		
	public boolean eitherAssigned(){
		if (visitingClass_ != null || visitingMethod_ != null){
			return true;
		} else {
			return false;
		}
	}
	
	public void print(){
		symbolTable_.print();
	}
	
	public void printInfo(){
		symbolTable_.printInfo();
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

	public void updateOffset(String varType) {
		if (varType.equals("boolean") == true) {
			globalOffset_ = globalOffset_ + 1;
		} else {
			globalOffset_ = globalOffset_ + 4;
		}
	}

	public void updateMethodOffset(){
		methodOffset_ = methodOffset_ + 8;
	}


	public int calculateVarOffset(String varType, String varName) {

		Class cls = symbolTable_.getClass(visitingClass_);
		String baseClass = cls.getSuperName();
		Class pcls = symbolTable_.getClass(baseClass);
		while (pcls != null) {
			List<Variable> varList = pcls.getDataMembers();
			for (Variable v : varList) {
				if (v.getName().equals(varName) == true) {
					if (v.getType().equals(varType) == true) {
						return v.getOffset();
					}
				}
			} //  Ran dry of the data member list of an ancestor was empty, try the next one, if it exists
			if (pcls.isSubclass() == true) {
				pcls = symbolTable_.getClass(pcls.getSuperName());
				if (pcls == null) {
					System.out.println("Superclass has not been declared before it's subclass");
					System.exit(1);
				}
			} else {
				return globalOffset_;
			}
		}
		return globalOffset_;
	}

	public int calculateMethodOffset(String methodName) {

		Class cls = symbolTable_.getClass(visitingClass_);
		String baseClass = cls.getSuperName();
		Class pcls = symbolTable_.getClass(baseClass);
		while (pcls != null) {
			List<Method> mList = pcls.getMethods();
			for (Method m : mList) {
				if (m.getName().equals(methodName) == true) {
					return m.getOffset();
				}
			}	//  ran dry of the data member list of an ancestor was empty, try the next one, if it exists
			if (pcls.isSubclass() == true) {
				pcls = symbolTable_.getClass(pcls.getSuperName());
				if (pcls == null) {
					System.out.println("Superclass has not been declared before it's subclass");
					System.exit(1);
				}
			} else {
				return methodOffset_;
			}
		}
		return methodOffset_;
	}
}

