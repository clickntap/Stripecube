package com.clickntap.smart;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.clickntap.utils.ConstUtils;
import com.clickntap.utils.StringUtils;

public class SmartFacebook {

	public Number uid;

	public SmartFacebook(SmartContext smartContext) throws Exception {
		String appId = smartContext.getFacebookAppId();
		Cookie[] cookies = smartContext.getRequest().getCookies();
		for (int i = 0; cookies != null && i < cookies.length; i++) {
			if (("fbs_" + appId).equals(cookies[i].getName())) {
				uid = Long.parseLong(SmartFacebook.parseQueryString(cookies[i].getValue()).get("uid"));
			}
		}
	}

	public Number getUid() {
		return uid;
	}

	public static Map<String, String> parseQueryString(String queryString) {
		queryString = StringUtils.trimTrailingCharacter(StringUtils.trimLeadingCharacter(queryString, ConstUtils.QUOT_CHAR), ConstUtils.QUOT_CHAR);
		Map<String, String> map = new HashMap<String, String>();
		String[] parameters = queryString.split("&");
		for (int i = 0; i < parameters.length; i++) {
			String[] keyAndValue = parameters[i].split("=");
			if (keyAndValue.length != 2) {
				continue;
			}
			String key = keyAndValue[0];
			String value = keyAndValue[1];
			map.put(key, value);
		}
		return map;
	}
}
