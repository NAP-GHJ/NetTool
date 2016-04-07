/*Etcd Service */

public class Etcd {
	
	int number;
	String []hostList ;
	String []eth0List ;
	String []eth1List ;
	
	/*Etcd service */
	public Etcd(){
		getHostInfo();
		
		for(int i = 0; i < number ; i++){
			System.out.println(hostList[i]+" "+eth0List[i]+" "+eth1List[i]);
		}
		
		/*Etcd command*/
		Command command;
		String cluster = "-initial-cluster ";
		for(int i = 0; i < number; i++){
			if(i < number - 1)
				cluster = cluster + "node"+i+"=http://"+eth0List[i]+":2380,";
			else {
				cluster = cluster + "node"+i+"=http://"+eth0List[i]+":2380";
				cluster = cluster +" -initial-cluster-state new ";
			}
		}
		//System.out.println(cluster);
		for(int i = 0; i < number; i++){
			String ssh = "ssh "+eth0List[i]+" ";
			String ip = "http://"+eth0List[i]+":2380 ";
			String etcd = "etcd -name node"+i+" -initial-advertise-peer-urls "+ip+
				"-listen-peer-urls "+ip+" -initial-cluster-token cluster1 "+cluster;
			String cmd = ssh+etcd+" &>/dev/null &";
			command = new Command(cmd, false);
			System.out.println(cmd);
		}
		
	}
	
	/*get host info*/
	public void getHostInfo(){
		Host host = new Host();
		host.getHostInfo();
		number = host.current;
		hostList = host.nameList;
		eth0List = host.ethList0;
		eth1List = host.ethList1;
	}
	
	/*get cluster node*/
	public String getNode(){
		return eth0List[0];
	}
	
}
