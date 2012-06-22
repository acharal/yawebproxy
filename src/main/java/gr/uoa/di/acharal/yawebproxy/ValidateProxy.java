package gr.uoa.di.acharal.yawebproxy;

import java.net.*;
import java.io.*;
import gr.uoa.di.acharal.yawebproxy.http.*;
import java.util.logging.*;


public class ValidateProxy extends HttpProxy {

	public ValidateProxy() { }
	
	public ValidateProxy(int port) { 
		super(port);
	}
	
	public ValidateProxy(String proxy, int port) throws UnknownHostException {
		super(proxy, port);
	}
	
	public ValidateProxy(int myport, String proxy, int port) throws UnknownHostException {
		super(myport, proxy, port);
	}
	
	public ClientSocketHandler newClientHandler(Socket s) {
		return new ValidationHandler(this, s);
	}

	public static void main(String args[]) {
		if (args.length < 1)
			usage();

		ValidateProxy proxy = new ValidateProxy(Integer.parseInt(args[0]));
		if (args.length == 2)
			proxy.setCacheRoot(args[1]);
		else
			proxy.setCacheRoot("./cache/vproxy/");
		proxy.run();
	}
	
	private static void usage() {
		System.err.println("Usage: program port-to-listen [cachedir optional]");
		System.exit(1);
	}
}
