package cgeneration;

public class MethodToString{
	
	/* This class stores string info on a method definition */
	public String methodName_;	//  eg Fac.Factorial
	public String methodDefinitionTemplate_;	// any LLVM code produced for this method. followed by a bodyTemplate_ string
	public String bodyTemplate_;	//  eg #bodyFac.Factorial

	public MethodToString(String a, String b, String c) {
		methodName_ = a;
		methodDefinitionTemplate_ = b;
		bodyTemplate_ = c;
	}


}