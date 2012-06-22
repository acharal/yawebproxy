package gr.uoa.di.acharal.yawebproxy.http;

import java.io.*;

abstract public class HttpEntity implements HttpTransferable {

	private HttpHeader hdr;
	private Integer len = null;
	transient private HttpConnection conn;
	
	HttpEntity(HttpHeader hdr, HttpConnection conn) {
		this.conn = conn;
		setHeader(hdr);
	}

	HttpEntity(HttpConnection conn) {
		this.conn = conn;
	}
	
	public Integer getLength() { return len; }

	public HttpHeader getHeader() { 
		return hdr;
	}

	public HttpConnection getConnection() { 
		return conn;
	}
	
	protected void setHeader(HttpHeader hdr)  {
		this.hdr = hdr;
		String slen = this.hdr.getHeader("Content-Length");
		if (slen != null) {
			try {
				len = new Integer(slen);
			} catch (NumberFormatException e) {
				len = null;
			}
		}
	}

	abstract public void transferTo(HttpConnection to) throws IOException;

	protected int transferContentBytes(HttpConnection to, int count) throws IOException {
		HttpReader r = conn.getReader();
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
			remaining -= read;
		}
		w.flush();
		return count;
	}
	
	protected int transferUntilEOF(HttpConnection to) throws IOException {
		HttpReader r = conn.getReader();
		HttpWriter w = to.getWriter();
		int bufsize = 4084;
		byte[] buf = new byte[bufsize];
		
		int read, total = 0;
		while (true) {
			read = r.read(buf, 0, bufsize);
			if (read == -1)
				break;
			w.write(buf, 0, read);
			total += read;
		}
		w.flush();
		return total;
	}
}
