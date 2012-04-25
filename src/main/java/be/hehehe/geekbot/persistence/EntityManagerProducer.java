package be.hehehe.geekbot.persistence;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Singleton
public class EntityManagerProducer {

	private static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("geekbotPU");

	@Produces
	public EntityManager createEntityManager() {
		return emf.createEntityManager();
	}
}
