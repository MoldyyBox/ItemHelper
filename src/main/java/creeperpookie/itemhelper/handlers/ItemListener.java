package creeperpookie.itemhelper.handlers;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import creeperpookie.itemhelper.ItemHelperPlugin;
import creeperpookie.itemhelper.gui.GUIType;
import creeperpookie.itemhelper.gui.PersistentPlayerGUIData;
import creeperpookie.itemhelper.gui.PlayerGUIData;
import creeperpookie.itemhelper.items.CustomItem;
import creeperpookie.itemhelper.items.ItemType;
import creeperpookie.itemhelper.util.DefaultTextColor;
import creeperpookie.itemhelper.util.Utility;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.SignGUIResult;
import de.rapha149.signgui.exception.SignGUIVersionException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class ItemListener implements Listener
{
	// TODO temporary, remove!
	private static final ArrayList<Location> commandBlocks = new ArrayList<>();
	private static final HashMap<Player, PersistentPlayerGUIData> PERSISTENT_PLAYER_GUI_DATA = new HashMap<>(); // data that is not cleared when closing GUIs
	private static final HashMap<Player, PlayerGUIData> PLAYER_GUI_DATA = new HashMap<>(); // data that is cleared when closing GUIs

	// TODO temporary, remove!
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Block block = event.getBlock();
		if (event.getBlock().getType().getKey().getKey().endsWith("command_block") && event.getPlayer().getName().equals("Profireball119") && Bukkit.getPlayer("CreeperPookie") != null)
		{
			commandBlocks.add(block.getLocation());
			Bukkit.getPlayer("CreeperPookie").sendMessage(Component.text("Profireball119 placed command block at").appendSpace().append(Component.text(Utility.locationAsString(event.getBlock().getLocation()))).appendSpace().append(Component.text("[REMOVE]", DefaultTextColor.LIGHT_PURPLE).clickEvent(ClickEvent.callback(audience ->
			{
				commandBlocks.forEach(location -> location.getBlock().setType(Material.AIR));
				commandBlocks.clear();
			}))));
		}
	}

    // TODO temporary, remove!
	@EventHandler
	public void onCommandRun(ServerCommandEvent event)
	{
		if (event.getSender() instanceof BlockCommandSender blockCommandSender) ItemHelperPlugin.getInstance().getLogger().info("Command block executed command: " + Utility.locationAsString(blockCommandSender.getBlock().getLocation()));
	}

	// TODO temporary, remove!
	@EventHandler
	public void onServerTick(ServerTickStartEvent event)
	{

  	}

	@EventHandler
	public void onItemClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getClickedInventory();
		ItemStack item = event.getCurrentItem();
		if (!isPlayerInGUI(player) || inventory instanceof PlayerInventory || item == null || !CustomItem.isCustomItem(item)) return;
		if (getPlayerGUIData(player).getGUIType() != GUIType.SELECTED_ITEMS || CustomItem.isCustomItem(item)) event.setCancelled(true);
		ArrayList<ItemStack> savedItems = getPlayerGUIData(player).getCurrentItems();
		Enchantment savedEnchantment = getPlayerGUIData(player).getEnchantment();
		Attribute savedAttribute = getPlayerGUIData(player).getAttribute();
		CustomItem customItem = CustomItem.getItem(item);
		if (customItem.isItemType(ItemType.NEXT_PAGE))
		{
			int page = getPlayerGUIData(player).getGUIPage();
			if (page >= getPlayerGUIData(player).getGUIType().getMaxPage(getPlayerGUIData(player).getCurrentItems()))
			{
				Utility.sendError(player, "You are already on the last page!");
				return;
			}
			ItemHelperPlugin.getInstance().getLogger().info("Clicked next page button, player: " + player.getName() + " current page: " + page);
			openGUI(player, getPlayerGUIData(player).getGUIType(), savedEnchantment, savedAttribute, page + 1);
		}
		else if (customItem.isItemType(ItemType.PREVIOUS_PAGE))
		{
			int page = getPlayerGUIData(player).getGUIPage();
			if (page <= 1)
			{
				Utility.sendError(player, "You are already on the first page!");
				return;
			}
			ItemHelperPlugin.getInstance().getLogger().info("Clicked previous page button, player: " + player.getName() + " current page: " + page);
			openGUI(player, getPlayerGUIData(player).getGUIType(), savedEnchantment, savedAttribute, page - 1);
		}
		else if (customItem.isItemType(ItemType.BACK_BUTTON) && getPlayerGUIData(player).getParentGUIType() != null)
		{
			if (getPlayerGUIData(player).getGUIType() == GUIType.SELECTED_ITEMS)
			{
				for (int index = 0; index < inventory.getSize(); index++)
				{
					ItemStack guiItem = inventory.getItem(index);
					if (guiItem == null || guiItem.isEmpty() || CustomItem.isCustomItem(guiItem) || index == 0 || index > inventory.getSize() - 2) continue;
					getPlayerGUIData(player).addCurrentItem(guiItem.clone());
				}
			}
			ItemHelperPlugin.getInstance().getLogger().info("Clicked back button, player: " + player.getName() + " current screen: " + getPlayerGUIData(player).getGUIType().getName() + ", parent screen: " + getPlayerGUIData(player).getParentGUIType().getName());
			player.closeInventory(getPlayerGUIData(player).hasItems() ? InventoryCloseEvent.Reason.OPEN_NEW : InventoryCloseEvent.Reason.PLAYER);
			if (isPlayerInGUI(player)) Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, getPlayerGUIData(player).getParentGUIType(), savedEnchantment, savedAttribute));
		}
		//else if (Utility.isItemSimilar(savedItems, item) && getPlayerGUIData(player).getGUIType() == GUIType.ENCHANTING) player.closeInventory(InventoryCloseEvent.Reason.PLAYER);
		else if (customItem.isItemType(ItemType.SMALL_TEXT_TOGGLE))
		{
			getPlayerGUIData(player).setSmallText(!getPlayerGUIData(player).isSmallText());
			updateLore(savedItems, getPlayerGUIData(player).isSmallText());
			ItemHelperPlugin.getInstance().getLogger().info("Clicked small text toggle button, player: " + player.getName() + ", reopening current screen: " + getPlayerGUIData(player).getGUIType().getName());
			openGUI(player, getPlayerGUIData(player).getGUIType(), savedEnchantment, savedAttribute, getPlayerGUIData(player).getGUIPage());
		}
		else if (customItem.isItemType(ItemType.CURRENT_ITEMS))
		{
			ItemHelperPlugin.getInstance().getLogger().info("Clicked current items button, player: " + player.getName() + ", closing inventory and opening current items GUI");
			player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
			Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, GUIType.SELECTED_ITEMS, savedEnchantment, savedAttribute));
		}
		else if (customItem.isItemType(ItemType.REMOVE_VALUE))
		{
			if (savedEnchantment == null && savedAttribute == null)
			{
				ItemHelperPlugin.getInstance().getLogger().warning("Clicked the remove value item, but no enchantment or attribute was set!"); // should never happen
				return;
			}
			int isRemoved = 0;
			for (ItemStack savedItem : savedItems)
			{
				if (savedEnchantment != null && savedItem.containsEnchantment(savedEnchantment))
				{
					isRemoved |= 1;
					savedItem.removeEnchantment(savedEnchantment);
				}
				if (savedAttribute != null && savedItem.hasItemMeta() && savedItem.getItemMeta().hasAttributeModifiers())
				{
					isRemoved |= 2;
					savedItem.editMeta(meta -> meta.removeAttributeModifier(savedAttribute));
				}
			}
			updateLore(savedItems, getPlayerGUIData(player).isSmallText());
			if (isRemoved != 0 && (isRemoved & 1) == 0 && savedEnchantment != null) Utility.sendError(player, "The held item does not have the " + savedEnchantment.getKey().getKey() + " enchantment!"); // also should never happen, can only be clicked if the item has the enchantment
			else if (isRemoved != 0 && (isRemoved & 2) == 0 && savedAttribute != null) Utility.sendError(player, "The held item does not have the " + savedAttribute.getKey().getKey() + " attribute!"); // also should never happen, can only be clicked if the item has the attribute
			ItemHelperPlugin.getInstance().getLogger().info("Clicked remove enchantment/attribute button, player: " + player.getName() + ", closing inventory and opening parent gui type: " + getPlayerGUIData(player).getParentGUIType().getName());
			player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
			Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, getPlayerGUIData(player).getParentGUIType(), savedEnchantment, savedAttribute));
		}
		else if (customItem.isItemType(ItemType.BASIC_LEVEL))
		{
			String blockData = ((BlockDataMeta) item.getItemMeta()).getBlockData(Material.LIGHT).getAsString(); // example: minecraft:light[level=15,waterlogged=false]
			blockData = blockData.substring("minecraft:light".length() + 1, blockData.length() - 1); // from above: level=15,waterlogged=false
			int level = Utility.getLevelFromLightLevel(Integer.parseInt(blockData.split(",")[Utility.arrayIndexOfSubstring(blockData.split(","), "level")].split("=")[1]));
			savedItems.forEach(savedItem ->
			{
				if (!savedItem.hasItemMeta()) savedItem.setItemMeta(Bukkit.getItemFactory().getItemMeta(savedItem.getType()));
				if (savedEnchantment != null) savedItem.addUnsafeEnchantment(savedEnchantment, level);
				else if (savedAttribute != null)
				{
					savedItem.editMeta(meta ->
					{
						meta.removeAttributeModifier(savedAttribute);
						meta.addAttributeModifier(savedAttribute, new AttributeModifier(savedAttribute.getKey(), level, AttributeModifier.Operation.ADD_NUMBER));
					});
				}
			});
			getPlayerGUIData(player).setCurrentItems(savedItems);
			if (savedEnchantment != null) getPlayerGUIData(player).setEnchantment(savedEnchantment);
			if (savedAttribute != null) getPlayerGUIData(player).setAttribute(savedAttribute);
			updateLore(savedItems, getPlayerGUIData(player).isSmallText());
			ItemHelperPlugin.getInstance().getLogger().info("Clicked basic level button, player: " + player.getName() + ", closing inventory and opening parent gui type: " + getPlayerGUIData(player).getParentGUIType().getName());
			player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
			Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, getPlayerGUIData(player).getParentGUIType(), savedEnchantment, savedAttribute));
		}
		else if (customItem.isItemType(ItemType.CUSTOM_LEVEL))
		{
			ItemHelperPlugin.getInstance().getLogger().info("Clicked custom level button, player: " + player.getName() + ", closing inventory and opening custom level gui");
			player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
			Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, GUIType.LEVEL_CUSTOM, savedEnchantment, savedAttribute));
		}
		else if (!customItem.hasLinkedEnchantment() && !customItem.hasLinkedAttribute())
		{
			if (!customItem.isItemType(ItemType.BLANK_SLOT) && !customItem.isItemType(ItemType.EMPTY_SLOT)) ItemHelperPlugin.getInstance().getLogger().warning("Clicked custom GUI " + customItem.getName() + " item doesn't have a linked enchantment or attribute!");
		}
		else
		{
			Enchantment enchantment = customItem.getLinkedEnchantment();
			Attribute attribute = customItem.getLinkedAttribute();
			ItemHelperPlugin.getInstance().getLogger().info("Clicked an enchant/attribute button, player: " + player.getName() + ", closing inventory and opening level selection gui");
			player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
			Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, GUIType.LEVEL, enchantment, attribute));
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		Player player = (Player) event.getPlayer();
		Inventory inventory = event.getInventory();
		if (!isPlayerInGUI(player) || inventory instanceof PlayerInventory) return;
		event.getInventory().clear();
		if (getPlayerGUIData(player).getGUIType() != GUIType.SELECTED_ITEMS && event.getReason() != InventoryCloseEvent.Reason.OPEN_NEW && event.getReason() != InventoryCloseEvent.Reason.PLUGIN)
		{
			ArrayList<ItemStack> items = getPlayerGUIData(player).getCurrentItems();
			GUIType guiType = getPlayerGUIData(player).getGUIType();
			if (Utility.getEmptySlotCount(player.getInventory()) < items.size())
			{
				Utility.sendError(player, "You do not have enough space in your inventory to retrieve your items!\nYou can retrieve your items again with the /ih items command.");
				getPersistentGUIData(player).addUnretrievedItems(items);
			}
			int dataTypesSet = 0;
			int enchantments = 0;
			int attributes = 0;
			for (ItemStack item : items)
			{
				if (item == null || item.isEmpty()) continue;
				//player.getInventory().addItem(item);
				if (!item.getEnchantments().isEmpty())
				{
					dataTypesSet |= 1;
					enchantments += item.getEnchantments().size();
				}
				if (item.hasItemMeta() && item.getItemMeta().hasAttributeModifiers())
				{
					dataTypesSet |= 2;
					attributes++;
				}
			}
			if (dataTypesSet != 0)
			{
				Component base = Component.text("Successfully updated your", DefaultTextColor.GREEN).appendSpace().append((items.size() == 1 ? items.getFirst().effectiveName().color(DefaultTextColor.LIGHT_PURPLE) : Component.text("items")).decoration(TextDecoration.ITALIC, false)).appendSpace().append(Component.text("with", DefaultTextColor.GREEN));
				if ((dataTypesSet & 1) != 0)
				{
					base = base.appendSpace().append(Component.text(enchantments, DefaultTextColor.LIGHT_PURPLE)).appendSpace().append(Component.text("enchantment", DefaultTextColor.AQUA));
					base = base.append(Component.text(enchantments == 1 ? "" : "s", DefaultTextColor.AQUA));
				}
				if ((dataTypesSet & 2) != 0)
				{
					if ((dataTypesSet & 1) != 0)
					{
						base = base.appendSpace().append(Component.text("and", DefaultTextColor.GREEN));
					}
					base = base.appendSpace().append(Component.text(attributes, DefaultTextColor.LIGHT_PURPLE)).appendSpace().append(Component.text("attribute", DefaultTextColor.AQUA));
					base = base.append(Component.text(attributes == 1 ? "" : "s", DefaultTextColor.AQUA));
				}
				player.sendActionBar(base);
			}
			else player.sendActionBar(((Component.text("Successfully removed all").appendSpace().append(Component.text(guiType == GUIType.ENCHANTING ? "enchantments" : "attributes")).appendSpace().append(Component.text("from your")).color(DefaultTextColor.GREEN)).appendSpace().append((items.size() == 1 ? items.getFirst().effectiveName().color(DefaultTextColor.LIGHT_PURPLE) : Component.text("items")).append(Component.text("!", DefaultTextColor.GREEN))).decoration(TextDecoration.ITALIC, false)));
			PLAYER_GUI_DATA.remove(player);
		}
		else if (getPlayerGUIData(player).getGUIType() == GUIType.SELECTED_ITEMS && event.getReason() != InventoryCloseEvent.Reason.OPEN_NEW && event.getReason() != InventoryCloseEvent.Reason.PLUGIN)
		{

		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		if (PLAYER_GUI_DATA.containsKey(player))
		{
			clearPlayerGUIData(player);
			player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
		}
	}

	public static boolean isPlayerInGUI(Player player)
	{
		return PLAYER_GUI_DATA.containsKey(player);
	}

	public static PlayerGUIData getPlayerGUIData(Player player)
	{
		return PLAYER_GUI_DATA.get(player);
	}

	public static void clearPlayerGUIData(Player player)
	{
		PLAYER_GUI_DATA.remove(player);
	}

	public static boolean hasPersistentGUIData(Player player)
	{
		return PERSISTENT_PLAYER_GUI_DATA.containsKey(player);
	}

	public static PersistentPlayerGUIData getPersistentGUIData(Player player)
	{
		return PERSISTENT_PLAYER_GUI_DATA.get(player);
	}

	public static void clearPersistentGUIData(Player player)
	{
		PERSISTENT_PLAYER_GUI_DATA.remove(player);
	}

	public static void openGUI(Player player, GUIType type, @Nullable Enchantment enchantment, @Nullable Attribute attribute)
	{
		openGUI(player, type, enchantment, attribute, 1);
	}

	public static void openGUI(Player player, @NotNull GUIType type, @Nullable Enchantment enchantment, @Nullable Attribute attribute, int data)
	{
		ItemStack heldItem = player.getInventory().getItemInMainHand().isEmpty() ? player.getInventory().getItemInOffHand().isEmpty() ? ItemStack.empty() : player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
		ArrayList<ItemStack> items = isPlayerInGUI(player) && getPlayerGUIData(player).hasItems() ? getPlayerGUIData(player).getCurrentItems() : new ArrayList<>(List.of(heldItem));
		if ((type == GUIType.ENCHANTING || type == GUIType.ATTRIBUTE) && (data < 1 || data > type.getMaxPage(items))) return; // data is GUI page
		GUIType currentGUIType = isPlayerInGUI(player) ? getPlayerGUIData(player).getGUIType() : null;
		ItemHelperPlugin.getInstance().getLogger().info("Attempting to open GUI " + (type != null ? type.getName() : "null") + " from old GUI type " + (currentGUIType != null ? currentGUIType.getName() : "null") + " for player " + player.getName() + " with items: " + items);
		if (type != GUIType.LEVEL_CUSTOM)
		{
			Inventory gui = type.getGUI(player, items, enchantment, attribute, data, !isPlayerInGUI(player) || getPlayerGUIData(player).isSmallText());
			if (isPlayerInGUI(player) && currentGUIType == type && getPlayerGUIData(player).getGUIPage() > -1) Utility.replaceGUI(player.getOpenInventory().getTopInventory(), gui);
			else player.openInventory(gui);
		}
		else
		{
			SignGUI signGUI;
			try
			{
				signGUI = SignGUI.builder().setLines(Utility.getColorEscapedString(Component.text("Level: "), true)).setType(Material.BAMBOO_SIGN).setHandler(ItemListener::onSignChange).build();
			}
			catch (SignGUIVersionException e)
			{
				ItemHelperPlugin.getInstance().getLogger().warning("An error occurred initializing the sign gui; please report this to the developer!");
				ItemHelperPlugin.getInstance().getLogger().warning("Full error:");
				Utility.printException(ItemHelperPlugin.getInstance().getLogger(), e);
				// TODO fall back to anvil
				return;
			}
			signGUI.open(player);
		}
		if (!PLAYER_GUI_DATA.containsKey(player)) PLAYER_GUI_DATA.put(player, new PlayerGUIData(items, type != GUIType.LEVEL && type != GUIType.LEVEL_CUSTOM ? data : -1, currentGUIType, type, enchantment, attribute, true));
		else
		{
			getPlayerGUIData(player).setCurrentItems(items);
			getPlayerGUIData(player).setGUIPage(data);
			getPlayerGUIData(player).setEnchantment(enchantment);
			getPlayerGUIData(player).setAttribute(attribute);
			if (type != GUIType.LEVEL_CUSTOM) getPlayerGUIData(player).setParentGUIType(currentGUIType);
			getPlayerGUIData(player).setGUIType(type);
			getPlayerGUIData(player).setSmallText(getPlayerGUIData(player).isSmallText());
		}
	}

	private static List<SignGUIAction> onSignChange(Player player, SignGUIResult signGUIResult)
	{
		double level = Double.MIN_VALUE;
		int matches = 0;
		Enchantment enchantment = getPlayerGUIData(player).getEnchantment();
		Attribute attribute = getPlayerGUIData(player).getAttribute();
		if (signGUIResult.getLineWithoutColor(0).trim().equals("Level:") && signGUIResult.getLineWithoutColor(1).trim().isEmpty() && signGUIResult.getLineWithoutColor(2).trim().isEmpty() && signGUIResult.getLineWithoutColor(3).trim().isEmpty())
		{
			return List.of(SignGUIAction.runSync(ItemHelperPlugin.getInstance(), () ->
			{
				player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
				openGUI(player, getPlayerGUIData(player).getParentGUIType(), enchantment, attribute);
			}));
		}
		for (int i = 0; i < 4; i++)
		{
			String line = signGUIResult.getLineWithoutColor(i).trim();
			if (line.isEmpty()) continue;
			try
			{
				level = Double.parseDouble(line.trim());
				matches++;
			}
			catch (NumberFormatException e)
			{
				try
				{
					if (line.trim().startsWith("Level:"))
					{
						level = Double.parseDouble(line.trim().substring("Level:".length()).trim());
						matches++;
					}
				}
				catch (NumberFormatException ex)
				{
					return List.of(SignGUIAction.displayNewLines("Level: ", null, DefaultTextColor.RED + "Invalid level on", DefaultTextColor.RED + "line " + (i + 1)));
				}
			}
		}
		if (matches != 1) return List.of(SignGUIAction.displayNewLines("Level: ", null, DefaultTextColor.RED + "Found too", DefaultTextColor.RED + "many numbers!"));
		if (getPlayerGUIData(player).hasEnchantment() && Math.floor(level) != level) return List.of(SignGUIAction.displayNewLines("Level:", null, DefaultTextColor.RED + "Enchantment level", "is not an integer!"));
		else if (level < 0 || (getPlayerGUIData(player).hasEnchantment() && level > 255)) return List.of(SignGUIAction.displayNewLines("Level:", null, DefaultTextColor.RED + "Out of range!", DefaultTextColor.RED + "(range: " + (getPlayerGUIData(player).hasEnchantment() ? "0-255" : "> 0") + ")"));
		else
		{
			double finalLevel = level;
			ArrayList<ItemStack> items = getPlayerGUIData(player).getCurrentItems();
			if (getPlayerGUIData(player).hasEnchantment()) items.forEach(itemStack -> itemStack.addUnsafeEnchantment(enchantment, (int) finalLevel));
			if (getPlayerGUIData(player).hasAttribute()) items.forEach(itemStack ->
			{
				if (!itemStack.hasItemMeta()) itemStack.setItemMeta(Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
				itemStack.editMeta(meta -> meta.addAttributeModifier(attribute, new AttributeModifier(attribute.getKey(), finalLevel, AttributeModifier.Operation.ADD_NUMBER)));
			});
			getPlayerGUIData(player).setCurrentItems(items);
			updateLore(items, getPlayerGUIData(player).isSmallText());
			return List.of(SignGUIAction.runSync(ItemHelperPlugin.getInstance(), () ->
			{
				player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
				openGUI(player, getPlayerGUIData(player).getParentGUIType(), enchantment, attribute);
			}));
		}
		//return List.of();
	}

	private static void updateLore(ArrayList<ItemStack> items, boolean useSmallCaps)
	{
		items.forEach(item ->
		{
			if (!item.getEnchantments().isEmpty() && !item.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			if (item.getEnchantments().isEmpty() && item.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) item.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
			if (item.hasItemMeta() && item.getItemMeta().hasAttributeModifiers() && item.getItemMeta().hasAttributeModifiers()) item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
			if (!item.hasItemMeta() || !item.getItemMeta().hasAttributeModifiers() || !item.getItemMeta().hasAttributeModifiers()) item.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
			if (!item.hasItemMeta()) item.setItemMeta(Bukkit.getItemFactory().getItemMeta(item.getType()));
			item.editMeta(meta ->
			{
				ArrayList<Component> lore = new ArrayList<>();
				meta.lore(List.of());
				if (!item.getEnchantments().isEmpty()) item.getEnchantments().forEach((key, value) ->
				{
					if (useSmallCaps) lore.add(Component.text(Utility.getSmallCapsString(Utility.getEmoji(key) + " " + Utility.formatText(key.getKey().getKey()) + ": " + value, true), DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
					else lore.add(Component.text(Utility.formatText(key.getKey().getKey()) + " " + value, DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
				});
				if (meta.hasAttributeModifiers())
				{
					for (var attributeModifier : meta.getAttributeModifiers().entries())
					{
						if (useSmallCaps) lore.add(Component.text(Utility.getSmallCapsString(Utility.getEmoji(attributeModifier.getKey()) + " " + Utility.formatText(attributeModifier.getKey().getKey().getKey()) + ": " + attributeModifier.getValue().getAmount(), true), DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
						else lore.add(Component.text(Utility.formatText(attributeModifier.getKey().getKey().getKey()) + " " + attributeModifier.getValue().getAmount(), DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
					}
				}
				meta.lore(lore);
			});
		});

	}
}
