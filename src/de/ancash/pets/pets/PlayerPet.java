package de.ancash.pets.pets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.ancash.pets.Pets;
import de.ancash.pets.utils.Chat;
import de.ancash.pets.utils.Rarity;
import de.ancash.pets.utils.datastructure.tuples.Duplet;
import de.ancash.pets.utils.datastructure.tuples.ImmutableTriplet;
import de.ancash.pets.utils.datastructure.tuples.Tuple;
import de.ancash.pets.utils.Chat.ChatLevel;
import de.tr7zw.nbtapi.NBTItem;

public class PlayerPet extends Levelable{

	private final ArmorStand armorStand;
	private final ArmorStand nameTag;
	private final UUID playerUUID;
	private final Duplet<UUID, Pet> petInfo;
	private final BukkitRunnable runnable;
	private boolean running = false;
	
	private static HashMap<UUID, PlayerPet> registered = new HashMap<UUID, PlayerPet>();
	
	public PlayerPet(int currentLevel, double currentXP, double[] requirements, UUID petId, Pet template, Player p) {
		super(currentLevel, currentXP, requirements);
		this.playerUUID = p.getUniqueId();
		this.armorStand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
		armorStand.setVisible(false);
		armorStand.setMarker(true);
		armorStand.setCustomNameVisible(false);
		
		armorStand.setSmall(true);
		nameTag = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
		nameTag.setVisible(false);
		nameTag.setMarker(true);
		nameTag.setCustomNameVisible(false);
		
		petInfo = Tuple.of(petId, template);
		
		runnable = getRunnable();
		
		registered.put(p.getUniqueId(), this);
		
		start();
	}
	
	private BukkitRunnable getRunnable() {
		return new BukkitRunnable() {
			
			Player player = Bukkit.getPlayer(playerUUID);
			boolean up = false;
			double bobbing = 0;
			
			@Override
			public void run() {
				if(petInfo.getFirst() == null) {
					up = false;
					return;
				}
				ImmutableTriplet<Double, Double, Double> now  = Tuple.immutableOf(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
				double distance = Math.sqrt(player.getLocation().distanceSquared(armorStand.getLocation()));
				Location test = armorStand.getLocation().clone().add(0, -0.5, 0);
				if(distance > 3) {
					test.add(new Vector(now.getFirst(), now.getSecond(), now.getThird()).subtract(test.toVector()).normalize().multiply(0.3));
					float yaw = (float) Math
	                        .toDegrees(Math.atan2(player.getLocation().getZ() - armorStand.getLocation().getZ(),
	                        		player.getLocation().getX() - armorStand.getLocation().getX()))
	                        - 90;
					test.setYaw(yaw);
				}
				test.add(0, 0.5 + bobbing, 0);
				
				if(!up) {
					bobbing = bobbing - 0.015;
				} else {
					bobbing = bobbing + 0.015;
				}
				if(bobbing >= 0.2 || bobbing <= -0.2) up = !up;
				
				armorStand.teleport(test);
				updateName();
			}
		};
	}
	
	public String getName() {
		return petInfo.getSecond().getRarity().getPrefix() + petInfo.getSecond().getName();
	}
	
	public void addXP(double amount) {
		super.addXP(amount, Bukkit.getPlayer(playerUUID));
	}
	
	public UUID getActiveUUID() {
		return petInfo.getFirst();
	}
	
	public boolean isActive() {
		return getActiveUUID() != null;
	}
	
	private void stop() {
		try {runnable.cancel(); running = false;}catch(Exception e) {}
	}
	
	public void start() {
		runnable.runTaskTimer(Pets.getInstance(), 1, 1);
		running = true;
	}
	
	public ArmorStand getFirstrmorStand() {
		return armorStand;
	}
	
	public void updateName() {
		if(!nameTag.getCustomName().contains("" + getLevel())) nameTag.setCustomName("§8[§7Lv" + getLevel() + "§8] " + petInfo.getSecond().getRarity().getPrefix() + Bukkit.getPlayer(playerUUID).getDisplayName() + "'s " + petInfo.getSecond().getName());
		
		nameTag.teleport(armorStand.getLocation().clone().add(0, 1, 0));
	}
	
	public void clear() {
		save();
		petInfo.setFirst(null);
		petInfo.setSecond(null);
		nameTag.setCustomNameVisible(false);
		armorStand.setHelmet(null);
	}
	
	public void save() {
		if(petInfo.getFirst() == null) return;
		File playerFile = new File("plugins/Pets/player/" + playerUUID.toString());
		FileConfiguration fc = YamlConfiguration.loadConfiguration(playerFile);
		try {
			fc.load(playerFile);
			fc.set(petInfo.getFirst().toString() + ".level", getLevel());
			fc.set(petInfo.getFirst().toString() + ".currentXP", getCurrentXP());
			fc.save(playerFile);
		} catch (IOException | InvalidConfigurationException e) {
			Chat.sendMessage("Error While Saving Pet(" + petInfo.getSecond().getName() + ") For " + Bukkit.getOfflinePlayer(playerUUID).getName(), ChatLevel.WARN);
			e.printStackTrace();
		}
	}
	
	public void newPet(UUID petId) {
		if(running) clear();
		File f = new File("plugins/Pets/player/" + playerUUID.toString());
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		Rarity r = Rarity.valueOf(fc.getString(petId.toString() + ".rarity"));
		this.petInfo.setFirst(petId);
		this.petInfo.setSecond(PetTemplate.get(fc.getString(petId.toString() + ".type")).getPet(r));
		setLevel(fc.getInt(petId.toString() + ".level"));
		setCurrentXP(fc.getDouble(petId.toString() + ".currentXP"));
		setRequirements(Levelable.getRequirements(r));
		armorStand.setHelmet(petInfo.getSecond().getItemStack());
		armorStand.teleport(Bukkit.getPlayer(playerUUID));
		nameTag.setCustomName("§8[§7Lv" + getLevel() + "§8] " + petInfo.getSecond().getRarity().getPrefix() + Bukkit.getPlayer(playerUUID).getDisplayName() + "'s " + petInfo.getSecond().getName());
		nameTag.setCustomNameVisible(true);
	}
	
	public void delete() {
		clear();
		stop();
		nameTag.remove();
		armorStand.remove();
		registered.remove(playerUUID);
	}
	
	public static PlayerPet get(UUID id) {
		return registered.get(id);
	}
	
	public static ItemStack getItemFromFile(Player p, String id) throws FileNotFoundException, IOException, InvalidConfigurationException {
		File playerFile = new File("plugins/Pets/player/" + p.getUniqueId().toString());
		FileConfiguration fc = YamlConfiguration.loadConfiguration(playerFile);
		fc.load(playerFile);
		
		
		int level = fc.getInt(id + ".level");
		double currentXp = fc.getDouble(id + ".currentXP");
		String type = fc.getString(id + ".type");
		String rarity = fc.getString(id + ".rarity");
		ItemStack pet = PetTemplate.get(fc.getString(id + ".type")).getPet(Rarity.valueOf(fc.getString(id + ".rarity").toUpperCase())).replacePlaceholder(level, currentXp, Levelable.getRequirementsForLevel(Rarity.valueOf(rarity), level));
		fc.set(id, null);
		fc.save(playerFile);
		
		NBTItem nbt = new NBTItem(pet);
		nbt.setString("petType", type);
		nbt.setString("petRarity", rarity);
		nbt.setInteger("petLevel", level);
		nbt.setDouble("petCurrentXP", currentXp);
				
		return nbt.getItem();
	}
	
	public static void saveItemToFile(Player p) throws IOException {
		NBTItem nbt = new NBTItem(p.getItemInHand());
		String petType = nbt.getString("petType");
		PetTemplate template = PetTemplate.get(petType);
		if(template == null) {
			p.sendMessage("§Couldn not find pet '" + petType + "'");
			return;
		}
		String rarity = nbt.getString("petRarity");
		int currentLevel = nbt.getInteger("petLevel");
		double currentXp = nbt.getDouble("petCurrentXP");
		
		File playerFile = new File("plugins/Pets/player/" + p.getUniqueId().toString());
		
		if(!playerFile.exists()) {
			if(!new File("plugins/Pets/player").exists()) new File("plugins/Pets/player").mkdir();
			playerFile.createNewFile();
		}
		
		FileConfiguration fc = YamlConfiguration.loadConfiguration(playerFile);
		try {
			fc.load(playerFile);
			String id = UUID.randomUUID().toString();
			fc.set(id + ".level", currentLevel);
			fc.set(id + ".currentXP", currentXp);
			fc.set(id + ".rarity", rarity);
			fc.set(id + ".type", petType);
			fc.save(playerFile);
			p.setItemInHand(null);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		p.sendMessage("§aAdded Pet to your Pet Inventory!");
	}
}
