package creeperpookie.itemhelper;

import creeperpookie.itemhelper.commands.AttributeHelperCommand;
import creeperpookie.itemhelper.commands.EnchantHelperCommand;
import creeperpookie.itemhelper.commands.ItemHelperCommonCommand;
import creeperpookie.itemhelper.handlers.ItemListener;
import creeperpookie.itemhelper.items.CustomItem;
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
		CustomItem.registerAll();
		Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
		getCommand("itemhelper").setExecutor(new ItemHelperCommonCommand());
		getCommand("enchanthelper").setExecutor(new EnchantHelperCommand());
		getCommand("attributehelper").setExecutor(new AttributeHelperCommand());
	}

	@Override
	public void onDisable()
	{
		// Plugin shutdown logic
		getCommand("itemhelper").unregister(Bukkit.getCommandMap());
		getCommand("enchanthelper").unregister(Bukkit.getCommandMap());
		getCommand("attributehelper").unregister(Bukkit.getCommandMap());
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
