package tcpackage;
import java.lang.*;
import java.util.*;
import visitor.*;
import syntaxtree.*;


public class BuildVisitor extends DepthFirstVisitor{
	
	private String visitingClass_;
	private String visitingMethod_;	
	public SymbolTable symbolTable_;

	public BuildVisitor(){
		
		visitingClass_ = null;
		visitingMethod_ = null;
		symbolTable_ = new SymbolTable();	
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
		boolean flag = symbolTable_.put(baseClass, visitingClass_);
		if (flag == false) {
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
		visitingClass_ = null;
		visitingMethod_ = null;	
	}
	
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
		if (methodIsNull() == true){
			flag = cls.addDataMember(new Variable(varName, varType));
			if (flag == false) {
				System.out.println("BuildError");
				System.exit(1);
			}
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
		boolean flag = cls.addMethod(new Method(retType, methodName));
		if (flag == false) {
			System.out.println("BuildError");
			System.exit(1);
		}
		visitingMethod_ = methodName;
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


}

