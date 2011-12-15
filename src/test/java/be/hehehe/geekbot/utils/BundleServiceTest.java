package be.hehehe.geekbot.utils;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;

@RunWith(WeldRunner.class)
public class BundleServiceTest {

	@Inject
	private BundleService bundleService;

	@Test
	public void testBotName() {
		Assert.assertTrue(StringUtils.isNotBlank(bundleService.getBotName()));
	}

}
