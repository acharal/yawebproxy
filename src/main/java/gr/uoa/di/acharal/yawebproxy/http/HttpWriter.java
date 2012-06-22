package gr.uoa.di.acharal.yawebproxy.http;

import java.io.*;
import java.util.Iterator;
import java.util.List;

//public class HttpWriter extends BufferedWriter {
public class HttpWriter extends DataOutputStream {

	static String CRLF = "\r\n";
	
	/*public HttpWriter(Writer writer) {
		super(writer);
	}
	*/

	public HttpWriter(OutputStream out) {
		super(out);
	}
	
	public void writeRequestHeader(HttpRequestHeader hdr) throws IOException { 
		String reqline = hdr.getMethod()+" "+hdr.getURL()+" "+hdr.getHttpVersion()+CRLF;
		writeBytes(reqline);
		writeHeaders(hdr.getHeaders());
	}
	
	public void writeResponseHeader(HttpResponseHeader hdr) throws IOException { 
		String reqline = hdr.getHttpVersion()+" "+hdr.getStatusCode()+CRLF;
		writeBytes(reqline);
		writeHeaders(hdr.getHeaders());
	}
	
	protected void writeHeaders(List hdrs) throws IOException { 
		String hstring ="";
		Iterator it = hdrs.iterator();
		while(it.hasNext()) {
			Header hdr = (Header)it.next();
			hstring += hdr.getType() + ": " + hdr.getValue() + CRLF;
		}
		writeBytes(hstring+CRLF);
	}

}
