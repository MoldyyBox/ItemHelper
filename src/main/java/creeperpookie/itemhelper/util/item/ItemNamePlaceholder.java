package creeperpookie.itemhelper.util.item;

import creeperpookie.itemhelper.gui.PlayerGUIData;
import creeperpookie.itemhelper.handlers.ItemListener;
import creeperpookie.itemhelper.util.Pair;
import creeperpookie.itemhelper.util.Utility;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ItemNamePlaceholder
{
	ITEM_NAME(ItemStack.class),
	ENCHANTMENT(Enchantment.class),
	ATTRIBUTE(Attribute.class),
	LEVEL(Integer.class),
	ITEM_COUNT(Integer.class),
	SMALL_TEXT(Boolean.class);

	private final Class<?> dataClass;

	ItemNamePlaceholder(Class<?> dataClass)
	{
		this.dataClass = dataClass;
	}

	public Class<?> getDataClass()
	{
		return dataClass;
	}

	/**
	 * Formats the input string by replacing the placeholder with the provided data.
	 *
	 * @param input The input string containing the placeholder
	 * @param data The data to replace the placeholder with.
	 * @return The formatted string with the placeholder replaced by the data
	 * @throws IllegalArgumentException if the data type does not match the expected type for this placeholder.
	 */
	public String format(@NotNull String input, @NotNull Object data)
	{
		if (input.isEmpty()) return input;
		else if (this != SMALL_TEXT && data.getClass() != dataClass) throw new IllegalArgumentException("Data type mismatch: expected " + dataClass.getSimpleName() + ", got " + data.getClass().getSimpleName());
		String result = input;
		String normalRegex = String.format("(?i)(?<!\\\\)\\$\\{%1$s}|(?<!\\\\)\\$\\(%1$s\\)", name().toLowerCase()); // Matches $(name) or ${name}, but not \$(name) or \${name}
		String escapedBraceRegex = String.format("(?i)\\\\\\$\\{%1$s}", name().toLowerCase()); // Matches only \${name}
		String escapedParenthesisRegex = String.format("(?i)\\\\\\$\\(%1$s\\)", name().toLowerCase()); // Matches only \$(name)
		if (this == SMALL_TEXT) return (boolean) data ? Utility.getSmallCapsString(result.replaceAll(normalRegex, ""), true) : result;
		result = result.replaceAll(normalRegex, Utility.formatText(switch (this)
		{
			case ITEM_NAME -> ((ItemStack) data).getType().name();
			case ENCHANTMENT -> ((Enchantment) data).getKey().getKey();
			case ATTRIBUTE -> ((Attribute) data).getKey().getKey();
			case LEVEL, ITEM_COUNT -> String.valueOf(data);
			default -> throw new IllegalStateException("Unexpected value: " + this);
		}));
		return result.replaceAll(escapedBraceRegex, "${" + name().toLowerCase() + "}").replaceAll(escapedParenthesisRegex, "$(" + name().toLowerCase() + ")");
	}

	/**
	 * Formats the input string by replacing all placeholders with their respective data, retrieving the replacement data from the player's GUI data..
	 *
	 * @param input The input string containing the placeholders
	 * @return The formatted string with all placeholders replaced by their respective data from the player's GUI data.
	 */
	public static String formatAll(@NotNull String input, @NotNull PlayerGUIData data)
	{
		if (input.isEmpty()) return input;
		ArrayList<Pair<ItemNamePlaceholder, Object>> args = new ArrayList<>();
		ArrayList<ItemStack> items = data.getCurrentItems();
		Enchantment enchantment = data.getEnchantment();
		Attribute attribute = data.getAttribute();
		if (!items.isEmpty() && items.stream().allMatch(item -> items.getFirst().getType().equals(item.getType()))) args.add(new Pair<>(ItemNamePlaceholder.ITEM_NAME, items.getFirst()));
		if (enchantment != null) args.add(new Pair<>(ItemNamePlaceholder.ENCHANTMENT, Utility.formatText(enchantment.getKey().getKey())));
		if (attribute != null) args.add(new Pair<>(ItemNamePlaceholder.ATTRIBUTE, Utility.formatText(attribute.getKey().getKey())));
		args.add(new Pair<>(ItemNamePlaceholder.ITEM_COUNT, items.size()));
		args.add(new Pair<>(ItemNamePlaceholder.SMALL_TEXT, data.isSmallText()));
		return formatAll(input, args);
	}

	public static String formatAll(@NotNull String input, @NotNull List<Pair<@NotNull ItemNamePlaceholder, @NotNull Object>> data)
	{
		HashMap<ItemNamePlaceholder, Integer> placeholderCount = new HashMap<>();
		HashMap<ItemNamePlaceholder, Object> placeholderData = new HashMap<>();
		String formatted = input;
		if (data.isEmpty()) return formatted;
		for (var pair : data)
		{
			ItemNamePlaceholder placeholder = pair.getLeft();
			Object placeholderValue = pair.getRight();
			placeholderCount.put(placeholder, placeholderCount.getOrDefault(placeholder, 0) + 1);
			if (placeholderCount.get(placeholder) > 1) throw new IllegalArgumentException("Duplicate placeholder: " + placeholder.name().toLowerCase() + " (" + placeholderCount.get(placeholder) + " occurrences)");
			else
			{
				if (placeholder.getDataClass() != placeholderValue.getClass()) throw new IllegalArgumentException("Data type mismatch for placeholder " + placeholder.name().toLowerCase() + ": expected " + placeholder.getDataClass().getSimpleName() + ", got " + placeholderValue.getClass().getSimpleName());
				else placeholderData.put(placeholder, placeholderValue);
			}
		}
		for (ItemNamePlaceholder placeholder : ItemNamePlaceholder.values())
		{
			if (placeholder == SMALL_TEXT) continue; // SMALL_TEXT must be formatted last, as it will replace all other letter-like ASCII characters
			if (placeholderCount.containsKey(placeholder)) formatted = placeholder.format(formatted, placeholderData.get(placeholder));
		}
		if (placeholderCount.containsKey(SMALL_TEXT)) SMALL_TEXT.format(formatted, placeholderData.get(SMALL_TEXT));
		return formatted;
	}
}
