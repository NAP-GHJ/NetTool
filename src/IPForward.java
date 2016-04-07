import java.io.File;
import java.util.Scanner;

/*Ip forward network*/
public class IPForward {
	
	/*IPForward*/
	public IPForward(String []args){
		String type = args[2];
		if(type.equalsIgnoreCase("default")){
			defaultNetwork();
		}
		else if(type.equalsIgnoreCase("vxlan")){
			
		}
		else{
			System.out.println("add-network ip forward error");
			Usage usage = new Usage();
		}
		
	}
	
	/*Default network*/
	public void defaultNetwork(){
		try{
			/*Host IP*/
			int maxSize = 10;
			int current = 0;
			String []hosts = new String[maxSize];
			String []eth0s = new String[maxSize];
			String []eth1s = new String[maxSize];
			String []ipBr0 = new String[maxSize];
			String []iprange = new String [maxSize];
			
			/*Full-Connections Between Hosts*/
			File file = new File("/home/ghj/netToolFile/hosts.txt");
			if(!file.exists()){
				System.out.println("File hosts.txt doesn't exit , add-host first ");
				return;
			}
			Scanner input = new Scanner(file);
			while(input.hasNext()){
				String temp = input.nextLine();
				String []tokens = temp.split(" ");
				hosts[current] = tokens[0];
				eth0s[current] = tokens[1];
				eth1s[current] = tokens[2];
				current ++;
				iprange[current-1] = "10.0."+current+".0/24";
				ipBr0[current-1] = "10.0."+current+".1/24";
			}
			input.close();
			
			/*Process cmd in order*/
			Command command = null;
			/*Create bridge br0 on each host*/
			String []ssh = new String[maxSize];
			for(int i = 0; i < current; i++){
				ssh[i] = "ssh "+eth0s[i]+" ";
			}
			
			String []br0s = new String[maxSize];
			for(int i = 0; i < current; i++){
				br0s[i] = ssh[i]+" brctl addbr br0;ifconfig br0 "+ipBr0[i]+" up";
				System.out.println(br0s[i]);
				command = new Command(br0s[i], false);
			}
			
			/*Ip forward*/
			for(int i = 0; i < current; i++){
				for(int j = 0; j < current; j++){
					if(j != i){
						String ipRoute = ssh[i]+"ip route add "+iprange[j]+" via "+eth1s[j];
						System.out.println(ipRoute);
						command = new Command(ipRoute, false);
					}
				}
			}
			
			/*Docker service*/
			for(int i = 0; i < current; i++){
				String dockerStr = ssh[i]+" service docker stop; docker daemon -b=br0";
				System.out.println(dockerStr);
				command = new Command(dockerStr, false);
			}
			
			/*Write Network info to network.txt*/
			
			/***************************/
			
			
			
		}
		catch(Exception e){
			
		}
	}
	
	/*Vxlan network*/
	public void vxlanNetwork(){
		return;
	}
}
