package be.hehehe.geekbot.bot;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletEvent {

	/**
	 * Returns the servlet request associated with this event.
	 * 
	 * @return the request object
	 */
	HttpServletRequest getRequest();

	/**
	 * Returns the servlet response associated with this event.
	 * 
	 * @return the response object
	 */
	HttpServletResponse getResponse();
}
