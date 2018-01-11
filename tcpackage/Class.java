package tcpackage;

import java.util.*;

public class Class{
	public static void main(String[] args){
		Method m = new Method("int", "boolean");
		Method m2 = m.instance();
		Variable dt = new Variable("int[]", "array");
		Class c = new Class("TestClass");
		c.addDataMember(dt);
		c.addMethod(m2);
		c.print();
	}	
	private String superName_;
	private String name_;
	private List<Variable> dataMembers_;
	private List<Method> methods_;
	private int offset_;


	public Class(String superName, String name, int offset){
		superName_ = superName;
		name_ = name;
		dataMembers_ = new ArrayList<Variable>();
		methods_ = new ArrayList<Method>();
		offset_ = 0;
	}

	public Class(String superName, String name){
		superName_ = superName;
		name_ = name;
		dataMembers_ = new ArrayList<Variable>();
		methods_ = new ArrayList<Method>();
		offset_ = 0;
	}
	
	public Class(String name){
		superName_ = null;
		name_ = name;
		dataMembers_ = new ArrayList<Variable>();
		methods_ = new ArrayList<Method>();
		offset_ = 0;
	}
	
	public String getSuperName(){
		return superName_;
	}
	
	public String getName(){
		return name_;
	}
	
	public List<Variable> getDataMembers(){
		return dataMembers_;
	}

	public Variable getDataMember(String dmName) {
		for (Variable v : dataMembers_) {
			if (v.getName().equals(dmName) == true) {
				return v;
			}
		}
		return null;
	}

	public List<Method> getMethods(){
		return methods_;
	}

	public Method getMethod(String methodName){
		for (Method m : methods_){
			if (m.getName().equals(methodName) == true){
				return m;
			}
		}
		return null;
	}

	public void setSuperName(String superName){
		superName_ = superName;
	}
	
	public void setName(String name){
		name_ = name;
	}
	
	public boolean addDataMember(Variable dataMember){
		for (Variable v : dataMembers_){
			if (v.getName().equals(dataMember.getName()) == true){
				return false;
			}
		}
		return dataMembers_.add(dataMember);
	}
	
	public boolean addMethod(Method method){
		for (Method m : methods_){
			if (m.getName().equals(method.getName()) == true){
				return false;
			}
		}
		return methods_.add(method);
	} 	
	
	public String toString(){
		String dms = "class " + name_;
		if (superName_ != null) {
			dms = dms + "extends " + superName_ + " ";
		}
		dms = dms + "{\n";
		for (Variable v : dataMembers_) {
			dms = dms + "\t" +  v.toString() + ";\n";
		}
		for (Method m : methods_){
			dms = dms + "\t" +  m.toString().replace("\n", "\n\t") + "\n";
		}
		dms = dms + "}";
		return dms;
	}
	
	public boolean isSubclass(){
		if(superName_ == null) {
			return false;
		} else {
			return true;
		}
	}

	public void print(){
		System.out.println(this.toString());
	}

	public void printInfo() {
	String dms = "";
	String meths = "";

	for (Variable v : dataMembers_) {
		if (v.getOffset() >= 0) {
			dms = dms + name_ + "." + v.getName() + " : " + v.getOffset() + "\n";
		}
	}
	for (Method m : methods_) {
		if (m.getOffset() >= 0 && m.getOverloaded() == false) {
			meths = meths + name_ + "." + m.getName() + " : " + m.getOffset() + "\n";
		}	
	}
	System.out.println(dms + meths);
}
	
}
