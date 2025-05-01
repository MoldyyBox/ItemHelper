package creeperpookie.itemhelper.items.gui;

import creeperpookie.itemhelper.items.CustomItem;
import creeperpookie.itemhelper.items.ItemConstants;
import creeperpookie.itemhelper.items.ItemType;
import creeperpookie.itemhelper.util.DefaultTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SmallTextToggleItem implements CustomItem
{
	@Override
	@NotNull
	public ItemStack getItemStack(int data)
	{

		ItemStack item = new ItemStack(data == 0 ? Material.RED_CONCRETE : Material.GREEN_CONCRETE);
		item.editMeta(meta ->
		{
			meta.setCustomModelData(getModelData());
			meta.displayName(Component.text("Small Text [" + (data == 0 ? "OFF" : "ON") + "]", DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD));
			meta.lore(List.of(Component.text("").decoration(TextDecoration.ITALIC, false)));
		});
		item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
		item.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
		return item;
	}

	@Override
	@NotNull
	public ItemStack getItemStack()
	{
		return getItemStack(1);
	}

	@Override
	@NotNull
	public String getName()
	{
		return "small_text_toggle";
	}

	@Override
	public int getModelData()
	{
		return ItemConstants.SMALL_TEXT_TOGGLE_MODEL_DATA;
	}

	@Override
	public boolean isItem(ItemStack item)
	{
		ItemStack currentItem = getItemStack();
		return item != null && item.hasItemMeta() || item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == currentItem.getItemMeta().getCustomModelData() && item.getEnchantmentLevel(Enchantment.INFINITY) == 1 && item.getItemFlags().equals(currentItem.getItemFlags());
	}

	@Override
	public boolean isItemType(@NotNull ItemType type)
	{
		CustomItem inputtedCustomItem = CustomItem.getItem(type);
		return inputtedCustomItem != null && this.equals(inputtedCustomItem);
	}

	@Override
	public boolean equals(@NotNull CustomItem item)
	{
		return item instanceof SmallTextToggleItem;
	}
}
