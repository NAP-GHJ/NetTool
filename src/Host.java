import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

/*Info and Functions for Host*/
public class Host {
	String name;
	String ipEth0;
	String ipEth1;
	
	int current = 0;
	int maxSize = 10;
	String []nameList = new String[maxSize];
	String []ethList0 = new String[maxSize];
	String []ethList1 = new String[maxSize];
	
	public Host(){}
	public Host(String []args){
		if(args.length < 3){
			System.out.println("error usage ...\n");
			Usage usage = new Usage("Host");
		}
		else{
			name = args[1];
			ipEth0 = args[2];
			init();
		}
	}
	
	/*Init the host*/
	private void init(){
		try{
		/*Read config file*/
			NetTool netTool = new NetTool();
			/*Create network-host file for each host*/
			String newName = netTool.dir1+"/"+name;
			File newFile = new File(newName);
			if(!newFile.exists())
				newFile.createNewFile();
			
			File fileName = new File(netTool.hostFile);
			if(fileName.exists()){
				//System.out.println("hosts.txt file exists");
				Scanner input = new Scanner(fileName);
				String lastLine =  null;
				while(input.hasNext()){
					lastLine = input.nextLine();
				}
				char number = lastLine.charAt(lastLine.length()-1);
				int newNum = number - '0'+1;
				String s = String.valueOf(newNum);
				ipEth1 = "192.168.100."+s;
				//System.out.println(ipEth1);
				PrintWriter output = new PrintWriter(new FileWriter(fileName, true));
				//ipEth1 = "192.168.100.1";
				output.println(name+" "+ipEth0+" "+ipEth1);
				
				output.close();
			}
			else{
				//System.out.println("hosts.txt file doesn't exist");
				PrintWriter output = new PrintWriter(fileName);
				ipEth1 = "192.168.100.1";
				output.println(name+" "+ipEth0+" "+ipEth1);
				output.close();
			}
			
			/*初始化*/
			String ssh = "ssh "+ipEth0+" ";
			String eth1 =ssh+"ifconfig eth1 "+ipEth1+" up";
			/*Process cmd in order*/
			Command command = null;
			command = new Command(eth1, true);
			
		}
		catch(Exception e){
			
		}
	}
	
	/*Display the hosts info*/
	public void display(){
		try{
			NetTool netTool = new NetTool();
			File file = new File(netTool.hostFile);
			if(!file.exists()){
				System.out.println("No host in the cluster .");
				return;
			}
			Scanner input = new Scanner (file);
			while(input.hasNext()){
				String temp = input.nextLine();
				String [] tokens = temp.split(" ");
				for(int i = 0; i < tokens.length; i++){
					if(i == 1)
						System.out.print("    eth0:");
					if(i == 2)
						System.out.print("    eth1:");
					System.out.println(tokens[i]);
				}
			}
			input.close();
		}
		catch(Exception e){
			System.out.println("Show host read file error");
		}
	}
	
	/*Host to Ip*/
	public String HostIp(String host){
		String string = null;
		try{
			NetTool netTool = new NetTool();
			File file = new File(netTool.hostFile);
			if(!file.exists()){
				System.out.println("No host in the cluster");
				return string;
			}
			Scanner input = new Scanner (file);
			while(input.hasNext()){
				String temp = input.nextLine();
				//System.out.println(temp);
				String []tokenStrings = temp.split(" ");
				if(tokenStrings[0].equalsIgnoreCase(host)){
					string = tokenStrings[1];
				}
			}
			input.close();
		}
		catch(Exception e){
			System.out.println("Show host read file error");
			//return string;
		}
		return string;
	}

	/*get host info*/
	public void getHostInfo(){
		try{
			NetTool netTool = new NetTool();
			File file =new File(netTool.hostFile);
			if(!file.exists()){
				System.out.println("No host in the cluster");
				return;
			}
			Scanner input = new Scanner (file);
			while(input.hasNext()){
				String temp = input.nextLine();
				
				String []token = temp.split(" ");
				nameList[current] = token[0];
				ethList0[current] = token[1];
				ethList1[current] = token[2];
				current++;
			}
			input.close();
		}
		catch(Exception e){
			System.out.println(e+"Show host read file error");
		}
	}
}
