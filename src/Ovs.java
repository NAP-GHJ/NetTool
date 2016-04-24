import java.util.Scanner;

/*Network using ovs and GRE for Docker Container*/
public class Ovs {
	
	int number;
	String []hostList ;
	String []eth0List ;
	String []eth1List ;
	
	String []br0List;
	String []iprangeList;
	
	public Ovs(){}
	
	public Ovs(String [] args){
		/*get host infomation first*/
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
	
	/*default network:connect all host*/
	private void defaultNetwork(){
		
		commonCmd();
		
		String cmdString;
		String sshString;
		Command command = null ;
		
		for(int i = 0; i < number ; i++){
			sshString = "ssh "+eth0List[i]+" ";
			for(int j = 0; j < number ; j++){
				if(i!=j){
				cmdString = sshString+" ovs-vsctl add-port ovs0 gre"+i+j
						+" -- set interface gre"+i+j+" type=gre options:remote_ip="
						+eth0List[j];
				command = new Command(cmdString, false);
				}
			}
		}
	}
	
	/*option network:connect some host*/
	private void optionNetwork(){
		commonCmd();
		System.out.println("Network type is ovs,please input connections: [host-1] [host-2],end with #");
		
		Scanner input = new Scanner(System.in);
		while(input.hasNext()){
			
		}
	}
	
	/*common cmd between default and option network*/
	private void commonCmd(){
		String cmd;
		String ssh;
		Command command;
		
		for(int i = 0; i < number; i++){
			ssh = "ssh "+eth0List[i]+" ";
			
			cmd = ssh+" ovs-vsctl add-br ovs0";
			command = new Command(cmd, false);
			System.out.println(cmd);
			
			br0List[i] = "10.0."+(i+1)+".0/16";
			iprangeList[i] = "10.0."+(i+1)+".0/24";
			
			cmd = ssh +" brctl addbr br0;"+" ifconfig br0 "+br0List[i]+" up";			
			command = new Command(cmd, false);
			System.out.println(cmd);
			
			cmd = ssh+" ovs-vsctl add-port ovs0 br0";
			command = new Command(cmd, false);
			System.out.println(cmd);
			
			cmd = ssh+" service docker stop";
			command = new Command(cmd, false);
			System.out.println(cmd);
			
			cmd = ssh+" docker daemon -b=br0 --fixed-cidr="+iprangeList[i]+" &>/dev/null &";
			command = new Command(cmd, false);
			System.out.println(cmd);
		}
	}
	
	/*add-connection between two hosts */
	private void addConnect(){
		
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
}
