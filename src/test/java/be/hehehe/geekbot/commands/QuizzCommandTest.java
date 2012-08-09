package be.hehehe.geekbot.commands;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import be.hehehe.geekbot.ArquillianTest;

public class QuizzCommandTest extends ArquillianTest {

	@Inject
	QuizzCommand quizzCommand;

	@Test
	public void matchIntegersTest() {
		Assert.assertTrue(quizzCommand.matches("1000", "1 000"));
	}

	@Test
	public void matchStrippedAccentsTest() {
		Assert.assertTrue(quizzCommand.matches("garcon", "garçon"));
		Assert.assertTrue(quizzCommand.matches("education", "éducation"));
		Assert.assertTrue(quizzCommand.matches("état", "ETAT"));
	}

	@Test
	public void matchAlmostTest() {
		Assert.assertTrue(quizzCommand.matches("paquebo", "Paquebot"));
	}
}
