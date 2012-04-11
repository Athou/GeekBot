package be.hehehe.geekbot.commands;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;

@RunWith(WeldRunner.class)
public class HoroscopeCommandTest {

	@Inject
	HoroscopeCommand horoscopeCommand;

	@Test
	public void quoteTest() {
		TriggerEvent event = new TriggerEventImpl("balance");
		String horoscope = horoscopeCommand.getHoroscope(event);
		Assert.assertTrue(StringUtils.isNotBlank(horoscope));
	}
}
