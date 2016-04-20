/*Overlay Network for Docker - Flannel*/

public class Flannel {
	/*IP-Range for the cluster*/
	String ipRangeString;
	
	public Flannel(){}
	
	public Flannel(String []args){
		String type = args[2];
		if(type.equalsIgnoreCase("default")){
			//defaultNetwork();
		}
		else if(type.equalsIgnoreCase("option")){
			
		}
		else{
			System.out.println("add-network ip forward error");
			Usage usage = new Usage();
		}
		
	}
}	
