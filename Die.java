import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Die{
	
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			//File file = new File(arg);
			FileInputStream fis = null;
		//	try {
				//fis = new FileInputStream(file);
				String nameMatch = arg.split(".java")[0];
				System.out.println(nameMatch);
				System.out.println("Succesful type checking");
			// } catch (IOException e) {
			// 	System.out.println(e);
			// }
		
		}
		return;
	}

} 