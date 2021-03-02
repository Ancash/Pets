package de.ancash.pets.utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Ability {

	//cc cd defense health speed strength 
	
	FORAGING_FORAGING_XP_BOOST,
	MINING_MINING_XP_BOOST,
	FISHING_FISHING_XP_BOOST,
	COMBAT_COMBAT_XP_BOOST,
	FARMING_FARMING_XP_BOOST,
	ENCHANTING_ENCHANTING_XP_BOOST,
	
	FARMING_HIVE,
	FARMING_BUSY_BUZZ_BUZZ,
	FARMING_WEAPONIZED_HONEY,
	FARMING_LIGHT_FEET,
	FARMING_EGGSTRA,
	FARMING_MIGHTY_CHICKENS,
	FARMING_STOMP,
	FARMING_WALKING_FORTRESS,
	FARMING_TRUNK_EFFICIENCY,
	FARMING_RIDEABLE,
	FARMING_RUN,
	FARMING_SPRINT,
	FARMING_TRAMPLE,
	FARMING_HAPPY_FEET,
	FARMING_EFFICIENT_FARMING,
	
	MINING_CANDY_LOVER,
	MINING_NIGHTMARE,
	MINING_FAST_HOOKS,
	MINING_MORE_STONKS,
	MINING_PEARL_MUNCHER,
	MINING_PEARL_POWERED,
	MINING_RIDEABLE,
	MINING_FORTIFY,
	MINING_STEADY_GROUND,
	MINING_TRUE_DEFENSE_BOOST,
	MINING_DEXTERITY,
	MINING_STRONGER_BONES,
	MINING_WITHER_BLOOD,
	MINING_DEATHS_TOUCH,
	MINING_MITHRIL_AFFINITY,
	MINING_THE_SMELL_OF_POWDER,
	MINING_DANGER_AVERSE,
	
	COMBAT_HUNTER,
	COMBAT_OMEN,
	COMBAT_SUPERNATURAL,
	COMBAT_NETHER_EMBODIMENT,
	COMBAT_BLING_ARMOR,
	COMBAT_FUSION_STYLE_POTATO,
	COMBAT_END_STRIKE,
	COMBAT_ONE_WITH_THE_DRAGON,
	COMBAT_SUPERIOR,
	COMBAT_ENDERIAN,
	COMBAT_TELEPORT_SAVVY,
	COMBAT_ZEALOT_MADNESS,
	COMBAT_AMPLIFIED_HEALING,
	COMBAT_ZOMBIE_ARM,
	COMBAT_REAPER_SOUL,
	COMBAT_LAST_STAND,
	COMBAT_RICOCHET,
	COMBAT_TOSS,
	COMBAT_ODYSSEY,
	COMBAT_LEGENDARY_CONSTITUTION,
	COMBAT_PERPETUAL_EMPATHY,
	COMBAT_KING_OF_KINGS,
	COMBAT_RIDEABLE,
	COMBAT_GALLOP,
	COMBAT_RIDE_INTO_BATTLE,
	COMBAT_SCAVENGER,
	COMBAT_FINDER,
	COMBAT_FURY_CLAWS,
	COMBAT_SLIMY_MINIONS,
	COMBAT_SALT_BLADE,
	COMBAT_HOT_EMBER,
	COMBAT_REKINDLE,
	COMBAT_FOURTH_FLARE,
	COMBAT_MAGIC_BIRD,
	COMBAT_ETERNAL_COINS,
	COMBAT_BACON_FARMER,
	COMBAT_PORK_MASTER,
	COMBAT_GIANT_SLAYER,
	COMBAT_BONE_ARROWS,
	COMBAT_COMBO,
	COMBAT_SKELETAL_DEFENSE,
	COMBAT_BLIZZARD,
	COMBAT_FROSTBITE,
	COMBAT_SNOW_CANNON,
	COMBAT_ONE_WITH_THE_SPIDER,
	COMBAT_WEB_WEAVER,
	COMBAT_SPIDER_WHISPERER,
	COMBAT_SPIRIT_ASSISTANCE,
	COMBAT_SPIRIT_LEAP,
	COMBAT_SPIRIT_COOLDOWNS,
	COMBAT_WEBBED_CELLS,
	COMBAT_EIGHT_LEGS,
	COMBAT_ARACHNID_SLAYER,
	COMBAT_MERCILESS_SWIPE,
	COMBAT_HEMORRHAGE,
	COMBAT_APEX_PREDATOR,
	COMBAT_TURTLE_TACTICS,
	COMBAT_GENIUS_AMNIOTE,
	COMBAT_UNFLIPPABLE,
	COMBAT_ALPHA_DOG,
	COMBAT_PACK_LEADER,
	COMBAT_CHOMP,
	COMBAT_ROTTEN_BLADE,
	COMBAT_LIVING_DEAD,
	
	FORAGING_GOOD_HEART,
	FORAGING_HIGHER_GROUND,
	FORAGING_LONG_NECK,
	FORAGING_PRIMAL_FORCE,
	FORAGING_FIRST_POUNCE,
	FORAGING_KING_OF_THE_JUNGLE,
	FORAGING_TREEBORN,
	FORAGING_VINE_SWING,
	FORAGING_EVOLVED_AXES,
	FORAGING_TREE_HUGGER,
	FORAGING_TREE_ESSENCE,
	
	FISHING_COLD_BREEZE,
	FISHING_ICE_SHIELDS,
	FISHING_YETI_FURY,
	FISHING_INGEST,
	FISHING_BULK,
	FISHING_ARCHIMEDES,
	FISHING_POD_TACTICS,
	FISHING_ECHOLOCATION,
	FISHING_SPLASH_SURPRISE,
	FISHING_QUICK_REEL,
	FISHING_WATER_BENDER,
	FISHING_DEEP_SEA_DIVER,
	FISHING_BLOOD_SCENT,
	FISHING_ENHANCED_SCALES,
	FISHING_FEEDING_FRENZY,
	FISHING_INK_SPECIALITY,
	FISHING_MORE_INK,
	
	ENCHANTING_LAZERBEAM,
	ENCHANTING_MANA_POOL,
	
	ALCHEMY_RADIANT_REGENERATION,
	ALCHEMY_HUNGRY_HEALER,
	ALCHEMY_POWERFUL_POTIONS,
	ALCHEMY_FLAMBOYANT,
	ALCHEMY_REPEAT,
	ALCHEMY_BIRD_DISCOURSE,
	ALCHEMY_MANA_SAVER,
	ALCHEMY_OVERHEAL,
	ALCHEMY_DUNGEON_WIZARD;
	
	private final String placeholder;
	//private Set<Pair<Attribute, Double>> attributes = new HashSet<Pair<Attribute,Double>>();
	private final List<String> description;
	
	private static HashMap<String, List<String>> loaded = new HashMap<String, List<String>>();
	private static File skillsDesc = new File("plugins/Pets/skills.yml");
	private static FileConfiguration fc = YamlConfiguration.loadConfiguration(skillsDesc);
	
	Ability() {
		check();
		this.description = getLore();
		StringBuilder sb = new StringBuilder();
		sb.append("%");
		for(int i = 1; i<this.name().split("_").length; i++) {
			sb.append(this.name().split("_")[i]);
			if(i != this.name().split("_").length - 1) {
				sb.append("_");
			}
		}
		sb.append("%");
		this.placeholder = sb.toString().toLowerCase();
	}
	
	private void check() {
		if(loaded == null) loaded = new HashMap<String, List<String>>();
		if(skillsDesc == null) skillsDesc = new File("plugins/Pets/skills.yml");
		if(fc == null) fc = YamlConfiguration.loadConfiguration(skillsDesc);
	}
	
	private List<String> getLore() {
		if(loaded.containsKey(placeholder)) return loaded.get(placeholder);	
		StringBuilder type = new StringBuilder();
		type.append(this.name().toLowerCase().split("_")[0] + ".");
		StringBuilder exact = new StringBuilder();
		for(int i = 1; i<this.name().split("_").length; i++) exact.append((i == 1 ? this.name().toLowerCase().split("_")[i] : WordUtils.capitalize(this.name().toLowerCase().split("_")[i])));
		if(fc.getStringList(type.toString() + exact.toString()) == null || fc.getStringList(type.toString() + exact.toString()).size() == 0) {
			System.out.println("No description for: " + this.name());
		}
		return fc.getStringList(type.toString() + exact.toString());
	}
	
	public List<String> getDescription(){
		return description;
	}
	
	public String getPlaceholderWithoutVowels() {
		StringBuilder withoutVowels = new StringBuilder();
		String temp = "%" + WordUtils.capitalize(placeholder.replace("_", " ").replace("%", "")).replace(" ", "") + "%";
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
	
	public String getPlaceholder() {
		return placeholder;
	}
	
	public static Ability get(String name) {
		for(Ability a : values()) {
			if(a.getPlaceholder().replace("%", "").replace("_", "").equals(name.toLowerCase())) return a;
		}
		return null;
	}
	
	public static List<String> getDescription(String abilitName) {
		if(loaded.containsKey(abilitName)) return loaded.get(abilitName);
		for(String key : fc.getKeys(true)) {
			if(key.contains(abilitName)) return fc.getStringList(key);
		}
		return null;
	}
	
	public static void clear() {
		loaded.clear();
	}
}
