package com.stashunithelper;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Item;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.ui.ColorScheme;

/** Snapshot of what the player carries (inventory and worn) and last saw in their bank. */
final class OwnedItems
{
	/** Where an item is, with the label and colour the panel and items box show for it. */
	@Getter
	@RequiredArgsConstructor
	enum Where
	{
		CARRIED("Carried", ColorScheme.PROGRESS_COMPLETE_COLOR),
		BANK("Bank", ColorScheme.PROGRESS_INPROGRESS_COLOR),
		MISSING("Missing", ColorScheme.PROGRESS_ERROR_COLOR);

		private final String label;
		private final Color color;
	}

	private final Map<Integer, Integer> carried;
	private final Map<Integer, Integer> all;
	private final Item[] carriedItems;
	private final Item[] allItems;

	OwnedItems(Map<Integer, Integer> carried, Map<Integer, Integer> bank)
	{
		this.carried = Map.copyOf(carried);
		Map<Integer, Integer> merged = new HashMap<>(carried);
		bank.forEach((id, qty) -> merged.merge(id, qty, Integer::sum));
		this.all = Map.copyOf(merged);
		this.carriedItems = toItems(this.carried);
		this.allItems = toItems(this.all);
	}

	int count(int itemId)
	{
		return all.getOrDefault(itemId, 0);
	}

	Where locate(ItemRequirement requirement)
	{
		if (requirement.fulfilledBy(carriedItems))
		{
			return Where.CARRIED;
		}
		return requirement.fulfilledBy(allItems) ? Where.BANK : Where.MISSING;
	}

	Where locate(Set<Integer> anyOf, int quantity)
	{
		Where best = Where.MISSING;
		for (int id : anyOf)
		{
			if (carried.getOrDefault(id, 0) >= quantity)
			{
				return Where.CARRIED;
			}
			if (all.getOrDefault(id, 0) >= quantity)
			{
				best = Where.BANK;
			}
		}
		return best;
	}

	private static Item[] toItems(Map<Integer, Integer> items)
	{
		return items.entrySet().stream()
			.map(e -> new Item(e.getKey(), e.getValue()))
			.toArray(Item[]::new);
	}
}
