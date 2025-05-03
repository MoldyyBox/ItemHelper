package creeperpookie.itemhelper.gui;

import creeperpookie.itemhelper.items.CustomItem;
import creeperpookie.itemhelper.items.ItemType;
import creeperpookie.itemhelper.util.DefaultTextColor;
import creeperpookie.itemhelper.util.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public enum GUIType
{
	STORED_ITEMS,
	SELECTED_ITEMS,
	ENCHANTING,
	ATTRIBUTE,
	LEVEL,
	LEVEL_CUSTOM;

	private static final Component STORED_ITEMS_GUI_TITLE = Component.text("Stored Items", DefaultTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
	private static final Component SELECTED_ITEMS_GUI_TITLE = Component.text("Selected Items", DefaultTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
	private static final Component ENCHANTING_GUI_TITLE = Component.text("Enchantments", DefaultTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
	private static final Component ATTRIBUTE_GUI_TITLE = Component.text("Attributes", DefaultTextColor.BLUE).decoration(TextDecoration.ITALIC, false);
	private static final Component LEVEL_GUI_TITLE = Component.text("Enchantment Level", DefaultTextColor.BLUE).decoration(TextDecoration.ITALIC, false);
	private static final Inventory ENCHANTING_GUI_1 = Bukkit.createInventory(null, 54, ENCHANTING_GUI_TITLE);
	private static final Inventory ENCHANTING_GUI_2 = Bukkit.createInventory(null, 54, ENCHANTING_GUI_TITLE);
	private static final Inventory ATTRIBUTE_GUI_1 = Bukkit.createInventory(null, 54, ATTRIBUTE_GUI_TITLE);
	private static final Inventory ATTRIBUTE_GUI_2 = Bukkit.createInventory(null, 54, ATTRIBUTE_GUI_TITLE);
	private static final Inventory LEVEL_GUI = Bukkit.createInventory(null, 27, LEVEL_GUI_TITLE);

	public String getName()
	{
		return Utility.formatText(name());
	}

	/**
	 * Gets the GUI for the given player and items.
	 *
	 * @param player       The player to get the GUI for.
	 * @param items        The items to display in the GUI.
	 * @param enchantment  The enchantment to display in the GUI.
	 * @param attribute    The attribute to display in the GUI.
	 * @param page         The page of the GUI to display.
	 * @param useSmallText Whether to use small text in the GUI.
	 * @return The GUI for the given player and items, or null if no GUI is available.
	 */
	@NotNull
	public Inventory getGUI(Player player, ArrayList<ItemStack> items, @Nullable Enchantment enchantment, @Nullable Attribute attribute, int page, boolean useSmallText)
	{
		if (items.isEmpty()) throw new IllegalArgumentException("Tried to get an " + getName() + " GUI with no held items from player " + player.getName());
		else if (this == LEVEL && enchantment == null && attribute == null) throw new IllegalArgumentException("Tried to get an " + getName() + " GUI for no enchantment or attribute from player " + player.getName());
		else if (page < 0) throw new IllegalArgumentException("Tried to get an " + getName() + " GUI for an invalid page number " + page + " from player " + player.getName());
		else return Utility.copyInventory(switch (this)
		{
			case STORED_ITEMS ->
			{
				if (page > getMaxPage()) throw new IllegalArgumentException("Tried to get an " + getName() + " GUI for an invalid page number " + page + " from player " + player.getName());
				int startIndex = (52 * page);
				Inventory gui = Bukkit.createInventory(null, 54, getTitle());
				for (int index = 0, itemIndex = startIndex; itemIndex < items.size(); index++, itemIndex++)
				{
					ItemStack item = items.get(itemIndex);
					gui.setItem(index, item == null ? ItemStack.empty() : item.clone());
					if (index >= gui.getSize() - 2) break;
				}
				gui.setItem(gui.getSize() - 2, CustomItem.getItem(ItemType.PREVIOUS_PAGE).getItemStack());
				gui.setItem(gui.getSize() - 1, CustomItem.getItem(ItemType.NEXT_PAGE).getItemStack());
				yield gui;
			}
			case SELECTED_ITEMS ->
			{
				// TODO verify GUI generates correctly
				if (page > getMaxPage()) throw new IllegalArgumentException("Tried to get an " + getName() + " GUI for an invalid page number " + page + " from player " + player.getName());
				int startIndex = (51 * page);
				Inventory gui = Bukkit.createInventory(null, 54, getTitle());
				for (int index = 1, itemIndex = startIndex; itemIndex < items.size(); index++, itemIndex++)
				{
					ItemStack item = items.get(itemIndex);
					gui.setItem(index, item == null ? ItemStack.empty() : item.clone());
					if (index >= gui.getSize() - 2) break;
				}
				gui.setItem(0, CustomItem.getItem(ItemType.BACK_BUTTON).getItemStack(items.size()));
				gui.setItem(gui.getSize() - 2, CustomItem.getItem(ItemType.PREVIOUS_PAGE).getItemStack());
				gui.setItem(gui.getSize() - 1, CustomItem.getItem(ItemType.NEXT_PAGE).getItemStack());
				yield gui;
			}
			case ENCHANTING, ATTRIBUTE ->
			{
				Inventory gui = Utility.copyInventory(page == 0 ? this == ENCHANTING ? ENCHANTING_GUI_1 : ATTRIBUTE_GUI_1 : this == ENCHANTING ? ENCHANTING_GUI_2 : ATTRIBUTE_GUI_2, getTitle());
				gui.setItem(13, CustomItem.getItem(ItemType.CURRENT_ITEMS).getItemStack(items.size()));
				gui.setItem(15, CustomItem.getItem(ItemType.SMALL_TEXT_TOGGLE).getItemStack(useSmallText ? 1 : 0));
				yield gui;
			}
			case LEVEL ->
			{
				Inventory gui = Utility.copyInventory(LEVEL_GUI, getTitle());
				gui.setItem(4, CustomItem.getItem(ItemType.CURRENT_ITEMS).getItemStack(items.size()));
				if ((enchantment != null && items.getFirst().containsEnchantment(enchantment)) || (attribute != null && items.getFirst().hasItemMeta() && items.getFirst().getItemMeta().hasAttributeModifiers() && !items.getFirst().getItemMeta().getAttributeModifiers().isEmpty())) gui.setItem(9, CustomItem.getItem(ItemType.REMOVE_VALUE).getItemStack());
				else gui.setItem(9, CustomItem.getItem(ItemType.BASIC_LEVEL).getItemStack(0));
				yield gui;
			}
			case LEVEL_CUSTOM -> throw new IllegalStateException("GUI Type " + getName() + "does not have a inventory GUI");
		}, getTitle());
	}

	@Nullable
	public Component getTitle()
	{
		return switch (this)
		{
			case STORED_ITEMS -> STORED_ITEMS_GUI_TITLE;
			case SELECTED_ITEMS -> SELECTED_ITEMS_GUI_TITLE;
			case ENCHANTING -> ENCHANTING_GUI_TITLE;
			case ATTRIBUTE -> ATTRIBUTE_GUI_TITLE;
			case LEVEL -> LEVEL_GUI_TITLE;
			case LEVEL_CUSTOM -> null;
		};
	}

	public int getMaxPage()
	{
		return switch (this)
		{
			case STORED_ITEMS, SELECTED_ITEMS -> Integer.MAX_VALUE;
			case LEVEL, LEVEL_CUSTOM -> 1;
			case ENCHANTING, ATTRIBUTE -> 2;
		};
	}

	public static void registerItems()
	{
		ENCHANTING_GUI_1.clear();
		ENCHANTING_GUI_2.clear();
		ATTRIBUTE_GUI_1.clear();
		ATTRIBUTE_GUI_2.clear();
		LEVEL_GUI.clear();

		// Set up the enchanting and attribute GUIs
		for (int slot = 0; slot < 13; slot++)
		{
			ENCHANTING_GUI_1.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
			ENCHANTING_GUI_2.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
			ATTRIBUTE_GUI_1.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
			ATTRIBUTE_GUI_2.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		}
		// Slot index 13 is the held item
		ENCHANTING_GUI_1.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ENCHANTING_GUI_2.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ATTRIBUTE_GUI_1.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ATTRIBUTE_GUI_2.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		// Slot index 15 is the small text toggle
		for (int slot = 16; slot < 27; slot++)
		{
			ENCHANTING_GUI_1.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
			ENCHANTING_GUI_2.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
			ATTRIBUTE_GUI_1.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
			ATTRIBUTE_GUI_2.setItem(slot, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
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

		// Set up the attribute GUIs
		// Page 1
		ATTRIBUTE_GUI_1.setItem(27, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ATTRIBUTE_GUI_1.setItem(28, CustomItem.getItem(ItemType.ARMOR_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(29, CustomItem.getItem(ItemType.ARMOR_TOUGHNESS_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(30, CustomItem.getItem(ItemType.ATTACK_DAMAGE_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(31, CustomItem.getItem(ItemType.ATTACK_KNOCKBACK_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(32, CustomItem.getItem(ItemType.ATTACK_SPEED_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(33, CustomItem.getItem(ItemType.BLOCK_BREAK_SPEED_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(34, CustomItem.getItem(ItemType.BLOCK_INTERACTION_RANGE_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(35, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());

		ATTRIBUTE_GUI_1.setItem(36, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ATTRIBUTE_GUI_1.setItem(37, CustomItem.getItem(ItemType.BURNING_TIME_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(38, CustomItem.getItem(ItemType.ENTITY_INTERACTION_RANGE_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(39, CustomItem.getItem(ItemType.EXPLOSION_KNOCKBACK_RESISTANCE_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(40, CustomItem.getItem(ItemType.FALL_DAMAGE_MULTIPLIER_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(41, CustomItem.getItem(ItemType.FLYING_SPEED_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(42, CustomItem.getItem(ItemType.FOLLOW_RANGE_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(43, CustomItem.getItem(ItemType.GRAVITY_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(44, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());

		ATTRIBUTE_GUI_1.setItem(45, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ATTRIBUTE_GUI_1.setItem(46, CustomItem.getItem(ItemType.JUMP_STRENGTH_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(47, CustomItem.getItem(ItemType.KNOCKBACK_RESISTANCE_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(48, CustomItem.getItem(ItemType.LUCK_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(49, CustomItem.getItem(ItemType.MAX_ABSORPTION_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(50, CustomItem.getItem(ItemType.MAX_HEALTH_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(51, CustomItem.getItem(ItemType.MINING_EFFICIENCY_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(52, CustomItem.getItem(ItemType.MOVEMENT_EFFICIENCY_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_1.setItem(53, CustomItem.getItem(ItemType.NEXT_PAGE).getItemStack());

		// Page 2
		ATTRIBUTE_GUI_2.setItem(27, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ATTRIBUTE_GUI_2.setItem(28, CustomItem.getItem(ItemType.MOVEMENT_SPEED_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_2.setItem(29, CustomItem.getItem(ItemType.OXYGEN_BONUS_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_2.setItem(30, CustomItem.getItem(ItemType.SAFE_FALL_DISTANCE_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_2.setItem(31, CustomItem.getItem(ItemType.SCALE_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_2.setItem(32, CustomItem.getItem(ItemType.SNEAKING_SPEED_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_2.setItem(33, CustomItem.getItem(ItemType.SPAWN_REINFORCEMENTS_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_2.setItem(34, CustomItem.getItem(ItemType.STEP_HEIGHT_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_2.setItem(35, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());

		ATTRIBUTE_GUI_2.setItem(36, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ATTRIBUTE_GUI_2.setItem(37, CustomItem.getItem(ItemType.SUBMERGED_MINING_SPEED_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_2.setItem(38, CustomItem.getItem(ItemType.SWEEPING_DAMAGE_RATIO_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_2.setItem(39, CustomItem.getItem(ItemType.TEMPT_RANGE_ATTRIBUTE).getItemStack());
		ATTRIBUTE_GUI_2.setItem(40, CustomItem.getItem(ItemType.WATER_MOVEMENT_EFFICIENCY_ATTRIBUTE).getItemStack());

		for (int index = 41; index < 53; index++)
		{
			if (index == 44 || index == 45) continue;
			ATTRIBUTE_GUI_2.setItem(index, CustomItem.getItem(ItemType.EMPTY_SLOT).getItemStack());
		}
		ATTRIBUTE_GUI_2.setItem(44, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ATTRIBUTE_GUI_2.setItem(45, CustomItem.getItem(ItemType.PREVIOUS_PAGE).getItemStack());
		ATTRIBUTE_GUI_2.setItem(53, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());

		//LEVEL_GUI.setItem(9, CustomItem.getItem(ItemType.BASIC_ENCHANTMENT_LEVEL).getItemStack());
		// Slot 9 is either the level 1 enchantment level icon or the remove enchantment icon, depending on if the held item has the enchantment or not
		LEVEL_GUI.setItem(10, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		LEVEL_GUI.setItem(11, CustomItem.getItem(ItemType.BASIC_LEVEL).getItemStack(5));
		LEVEL_GUI.setItem(12, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		LEVEL_GUI.setItem(13, CustomItem.getItem(ItemType.BASIC_LEVEL).getItemStack(10));
		LEVEL_GUI.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		LEVEL_GUI.setItem(15, CustomItem.getItem(ItemType.BASIC_LEVEL).getItemStack(15));
		LEVEL_GUI.setItem(16, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		LEVEL_GUI.setItem(17, CustomItem.getItem(ItemType.CUSTOM_LEVEL).getItemStack());

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

	static
	{
		registerItems();
	}
}
