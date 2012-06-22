package gr.uoa.di.acharal.yawebproxy;

import gr.uoa.di.acharal.yawebproxy.http.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Accepts IMS requests and check if are valid according to its cache.
 * If not valid the respond with the cached object. If object not in 
 * its cache, it tunnels the request to original servers to the net 
 * and then supply the client.
 * @author angel
 *
 */
public class ValidationHandler extends ClientSocketHandler {

	public ValidationHandler(HttpProxy proxy, Socket socket) {
		super(proxy, socket);
	}
	
	public void handleRequest(HttpRequest req) {
		HttpRequestHeader hdr = (HttpRequestHeader) req.getHeader();
		if (Cache.isCachable(hdr)) {
			CachableHttpObject obj = proxy.cache.get(hdr);
			if (obj == null) {
				// object not in cache
				try {
					obj = new CachableHttpObject(hdr, proxy);
					obj.setCache(proxy.cache);
					obj.setModified(new Date(System.currentTimeMillis()));
					new TransferHandler(conn, obj).run();
					proxy.cache.put(hdr.getURL(), obj);
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
				// object in cache
				// check if client's cache is consistent.
				String modified = hdr.getHeader("If-Modified-Since");
				if (modified != null) {
					SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance();
					df.applyPattern("EEE, d MMM yyyy HH:mm:ss z");
					Date dmod = null;
					try {
							dmod = df.parse(modified);
					} catch (ParseException e) { 
						proxy.logger.warning(e.toString());
					}

					if (obj.getLastModified().after(dmod)) {
						// cache is not consistent so transfer the cached object
						new TransferHandler(conn, obj).run();
					} else  {
						// send a 304 Not Modified Response
						handleNotModified();
					}
				} else { 
					new TransferHandler(conn, obj).run();
				}
			}
		} else {
			tunnelRequest(req);
		}
	}
		
	protected void handleNotModified() {
		HttpResponseHeader rh = new HttpResponseHeader();
		rh.setStatusCode("304 Not Modified");
		HttpResponse response = new HttpResponse(rh);
		new TransferHandler(conn, response).run();
	}
	
}
