package creeperpookie.enchanthelper;

import creeperpookie.enchanthelper.commands.EnchantHelperCommand;
import creeperpookie.enchanthelper.handlers.ItemListener;
import creeperpookie.enchanthelper.items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class ItemHelperPlugin extends JavaPlugin
{
	// The instance of the plugin
	private static ItemHelperPlugin instance;
	private static final Random RANDOM = new Random();

	@Override
	public void onEnable()
	{
		instance = this;
		getLogger().info("Enabling EnchantHelper");
		CustomItem.registerAll();
		Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
		getCommand("enchanthelper").setExecutor(new EnchantHelperCommand());
	}

	@Override
	public void onDisable()
	{
		// Plugin shutdown logic
		getLogger().info("Disabling EnchantHelper");
		getCommand("enchanthelper").unregister(Bukkit.getCommandMap());
	}

	public static ItemHelperPlugin getInstance()
	{
		return instance;
	}

	public static Random getRandom()
	{
		return RANDOM;
	}
}
