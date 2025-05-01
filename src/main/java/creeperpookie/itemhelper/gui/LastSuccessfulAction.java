package creeperpookie.itemhelper.gui;

import creeperpookie.itemhelper.util.Pair;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;

public class LastSuccessfulAction
{
	private final ArrayList<Pair<Attribute, Double>> attributes;
	private final ArrayList<Pair<Enchantment, Integer>> enchantments;

	public LastSuccessfulAction()
	{
		attributes = new ArrayList<>();
		enchantments = new ArrayList<>();
	}

	public LastSuccessfulAction(ArrayList<Pair<Attribute, Double>> attributes, ArrayList<Pair<Enchantment, Integer>> enchantments)
	{
		this.attributes = attributes;
		this.enchantments = enchantments;
	}

	public ArrayList<Pair<Attribute, Double>> getAttributes()
	{
		return attributes;
	}

	public ArrayList<Pair<Enchantment, Integer>> getEnchantments()
	{
		return enchantments;
	}
}
