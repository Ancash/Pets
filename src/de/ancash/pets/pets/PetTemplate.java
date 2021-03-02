package de.ancash.pets.pets;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.ancash.pets.utils.Ability;
import de.ancash.pets.utils.Attribute;
import de.ancash.pets.utils.Rarity;
import de.ancash.pets.utils.datastructure.tuples.ImmutableDuplet;

public class PetTemplate {
	
	private static HashMap<String, PetTemplate> registered = new HashMap<String, PetTemplate>();
	
	private Pet common;
	private Pet uncommon;
	private Pet rare;
	private Pet epic;
	private Pet legendary;
	
	private final String name;
	private final String texture;
	
	private PetTemplate(String name, String texture) {
		this.name = name;
		this.texture = texture;
		registered.put(this.name, this);
	}
	
	public String getName() {
		return name;
	}
	
	public String getTexture() {
		return texture;
	}
	
	public static Pet getPet(String type, Rarity r) {
		return get(type).getPet(r);
	}
	
	public static void addPetToTemplate(String petName, String texture, Rarity r ,List<String> lore, Set<Ability> ability,
		List<ImmutableDuplet<Ability, List<ImmutableDuplet<String, Double>>>> upgradePerLevel, List<ImmutableDuplet<Attribute, Double>> statsUpgrade) {
		if(!registered.containsKey(petName)) 
			new PetTemplate(petName, texture);

		get(petName).createPet(r, lore, ability, upgradePerLevel, statsUpgrade);
	}
	
	public boolean createPet(Rarity r,List<String> lore, Set<Ability> ability, 
			List<ImmutableDuplet<Ability, List<ImmutableDuplet<String, Double>>>> upgradePerLevel, List<ImmutableDuplet<Attribute, Double>> statsUpgrade) {
		switch (r) {
		case COMMON:
			this.common = new Pet(this.name, r, this.texture, lore, ability, upgradePerLevel, statsUpgrade);
			return true;
		case UNCOMMON:
			this.uncommon = new Pet(this.name, r, this.texture, lore, ability, upgradePerLevel, statsUpgrade);
			return true;
		case RARE:
			this.rare = new Pet(this.name, r, this.texture, lore, ability, upgradePerLevel, statsUpgrade);
			return true;
		case EPIC:
			this.epic = new Pet(this.name, r, this.texture, lore, ability, upgradePerLevel, statsUpgrade);
			return true;
		case LEGENDARY:
			this.legendary = new Pet(this.name, r, this.texture, lore, ability, upgradePerLevel, statsUpgrade);
			return true;
		default:
			return false;
		}
	}
	
	public Pet getPet(Rarity r) {
		
		switch (r) {
		case COMMON:
			return common;
		case UNCOMMON:
			return uncommon;
		case RARE:
			return rare;
		case EPIC:
			return epic;
		case LEGENDARY:
			return legendary;
		default:
			return null;
		}
	}
	
	public static PetTemplate get(String petName) {
		return registered.get(petName);
	}
}
