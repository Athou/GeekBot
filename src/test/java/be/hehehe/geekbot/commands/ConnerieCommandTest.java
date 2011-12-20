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
public class ConnerieCommandTest {

	@Inject
	ConnerieCommand connerieCommand;

	@Before
	public void init() {
		ConnerieDAO dao = EasyMock.createMock(ConnerieDAO.class);
		connerieCommand.dao = dao;
	}

	@Test
	public void test() {
		String keywords = "hello test";
		String result = "blabla hello test blabla";
		EasyMock.expect(
				connerieCommand.dao.getRandomMatching(keywords.split(" ")))
				.andReturn(new Connerie(result));
		EasyMock.replay(connerieCommand.dao);
		Assert.assertEquals(result,
				connerieCommand.getRandQuote(new TriggerEventImpl(keywords)));
		EasyMock.verify(connerieCommand.dao);
	}

	@Test
	public void test2() {
		String keywords = "hello test";
		EasyMock.expect(
				connerieCommand.dao.getRandomMatching(keywords.split(" ")))
				.andReturn(null);
		EasyMock.replay(connerieCommand.dao);
		Assert.assertEquals(null,
				connerieCommand.getRandQuote(new TriggerEventImpl(keywords)));
		EasyMock.verify(connerieCommand.dao);
	}

}
