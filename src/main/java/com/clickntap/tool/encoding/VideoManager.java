package com.clickntap.tool.encoding;

import java.util.List;

public interface VideoManager {

    public abstract List<VideoInfo> getVideos() throws Exception;

    public abstract VideoInfo getVideoStatus(Number videoId) throws Exception;

    public abstract boolean deleteVideo(Number videoId) throws Exception;

    public abstract VideoInfo addVideo(String source) throws Exception;

}