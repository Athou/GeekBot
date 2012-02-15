package be.hehehe.geekbot.commands;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;

@RunWith(WeldRunner.class)
public class UpdateCommandTest {

	private static final String TEST_STRING = "Hello";

	@Inject
	Instance<UpdateCommand> instance;

	@Test
	public void test() {
		UpdateCommand u1 = instance.get();
		u1.state.put(TEST_STRING);
		UpdateCommand u2 = instance.get();
		Assert.assertNotSame(u1, u2);
		Assert.assertTrue(TEST_STRING.equals(u2.state.get()));
	}
}
