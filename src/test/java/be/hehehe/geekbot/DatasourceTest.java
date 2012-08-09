package be.hehehe.geekbot;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;

@Singleton
@DataSourceDefinition(name = "java:global/datasources/inmemory", minPoolSize = 0, initialPoolSize = 0, className = "org.h2.Driver", user = "sa", password = "", url = "jdbc:h2:mem:test", properties = { "eclipselink.ddl-generation=drop-and-create-tables" })
// @DataSourceDefinition(name = "java:global/datasources/inmemory", className =
// "org.apache.derby.jdbc.ClientDataSource", url = "jdbc:derby:memory:testdb")
public class DatasourceTest {

}
