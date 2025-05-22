package creeperpookie.itemhelper.gui;

import creeperpookie.itemhelper.items.CustomItem;
import creeperpookie.itemhelper.items.ItemType;
import creeperpookie.itemhelper.items.gui.PreviousHistoryEntryItem;
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
				gui.setItem(gui.getSize() - 2, ItemType.PREVIOUS_PAGE.getItemStack());
				gui.setItem(gui.getSize() - 1, ItemType.NEXT_PAGE.getItemStack());
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
				gui.setItem(0, ItemType.BACK_BUTTON.getItemStack(items.size()));
				gui.setItem(gui.getSize() - 2, ItemType.PREVIOUS_PAGE.getItemStack());
				gui.setItem(gui.getSize() - 1, ItemType.NEXT_PAGE.getItemStack());
				yield gui;
			}
			case ENCHANTING, ATTRIBUTE ->
			{
				Inventory gui = Utility.copyInventory(page == 0 ? this == ENCHANTING ? ENCHANTING_GUI_1 : ATTRIBUTE_GUI_1 : this == ENCHANTING ? ENCHANTING_GUI_2 : ATTRIBUTE_GUI_2, getTitle());
				gui.setItem(11, ItemListener.hasPersistentGUIData(player) && ItemListener.getPersistentGUIData(player).hasLastSuccessfulActions() ? ItemType.PREVIOUS_HISTORY.getItemStack() : ItemType.BLANK_SLOT.getItemStack());
				gui.setItem(13, ItemType.CURRENT_ITEMS.getItemStack(items.size()));
				gui.setItem(15, ItemType.SMALL_TEXT_TOGGLE.getItemStack(useSmallText ? 1 : 0));
				yield gui;
			}
			case LEVEL ->
			{
				Inventory gui = Utility.copyInventory(LEVEL_GUI, getTitle());
				gui.setItem(4, ItemType.CURRENT_ITEMS.getItemStack(items.size()));
				if ((enchantment != null && items.getFirst().containsEnchantment(enchantment)) || (attribute != null && items.getFirst().hasItemMeta() && items.getFirst().getItemMeta().hasAttributeModifiers() && !items.getFirst().getItemMeta().getAttributeModifiers().isEmpty())) gui.setItem(9, ItemType.REMOVE_VALUE.getItemStack());
				else gui.setItem(9, ItemType.BASIC_LEVEL.getItemStack(0));
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
			ENCHANTING_GUI_1.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
			ENCHANTING_GUI_2.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
			ATTRIBUTE_GUI_1.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
			ATTRIBUTE_GUI_2.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
		}
		// Slot index 13 is the held item
		ENCHANTING_GUI_1.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ENCHANTING_GUI_2.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ATTRIBUTE_GUI_1.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ATTRIBUTE_GUI_2.setItem(14, CustomItem.getItem(ItemType.BLANK_SLOT).getItemStack());
		ENCHANTING_GUI_1.setItem(14, ItemType.BLANK_SLOT.getItemStack());
		ENCHANTING_GUI_2.setItem(14, ItemType.BLANK_SLOT.getItemStack());
		ATTRIBUTE_GUI_1.setItem(14, ItemType.BLANK_SLOT.getItemStack());
		ATTRIBUTE_GUI_2.setItem(14, ItemType.BLANK_SLOT.getItemStack());
		// Slot index 15 is the small text toggle
		for (int slot = 16; slot < 27; slot++)
		{
			ENCHANTING_GUI_1.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
			ENCHANTING_GUI_2.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
			ATTRIBUTE_GUI_1.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
			ATTRIBUTE_GUI_2.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
		}

		// Set up the level GUI
		for (int slot = 0; slot < 4; slot++)
		{
			LEVEL_GUI.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
		}
		// Slot index 4 is the held item
		for (int slot = 5; slot < 9; slot++)
		{
			LEVEL_GUI.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
		}

		// Page 1
		ENCHANTING_GUI_1.setItem(27, ItemType.BLANK_SLOT.getItemStack());
		ENCHANTING_GUI_1.setItem(28, ItemType.AQUA_AFFINITY.getItemStack());
		ENCHANTING_GUI_1.setItem(29, ItemType.BANE_OF_ARTHROPODS.getItemStack());
		ENCHANTING_GUI_1.setItem(30, ItemType.BLAST_PROTECTION.getItemStack());
		ENCHANTING_GUI_1.setItem(31, ItemType.BREACH.getItemStack());
		ENCHANTING_GUI_1.setItem(32, ItemType.CHANNELING.getItemStack());
		ENCHANTING_GUI_1.setItem(33, ItemType.CURSE_OF_BINDING.getItemStack());
		ENCHANTING_GUI_1.setItem(34, ItemType.CURSE_OF_VANISHING.getItemStack());
		ENCHANTING_GUI_1.setItem(35, ItemType.BLANK_SLOT.getItemStack());

		ENCHANTING_GUI_1.setItem(36, ItemType.BLANK_SLOT.getItemStack());
		ENCHANTING_GUI_1.setItem(37, ItemType.DENSITY.getItemStack());
		ENCHANTING_GUI_1.setItem(38, ItemType.DEPTH_STRIDER.getItemStack());
		ENCHANTING_GUI_1.setItem(39, ItemType.EFFICIENCY.getItemStack());
		ENCHANTING_GUI_1.setItem(40, ItemType.FEATHER_FALLING.getItemStack());
		ENCHANTING_GUI_1.setItem(41, ItemType.FIRE_ASPECT.getItemStack());
		ENCHANTING_GUI_1.setItem(42, ItemType.FIRE_PROTECTION.getItemStack());
		ENCHANTING_GUI_1.setItem(43, ItemType.FLAME.getItemStack());
		ENCHANTING_GUI_1.setItem(44, ItemType.BLANK_SLOT.getItemStack());

		ENCHANTING_GUI_1.setItem(45, ItemType.BLANK_SLOT.getItemStack());
		ENCHANTING_GUI_1.setItem(46, ItemType.FORTUNE.getItemStack());
		ENCHANTING_GUI_1.setItem(47, ItemType.FROST_WALKER.getItemStack());
		ENCHANTING_GUI_1.setItem(48, ItemType.IMPALING.getItemStack());
		ENCHANTING_GUI_1.setItem(49, ItemType.INFINITY.getItemStack());
		ENCHANTING_GUI_1.setItem(50, ItemType.KNOCKBACK.getItemStack());
		ENCHANTING_GUI_1.setItem(51, ItemType.LOOTING.getItemStack());
		ENCHANTING_GUI_1.setItem(52, ItemType.LOYALTY.getItemStack());
		ENCHANTING_GUI_1.setItem(53, ItemType.NEXT_PAGE.getItemStack());

		// Page 2
		ENCHANTING_GUI_2.setItem(27, ItemType.BLANK_SLOT.getItemStack());
		ENCHANTING_GUI_2.setItem(28, ItemType.LUCK_OF_THE_SEA.getItemStack());
		ENCHANTING_GUI_2.setItem(29, ItemType.LURE.getItemStack());
		ENCHANTING_GUI_2.setItem(30, ItemType.MENDING.getItemStack());
		ENCHANTING_GUI_2.setItem(31, ItemType.MULTISHOT.getItemStack());
		ENCHANTING_GUI_2.setItem(32, ItemType.PIERCING.getItemStack());
		ENCHANTING_GUI_2.setItem(33, ItemType.PROTECTION.getItemStack());
		ENCHANTING_GUI_2.setItem(34, ItemType.PROJECTILE_PROTECTION.getItemStack());
		ENCHANTING_GUI_2.setItem(35, ItemType.BLANK_SLOT.getItemStack());

		ENCHANTING_GUI_2.setItem(36, ItemType.BLANK_SLOT.getItemStack());
		ENCHANTING_GUI_2.setItem(37, ItemType.POWER.getItemStack());
		ENCHANTING_GUI_2.setItem(38, ItemType.PUNCH.getItemStack());
		ENCHANTING_GUI_2.setItem(39, ItemType.QUICK_CHARGE.getItemStack());
		ENCHANTING_GUI_2.setItem(40, ItemType.RESPIRATION.getItemStack());
		ENCHANTING_GUI_2.setItem(41, ItemType.RIPTIDE.getItemStack());
		ENCHANTING_GUI_2.setItem(42, ItemType.SHARPNESS.getItemStack());
		ENCHANTING_GUI_2.setItem(43, ItemType.SILK_TOUCH.getItemStack());
		ENCHANTING_GUI_2.setItem(44, ItemType.BLANK_SLOT.getItemStack());

		ENCHANTING_GUI_2.setItem(45, ItemType.PREVIOUS_PAGE.getItemStack());
		ENCHANTING_GUI_2.setItem(46, ItemType.SMITE.getItemStack());
		ENCHANTING_GUI_2.setItem(47, ItemType.SOUL_SPEED.getItemStack());
		ENCHANTING_GUI_2.setItem(48, ItemType.SWEEPING_EDGE.getItemStack());
		ENCHANTING_GUI_2.setItem(49, ItemType.SWIFT_SNEAK.getItemStack());
		ENCHANTING_GUI_2.setItem(50, ItemType.THORNS.getItemStack());
		ENCHANTING_GUI_2.setItem(51, ItemType.UNBREAKING.getItemStack());
		ENCHANTING_GUI_2.setItem(52, ItemType.WIND_BURST.getItemStack());
		ENCHANTING_GUI_2.setItem(53, ItemType.BLANK_SLOT.getItemStack());

		// Set up the attribute GUIs
		// Page 1
		ATTRIBUTE_GUI_1.setItem(27, ItemType.BLANK_SLOT.getItemStack());
		ATTRIBUTE_GUI_1.setItem(28, ItemType.ARMOR_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(29, ItemType.ARMOR_TOUGHNESS_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(30, ItemType.ATTACK_DAMAGE_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(31, ItemType.ATTACK_KNOCKBACK_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(32, ItemType.ATTACK_SPEED_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(33, ItemType.BLOCK_BREAK_SPEED_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(34, ItemType.BLOCK_INTERACTION_RANGE_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(35, ItemType.BLANK_SLOT.getItemStack());

		ATTRIBUTE_GUI_1.setItem(36, ItemType.BLANK_SLOT.getItemStack());
		ATTRIBUTE_GUI_1.setItem(37, ItemType.BURNING_TIME_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(38, ItemType.ENTITY_INTERACTION_RANGE_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(39, ItemType.EXPLOSION_KNOCKBACK_RESISTANCE_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(40, ItemType.FALL_DAMAGE_MULTIPLIER_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(41, ItemType.FLYING_SPEED_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(42, ItemType.FOLLOW_RANGE_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(43, ItemType.GRAVITY_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(44, ItemType.BLANK_SLOT.getItemStack());

		ATTRIBUTE_GUI_1.setItem(45, ItemType.BLANK_SLOT.getItemStack());
		ATTRIBUTE_GUI_1.setItem(46, ItemType.JUMP_STRENGTH_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(47, ItemType.KNOCKBACK_RESISTANCE_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(48, ItemType.LUCK_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(49, ItemType.MAX_ABSORPTION_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(50, ItemType.MAX_HEALTH_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(51, ItemType.MINING_EFFICIENCY_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(52, ItemType.MOVEMENT_EFFICIENCY_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_1.setItem(53, ItemType.NEXT_PAGE.getItemStack());

		// Page 2
		ATTRIBUTE_GUI_2.setItem(27, ItemType.BLANK_SLOT.getItemStack());
		ATTRIBUTE_GUI_2.setItem(28, ItemType.MOVEMENT_SPEED_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_2.setItem(29, ItemType.OXYGEN_BONUS_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_2.setItem(30, ItemType.SAFE_FALL_DISTANCE_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_2.setItem(31, ItemType.SCALE_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_2.setItem(32, ItemType.SNEAKING_SPEED_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_2.setItem(33, ItemType.SPAWN_REINFORCEMENTS_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_2.setItem(34, ItemType.STEP_HEIGHT_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_2.setItem(35, ItemType.BLANK_SLOT.getItemStack());

		ATTRIBUTE_GUI_2.setItem(36, ItemType.BLANK_SLOT.getItemStack());
		ATTRIBUTE_GUI_2.setItem(37, ItemType.SUBMERGED_MINING_SPEED_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_2.setItem(38, ItemType.SWEEPING_DAMAGE_RATIO_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_2.setItem(39, ItemType.TEMPT_RANGE_ATTRIBUTE.getItemStack());
		ATTRIBUTE_GUI_2.setItem(40, ItemType.WATER_MOVEMENT_EFFICIENCY_ATTRIBUTE.getItemStack());

		for (int index = 41; index < 53; index++)
		{
			if (index == 44 || index == 45) continue;
			ATTRIBUTE_GUI_2.setItem(index, ItemType.EMPTY_SLOT.getItemStack());
		}
		ATTRIBUTE_GUI_2.setItem(44, ItemType.BLANK_SLOT.getItemStack());
		ATTRIBUTE_GUI_2.setItem(45, ItemType.PREVIOUS_PAGE.getItemStack());
		ATTRIBUTE_GUI_2.setItem(53, ItemType.BLANK_SLOT.getItemStack());

		//LEVEL_GUI.setItem(9, ItemType.BASIC_ENCHANTMENT_LEVEL.getItemStack());
		// Slot 9 is either the level 1 enchantment level icon or the remove enchantment icon, depending on if the held item has the enchantment or not
		LEVEL_GUI.setItem(10, ItemType.BLANK_SLOT.getItemStack());
		LEVEL_GUI.setItem(11, ItemType.BASIC_LEVEL.getItemStack(5));
		LEVEL_GUI.setItem(12, ItemType.BLANK_SLOT.getItemStack());
		LEVEL_GUI.setItem(13, ItemType.BASIC_LEVEL.getItemStack(10));
		LEVEL_GUI.setItem(14, ItemType.BLANK_SLOT.getItemStack());
		LEVEL_GUI.setItem(15, ItemType.BASIC_LEVEL.getItemStack(15));
		LEVEL_GUI.setItem(16, ItemType.BLANK_SLOT.getItemStack());
		LEVEL_GUI.setItem(17, ItemType.CUSTOM_LEVEL.getItemStack());

		for (int slot = 18; slot < 22; slot++)
		{
			LEVEL_GUI.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
		}
		LEVEL_GUI.setItem(22, ItemType.BACK_BUTTON.getItemStack());
		for (int slot = 23; slot < 27; slot++)
		{
			LEVEL_GUI.setItem(slot, ItemType.BLANK_SLOT.getItemStack());
		}
	}

	static
	{
		registerItems();
	}
}
