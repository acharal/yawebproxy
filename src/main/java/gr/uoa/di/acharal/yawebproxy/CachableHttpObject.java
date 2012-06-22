package gr.uoa.di.acharal.yawebproxy;

import gr.uoa.di.acharal.yawebproxy.http.*;

import java.io.*;
import java.net.*;

public class CachableHttpObject extends HttpObject {

	private File cached = null;
	private Cache cache;
	private boolean iscached = false;
	private boolean isvalid = true;

	public CachableHttpObject(String surl, HttpConnection conn) throws MalformedURLException, HttpException {
		super(surl, conn);
	}

	public CachableHttpObject(HttpObject obj) {
		super(obj);
	}
	
	public CachableHttpObject(HttpRequestHeader hdr) throws IOException, MalformedURLException, HttpException {
		super(hdr);
	}
	
	public CachableHttpObject(HttpRequestHeader hdr, ConnectionHandler ch) throws IOException, MalformedURLException, HttpException {
		super(hdr, ch);
	}
	
	public void setCache(Cache cache) { 
		this.cache = cache;
		String name = this.cache.getCacheFileName(getURL());
		cached = new File(name);
		iscached = false;
	}
	
	public boolean isCached() {
		return iscached;
	}

	public boolean isValid() { 
		return isvalid;
	}

	public boolean isExpired() {
		return false;
	}
	
	public void validate() throws IOException, HttpException { 
		// send an IMS GET
		HttpRequestHeader hdr = new HttpRequestHeader();
		hdr.setMethod("GET");
		hdr.setURL(getURL().toString());
		hdr.copyHeader(getHeader());
		if (getLastModified() != null)
			hdr.setHeader("If-Modified-Since", getLastModified().toString());

		try {
			requestObject(hdr);
		} catch (MalformedURLException e) { 
			
		} catch (HttpException e) { 
			HttpResponse response = e.getResponse();
			HttpResponseHeader rh = (HttpResponseHeader)response.getHeader();
			if (rh.getStatusCode().equalsIgnoreCase("304 Not Modified")) {
				/* check is done! */
				isvalid = true;
			} else { 
				isvalid = false;
				throw e;
			}
		}
	}
	
	public void cache() throws IOException {
		prepareFile();
		saveToFile(cached);
	}

	public void transferTo(HttpConnection to) throws IOException {
		HttpWriter w = to.getWriter();
		w.writeResponseHeader((HttpResponseHeader)getHeader());
		w.flush();
		if (isCached()) {
			/*transfer from the file */
			FileInputStream cachedObj = new FileInputStream(cached);
			transferFromStream(cachedObj, to);
			cachedObj.close();
		} else {
			prepareFile();
			FileOutputStream of = new FileOutputStream(cached);
			if (getLength() != null) {
				int len = getLength().intValue();
				transferContentBytes(to, of, len);
			} else {
				transferUntilEOF(to, of);
			}
			of.close();
			iscached = true;
		}
	}
	
	protected int transferContentBytes(HttpConnection to, OutputStream to2, int count) throws IOException {
		HttpReader r = getConnection().getReader();
		HttpWriter w = to.getWriter();
		int remaining = count;
		int bufsize = 4084;
		byte[] buf = new byte[bufsize];
		
		int toread, read;
		while (remaining > 0) {
			toread = (remaining > bufsize)? bufsize : remaining;
			read = r.read(buf, 0, toread);
			if (read == -1) {
				break;
			}
				//throw new IOException("Stream ended unexpectetely");
			w.write(buf, 0, read);
			to2.write(buf, 0, read);
			remaining -= read;
		}
		w.flush();
		to2.flush();
		return count;
	}
	
	protected int transferUntilEOF(HttpConnection to, OutputStream to2) throws IOException {
		HttpReader r = getConnection().getReader();
		HttpWriter w = to.getWriter();
		int bufsize = 4084;
		byte[] buf = new byte[bufsize];
		
		int read, total = 0;
		while (true) {
			read = r.read(buf, 0, bufsize);
			if (read == -1)
				break;
			w.write(buf, 0, read);
			to2.write(buf, 0, read);
			total += read;
		}
		w.flush();
		to2.flush();
		return total;
	}
	
	protected int transferFromStream(InputStream from, HttpConnection to) throws IOException {
		int total = 0;
		int read;
		int bufsize = 4084;
		byte[] buf = new byte[bufsize];
		HttpWriter w = to.getWriter();
		
		while (true) {
			read = from.read(buf, 0, bufsize);
			if (read == -1)
				break;
			w.write(buf, 0, read);
			total += read;
		}
		w.flush();
		return total;
	}
	
	protected int transferToStream(OutputStream to) throws IOException {
		int total = 0;
		int read;
		int bufsize = 4084;
		byte[] buf = new byte[bufsize];
		HttpReader from = getConnection().getReader(); 
		
		while (true) {
			read = from.read(buf, 0, bufsize);
			if (read == -1)
				break;
			to.write(buf, 0, read);
			total += read;
		}
		to.flush();
		return total;
	}
	
	protected void prepareFile() throws IOException {
		File dir = cached.getParentFile();
		if (dir != null) 
			dir.mkdirs();
		cached.createNewFile();
		if (!cached.canWrite())
			cached = null;
	}
	
	protected  void saveToFile(File file) throws IOException { 
		FileOutputStream cachedObj = new FileOutputStream(file);
		transferToStream(cachedObj);
		cachedObj.close();
	}

}
