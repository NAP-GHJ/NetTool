/*linux-bridge network for docker container*/

import java.util.Scanner;

public class LinuxBridge {
	
	int number;
	String []hostList ;
	String []eth0List ;
	String []eth1List ;
	
	String []br0List;
	String []iprangeList;
	
	/*linux bridge*/
	public LinuxBridge(String []args){
		getHostInfo();
		
		String temp = args[2];
		if(temp.equalsIgnoreCase("default")){
			defaultNetwork();
		}
		else if(temp.equalsIgnoreCase("option")){
			optionNetwork();
		}
		else{
			System.out.println("Error usage .");
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
		
		br0List = new String[number];
		iprangeList = new String[number];
	}
	
	/*default network*/
	public void defaultNetwork(){
		for(int i = 0; i < number; i++){
			br0List[i] = "10.0."+(i+1)+".0/16";
			iprangeList[i] = "10.0."+(i+1)+".0/24";
		}
		execCmd();
	}
	
	/*option network*/
	public void optionNetwork(){
		System.out.println("Please input the ip-range of each host");
		System.out.println("only the ip-range is in the same ip-range can the container connects with each other");
		
		Scanner input = new Scanner(System.in);
		
		for(int i = 0; i < number; i++){
			System.out.println("Input the ip of bridge br0 for "+hostList[i]);
			String temp = input.nextLine();
			br0List[i] = temp;
			System.out.println("Input the ip range for "+hostList[i]);
			temp = input.nextLine();
			iprangeList[i] = temp;
		}
		
		/*exec cmd*/
		execCmd();
		//writeFile();
	}
	
	/*Exec cmd*/
	public void execCmd(){
		Command command ;
		
		for(int i = 0; i < number ; i++){
			String ssh = "ssh "+eth0List[i]+" ";
			String cmd1 = ssh+" brctl addbr br0;ifconfig br0 "+br0List[i]+" up;brctl addif br0 eth1";
			String cmd2 = ssh+" service docker stop; docker daemon -b=br0 --fixed-cidr=\""+iprangeList[i]+"\" &>/dev/null &";
			System.out.println(cmd1);
			System.out.println(cmd2);
			
			command = new Command(cmd1, false);
			command = new Command(cmd2, false);
			
				
		}
	}
	
}	
