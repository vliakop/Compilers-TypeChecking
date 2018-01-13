package cgeneration;

import java.util.*;
import tcpackage.*;

/* Keeps info on all  data members
	and  methods: inheritanced or not
*/

public class ObfuscatedClass{
	
	public String superName_;
	public String name_;
	public int fieldBytes_;
	public List<Variable> dataMembers_;
	public int methodBytes_;
	public List<Method> methods_;
	public List<String> methodInClass_;

	public ObfuscatedClass(String superName, String name){
		superName_ = superName;
		name_ = name;
		fieldBytes_ = 0;
		dataMembers_ = new ArrayList<Variable>();
		methodBytes_ = 0;
		methods_ = new ArrayList<Method>();
		methodInClass_ = new ArrayList<String>();
	}




}