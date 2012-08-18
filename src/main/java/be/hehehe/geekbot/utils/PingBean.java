package be.hehehe.geekbot.utils;

import java.io.InputStream;
import java.net.URL;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

@Singleton
public class PingBean {

	@Inject
	BundleService bundleService;

	@Inject
	Logger log;

	@Schedule(hour = "*", persistent = false)
	private void refresh() {
		InputStream stream = null;
		try {
			URL url = new URL(bundleService.getWebServerRootPath());
			stream = url.openStream();
			IOUtils.toByteArray(stream);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
}
