package tcpackage;

import java.util.*;

public class SymbolTable{

	private Map<String, Class> table_;
	
	public SymbolTable(){
		table_ = new HashMap<String, Class>();
	}
	
	public boolean containsKey(String className){
		return table_.containsKey(className);
	}
	
	public Object put(String className, Class value){
		return table_.put(className, value);	
	}
}
