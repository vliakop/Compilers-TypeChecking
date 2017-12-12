package tcpackage;

import java.util.*; /*  Java Collections  */

public class Method{
	
	public Method  instance(){
		Variable p1 = new Variable("par1", "String");
		Variable p2 = new Variable("par2",  "int");
		Variable l1 = new Variable("local1", "String");
		Variable l2 = new Variable("local2", "String");
		Method m = new Method("void", "testMethod");
		m.addParameter(p1);
		m.addParameter(p2);
		m.addLocalVariable(l1);
		m.addLocalVariable(l2);
		return m;
		//m.print();
		//System.out.println(m.toString());
	}
		
	private String returnType_;
	private String name_;
	private List<Variable> parameters_;
	private List<Variable> localVariables_;
	private int offset_;
		
	public Method(String returnType, String name, int offset){
		returnType_ = returnType;
		name_ = name;
		parameters_ = new ArrayList<Variable>();
		localVariables_ = new ArrayList<Variable>();
		offset_ = offset;	
	}

	public Method(String returnType, String name){
		returnType_ = returnType;
		name_ = name;
		parameters_ = new ArrayList<Variable>();
		localVariables_ = new ArrayList<Variable>();	
		offset_ = -1;
	}
	
	public int getOffset() {
		return offset_;
	}

	public String getReturnType(){
		return returnType_;
	}
	
	public String getName(){
		return name_;
	}
	
	public List<Variable> getParameters(){
		return parameters_;
	}
	
	public Variable getParameter(String paramName) {
		for (Variable v : parameters_) {
			if (v.getName().equals(paramName) == true) {
				return v;
			}
		}
		return null;
	}

	public Variable getLocalVariable(String lvName) {
		for (Variable v : localVariables_) {
			if (v.getName().equals(lvName) == true) {
				return v;
			}
		}
		return null;
	}

	public List<Variable> getLocalVariables(){
		return localVariables_;
	}
	
	public void setReturnType(String returnType){
		returnType_ = returnType;
	}
	
	public void setName(String name){
		name_ = name;
	}
	
	public void setOffset(int offset) {
		offset_ = offset;
	}

	public boolean addParameter(Variable parameter){
		for (Variable p : parameters_) {
			if (p.getName().equals(parameter.getName()) == true){
				return false;
			}
		}
		return parameters_.add(parameter);
	}
	
	public boolean addLocalVariable(Variable localVariable){
		if(localVariables_.contains(localVariable) == false) {
			return localVariables_.add(localVariable);
		} else {
			return false;
		}
	}
	
	public void print(){
		System.out.println("method:" + returnType_ + " " + name_);
		System.out.println("parameters:");
		for (Variable v : parameters_) {
			System.out.println("\t" + v.toString());
		}
		System.out.println("local variables:");
		for (Variable v : localVariables_) {
			System.out.println("\t" + v.toString());
		}
	}
	
	public String toString(){
		String params = "( ";
		for (Variable v : parameters_) {
			params = params +  v.toString() + ", ";
		}
		params = params + ")";
		String lvars = " {";
		for (Variable v : localVariables_) {
			lvars = lvars + "\n\t" + v.toString() + ";";
		}
		lvars = lvars + "\n}";
		String retVal = returnType_ + " " + name_ + params + lvars + ":" + offset_;
		return retVal;	
	}	
 
	/* Returns a string of the paramet ers
	 * eg: (int i, int j, A k) 
	 */
	public String parametersToString() {
		String params = "( ";
		for (Variable v : parameters_) {
			params = params +  v.toString() + ", ";
		}
		params = params + ")";
		return params;
	}

	public String parameterTypesToString() {
		String params = "( ";
		for (Variable v : parameters_) {
			params = params + v.getType() + ", ";
		}
		params = params + ")";
		return params;
	}
}
