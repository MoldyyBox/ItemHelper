package creeperpookie.itemhelper.items.gui.enchantments;

import creeperpookie.itemhelper.items.CustomItem;
import creeperpookie.itemhelper.items.ItemConstants;
import creeperpookie.itemhelper.util.DefaultTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfinityItem implements CustomItem
{
	@Override
	@NotNull
	public ItemStack getItemStack()
	{
		ItemStack item = new ItemStack(Material.ARROW);
		item.editMeta(meta ->
		{
			meta.setCustomModelData(getModelData());
			meta.displayName(Component.text("Infinity", DefaultTextColor.AQUA).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD));
			meta.lore(List.of());
		});
		item.addUnsafeEnchantment(Enchantment.INFINITY, 1); // yay matching enchantment :D
		item.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
		return item;
	}

	@Override
	@NotNull
	public String getName()
	{
		return "infinity";
	}

	@Override
	public int getModelData()
	{
		return ItemConstants.INFINITY_MODEL_DATA;
	}
}
