package de.ancash.pets.listeners;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.ancash.pets.pets.PlayerPet;
import de.tr7zw.nbtapi.NBTItem;

public class RightClickPetItemListener implements Listener{

	@EventHandler
	public void onItemRightClick(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return;
		
		NBTItem nbt = new NBTItem(p.getItemInHand());
		if(!nbt.hasKey("petType")) return;
		if(!nbt.hasKey("petRarity")) return;
		e.setCancelled(true);
		try {
			PlayerPet.saveItemToFile(p);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
