import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/*Add container */

public class Container {
	String nameC;
	String ipC;
	String imageC;
	String ID;
	
	public Container(){}
	public Container(String host,boolean flag){
		if(flag == false){
			defaultC(host);
		}
		else{
			optionC(host);
		}
	}
	
	/*Default add container*/
	public void defaultC(String host){
		String ip = new Host().HostIp(host);
		//System.out.println(ip);
		/*Read host-container.txt to name a container*/
		String ssh = "ssh "+ip;
		String conString = " docker run -itd --net none test bash";
		String cmd = ssh+conString;
		Command process = new Command(cmd, true);
		
	}
	
	/*Option add container*/
	public void optionC(String host){
		try{
		
		/*command*/
		Command command;
			
		/*ssh*/
		String hostIp = new Host().HostIp(host);
		String ssh = "ssh "+hostIp+" ";
		
		/*switch the network */
		String fileName = new NetTool().networkFile;
		File file = new File(fileName);
		Scanner input = new Scanner(file);
		String type = input.nextLine();
		input.close();
		
		input = new Scanner(System.in);
		
		/*Container info for each network*/
		String ipString;
		String tagString;
		String nameString;
		String networkName;
		
		String [] info;
		
		switch(type){
			case "linux-bridge":			
				do{
					System.out.println("Network type is linux-bridge ,please input the name of the container: ");
					nameString = input.nextLine();
				}
				while(!nameCheck(host, nameString));
				
				String case1 = ssh+" docker run -itd --name "+nameString+" test bash";
				command = new Command(case1, true);
				
				/*update info*/
				info = new String[2];
				info[0] = nameString;
				info[1] = getContainerIp(hostIp,nameString);
				updateHostFile(host, info);
				
				break;
			case "weave":
				do{
					System.out.println("Network type is weave ,input the ip of the container: ");
					ipString = input.nextLine();
				}
				while(!ipCheck(ipString));
				
				
				do{
					System.out.println("Please input the name of the container: ");
					nameString = input.nextLine();
				}
				while(!nameCheck(host, nameString));
				
				String cmd = ssh+" weave run "+ipString+" -itd --name "+nameString+" test bash";
				command = new Command(cmd, true);
				
				/*update info*/
				info = new String[2];
				info[0] = nameString;
				info[1] = ipString;
				updateHostFile(host, info);
				
				break;
			case "calico":
				do{
				System.out.println("Network type is calico ,input ip of the container:");
				ipC = input.nextLine();
				}
				while(!ipCheck(ipC));
				String con = ssh +" docker run -itd --net none --name "+nameC+" test bash";
				String addIp = ssh +" calicoctl container add "+nameC+" "+ipC;
				command = new Command(con, true);
				command = new Command(addIp, true);		
				break;
			case "pipework":
				
				do{
					System.out.println("Network type is pipework using ovs,please input the name of the container:");
					nameString = input.nextLine();
				}
				while(!nameCheck(host,nameString));
				do{
					System.out.println("Network type is pipework using ovs, input ip and vxlan tag of the container:");
					ipString = input.nextLine();
					tagString = input.nextLine();
				}
				while(!ipCheck(ipString));
				
				String cmd1 = ssh+" docker run -itd --net=none --name "+nameString+" test bash";
				command = new Command(cmd1, true);
				String cmd2 = ssh+" pipework ovs0 "+nameString+" "+ipString+" @"+tagString;
				System.out.println(cmd2);
				command = new Command(cmd2, true);
				
				/*Update information*/
				info = new String[3];
				info[0] = nameString;
				info[1] = ipString;
				info[2] = tagString;
				updateHostFile(host,info);  //container name,ip,tag
				break;
			case "docker-network":
				do{
					System.out.println("Network type is docker-network,input the name of the container:");
					nameString = input.nextLine();
				}
				while(!nameCheck(host,nameString));
				do{	
					System.out.println("Choose a network to join in from the follow:");
					new Network().display();
					System.out.println("Network type is docker-network, input the overlay-network to join in:");
					networkName = input.nextLine();
				}
				while(!networkNameCheck(networkName));
				
				cmd = ssh+" docker run -itd --name "+nameString+" --net "+networkName+" test bash";
				command = new Command(cmd, true);
				
				/*get the ip of the container */
				ipString = getConIp(host,nameString,networkName);
				
				/*Update information*/
				info = new String[3];
				info[0] = nameString;
				info[1] = ipString;
				info[2] = networkName;
				updateHostFile(host,info);  //container name,ip,network-name
				
				break;
			default:
				break;
		}
			
		}
		catch(Exception e){
			System.out.println("add-container option error");
		}
	}
	
	/*get con ip*/
	public String getConIp(String host,String con,String network){
		String string = null;
		try{
			String ssh = "ssh "+new Host().HostIp(host)+" ";
			String cmd = ssh+" docker inspect --format='{{ .NetworkSettings.Networks."+network+".IPAddress }}' "+con;
			System.out.println(cmd);
			Command command = new Command(cmd, false);
			BufferedReader result = command.getInro();
			
			string = result.readLine();
			return string;
		}
		catch(Exception e){
			
		}
		return string;
	}
	/*Update the host-container file*/
	public void updateHostFile(String host, String []info){
		try{
			String fileName = (new NetTool().dir1)+"/"+host;
			File file = new File(fileName);
			PrintWriter output = new PrintWriter(new FileWriter(file, true));
			for(int i = 0; i < info.length; i++){
				output.print(info[i]+" ");
			}
			output.println();
			output.close();
		}
		catch(Exception e){
			
		}
	}
	
	/*Display Container Info*/
	public void display(){
		Host host = new Host();
		host.getHostInfo();
		for(int i = 0; i < host.current; i++){
			display(host.nameList[i]);
		}
	}
	public void display(String host){
		try{
			System.out.println(host+":");
			String fileName = (new NetTool().dir1)+"/"+host;
			File file = new File(fileName);
			Scanner input = new Scanner(file);
			while(input.hasNext()){
				System.out.println("    -- "+input.nextLine());
			}
			input.close();
			System.out.println();
		}
		catch(Exception e){
			
		}
	}
	
	/*Check if the name has been used*/
	boolean nameCheck(String host,String name){
		try{
			String fileName = (new NetTool().dir1)+"/"+host;
			File file = new File(fileName);
			Scanner input = new Scanner(file);
			while(input.hasNext()){
				String temp = input.nextLine();
				String []token = temp.split(" ");
				if(token[0].equalsIgnoreCase(name)){
					System.out.println("the name has been used , please reset the name");
					input.close();
					return false;
				}
			}
			input.close();
		}
		catch(Exception e){
			
		}
		return true;
	}
	
	/*Check if the ip has been used*/
	boolean ipCheck(String ip){
		Host host = new Host();
		host.getHostInfo();
		for(int i = 0; i < host.current; i++){
			if( ipCheck(host.nameList[i],ip) == false){
				return false;
			}
		}
		return true;
	}
	boolean ipCheck(String host,String ip){
		try{
			
			String fileName = (new NetTool().dir1)+"/"+host;
			File file = new File(fileName);
			Scanner input = new Scanner(file);
			while(input.hasNext()){
				String temp = input.nextLine();
				String []token = temp.split(" ");
				if(token[1].equalsIgnoreCase(ip)){
					System.out.println("the ip has been used , please reset the ip");
					input.close();
					return false;
				}
			}
			input.close();
		}
		catch(Exception e){
			
		}
		return true;
	}
	
	/*get container ip*/
	public String getContainerIp(String hostIp, String name){
		String ip,cmd;
		Command command;
		cmd = "ssh "+hostIp+" docker inspect --format='{{.NetworkSettings.IPAddress}}' "+name;
		command = new Command(cmd, true);
		ip = command.firstLine;
		return ip;
	}
	
	/*network name check*/
	private boolean networkNameCheck(String name){
		return new Network().networkNameCheck(name);
	}
}
