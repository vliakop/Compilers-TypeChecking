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
	
	/* Returns the last className if sth already existed, else null */	
	public boolean put(String className, Class value){
		Object obj = table_.put(className, value);
		if (obj == null) {
			return true;
		} else {
			return false;
		}
	}
}
