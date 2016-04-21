import java.io.File;
import java.util.Scanner;

/*Ip forward network*/
public class IPForward {
	
	/*Host Info*/
	int maxSize = 10;
	int number ;
	String []hostList = new String[maxSize];
	String []eth0List = new String[maxSize];
	String []eth1List = new String[maxSize];
	String []ipBr0 = new String[maxSize];
	String []iprange = new String [maxSize];
	
	
	/*IPForward*/
	public IPForward(String []args){
		String type = args[2];
		if(type.equalsIgnoreCase("default")){
			defaultNetwork();
		}
		else if(type.equalsIgnoreCase("option")){
			optionNetwork();
		}
		else{
			System.out.println("Error usage .");
			Usage usage = new Usage("Network");
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
	
	/*Default network*/
	public void defaultNetwork(){
		getHostInfo();
		
		/*Command*/
		String cmdString = null;
		Command command = null;
		
		for(int i = 0; i < number ; i++){
			iprange[i] = "10.0."+i+".0/24";
			ipBr0[i] = "10.0."+i+".1/24";
		}
	
		String sshString;
			
		/*Add br0 bridge*/
		for(int i = 0; i < number; i++){
			
			sshString = "ssh "+eth0List[i]+" ";	
			cmdString = sshString + " brctl addbr br0;ifconfig br0 "+ipBr0[i]+" up";
			System.out.println(cmdString);
			command = new Command(cmdString, false);
		}
			
		/*Ip forward*/
		for(int i = 0; i < number; i++){
			sshString = "ssh "+eth0List[i]+" ";
			for(int j = 0; j < number; j++){
				if(j != i){
					cmdString = sshString+" ip route add "+iprange[j]+" via "+eth1List[j];
					System.out.println(cmdString);
					command = new Command(cmdString, false);
				}
			}
		}
		
		/*Docker service*/
		for(int i = 0; i < number; i++){
			sshString = "ssh "+eth0List[i]+" ";
			cmdString = sshString+" service docker stop; docker daemon -b=br0 &>/dev/null &";
			System.out.println(cmdString);
			command = new Command(cmdString, false);
		}
		
	}
	
	/*Option network*/
	public void optionNetwork(){
		getHostInfo();
		
		/*Command*/
		String cmdString = null;
		Command command = null;
		
		System.out.println("Network type is ip-forward , please input the ip-range:");
		Scanner input = new Scanner(System.in);
		
		for(int i = 0; i < number; i++){
			System.out.println("Please input ip-range for Host-"+(i+1));
			System.out.println("    -- iprange:");
			iprange[i] = input.nextLine();
			System.out.println("    -- ip for linux-bridge to connect containers:");
			ipBr0[i] = input.nextLine();
		}
	
		String sshString;
			
		/*Add br0 bridge*/
		for(int i = 0; i < number; i++){
			
			sshString = "ssh "+eth0List[i]+" ";	
			cmdString = sshString + " brctl addbr br0;ifconfig br0 "+ipBr0[i]+" up";
			System.out.println(cmdString);
			command = new Command(cmdString, false);
		}
			
		/*Ip forward*/
		
		for(int i = 0; i < number; i++){
			sshString = "ssh "+eth0List[i]+" ";
			for(int j = 0; j < number; j++){
				if(j != i){
					cmdString = sshString+" ip route add "+iprange[j]+" via "+eth1List[j];
					System.out.println(cmdString);
					command = new Command(cmdString, false);
				}
			}
		}
		
		/*Docker service*/
		for(int i = 0; i < number; i++){
			sshString = "ssh "+eth0List[i]+" ";
			cmdString = sshString+" service docker stop; docker daemon -b=br0 &>/dev/null &";
			System.out.println(cmdString);
			command = new Command(cmdString, false);
		}
	}
}
