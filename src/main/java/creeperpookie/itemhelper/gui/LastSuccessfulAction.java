package creeperpookie.itemhelper.gui;

import creeperpookie.itemhelper.util.Pair;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LastSuccessfulAction
{
	private final ArrayList<Pair<Attribute, Double>> attributes;
	private final ArrayList<Pair<Enchantment, Integer>> enchantments;
	private boolean smallText;

	public LastSuccessfulAction()
	{
		attributes = new ArrayList<>();
		enchantments = new ArrayList<>();
		smallText = true;
	}

	public LastSuccessfulAction(ArrayList<Pair<Attribute, Double>> attributes, ArrayList<Pair<Enchantment, Integer>> enchantments, boolean smallText)
	{
		this.attributes = attributes;
		this.enchantments = enchantments;
		this.smallText = smallText;
	}

	public static LastSuccessfulAction getFromItem(@NotNull ItemStack item, boolean smallText)
	{
		LastSuccessfulAction lastSuccessfulAction = new LastSuccessfulAction();
		if (item.hasItemMeta() && item.getItemMeta().hasAttributeModifiers()) item.getItemMeta().getAttributeModifiers().entries().forEach(attributeModifier -> lastSuccessfulAction.addAttribute(attributeModifier.getKey(), attributeModifier.getValue().getAmount()));
		if (!item.getEnchantments().isEmpty()) item.getEnchantments().forEach(lastSuccessfulAction::addEnchantment);
		lastSuccessfulAction.setSmallText(smallText);
		return lastSuccessfulAction;
	}

	public ArrayList<Pair<Attribute, Double>> getAttributes()
	{
		return attributes;
	}

	public void addAttribute(@NotNull Attribute attribute, double amount)
	{
		Pair<Attribute, Double> attributePair = new Pair<>(attribute, amount);
		if (!attributes.contains(attributePair)) attributes.add(attributePair);
	}

	public void removeAttribute(@NotNull Attribute attribute)
	{
		List<Pair<Attribute, Double>> removedAttributes = attributes.stream().filter(attributeDoublePair -> attributeDoublePair.equalsLeft(attribute)).toList();
		while (!removedAttributes.isEmpty()) attributes.remove(removedAttributes.removeFirst());
	}

	public void removeAttribute(@NotNull Attribute attribute, double amount)
	{
		attributes.remove(new Pair<>(attribute, amount));
	}

	@NotNull
	public ArrayList<Pair<Enchantment, Integer>> getEnchantments()
	{
		return enchantments;
	}

	public void addEnchantment(@NotNull Enchantment enchantment, int amount)
	{
		Pair<Enchantment, Integer> enchantmentPair = new Pair<>(enchantment, amount);
		if (!enchantments.contains(enchantmentPair)) enchantments.add(enchantmentPair);
	}

	public void removeEnchantment(@NotNull Enchantment enchantment)
	{
		List<Pair<Enchantment, Integer>> removedEnchantments = enchantments.stream().filter(enchantmentIntegerPair -> enchantmentIntegerPair.equalsLeft(enchantment)).toList();
		while (!removedEnchantments.isEmpty()) enchantments.remove(removedEnchantments.removeFirst());
	}

	public boolean isSmallText()
	{
		return smallText;
	}

	public void setSmallText(boolean useSmallText)
	{
		this.smallText = useSmallText;
	}
}
