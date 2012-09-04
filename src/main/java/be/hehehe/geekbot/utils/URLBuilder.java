package be.hehehe.geekbot.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class URLBuilder {
	private String baseURL;
	private List<String> params;

	public URLBuilder(String baseURL) {
		this.baseURL = baseURL;
		this.params = new ArrayList<String>();
	}

	public URLBuilder addParam(String name, String value) {
		if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(value)) {
			params.add(name + "=" + value);
		}
		return this;
	}

	public String build() {
		String result = null;
		String paramString = StringUtils.join(params, "&");
		if (baseURL == null) {
			result = paramString;
		} else if (StringUtils.isEmpty(paramString)) {
			return baseURL;
		} else {
			int position = baseURL.indexOf("?");
			if (position == -1) {
				result = baseURL + "?" + paramString;
			} else if (position == baseURL.length() - 1) {
				result = baseURL + paramString;
			} else {
				result = baseURL + "&" + paramString;
			}
		}
		return result;
	}
}
