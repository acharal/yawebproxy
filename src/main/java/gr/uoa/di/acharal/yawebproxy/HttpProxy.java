package gr.uoa.di.acharal.yawebproxy;

import java.net.*;
import java.io.*;
import gr.uoa.di.acharal.yawebproxy.http.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.logging.*;

public class HttpProxy implements Runnable, ConnectionHandler {
	
	private ServerSocket serv = null;
	//private ExecutorService executor = Executors.newCachedThreadPool();
	private int port = 3128;
	
	Cache cache = new Cache("./cache/");
	Logger logger = Logger.global;
	
	InetAddress forwp = null;
	int         forwport = 0;

	public HttpProxy() { }
	
	public HttpProxy(int port) { 
		this.port = port;
	}
	
	public HttpProxy(String proxy, int port) throws UnknownHostException {
		try {
			forwp = InetAddress.getByName(proxy);
			forwport = port;
		} catch (UnknownHostException e) {
			logger.severe("Unknown host "+proxy);
			throw e;
		}
	}
	
	public HttpProxy(int myport, String proxy, int port) throws UnknownHostException {
		this(proxy, port);
		this.port = myport;
	}

	public void setCacheRoot(String root) {
		cache.changeRoot(root);
	}
	
	public void run() { 
		openServer(port);
		while (true) {
			try {
				Socket acceptedSocket = serv.accept();
				//executor.execute(newClientHandler(acceptedSocket));
				new Thread(newClientHandler(acceptedSocket)).start();	
				//new Thread(new ClientSocketHandler(this, acceptedSocket)).start();
			} catch (IOException e) {
				logger.severe(e.getMessage());
			}
		}
	}

	public ClientSocketHandler newClientHandler(Socket s) {
		return new ClientSocketHandler(this, s);
	}
	
	/**
	 * Open server socket and makes proxy to start listening to 
	 * connections.
	 */
	public void openServer(int port) {
		try {
			serv = new ServerSocket(port);
			logger.info("Server is opened and listening in port "+port);
		} catch (IOException e) { 
			logger.severe(e.getMessage());
		}
	}
	
	public void closeServer() { 
		if (serv != null && !serv.isClosed()) {
			try {
				serv.close();
			}catch (IOException e) { 
				logger.warning(e.getMessage());
			}
		}
	}

	public HttpConnection getConnection(String surl) throws IOException, MalformedURLException {
		if (forwp != null) {
			return new HttpConnection(new Socket(forwp, forwport));
		}
		return new HttpConnection(surl);
	}
	
	public void releaseConnection(HttpConnection conn) throws IOException {
		if (conn != null)
			conn.close();
	}
	
	public static void main(String args[]) {
		try {
			HttpProxy proxy = new HttpProxy(3128, "10.14.147.12", 3128);
			proxy.run();
		} catch (UnknownHostException e) { 
			System.exit(0);
		}
	}
}
