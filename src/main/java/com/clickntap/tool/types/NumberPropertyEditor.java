package com.clickntap.tool.types;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

import com.clickntap.utils.ConstUtils;

public class NumberPropertyEditor extends PropertyEditorSupport {

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

	public String getAsText() {
		return getValue() == null ? ConstUtils.EMPTY : getValue().toString();
	}

}
