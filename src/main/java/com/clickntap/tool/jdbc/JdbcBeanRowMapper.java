package com.clickntap.tool.jdbc;

import com.clickntap.tool.types.Datetime;
import com.clickntap.utils.BindUtils;
import com.clickntap.utils.IOUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.jdbc.core.RowMapper;

import java.io.ByteArrayOutputStream;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@SuppressWarnings("unchecked")
public class JdbcBeanRowMapper implements RowMapper {
    private Class beanClass;

    public JdbcBeanRowMapper(Class beanClass) {
        this.beanClass = beanClass;
    }

    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        Object bean = null;
        try {
            bean = beanClass.newInstance();
            MutablePropertyValues pvs = new MutablePropertyValues();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                Object o = rs.getObject(i);
                if (o instanceof Timestamp)
                    o = new Datetime(((Timestamp) o).getTime());
                if (o instanceof Clob) {
                    Clob clob = (Clob) o;
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    IOUtils.copy(clob.getAsciiStream(), out);
                    o = new String(out.toByteArray(), "UTF-8");
                }
                // if (o instanceof String)
                // o = AsciiUtils.utf7ToText(o.toString());
                pvs.addPropertyValue(rs.getMetaData().getColumnLabel(i), o);
            }
            BindUtils.bind(bean, pvs);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
        return bean;
    }
}
