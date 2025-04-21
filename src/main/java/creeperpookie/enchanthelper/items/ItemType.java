package creeperpookie.enchanthelper.items;

import creeperpookie.enchanthelper.items.gui.*;
import creeperpookie.enchanthelper.items.gui.enchantments.*;
import creeperpookie.enchanthelper.items.gui.enchantments.curses.CurseOfBindingItem;
import creeperpookie.enchanthelper.items.gui.enchantments.curses.CurseOfVanishingItem;
import creeperpookie.enchanthelper.items.gui.enchantments.levels.BasicEnchantmentLevelItem;
import creeperpookie.enchanthelper.items.gui.enchantments.levels.CustomEnchantmentLevelItem;
import creeperpookie.enchanthelper.items.gui.enchantments.levels.RemoveEnchantmentItem;

public enum ItemType
{
	// GUI buttons
	BLANK_SLOT(BlankSlotItem.class),
	BACK_BUTTON(BackButtonItem.class),
	NEXT_PAGE(NextPageItem.class),
	PREVIOUS_PAGE(PreviousPageItem.class),
	SMALL_TEXT_TOGGLE(SmallTextToggleItem.class),

	// Enchantment levels
	BASIC_ENCHANTMENT_LEVEL(BasicEnchantmentLevelItem.class),
	CUSTOM_ENCHANTMENT_LEVEL(CustomEnchantmentLevelItem.class),
	REMOVE_ENCHANTMENT(RemoveEnchantmentItem.class),

	// Enchantments
	AQUA_AFFINITY(AquaAffinityItem.class),
	BANE_OF_ARTHROPODS(BaneOfArthropodsItem.class),
	BLAST_PROTECTION(BlastProtectionItem.class),
	BREACH(BreachItem.class),
	CHANNELING(ChannelingItem.class),
	CURSE_OF_BINDING(CurseOfBindingItem.class),
	CURSE_OF_VANISHING(CurseOfVanishingItem.class),
	DENSITY(DensityItem.class),
	DEPTH_STRIDER(DepthStriderItem.class),
	EFFICIENCY(EfficiencyItem.class),
	FEATHER_FALLING(FeatherFallingItem.class),
	FIRE_ASPECT(FireAspectItem.class),
	FIRE_PROTECTION(FireProtectionItem.class),
	FLAME(FlameItem.class),
	FORTUNE(FortuneItem.class),
	FROST_WALKER(FrostWalkerItem.class),
	IMPALING(ImpalingItem.class),
	INFINITY(InfinityItem.class),
	KNOCKBACK(KnockbackItem.class),
	LOOTING(LootingItem.class),
	LOYALTY(LoyaltyItem.class),
	LUCK_OF_THE_SEA(LuckOfTheSeaItem.class),
	LURE(LureItem.class),
	MENDING(MendingItem.class),
	MULTISHOT(MultishotItem.class),
	PIERCING(PiercingItem.class),
	POWER(PowerItem.class),
	PROJECTILE_PROTECTION(ProjectileProtectionItem.class),
	PROTECTION(ProtectionItem.class),
	PUNCH(PunchItem.class),
	QUICK_CHARGE(QuickChargeItem.class),
	RESPIRATION(RespirationItem.class),
	RIPTIDE(RiptideItem.class),
	SHARPNESS(SharpnessItem.class),
	SILK_TOUCH(SilkTouchItem.class),
	SMITE(SmiteItem.class),
	SOUL_SPEED(SoulSpeedItem.class),
	SWEEPING_EDGE(SweepingEdgeItem.class),
	SWIFT_SNEAK(SwiftSneakItem.class),
	THORNS(ThornsItem.class),
	UNBREAKING(UnbreakingItem.class),
	WIND_BURST(WindBurstItem.class);

	private final Class<? extends CustomItem> itemClass;

	ItemType(Class<? extends CustomItem> itemClass)
	{
		this.itemClass = itemClass;
	}

	public Class<? extends CustomItem> getItemClass()
	{
		return itemClass;
	}
}
