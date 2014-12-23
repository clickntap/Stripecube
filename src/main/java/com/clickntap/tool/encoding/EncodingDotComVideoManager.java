package com.clickntap.tool.encoding;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.clickntap.utils.XMLUtils;

public class EncodingDotComVideoManager implements VideoManager {
	private String userId;
	private String userKey;

	public EncodingDotComVideoManager(String userId, String userKey) {
		this.userId = userId;
		this.userKey = userKey;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clickntap.tool.encoding.VideoManager#getVideos()
	 */
	@Override
	public List<VideoInfo> getVideos() throws Exception {
		List<VideoInfo> videos = new ArrayList<VideoInfo>();
		Document out = executeRequest(buildRequest("GetMediaList", null, null));
		for (Element element : (List<Element>) out.getRootElement().elements("media")) {
			VideoInfo video = new VideoInfo();
			video.setVideoId(Long.parseLong(element.element("mediaid").getTextTrim()));
			video.setStatus(element.element("mediastatus").getTextTrim());
			videos.add(video);
		}
		return videos;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clickntap.tool.encoding.VideoManager#getVideoStatus(java.lang.Number)
	 */
	@Override
	public VideoInfo getVideoStatus(Number videoId) throws Exception {
		VideoInfo video = new VideoInfo();
		Document out = executeRequest(buildRequest("GetStatus", videoId, null));
		video.setVideoId(Long.parseLong(out.getRootElement().element("id").getTextTrim()));
		video.setStatus(out.getRootElement().element("status").getTextTrim());
		try {
			video.setDescription(out.getRootElement().element("description").getTextTrim());
		} catch (Exception e) {
		}
		video.setSource(out.getRootElement().element("sourcefile").getTextTrim());
		video.setDestination(out.getRootElement().element("format").element("s3_destination").getTextTrim());
		return video;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clickntap.tool.encoding.VideoManager#deleteVideo(java.lang.Number)
	 */
	@Override
	public boolean deleteVideo(Number videoId) throws Exception {
		Document out = executeRequest(buildRequest("CancelMedia", videoId, null));
		try {
			if (out.getRootElement().element("message").getTextTrim().toLowerCase().equals("deleted")) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clickntap.tool.encoding.VideoManager#addVideo(java.lang.String)
	 */
	@Override
	public VideoInfo addVideo(String source) throws Exception {
		VideoInfo video = new VideoInfo();
		Document out = executeRequest(buildRequest("AddMedia", null, source));
		video.setVideoId(Long.parseLong(out.getRootElement().element("MediaID").getTextTrim()));
		return video;
	}

	private Document executeRequest(Document doc) throws Exception {
		URL server = null;
		String url = "http://manage.encoding.com";
		server = new URL(url);
		String xmlRequest = "xml=" + URLEncoder.encode(doc.asXML().toString(), "UTF8");
		HttpURLConnection urlConnection = (HttpURLConnection) server.openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		urlConnection.setConnectTimeout(60000);
		urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		BufferedWriter request = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
		request.write(xmlRequest);
		request.flush();
		request.close();
		urlConnection.connect();
		InputStream in = urlConnection.getInputStream();
		Document response = XMLUtils.copyFrom(in);
		in.close();
		return response;
	}

	private Document buildRequest(String action, Number videoId, String source) {
		Document doc = DocumentHelper.createDocument();
		Element query = doc.addElement("query");
		query.addElement("userid").setText(userId);
		query.addElement("userkey").setText(userKey);
		query.addElement("action").setText(action);
		if (videoId != null) {
			query.addElement("mediaid").setText(videoId.toString());
		}
		if (source != null) {
			query.addElement("source").setText(source);
			Element format = query.addElement("format");
			format.addElement("output").setText("fl9");
			format.addElement("size").setText("800x450");
			format.addElement("bitrate").setText("512k");
			format.addElement("audio_bitrate").setText("128k");
			format.addElement("audio_channels_number").setText("2");
			format.addElement("keep_aspect_ratio").setText("yes");
			format.addElement("video_codec").setText("libx264");
			format.addElement("profile").setText("high");
			format.addElement("VCodecParameters").setText("no");
			format.addElement("audio_codec").setText("libfaac");
			format.addElement("two_pass").setText("yes");
			format.addElement("cbr").setText("no");
			format.addElement("deinterlacing").setText("yes");
			format.addElement("keyframe").setText("300");
			format.addElement("audio_volume").setText("100");
			format.addElement("file_extension").setText("mp4");
		}
		return doc;
	}
}
