package creeperpookie.itemhelper.items.gui.enchantments.curses;

import creeperpookie.itemhelper.items.CustomItem;
import creeperpookie.itemhelper.items.ItemConstants;
import creeperpookie.itemhelper.util.DefaultTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CurseOfBindingItem implements CustomItem
{
	@Override
	@NotNull
	public ItemStack getItemStack()
	{
		ItemStack item = new ItemStack(Material.CHAIN);
		item.editMeta(Damageable.class, meta ->
		{
			meta.setCustomModelData(getModelData());
			meta.displayName(Component.text("Curse of Binding", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD));
			meta.lore(List.of());
		});
		item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
		item.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
		return item;
	}

	@Override
	@NotNull
	public String getName()
	{
		return "curse_of_binding";
	}

	@Override
	public int getModelData()
	{
		return ItemConstants.CURSE_OF_BINDING_MODEL_DATA;
	}

	@Override
	public String getLinkedEnchantmentName()
	{
		return "binding_curse";
	}
}
