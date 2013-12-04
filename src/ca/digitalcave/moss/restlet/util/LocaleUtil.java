package ca.digitalcave.moss.restlet.util;

import java.util.List;
import java.util.Locale;

import org.restlet.data.Language;
import org.restlet.data.Preference;

public class LocaleUtil {
	/**
	 * This method returns a Locale corresponding to the first entry in the AcceptLanguages.  This
	 * is not a perfect solution (for instance, if the first locale is, say, Swahili (or some other 
	 * language not supported in a given translation), and the second is, say, French, this method
	 * will still return Swahihi (which will then fall back to loading the default English resource 
	 * bundle), even though the client would have preferred French.  Given how resource bundles are
	 * loaded, I don't know if it is possible to change this behaviour...
	 * 
	 * This method MUST return a locale instance.  If the acceptLanguages parameter is invalid
	 * or null, default to returning English.
	 * @param acceptLanguages
	 * @return
	 */
	public static Locale parseLocales(List<Preference<Language>> acceptLanguages){
		if (acceptLanguages == null || acceptLanguages.size() == 0) return Locale.ENGLISH;
		
		for (Preference<Language> language : acceptLanguages){
			final String[] split = language.getMetadata().getName().split("-");
			if (split.length == 1){
				return new Locale(split[0].toLowerCase());
			}
			else if (split.length == 2){
				return new Locale(split[0].toLowerCase(), split[1].toUpperCase());
			}
		}
		
		return Locale.ENGLISH;
	}
}
