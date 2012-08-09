package be.hehehe.geekbot.bot;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;

import be.hehehe.geekbot.ArquillianTest;

public class GeekBotCDIExtensionTest extends ArquillianTest {

	@Inject
	private GeekBotCDIExtension extension;

	@Test
	public void testTriggers() {
		Assert.assertTrue(extension.getTriggers().size() > 0);
	}

	@Test
	public void testRandoms() {
		Assert.assertTrue(extension.getRandoms().size() > 0);
	}

	@Test
	public void testTimers() {
		Assert.assertTrue(extension.getTimers().size() > 0);
	}
}
