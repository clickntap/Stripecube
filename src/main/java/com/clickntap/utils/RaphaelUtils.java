package com.clickntap.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.amazonaws.util.StringInputStream;

public class RaphaelUtils {

	public static String getRaphaelCode(File svg, String container) throws Exception {
		InputStream in = new FileInputStream(svg);
		String code = getRaphaelCode(in, container);
		in.close();
		return code;
	}

	public static String getRaphaelCode(String svg, String container) throws Exception {
		InputStream in = new StringInputStream(svg);
		String code = getRaphaelCode(in, container);
		in.close();
		return code;
	}

	public static String getRaphaelCode(InputStream in, String container) throws Exception {
		Document doc = XMLUtils.copyFrom(in);
		StringBuffer sb = new StringBuffer();
		sb.append("var ").append(container);
		sb.append(" = Raphael('");
		sb.append(container);
		sb.append("',");
		sb.append(doc.getRootElement().attributeValue("width"));
		sb.append(",");
		sb.append(doc.getRootElement().attributeValue("height"));
		sb.append(");\n");
		sb.append(container);
		sb.append(".setViewBox(0,0,");
		sb.append(doc.getRootElement().attributeValue("width"));
		sb.append(",");
		sb.append(doc.getRootElement().attributeValue("height"));
		sb.append(");\n");
		for (Element element : (List<Element>) doc.getRootElement().element("g").elements("path")) {
			sb.append(container).append(".path('");
			sb.append(element.attributeValue("d").toString());
			sb.append("').attr({");
			StringBuffer attrs = new StringBuffer();
			String fill = element.attributeValue("fill");
			if (fill != null && !fill.isEmpty()) {
				attrs.append("fill:'");
				attrs.append(fill);
				attrs.append("',");
			}
			String stroke = element.attributeValue("stroke");
			attrs.append("stroke:'");
			if (stroke != null && !stroke.isEmpty()) {
				attrs.append(stroke);
			} else {
				attrs.append("none");
			}
			attrs.append("',");
			sb.append(attrs.toString().substring(0, attrs.length() - 1));
			sb.append("});\n");
		}
		return sb.toString();
	}

}
