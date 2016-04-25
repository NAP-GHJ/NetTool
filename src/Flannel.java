import java.util.Scanner;

/*Overlay Network for Docker - Flannel*/

public class Flannel {
	/*IP-Range for the cluster*/
	String ipRangeString;
	
	/*Host Info*/
	int maxSize = 10;
	int number ;
	String []hostList = new String[maxSize];
	String []eth0List = new String[maxSize];
	String []eth1List = new String[maxSize];
	
	public Flannel(){}
	
	public Flannel(String []args){
		String type = args[2];
		
		getHostInfo();
		
		etcd();
		
		addNetwork();
	}
	
	/*Get Host Info*/
	public void getHostInfo(){
		Host host = new Host();
		host.getHostInfo();
		number = host.current;
		hostList = host.nameList;
		eth0List = host.ethList0;
		eth1List = host.ethList1;
	}
	
	/*Etcd service for flannel*/
	private void etcd(){
		
		String sshString;
		String cmdString;
		Command command = null;
		
		/*First Node*/
		sshString = "ssh "+eth0List[0]+" ";
		cmdString = sshString+"etcd -listen-client-urls http://0.0.0.0:4001 -advertise-client-urls http://127.0.0.1:4001 &>/dev/null &";
		System.out.println(cmdString);
		
		command = new Command(cmdString, false);
		
		cmdString = sshString+" etcdctl rm /coreos.com/network --recursive ";
		System.out.println(cmdString);
		
		command = new Command(cmdString, false);
		 
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("Network type is flannel ,please input the ip-range of the network :");
		ipRangeString = input.nextLine();
		
		cmdString = sshString+" etcdctl mk /coreos.com/network/config '{\"Network\":\""+ipRangeString+"\"}'";
		System.out.println(cmdString);
		
		command = new Command(cmdString, false);
		
	}
	
	/*Create Flannel Network for Docker */
	private void addNetwork(){
		String sshString;
		String cmdString;
		Command command = null;
		
		for(int i = 0; i < number ; i++){
			sshString = " ssh "+eth0List[i];

			cmdString = sshString+" flanneld -iface=\"eth0\" -etcd-endpoints=\"http://"+eth0List[0]+":4001\" &>/dev/null &";
			System.out.println(cmdString);
			command = new Command(cmdString, false);
			
			cmdString = sshString+" service docker stop";
			System.out.println(cmdString);
			command = new Command(cmdString, false);
			
			cmdString = sshString+" source /run/flannel/subnet.env;ifconfig docker0 ${FLANNEL_SUBNET}";
			System.out.println(cmdString);
			command = new Command(cmdString, false);
			
			cmdString = sshString+" source /run/flannel/subnet.env;docker daemon --bip=${FLANNEL_SUBNET} --mtu=${FLANNEL_MTU} &>/dev/null &";
			command = new Command(cmdString, false);
			System.out.println(cmdString);
		}
	}
}	

