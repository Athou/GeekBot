package be.hehehe.geekbot.web;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import be.hehehe.geekbot.ArquillianTest;
import be.hehehe.geekbot.WicketTestApplication;
import be.hehehe.geekbot.web.auth.LoginPage;

public class WicketTest extends ArquillianTest {

	private WicketTester wicketTester;

	@Inject
	BeanManager beanManager;

	@Before
	public void initTester() {
		wicketTester = new WicketTester(new WicketTestApplication(beanManager));
	}

	@Test
	public void testHomePage() {
		wicketTester.startPage(HomePage.class);
	}

	@Test
	public void testLoginPage() {
		wicketTester.startPage(LoginPage.class);
	}

	@Test
	public void testScoreboardPage() {
		wicketTester.startPage(QuizzScorePage.class);
	}

	@Test
	public void testScoreMergePage() {
		wicketTester.startPage(QuizzMergePage.class);
	}

	@Test
	public void testLogPage() {
		wicketTester.startPage(LogViewerPage.class);
	}

}
