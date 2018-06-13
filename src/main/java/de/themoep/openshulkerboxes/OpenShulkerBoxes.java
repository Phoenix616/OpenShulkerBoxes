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

import de.themoep.inventorygui.GuiStorageElement;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public final class OpenShulkerBoxes extends JavaPlugin {
    
    private Set<String> openableStartsWith;
    private Set<String> openableEndsWith;
    private Set<Material> openable;
    private Set<ClickType> openInInventoryTypes;
    //private Set<ClickType> openInCreativeTypes; // Creative is broken ;_;
    private Set<Action> openFromHotbarActions;
    private boolean openFromHotbarRequiresSneaking = true;
    
    @Override
    public void onEnable() {
        loadConfig();
        getCommand("openshulkerboxes").setExecutor(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }
    
    public void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
    
        openableStartsWith = new LinkedHashSet<>();
        openableEndsWith = new LinkedHashSet<>();
        openable = EnumSet.noneOf(Material.class);
        for (String matStr : getConfig().getStringList("openable-items")) {
            if (matStr.startsWith("*")) {
                openableEndsWith.add(matStr.substring(1).toUpperCase());
            } else if (matStr.endsWith("*")) {
                openableStartsWith.add(matStr.substring(0, matStr.length() - 1).toUpperCase());
            } else {
                Material material = Material.matchMaterial(matStr);
                if (material != null) {
                    openable.add(material);
                } else {
                    getLogger().log(Level.WARNING, "Could not find Material with name/id '" + matStr + "'!");
                }
            }
        }
        openInInventoryTypes = getConfigSet(ClickType.class, "open.in-inventory");
        //openInCreativeTypes = getConfigSet(ClickType.class, "open.in-creative"); // Creative is broken ;_;
        openFromHotbarActions = getConfigSet(Action.class, "open.from-hotbar.action");
        openFromHotbarRequiresSneaking = getConfig().getBoolean("open.from-hotbar.sneaking");
    }
    
    private <T extends Enum> Set<T> getConfigSet(Class<T> enumClass, String path) {
        Set<T> set = EnumSet.noneOf(enumClass);
        Method valueOf;
        try {
            valueOf = enumClass.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return set;
        }
        if (getConfig().isString(path)) {
            addToSet(set, valueOf, getConfig().getString(path).toUpperCase().replace('-', '_').replace(' ', '_'));
        } else if (getConfig().isList(path)) {
            for (String string : getConfig().getStringList(path)) {
                addToSet(set, valueOf, string.toUpperCase());
            }
        }
        return set;
    }
    
    private <T extends Enum<T>> void addToSet(Set<T> set, Method valueOf, String string) {
        try {
            set.add((T) valueOf.invoke(null, string));
        } catch (IllegalArgumentException e) {
            getLogger().log(Level.SEVERE, string + " is not a valid " + valueOf.getDeclaringClass().getSimpleName());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if ("reload".equalsIgnoreCase(args[0]) && sender.hasPermission("openshulkerboxes.command.reload")) {
                loadConfig();
                sender.sendMessage(ChatColor.YELLOW + "Config reloaded!");
                return true;
            }
        }
        return false;
    }
    
    public boolean isOpenable(Material type) {
        if (openable.contains(type)) {
            return true;
        }
        for (String endsWith : openableEndsWith) {
            if (type.toString().endsWith(endsWith)) {
                return true;
            }
        }
        for (String startsWith : openableStartsWith) {
            if (type.toString().startsWith(startsWith)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean showItemGui(HumanEntity viewer, ItemStack item) {
        if (item != null && isOpenable(item.getType())) {
            ItemMeta itemMeta = item.getItemMeta();
            
            if (itemMeta instanceof BlockStateMeta && ((BlockStateMeta) itemMeta).hasBlockState()) {
                BlockStateMeta stateMeta = (BlockStateMeta) itemMeta;
                BlockState blockState = stateMeta.getBlockState();
                if (blockState instanceof Container) {
                    Container container = (Container) blockState;
                    Inventory inventory = container.getInventory();
                    List<String> rows = new ArrayList<>();
                    if (inventory.getType() == InventoryType.DISPENSER || inventory.getType() == InventoryType.DROPPER) {
                        rows = Arrays.asList("ccc", "ccc", "ccc");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < inventory.getSize(); i++) {
                            sb.append('c');
                            if ((i + 1) % 9 == 0) {
                                rows.add(sb.toString());
                                sb = new StringBuilder();
                            }
                        }
                    }
                    GuiStorageElement storage = new GuiStorageElement('c', inventory) {
                        @Override
                        public Action getAction() {
                            Action action = super.getAction();
                            return click -> {
                                if (click.getEvent().getCursor() != null
                                        && isOpenable(click.getEvent().getCursor().getType())) {
                                    return true;
                                }
                                if (click.getEvent().getCurrentItem() != null
                                        && isOpenable(click.getEvent().getCurrentItem().getType())) {
                                    return true;
                                }
                                boolean c = action.onClick(click);
                                stateMeta.setBlockState(container);
                                item.setItemMeta(stateMeta);
                                return c;
                            };
                        }
                    };
                    String title = itemMeta.getDisplayName();
                    if (title == null && container instanceof Nameable) {
                        title = ((Nameable) container).getCustomName();
                    }
                    
                    if (title == null || title.startsWith("container.")) { // meh
                        title = inventory.getType().getDefaultTitle();
                    }
                    
                    InventoryGui gui = new InventoryGui(this, container, title, rows.toArray(new String[0]), storage);
                    gui.setCloseAction(close -> false);
                    gui.show(viewer);
                    return true;
                }
            }
        }
        return false;
    }
    
    public Set<ClickType> getOpenInInventoryTypes() {
        return openInInventoryTypes;
    }
    /* // Creative is broken ;_;
    public Set<ClickType> getOpenInCreativeTypes() {
        return openInCreativeTypes;
    }
    */
    public Set<Action> getOpenFromHotbarActions() {
        return openFromHotbarActions;
    }
    
    public boolean isOpenFromHotbarRequiringSneaking() {
        return openFromHotbarRequiresSneaking;
    }
}
