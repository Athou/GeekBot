package be.hehehe.geekbot.utils;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;

@RunWith(WeldRunner.class)
public class BotUtilsServiceTest {

	@Inject
	BotUtilsService botUtilsService;

	@Test
	public void testExtractURL() {
		String url = "http://www.commabeat.com";
		Assert.assertEquals(url,
				botUtilsService.extractURL("dezfzeg " + url + " fzegerh"));
	}

	@Test
	public void testExtractURL2() {
		String url = "www.commabeat.com";
		Assert.assertEquals("http://" + url,
				botUtilsService.extractURL("dezfzeg " + url + " fzegerh"));
	}

	@Test
	public void testExtractURL3() {
		String url = "http://youtu.be/aaa";
		Assert.assertEquals("http://www.youtube.com/watch?v=aaa",
				botUtilsService.extractURL("dezfzeg " + url + " fzegerh"));
	}

	@Test
	public void testGetRequestParametersFromURL() {
		String url = "http://youtu.be/?c=d";
		Assert.assertEquals("d",
				botUtilsService.getRequestParametersFromURL(url).get("c"));
	}

}
