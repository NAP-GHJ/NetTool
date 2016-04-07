/*Network pipework*/

public class Pipework {
	int number;
	String []hostList ;
	String []eth0List ;
	String []eth1List ;
	
	Pipework(){}
	Pipework(String []args){
		getHostInfo();
		
		Command command;
		
		for(int i = 0; i < number; i++){
			String ssh = "ssh "+eth0List[i]+" ";
			String eth1 =ssh+"ifconfig eth1 promisc";
			String ovs = ssh+" ovs-vsctl add-br ovs0;ovs-vsctl add-port ovs0 eth1";
			command = new Command(eth1, true);
			System.out.println(eth1);
			command = new Command(ovs, true);
			System.out.println(ovs);
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
