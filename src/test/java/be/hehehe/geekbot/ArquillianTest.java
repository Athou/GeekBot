package be.hehehe.geekbot;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public abstract class ArquillianTest {

	private static WebArchive archive;

	@Deployment
	public static WebArchive createDeployment() {
		if (archive == null) {
			WebArchive war = ShrinkWrap.create(WebArchive.class);
			war.addPackages(true, "be.hehehe");
			war.merge(
					ShrinkWrap.create(GenericArchive.class)
							.as(ExplodedImporter.class)
							.as(GenericArchive.class), "/WEB-INF/classes/be",
					Filters.includeAll());

			war.addAsWebInfResource(EmptyAsset.INSTANCE,
					ArchivePaths.create("beans.xml"));
			war.addAsWebInfResource(new FileAsset(new File(
					"src/test/resources/META-INF/persistence.xml")),
					"classes/META-INF/persistence.xml");
			war.addAsWebInfResource(new FileAsset(new File(
					"src/main/webapp/WEB-INF/web.xml")), "web.xml");
			archive = war;
		}
		return archive;
	}

}
