package de.ancash.pets.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.ancash.pets.pets.Levelable;
import de.ancash.pets.pets.PetTemplate;
import de.ancash.pets.pets.PlayerPet;
import de.ancash.pets.utils.ItemStackUtils;
import de.ancash.pets.utils.Rarity;
import de.tr7zw.nbtapi.NBTItem;

public class PetCommands implements CommandExecutor{

	private static final String noPerms = "§cYou don't have permission to do that";
	private static final String invFull = "§cYour inventory is full!";
	private static final String unknownRarity = "§cUnknown Rarity: ";
	private static final String unknownPetName = "§cUnknown Pet Name: ";
	private static final String notAPet = "§cThat's not a Pet what you are holding!";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(!(sender instanceof Player)) return true;
		if(args.length == 0) return false;
		Player p = (Player) sender;
		String msg = null;
		switch (args[0].toLowerCase()) {
		case "give":
			msg = handleGivePet(args, p);
			break;
		case "setlevel":
			msg =  handleSetLevel(args, p);
			break;
		case "inv":
			msg = handleOpenPetInventory(p);
		default:
			break;
		}
		if(msg == null) return true;
		if(msg.equals("unknownCMD")) return false;
		p.sendMessage(msg);
		return true;
	}
	
	private String handleOpenPetInventory(Player p) {
		if(!p.hasPermission("pets.inv")) return noPerms;

		p.openInventory(getPetInventory(p));
		return null;
	}

	public static Inventory getPetInventory(Player p) {
		Inventory inv = Bukkit.createInventory(null, 5 * 9, "Your Pets");
		PlayerPet pp = PlayerPet.get(p.getUniqueId());
		if(pp.isActive()) pp.save();
		File playerFile = new File("plugins/Pets/player/" + p.getUniqueId().toString());
		FileConfiguration fc = YamlConfiguration.loadConfiguration(playerFile);
		
		ItemStack close = new ItemStack(Material.BARRIER);
		ItemMeta im = close.getItemMeta();
		im.setDisplayName("§cClose");
		close.setItemMeta(im);
		inv.setItem(40, close);
		
		ItemStack petToItem = new ItemStack(Material.STAINED_GLASS_PANE, 1 , (byte) 7);
		ItemMeta pTI = petToItem.getItemMeta();
		pTI.setDisplayName("§cConvert Pet to Item");
		petToItem.setItemMeta(pTI);
		inv.setItem(41, petToItem);
		for(String key : fc.getKeys(false)) {
			Rarity r = Rarity.valueOf(fc.getString(key + ".rarity"));
			int level = fc.getInt(key + ".level");
			
			ItemStack pet = PetTemplate.get(fc.getString(key + ".type")).getPet(r).replacePlaceholder(level, fc.getDouble(key + ".currentXP"), Levelable.getRequirementsForLevel(r, level));
			if(pp.getActiveUUID() != null && pp.getActiveUUID().toString().equals(key)) {
				pet = ItemStackUtils.addLore(pet, "", "§cClick to despawn!", "");
			} else {
				pet = ItemStackUtils.addLore(pet, "", "§eClick to spawn!", "");
			}
			NBTItem nbt = new NBTItem(pet);
			nbt.setString("petUUID", key);
			
			inv.addItem(nbt.getItem());
		}
		return inv;
	}
	
	private String handleSetLevel(String[] args, Player p) {
		if(!p.hasPermission("pets.setlevel")) return noPerms;
		if(args.length != 2) return "unknownCMD";
		ItemStack pet = p.getItemInHand();
		if(pet == null || pet.getType().equals(Material.AIR) || !pet.getType().equals(Material.SKULL_ITEM)) return "§cPlease hold your pet in your hand!";
		
		int i = 1;
		try {
			i = Integer.valueOf(args[1]);
		} catch(Exception e) {
			return "§cThat's not a number: " + args[1];
		}
		if(i > 100) return "§cLevel too high (max 100)! " + i;
		
		NBTItem nbt = new NBTItem(pet);
		if(!nbt.hasKey("petType")) return notAPet;
		if(!nbt.hasKey("petRarity")) return notAPet;
		p.setItemInHand(PetTemplate.get(nbt.getString("petType")).getPet(Rarity.valueOf(nbt.getString("petRarity"))).replacePlaceholder(i, nbt.getDouble("petCurrentXP"), Levelable.getRequirementsForLevel(Rarity.valueOf(nbt.getString("petRarity")), i)));
		return null;
	}
	
	private String handleGivePet(String[] args, Player p) {
		if(!p.hasPermission("pets.give")) return noPerms;
		if(args.length != 3) return "unknownCMD";
		PetTemplate pt = PetTemplate.get(args[1]);
		if(pt == null) 	return unknownPetName + args[1];
		
		Rarity r = null;
		try {
			r =  Rarity.valueOf(args[2].toUpperCase());
		} catch(Exception e) {
			return unknownRarity + args[2];
		}
		if(p.getInventory().firstEmpty() == -1) return invFull;
		
		p.getInventory().addItem(pt.getPet(r).replacePlaceholder(1, 0, Levelable.getRequirementsForLevel(r, 1)));
		return null;
	}
}
