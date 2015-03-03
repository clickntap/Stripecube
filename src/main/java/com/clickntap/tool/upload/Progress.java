package com.clickntap.tool.upload;

import javax.servlet.http.HttpServletRequest;

public class Progress {

    private String multipartKey;
    private HttpServletRequest request;

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getMultipartKey() {
        return multipartKey;
    }

    public void setMultipartKey(String multipartKey) {
        this.multipartKey = multipartKey;
    }

    public ProgressCommonsListener getListener() {
        if (hasListener()) {
            return (ProgressCommonsListener) request.getSession().getAttribute(multipartKey);
        }
        return null;
    }

    public boolean hasListener() {
        return request != null && request.getSession().getAttribute(multipartKey) != null;
    }

}
