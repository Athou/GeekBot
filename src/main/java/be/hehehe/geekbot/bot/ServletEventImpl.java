package be.hehehe.geekbot.bot;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletEventImpl implements ServletEvent {

	private HttpServletRequest request;
	private HttpServletResponse response;

	public ServletEventImpl(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public HttpServletResponse getResponse() {
		return response;
	}

}
