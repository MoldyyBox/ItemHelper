package creeperpookie.enchanthelper.handlers;

import creeperpookie.enchanthelper.ItemHelperPlugin;
import creeperpookie.enchanthelper.gui.EnchantingGUI;
import creeperpookie.enchanthelper.gui.GUIType;
import creeperpookie.enchanthelper.gui.PlayerGUIData;
import creeperpookie.enchanthelper.items.CustomItem;
import creeperpookie.enchanthelper.items.ItemType;
import creeperpookie.enchanthelper.util.DefaultTextColor;
import creeperpookie.enchanthelper.util.Utility;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.SignGUIResult;
import de.rapha149.signgui.exception.SignGUIVersionException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockDataMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ItemListener implements Listener
{
	private static final HashMap<Player, PlayerGUIData> PLAYER_GUI_DATA = new HashMap<>();

	@EventHandler
	public void onItemClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getClickedInventory();
		ItemStack item = event.getCurrentItem();
		if (!isPlayerInGUI(player) || inventory instanceof PlayerInventory || item == null || (!CustomItem.isCustomItem(item) && !item.isSimilar(getPlayerGUIData(player).getItem()))) return;
		event.setCancelled(true);
		ItemStack savedItem = getPlayerGUIData(player).getItem();
		if (item.isSimilar(CustomItem.getItem(ItemType.NEXT_PAGE).getItemStack()))
		{
			int page = getPlayerGUIData(player).getEnchantingGUIPage();
			if (page >= 2)
			{
				Utility.sendError(player, "You are already on the last page!");
				return;
			}
			openGUI(player, page + 1);
		}
		else if (item.isSimilar(CustomItem.getItem(ItemType.PREVIOUS_PAGE).getItemStack()))
		{
			int page = getPlayerGUIData(player).getEnchantingGUIPage();
			if (page <= 1)
			{
				Utility.sendError(player, "You are already on the first page!");
				return;
			}
			openGUI(player, page - 1);
		}
		else if (item.isSimilar(CustomItem.getItem(ItemType.BACK_BUTTON).getItemStack()))
		{
			if (getPlayerGUIData(player).getGUIType() == GUIType.ENCHANTING_LEVEL)
			{
				player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
				Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player, getPlayerGUIData(player).getEnchantingGUIPage()));
			}
			else openGUI(player);
		}
		else if (item.isSimilar(savedItem) && getPlayerGUIData(player).getGUIType() == GUIType.ENCHANTING) player.closeInventory(InventoryCloseEvent.Reason.PLAYER);
		else
		{
			if (CustomItem.getItem(ItemType.SMALL_TEXT_TOGGLE).isItem(item))
			{
				getPlayerGUIData(player).setSmallText(!getPlayerGUIData(player).isSmallText());
				updateLore(savedItem, getPlayerGUIData(player).isSmallText());
				if (getPlayerGUIData(player).getGUIType() == GUIType.ENCHANTING) openGUI(player, getPlayerGUIData(player).getEnchantingGUIPage());
			}
			else if (CustomItem.getItem(ItemType.REMOVE_ENCHANTMENT).isItem(item))
			{
				if (getPlayerGUIData(player).getEnchantment() == null)
				{
					ItemHelperPlugin.getInstance().getLogger().warning("Clicked the remove enchantment item, but no enchantment was set!"); // should never happen
					return;
				}
				Enchantment enchantment = getPlayerGUIData(player).getEnchantment();
				if (savedItem.containsEnchantment(enchantment))
				{
					savedItem.removeEnchantment(enchantment);
					updateLore(savedItem, getPlayerGUIData(player).isSmallText());
					player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
					Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player));
				}
				else Utility.sendError(player, "The held item does not have the " + enchantment.getKey().getKey() + " enchantment!"); // also should never happen, can only be clicked if the item has the enchantment
			}
			else if (CustomItem.getItem(ItemType.BASIC_ENCHANTMENT_LEVEL).isItem(item))
			{
				String blockData = ((BlockDataMeta) item.getItemMeta()).getBlockData(Material.LIGHT).getAsString(); // example: minecraft:light[level=15,waterlogged=false]
				blockData = blockData.substring("minecraft:light".length() + 1, blockData.length() - 1); // from above: level=15,waterlogged=false
				int parsedLightLevel = Integer.parseInt(blockData.split(",")[Utility.arrayIndexOfSubstring(blockData.split(","), "level")].split("=")[1]);
				int level = Utility.getEnchantingLevel(parsedLightLevel);
				savedItem.addUnsafeEnchantment(getPlayerGUIData(player).getEnchantment(), level);
				updateLore(savedItem, getPlayerGUIData(player).isSmallText());
				player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
				Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openGUI(player));
			}
			else if (CustomItem.getItem(ItemType.CUSTOM_ENCHANTMENT_LEVEL).isItem(item))
			{
				player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
				Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openCustomLevelGUI(player, getPlayerGUIData(player).getEnchantment()));
			}
			else
			{
				CustomItem customItem = CustomItem.getItem(item);
				if (customItem == null || !customItem.hasLinkedEnchantment())
				{
					if (customItem != null && !customItem.isItemType(ItemType.BLANK_SLOT)) ItemHelperPlugin.getInstance().getLogger().warning("Clicked custom GUI " + customItem.getName() + " item doesn't have a linked enchantment!");
					return;
				}
				Enchantment enchantment = customItem.getLinkedEnchantment();
				player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
				Bukkit.getScheduler().runTask(ItemHelperPlugin.getInstance(), () -> openLevelGUI(player, enchantment));
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		Player player = (Player) event.getPlayer();
		Inventory inventory = event.getInventory();
		if (!isPlayerInGUI(player) || inventory instanceof PlayerInventory) return;
		event.getInventory().clear();
		if (event.getReason() != InventoryCloseEvent.Reason.OPEN_NEW && event.getReason() != InventoryCloseEvent.Reason.PLUGIN)
		{
			ItemStack item = getPlayerGUIData(player).getItem();
			if (!player.getInventory().getItemInMainHand().isSimilar(item))
			{
				player.getInventory().setItemInMainHand(item);
				if (!item.getEnchantments().isEmpty()) player.sendActionBar(Component.text("Successfully updated your", DefaultTextColor.GREEN).appendSpace().append(item.effectiveName().color(DefaultTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).appendSpace().append(Component.text("with", DefaultTextColor.GREEN)).appendSpace().append(Component.text(item.getEnchantments().size(), DefaultTextColor.AQUA)).appendSpace().append(Component.text("enchantment").append(Component.text(item.getEnchantments().size() == 1 ? "" : "s")).append(Component.text("!")).color(DefaultTextColor.GREEN)).decoration(TextDecoration.ITALIC, false));
				else player.sendActionBar((Component.text("Successfully removed all enchantments from your", DefaultTextColor.GREEN).appendSpace().append(item.effectiveName().color(DefaultTextColor.LIGHT_PURPLE).append(Component.text("!", DefaultTextColor.GREEN))).decoration(TextDecoration.ITALIC, false)));
			}
			PLAYER_GUI_DATA.remove(player);
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

	public static void openGUI(Player player)
	{
		openGUI(player, 1);
	}

	public static void openGUI(Player player, int page)
	{
		if (page < 1 || page > 2) return;
		ItemStack item = PLAYER_GUI_DATA.containsKey(player) && getPlayerGUIData(player).hasItem() ? getPlayerGUIData(player).getItem() : player.getInventory().getItemInMainHand();
		Inventory gui = Utility.copyInventory(EnchantingGUI.getEnchantingGUI(player, item, page, !isPlayerInGUI(player) || getPlayerGUIData(player).isSmallText()), EnchantingGUI.getTitle(GUIType.ENCHANTING));
		if (isPlayerInGUI(player) && getPlayerGUIData(player).getEnchantingGUIPage() > -1) Utility.replaceGUI(player.getOpenInventory().getTopInventory(), gui);
		else player.openInventory(gui);
		if (!PLAYER_GUI_DATA.containsKey(player)) PLAYER_GUI_DATA.put(player, new PlayerGUIData(item, page, GUIType.ENCHANTING, null, true));
		else
		{
			getPlayerGUIData(player).setItem(item);
			getPlayerGUIData(player).setEnchantingGUIPage(page);
			getPlayerGUIData(player).setGUIType(GUIType.ENCHANTING);
			getPlayerGUIData(player).setEnchantment(null);
			getPlayerGUIData(player).setSmallText(getPlayerGUIData(player).isSmallText());
		}
	}

	public static void openLevelGUI(Player player, Enchantment enchantment)
	{
		ItemStack item = PLAYER_GUI_DATA.containsKey(player) && getPlayerGUIData(player).hasItem() ? getPlayerGUIData(player).getItem() : player.getInventory().getItemInMainHand();
		Inventory gui = EnchantingGUI.getLevelGUI(player, item, enchantment);
		player.openInventory(gui);
		if (!PLAYER_GUI_DATA.containsKey(player)) PLAYER_GUI_DATA.put(player, new PlayerGUIData(item, -1, GUIType.ENCHANTING_LEVEL, enchantment, true));
		else
		{
			getPlayerGUIData(player).setItem(item);
			getPlayerGUIData(player).setEnchantingGUIPage(-1);
			getPlayerGUIData(player).setGUIType(GUIType.ENCHANTING_LEVEL);
			getPlayerGUIData(player).setEnchantment(enchantment);
			getPlayerGUIData(player).setSmallText(getPlayerGUIData(player).isSmallText());
		}
	}

	public static void openCustomLevelGUI(Player player, Enchantment enchantment)
	{
		SignGUI signGUI;
		try
		{
			signGUI = SignGUI.builder().setLines(Utility.getColorEscapedString(Component.text("Enter level: "), true)).setType(Material.BAMBOO_SIGN).setHandler(ItemListener::onSignChange).build();
		}
		catch (SignGUIVersionException e)
		{
			ItemHelperPlugin.getInstance().getLogger().warning("An error occurred initializing the sign gui; please report this to the developer!");
			// TODO fall back to anvil
			return;
		}
		signGUI.open(player);
		ItemStack item = PLAYER_GUI_DATA.containsKey(player) && getPlayerGUIData(player).hasItem() ? getPlayerGUIData(player).getItem() : player.getInventory().getItemInMainHand();
		if (!PLAYER_GUI_DATA.containsKey(player)) PLAYER_GUI_DATA.put(player, new PlayerGUIData(item, -1, GUIType.ENCHANTING_LEVEL_CUSTOM, enchantment, true));
		else
		{
			getPlayerGUIData(player).setItem(item);
			getPlayerGUIData(player).setEnchantingGUIPage(-1);
			getPlayerGUIData(player).setGUIType(GUIType.ENCHANTING_LEVEL_CUSTOM);
			getPlayerGUIData(player).setEnchantment(enchantment);
			getPlayerGUIData(player).setSmallText(getPlayerGUIData(player).isSmallText());
		}
	}

	private static List<SignGUIAction> onSignChange(Player player, SignGUIResult signGUIResult)
	{
		int level;
		String line1 = signGUIResult.getLineWithoutColor(0).trim();
		try
		{
			level = Integer.parseInt(line1.trim());
		}
		catch (NumberFormatException e)
		{
			try
			{
				if (line1.startsWith("Enter level:"))
				{
					level = Integer.parseInt(line1.substring(12).trim());
				}
				else
				{
					String line2 = signGUIResult.getLineWithoutColor(1).trim();
					try
					{
						level = Integer.parseInt(line2);
					}
					catch (NumberFormatException ex)
					{
						return List.of(SignGUIAction.displayNewLines("Enter level:", null, null, DefaultTextColor.RED + "Invalid level " + line2 + "!"));
					}
				}
			}
			catch (NumberFormatException ex)
			{
				return List.of(SignGUIAction.displayNewLines("Enter level:", null, null, DefaultTextColor.RED + "Invalid level " + line1 + "!"));
				//Utility.sendError(player, "The inputted level " + line1 + " is not a number!");
			}
		}
		if (level < 0 || level > 255)
		{
			Utility.sendError(player, "The inputted level must be between 0 and 255!");
		}
		else
		{
			getPlayerGUIData(player).getItem().addUnsafeEnchantment(getPlayerGUIData(player).getEnchantment(), level);
			updateLore(getPlayerGUIData(player).getItem(), getPlayerGUIData(player).isSmallText());
			return List.of(SignGUIAction.runSync(ItemHelperPlugin.getInstance(), () ->
			{
				player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
				openGUI(player);
			}));
		}
		return Collections.emptyList();
	}

	private static void updateLore(ItemStack item, boolean useSmallCaps)
	{
		if (!item.getEnchantments().isEmpty() && !item.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		if (item.getEnchantments().isEmpty() && item.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) item.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.editMeta(meta ->
		{
			ArrayList<Component> lore = new ArrayList<>();
			meta.lore(List.of());
			item.getEnchantments().forEach((key, value) ->
			{
				if (useSmallCaps) lore.add(Component.text(Utility.getSmallCapsString(Utility.formatText(key.getKey().getKey()) + " " + value, true), DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
				else lore.add(Component.text(Utility.formatText(key.getKey().getKey()) + " " + value, DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
			});
			meta.lore(lore);
		});
	}
}
