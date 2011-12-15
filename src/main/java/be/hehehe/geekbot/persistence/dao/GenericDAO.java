package be.hehehe.geekbot.persistence.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;

import be.hehehe.geekbot.persistence.EntityManagerHelper;

@Named
public abstract class GenericDAO<T> {

	protected EntityManager em;
	private Class<T> genericType;

	@SuppressWarnings("unchecked")
	public GenericDAO() {
		em = EntityManagerHelper.getInstance().createEntityManager();
		ParameterizedType type = (ParameterizedType) getClass()
				.getGenericSuperclass();
		genericType = (Class<T>) type.getActualTypeArguments()[0];

	}

	public void save(T object) {
		em.getTransaction().begin();
		em.persist(object);
		em.getTransaction().commit();
	}

	public T findById(long id) {
		T t = em.find(genericType, id);
		return t;
	}

	@SuppressWarnings("unchecked")
	public List<T> findByExample(T exampleInstance) {
		Criteria crit = getSession().createCriteria(genericType);
		Example example = Example.create(exampleInstance);
		crit.add(example);
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		Criteria crit = getSession().createCriteria(genericType);
		return crit.list();
	}

	public long getCount() {
		Number number = (Number) getSession().createCriteria(genericType)
				.setProjection(Projections.rowCount()).uniqueResult();
		return number.longValue();
	}

	protected Session getSession() {
		return (Session) em.getDelegate();
	}

	protected Criteria createCriteria() {
		return getSession().createCriteria(genericType);
	}

}
