package creeperpookie.enchanthelper.commands;

import creeperpookie.enchanthelper.handlers.ItemListener;
import creeperpookie.enchanthelper.util.DefaultTextColor;
import creeperpookie.enchanthelper.util.Utility;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EnchantHelperCommand implements CommandExecutor, TabCompleter
{
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args)
	{
		if (!(sender instanceof Player player))
		{
			sender.sendMessage("This command can only be run by a player!");
			return true;
		}
		else if (!player.isOp() && !player.hasPermission("enchanthelper.use"))
		{
			sender.sendMessage(Component.text("You do not have permission to use this command!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
			return true;
		}
		else if (player.getInventory().getItemInMainHand().isEmpty())
		{
			player.sendActionBar(Component.text("You must be holding an item to use this command!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
			return true;
		}
		ItemStack heldItem = player.getInventory().getItemInMainHand();
		switch (args.length)
		{
			case 0 ->
			{
				if (ItemListener.isPlayerInGUI(player)) player.sendActionBar(Component.text("You are already in the enchantment GUI!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
				else ItemListener.openGUI(player);
				return true;
			}
			case 1 ->
			{
				switch (args[0]) // Single argument switch for future subcommands
				{
					case "reset" ->
					{
						if (!ItemListener.isPlayerInGUI(player)) player.sendActionBar(Component.text("You aren't in the enchantment GUI!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
						else
						{
							ItemListener.clearPlayerGUIData(player);
							player.sendActionBar(Component.text("Successfully reset the enchantment GUI!", DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
						}
					}
				}
				return true;
			}
			case 2 ->
			{
				Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(new NamespacedKey("minecraft", args[0]));
				if (enchantment == null)
				{
					player.sendActionBar(Component.text("The inputted enchantment type is not valid!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
					return true;
				}
				int level;
				try
				{
					level = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException e)
				{
					player.sendActionBar(Component.text("The inputted enchantment level is not a valid number!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
					return true;
				}
				if (level < 0 || level > 255) // Level 0 is a special case, meaning remove enchantment; it should be a separate subcommand anyway though
				{
					player.sendActionBar(Component.text("The inputted enchantment level is out of range!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
					return true;
				}
				heldItem.addUnsafeEnchantment(enchantment, level);
				player.sendActionBar(Component.text("Successfully applied enchantment to", DefaultTextColor.GREEN).appendSpace().append(heldItem.displayName().color(DefaultTextColor.LIGHT_PURPLE)).decoration(TextDecoration.ITALIC, false));
			}
		}
		return true;
	}

	@Override
	@Nullable
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args)
	{
		if (!sender.isOp() && !sender.hasPermission("enchanthelper.use")) return List.of();
		switch (args.length)
		{
			case 1 ->
			{
				ArrayList<String> commands = new ArrayList<>(Utility.getEnchantmentsAsString());
				commands.add("reset");
				return commands;
			}
			case 2 ->
			{
				return Stream.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9").map(s -> args[1] + s).toList();
			}
		}
		return List.of();
	}

	private void printHelp(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
	{
		if (args.length == 1) sender.sendMessage(Component.text("You must specify an enchantment level!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
	}
}
