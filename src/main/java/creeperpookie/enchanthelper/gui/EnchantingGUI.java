package creeperpookie.enchanthelper.gui;

import creeperpookie.enchanthelper.items.CustomItem;
import creeperpookie.enchanthelper.items.ItemType;
import creeperpookie.enchanthelper.util.DefaultTextColor;
import creeperpookie.enchanthelper.util.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchantingGUI
{
	private static final TextComponent ENCHANTING_GUI_TITLE = Component.text("Enchantments", DefaultTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
	private static final TextComponent ENCHANT_LEVEL_GUI_TITLE = Component.text("Enchantment Level", DefaultTextColor.BLUE).decoration(TextDecoration.ITALIC, false);
	private static final Inventory ENCHANTING_GUI_1 = Bukkit.createInventory(null, 54, ENCHANTING_GUI_TITLE);
	private static final Inventory ENCHANTING_GUI_2 = Bukkit.createInventory(null, 54, ENCHANTING_GUI_TITLE);
	private static final Inventory LEVEL_GUI = Bukkit.createInventory(null, 27, ENCHANT_LEVEL_GUI_TITLE);

	public static Component getTitle(GUIType type)
	{
		return type == GUIType.ENCHANTING ? ENCHANTING_GUI_TITLE : ENCHANT_LEVEL_GUI_TITLE;
	}

	public static Inventory getEnchantingGUI(Player player, ItemStack item, int page, boolean useSmallText)
	{
		if (item.isEmpty()) throw new IllegalArgumentException("Tried to get an Enchanting GUI for an empty held item from player " + player.getName());
		Inventory gui = page == 1 ? EnchantingGUI.ENCHANTING_GUI_1 : EnchantingGUI.ENCHANTING_GUI_2;
		gui.setItem(13, item);
		gui.setItem(15, CustomItem.getItem(ItemType.SMALL_TEXT_TOGGLE).getItemStack(useSmallText ? 1 : 0));
		return Utility.copyInventory(gui, EnchantingGUI.ENCHANTING_GUI_TITLE);
	}

	public static Inventory getLevelGUI(Player player, ItemStack item, Enchantment enchantment)
	{
		if (item == null) item = ItemStack.empty();
		else item = item.clone();
		if (item.isEmpty()) throw new IllegalArgumentException("Tried to get an Enchanting GUI for an empty held item from player " + player.getName());
		Inventory gui = LEVEL_GUI;
		gui.setItem(4, item);
		if (item.containsEnchantment(enchantment)) gui.setItem(9, CustomItem.getItem(ItemType.REMOVE_ENCHANTMENT).getItemStack());
		else gui.setItem(9, CustomItem.getItem(ItemType.BASIC_ENCHANTMENT_LEVEL).getItemStack(0));
		return Utility.copyInventory(gui, ENCHANT_LEVEL_GUI_TITLE);
	}

	static
	{
		// Set up the enchanting GUI
		for (int slot = 0; slot < 13; slot++)
		{
			ENCHANTING_GUI_1.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
			ENCHANTING_GUI_2.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		}
		// Slot index 13 is the held item
		ENCHANTING_GUI_1.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ENCHANTING_GUI_2.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		// Slot index 15 is the small text toggle
		for (int slot = 16; slot < 27; slot++)
		{
			ENCHANTING_GUI_1.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
			ENCHANTING_GUI_2.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		}

		// Set up the level GUI
		for (int slot = 0; slot < 4; slot++)
		{
			LEVEL_GUI.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		}
		// Slot index 4 is the held item
		for (int slot = 5; slot < 9; slot++)
		{
			LEVEL_GUI.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		}

		// Page 1
		ENCHANTING_GUI_1.setItem(27, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ENCHANTING_GUI_1.setItem(28, CustomItem.getItem(ItemType.AQUA_AFFINITY).getItemStack());
		ENCHANTING_GUI_1.setItem(29, CustomItem.getItem(ItemType.BANE_OF_ARTHROPODS).getItemStack());
		ENCHANTING_GUI_1.setItem(30, CustomItem.getItem(ItemType.BLAST_PROTECTION).getItemStack());
		ENCHANTING_GUI_1.setItem(31, CustomItem.getItem(ItemType.BREACH).getItemStack());
		ENCHANTING_GUI_1.setItem(32, CustomItem.getItem(ItemType.CHANNELING).getItemStack());
		ENCHANTING_GUI_1.setItem(33, CustomItem.getItem(ItemType.CURSE_OF_BINDING).getItemStack());
		ENCHANTING_GUI_1.setItem(34, CustomItem.getItem(ItemType.CURSE_OF_VANISHING).getItemStack());
		ENCHANTING_GUI_1.setItem(35, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());

		ENCHANTING_GUI_1.setItem(36, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ENCHANTING_GUI_1.setItem(37, CustomItem.getItem(ItemType.DENSITY).getItemStack());
		ENCHANTING_GUI_1.setItem(38, CustomItem.getItem(ItemType.DEPTH_STRIDER).getItemStack());
		ENCHANTING_GUI_1.setItem(39, CustomItem.getItem(ItemType.EFFICIENCY).getItemStack());
		ENCHANTING_GUI_1.setItem(40, CustomItem.getItem(ItemType.FEATHER_FALLING).getItemStack());
		ENCHANTING_GUI_1.setItem(41, CustomItem.getItem(ItemType.FIRE_ASPECT).getItemStack());
		ENCHANTING_GUI_1.setItem(42, CustomItem.getItem(ItemType.FIRE_PROTECTION).getItemStack());
		ENCHANTING_GUI_1.setItem(43, CustomItem.getItem(ItemType.FLAME).getItemStack());
		ENCHANTING_GUI_1.setItem(44, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());

		ENCHANTING_GUI_1.setItem(45, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ENCHANTING_GUI_1.setItem(46, CustomItem.getItem(ItemType.FORTUNE).getItemStack());
		ENCHANTING_GUI_1.setItem(47, CustomItem.getItem(ItemType.FROST_WALKER).getItemStack());
		ENCHANTING_GUI_1.setItem(48, CustomItem.getItem(ItemType.IMPALING).getItemStack());
		ENCHANTING_GUI_1.setItem(49, CustomItem.getItem(ItemType.INFINITY).getItemStack());
		ENCHANTING_GUI_1.setItem(50, CustomItem.getItem(ItemType.KNOCKBACK).getItemStack());
		ENCHANTING_GUI_1.setItem(51, CustomItem.getItem(ItemType.LOOTING).getItemStack());
		ENCHANTING_GUI_1.setItem(52, CustomItem.getItem(ItemType.LOYALTY).getItemStack());
		ENCHANTING_GUI_1.setItem(53, CustomItem.getItem(ItemType.NEXT_PAGE).getItemStack());

		// Page 2
		ENCHANTING_GUI_2.setItem(27, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ENCHANTING_GUI_2.setItem(28, CustomItem.getItem(ItemType.LUCK_OF_THE_SEA).getItemStack());
		ENCHANTING_GUI_2.setItem(29, CustomItem.getItem(ItemType.LURE).getItemStack());
		ENCHANTING_GUI_2.setItem(30, CustomItem.getItem(ItemType.MENDING).getItemStack());
		ENCHANTING_GUI_2.setItem(31, CustomItem.getItem(ItemType.MULTISHOT).getItemStack());
		ENCHANTING_GUI_2.setItem(32, CustomItem.getItem(ItemType.PIERCING).getItemStack());
		ENCHANTING_GUI_2.setItem(33, CustomItem.getItem(ItemType.PROTECTION).getItemStack());
		ENCHANTING_GUI_2.setItem(34, CustomItem.getItem(ItemType.PROJECTILE_PROTECTION).getItemStack());
		ENCHANTING_GUI_2.setItem(35, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());

		ENCHANTING_GUI_2.setItem(36, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ENCHANTING_GUI_2.setItem(37, CustomItem.getItem(ItemType.POWER).getItemStack());
		ENCHANTING_GUI_2.setItem(38, CustomItem.getItem(ItemType.PUNCH).getItemStack());
		ENCHANTING_GUI_2.setItem(39, CustomItem.getItem(ItemType.QUICK_CHARGE).getItemStack());
		ENCHANTING_GUI_2.setItem(40, CustomItem.getItem(ItemType.RESPIRATION).getItemStack());
		ENCHANTING_GUI_2.setItem(41, CustomItem.getItem(ItemType.RIPTIDE).getItemStack());
		ENCHANTING_GUI_2.setItem(42, CustomItem.getItem(ItemType.SHARPNESS).getItemStack());
		ENCHANTING_GUI_2.setItem(43, CustomItem.getItem(ItemType.SILK_TOUCH).getItemStack());
		ENCHANTING_GUI_2.setItem(44, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());

		ENCHANTING_GUI_2.setItem(45, CustomItem.getItem(ItemType.PREVIOUS_PAGE).getItemStack());
		ENCHANTING_GUI_2.setItem(46, CustomItem.getItem(ItemType.SMITE).getItemStack());
		ENCHANTING_GUI_2.setItem(47, CustomItem.getItem(ItemType.SOUL_SPEED).getItemStack());
		ENCHANTING_GUI_2.setItem(48, CustomItem.getItem(ItemType.SWEEPING_EDGE).getItemStack());
		ENCHANTING_GUI_2.setItem(49, CustomItem.getItem(ItemType.SWIFT_SNEAK).getItemStack());
		ENCHANTING_GUI_2.setItem(50, CustomItem.getItem(ItemType.THORNS).getItemStack());
		ENCHANTING_GUI_2.setItem(51, CustomItem.getItem(ItemType.UNBREAKING).getItemStack());
		ENCHANTING_GUI_2.setItem(52, CustomItem.getItem(ItemType.WIND_BURST).getItemStack());
		ENCHANTING_GUI_2.setItem(53, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());

		//LEVEL_GUI.setItem(9, CustomItem.getItem(ItemType.BASIC_ENCHANTMENT_LEVEL).getItemStack());
		// Slot 9 is either the level 1 enchantment level icon or the remove enchantment icon, depending on if the held item has the enchantment or not
		LEVEL_GUI.setItem(10, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		LEVEL_GUI.setItem(11, CustomItem.getItem(ItemType.BASIC_ENCHANTMENT_LEVEL).getItemStack(5));
		LEVEL_GUI.setItem(12, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		LEVEL_GUI.setItem(13, CustomItem.getItem(ItemType.BASIC_ENCHANTMENT_LEVEL).getItemStack(10));
		LEVEL_GUI.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		LEVEL_GUI.setItem(15, CustomItem.getItem(ItemType.BASIC_ENCHANTMENT_LEVEL).getItemStack(15));
		LEVEL_GUI.setItem(16, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		LEVEL_GUI.setItem(17, CustomItem.getItem(ItemType.CUSTOM_ENCHANTMENT_LEVEL).getItemStack());

		for (int slot = 18; slot < 22; slot++)
		{
			LEVEL_GUI.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		}
		LEVEL_GUI.setItem(22, CustomItem.getItem(ItemType.BACK_BUTTON).getItemStack());
		for (int slot = 23; slot < 27; slot++)
		{
			LEVEL_GUI.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		}
	}
}
