package be.hehehe.geekbot.web.utils;

import org.apache.wicket.model.Model;

public class StringModel extends Model<String> {

	private static final long serialVersionUID = 1L;

	private String wrapped;

	public StringModel() {
		this(null);
	}

	public StringModel(String wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public String getObject() {
		return wrapped;
	}

	@Override
	public void setObject(String object) {
		this.wrapped = object;
	}
}
