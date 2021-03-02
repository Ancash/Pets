package de.ancash.pets.pets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.ancash.pets.utils.Ability;
import de.ancash.pets.utils.Attribute;
import de.ancash.pets.utils.Chat;
import de.ancash.pets.utils.ItemStackUtils;
import de.ancash.pets.utils.MathsUtils;
import de.ancash.pets.utils.Rarity;
import de.ancash.pets.utils.datastructure.tuples.ImmutableDuplet;
import de.ancash.pets.utils.datastructure.tuples.Tuple;
import de.tr7zw.nbtapi.NBTItem;
 
public class Pet {

	private final String name;
	private ItemStack is;
	private final String texture;
	private final String type;
	private final List<String> lore;
	private final Set<Ability> ability;
	private List<ImmutableDuplet<Attribute, Double>> statsUpgrade = new ArrayList<ImmutableDuplet<Attribute,Double>>();
	private List<ImmutableDuplet<Ability, List<ImmutableDuplet<String, Double>>>> upgradePerLevel;
	private final Rarity rarity;
	
	
	public Pet(String type, Rarity rarity, String texture, List<String> lore, Set<Ability> ability, List<ImmutableDuplet<Ability, List<ImmutableDuplet<String, Double>>>> upgradePerLevel2, List<ImmutableDuplet<Attribute, Double>> statsUpgrade2) {
		this.upgradePerLevel = upgradePerLevel2;
		this.name = rarity.getPrefix() + type;
		this.texture = texture;
		this.ability = ability;
		this.type = type;
		this.rarity = rarity;
		this.statsUpgrade = statsUpgrade2;
		List<String> loreWithAbilities = new ArrayList<String>();
		lore.forEach(str ->{
			if(str.equals("%progress%") || !str.contains("%") || (!String.valueOf(str.charAt(0)).equals("%") && String.valueOf(str.charAt(str.length() - 1)).equals("%")) || (String.valueOf(str.charAt(0)).equals("%") && !String.valueOf(str.charAt(str.length() - 1)).equals("%")) || (!String.valueOf(str.charAt(0)).equals("%") && !String.valueOf(str.charAt(str.length() - 1)).equals("%"))) {
				loreWithAbilities.add(str);
			} else {
				boolean canAdd = false;
				if(ability != null && !ability.isEmpty()) {
					for(Ability a : ability) {
						if(a.getPlaceholder().toLowerCase().replace("_", "").equals(str.toLowerCase())) canAdd = true;
					}
				}
				if(!canAdd) {
					if(!str.contains("%progress")) Chat.sendMessage("Tried To Add Ability(" + str.replace("%","") + ") For Pet(" + name + "§r) Which Doesn't Have It! Skipping...", Chat.ChatLevel.WARN);
				} else {
					loreWithAbilities.addAll(Ability.getDescription(str.replace("%", "")));
				}
			}
		});
		this.lore = loreWithAbilities;
		this.is = createPet(type, rarity, name, texture, this.lore);
	}
	
	public void setItemStack(ItemStack is) {
		this.is = is;
	}
	
	public Set<Ability> getFirstbilities(){
		return ability;
	}
	
	public ItemStack getItemStack() {
		return is;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTexture() {
		return texture;
	}
	
	public Rarity getRarity() {
		return rarity;
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public Pet clone() {
		return new Pet(type, rarity, texture, lore, ability, upgradePerLevel, statsUpgrade);
	}
	
	private ItemStack createPet(String type, Rarity rarity, String displayname, String texture, List<String> lore) {
		ItemStack pet = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		ItemMeta im = pet.getItemMeta();
		for(int i = 0; i<lore.size(); i++) lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
		im.setLore(lore);
		im.setDisplayName(displayname.replace("§l", ""));
		pet.setItemMeta(im);
		NBTItem nbt = new NBTItem(pet);
		nbt.setString("petType", type);
		nbt.setString("petRarity", rarity.getName());
		pet = nbt.getItem();
		pet = ItemStackUtils.setTexture(pet, texture);
		
		return pet;
	}
	
	public ItemStack replacePlaceholder(int level, double currentXp, double xpRequirements) {
		ItemStack clone = is.clone();
		HashMap<String, String> placeholder = new HashMap<String, String>();
		//xp stuff
		placeholder.put("%currentXp%", "" + currentXp);
		placeholder.put("%level%", "" + level);
		placeholder.put("%requiriedXp%", "" + xpRequirements);
		StringBuilder progress = new StringBuilder();
		progress.append("§a");
		for(double d = 0.05; d <= 1; d = d +0.05) {
			if(currentXp / xpRequirements >= d) {
				progress.append("-");
			} else {
				progress.append("§f-");
			}
		}
		progress.append(" §e" + (int) currentXp + "§6/§e" + (int) xpRequirements);
		placeholder.put("%progress%", level == 100 ? "§b§lMAX§r" : progress.toString());
		placeholder.put("%progressPercentage%", "" + MathsUtils.round(currentXp/xpRequirements, 1));
		placeholder.put("%nextLevel%", "" + (level == 100 ? level : level + 1));
		
		//stats
		for(Attribute att : Attribute.values()) {
			placeholder.put(att.getPlaceholderWithoutVowels(), "" + (int) (getUpgradePerLevel(att) * level));
		}
		for(Ability a : ability) {
			ImmutableDuplet<Double, Double> xy = getFirstbilityUpgradePerLevel(a);
			if(xy == null) continue;
			placeholder.put("%" + (a.getPlaceholderWithoutVowels().replace("%", "")) + "X", "" + MathsUtils.round(xy.getFirst()*level, 1));
			placeholder.put("%" + (a.getPlaceholderWithoutVowels().replace("%", "")) + "Y", "" + MathsUtils.round(xy.getSecond()*level, 1));
		}
		clone = ItemStackUtils.replacePlaceholder(clone, placeholder);
		
		NBTItem nbt = new NBTItem(clone);
		nbt.setDouble("petCurrentXp", currentXp);
		nbt.setInteger("petLevel", level);
		
		
		return nbt.getItem();
	}
	
	public ImmutableDuplet<Double, Double> getFirstbilityUpgradePerLevel(Ability a) {
		for(ImmutableDuplet<Ability, List<ImmutableDuplet<String, Double>>> pair : upgradePerLevel) {
			if(pair.getFirst().equals(a)) {
				double x = pair.getSecond().size() >= 1 ? pair.getSecond().get(0).getSecond() : 0;
				double y = pair.getSecond().size() >= 2 ? pair.getSecond().get(1).getSecond() : 0;
				return Tuple.immutableOf(x, y);
			}
		}
		return null;
	}
	
	public double getUpgradePerLevel(Attribute att) {
		for(int i = 0; i<statsUpgrade.size(); i++) {
			if(statsUpgrade.get(i).getFirst().equals(att)) return statsUpgrade.get(i).getSecond();
		}
		return 0;
	}
}
