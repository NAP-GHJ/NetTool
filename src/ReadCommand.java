/*Read and analysis command*/
public class ReadCommand {
	
	public ReadCommand(String []args){
		analysis(args);
	}
	
	/*Analysis the command*/
	public void analysis(String []args){
		String temp = args[0];
		switch (temp) {
		case "--help":
			Usage usage = new Usage();
			break;
		case "system":
			NetTool netTool = new NetTool(args);
			break;
		case "add-host":
			Host host = new Host(args);
			break;
		case "show-host":
			new Host().display();
			break;
		case "add-network":
			addNetwork(args);
			break;
		case "show-network":
			new Network().display();
			new Container().display();
			break;
		case "add-container":
			addContainer(args);
			break;
		case "show-container":
			new Container().display();
			break;
		case "test-container":
			new Container().testPerformance(args);
			break;
		case "calico-add-connect":
			new Calico().addConnect();
			break;
		default:
			System.out.println("Error usage .");
			usage = new Usage();
			break;
		}
	}
	
	/*Add container*/
	public void addContainer(String []args){
		if(args.length < 3){
			System.out.println("Add-container error usage .");
			Usage usage = new Usage("Container");
			return;
		}
		String host = args[1];
		String type = args[2];
		switch (type) {
		
		case "default":
			Container container = new Container(host, false);
			break;
		case "option":
			Container container2 = new Container(host, true);
			break;
		default:
			System.out.println("Add-container error usage .");
			Usage usage = new Usage("Container");
			break;
		}
	}

	/*Add network*/
	public void addNetwork(String []args){
		if(args.length < 3){
			System.out.println("Error usage .");
			Usage usage = new Usage("Network");
			return;
		}
		
		Network network = new Network(args[1]);
		
		String networkType = args[1];
		switch (networkType) {
		case "ip-forward":
			IPForward ipForward = new IPForward(args);
			break;
		case "linux-bridge":
			LinuxBridge lb = new LinuxBridge(args);
			break;
		case "weave":
			Weave weave = new Weave(args);
			break;
		case "calico":
			Calico calico = new Calico(args);
			break;
		case "pipework":
			Pipework pipework = new Pipework(args);
			break;
		case "docker-network":
			DockerNetwork dockerNetwork = new DockerNetwork(args);
			break;
		case "ovs":
			Ovs ovs = new Ovs(args);
			break;
		case "flannel":
			Flannel flannel = new Flannel(args);
			break;
		default:
			System.out.println("Error usage .");
			Usage usage = new Usage("Network");
			break;
		}
		
	}
	
}
