package be.hehehe.geekbot.commands;

import javax.inject.Inject;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;
import be.hehehe.geekbot.bot.TriggerEventImpl;
import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.persistence.model.Connerie;

@RunWith(WeldRunner.class)
public class RandomCommandTest {

	@Inject
	RandomCommand randomCommand;

	@Before
	public void init() {
		ConnerieDAO dao = EasyMock.createMock(ConnerieDAO.class);
		randomCommand.dao = dao;
	}

	@Test
	public void test() {
		String keywords = "hello test";
		String result = "blabla hello test blabla";
		EasyMock.expect(
				randomCommand.dao.getRandomMatching(keywords.split(" ")))
				.andReturn(new Connerie(result));
		EasyMock.replay(randomCommand.dao);
		Assert.assertEquals(result,
				randomCommand.getRandQuote(new TriggerEventImpl(keywords)));
		EasyMock.verify(randomCommand.dao);
	}

	@Test
	public void test2() {
		String keywords = "hello test";
		EasyMock.expect(
				randomCommand.dao.getRandomMatching(keywords.split(" ")))
				.andReturn(null);
		EasyMock.replay(randomCommand.dao);
		Assert.assertEquals(null,
				randomCommand.getRandQuote(new TriggerEventImpl(keywords)));
		EasyMock.verify(randomCommand.dao);
	}

}
