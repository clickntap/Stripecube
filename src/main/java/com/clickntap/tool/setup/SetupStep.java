package com.clickntap.tool.setup;

import com.clickntap.hub.BOManager;
import org.springframework.jdbc.core.JdbcTemplate;

public interface SetupStep {
    public String getDescription() throws Exception;

    public boolean isSkipable() throws Exception;

    public void init(JdbcTemplate db, BOManager app, String schema) throws Exception;

    public void setup() throws Exception;
}
