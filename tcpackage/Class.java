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
	
	public Class(String superName, String name){
		superName_ = superName;
		name_ = name;
		dataMembers_ = new ArrayList<Variable>();
		methods_ = new ArrayList<Method>();
	}
	
	public Class(String name){
		superName_ = null;
		name_ = name;
		dataMembers_ = new ArrayList<Variable>();
		methods_ = new ArrayList<Method>();
	}
	
	public String getSuperName(){
		return superName_;
	}
	
	public String getName(){
		return name_;
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
		return dataMembers_.add(dataMember);
	}
	
	public boolean addMethod(Method method){
		if (methods_.contains(method) == true) {
			return false;
		} else {
			methods_.add(method);
			return true;
		}
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
	
	public void print(){
		System.out.println(this.toString());
	}	
}
