package com.clickntap.tool.jdbc;

import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.List;

public class JdbcDefaultBlobber implements JdbcBlobber {

    public void update(Object bean, MultipartFile multipartFile, List<String> scripts, JdbcManager jdbcManager) throws Exception {
        Method method = bean.getClass().getMethod("setFile", MultipartFile.class);
        if (method != null)
            method.invoke(bean, multipartFile);
        jdbcManager.updateScript(scripts.get(0), bean);
    }
}
