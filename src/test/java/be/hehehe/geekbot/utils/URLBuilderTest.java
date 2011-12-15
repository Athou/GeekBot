package be.hehehe.geekbot.utils;

import junit.framework.Assert;

import org.junit.Test;

public class URLBuilderTest {

	@Test
	public void testURL() {
		URLBuilder url = new URLBuilder("http://www.commabeat.com/cc");
		url.addParam("a", "b");
		Assert.assertEquals(url.build(), "http://www.commabeat.com/cc?a=b");
	}

	@Test
	public void testURL2() {
		URLBuilder url = new URLBuilder("http://www.commabeat.com/cc");
		url.addParam("a", "b");
		url.addParam("c", "d");
		Assert.assertEquals(url.build(), "http://www.commabeat.com/cc?a=b&c=d");
	}

	@Test
	public void testURL3() {
		URLBuilder url = new URLBuilder("http://www.commabeat.com/cc/");
		url.addParam("a", "b");
		Assert.assertEquals(url.build(), "http://www.commabeat.com/cc/?a=b");
	}

	@Test
	public void testURL4() {
		URLBuilder url = new URLBuilder(
				"http://www.commabeat.com/cc?param=true");
		url.addParam("a", "b");
		url.addParam("c", "d");
		Assert.assertEquals(url.build(),
				"http://www.commabeat.com/cc?param=true&a=b&c=d");
	}

}
