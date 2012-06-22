package gr.uoa.di.acharal.yawebproxy;

import java.util.HashMap;
import java.io.File;
import gr.uoa.di.acharal.yawebproxy.http.*;

public class Cache {

	HashMap index = new HashMap();
	String cacheroot = "/tmp/cache/";
	int counter = 0;
	
	public Cache(String root) {
		cacheroot = root;
	}
	
	public void changeRoot(String root) {
		cacheroot = root;
	}
	
	synchronized public CachableHttpObject get(String url) {
		return (CachableHttpObject) index.get(url);
	}
	
	synchronized public CachableHttpObject get(HttpRequestHeader req) {
		return (CachableHttpObject) index.get(req.getURL());
	}

	synchronized public void put(String url, CachableHttpObject obj) {
			index.put(url, obj);
	}

	static public boolean isCachable(HttpRequestHeader req)  {
		if (req.getMethod().equals("GET") &&
			(req.getHeader("Authentication") == null) &&
			(req.getHeader("Cookie") == null) &&
			(req.getHeader("Range") == null) &&
			(req.getHeader("Cache-Control") == null))
			return true;
		else
			return false;
	}

	public String getCacheFileName(java.net.URL url) { 
		//return cacheroot+"/"+url.getHost()+"/"+url.getPath();
		counter++;
		return cacheroot+"/cache"+counter;
	}
}
