package ca.loobo.restbot.param;

import java.net.URI;
import java.net.URISyntaxException;

import ca.loobo.restbot.CaseFinder;
import ca.loobo.restbot.Context;

public abstract class URIParamProducer implements ParamProducer{
	protected Context context;
	
	public URIParamProducer (Context context) {
		this.context = context;
	}

	public Context getContext() {
		return this.context;
	}
	
	@Override
	public String value(CaseFinder finder, String valueUrl) {
		URI uri;
		try {
			if (valueUrl.startsWith("$")) {
				valueUrl = valueUrl.substring(1);
			}
			uri = new URI(valueUrl);
			return value(finder, uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	abstract public String value(CaseFinder finder, URI valueUrl);
}
