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
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Portable light meter plugin
 * @author James Cornwell-Shiel
 */
public class UVLightmeter extends JavaPlugin implements Listener {
    private ShapelessRecipe lightMeterRecipe;
    private ItemStack lightMeterItemStack;
    private ItemMeta lightMeterMetaData;
    
    private String _itemName = "Portable Light Meter";
    private List<String> _itemLore = new ArrayList<String>();
    
    /**
     * Fire up the plugin
     */
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        _itemLore.add("Right click a block to detect its light level.");
        _itemLore.add("0 is pitch black, 15 is fully lit.");
        
        lightMeterItemStack = new ItemStack(Material.WOOD_SWORD, 1);
        lightMeterMetaData = lightMeterItemStack.getItemMeta();
        lightMeterMetaData.setDisplayName(_itemName);
        lightMeterMetaData.setLore(_itemLore);
        
        lightMeterItemStack.setItemMeta(lightMeterMetaData);
        
        lightMeterRecipe = new ShapelessRecipe(lightMeterItemStack);
        lightMeterRecipe.addIngredient(Material.DAYLIGHT_DETECTOR).addIngredient(Material.STICK).addIngredient(Material.REDSTONE_COMPARATOR);
        getServer().addRecipe(lightMeterRecipe);
    }
    
    /**
     * Shut down the plugin
     */
    @Override
    public void onDisable() {
        // to-do: remove recipes on disable
    }
    
    /**
     * Respond to commands
     * 
     * @param sender Player/console
     * @param cmd command
     * @param label command name
     * @param args command arguments
     * @return true if we processed the command, false if not.
     */
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
    
    /**
     * Listens for players to craft an item, 
     * checks if it's a light meter, and if so, 
     * checks permissions and alerts the player if he lacks permissions.
     * @param event
     */
    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        if (event.getRecipe().getResult().equals(lightMeterItemStack)) {
            if (event.getWhoClicked().hasPermission("uvlightmeter.craft")) {
            } else {
                ((Player)event.getWhoClicked()).sendMessage(ChatColor.RED + "You don't have permission to craft a " + _itemName);
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * Listens for players to interact with a block with a light meter,
     * checks permissions, and processes the reading.
     * @param event
     */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(     event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                event.hasItem() &&
                event.hasBlock() &&
                event.getItem().hasItemMeta() &&
                event.getItem().getItemMeta().hasDisplayName() &&
                event.getItem().getItemMeta().getDisplayName().equals(_itemName) &&
                event.getItem().getItemMeta().hasLore() && 
                event.getItem().getItemMeta().getLore().equals(_itemLore) &&
                event.getPlayer().hasPermission("uvlightmeter.use")) {
            int lightLevel = event.getClickedBlock().getLocation().add(0, 1, 0).getBlock().getLightLevel();
            event.getPlayer().sendMessage(String.format("The light level is %d.", lightLevel));
            event.getItem().setDurability((short) (event.getItem().getDurability()+1));
            if(event.getItem().getDurability() >= (short)100) {
                event.getPlayer().setItemInHand(null);
            }
            event.setCancelled(true);
        }
    }
    
}
