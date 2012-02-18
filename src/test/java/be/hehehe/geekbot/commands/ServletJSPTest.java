package be.hehehe.geekbot.commands;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;

public class ServletJSPTest {

	@SuppressWarnings("serial")
	@Test
	public void testJSP() throws Exception {
		Server server = new Server(11224);
		HandlerList handlerList = new HandlerList();

		WebAppContext webappcontext = new WebAppContext();
		webappcontext.setContextPath("/");
		webappcontext.setWar(getClass().getResource("/web").toExternalForm());
		webappcontext.setInitParameter(
				"org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		webappcontext.setErrorHandler(new ErrorPageErrorHandler() {
			@Override
			public void handle(String arg0, Request arg1,
					HttpServletRequest arg2, HttpServletResponse arg3)
					throws IOException {
			}
		});

		String path = "/testjsp";
		webappcontext.addServlet(new ServletHolder(new HttpServlet() {
			@Override
			protected void doGet(HttpServletRequest req,
					HttpServletResponse resp) throws ServletException,
					IOException {
				req.getRequestDispatcher("test.jsp").forward(req, resp);
			}
		}), path);

		handlerList.setHandlers(new Handler[] { webappcontext,
				new DefaultHandler() });
		server.setHandler(handlerList);
		server.start();

		Assert.assertEquals(IOUtils.toString(new URL(
				"http://localhost:11224/test.jsp").openStream()), "hello");
	}
}
