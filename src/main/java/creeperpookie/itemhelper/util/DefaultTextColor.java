package creeperpookie.itemhelper.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Random;

public class DefaultTextColor implements TextColor
{
	private final int value;

	public static final DefaultTextColor BLACK = new DefaultTextColor(0);
	public static final DefaultTextColor DARK_BLUE = new DefaultTextColor(170);
	public static final DefaultTextColor DARK_GREEN = new DefaultTextColor(43520);
	public static final DefaultTextColor DARK_AQUA = new DefaultTextColor(43690);
	public static final DefaultTextColor DARK_RED = new DefaultTextColor(11141120);
	public static final DefaultTextColor DARK_PURPLE = new DefaultTextColor(11141290);
	public static final DefaultTextColor GOLD = new DefaultTextColor(16755200);
	public static final DefaultTextColor GRAY = new DefaultTextColor(11184810);
	public static final DefaultTextColor DARK_GRAY = new DefaultTextColor(5592405);
	public static final DefaultTextColor BLUE = new DefaultTextColor(5592575);
	public static final DefaultTextColor GREEN = new DefaultTextColor(5635925);
	public static final DefaultTextColor AQUA = new DefaultTextColor(5636095);
	public static final DefaultTextColor RED = new DefaultTextColor(16733525);
	public static final DefaultTextColor LIGHT_PURPLE = new DefaultTextColor(16733695);
	public static final DefaultTextColor YELLOW = new DefaultTextColor(16777045);
	public static final DefaultTextColor WHITE = new DefaultTextColor(16777215);

	private DefaultTextColor(int value)
	{
		this.value = value;
	}

	@Override
	public int value()
	{
		return value;
	}

	public static TextColor getRandom(Random random)
	{
		return TextColor.color(random.nextInt(WHITE.value() + 1));
	}

	public static Component gradient(String message, int fromColor, int toColor)
	{
		Component component = Component.empty();
		for (int i = 0; i < message.length(); i++)
		{
			component = component.append(Component.text(message.charAt(i)).color(TextColor.color(getCharColor(message.length(), i, fromColor, toColor))));
		}
		return component;
	}

	private static int getCharColor(int length, int index, int fromColor, int toColor)
	{
		int redDifference = ((toColor >> 16) & 0xFF) - ((fromColor >> 16) & 0xFF);
		int greenDifference = ((toColor >> 8) & 0xFF) - ((fromColor >> 8) & 0xFF);
		int blueDifference = (toColor & 0xFF) - (fromColor & 0xFF);

		int redStep = redDifference / (length - 1);
		int greenStep = greenDifference / (length - 1);
		int blueStep = blueDifference / (length - 1);

		int red = ((fromColor >> 16) & 0xFF) + (redStep * index);
		int green = ((fromColor >> 8) & 0xFF) + (greenStep * index);
		int blue = (fromColor & 0xFF) + (blueStep * index);

		return (red << 16) | (green << 8) | blue;
	}

	@Override
	public String toString()
	{
		if (NamedTextColor.namedColor(value) != null) return "§" + getColorChar();
		else
		{
			StringBuilder hex = new StringBuilder(Integer.toHexString(value).toLowerCase());
			if (hex.length() < 6)
			{
				int missing = 6 - hex.length();
				hex.insert(0, "0".repeat(missing));
			}
			hex.insert(0, "§#");
			return hex.toString();
		}
	}

	private char getColorChar()
	{
		return switch (this.value)
		{
			case 0 -> '0';
			case 170 -> '1';
			case 43520 -> '2';
			case 43690 -> '3';
			case 11141120 -> '4';
			case 11141290 -> '5';
			case 16755200 -> '6';
			case 11184810 -> '7';
			case 5592405 -> '8';
			case 5592575 -> '9';
			case 5635925 -> 'a';
			case 5636095 -> 'b';
			case 16733525 -> 'c';
			case 16733695 -> 'd';
			case 16777045 -> 'e';
			case 16777215 -> 'f';
			default -> '_';
		};
	}
}
