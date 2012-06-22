package gr.uoa.di.acharal.yawebproxy;

import java.net.Socket;
import java.net.UnknownHostException;

public class ForwardProxy extends HttpProxy {

	public ForwardProxy() { }

	public ForwardProxy(int port) { 
		super(port);
	}
	
	public ForwardProxy(String proxy, int port) throws UnknownHostException {
		super(proxy, port);
	}
	
	public ForwardProxy(int myport, String proxy, int port) throws UnknownHostException {
		super(myport, proxy, port);
	}
	
	public ClientSocketHandler newClientHandler(Socket s) {
		return new ReqForwardHandler(this, s);
	}

	public static void main(String args[]) {
		if (args.length < 3)
			usage();

		try {
			ForwardProxy proxy = new ForwardProxy(Integer.parseInt(args[0]), 
												args[1], Integer.parseInt(args[2]));
			
			if (args.length == 4)
				proxy.setCacheRoot(args[3]);
			else
				proxy.setCacheRoot("./cache/fproxy/");
			proxy.run();
		} catch (UnknownHostException e) { 
			System.exit(0);
		} catch (Exception e) { 
			usage();
		}
	}
	
	private static void usage() {
		System.err.println("Usage: program port-to-listen vproxy-ip vproxy-port [cache-dir optional]");
		System.exit(1);
	}
}
