package com.clickntap.tool.mail;

public class Body {

	public Body(String content, String contentType) {
		this.content = content;
		this.contentType = contentType;
	}

	private String contentType;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
