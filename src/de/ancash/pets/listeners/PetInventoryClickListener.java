package de.ancash.pets.listeners;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.ancash.pets.commands.PetCommands;
import de.ancash.pets.pets.Pet;
import de.ancash.pets.pets.PetTemplate;
import de.ancash.pets.pets.PlayerPet;
import de.ancash.pets.utils.Chat;
import de.ancash.pets.utils.Rarity;
import de.ancash.pets.utils.Chat.ChatLevel;
import de.tr7zw.nbtapi.NBTItem;

public class PetInventoryClickListener implements Listener{

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPetInvClick(InventoryClickEvent e) throws IllegalArgumentException, FileNotFoundException, IOException, InvalidConfigurationException {
		if(!e.getView().getTitle().equals("Your Pets")) return;
		e.setCancelled(true);
		if(!e.getInventory().equals(e.getClickedInventory())) return;
		if(e.getSlot() == -9999) return;
		InventoryAction action = e.getAction();
		if(action.name().toLowerCase().contains("drop") || action == InventoryAction.NOTHING || action == InventoryAction.UNKNOWN) return;
		if(action == InventoryAction.HOTBAR_SWAP || action.name().toLowerCase().contains("to_other")) return;
		Player p = (Player) e.getWhoClicked();
		if(e.getSlot() == 40) {
			p.closeInventory();
			return;
		}
		if(e.getSlot() == 41) {
			ItemStack is = e.getInventory().getItem(41);
			if(is.getData().toString().contains("5")) {
				ItemStack convertPetToItem = new ItemStack(Material.STAINED_GLASS_PANE,1 ,(byte)7);
				ItemMeta cPTI = convertPetToItem.getItemMeta();
				cPTI.setDisplayName("§cConvert Pet to Item");
				convertPetToItem.setItemMeta(cPTI);
				e.getInventory().setItem(41, convertPetToItem);
				return;
			}
			if(is.getData().toString().contains("7")) {
				ItemStack convertPetToItem = new ItemStack(Material.STAINED_GLASS_PANE,1 ,(byte)5);
				ItemMeta cPTI = convertPetToItem.getItemMeta();
				cPTI.setDisplayName("§cConvert Pet to Item");
				convertPetToItem.setItemMeta(cPTI);
				e.getInventory().setItem(41, convertPetToItem);
				return;
			}
		}
		
		ItemStack is = e.getInventory().getItem(e.getSlot());
		if(is == null || !is.getType().equals(Material.SKULL_ITEM)) return;
		
		NBTItem nbt = new NBTItem(is);
		if(!nbt.hasKey("petUUID")) return;
		String uuid = nbt.getString("petUUID");
		
		PlayerPet pp = PlayerPet.get(p.getUniqueId());
		if(e.getInventory().getItem(41).getData().getData() == 5) {
			if(p.getInventory().firstEmpty() == -1) {
				p.sendMessage("§cYour inventory is full!");
			} else {
				if(pp.getActiveUUID() != null) pp.clear();
				p.getInventory().addItem(PlayerPet.getItemFromFile(p, uuid));
				p.sendMessage("§aConverted Pet to Item!");
			}
			p.openInventory(PetCommands.getPetInventory(p));
			return;
		}
		
		
		if(pp.getActiveUUID() != null && pp.getActiveUUID().equals(UUID.fromString(uuid))) {
			pp.clear();
			p.openInventory(PetCommands.getPetInventory(p));
			return;
		}
		String type = nbt.getString("petType");
		Rarity r = Rarity.valueOf(nbt.getString("petRarity"));
		
		PetTemplate pt = PetTemplate.get(type);
		if(pt == null) {
			p.sendMessage("§cSomething went wrong!");
			Chat.sendMessage("§cCould not find template for pet " + type, ChatLevel.FATAL);
			return;
		}
		
		Pet pet = pt.getPet(r);
		if(pet == null) {
			p.sendMessage("§cSomething went wrong!");
			Chat.sendMessage("§cCould not find pet with rarity" + r.getName() + " §rin " + type, ChatLevel.FATAL);
			return;
		}
		if(pp.isActive()) pp.clear();
		pp.newPet(UUID.fromString(uuid));
		p.openInventory(PetCommands.getPetInventory(p));
	}
}
