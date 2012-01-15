package be.hehehe.geekbot.bot;

/**
 * Injectable class serving as a temporary in-memory storage. Commands may store
 * objects in this structure for later use instead of using static fields.
 * 
 * 
 */
public interface State {

	void put(Object key, Object value);

	Object get(Object key);

	<T> T get(Object key, Class<? extends T> klass);

	void put(Object value);

	Object get();

	<T> T get(Class<? extends T> klass);
}
