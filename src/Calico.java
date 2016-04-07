import java.util.Scanner;

/*Calico Network*/

public class Calico {
	
	String ipPool;
	
	static int count = 0;
	
	int number;
	String []hostList ;
	String []eth0List ;
	String []eth1List ;
	
	public Calico(){}
	
	public Calico(String []args){
		/*Start the etcd service first*/
		Etcd etcd = new Etcd();
		
		int  ij= 1;
		if(ij == 1)
		 return;
		/*Get Host Info*/
		getHostInfo();
		
		/*Start Calico On Each Node*/
		String ssh;
		String cmd;
		Command command;
		for(int i = 0; i < number ; i++){
			ssh = "ssh "+eth0List[i]+" ";
			cmd = ssh+" calicoctl node";
			command = new Command(cmd,true);
			if( i == 0)
				command = new Command(cmd,true);
			System.out.println(cmd);
		}
		
		/*Config the ip-pool*/
		System.out.println("Set the ip-pool:");
		Scanner input = new Scanner(System.in);
		ipPool = input.nextLine();
		cmd = "ssh "+eth0List[0]+" calicoctl pool add "+ipPool+" --ipip --nat-outgoing";
		command = new Command(cmd,false);
		
	}
	
	/*Add-connect*/
	public void addConnect(){
		System.out.println("Network type is calico ,add connect-relation between containers");
		System.out.println("[host-name]:[con-name] [host-name]:[con-name]");
		Scanner input = new Scanner(System.in);
		String connection = input.nextLine();
		String fileName = "profile"+count;
		count ++;
		
		Command command;
		String [] token = connection.split(" ");
		for(int i = 0; i < token.length; i++){
			String []hostCon = token[i].split(":");
			String ssh = "ssh "+(new Host().HostIp(hostCon[0]));
			String cmd;
			if(i == 0){
				
				cmd = ssh+" calicoctl profile add "+fileName;
				System.out.println(cmd);
				command = new Command(cmd, true);
			}
			cmd = ssh +" calicoctl container "+hostCon[1]+" profile append "+fileName;
			System.out.println(cmd);
			command = new Command(cmd, true);
		}
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
}
