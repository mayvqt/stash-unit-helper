package com.stashunithelper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.emote.STASHUnit;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.SlotLimitationRequirement;

/** One STASH unit with its tier, display name and the items it stores. */
@Getter
final class StashUnitInfo
{
	private static final Set<String> SMALL_WORDS = Set.of("a", "an", "the", "of", "in", "by", "on", "to", "at", "off", "near");

	private final STASHUnit unit;
	private final StashTier tier;
	private final String name;
	/** Items the unit stores; null when RuneLite no longer has a clue with this text. */
	private final List<ItemRequirement> requirements;

	StashUnitInfo(STASHUnit unit, StashTier tier, int clueItemId, String clueText)
	{
		this.unit = unit;
		this.tier = tier;
		this.name = prettyName(unit);

		EmoteClue clue = resolve(unit, clueItemId, clueText);
		// "Have nothing equipped" style requirements are about empty slots, not stored items.
		this.requirements = clue == null ? null : Arrays.stream(clue.getItemRequirements())
			.filter(r -> !(r instanceof SlotLimitationRequirement))
			.collect(Collectors.toUnmodifiableList());
	}

	/**
	 * Item ids survive clue rewording, but every beginner and master clue shares one id, so those
	 * fall back to the clue text.
	 */
	static EmoteClue resolve(STASHUnit unit, int clueItemId, String clueText)
	{
		EmoteClue clue = EmoteClue.forItemId(clueItemId);
		if (clue == null || clue.getStashUnit() != unit)
		{
			clue = EmoteClue.forText(clueText);
		}
		return clue != null && clue.getStashUnit() == unit ? clue : null;
	}

	static String prettyName(STASHUnit unit)
	{
		String[] words = unit.name().toLowerCase(Locale.ROOT).split("_");
		StringBuilder sb = new StringBuilder();
		for (String word : words)
		{
			// Enum names start with "_" for leading digits and end with an object id to stay unique.
			if (word.isEmpty() || (word.chars().allMatch(Character::isDigit) && word.length() > 3))
			{
				continue;
			}
			if (sb.length() > 0)
			{
				sb.append(' ');
			}
			boolean small = sb.length() > 0 && SMALL_WORDS.contains(word);
			sb.append(small ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1));
		}
		return sb.toString();
	}
}
