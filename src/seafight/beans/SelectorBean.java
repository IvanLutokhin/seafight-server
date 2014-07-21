package seafight.beans;

import java.io.Serializable;

public class SelectorBean implements Serializable, IBean {
	private static final long serialVersionUID = 1L;
	
	private String host;
	private Integer port;
	
	public SelectorBean() { }
	
	public String getHost() {
		return this.host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return this.port;
	}
	
	public void setPort(Integer port) {
		this.port = port;
	}
}