package com.clickntap.hub;

import com.clickntap.tool.jdbc.JdbcManager;
import com.clickntap.utils.PListUtils;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class XMLController extends MultiActionController {

    private BOManager app;
    private JdbcManager jdbcManager;

    public BOManager getApp() {
        return app;
    }

    public void setApp(BOManager app) {
        this.app = app;
    }

    public JdbcManager getJdbcManager() {
        return jdbcManager;
    }

    public void setJdbcManager(JdbcManager jdbcManager) {
        this.jdbcManager = jdbcManager;
    }

    protected void handleRequest(HttpServletResponse response, Object object) {
        response.setContentType("text/xml; UTF-8");
        try {
            OutputFormat format = OutputFormat.createCompactFormat();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(PListUtils.toPList(object).getDocument());
            response.setContentLength(out.size());
            handleRequest(response, out.toByteArray());
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    protected void handleRequest(HttpServletResponse response, byte[] out) throws IOException {
        response.getOutputStream().write(out);
    }

    protected void handleException(HttpServletResponse response, Throwable throwable) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(out);
            throwable.printStackTrace(writer);
            writer.close();
            XMLErrorResponse res = new XMLErrorResponse();
            res.setException(throwable);
            res.setStackTrace(out.toString("UTF-8"));
            handleRequest(response, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
