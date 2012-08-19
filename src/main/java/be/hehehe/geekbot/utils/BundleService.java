package be.hehehe.geekbot.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

@Singleton
public class BundleService {

	@Inject
	Logger log;

	private Properties props;

	@PostConstruct
	public void init() {
		props = new Properties();
		InputStream is = null;
		try {
			String configPath = "/config.properties";
			String openshiftDataDir = System.getenv("OPENSHIFT_DATA_DIR");
			if (openshiftDataDir != null) {
				is = new FileInputStream(openshiftDataDir + configPath);
			} else {
				is = getClass().getResourceAsStream(configPath);
			}
			props.load(is);
		} catch (Exception e) {
			log.fatal("Could not load config file");
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	public String getAdminPassword() {
		return getValue("admin.password");
	}

	public String getBotName() {
		return getValue("botname");
	}

	public String getChannel() {
		return getValue("channel");
	}

	public String getServer() {
		return getValue("server");
	}

	public int getPort() {
		return Integer.parseInt(getValue("port"));
	}

	public String getBitlyLogin() {
		return getValue("bitly.login");
	}

	public String getBitlyApiKey() {
		return getValue("bitly.apikey");
	}

	public String getImgurApiKey() {
		return getValue("imgur.apikey");
	}

	public String getVDMApiKey() {
		return getValue("vdm.apikey");
	}

	public String getWebServerRootPath() {
		String host = getValue("webserver.hostname");
		if (host.endsWith("/")) {
			host = host.substring(0, host.length() - 1);
		}
		return host;
	}

	public int getWebServerPort() {
		return Integer.parseInt(getValue("webserver.port"));
	}

	public boolean isTest() {
		return "true".equals(getValue("test"));
	}

	private String getValue(String key) {
		return props.getProperty(key, null);
	}

}
