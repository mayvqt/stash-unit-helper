package com.stashunithelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.Value;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.cluescrolls.clues.emote.STASHUnit;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;

/** Decides which STASH units the player can build or fill now, and what to carry there. */
final class StashPlanner
{
	static final int PLANKS = 2;
	static final int NAILS = 10;
	static final Set<Integer> NAIL_IDS = Set.of(ItemID.NAILS_BRONZE, ItemID.NAILS_IRON, ItemID.NAILS,
		ItemID.NAILS_BLACK, ItemID.NAILS_MITHRIL, ItemID.NAILS_ADAMANT, ItemID.NAILS_RUNE);
	static final Set<Integer> HAMMERS = Set.of(ItemID.HAMMER, ItemID.IMCANDO_HAMMER, ItemID.IMCANDO_HAMMER_OFFHAND);
	static final Set<Integer> SAWS = Set.of(ItemID.POH_SAW, ItemID.EYEGLO_CRYSTAL_SAW, ItemID.WEARABLE_SAW,
		ItemID.WEARABLE_SAW_OFFHAND);

	/** A unit to visit and what the trip is for. */
	@Value
	static class Target
	{
		StashUnitInfo info;
		boolean build;
		boolean fill;
		/** Whether the player has the level and owns everything the trip needs. */
		boolean ready;
	}

	/** Planning inputs that come from the player and the config. */
	@Value
	static class Options
	{
		int constructionLevel;
		boolean build;
		boolean fill;
		StashTier tier;
		Set<STASHUnit> skipped;
	}

	private StashPlanner()
	{
	}

	/** Unfinished units of the chosen tier; with readyOnly, only those that can be done right now. */
	static List<Target> targets(List<StashUnitInfo> units, Function<STASHUnit, StashState> states, OwnedItems owned,
		Options options, boolean readyOnly)
	{
		List<Target> targets = new ArrayList<>();
		for (StashUnitInfo info : units)
		{
			StashState state = states.apply(info.getUnit());
			if (state == null || state == StashState.FILLED || options.getSkipped().contains(info.getUnit())
				|| info.getTier() != options.getTier()
				|| (state == StashState.NOT_BUILT ? !options.isBuild() : !options.isFill()))
			{
				continue;
			}
			Target target = target(info, state, owned, options);
			if (!readyOnly || target.isReady())
			{
				targets.add(target);
			}
		}
		return targets;
	}

	/**
	 * The trip to a unit: build it if needed, and fill it on the same visit when the items are owned.
	 * A built unit is always a fill trip, even before the items are owned, so the player sees what's missing.
	 */
	static Target target(StashUnitInfo info, StashState state, OwnedItems owned, Options options)
	{
		StashTier tier = info.getTier();
		boolean build = state == StashState.NOT_BUILT;
		boolean hasFillItems = hasFillItems(info, owned);
		boolean fill = options.isFill() && (state == StashState.BUILT || (build && hasFillItems));
		boolean ready = (!build || (options.getConstructionLevel() >= tier.getConstructionLevel() && hasBuildMaterials(tier, owned)))
			&& (!fill || hasFillItems);
		return new Target(info, build, fill, ready);
	}

	/** Whether the item is used to build a unit of this tier. */
	static boolean isBuildMaterial(StashTier tier, int itemId)
	{
		return itemId == tier.getPlankId() || NAIL_IDS.contains(itemId) || HAMMERS.contains(itemId)
			|| SAWS.contains(itemId) || (tier.isGoldLeaf() && itemId == ItemID.GOLD_LEAF);
	}

	static boolean hasBuildMaterials(StashTier tier, OwnedItems owned)
	{
		return owned.count(tier.getPlankId()) >= PLANKS
			&& owned.locate(NAIL_IDS, NAILS) != OwnedItems.Where.MISSING
			&& owned.locate(HAMMERS, 1) != OwnedItems.Where.MISSING
			&& owned.locate(SAWS, 1) != OwnedItems.Where.MISSING
			&& (!tier.isGoldLeaf() || owned.count(ItemID.GOLD_LEAF) >= 1);
	}

	static boolean hasFillItems(StashUnitInfo info, OwnedItems owned)
	{
		if (info.getRequirements() == null)
		{
			return false;
		}
		for (ItemRequirement requirement : info.getRequirements())
		{
			if (owned.locate(requirement) == OwnedItems.Where.MISSING)
			{
				return false;
			}
		}
		return true;
	}
}
