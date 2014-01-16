package com.dp.bigdata.taurus.alert;

public class MailInfo {
	private String host = "mail.51ping.com";				//smtp server
	private String user = "info@51ping.com";	//user name
	private String password = "chinese1";				//password for current user
	private String from = "info@51ping.com";	//mail sender
	private String to = "renyuan.sun@dianping.com";		//mail receiver
	private String subject = "Hello Mail";				//mail title
	private String content = "hello";					//mail content
	private String format = "text/plain";							//mail is displayed in this format
	private String charset = "utf-8";					//mail is displayed in this charset
	
	public MailInfo(String host, String user, String password, String from,
			String to, String subject, String content, String format,
			String charset) {
		super();
		this.host = host;
		this.user = user;
		this.password = password;
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.content = content;
		this.format = format;
		this.charset = charset;
	}

	public MailInfo(String to, String subject, String content) {
		super();
		this.to = to;
		this.subject = subject;
		this.content = content;
	}
	
	public void addTo(String to){
		this.to += "," + to;
	}
	
	public MailInfo() {
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
