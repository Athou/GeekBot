package be.hehehe.geekbot.bot;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;

@RunWith(WeldRunner.class)
public class ScannerServiceTest {

	@Inject
	private ScannerService scannerService;

	@Test
	public void testTriggers() {
		Assert.assertTrue(scannerService.scanTriggers().size() > 0);
	}

	@Test
	public void testRandoms() {
		Assert.assertTrue(scannerService.scanRandom().size() > 0);
	}

	@Test
	public void testTimers() {
		Assert.assertTrue(scannerService.scanTimers().size() > 0);
	}
}
