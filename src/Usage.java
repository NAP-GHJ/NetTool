/*Print usage*/
public class Usage {
	public Usage(){
		System.out.println("Usage for NetTool: network config tool for Docker Container");
		display();
	}
	public Usage(String type){
		System.out.println("Usage for "+type+" : ");
		switch(type){
    		case "Container":
    			container();
    			break;
			case "Network":
				network();
				break;
			case "System":
				system();
				break;
			case "Host":
				host();
				break;
			default:;
		}
	}
	
	
	
	/*Display the usage*/
	private void display(){
		//System.out.println("Usage......");
		//System.out.println("Usage   end");
	}
	
	/*Usage about container*/
	private void container(){
		System.out.println("  -- add-container [Host-name] [default/option] : add a container on the host , you can choose default mode and options mode");
		System.out.println("  -- show-container : display the information of all the container in the cluster");
		System.out.println("  -- test-container [Host-1] [Con-1] [Host-2] [Con2]:test the performance of network between two containers");
	}
	
	/*Usage about host*/
	private void host(){
		System.out.println("  -- add-host [Host-name] [Host-ip] : add a host to the cluster and record its infomation");
		System.out.println("  -- show-host : display the information of all the hosts in the cluster");
	}
	
	/*Usage for network command*/
	private void network(){
		System.out.println("  -- add-network [network-type] [option/default]: create network for the cluster and network type can be choose from the following:");
		System.out.println("     -- linux-bridge");
		System.out.println("     -- ip-forward");
		System.out.println("     -- weave");
		System.out.println("     -- pipework");
		System.out.println("  -- show-network : display the network information of the cluster");
		
	}
	
	/*system*/
	private void system(){
		System.out.println("  -- system [start] : start the system");
		System.out.println("  -- system [stop] : stop the system");
		System.out.println("  -- system [restart] : restart the system\n");
	}
}
