package com.stashunithelper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.ItemID;

/** Construction level and materials to build a STASH unit of each clue tier. */
@Getter
@RequiredArgsConstructor
public enum StashTier
{
	BEGINNER("Beginner", 12, ItemID.WOODPLANK, false),
	EASY("Easy", 27, ItemID.WOODPLANK, false),
	MEDIUM("Medium", 42, ItemID.PLANK_OAK, false),
	HARD("Hard", 55, ItemID.PLANK_TEAK, false),
	ELITE("Elite", 77, ItemID.PLANK_MAHOGANY, false),
	MASTER("Master", 88, ItemID.PLANK_MAHOGANY, true);

	private final String displayName;
	private final int constructionLevel;
	private final int plankId;
	private final boolean goldLeaf;

	@Override
	public String toString()
	{
		return displayName + " (Construction " + constructionLevel + ")";
	}
}
