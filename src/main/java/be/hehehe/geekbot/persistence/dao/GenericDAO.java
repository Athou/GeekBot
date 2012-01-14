package be.hehehe.geekbot.persistence.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Criteria;
import org.hibernate.Session;

import be.hehehe.geekbot.persistence.EntityManagerHelper;

public abstract class GenericDAO<T> {

	protected static EntityManager em;
	protected CriteriaBuilder builder;
	private Class<T> genericType;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		ParameterizedType type = (ParameterizedType) getClass()
				.getGenericSuperclass();
		genericType = (Class<T>) type.getActualTypeArguments()[0];
		em = EntityManagerHelper.createEntityManager();
		builder = em.getCriteriaBuilder();
	}

	public void save(T object) {
		em.getTransaction().begin();
		em.persist(object);
		em.getTransaction().commit();
	}

	public void update(T... objects) {
		em.getTransaction().begin();
		for (Object object : objects) {
			em.merge(object);
		}
		em.getTransaction().commit();
	}

	public void delete(T object) {
		em.getTransaction().begin();
		object = em.merge(object);
		em.remove(object);
		em.getTransaction().commit();
	}

	public void deleteById(Object id) {
		em.getTransaction().begin();
		Object ref = em.getReference(genericType, id);
		em.remove(ref);
		em.getTransaction().commit();
	}

	public T findById(long id) {
		T t = em.find(genericType, id);
		return t;
	}

	public List<T> findAll() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(genericType);
		query.from(genericType);
		return em.createQuery(query).getResultList();
	}

	public List<T> findAll(int startIndex, int count) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(genericType);
		query.from(genericType);
		TypedQuery<T> q = em.createQuery(query);
		q.setMaxResults(count);
		q.setFirstResult(startIndex);
		return q.getResultList();

	}

	public long getCount() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(genericType);
		query.select(builder.count(root));
		return em.createQuery(query).getSingleResult();
	}

	public <Y> List<T> findByField(SingularAttribute<T, Y> field, Object value) {
		CriteriaQuery<T> query = builder.createQuery(genericType);
		Root<T> root = query.from(genericType);
		query.where(builder.equal(root.get(field), value));
		return em.createQuery(query).getResultList();
	}

	protected Session getSession() {
		return (Session) em.getDelegate();
	}

	protected Criteria createCriteria() {
		return getSession().createCriteria(genericType);
	}

}
