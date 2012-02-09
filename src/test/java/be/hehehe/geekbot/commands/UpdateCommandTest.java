package be.hehehe.geekbot.commands;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;
import be.hehehe.geekbot.commands.UpdateCommand;

@RunWith(WeldRunner.class)
public class UpdateCommandTest {

	private static final String TEST_STRING = "Hello";

	@Inject
	WeldContainer container;

	@Test
	public void test() {
		UpdateCommand u1 = container.instance().select(UpdateCommand.class)
				.get();
		u1.state.put(TEST_STRING);
		UpdateCommand u2 = container.instance().select(UpdateCommand.class)
				.get();
		Assert.assertNotSame(u1, u2);
		Assert.assertTrue(TEST_STRING.equals(u2.state.get()));
	}

}
