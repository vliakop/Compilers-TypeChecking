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
}
