package com.clickntap.tool.types;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.web.multipart.MultipartFile;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class HttpFile implements MultipartFile {

	private InputStream inputStream;

	public HttpFile(String url) throws IOException {
		this(new URL(url));
	}

	public HttpFile(URL url) throws IOException {
		this.inputStream = url.openStream();
	}

	public byte[] getBytes() throws IOException {
		throw new NotImplementedException();
	}

	public String getContentType() {
		throw new NotImplementedException();
	}

	public InputStream getInputStream() throws IOException {
		return inputStream;
	}

	public String getName() {
		throw new NotImplementedException();
	}

	public String getOriginalFilename() {
		throw new NotImplementedException();
	}

	public long getSize() {
		throw new NotImplementedException();
	}

	public boolean isEmpty() {
		return false;
	}

	public void transferTo(File arg0) throws IOException, IllegalStateException {
		throw new NotImplementedException();
	}

}
