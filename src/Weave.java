/*Weave network for Docker Containers*/

import java.io.PrintWriter;
import java.util.Scanner;

public class Weave {
	
	/*Information needed*/
	int number;
	String []hostList ;
	String []eth0List ;
	String []eth1List ;
	
	String []iprangeList;
	
	/*Weave*/
	public Weave(){}
	public Weave(String[]args){
		String type = args[2];
		getHostInfo();
		if(type.equalsIgnoreCase("default")){
			defaultNetwork();
		}
		else if(type.equalsIgnoreCase("option")){
			optionNetwork();
		}
		else{
			System.out.println("Error usage.");
			Usage usage = new Usage("Network");
		}
	}
	
	/*get host info*/
	public void getHostInfo(){
		Host host = new Host();
		host.getHostInfo();
		number = host.current;
		hostList = host.nameList;
		eth0List = host.ethList0;
		eth1List = host.ethList1;
	}
	
	/*default network*/
	public void defaultNetwork(){
		
		for(int i = 0; i < number; i++){
			String ssh = "ssh "+eth0List[i]+" ";
			String cmd;
			Command command;
			if(i == 0){
				cmd = ssh+" weave launch";
			}
			else{
				cmd = ssh+" weave launch "+eth0List[i-1];
			}
			command = new Command(cmd, false);
			System.out.println(cmd);
		}
	}
	
	/*option*/
	public void optionNetwork(){
		String sshString;
		String cmdString;
		Command command;
		for(int i = 0; i < number ; i++){
			sshString = "ssh "+eth0List[i]+" ";
			cmdString = sshString + " weave launch";
			command = new Command(cmdString, false);
			System.out.println(cmdString);
		}
		String connectionString;
		Scanner input = new Scanner(System.in);
		while(true){
			System.out.println("Network type is weave [option], connect two host like : [host-1] [host-2]\nend with a line with symbol #");
			connectionString = input.nextLine();
			if(connectionString.equalsIgnoreCase("#"))
				break;
			String [] token = connectionString.split(" ");
			String ip1 = new Host().HostIp(token[0]);
			String ip2 = new Host().HostIp(token[1]);
			cmdString = "ssh "+ip1+" weave connect "+ip2;
			command = new Command(cmdString, false);
			System.out.println(cmdString);
		}
	}
}
