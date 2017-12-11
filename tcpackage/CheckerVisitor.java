package tcpackage;

import java.io.*;
import java.util.*;
import java.lang.*;
import visitor.*;
import syntaxtree.*;

public class CheckerVisitor extends GJDepthFirst<String, String> {
	
	private SymbolTable symbolTable_;
	private String visitingClass_;
	private String visitingMethod_;

	public CheckerVisitor(SymbolTable symbolTable) {
		symbolTable_ = symbolTable;
		visitingClass_ = null;
		visitingMethod_ = null;
		if (symbolTable_.subclassChecks() == false) {
			System.exit(2);
		}
	}

}