package tcpackage;

import java.util.*;
import cgeneration.*;

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

	public void printInfo(){
		for (Map.Entry<String, Class> e : table_.entrySet()){
			e.getValue().printInfo();
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
					System.exit(1);
				}
				while (pcls != null) {
				/*	boolean all = allDifferentVars(cls.getDataMembers(), pcls.getDataMembers());
					if (all == false) {
						System.out.print("Redeclaring already-existent datamembers in class " + cls.getName() + " from baseclass " + pcls.getName());
						System.exit(1);
					}
				*/
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
		if (type1 == null || type2 == null) {
			System.out.println("Argument compatibility check error");
			System.exit(3);
		}
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

	public Method getMethodFromClass(String methodName, String className) {

		Class cls = this.getClass(className);
		if (cls == null) {
			return null;
		}

		Method m = cls.getMethod(methodName);
		if (m != null) {
			return m;
		} else {
			Class pcls = this.getClass(cls.getSuperName());
			while (pcls != null) {
				m = pcls.getMethod(methodName);
				if(m != null) {
					return m;
				}
				pcls = this.getClass(pcls.getSuperName());
			}
			return null;
		}
	}

		/* Misleading name - it actually returns the type of varName given
		* varName could be a Type, in which case the type is returned with no further checks
		*/
	public String IdentifierToType(String vClass, String vMethod, String varName) {
		if(varName == null) {
			System.out.println("ID2TypeError");
			System.exit(2);
		}
		if (this.primitive(varName) == true || this.containsKey(varName) == true) {
			return varName;
		}
		if (vClass != null && vMethod != null) {	// Check the current method of the class that is being examined
			if (varName.equals("this") == true) {
				return vClass;
			}
			Class cls = this.getClass(vClass);
			if (cls == null) {
				System.out.println("Class " + vClass + " was not identified.");
				System.exit(2);
			}
			Method m = cls.getMethod(vMethod);
			if (m == null) {
				System.out.println("Method " + vMethod + " of class " + vClass + " was not identified.");
				System.exit(2);
			}
			Variable var = m.getParameter(varName);
			if (var != null) {
				return var.getType();
			} else {
				var = m.getLocalVariable(varName);
				if (var != null) {
					return var.getType();
				} else {
					var = cls.getDataMember(varName);
					if (var == null) {
						if (cls.isSubclass() == true) {
							cls = this.getClass(cls.getSuperName());
							while (cls != null) {
								var = cls.getDataMember(varName);
								if (var != null) {
									return var.getType();
								} else {
									cls = this.getClass(cls.getSuperName());
								}
							}
							return null;
						} else {
							return null;
						}
					} else {
						return var.getType();
					}
				}
			}
		}
		return null;
	}	

	/* Useless for the code generation part */
	public int fieldMaxOffset(String className) {

		int max = -1;
		Class cls = this.getClass(className);
		if (cls == null){
			return max;
		}
		List<Variable> dms = cls.getDataMembers();
		int size = dms.size();
		if (size > 0) {
			try{
				max = dms.get(size - 1).getOffset();
			} catch (IndexOutOfBoundsException e){
				System.out.println(e);
			}
			
		} else {
			Class pcls = this.getClass(cls.getSuperName());
			if (pcls == null) {	// a class without data members
				return 0;
			}
			while(pcls != null) {
				dms = pcls.getDataMembers();
				size = dms.size();
				if (size > 0) {
					max = dms.get(size - 1).getOffset();
					break;
				}
				pcls = this.getClass(pcls.getSuperName());
			}
		}
		return max;
	}

	/* builds the classesinfo class */

	public void buildClassesInfo(ClassesInfo ci) {

		for (Map.Entry<String, Class> entry : table_.entrySet()) {

			Class cls = entry.getValue();
			List<Variable> dms = cls.getDataMembers();
			int size = dms.size();

			ObfuscatedClass oc = new ObfuscatedClass(cls.getSuperName(), cls.getName());
			/* For every class and superclass: run the datamembers list from bottom to top, adding every element
				at the beginning of the obfuscated variable list
			*/
			List<Variable> ocDms = oc.dataMembers_;
			for (int i = size -1; i >= 0; i--) {
				ocDms.add(0, dms.get(i));
			}
			Class pcls = this.getClass(oc.superName_);
			while(pcls != null) {
				dms = pcls.getDataMembers();
				size = dms.size();
				for (int i = size - 1; i >= 0; i--) {
					ocDms.add(0, dms.get(i));
				}
				pcls = this.getClass(pcls.getSuperName());
			}
			int fieldBytes = 0;
			for (Variable v : ocDms) {
				String type = v.getType();
				if (type.equals("boolean") == true) {
					fieldBytes = fieldBytes + 1;
				} else if (type.equals("int") == true) {
					fieldBytes = fieldBytes + 4;
				} else {
					fieldBytes = fieldBytes + 8;
				}
			}
			oc.fieldBytes_ = fieldBytes;
//			System.out.println("oc with name " + oc.name_ + " has " + oc.dataMembers_.size()  + " different fields");
			//System.out.println("class: " + oc.name_ + "--> :" + ocDms.size() + " and total bytes of " + oc.fieldBytes_);

			/* Now building the obfuscated methods */
			List<Method> meths = cls.getMethods();
			List<Method> ocMeths = oc.methods_;
//			System.out.println("st::buildCG. Class '" + cls.getName() + "' has " + meths.size() + " methods");
			/* Find the maxMethodOffset to calculate the # of methods in a class */
			int maxMethodOffset = 0;
			for (Method m : meths) {
//				System.out.println("st::buildCG2. " + m.getName() + " " + m.getReturnType() + " with " + m.getParameters().size() + " parameters");
				if (m.getOffset() > maxMethodOffset) {
					maxMethodOffset = m.getOffset();
				}
			}
			pcls = this.getClass(oc.superName_);
			while (pcls != null) {
				meths = pcls.getMethods();
				for (Method m : meths) {
					if (m.getOffset() > maxMethodOffset) {
						maxMethodOffset = m.getOffset();
					}
				}
				pcls = this.getClass(pcls.getSuperName());
			}
			int no = maxMethodOffset/8 + 1;
			//System.out.println("class: " + oc.name_ + " has " + no + " method(s) \n");

			oc.methodBytes_ = no*8;
			boolean found;
			meths  = cls.getMethods();
			for (int i = 0; i < no; i++) {
				found = false;
				meths  = cls.getMethods();
				for (Method m : meths) {
					if (i*8 == m.getOffset()){
						ocMeths.add(m);
						oc.methodInClass_.add(cls.getName());
						found = true;
						break;
					}
				}
				if (found == false) {
					//System.out.println("I will search in parent meths");
					pcls = this.getClass(oc.superName_);
					while (pcls != null && found == false) {
						meths = pcls.getMethods();
						for (Method m : meths) {
							if (i*8 == m.getOffset()) {
								ocMeths.add(m);
								oc.methodInClass_.add(pcls.getName());
								found=  true;
								break;
							}
						}
						pcls = this.getClass(pcls.getSuperName());
					}
				}
				//System.out.println("Method " + ocMeths.get(i).getName() + " of class " + cls.getName() + " resides in class " + oc.methodInClass_.get(i) + "\n");
			}	// for every method in the class
//			System.out.println("oc with name " + oc.name_ + " has " + oc.methods_.size()  + " different methods");
			ci.table_.put(oc.name_, oc);
		}	// For every class in the st::Map
	}

}	/* END OF CLASS */


