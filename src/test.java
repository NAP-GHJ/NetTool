import java.io.*;
import java.util.*;

public class test {
	public static void main(String []args){
		try{
			/*Read Command*/
			
			String cmd = "ssh 192.168.108.131 docker daemon -b=br0 --fixed-cidr=10.0.1.0/24";
			Command command;
			
			command = new Command(cmd,false);
			
			//command = new Command("ssh 192.168.108.131 docker rm `docker ps -a -q`",true);
			
		}
		catch(Exception e){
			
		}
	}
}
