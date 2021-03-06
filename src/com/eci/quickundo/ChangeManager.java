package com.eci.quickundo;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class ChangeManager {

	private Map<Location, BlockChange> changeMap; // Map for holding the different changes the player makes, and the plugin records. This makes it easy to locate previous changes by using the location as key
	
	private World world;
	private Player player;
	private boolean active; // Value for keeping track of whether undo has been done. Also functions as a checkpoint for whether the plugin should record mere changes in the main file

	public ChangeManager(Player player) {
		this.changeMap = new LinkedHashMap<>(); // Now the changes are stored in order, to prepare for future features (Undo a certain amount of changes)
		this.player = player;
		this.world = player.getWorld();
		this.active = true;
	}
	
	public void undo() {
		if (active) { // Check if undo has been done before
			
			if (changeMap.size() != 0) { // Check if there has even been changes
				Collection<BlockChange> changeCollection = changeMap.values();
				for (BlockChange change : changeCollection) {
					world.getBlockAt(change.getBlockChangeLocation()).setType(change.getPrevBlockMaterial()); // Change their material back to the previous
				}
				
				active = false; // Set active to false, as the plugin should no longer keep track of the blocks that are broken and placed
				
				QUndo.formatMessage(player, ChatColor.GREEN + "Undo complete");
				QUndo.formatMessage(player, changeMap.size() + " changes affected"); // Count the changes and send message
			} else {
				QUndo.formatMessage(player, ChatColor.RED + "Nothing to be undone");
			}
		} else {
			QUndo.formatMessage(player, ChatColor.RED + "Quick Undo has already been done");
		}
	}

	public void redo() {
		if (!active) {
			if (changeMap.size() != 0) {
				Collection<BlockChange> changeCollection = changeMap.values();
				for (BlockChange change : changeCollection) {
					world.getBlockAt(change.getBlockChangeLocation()).setType(change.getCurrBlockMaterial()); // Change their material back to the previous
				}
				
				active = true; // Set active to true, as the blocks that are changed after this point, should be remembered
				
				QUndo.formatMessage(player, ChatColor.GREEN + "Redo complete");
				QUndo.formatMessage(player, changeMap.size() + " changes affected"); // Count the changes and send message
			} else {
				QUndo.formatMessage(player, ChatColor.RED + "Nothing to be redone");
			}
		} else {
			QUndo.formatMessage(player, ChatColor.RED + "Nothing to be redone");
		}
	}
	
	public Map<Location, BlockChange> getChangeMap() {
		return changeMap;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void debug(CommandSender sender) { // This is going to be reused later, so the player can see which changes they have made, in chronological order.
		Collection<BlockChange> changeCollection = changeMap.values();
		for (BlockChange change : changeCollection) {
			sender.sendMessage(change.toString());
		}
		sender.sendMessage("-END-");
	}

}
