import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;


public class DockerNetwork {
	
	int number;
	String []hostList ;
	String []eth0List ;
	String []eth1List ;
	
	public DockerNetwork(){
		
	}
	public DockerNetwork(String []args){
		/*get host info */
		getHostInfo();
		
		/*start consul service first and docker daemon first*/
		String ssh;
		String cmd;
		Command command;
		String nameString;
		if(firstNetowrk()){
			for(int i = 0; i < number ; i++){
				ssh = "ssh "+eth0List[i]+" ";
				cmd = ssh+"docker daemon -H tcp://0.0.0.0:2376 -H unix:///var/run/docker.sock --cluster-store=consul://192.168.108.131:8500 --cluster-advertise=eth0:2376";
				System.out.println(cmd);
				command = new Command(cmd, false);
			}
			ssh = "ssh "+eth0List[0]+" ";
			cmd = ssh+" docker run -d -p \"8500:8500\" -h \"consul\" progrium/consul -server -bootstrap";
			System.out.println(cmd);
			command = new Command(cmd,true);
		
		}
		/*create network*/
		ssh = "ssh "+eth0List[0]+" ";
		do{
			Scanner input = new Scanner(System.in);
			System.out.println("Please input the name of the network:");
			nameString = input.nextLine();
		}
		while(!nameCheck(nameString));
		cmd = ssh+" docker network create -d overlay "+nameString;
		command = new Command(cmd, true);
		
		/*update network file*/
		updateInfo(nameString);
	}
	
	private boolean nameCheck(String name){
		try{
			NetTool netTool = new NetTool();
			File file = new File(netTool.networkFile);
			Scanner input = new Scanner(file);
			while(input.hasNext()){
				String temp = input.nextLine();
				if(name.equalsIgnoreCase(temp)){
					System.out.println("The name has used for another network");
					return false;
				}
			}
			input.close();
			
		}catch(Exception e){
			
		}
		return true;
	}
	
	private boolean firstNetowrk(){
		try{
			NetTool netTool = new NetTool();
			File file = new File(netTool.networkFile);
			Scanner input = new Scanner(file);
			int i = 0;
			while(input.hasNext()){
				input.nextLine();
				i++;
			}
			input.close();
			if(i > 1)
				return false;
		}catch(Exception e){
			
		}
		return true;
	}
	
	private void updateInfo(String name){
		try{
			NetTool netTool = new NetTool();
			File file = new File(netTool.networkFile);
			PrintWriter printWriter = new PrintWriter(new FileWriter(file,true));
			printWriter.println(name);		
			printWriter.close();
		}
		catch(Exception e){
			
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
}
