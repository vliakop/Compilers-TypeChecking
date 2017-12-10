package tcpackage;

import java.util.*;
import visitor.*;
import syntaxtree.*;


public class BuildVisitor extends DepthFirstVisitor{
	
	private String visitingClass_;
	private String visitingMethod_;	
	private SymbolTable symbolTable_;

	public BuildVisitor(){
		
		visitingClass_ = null;
		visitingMethod_ = null;
		symbolTable_ = new SymbolTable();	
	}
	
	

	@Override
	public void visit(MainClass n) {
		if (this.eitherAssigned() == true){
			System.out.println("BuildError");
			return;	
		}
		visitingClass_ = n.f1.f0.toString();
		boolean flag = symbolTable_.put(visitingClass_, null);
		if (flag == false) {
			System.out.println("BuildError");
			return;	
		}
		visitingMethod_ =  n.f0.toString();
		Class cls = symbolTable_.getClass(visitingClass_);
		if (cls == null){
			System.out.println("BuildError");
			return;	
		}
		if (cls.addMethod(new Method("void", visitingMethod_ )) == false){
			System.out.println("BuildError");
			return;	
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
	}	

	public boolean eitherAssigned(){
		if (visitingClass_ != null || visitingMethod_ != null){
			return true;
		} else {
			return false;
		}
	}

}

