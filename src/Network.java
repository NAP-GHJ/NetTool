import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Network {
	public Network(){}
	public Network(String type){
		try{
			NetTool netTool = new NetTool();
			File file = new File(netTool.networkFile);
			PrintWriter printWriter = new PrintWriter(new FileWriter(file,true));
			Scanner input = new Scanner(file);
			if(!input.hasNext()){
				printWriter.println(type);
			}
			input.close();
			printWriter.close();
		}
		catch(Exception e){
			
		}
	}
	
	/*Display the network information*/
	public void display(){
		try{
			NetTool netTool = new NetTool();
			File file = new File(netTool.networkFile);
			Scanner input = new Scanner(file);
			System.out.print("Network type is ");
			while(input.hasNext()){
				System.out.println(input.nextLine());
			}
			input.close();
		}
		catch(Exception e){
			
		}
	}
	
	/*check network name*/
	public boolean networkNameCheck(String name){
		try{
			NetTool netTool = new NetTool();
			File file = new File(netTool.networkFile);
			Scanner input = new Scanner(file);
			while(input.hasNext()){
				String temp = input.nextLine();
				if(name.equalsIgnoreCase(temp)){
					return true;
				}
			}
			input.close();
		}
		catch(Exception e){
			
		}
		return false;
	}
}
