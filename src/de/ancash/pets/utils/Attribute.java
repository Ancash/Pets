package de.ancash.pets.utils;

import org.apache.commons.lang.WordUtils;

public enum Attribute {
	
	STRENGTH("Strength", Integer.MAX_VALUE),
	CRIT_CHANCE("Crit Chance", Integer.MAX_VALUE),
	CRIT_DAMAGE("Crit Damage", Integer.MAX_VALUE),
	HEALTH("Health", Integer.MAX_VALUE),
	DEFENSE("Defense", Integer.MAX_VALUE),
	INTELLIGENCE("Intelligence", Integer.MAX_VALUE),
	SPEED("Speed", Integer.MAX_VALUE);
	
	
	private final String name;
	private final int maxValue;
	
	Attribute(String string, int maxValue) {
		this.name = string;
		this.maxValue = maxValue;
	}
	
	public static Attribute get(String name) {
		for(Attribute att : values()) {
			if(att.getName().toLowerCase().replace(" ", "").equals(name.toLowerCase())) return att;
		}
		return null;
	}
	
	public String getName() {
		return this.name;
	}

	public int getMaxValue() {
		return this.maxValue;
	}
	
	public String getPlaceholderWithoutVowels() {
		StringBuilder withoutVowels = new StringBuilder();
		String placeholder = "%" + name;
		String temp = "%" + WordUtils.capitalize(placeholder.replace("_", " ").replace("%", "")).replace(" ", "");
		for(int i = 0; i<temp.length(); i++) {
			switch (temp.toLowerCase().charAt(i)) {
			case 'a':
				continue;
			case 'e':
				continue;
			case 'i':
				continue;
			case 'o':
				continue;
			case 'u':
				continue;
			default:
				withoutVowels.append(temp.charAt(i));
				break;
			}
		}
		withoutVowels.setCharAt(1, Character.toLowerCase(withoutVowels.charAt(1)));
		return withoutVowels.toString();
	}
}
