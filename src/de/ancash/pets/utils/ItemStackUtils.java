package de.ancash.pets.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class ItemStackUtils {

	public static String getTexure(ItemStack pet) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String texture = null;
		
		SkullMeta sm = (SkullMeta) pet.getItemMeta();
		Field profileField = sm.getClass().getDeclaredField("profile");
		profileField.setAccessible(true);
		GameProfile profile = (GameProfile) profileField.get(sm);
		Collection<Property> textures = profile.getProperties().get("textures");
		for(Property p : textures) {
			texture = p.getValue();
		}
		return texture;
	}
	
	public static ItemStack setTexture(ItemStack pet, String texture) {
		SkullMeta hm = (SkullMeta) pet.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", texture));
		try {
			Field field = hm.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			field.set(hm, profile);
		} catch(IllegalArgumentException  | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		pet.setItemMeta(hm);
		return pet;
	}
	
	public static ItemStack replacePlaceholder(ItemStack is, HashMap<String, String> placeholder) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<String>();
		for(String str : im.getLore()) {
			for(String place : placeholder.keySet()) {
				if(str.contains(place)) {
					str = str.replace(place, placeholder.get(place));
				}
			}
			lore.add(str);
		}
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack addLore(ItemStack is, String...lore) {
		ItemMeta im = is.getItemMeta();
		List<String> lored = im.getLore();
		for(String str : lore) lored.add(str);
		im.setLore(lored);
		is.setItemMeta(im);
		return is;
	}
}
