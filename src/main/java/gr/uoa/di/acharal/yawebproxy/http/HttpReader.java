package gr.uoa.di.acharal.yawebproxy.http;

import java.io.*;

//public class HttpReader extends BufferedReader {
public class HttpReader extends DataInputStream {

	//public HttpReader(Reader reader) {
		//super(reader);
	//}
	
	public HttpReader(InputStream in) {
		super(in);
	}

	public HttpRequestHeader readRequestHeader() throws IOException, MalformedHeaderException {
		String line;
		HttpRequestHeader hdr = new HttpRequestHeader();
		line = readLine();
		parseRequestLine(line, hdr);
		
		while ((line = readLine()) != null && 
				!line.equals("")) {
			Header hdr1 = parseHeading(line);
			hdr.addHeader(hdr1);
		}
		return hdr;
	}
	
	public HttpResponseHeader readResponseHeader() throws IOException, MalformedHeaderException {
		String line;
		HttpResponseHeader hdr = new HttpResponseHeader();
		line = readLine();
		parseStatusLine(line, hdr);
		
		while ((line = readLine()) != null && 
				!line.equals("")) {
			Header hdr1 = parseHeading(line);
			hdr.addHeader(hdr1);
		}
		return hdr;
	}
	
	private Header parseHeading(String line) throws MalformedHeaderException {
		int s1 = line.indexOf(':');
		String type, value;
		
		if (s1 < 0)
			throw new MalformedHeaderException();
		
		type = line.substring(0, s1);
		value = line.substring(s1+1);
		
		return new Header(type.trim(), value.trim());
	}
	
	private void parseRequestLine(String line, HttpRequestHeader hdr) throws MalformedHeaderException {
		String method = null, version = null, requestURL = null;
		
		if (line == null)
			throw new MalformedHeaderException();
			
		int s1 = line.indexOf (' ');
		if (s1 < 0) {
			throw new MalformedHeaderException();
		}
		int s2 = line.indexOf (' ', s1+1);
		method = line.substring(0,s1);

		if (s2 > 0) {
		    requestURL = line.substring (s1+1,s2);
		    version = line.substring (s2+1).trim ();
		} else {
		    requestURL = line.substring (s1+1);
		    version = null;
		}
		hdr.setMethod(method);
		hdr.setHttpVersion(version);
	
		hdr.setURL(requestURL);

	}

	private void parseStatusLine(String line, HttpResponseHeader hdr) throws MalformedHeaderException {
		String statusCode = null, version = null;
		
		if (line == null)
			throw new MalformedHeaderException();
		
		int s1 = line.indexOf (' ');
		if (s1 < 0) {
			throw new MalformedHeaderException();
		}

		version = line.substring(0,s1);
		statusCode = line.substring (s1+1);

		hdr.setHttpVersion(version);
		hdr.setStatusCode(statusCode);

	}
}
