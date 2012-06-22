package gr.uoa.di.acharal.yawebproxy;

import gr.uoa.di.acharal.yawebproxy.http.*;

import java.net.*;
import java.io.*;

public class ClientSocketHandler implements Runnable {

	HttpConnection conn;
	HttpProxy proxy;

	public ClientSocketHandler(HttpProxy proxy, Socket socket) { 
		this.proxy = proxy;
		try {
			conn = new HttpConnection(socket);
		} catch (IOException e) {
			proxy.logger.warning(e.getMessage());
		}
	}

	public void run() {
		try {
		while (conn.isConnected()) {
			HttpRequest req= conn.readRequest();
			//String scon = req.getHeader().getHeader("Connection");
			//proxy.logger.info("GET "+((HttpRequestHeader)req.getHeader()).getURL());
			handleRequest(req);
		//	if (scon != null && scon.equals("close")) {
				conn.close();
				break;
			//}
		}
		} catch (MalformedHeaderException e) {
			handleFailedRequest();
		} catch (IOException e) {  }
	}

	public void handleRequest(HttpRequest req) { 
		// TODO: handle the failed requests, returning error responses.
		HttpRequestHeader hdr = (HttpRequestHeader) req.getHeader();
		if (Cache.isCachable(hdr)) {
			//proxy.logger.info("URL "+hdr.getURL()+" is cachable");
			CachableHttpObject obj = proxy.cache.get(hdr);
			if (obj == null) {
				try {
					obj = new CachableHttpObject(hdr, proxy);
					obj.setCache(proxy.cache);
				} catch (HttpException httpe) { 
					// Response other than 200 OK
					try {
						new TransferHandler(conn, httpe.getResponse()).run();
						proxy.releaseConnection(httpe.getResponse().getConnection());
						conn.close();
					} catch (IOException e) { }
					return;
				} catch (IOException ioe) { 
					proxy.logger.warning(ioe.getMessage());
					return;
				}
			} else { 
				/* always check if is valid */
				try {
					obj.validate();
				} catch (IOException e) { 
					handleFailedRequest();
				} catch (HttpException httpe) { 
					// something going wrong with validation
					// tunnel the error response to client
					try {
						new TransferHandler(conn, httpe.getResponse()).run();
						proxy.releaseConnection(httpe.getResponse().getConnection());
						conn.close();
					} catch (IOException e) { }
				}
				proxy.logger.info("cache hit: "+hdr.getURL());
				/* we have a cache hit! */
			}
			new TransferHandler(conn, obj).run();
			proxy.cache.put(hdr.getURL(), obj);
		} else {
			/* no caching just forward the request */
			tunnelRequest(req);
		}
	}
	
	public void handleFailedRequest() {
		try {
			conn.close();
		} catch (IOException e) { }
	} 

	public void tunnelRequest(HttpRequest req) {
		
		try {
			HttpRequestHeader hdr = (HttpRequestHeader) req.getHeader();
			HttpConnection c = proxy.getConnection(hdr.getURL());
			new TransferHandler(c, req).run();
			HttpResponse reply = c.readResponse();
			new TransferHandler(conn, reply).run();
			proxy.releaseConnection(c);
		} catch (IOException e) { 
			handleFailedRequest();
		} catch (MalformedHeaderException e) {
			handleFailedRequest();
		}
	}
}
