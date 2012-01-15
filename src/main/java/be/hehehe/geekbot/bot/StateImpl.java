package be.hehehe.geekbot.bot;

import java.util.Map;

import javax.enterprise.inject.Alternative;

import com.google.common.collect.Maps;

@Alternative
public class StateImpl implements State {

	private Map<Object, Object> state = Maps.newHashMap();
	private Object singleObject;

	@Override
	public void put(Object key, Object value) {
		state.put(key, value);
	}

	@Override
	public Object get(Object key) {
		return state.get(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<? extends T> klass) {
		return (T) get(key);
	}

	@Override
	public void put(Object value) {
		singleObject = value;
	}

	@Override
	public Object get() {
		return singleObject;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Class<? extends T> klass) {
		return (T) singleObject;
	}

}
