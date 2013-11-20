package ca.digitalcave.moss.restlet;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.moss.restlet.util.PasswordChecker;

public class PasswordCheckResource extends ServerResource {

	@Override
	protected Representation post(Representation entity) throws ResourceException {
		final PasswordChecker checker = (PasswordChecker) getRequest().getAttributes().get(PasswordChecker.class.getName());
		
		final Form form = new Form(entity);
		final String password = form.getFirstValue("password");
		
		return new WriterRepresentation(MediaType.APPLICATION_JSON) {
			@Override
			public void write(Writer w) throws IOException {
				boolean result = true;
				
				w.write("{");
				
				final int score = checker.getStrenthScore(password);
				w.write("score:");
				w.write(score);
				
				if (checker.isStrengthEnforced()) {
					final boolean passed = checker.testStrength(password);
					result &= passed;
					w.write("strength:");
					w.write(Boolean.toString(passed));
				}
				
				if (checker.isLengthEnforced()) {
					final boolean passed = checker.testLength(password);
					result &= passed;
					w.write("length:");
					w.write(Boolean.toString(passed));
				}

				if (checker.isDictionaryEnforced()) {
					final boolean passed = checker.testDictionary(password);
					result &= passed;
					w.write("dictionary:");
					w.write(Boolean.toString(passed));
				}

				if (checker.isMultiClassEnforced()) {
					final boolean passed = checker.testMultiClass(password);
					result &= passed;
					w.write("multiClass:");
					w.write(Boolean.toString(passed));
				}

				if (checker.isPatternsEnforced()) {
					final boolean passed = checker.testPatterns(password);
					result &= passed;
					w.write("restricted:");
					w.write(Boolean.toString(passed));
				}

				if (checker.isHistoryEnforced()) {
					final boolean passed = checker.testHistory(password);
					result &= passed;
					w.write("history:");
					w.write(Boolean.toString(passed));
				}

				if (checker.isCustomEnforced()) {
					final boolean passed = checker.testCustom(password);
					result &= passed;
					w.write("custom:");
					w.write(Boolean.toString(passed));
				}

				w.write("passed:");
				w.write(Boolean.toString(result));
				
				w.write("}");
				w.flush();
			}
		};
	}
}
