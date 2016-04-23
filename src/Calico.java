import java.util.Scanner;

/*Calico Network*/

public class Calico {
	
	String ipPool;
	
	int number;
	String []hostList ;
	String []eth0List ;
	String []eth1List ;
	
	public Calico(){}
	
	public Calico(String []args){
		/*Start the etcd service first*/
		Etcd etcd = new Etcd();
		
		/*Get Host Info*/
		getHostInfo();
		
		/*Start Calico On Each Node*/
		String sshString;
		String cmdString;
		Command command;
		for(int i = 0; i < number ; i++){
			sshString = "ssh "+eth0List[i]+" ";
			cmdString = sshString +" calicoctl node --ip="+eth1List[i];
			command = new Command(cmdString,false);
			System.out.println(cmdString);
		}
		
		/*Config the ip-pool*/
		System.out.println("Network type is calico, please set the ip-pool:");
		Scanner input = new Scanner(System.in);
		ipPool = input.nextLine();
		cmdString = "ssh "+eth0List[0]+" calicoctl pool add "+ipPool+" --ipip --nat-outgoing";
		command = new Command(cmdString,false);
		
	}
	
	/*Add-connect*/
	public void addConnect(){
		System.out.println("Network type is calico ,add connect-relation between containers");
		System.out.println("    -- [host-name]:[con-name] [host-name]:[con-name]");
		Scanner input = new Scanner(System.in);
		String connection = input.nextLine();
		
		Command command;
		String sshString,cmdString;
		
		String [] token = connection.split(" ");
		String [] host1 = token[0].split(":");
		String [] host2 = token[1].split(":");
		
		String fileName = "profile"+host1[0]+host1[1]+host2[0]+host2[1];
		
		for(int i = 0; i < token.length; i++){
			
			String [] hostCon = token[i].split(":");
			sshString = "ssh "+(new Host().HostIp(hostCon[0]));
			if(i == 0){
				
				cmdString = sshString+" calicoctl profile add "+fileName;
				System.out.println(cmdString);
				command = new Command(cmdString, true);
			}
			cmdString = sshString +" calicoctl container "+hostCon[1]+" profile append "+fileName;
			System.out.println(cmdString);
			command = new Command(cmdString, true);
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
