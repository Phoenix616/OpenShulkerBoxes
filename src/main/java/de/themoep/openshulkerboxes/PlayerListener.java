package de.themoep.openshulkerboxes;

/*
 * OpenShulkerBoxes
 * Copyright (C) 2018 Max Lee aka Phoenix616 (mail@moep.tv)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerListener implements Listener {
    private final OpenShulkerBoxes plugin;
    
    public PlayerListener(OpenShulkerBoxes plugin) {
        this.plugin = plugin;
    }
   
    @EventHandler
    public void on(InventoryClickEvent event) {
        if (plugin.getOpenInInventoryTypes().contains(event.getClick())
                && event.getClickedInventory() == event.getWhoClicked().getInventory() // only allow opening in inventory and not other containers
                && event.getWhoClicked().hasPermission("openshulkerboxes.open.in-inventory")
                && plugin.showItemGui(event.getWhoClicked(), event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }
    
    /* Creative is broken ;_;
    @EventHandler
    public void on(InventoryCreativeEvent event) {
        if (plugin.getOpenInCreativeTypes().contains(event.getClick())
                && event.getClickedInventory() == event.getWhoClicked().getInventory() // only allow opening in inventory and not other containers
                && plugin.showItemGui(event.getWhoClicked(), event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }
    */
    
    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND
                && plugin.getOpenFromHotbarActions().contains(event.getAction())
                && plugin.isOpenFromHotbarRequiringSneaking() == event.getPlayer().isSneaking()
                && event.getPlayer().hasPermission("openshulkerboxes.open.from-hotbar")
                && plugin.showItemGui(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand())) {
            event.setCancelled(true);
        }
    }
}
