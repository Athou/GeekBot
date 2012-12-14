package be.hehehe.geekbot.utils;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import be.hehehe.geekbot.ArquillianTest;

public class BundleServiceTest extends ArquillianTest {

	@Inject
	private BundleService bundleService;

	@Test
	public void testBotName() {
		Assert.assertTrue(StringUtils.isNotBlank(bundleService.getBotName()));
	}

}
