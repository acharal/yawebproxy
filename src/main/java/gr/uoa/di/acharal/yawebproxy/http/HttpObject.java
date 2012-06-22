package gr.uoa.di.acharal.yawebproxy.http;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

public class HttpObject extends HttpEntity {

	private URL url;
	protected ConnectionHandler ch;

	/** 
	 * construct an httpobject by an already opened connection waiting to read it.
	 * @param con
	 */
	public HttpObject(String surl, HttpConnection conn) throws MalformedURLException, HttpException {
		super(conn);
		url = new URL(surl);
	}

	/**
	 * construct an httpobject by a httprequest. Opens the connection and fetches the object. 
	 * @param header the request header
	 */
	public HttpObject(HttpRequestHeader header) throws IOException, MalformedURLException, HttpException {
		super(new HttpConnection(header.getURL()));
		url = new URL(header.getURL());
		requestObject(header);
	}
	
	public HttpObject(HttpRequestHeader header, ConnectionHandler ch) throws IOException, MalformedURLException, HttpException {
		super(ch.getConnection(header.getURL()));
		this.ch = ch;
		url = new URL(header.getURL());
		requestObject(header);
	}

	protected void requestObject(HttpRequestHeader hdr) throws IOException, MalformedURLException, HttpException {
		
		if (!getConnection().isConnected())
			getConnection().establish();
		
		HttpWriter w = getConnection().getWriter();
		HttpReader r = getConnection().getReader();
		w.writeRequestHeader(hdr);
		w.flush();

		try {
			HttpResponseHeader reply = r.readResponseHeader();
			if (reply.getStatusCode().equals("200 OK"))
				setHeader(reply);
			else
				throw new HttpException(new HttpResponse(reply, getConnection()));
		} catch (MalformedHeaderException e) {
			System.err.print(e);
		}
	}
	
	/**
	 * Copy constructor
	 * @param obj
	 */
	public HttpObject(HttpObject obj) {
		super(obj.getHeader(), obj.getConnection());
	}

	public URL getURL() {
		return url;
	}
	
	public Date getLastModified() {
		String sdate = getHeader().getHeader("Last-Modified");
		if (sdate == null)
			return null;

		SimpleDateFormat df = (SimpleDateFormat) DateFormat.getInstance();
		df.applyPattern("EEE, d MMM yyyy HH:mm:ss z");
		Date d = null;

		try {
			d = df.parse(sdate);
		} catch (ParseException e) { 
			return null;
		}
		return d;
	}
	
	public void setModified(Date d) {
		SimpleDateFormat df = (SimpleDateFormat) DateFormat.getInstance();
		df.applyPattern("EEE, d MMM yyyy HH:mm:ss z");
		getHeader().setHeader("Last-Modified", df.format(d));
	}
	
	public void transferTo(HttpConnection to) throws IOException {
		HttpWriter w = to.getWriter();
		//HttpReader r = getConnection().getReader();

		w.writeResponseHeader((HttpResponseHeader)getHeader());

		if (getLength() != null) {
			int len = getLength().intValue();
			transferContentBytes(to, len);
		} else {
			transferUntilEOF(to);
		}
		w.flush();
	}

}
