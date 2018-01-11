package tcpackage;

public class Variable{
		
	public static void main(String[] args){
		Variable v = new Variable("v", "String");
		v.print();
	}
	
	private String name_;
	private String type_;
	private int offset_;	
	private boolean overloaded_;
	
	public Variable(String name, String type){
		name_ = name;
		type_ = type;
		offset_ = -1;
		overloaded_ = false;
	}
	
	public String getName(){
		return name_;
	}
	
	public String getType(){
		return type_;
	}
	
	public int getOffset(){
		return offset_;
	}

	public boolean getOverloaded(){
		return overloaded_;
	}	

	public void setName(String name){
		name_ = name;
	}
	
	public void setType(String type){
		type_ = type;
	}

	public void setOffset(int offset){
		offset_ = offset;	
	}
	
	public void setOverloaded(boolean overload){
		overloaded_ = overload;	
	}
	
	public void print(){
		System.out.println("var:" + name_ + "(" + type_ + ")");
	}
	
	public String toString(){
		return type_ + " " + name_ + ":" + offset_; 
	}
}
