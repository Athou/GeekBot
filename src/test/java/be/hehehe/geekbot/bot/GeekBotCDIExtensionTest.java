package be.hehehe.geekbot.bot;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;

@RunWith(WeldRunner.class)
public class GeekBotCDIExtensionTest {

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
