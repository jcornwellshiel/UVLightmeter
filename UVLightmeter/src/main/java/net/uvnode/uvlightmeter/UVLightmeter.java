/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uvnode.uvlightmeter;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author James Cornwell-Shiel
 */
public class UVLightmeter extends JavaPlugin implements Listener {
    private ShapelessRecipe lightMeterRecipe;
    
    private String _itemName = "Portable Light Meter";
    private List<String> _itemLore = new ArrayList<String>();
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        createCraftingRecipe();
        _itemLore.add(ChatColor.GOLD + "Right click a block to detect its light level.");
        _itemLore.add(ChatColor.GOLD + "0 is pitch black, 15 is fully lit.");
    }
    
    @Override
    public void onDisable() {
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lightmeter")) {
            sender.sendMessage(ChatColor.GOLD + String.format("- UVLightmeter %s -", getDescription().getVersion()));
            sender.sendMessage(ChatColor.DARK_GRAY + "Craft a " + _itemName + " by combining a Daylight Detector, a Stick, and a Redstone Comparator on a crafting table.");
            sender.sendMessage(ChatColor.DARK_GRAY + "Equip the " + _itemName + " and right click a block to read the light level above that block.");
            sender.sendMessage(ChatColor.DARK_GRAY + "Each use reduces the durability of the " + _itemName + " so don't rapid-fire it too hard!");
            sender.sendMessage(ChatColor.DARK_GRAY + "If you can't craft the " + _itemName + " or right clicking has no effect, ask your server admin to grant you permission.");
            if (sender.hasPermission("uvlightmeter.admin") || sender.isOp() || !(sender instanceof Player)) {
                sender.sendMessage(ChatColor.DARK_GRAY + "uvlightmeter.admin: control admin settings");
                sender.sendMessage(ChatColor.DARK_GRAY + "uvlightmeter.craft: craft " + _itemName);
                sender.sendMessage(ChatColor.DARK_GRAY + "uvlightmeter.use: use a " + _itemName);
            }
            return true;
        } else {
            return false;
        }
    }

    private void createCraftingRecipe() {
        ItemStack lightMeterOutput = new ItemStack(Material.WATCH, 1);
        lightMeterOutput.setDurability((short) 0);
        
        ItemMeta metadata = lightMeterOutput.getItemMeta();
        metadata.setDisplayName(_itemName);        
        metadata.setLore(_itemLore);
        
        lightMeterOutput.setItemMeta(metadata);
        
        lightMeterRecipe = new ShapelessRecipe(lightMeterOutput);
        lightMeterRecipe.addIngredient(Material.DAYLIGHT_DETECTOR).addIngredient(Material.STICK).addIngredient(Material.REDSTONE_COMPARATOR);
        getServer().addRecipe(lightMeterRecipe);
    }
    
    
    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        if (event.getRecipe().equals(lightMeterRecipe) && !event.getWhoClicked().hasPermission("uvlightmeter.craft")) {
            ((Player)event.getWhoClicked()).sendMessage(ChatColor.RED + "You don't have permission to craft a " + _itemName);
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(event.getItem().hasItemMeta() &&
                event.getItem().getItemMeta().getDisplayName().equals(_itemName) &&
                event.getItem().getItemMeta().getLore().equals(_itemLore) &&
                event.getPlayer().hasPermission("uvlightmeter.use") &&
                event.hasBlock()) {
            int lightLevel = event.getClickedBlock().getLightLevel();
            String lightLevelText = (String) ((lightLevel > 7)?ChatColor.GREEN:ChatColor.RED + "" + lightLevel);
            event.getPlayer().sendMessage(String.format("The light level is %s", lightLevelText));
            event.getItem().setDurability((short) (event.getItem().getDurability()+10));
            event.setCancelled(true);
        }
    }
    
}
