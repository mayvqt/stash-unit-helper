package com.stashunithelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.cluescrolls.clues.emote.STASHUnit;
import org.junit.Test;

public class StashPlannerTest
{
	private static final STASHUnit GYPSY = STASHUnit.GYPSY_TENT_ENTRANCE; // Beginner: gold ring + gold necklace
	private static final List<StashUnitInfo> UNITS = List.of(StashUnitData.get(GYPSY));
	private static final OwnedItems NOTHING = new OwnedItems(Map.of(), Map.of());

	private static Map<Integer, Integer> buildKit()
	{
		Map<Integer, Integer> items = new HashMap<>();
		items.put(ItemID.WOODPLANK, 2);
		items.put(ItemID.NAILS_IRON, 10);
		items.put(ItemID.HAMMER, 1);
		items.put(ItemID.POH_SAW, 1);
		return items;
	}

	private static StashPlanner.Options options(int level, Set<STASHUnit> skipped)
	{
		return new StashPlanner.Options(level, true, true, tier -> true, skipped);
	}

	private static List<StashPlanner.Target> ready(StashState state, OwnedItems owned, int level)
	{
		return StashPlanner.targets(UNITS, Map.of(GYPSY, state)::get, owned, options(level, EnumSet.noneOf(STASHUnit.class)), true);
	}

	@Test
	public void buildNeedsLevelAndMaterials()
	{
		OwnedItems kit = new OwnedItems(buildKit(), Map.of());
		assertTrue(ready(StashState.NOT_BUILT, kit, 11).isEmpty());
		assertEquals(1, ready(StashState.NOT_BUILT, kit, 12).size());

		Map<Integer, Integer> mixedNails = buildKit();
		mixedNails.put(ItemID.NAILS_IRON, 5);
		mixedNails.put(ItemID.NAILS_BRONZE, 5);
		assertTrue("ten nails must be one metal", ready(StashState.NOT_BUILT, new OwnedItems(mixedNails, Map.of()), 99).isEmpty());
	}

	@Test
	public void bankedMaterialsCount()
	{
		assertEquals(1, ready(StashState.NOT_BUILT, new OwnedItems(Map.of(), buildKit()), 99).size());
	}

	@Test
	public void fillNeedsEveryItem()
	{
		Map<Integer, Integer> ring = Map.of(ItemID.GOLD_RING, 1);
		assertTrue(ready(StashState.BUILT, new OwnedItems(ring, Map.of()), 99).isEmpty());

		List<StashPlanner.Target> targets = ready(StashState.BUILT, new OwnedItems(ring, Map.of(ItemID.GOLD_NECKLACE, 1)), 99);
		assertEquals(1, targets.size());
		assertFalse(targets.get(0).isBuild());
		assertTrue(targets.get(0).isFill());
	}

	@Test
	public void buildTripAlsoFillsWhenItemsOwned()
	{
		Map<Integer, Integer> items = buildKit();
		items.put(ItemID.GOLD_RING, 1);
		items.put(ItemID.GOLD_NECKLACE, 1);
		StashPlanner.Target target = ready(StashState.NOT_BUILT, new OwnedItems(items, Map.of()), 99).get(0);
		assertTrue(target.isBuild());
		assertTrue(target.isFill());
	}

	@Test
	public void unfinishedIncludesUnitsThatAreNotReady()
	{
		List<StashPlanner.Target> targets = StashPlanner.targets(UNITS, Map.of(GYPSY, StashState.BUILT)::get, NOTHING,
			options(1, EnumSet.noneOf(STASHUnit.class)), false);
		assertEquals(1, targets.size());
		assertTrue(targets.get(0).isFill());
		assertFalse(targets.get(0).isReady());
	}

	@Test
	public void filledSkippedAndUnknownAreIgnored()
	{
		Map<Integer, Integer> items = buildKit();
		items.put(ItemID.GOLD_RING, 1);
		items.put(ItemID.GOLD_NECKLACE, 1);
		OwnedItems owned = new OwnedItems(items, Map.of());
		assertTrue(StashPlanner.targets(UNITS, Map.of(GYPSY, StashState.FILLED)::get, owned,
			options(99, EnumSet.noneOf(STASHUnit.class)), false).isEmpty());
		assertTrue(StashPlanner.targets(UNITS, Map.<STASHUnit, StashState>of()::get, owned,
			options(99, EnumSet.noneOf(STASHUnit.class)), false).isEmpty());
		assertTrue(StashPlanner.targets(UNITS, Map.of(GYPSY, StashState.BUILT)::get, owned,
			options(99, EnumSet.of(GYPSY)), false).isEmpty());
	}
}
