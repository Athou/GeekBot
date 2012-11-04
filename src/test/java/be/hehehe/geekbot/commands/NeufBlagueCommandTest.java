package be.hehehe.geekbot.commands;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import be.hehehe.geekbot.ArquillianTest;

public class NeufBlagueCommandTest extends ArquillianTest {

	@Inject
	NeufBlagueCommand command;

	@Test
	public void test() throws IOException {
		String string = command.lol();
		Assert.assertTrue(StringUtils.isNotBlank(string));
	}
}
