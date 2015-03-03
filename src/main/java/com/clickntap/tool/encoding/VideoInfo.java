package com.clickntap.tool.encoding;

public class VideoInfo {
    private Number videoId;
    private String status;
    private String description;
    private String source;
    private String destination;

    public Number getVideoId() {
        return videoId;
    }

    public void setVideoId(Number videoId) {
        this.videoId = videoId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

}
