package be.hehehe.geekbot.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerHelper {
	private static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("geekbotPU");

	public static EntityManagerFactory getInstance() {
		return emf;
	}
}
