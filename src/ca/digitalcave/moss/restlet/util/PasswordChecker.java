package ca.digitalcave.moss.restlet.util;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import ca.digitalcave.moss.crypto.MossHash;

import com.platinum.dpv.DictionaryPasswordValidator;

public class PasswordChecker {

	private boolean strengthEnforced = true;
	private boolean lengthEnforced = true;
	private boolean multiClassEnforced = true;
	private boolean dictionaryEnforced = true;
	private boolean historyEnforced = true;
	private boolean patternsEnforced = true;
	private boolean customEnforced = false;
	private int minimumStrength = 30;
	private int minimumLength = 8;
	private List<Pattern> patterns = Collections.emptyList();
	
	public boolean isValid(String identifier, String password) {
		return testLength(password) && testStrength(password) && testMultiClass(password) && testDictionary(password) && testHistory(identifier, password) && testPatterns(password) && testCustom(identifier, password);
	}
	
	/**
	 * Returns a code with the given strength scale:
	 * <ul>
	 * <li>0-19 very poor</li>
	 * <li>20-39 weak</li>
	 * <li>40-59 is average</li>
	 * <li>60-79 is strong</li>
	 * <li>80+ is excellent</li>
	 * <li>@param password</li>
	 * @return
	 */
	public int getStrenthScore(String password) {
		float factor = 0;

		if (hasLower(password)) factor += 2.6f;
		if (hasUpper(password)) factor += 2.6f;
		if (hasNumber(password)) factor += 1.0f;
		if (hasSpace(password)) factor += 0.1f;
		if (hasNumberSymbol(password)) factor += 1.0f;
		if (hasOther(password)) factor += 2.2f;

		return (int) (Math.pow(password.length(), 3) * factor / 100f);
	}
	
	public String getStrenthString(String password) {
		int strength = getStrenthScore(password);
		if (strength >= 80) return "Excellent";
		if (strength >= 60) return "Strong";
		if (strength >= 40) return "Average";
		if (strength >= 20) return "Weak";
		return "Very Poor";
	}
	
	public boolean hasLower(String password) {
		return password.matches(".*[a-z].*"); 
	}
	public boolean hasUpper(String password) {
		return password.matches(".*[A-Z].*");
	}
	public boolean hasNumber(String password) {
		return password.matches(".*[0-9].*");
	}
	public boolean hasSpace(String password) {
		return password.matches(".*[ ].*");
	}
	public boolean hasNumberSymbol(String password) {
		return password.matches(".*[!@#$%^&*()].*");
	}
	public boolean hasOther(String password) {
		return password.matches("/.*[^ a-zA-Z0-9!@#$%^&*()].*/");
	}
	public boolean hasSymbol(String password) {
		return hasNumberSymbol(password) || hasOther(password);
	}
	
	public boolean testMultiClass(String password) {
		return multiClassEnforced && !isSingleClass(password) ? true : false;
	}
	public boolean isSingleClass(String password) {
		return getClasses(password) < 2;
	}
	
	public int getClasses(String password) {
		int classes = 0;
		if (hasLower(password)) classes++;
		if (hasUpper(password)) classes++;
		if (hasNumber(password)) classes++;
		if (hasSpace(password)) classes++;
		if (hasNumberSymbol(password)) classes++;
		if (hasOther(password)) classes++;
		return classes; 
	}
	
	public boolean testStrength(String password) {
		return strengthEnforced && !isWeak(password) ? true : false;
	}
	public boolean isWeak(String password) {
		return getStrenthScore(password) < getMinimumStrength();
	}
	
	public boolean testLength(String password) {
		return lengthEnforced && !isShort(password) ? true : false;
	}
	public boolean isShort(String password) {
		return password.length() < getMinimumLength();
	}
	
	public boolean testDictionary(String password) {
		return dictionaryEnforced && !isInDictionary(password) ? true : false;
	}
	public boolean isInDictionary(String password) {
		try {
			final DictionaryPasswordValidator validator = DictionaryPasswordValidator.getInstance();
			return validator.isPasswordDictionaryBased(password);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	/**
	 * This method should be implemented in a subclass if history checking is required. 
	 */
	protected boolean verifyHash(String hash, String password) {
		return MossHash.verify(hash, password);
	}
	
	/**
	 * This method should be implemented in a subclass if history checking is required. 
	 */
	protected List<String> getHistory(String identifier) {
		return Collections.emptyList();
	}
	
	public boolean testHistory(String identifier, String password) {
		return historyEnforced && !isInHistory(identifier, password) ? true : false; 
	}
	public boolean isInHistory(String identifier, String password) {
		for(String hash : getHistory(identifier)){
			if (verifyHash(hash, password)) return true;
		}
		return false;
	}
	
	public boolean testPatterns(String password) {
		return patternsEnforced && !isRestricted(password) ? true : false;
	}
	public boolean isRestricted(String password) {
		for (Pattern pattern : getRestrictedPatterns()) {
			if (pattern.matcher(password).find()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean testCustom(String identifier, String password) {
		return customEnforced && !isCustom(identifier, password) ? true : false;
	}
	
	/**
	 * This method should be implemented in a subclass if other custom checks are required. 
	 */
	public boolean isCustom(String identifier, String password) {
		return false;
	}
	
	public List<Pattern> getRestrictedPatterns() {
		return patterns;
	}
	public PasswordChecker setRestrictedPatterns(List<Pattern> patterns) {
		this.patterns = patterns;
		return this;
	}
	
	public int getMinimumLength() {
		return minimumLength;
	}
	public PasswordChecker setMinimumLength(int minimumLength) {
		this.minimumLength = minimumLength;
		return this;
	}
	
	public int getMinimumStrength() {
		return minimumStrength;
	}
	public PasswordChecker setMinimumStrength(int minimumStrength) {
		this.minimumStrength = minimumStrength;
		return this;
	}
	
	public boolean isDictionaryEnforced() {
		return dictionaryEnforced;
	}
	public PasswordChecker setDictionaryEnforced(boolean dictionaryEnforced) {
		this.dictionaryEnforced = dictionaryEnforced;
		return this;
	}
	
	public boolean isHistoryEnforced() {
		return historyEnforced;
	}
	public PasswordChecker setHistoryEnforced(boolean historyEnforced) {
		this.historyEnforced = historyEnforced;
		return this;
	}
	
	public boolean isLengthEnforced() {
		return lengthEnforced;
	}
	public PasswordChecker setLengthEnforced(boolean lengthEnforced) {
		this.lengthEnforced = lengthEnforced;
		return this;
	}
	
	public boolean isStrengthEnforced() {
		return strengthEnforced;
	}
	public PasswordChecker setStrengthEnforced(boolean strengthEnforced) {
		this.strengthEnforced = strengthEnforced;
		return this;
	}
	
	public boolean isMultiClassEnforced() {
		return multiClassEnforced;
	}
	public PasswordChecker setMultiClassEnforced(boolean multiClassEnforced) {
		this.multiClassEnforced = multiClassEnforced;
		return this;
	}
	
	public boolean isPatternsEnforced() {
		return patternsEnforced;
	}
	public PasswordChecker setPatternsEnforced(boolean patternsEnforced) {
		this.patternsEnforced = patternsEnforced;
		return this;
	}
	
	public boolean isCustomEnforced() {
		return customEnforced;
	}
	public PasswordChecker setCustomEnforced(boolean customEnforced) {
		this.customEnforced = customEnforced;
		return this;
	}
	
}