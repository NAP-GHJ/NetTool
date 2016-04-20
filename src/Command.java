import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Command {
	
	/*Data Structure*/
	String command;
	
	String firstLine;

	BufferedReader result;
	
	public Command(String cmd,boolean flag){
		command = cmd;
		
		if(flag == true){
			executeTrue();
		}
		else {
			executeFalse();
		}
	}
	
	/*Execute but not display the result*/
	public void executeFalse(){
		try{
			Process process = null;
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
			if(process == null){
				System.out.println("process error");
			}
			BufferedReader br = new BufferedReader
					(new InputStreamReader(process.getInputStream()));
			result = br;
		}
		catch(Exception e){
			System.out.println("Exception when executing command");
		}
	}
	
	/*Execute and display the result*/
	public void executeTrue(){
		try{
			Process process = null;
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
			if(process == null){
				System.out.println("process error");
			}
			BufferedReader br = new BufferedReader
					(new InputStreamReader(process.getInputStream()));
			result = br;
			String line;
			int first = 0;
			while( (line = br.readLine())!= null){
				if(first == 0){
					firstLine = line;
					first = 1;
				}
				System.out.println(line+"ghj");
			}
		}
		catch(Exception e){
			System.out.println("Exception when executing command");
		}
	}
	
	/*Get Result :some important info*/
	public BufferedReader getInro(){
		return result;
	}
}
