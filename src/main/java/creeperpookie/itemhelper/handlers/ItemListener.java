package creeperpookie.itemhelper.handlers;

import creeperpookie.itemhelper.ItemHelperPlugin;
import creeperpookie.itemhelper.gui.GUIType;
import creeperpookie.itemhelper.gui.LastSuccessfulAction;
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
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemListener implements Listener
{
	private static final HashMap<Player, PersistentPlayerGUIData> PERSISTENT_PLAYER_GUI_DATA = new HashMap<>(); // data that is not cleared when closing GUIs
	private static final HashMap<Player, PlayerGUIData> PLAYER_GUI_DATA = new HashMap<>(); // data that is cleared when closing GUIs

	@EventHandler
	public void onItemClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		try
		{
			Inventory inventory = event.getClickedInventory();
			ItemStack item = event.getCurrentItem();
			if (!isPlayerInGUI(player) || inventory == null || inventory instanceof PlayerInventory || inventory instanceof CraftingInventory || item == null || !CustomItem.isCustomItem(item)) return;
			GUIType guiType = getPlayerGUIData(player).getGUIType();
			if (guiType != GUIType.SELECTED_ITEMS || CustomItem.isCustomItem(item)) event.setCancelled(true);
			int clickedSlot = event.getSlot();
			ArrayList<ItemStack> savedItems = getPlayerGUIData(player).getCurrentItems();
			Enchantment savedEnchantment = getPlayerGUIData(player).getEnchantment();
			Attribute savedAttribute = getPlayerGUIData(player).getAttribute();
			CustomItem customItem = CustomItem.getItem(item);
			if (customItem.isItemType(ItemType.NEXT_PAGE))
			{
				int page = getPlayerGUIData(player).getGUIPage();
				if (page >= guiType.getMaxPage())
				{
					Utility.sendError(player, "You are already on the last page!");
					return;
				}
				if (guiType == GUIType.SELECTED_ITEMS || guiType == GUIType.STORED_ITEMS) recheckGUIItems(inventory, guiType == GUIType.SELECTED_ITEMS ? savedItems : hasPersistentGUIData(player) ? getPersistentGUIData(player).getUnretrievedItems() : List.of(), page, 1, 52, guiType == GUIType.SELECTED_ITEMS);
				//ItemHelperPlugin.getInstance().getLogger().info("Clicked next page button, player: " + player.getName() + " current page: " + page);
				openGUI(player, guiType, savedEnchantment, savedAttribute, page + 1);
			}
			else if (customItem.isItemType(ItemType.PREVIOUS_PAGE))
			{
				int page = getPlayerGUIData(player).getGUIPage();
				if (page <= 0)
				{
					Utility.sendError(player, "You are already on the first page!");
					return;
				}
				if (guiType == GUIType.SELECTED_ITEMS || guiType == GUIType.STORED_ITEMS) recheckGUIItems(inventory, guiType == GUIType.SELECTED_ITEMS ? savedItems : hasPersistentGUIData(player) ? getPersistentGUIData(player).getUnretrievedItems() : List.of(), page, 1, 52, guiType == GUIType.SELECTED_ITEMS);
				//ItemHelperPlugin.getInstance().getLogger().info("Clicked previous page button, player: " + player.getName() + " current page: " + page);
				openGUI(player, guiType, savedEnchantment, savedAttribute, page - 1);
			}
			else if (customItem.isItemType(ItemType.BACK_BUTTON) && getPlayerGUIData(player).getParentGUIType() != null)
			{
				if (guiType == GUIType.SELECTED_ITEMS) recheckGUIItems(inventory, getPlayerGUIData(player).getCurrentItems(), getPlayerGUIData(player).getGUIPage(), 1, 52, true);
				//ItemHelperPlugin.getInstance().getLogger().info("Clicked back button, player: " + player.getName() + " current screen: " + getPlayerGUIData(player).getGUIType().getName() + ", parent screen: " + getPlayerGUIData(player).getParentGUIType().getName());
				player.closeInventory(getPlayerGUIData(player).hasItems() ? InventoryCloseEvent.Reason.OPEN_NEW : InventoryCloseEvent.Reason.PLAYER);
				if (isPlayerInGUI(player)) Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, getPlayerGUIData(player).getParentGUIType(), savedEnchantment, savedAttribute));
			}
			else if (customItem.isItemType(ItemType.PREVIOUS_HISTORY))
			{
				player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
				Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, GUIType.PREVIOUS_ITEMS, savedEnchantment, savedAttribute));
			}
			else if (customItem.isItemType(ItemType.PREVIOUS_HISTORY_ENTRY))
			{
				LastSuccessfulAction action = getPersistentGUIData(player).getLastSuccessfulActions().get(Math.abs(9 - getPersistentGUIData(player).getLastSuccessfulActions().size() - clickedSlot)); // TODO add support for > 9 history entries
				savedItems.forEach(savedItem ->
				{
					savedItem.editMeta(meta -> action.getAttributes().forEach(attribute ->
					{
						if (meta.hasAttributeModifiers() && meta.getAttributeModifiers(attribute.getLeft()) != null) meta.removeAttributeModifier(attribute.getLeft());
						meta.addAttributeModifier(attribute.getLeft(), new AttributeModifier(attribute.getLeft().getKey(), attribute.getRight(), AttributeModifier.Operation.ADD_NUMBER));
					}));
					action.getEnchantments().forEach(enchantment ->
					{
						if (savedItem.getEnchantmentLevel(enchantment.getLeft()) != 0) savedItem.removeEnchantment(enchantment.getLeft());
						savedItem.addUnsafeEnchantment(enchantment.getLeft(), enchantment.getRight());
					});
				});
				updateLore(savedItems, action.isSmallText());
				player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
				Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, getPlayerGUIData(player).getParentGUIType(), savedEnchantment, savedAttribute));
			}
			else if (customItem.isItemType(ItemType.BUNDLE_ITEMS))
			{
				recheckGUIItems(inventory, savedItems, getPlayerGUIData(player).getGUIPage(), 1, 52, true);
				if (player.getInventory().firstEmpty() == -1) Utility.sendError(player, "You do not have any free slots!");
				else if (!hasPersistentGUIData(player) || getPersistentGUIData(player).getUnretrievedItems().isEmpty()) Utility.sendError(player, "You have saved items to bundle!");
				else
				{
					ArrayList<ItemStack> bundledItems = new ArrayList<>();
					for (int index = 0; index < Math.min(getPersistentGUIData(player).getUnretrievedItems().size(), 27); index++)
					{
						bundledItems.add(getPersistentGUIData(player).getUnretrievedItems().get(index));
						inventory.clear(index);
					}
					ItemStack shulker = new ItemStack(Material.PURPLE_SHULKER_BOX);
					shulker.editMeta(BlockStateMeta.class, meta ->
					{
						meta.displayName(Component.text("Exported Items", DefaultTextColor.BLUE).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD));
						ShulkerBox blockState = (ShulkerBox) meta.getBlockState();
						bundledItems.forEach(blockState.getInventory()::addItem);
						meta.setBlockState(blockState);
					});
					player.getInventory().setItem(player.getInventory().firstEmpty(), shulker);
					recheckGUIItems(inventory, getPersistentGUIData(player).getUnretrievedItems(), getPlayerGUIData(player).getGUIPage(), 1, 52, true);
					player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
					Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, guiType, savedEnchantment, savedAttribute));
				}
			}
			else if (customItem.isItemType(ItemType.SMALL_TEXT_TOGGLE))
			{
				getPlayerGUIData(player).setSmallText(!getPlayerGUIData(player).isSmallText());
				updateLore(savedItems, getPlayerGUIData(player).isSmallText());
				//ItemHelperPlugin.getInstance().getLogger().info("Clicked small text toggle button, player: " + player.getName() + ", reopening current screen: " + getPlayerGUIData(player).getGUIType().getName());
				openGUI(player, guiType, savedEnchantment, savedAttribute, getPlayerGUIData(player).getGUIPage());
			}
			else if (customItem.isItemType(ItemType.CURRENT_ITEMS))
			{
				//ItemHelperPlugin.getInstance().getLogger().info("Clicked current items button, player: " + player.getName() + ", closing inventory and opening current items GUI");
				player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
				Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, GUIType.SELECTED_ITEMS, savedEnchantment, savedAttribute));
			}
			else if (customItem.isItemType(ItemType.REMOVE_VALUE))
			{
				if (savedEnchantment == null && savedAttribute == null)
				{
					//ItemHelperPlugin.getInstance().getLogger().warning("Clicked the remove value item, but no enchantment or attribute was set!"); // should never happen
					return;
				}
				AtomicInteger isRemoved = new AtomicInteger();
				for (ItemStack savedItem : savedItems)
				{
					if (savedItem == null || savedItem.isEmpty()) continue;
					else if (Tag.SHULKER_BOXES.isTagged(savedItem.getType()))
					{
						savedItem.editMeta(BlockStateMeta.class, meta ->
						{
							ShulkerBox blockState = (ShulkerBox) meta.getBlockState();
							blockState.getInventory().forEach(itemStack ->
							{
								if (savedEnchantment != null && savedItem.containsEnchantment(savedEnchantment))
								{
									isRemoved.updateAndGet(v -> v | 1);
									savedItem.removeEnchantment(savedEnchantment);
								}
								if (savedAttribute != null && meta.hasAttributeModifiers())
								{
									isRemoved.updateAndGet(v -> v | 2);
									meta.removeAttributeModifier(savedAttribute);
								}
							});
						});
					}
					if (savedEnchantment != null && savedItem.containsEnchantment(savedEnchantment))
					{
						isRemoved.updateAndGet(v -> v | 1);
						savedItem.removeEnchantment(savedEnchantment);
					}
					if (savedAttribute != null && savedItem.hasItemMeta() && savedItem.getItemMeta().hasAttributeModifiers())
					{
						isRemoved.updateAndGet(v -> v | 2);
						savedItem.editMeta(meta -> meta.removeAttributeModifier(savedAttribute));
					}
				}
				updateLore(savedItems, getPlayerGUIData(player).isSmallText());
				if (isRemoved.get() != 0 && (isRemoved.get() & 1) == 0 && savedEnchantment != null) Utility.sendError(player, "The held item does not have the " + savedEnchantment.getKey().getKey() + " enchantment!"); // also should never happen, can only be clicked if the item has the enchantment
				else if (isRemoved.get() != 0 && (isRemoved.get() & 2) == 0 && savedAttribute != null) Utility.sendError(player, "The held item does not have the " + savedAttribute.getKey().getKey() + " attribute!"); // also should never happen, can only be clicked if the item has the attribute
				//ItemHelperPlugin.getInstance().getLogger().info("Clicked remove enchantment/attribute button, player: " + player.getName() + ", closing inventory and opening parent gui type: " + getPlayerGUIData(player).getParentGUIType().getName());
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
					if (savedItem == null || savedItem.isEmpty()) return;
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
				//ItemHelperPlugin.getInstance().getLogger().info("Clicked basic level button, player: " + player.getName() + ", closing inventory and opening parent gui type: " + getPlayerGUIData(player).getParentGUIType().getName());
				player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
				Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, getPlayerGUIData(player).getParentGUIType(), savedEnchantment, savedAttribute));
			}
			else if (customItem.isItemType(ItemType.CUSTOM_LEVEL))
			{
				//ItemHelperPlugin.getInstance().getLogger().info("Clicked custom level button, player: " + player.getName() + ", closing inventory and opening custom level gui");
				player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
				Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, GUIType.LEVEL_CUSTOM, savedEnchantment, savedAttribute));
			}
		/*else if (!customItem.hasLinkedEnchantment() && !customItem.hasLinkedAttribute())
		{
			if (!customItem.isItemType(ItemType.BLANK_SLOT) && !customItem.isItemType(ItemType.EMPTY_SLOT)) ItemHelperPlugin.getInstance().getLogger().warning("Clicked custom GUI " + customItem.getName() + " item doesn't have a linked enchantment or attribute!");
		}*/
			else if (customItem.hasLinkedEnchantment() || customItem.hasLinkedAttribute())
			{
				Enchantment enchantment = customItem.getLinkedEnchantment();
				Attribute attribute = customItem.getLinkedAttribute();
				//ItemHelperPlugin.getInstance().getLogger().info("Clicked an enchant/attribute button, player: " + player.getName() + ", closing inventory and opening level selection gui");
				player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
				Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, GUIType.LEVEL, enchantment, attribute));
			}
		}
		catch (Exception e)
		{
			ItemHelperPlugin.getInstance().getLogger().severe("An error occurred while handling clicked items:");
			Utility.printException(ItemHelperPlugin.getInstance().getLogger(), e);
			clearPlayerGUIData(player);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		Player player = (Player) event.getPlayer();
		try
		{
			Inventory inventory = event.getInventory();
			if (!isPlayerInGUI(player) || inventory instanceof PlayerInventory || inventory instanceof CraftingInventory) return;
			for (int index = 0; index < inventory.getSize(); index++)
			{
				ItemStack item = inventory.getItem(index);
				if (CustomItem.isCustomItem(item)) inventory.setItem(index, ItemStack.empty()); // Don't clear non-custom items
			}
			if (getPlayerGUIData(player).getGUIType() != GUIType.SELECTED_ITEMS && getPlayerGUIData(player).getGUIType() != GUIType.STORED_ITEMS && event.getReason() != InventoryCloseEvent.Reason.OPEN_NEW && event.getReason() != InventoryCloseEvent.Reason.PLUGIN)
			{
				ArrayList<ItemStack> items = getPlayerGUIData(player).getCurrentItems();
				if (items.size() == 1 && items.getFirst().equals(getPlayerGUIData(player).getInitialItem()))
				{
					player.sendActionBar(Component.text("No changes were applied to your", DefaultTextColor.GOLD).appendSpace().append(items.getFirst().effectiveName().color(DefaultTextColor.AQUA).decoration(TextDecoration.ITALIC, false)));
					return;
				}
				if (!hasPersistentGUIData(player)) PERSISTENT_PLAYER_GUI_DATA.put(player, new PersistentPlayerGUIData(player.getUniqueId()));
				getPersistentGUIData(player).addLastSuccessfulAction(LastSuccessfulAction.getFromItem(items.getFirst(), getPlayerGUIData(player).isSmallText()), 9);
				GUIType guiType = getPlayerGUIData(player).getGUIType();
				if (Utility.getEmptySlotCount(player.getInventory()) < items.size())
				{
					Utility.sendError(player, "You do not have enough space in your inventory to retrieve your items!\nYou can retrieve your items again with the /ih items command.");
					getPersistentGUIData(player).addUnretrievedItems(items);
				}
				else items.forEach(item -> player.getInventory().setItem(player.getInventory().firstEmpty(), item.clone()));
				getPersistentGUIData(player).save();
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
			else if (getPlayerGUIData(player).getGUIType() == GUIType.STORED_ITEMS && event.getReason() != InventoryCloseEvent.Reason.OPEN_NEW && event.getReason() != InventoryCloseEvent.Reason.PLUGIN)
			{
				recheckGUIItems(inventory, getPersistentGUIData(player).getUnretrievedItems(), getPlayerGUIData(player).getGUIPage(), 1, 52, false);
			}
			else if (getPlayerGUIData(player).getGUIType() == GUIType.SELECTED_ITEMS && event.getReason() != InventoryCloseEvent.Reason.OPEN_NEW && event.getReason() != InventoryCloseEvent.Reason.PLUGIN)
			{
				recheckGUIItems(inventory, getPlayerGUIData(player).getCurrentItems(), getPlayerGUIData(player).getGUIPage(), 1, 52, true);
				if (getPlayerGUIData(player).getCurrentItems().isEmpty())
				{
					player.sendActionBar(Component.text("Successfully applied all").appendSpace().append(Component.text(getPlayerGUIData(player).hasEnchantment() ? "enchantments" : "attributes")).appendSpace().append(Component.text("to your items")).color(DefaultTextColor.GREEN));
					clearPlayerGUIData(player);
				}
			}
		}
		catch (Exception e)
		{
			ItemHelperPlugin.getInstance().getLogger().severe("An error occurred whilst handling closing gui:");
			Utility.printException(ItemHelperPlugin.getInstance().getLogger(), e);
			clearPlayerGUIData(player);
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

	private static void recheckGUIItems(Inventory inventory, List<ItemStack> items, int page, int minSlot, int maxSlot, boolean addItemsAllowed)
	{
		if (page < 0) page = 0;
		int itemIndex = ((page) * (maxSlot - minSlot));
		int itemsOnPage = items.size() - itemIndex;
		for (int index = minSlot; index < maxSlot; index++)
		{
			if (itemIndex < items.size()) items.remove(itemIndex);
			else break;
		}
		for (int inventoryIndex = minSlot; inventoryIndex < maxSlot; inventoryIndex++)
		{
			int itemSlot = ((page) * (maxSlot - minSlot)) + (inventoryIndex - minSlot);
			ItemStack inventoryItem = inventory.getItem(inventoryIndex);
			if ((addItemsAllowed || (inventoryIndex < itemsOnPage)) && inventoryItem != null && !inventoryItem.isEmpty() && itemSlot < items.size()) items.add(itemSlot, inventoryItem);
			else if ((addItemsAllowed || (inventoryIndex < itemsOnPage)) && inventoryItem != null && !inventoryItem.isEmpty() && itemSlot >= items.size()) items.add(inventoryItem);
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
		openGUI(player, type, enchantment, attribute, 0);
	}

	public static void openGUI(Player player, @NotNull GUIType type, @Nullable Enchantment enchantment, @Nullable Attribute attribute, int data)
	{
		try
		{
			if (!isPlayerInGUI(player))
			{
				if (!hasPersistentGUIData(player)) PERSISTENT_PLAYER_GUI_DATA.put(player, new PersistentPlayerGUIData(player.getUniqueId()));
				getPersistentGUIData(player).load();
			}
			ItemStack heldItem = player.getInventory().getItemInMainHand().isEmpty() ? player.getInventory().getItemInOffHand().isEmpty() ? ItemStack.empty() : player.getInventory().getItemInOffHand().clone() : player.getInventory().getItemInMainHand().clone();
			ArrayList<ItemStack> items = isPlayerInGUI(player) && getPlayerGUIData(player).hasItems() ? getPlayerGUIData(player).getCurrentItems() : new ArrayList<>(List.of(heldItem));
			if ((type == GUIType.ENCHANTING || type == GUIType.ATTRIBUTE) && (data < 0 || data > type.getMaxPage())) return; // data is GUI page
			GUIType currentGUIType = isPlayerInGUI(player) ? getPlayerGUIData(player).getGUIType() : null;
			//ItemHelperPlugin.getInstance().getLogger().info("Attempting to open GUI " + type.getName() + " from old GUI type " + (currentGUIType != null ? currentGUIType.getName() : "null") + " for player " + player.getName() + " with items: " + items);
			if (type != GUIType.LEVEL_CUSTOM)
			{
				Inventory gui = type.getGUI(player, type == GUIType.STORED_ITEMS ? hasPersistentGUIData(player) ? getPersistentGUIData(player).getUnretrievedItems() : new ArrayList<>() : items, enchantment, attribute, data, !isPlayerInGUI(player) || getPlayerGUIData(player).isSmallText());
				Inventory openInventory = player.getOpenInventory().getTopInventory();
				if (isPlayerInGUI(player) && currentGUIType == type && getPlayerGUIData(player).getGUIPage() > -1 && !(openInventory instanceof PlayerInventory) && !(openInventory instanceof CraftingInventory)) Utility.replaceGUI(openInventory, gui);
				else player.openInventory(gui);
			}
			else
			{
				SignGUI signGUI;
				try
				{
					signGUI = SignGUI.builder().setLines(Utility.getColorEscapedString(Component.text("Level: "), true)).setType(Material.BAMBOO_SIGN).setHandler(ItemListener::onSignChange).build();
				}
				catch (SignGUIVersionException signGuiException)
				{
					ItemHelperPlugin.getInstance().getLogger().warning("An error occurred initializing the sign gui; please report this to the developer!");
					ItemHelperPlugin.getInstance().getLogger().warning("Full error:");
					Utility.printException(ItemHelperPlugin.getInstance().getLogger(), signGuiException);
					// TODO fall back to anvil
					return;
				}
				signGUI.open(player);
			}
			if (!PLAYER_GUI_DATA.containsKey(player)) PLAYER_GUI_DATA.put(player, new PlayerGUIData(heldItem.clone(), items, type != GUIType.LEVEL && type != GUIType.LEVEL_CUSTOM ? data : -1, currentGUIType, type, enchantment, attribute, true));
			else
			{
				getPlayerGUIData(player).setCurrentItems(items);
				getPlayerGUIData(player).setGUIPage(data);
				getPlayerGUIData(player).setEnchantment(enchantment);
				getPlayerGUIData(player).setAttribute(attribute);
				if (type != GUIType.LEVEL_CUSTOM && currentGUIType != type) getPlayerGUIData(player).setParentGUIType(currentGUIType);
				getPlayerGUIData(player).setGUIType(type);
				getPlayerGUIData(player).setSmallText(getPlayerGUIData(player).isSmallText());
			}
		}
		catch (Exception e)
		{
			ItemHelperPlugin.getInstance().getLogger().severe("An error occurred whilst opening gui:");
			Utility.printException(ItemHelperPlugin.getInstance().getLogger(), e);
			clearPlayerGUIData(player);
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
			if (enchantment != null) items.forEach(itemStack -> itemStack.addUnsafeEnchantment(enchantment, (int) finalLevel));
			if (attribute != null) items.forEach(itemStack ->
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
			if (item == null || item.isEmpty()) return;
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
						else lore.add(Component.text(Utility.formatText(attributeModifier.getKey().getKey().getKey()) + ": " + attributeModifier.getValue().getAmount(), DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
					}
				}
				meta.lore(lore);
			});
		});
	}
}
