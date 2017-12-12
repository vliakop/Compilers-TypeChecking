package tcpackage;

import java.util.*;

public class SymbolTable{

	private Map<String, Class> table_;
	
	public SymbolTable(){
		table_ = new HashMap<String, Class>();
	}
	
	public Class getClass(String className){
		return table_.get(className);
	}
		
	public boolean containsKey(String className){
		return table_.containsKey(className);
	}
	
	public boolean put(String className){
		Object obj = table_.put(className, new Class(className));
		if (obj == null) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean put(String baseClass, String className){
		Object obj = table_.put(className, new Class(baseClass, className));
		if (obj == null) {
			return true;
		} else {
			return false;
		}
	}
	
	public void print(){
		for (Map.Entry<String, Class> e : table_.entrySet()){
			e.getValue().print();
		}
	}

	/* checks for datamember and no method overloading in subclasse: ensures that the parent class exists */
	public boolean subclassChecks(){

		for (Map.Entry<String, Class> entry : table_.entrySet()){
			Class cls = entry.getValue();
			if (cls.isSubclass() == true){	// if it's a subclass
				String superName = cls.getSuperName();
				Class pcls = this.getClass(superName); //  Get the first parent class
				if (pcls == null || cls.getName().equals(superName) == true){
					System.out.println("FirstParent TypeCheck Error");
					return false;
				}
				while (pcls != null) {
					boolean all = allDifferentVars(cls.getDataMembers(), pcls.getDataMembers());
					if (all == false) {
						System.out.print("Redeclaring already-existent datamembers in class " + cls.getName() + " from baseclass " + pcls.getName());
						System.exit(1);
					}
					boolean overload = noOverloading(cls.getMethods(), pcls.getMethods());
					if (overload == false) {
						System.out.println("In class " + cls.getName() + " cannot overload methods previously declared in class " + pcls.getName());
						System.exit(1);
					}
					if(pcls.isSubclass() == true) {
						pcls = this.getClass(pcls.getSuperName());
						if(pcls == null) {
							System.out.println("SubsequentParent TypeCheck Error");
							return false;
						}
					} else {
						break;
					}
				}
			}
		}
		return true;
	}

	public boolean allDifferentVars(List<Variable> childDataMembers, List<Variable> parentDataMembers){
		for (Variable v : childDataMembers) {
			for (Variable pv : parentDataMembers) {
				if(v.getName().equals(pv.getName()) == true) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean noOverloading(List<Method> childMethods, List<Method> parentMethods){
		for (Method m : childMethods) {
			for (Method pm : parentMethods) {
				if (m.getName().equals(pm.getName()) == true) { //  Same name methods : check return type and parameter list
					if (m.getReturnType().equals(pm.getReturnType()) == false) {	//  Ensure => Same Return Type
						return false;
					}
					if(m.parametersToString().equals(pm.parametersToString()) == false) { //  Ensure => Same Argument List
						return false;
					}
				}
			}
		}
		return true;
	}


	public boolean compatible (String type1, String type2) {
		if (type1.equals("int []") && type2.equals("int []")) {
			return true;
		} else if (type1.equals("int") && type2.equals("int")) {
			return true;
		} else if (type1.equals("boolean") && type2.equals("boolean")) {
			return true;
		} else if (primitive(type1) == false && primitive(type2) == false){
			if (type1.equals(type2) == true) {
				return true;
			} else {
				Class cls = this.getClass(type2);
				cls = this.getClass(cls.getSuperName());
				while (cls != null) {
					if (cls.getName().equals(type1) == true) {
						return true;
					} else {
						cls = this.getClass(cls.getSuperName());
					}
				}
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean primitive(String type) {
		if (type.equals("int") == true) {
			return true;
		} else if (type.equals("boolean") == true) {
			return true;
		} else if (type.equals("int []") == true){
			return true;
		}
		return false;
	}

}	/* END OF CLASS */


