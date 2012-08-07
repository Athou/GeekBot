package be.hehehe.geekbot.web.auth;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import be.hehehe.geekbot.Main;
import be.hehehe.geekbot.utils.BundleService;

@SuppressWarnings("serial")
public class WicketSession extends AuthenticatedWebSession {

	public WicketSession(Request request) {
		super(request);
	}

	@Override
	public Roles getRoles() {
		Roles roles = new Roles();
		if (isSignedIn()) {
			roles.add(Roles.ADMIN);
		}
		return roles;
	}

	@Override
	public boolean authenticate(String username, String password) {
		String adminPassword = Main.getBean(BundleService.class)
				.getAdminPassword();
		if (StringUtils.equals(password, adminPassword)) {
			setAttribute("name", username);
			return true;
		} else {
			return false;
		}
	}

}