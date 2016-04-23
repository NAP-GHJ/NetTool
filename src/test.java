import java.io.*;
import java.util.*;

public class test {
	public static void main(String []args){
		try{
			/*Read Command*/
			
			String cmd = "ssh 192.168.108.131 ls ../home/ghj";
			Command command;
			
			command = new Command(cmd,true);
			
			//command = new Command("ssh 192.168.108.131 docker rm `docker ps -a -q`",true);
			
		}
		catch(Exception e){
			
		}
	}
}
