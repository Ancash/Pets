package de.ancash.pets.utils;

public enum Rarity {

	COMMON("COMMON", "§f§l"),
	UNCOMMON("UNCOMMON", "§a§l"),
	RARE("RARE", "§9§l"),
	EPIC("EPIC", "§5§l"),
	LEGENDARY("LEGENDARY", "§6§l");
	
	private String name;
	private String prefix;
	
	Rarity(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getName() {
		return name;
	}
}
