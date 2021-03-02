package de.ancash.pets.pets;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.ancash.pets.utils.Rarity;

public class Levelable {
	
	public static final double[] COMMON_XP_REQUIREMENTS = new double[100];
	public static final double[] UNCOMMON_XP_REQUIREMENTS = new double[100];
	public static final double[] RARE_XP_REQUIREMENTS = new double[100];
	public static final double[] EPIC_XP_REQUIREMENTS = new double[100];
	public static final double[] LEGENDARY_XP_REQUIREMENTS = new double[100];
	
	public static double getRequirementsForLevel(Rarity r, int level) {
		switch (r) {
		case COMMON:
			return COMMON_XP_REQUIREMENTS[level - 1];
		case UNCOMMON:
			return UNCOMMON_XP_REQUIREMENTS[level - 1];
		case RARE:
			return RARE_XP_REQUIREMENTS[level - 1];
		case EPIC:
			return EPIC_XP_REQUIREMENTS[level - 1];
		case LEGENDARY:
			return LEGENDARY_XP_REQUIREMENTS[level - 1];
		default:
			break;
		}
		return -1;
	}
	
	public static double[] getRequirements(Rarity r) {
		switch (r) {
		case COMMON:
			return COMMON_XP_REQUIREMENTS;
		case UNCOMMON:
			return UNCOMMON_XP_REQUIREMENTS;
		case RARE:
			return RARE_XP_REQUIREMENTS;
		case EPIC:
			return EPIC_XP_REQUIREMENTS;
		case LEGENDARY:
			return LEGENDARY_XP_REQUIREMENTS;
		default:
			break;
		}
		return null;
	}
	
	static {
		File config = new File("plugins/Pets/config.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(config);
		for(int i = 0; i <= 98; i++) {
			String str = fc.getString("xp_requirements.Lvl" + (i + 2));
			COMMON_XP_REQUIREMENTS[i] = ((Integer.valueOf(str.split(" ")[0])));
			UNCOMMON_XP_REQUIREMENTS[i] = ((Integer.valueOf(str.split(" ")[1])));
			RARE_XP_REQUIREMENTS[i] = ((Integer.valueOf(str.split(" ")[2])));
			EPIC_XP_REQUIREMENTS[i] = ((Integer.valueOf(str.split(" ")[3])));
			LEGENDARY_XP_REQUIREMENTS[i] = ((Integer.valueOf(str.split(" ")[4])));
		}
	}
	
	private double currentXP;
	private int level;
	
	private double[] requirements;
	
	public Levelable(int currentLevel, double currentXP, double[] requirements) {
		this.requirements = requirements;
		this.currentXP = currentXP;
		this.level = currentLevel;
	}
	
	public double[] getRequirements() {
		return requirements;
	}
	
	public int getLevel() {
		return level;
	}
	
	public double getCurrentXP() {
		return currentXP;
	}
	
	public double getRequiredXP() {
		return requirements[level];
	}
	
	public void setRequirements(double[] reqs) {
		this.requirements = reqs;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setCurrentXP(double xp) {
		this.currentXP = xp;
	}
	
	public void addXP(double xp, Player p) {
		if(level >= 100) return;
		currentXP += xp;
		boolean up = false;
		PlayerPet pp = PlayerPet.get(p.getUniqueId());
		while(currentXP >= requirements[level - 1]) {
			currentXP = currentXP - requirements[level - 1];
			level++;
			up = true;
			p.sendMessage("§aYour " + pp.getName() + " §alevelled up to level §9" + level + "§a!");
		}
		if(up) PlayerPet.get(p.getUniqueId()).save();
	}
	
}	
