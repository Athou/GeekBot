package be.hehehe.geekbot.web;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.BeforeClass;
import org.junit.Test;

import be.hehehe.geekbot.ArquillianTest;
import be.hehehe.geekbot.web.auth.LoginPage;

public class WicketTest extends ArquillianTest {

	private static WicketTester wicketTester;

	@BeforeClass
	public static void initTester() {
		wicketTester = new WicketTester(new WicketApplication());
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
