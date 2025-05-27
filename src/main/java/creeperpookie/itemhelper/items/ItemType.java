package creeperpookie.itemhelper.items;

import creeperpookie.itemhelper.items.gui.*;
import creeperpookie.itemhelper.items.gui.attributes.*;
import creeperpookie.itemhelper.items.gui.enchantments.*;
import creeperpookie.itemhelper.items.gui.enchantments.curses.CurseOfBindingItem;
import creeperpookie.itemhelper.items.gui.enchantments.curses.CurseOfVanishingItem;
import creeperpookie.itemhelper.items.gui.levels.BasicEnchantmentLevelItem;
import creeperpookie.itemhelper.items.gui.levels.CustomEnchantmentLevelItem;
import creeperpookie.itemhelper.items.gui.levels.RemoveValueItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ItemType
{
	// GUI buttons
	EMPTY_SLOT(ReplacementSlotItem.class),
	BLANK_SLOT(BlankSlotItem.class),
	PREVIOUS_HISTORY(PreviousHistoryItem.class),
	PREVIOUS_HISTORY_ENTRY(PreviousHistoryEntryItem.class),
	CURRENT_ITEMS(CurrentItemsItem.class),
	BUNDLE_ITEMS(BundleItemsItem.class),
	BACK_BUTTON(BackButtonItem.class),
	NEXT_PAGE(NextPageItem.class),
	PREVIOUS_PAGE(PreviousPageItem.class),
	SMALL_TEXT_TOGGLE(SmallTextToggleItem.class),

	// Enchantment levels
	BASIC_LEVEL(BasicEnchantmentLevelItem.class),
	CUSTOM_LEVEL(CustomEnchantmentLevelItem.class),
	REMOVE_VALUE(RemoveValueItem.class),

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
	WIND_BURST(WindBurstItem.class),

	// Attributes
	ARMOR_ATTRIBUTE(ArmorAttributeItem.class),
	ARMOR_TOUGHNESS_ATTRIBUTE(ArmorToughnessAttributeItem.class),
	ATTACK_DAMAGE_ATTRIBUTE(AttackDamageAttributeItem.class),
	ATTACK_KNOCKBACK_ATTRIBUTE(AttackKnockbackAttributeItem.class),
	ATTACK_SPEED_ATTRIBUTE(AttackSpeedAttributeItem.class),
	BLOCK_BREAK_SPEED_ATTRIBUTE(BlockBreakSpeedAttributeItem.class),
	BLOCK_INTERACTION_RANGE_ATTRIBUTE(BlockInteractionRangeAttributeItem.class),
	BURNING_TIME_ATTRIBUTE(BurningTimeAttributeItem.class),
	ENTITY_INTERACTION_RANGE_ATTRIBUTE(EntityInteractionRangeAttributeItem.class),
	EXPLOSION_KNOCKBACK_RESISTANCE_ATTRIBUTE(ExplosionKnockbackResistanceAttributeItem.class),
	FALL_DAMAGE_MULTIPLIER_ATTRIBUTE(FallDamageMultiplierAttributeItem.class),
	FLYING_SPEED_ATTRIBUTE(FlyingSpeedAttributeItem.class),
	FOLLOW_RANGE_ATTRIBUTE(FollowRangeAttributeItem.class),
	GRAVITY_ATTRIBUTE(GravityAttributeItem.class),
	JUMP_STRENGTH_ATTRIBUTE(JumpStrengthAttributeItem.class),
	KNOCKBACK_RESISTANCE_ATTRIBUTE(KnockbackResistanceAttributeItem.class),
	LUCK_ATTRIBUTE(LuckAttributeItem.class),
	MAX_ABSORPTION_ATTRIBUTE(MaxAbsorptionAttributeItem.class),
	MAX_HEALTH_ATTRIBUTE(MaxHealthAttributeItem.class),
	MINING_EFFICIENCY_ATTRIBUTE(MiningEfficiencyAttributeItem.class),
	MOVEMENT_EFFICIENCY_ATTRIBUTE(MovementEfficiencyAttributeItem.class),
	MOVEMENT_SPEED_ATTRIBUTE(MovementSpeedAttributeItem.class),
	OXYGEN_BONUS_ATTRIBUTE(OxygenBonusAttributeItem.class),
	SAFE_FALL_DISTANCE_ATTRIBUTE(SafeFallDistanceAttributeItem.class),
	SCALE_ATTRIBUTE(ScaleAttributeItem.class),
	SNEAKING_SPEED_ATTRIBUTE(SneakingSpeedAttributeItem.class),
	SPAWN_REINFORCEMENTS_ATTRIBUTE(SpawnReinforcementsAttributeItem.class),
	STEP_HEIGHT_ATTRIBUTE(StepHeightAttributeItem.class),
	SUBMERGED_MINING_SPEED_ATTRIBUTE(SubmergedMiningSpeedAttributeItem.class),
	SWEEPING_DAMAGE_RATIO_ATTRIBUTE(SweepingDamageRatioAttributeItem.class),
	TEMPT_RANGE_ATTRIBUTE(TemptRangeAttributeItem.class),
	WATER_MOVEMENT_EFFICIENCY_ATTRIBUTE(WaterMovementEfficiencyAttributeItem.class);

	private final Class<? extends CustomItem> itemClass;

	ItemType(Class<? extends CustomItem> itemClass)
	{
		this.itemClass = itemClass;
	}

	public Class<? extends CustomItem> getItemClass()
	{
		return itemClass;
	}

	public CustomItem getCustomItem()
	{
		return CustomItem.getItem(this);
	}

	public boolean isItemStack(@Nullable ItemStack item)
	{
		return item != null && getCustomItem().isItem(item);
	}

	@NotNull
	public ItemStack getItemStack()
	{
		return getCustomItem().getItemStack();
	}

	@NotNull
	public ItemStack getItemStack(int data)
	{
		return getCustomItem().getItemStack(data);
	}
}
