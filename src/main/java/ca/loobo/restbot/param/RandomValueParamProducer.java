package ca.loobo.restbot.param;

import java.net.URI;

import org.apache.commons.lang.RandomStringUtils;

import ca.loobo.restbot.CaseFinder;
import ca.loobo.restbot.Context;

/**
 * Format:  $rands:///16
 * 			$randi:///8
 * @author robertx
 *
 */
public class RandomValueParamProducer extends URIParamProducer{

	public RandomValueParamProducer(Context context) {
		super(context);
	}
	
	@Override
	public String value(CaseFinder finder, URI valueUri) {
		int length = Integer.parseInt(valueUri.getPath().substring(1));
		String value = "";
		if ("rands".equals(valueUri.getScheme())) {
			value = RandomStringUtils.randomAlphanumeric(length);
		}
		else if ("randi".equals(valueUri.getScheme())) {
			value = RandomStringUtils.randomNumeric(length);
		}
		
		return value;
	}

	
}
