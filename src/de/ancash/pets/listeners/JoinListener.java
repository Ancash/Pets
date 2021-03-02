package de.ancash.pets.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.ancash.pets.pets.PlayerPet;

public class JoinListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		new PlayerPet(0, 0, null, null, null, e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		PlayerPet.get(e.getPlayer().getUniqueId()).clear();
		PlayerPet.get(e.getPlayer().getUniqueId()).delete();
	}
	
}
