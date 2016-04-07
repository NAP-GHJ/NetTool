/*Print usage*/
public class Usage {
	public Usage(){
		System.out.println("Usage for NetTool: network config tool for Docker Container");
		display();
	}
	public Usage(String type){
		System.out.println("Usage for "+type+" : ");
		switch(type){
			case "Network":
				network();
				break;
			case "System":
				system();
				break;
			default:;
		}
	}
	
	/*Display the usage*/
	private void display(){
		//System.out.println("Usage......");
		//System.out.println("Usage   end");
	}
	
	/*Usage about host*/
	private void host(){
		
	}
	
	/*Usage for network command*/
	private void network(){
		System.out.println("  -- add-network [network-type] [option/default]");
		System.out.println("  -- show-network");
	}
	
	/*system*/
	private void system(){
		System.out.println("  -- system [start] : start the system");
		System.out.println("  -- system [stop] : stop the system");
		System.out.println("  -- system [restart] : restart the system\n");
	}
}
