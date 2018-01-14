package cgeneration;

public class StringManager{
	
	public String accumulator_;
	public String declarations_ = "declare i8* @calloc(i32, i32)\ndeclare i32 @printf(i8*, ...)\ndeclare void @exit(i32)\n\n@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\ndefine void @print_int(i32 %i) {\n\t %_str = bitcast [4 x i8]* @_cint to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n\tret void\n}\n\ndefine void @throw_oob() {\n\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str)\n\t call void @exit(i32 1)\n\tret void\n}\n\n";
	
	public String define_ = "define #r @#fname(#args){\n#body\n}\n\n";
	public String ret_ = "\tret #type %#reg\n";
	public String alloca_ = "\t#optional alloca #type\n";
	public String store_ = "\tstore #type #source, #type* #target\n";
	public String load_ = "\tload #type, #type* %#sourceptr\n";
	public String call_ = "\tcall #ret @#fname(#args)\n";
	public String add_ = "\tadd #type #value1, #value2\n";
	public String sub_ = "\tsub #type #value1, #value2\n";
	public String mul_ = "\tmul #type #value1, #value2\n";
	public String and_ = "\tand #type #value1, #value2\n";
	public String xor_ = "\txor #type #value1, #value2\n"; 
	public String icmpSlt_ = " icmp slt #type #value1, #value2\n";
	public String br2labels_ = "\tbr i1 %#caseReg, label %#labelIf, label %#labelElse\n";
	public String br1label_ = "\tbr label %#goto\n";
	public String label_ = "label#:\n";
	public String bitcast_ = " bitcast #typeFrom %#reg to #typeTo";
	public String global_ = "@.#name = global [#size x #type] [#declarations]\n";

	private int labelCounter_;
	private int registerCounter_;
	private String tag_;	// label already taken
	private String register_;

	/* Default strings */

	
	public StringManager(){
		accumulator_ = "";
		labelCounter_ = 1;
		registerCounter_ = 1;
		tag_ = "label";
		register_ = "%_";
	}

	public String getLabel(){
		String retVal = tag_ + String.valueOf(labelCounter_) + ":";
		labelCounter_++;
		return retVal;
	}

	public String getRegister(){
		String retVal = register_ + String.valueOf(registerCounter_);
		registerCounter_++;
		return retVal;
	}

	public void reset(){
		labelCounter_ = 1;
		registerCounter_ = 1;
		return;
	}

}
