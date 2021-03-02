package de.ancash.pets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.pets.commands.PetCommands;
import de.ancash.pets.listeners.JoinListener;
import de.ancash.pets.listeners.PetInventoryClickListener;
import de.ancash.pets.listeners.RightClickPetItemListener;
import de.ancash.pets.listeners.XPCollectListener;
import de.ancash.pets.pets.PetTemplate;
import de.ancash.pets.pets.PlayerPet;
import de.ancash.pets.utils.Ability;
import de.ancash.pets.utils.Attribute;
import de.ancash.pets.utils.Chat;
import de.ancash.pets.utils.Chat.ChatLevel;
import de.ancash.pets.utils.datastructure.tuples.ImmutableDuplet;
import de.ancash.pets.utils.datastructure.tuples.Tuple;
import de.ancash.pets.utils.FileUtils;
import de.ancash.pets.utils.Rarity;

public class Pets extends JavaPlugin{

	private static final Set<String> abilityProperties = new HashSet<String>();
	
	static {
		abilityProperties.add("strength");
		abilityProperties.add("intelligence");
		abilityProperties.add("speed");
		abilityProperties.add("defense");
		abilityProperties.add("health");
		abilityProperties.add("critChance");
		abilityProperties.add("critDamage");
		abilityProperties.add("x");
		abilityProperties.add("y");
	}
	
	private static Pets plugin;
	
	public static Pets getInstance() {
		return plugin;
	}
	
	@Override
	public void onEnable() {
		plugin = this;
		try {
			checkFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Chat.sendMessage("Loading Pets from File...", ChatLevel.INFO);
		loadPetsFromFile();
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		
		pm.registerEvents(new RightClickPetItemListener(),this);
		pm.registerEvents(new PetInventoryClickListener(), this);
		pm.registerEvents(new XPCollectListener(), this);
		pm.registerEvents(new JoinListener(), this);
		
		getCommand("pet").setExecutor(new PetCommands());
		
		for(Player p : Bukkit.getOnlinePlayers()) new PlayerPet(0, 0, null, null, null, p);
	}
	
	@Override
	public void onDisable() {
		Ability.clear();
		for(Player p : Bukkit.getOnlinePlayers()) PlayerPet.get(p.getUniqueId()).delete();
	}
	
	private void checkFiles() throws IOException {
		File directory = new File("plugins/Pets/pets");
		if(!directory.exists()) {
			directory.mkdirs();
			copyPetsFromPlugin();
		}
		if(!new File("plugins/Pets/skills.yml").exists())
			FileUtils.copyInputStreamToFile(getResource("resources/skills.yml"), new File("plugins/Pets/skills.yml"));
		if(!new File("plugins/Pets/config.yml").exists())
			FileUtils.copyInputStreamToFile(getResource("resources/config.yml"), new File("plugins/Pets/config.yml"));
	}
	
	private void loadPetsFromFile() {
		File dir = new File("plugins/Pets/pets");
		for(File f : dir.listFiles()) {
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			for(Rarity rarity : Rarity.values()) {
				if(fc.getStringList(rarity.getName().toLowerCase() + ".lore").size() == 0) continue;
				
				Set<Ability> abilities = new HashSet<Ability>();
				List<ImmutableDuplet<Ability, List<ImmutableDuplet<String, Double>>>> upgradePerLevel = new ArrayList<ImmutableDuplet<Ability,List<ImmutableDuplet<String,Double>>>>();
				List<ImmutableDuplet<Attribute, Double>> statsUpgrade = new ArrayList<ImmutableDuplet<Attribute,Double>>();
				
				if(fc.getConfigurationSection(rarity.getName().toLowerCase() + ".abilities") != null) {
					for(String ab : fc.getConfigurationSection(rarity.getName().toLowerCase() + ".abilities").getKeys(false)) {
						Ability a = Ability.get(ab);
						if(a == null) {
							Chat.sendMessage("Couldn't find Ability " + ab + "! Skipping...", ChatLevel.WARN);
						} else {
							abilities.add(a);
							List<ImmutableDuplet<String, Double>> upgrades = new ArrayList<ImmutableDuplet<String,Double>>();
							
							for(String key : abilityProperties) {
								double ability = fc.getDouble(rarity.getName().toLowerCase() + ".abilities." + ab + "." + key);
								if(ability != 0) upgrades.add(Tuple.immutableOf(key, fc.getDouble(rarity.getName().toLowerCase() + ".abilities." + ab + "." + key)));
								Attribute att = Attribute.get(key);
								if(att != null) {
									double value = fc.getDouble(rarity.getName().toLowerCase() + ".upgradePerLevel." + key);
									if(value != 0) {
										statsUpgrade.add(Tuple.immutableOf(att, value));
									}
								}
							}
							upgradePerLevel.add(Tuple.immutableOf(a, upgrades));
							upgradePerLevel.add(Tuple.immutableOf(a, upgrades));
						}
						
						
					}
				}
				PetTemplate.addPetToTemplate(fc.getString("name"), fc.getString("texture"), rarity,
						fc.getStringList(rarity.getName().toLowerCase() + ".lore"), abilities, upgradePerLevel, statsUpgrade);
			}
		}
	}
	
	private void copyPetsFromPlugin() throws IOException {
		Set<String> toLoad = new HashSet<String>();
		toLoad.add("bee");
		toLoad.add("chicken");
		toLoad.add("elephant");
		toLoad.add("pig");
		toLoad.add("rabbit");
		
		for(String pet : toLoad) {
			FileUtils.copyInputStreamToFile(getResource("resources/pets/" + pet + ".yml"), new File("plugins/Pets/pets/" + pet + ".yml"));
		}
	}
	
	
}
