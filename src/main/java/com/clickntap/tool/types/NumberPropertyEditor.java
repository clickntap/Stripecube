package com.clickntap.tool.types;

import com.clickntap.utils.ConstUtils;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

public class NumberPropertyEditor extends PropertyEditorSupport {

    public String getAsText() {
        return getValue() == null ? ConstUtils.EMPTY : getValue().toString();
    }

    public void setAsText(String text) throws IllegalArgumentException {
        try {
            if (text != null && !text.equals(ConstUtils.EMPTY))
                setValue(new BigDecimal(text));
            else
                setValue(null);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
