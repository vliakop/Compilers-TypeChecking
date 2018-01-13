class testCase{

	public static void main(String[] args){
		int idas;
		System.out.println(idas);
	}
}
	
class A {
	
	int a;
	int b;
	boolean c;
	
	public int getA(){
		return a;
	}
	
	public boolean getC(){	
		return c;
	}
	
}


class B extends A{
	
	boolean a;
	int c;
	
	public int getB(){
		return b;
	}
	
	public boolean getC(){
		return false;
	}	
	
}


class C extends A {

	boolean flag;
}

class D extends C {

	boolean flag;
	int flag2;
	
	public int getD(){ return 10; }
	
	public boolean getB() { return true; }
	public int getA(){
		return 10;
	}
}

class E extends D {
	public int extremeTesting(){ return 25; }
}
