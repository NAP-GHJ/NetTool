/*start point of the NetTool*/

import java.io.File;
import java.util.Scanner;

public class StartPoint {
	public static void main(String []args){
		while(true){
		System.out.println("Input the command : (--help for usage)");
		Scanner input = new Scanner(System.in);
		String cmd = input.nextLine();
		//System.out.println(cmd);
		args = cmd.split(" ");
		
		for(int i = 0; i < args.length; i++){
			//System.out.print(args[i]+" ");
		}
		
		/*Input check*/
		if(args.length < 1){
			System.out.println("Error usage .");
			Usage usage = new Usage();
			return;
		}
		
		/*Read Command*/
		ReadCommand rd = new ReadCommand(args);
		}
	}
}
