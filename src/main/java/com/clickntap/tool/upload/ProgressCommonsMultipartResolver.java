package com.clickntap.tool.upload;

import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class ProgressCommonsMultipartResolver extends CommonsMultipartResolver {
    //
    // public boolean isMultipart(HttpServletRequest request) {
    // if (request.getParameter("USE_CUSTOM_PARSER") != null)
    // return false;
    // else
    // return super.isMultipart(request);
    // }
    //
    public static final String PROGRESS_SESSION_KEY = "multipartKey";
    //
    // protected MultipartParsingResult parseRequest(HttpServletRequest request)
    // throws MultipartException {
    // try {
    // request.setCharacterEncoding(ConstUtils.UTF_8);
    // } catch (UnsupportedEncodingException e) {
    // }
    // FileUpload fileUpload = prepareFileUpload(ConstUtils.UTF_8);
    // try {
    // if (request.getParameter(PROGRESS_SESSION_KEY) != null)
    // fileUpload.setProgressListener(new ProgressCommonsListener(request));
    // List fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
    // MultipartParsingResult parsingResult = parseFileItems(fileItems,
    // ConstUtils.UTF_8);
    // return parsingResult;
    // } catch (FileUploadBase.SizeLimitExceededException ex) {
    // throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
    // } catch (FileUploadException ex) {
    // throw new MultipartException("Could not parse multipart servlet request",
    // ex);
    // }
    // }
}
