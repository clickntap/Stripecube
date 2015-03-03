package com.clickntap.tool.jdbc;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JdbcBlobber {
    public void update(Object object, MultipartFile multipartFile, List<String> scripts, JdbcManager jdbcManager) throws Exception;
}
