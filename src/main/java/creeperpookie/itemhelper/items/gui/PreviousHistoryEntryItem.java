package creeperpookie.itemhelper.items.gui;

import creeperpookie.itemhelper.gui.LastSuccessfulAction;
import creeperpookie.itemhelper.items.CustomItem;
import creeperpookie.itemhelper.items.ItemConstants;
import creeperpookie.itemhelper.items.ItemType;
import creeperpookie.itemhelper.util.DefaultTextColor;
import creeperpookie.itemhelper.util.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PreviousHistoryEntryItem implements CustomItem
{
	@Override
	@NotNull
	public ItemStack getItemStack(int data)
	{
		ItemStack item = new ItemStack(Material.BAMBOO_SIGN);
		item.editMeta(meta ->
		{
			meta.setCustomModelData(getModelData());
			meta.displayName(Component.text("Modifier", DefaultTextColor.BLUE).append(Component.text(data == 1 ? "" : "s", DefaultTextColor.BLUE)).appendSpace().append(Component.text("(", DefaultTextColor.BLUE)).append(Component.text(data, DefaultTextColor.BLUE)).appendSpace().append(Component.text(data != 1 ? "entries" : "entry", DefaultTextColor.BLUE)).append(Component.text(")")).append(Component.text(":", DefaultTextColor.BLUE)).decoration(TextDecoration.ITALIC, false));
			meta.lore(List.of());
		});
		item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
		item.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
		return item;
	}

	@Override
	public @NotNull ItemStack getItemStack()
	{
		return getItemStack(1);
	}

	@Override
	@NotNull
	public String getName()
	{
		return "previous_history_entry";
	}

	@Override
	public int getModelData()
	{
		return ItemConstants.PREVIOUS_HISTORY_ENTRY_MODEL_DATA;
	}

	@Override
	public boolean isItem(ItemStack item)
	{
		ItemStack currentItem = getItemStack();
		return item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == currentItem.getItemMeta().getCustomModelData() && item.getEnchantmentLevel(Enchantment.INFINITY) == 1 && item.getItemFlags().equals(currentItem.getItemFlags());
	}

	@Override
	public boolean isItemType(@NotNull ItemType type)
	{
		CustomItem inputtedCustomItem = CustomItem.getItem(type);
		return this.equals(inputtedCustomItem);
	}

	@Override
	public boolean equals(@NotNull CustomItem item)
	{
		return item instanceof PreviousHistoryEntryItem;
	}

	public ItemStack updateLore(LastSuccessfulAction action)
	{
		if (action.getAttributes().isEmpty() && action.getEnchantments().isEmpty()) return getItemStack(0);
		ItemStack baseItem = getItemStack(action.getAttributes().size() + action.getEnchantments().size());
		baseItem.editMeta(meta ->
		{
			ArrayList<Component> lore = new ArrayList<>();
			for (var attributeData : action.getAttributes()) lore.add(Component.text(Utility.formatText(attributeData.getLeft().getKey().getKey()), DefaultTextColor.LIGHT_PURPLE).appendSpace().append(Component.text("attribute", DefaultTextColor.AQUA)).append(Component.text(", level:", DefaultTextColor.GRAY)).appendSpace().append(Component.text(Utility.isInteger(attributeData.getRight()) ? String.valueOf((int) ((double) attributeData.getRight())) : String.valueOf(attributeData.getRight()), DefaultTextColor.LIGHT_PURPLE)).decoration(TextDecoration.ITALIC, false));
			for (var enchantmentData : action.getEnchantments()) lore.add(Component.text(Utility.formatText(enchantmentData.getLeft().getKey().getKey()), DefaultTextColor.LIGHT_PURPLE).appendSpace().append(Component.text("enchantment", DefaultTextColor.AQUA)).append(Component.text(", level:", DefaultTextColor.GRAY)).appendSpace().append(Component.text(enchantmentData.getRight(), DefaultTextColor.LIGHT_PURPLE)).decoration(TextDecoration.ITALIC, false));
			lore.add(Component.text("Small Text:", DefaultTextColor.AQUA).appendSpace().append(Component.text(action.isSmallText() ? "Enabled" : "Disabled", DefaultTextColor.BLUE)).decoration(TextDecoration.ITALIC, false));
			meta.lore(lore);
		});
		return baseItem;
	}
}
