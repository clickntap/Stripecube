package com.clickntap.developers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.clickntap.utils.LessUtils;
import com.clickntap.utils.XMLUtils;

import freemarker.template.utility.StringUtil;

public class ClicknTapUi {

	public static void sync() throws Exception {
		ClicknTapUi.sync(null, null, null);
	}

	public static void sync(final String accessKey, final String secretKey, final String bucketName, final String confFile) throws Exception {
		new Thread(new Runnable() {
			public void run() {
				Map<String, String> crcMap = new HashMap<String, String>();
				while (true) {
					try {
						Thread.sleep(1000);
						syncUiDir(crcMap, accessKey, secretKey, bucketName, confFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static void sync(final String accessKey, final String secretKey, final String bucketName) throws Exception {
		ClicknTapUi.sync(accessKey, secretKey, bucketName, "src/main/webapp/ui/ui.xml");
	}

	protected static void syncUiDir(Map<String, String> crcMap, String accessKey, String secretKey, String bucketName, String confFile) throws Exception {
		Document doc = XMLUtils.copyFrom(confFile);
		String folder = new File(confFile).getParentFile().getCanonicalPath() + "/";
		for (Element element : (List<Element>) doc.getRootElement().elements("file")) {
			String key = element.attributeValue("name");
			String crc;
			if (key.endsWith("less")) {
				LessUtils.compile(folder + key, false);
				String from = folder + key.substring(0, key.length() - 5) + ".css";
				crc = Long.toString(FileUtils.checksumCRC32(new File(from)));
				if (!crcMap.containsKey(key) || !crc.equals(crcMap.get(key))) {
					if (element.attributeValue("dest") != null) {
						ClicknTapUi.copy(accessKey, secretKey, bucketName, from, element.attributeValue("dest"), element.attributeValue("content-type"));
					}
				}
			} else {
				crc = Long.toString(FileUtils.checksumCRC32(new File(folder + key)));
				if (!crcMap.containsKey(key) || !crc.equals(crcMap.get(key))) {
					if (element.attributeValue("dest") != null) {
						ClicknTapUi.copy(accessKey, secretKey, bucketName, folder + key, element.attributeValue("dest"), element.attributeValue("content-type"));
					}
				}
			}
			crcMap.put(key, crc);
		}
		loadLibs(doc, folder, "js");
		loadLibs(doc, folder, "css");
	}

	private static void loadLibs(Document doc, String folder, String type) throws IOException {
		List<Element> loads = (List<Element>) doc.getRootElement().elements("load");
		if (loads != null && loads.size() > 0) {
			StringBuffer code = new StringBuffer();
			String name = doc.getRootElement().attributeValue("project");
			for (Element element : loads) {
				if (type.equals(element.attributeValue("type"))) {
					File[] files = new File(getWebmetaDir(type)).listFiles();
					String lib = element.attributeValue("lib");
					String libName = null;
					for (File file : files) {
						if (file.getName().startsWith(lib)) {
							if (Character.isDigit(file.getName().charAt(lib.length() + 1)) && file.isFile()) {
								code.append(FileUtils.readFileToString(file)).append("\n");
								libName = FilenameUtils.getBaseName(file.getName());
								try {
									String[] requiredFonts = StringUtil.split(element.attributeValue("fonts"), ',');
									File[] fonts = new File(getWebmetaDir("fonts")).listFiles();
									File destDir = new File(folder + name + "/fonts");
									for (File font : fonts) {
										for (String requiredFont : requiredFonts) {
											if (font.getName().startsWith(requiredFont)) {
												FileUtils.copyFileToDirectory(font, destDir);
											}
										}
									}
								} catch (Exception e) {
								}
							}
						}
					}
					for (File file : files) {
						if (libName != null && file.getName().startsWith(lib)) {
							if (Character.isDigit(file.getName().charAt(lib.length() + 1)) && file.isDirectory()) {
								try {
									File destDir = new File(folder + name + "/css/" + file.getName().substring(libName.length() + 1));
									FileUtils.copyDirectory(file, destDir);
								} catch (Exception e) {
								}
							}
						}
					}
				}
			}
			if ("js".equals(type)) {
				File[] files = new File(folder + name).listFiles();
				for (File f : files) {
					if (f.getName().endsWith(".js") && f.getName().startsWith(name) && f.getName().length() > name.length() + 4) {
						code.append(FileUtils.readFileToString(f)).append("\n");
					}
				}
			}
			code.append(FileUtils.readFileToString(new File(folder + name + '/' + name + "." + type)));
			File out = new File(folder + name + '/' + type + '/' + name + "-min." + type);
			out.getParentFile().mkdir();
			if ("css".equals(type)) {
				FileUtils.writeStringToFile(out, moveImportToHead(code.toString()));
			} else {
				FileUtils.writeStringToFile(out, code.toString());
			}
		}
	}

	private static String getWebmetaDir(String type) {
		return "../../../git/Stripecube/webmeta/" + type;
	}

	private static String moveImportToHead(String code) {
		String[] lines = code.split(System.getProperty("line.separator"));
		StringBuffer codeHeader = new StringBuffer();
		StringBuffer codeBody = new StringBuffer();
		for (String line : lines) {
			if (line.startsWith("@import")) {
				codeHeader.append(line);
				codeHeader.append(System.getProperty("line.separator"));
			} else {
				codeBody.append(line);
				codeBody.append(System.getProperty("line.separator"));
			}
		}
		return new StringBuffer(codeHeader).append(System.getProperty("line.separator")).append(codeBody.toString()).toString();
	}

	public static void copy(String accessKey, String secretKey, String bucketName, String from, String to, String contentType) throws Exception {
		FileInputStream in = new FileInputStream(from);
		copy(accessKey, secretKey, bucketName, in, to, contentType);
		in.close();
	}

	public static void copy(String accessKey, String secretKey, String bucketName, InputStream from, String to, String contentType) throws Exception {
		System.out.println(to);
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		TransferManager transferManager = new TransferManager(credentials);
		copy(bucketName, from, contentType, to, from.available(), transferManager);
		transferManager.shutdownNow();
	}

	public static void copy(String bucketName, InputStream in, String contentType, String destinationPath, long contentSize, TransferManager transferManager) throws Exception {
		// System.out.println(destinationPath);
		ObjectMetadata md = new ObjectMetadata();
		if (contentSize == 0)
			contentSize = in.available();
		md.setContentLength(contentSize);
		md.setContentType(contentType);
		PutObjectRequest request = new PutObjectRequest(bucketName, destinationPath, in, md);
		request.setCannedAcl(CannedAccessControlList.PublicRead);
		Upload upload = transferManager.upload(request);
		upload.waitForCompletion();
	}
}
