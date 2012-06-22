package gr.uoa.di.acharal.yawebproxy.http;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public class GeneralHeader {

	private List headers = new LinkedList();
	
	public String getHeader(String type) {
		Header hdr;
		Iterator it = headers.iterator();
		while (it.hasNext()) {
			hdr = (Header)it.next();
			if (type.equalsIgnoreCase(hdr.getType())) { 
				return hdr.getValue();
			}
		}
		return null;
	}
	
	public void setHeader(String type, String value) {
		Header hdr;
		Iterator it = headers.iterator();
		while (it.hasNext()) {
			hdr = (Header)it.next();
			if (type.equalsIgnoreCase(hdr.getType())) { 
				hdr.setValue(value);
				return;
			}
		}
		this.addHeader(type, value);
	}
	
	public void removeHeader(String type) {
		Header hdr;
		Iterator it = headers.iterator();
		while (it.hasNext()) {
			hdr = (Header)it.next();
			if (type.equalsIgnoreCase(hdr.getType())) { 
				it.remove();
				return;
			}
		}
	}
	
	public void copyHeader(GeneralHeader hdr) {
		Header h;
		Iterator it = hdr.getHeaders().iterator();
		while (it.hasNext()) {
			h = (Header)it.next();
			this.addHeader(h.getType(), h.getValue());
		}
	}
	
	void addHeader(String type, String value) {
		headers.add(new Header(type, value));
	}
	
	void addHeader(Header hdr) {
		headers.add(hdr);
	}
	
	protected List getHeaders() {
		return headers;
	}
	
}
