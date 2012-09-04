package be.hehehe.geekbot.commands;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import be.hehehe.geekbot.ArquillianTest;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;

public class HoroscopeCommandTest extends ArquillianTest {

	@Inject
	HoroscopeCommand horoscopeCommand;

	@Test
	public void horoscopeTest() {
		TriggerEvent event = new TriggerEventImpl("balance");
		String horoscope = horoscopeCommand.getHoroscope(event);
		Assert.assertTrue(StringUtils.isNotBlank(horoscope));
	}
}
