package de.ancash.pets.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;

import de.ancash.pets.pets.PlayerPet;
import de.ancash.skills.events.XPCollectEvent;

public class XPCollectListener implements Listener{

	@EventHandler
	public void onXPCollect(XPCollectEvent e) {
		Player p = e.getPlayer();
		PlayerPet pp = PlayerPet.get(p.getUniqueId());
		if(!pp.isActive()) return;
		pp.addXP(e.getXPAdded());
	}
	
}
