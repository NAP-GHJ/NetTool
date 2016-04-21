/*system start stop and restart*/

import java.io.BufferedReader;
import java.io.File;

public class NetTool {
	
	String directory = "/home/ghj/netToolFile/";
	String dir1 = directory+"networkHost/";
	String dir2 = directory+"hostContainer/";
	String networkFile = directory+"network.txt";
	String hostFile = directory+"hosts.txt";
	
	/*cluster information*/
	int number;
	String []hostList ;
	String []eth0List ;
	String []eth1List ;

	/*conduct function*/
	public NetTool(){}
	public NetTool(String []args){
		
		if(args.length < 2){
			System.out.println("Error usage .");
			Usage usage = new Usage("System");
			return;
		}	
		
		String type = args[1];
		if(type.equalsIgnoreCase("start")){
			start();
		}
		else if(type.equalsIgnoreCase("stop")){
			stop();
		}
		else if(type.equalsIgnoreCase("restart")){
			restart();
		}
		else{
			System.out.println("Error usage .");
			Usage usage = new Usage("System");
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
	
	/*Start the system*/
	public void start(){
		/*Init config file*/
		String cmd = "rm -r "+directory;
		Command command = new Command(cmd, false);
		
		/*mk dir*/
		File parent = new File(directory);
		parent.mkdirs();
		/*mk dir1*/
		File child = new File(dir1);
		child.mkdirs();
		/*mk dir2*/
		File child2 = new File(dir2);
		child2.mkdirs();
		
		/*rm hosts.txt and network.txt*/
		File hosts = new File(hostFile);
		hosts.delete();
		File network = new File(networkFile);
		network.delete();
	}
	
	/*Stop the system*/
	public void stop(){
		/*Get host info*/
		getHostInfo();
		
		Command command;
		String cmd;
		
		/*rm dir*/
		cmd = "rm -r "+directory;
		command = new Command(cmd, false);
		
		/*shut down hosts*/
		for(int i = 0; i < number ; i++){
			String ssh = "ssh "+eth0List[i]+" ";
			/*docker container stop and remove*/
			dockerInit(ssh);
			
			/*linux bridge*/
			bridgeInit(ssh);
			
			/*ovs bridge*/
			ovsInit(ssh);
			
			/*reboot the host*/
			cmd = ssh+" reboot";
			System.out.println(cmd);
			command = new Command(cmd, false);
		}
	}
	
	/*system restart*/
	public void restart(){
		stop();
		start();
	}
	
	/*docker init*/
	public void dockerInit(String ssh){
		try{
		System.out.println("Init docker environment...");
		Command command ;
		String cmdString;
		
		/*Remove all the container*/
		cmdString = ssh+" docker stop `docker ps -a -q`";
		command = new Command(cmdString, false);
		System.out.println(cmdString);
		
		cmdString = ssh+" docker rm `docker ps -a -q`";
		command = new Command(cmdString, true);
		System.out.println(cmdString);
		}
		catch(Exception e){
			
		}
	}
	
	/*delete created ovs bridge*/
	public void ovsInit(String ssh){
		System.out.println("Init ovs bridges...");
		Command command ;
		int maxSize = 5;
		for(int i = 0; i < maxSize; i++){
			String dockerProcess = ssh+" ovs-vsctl del-br ovs"+i;
			command = new Command(dockerProcess,false);
		}
		
		/*socketplane*/
		String socketplane = ssh+" ovs-vsctl del-br docker0-ovs ";
		command = new Command(socketplane, false);
	}
	public void bridgeInit(String ssh){
		System.out.println("Init linux bridges...");
		Command command ;
		int maxSize = 5;
		for(int i = 0; i < maxSize; i++){
			String cmd = ssh+" ifconfig br"+i+" down";
			command = new Command(cmd, false);
			cmd = ssh+" brctl delbr br"+i;
			command = new Command(cmd,false);
		}
	}
}
