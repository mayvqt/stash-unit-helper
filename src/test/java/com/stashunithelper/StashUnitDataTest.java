package com.stashunithelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import net.runelite.client.plugins.cluescrolls.clues.emote.STASHUnit;
import org.junit.Test;

public class StashUnitDataTest
{
	@Test
	public void everyUnitHasAnEntryWhoseClueMatches()
	{
		for (STASHUnit unit : STASHUnit.values())
		{
			StashUnitInfo info = StashUnitData.get(unit);
			assertNotNull("No entry for " + unit, info);
			assertNotNull("Clue not found for " + unit, info.getRequirements());
			assertFalse("No stored items for " + unit, info.getRequirements().isEmpty());
		}
		assertEquals(STASHUnit.values().length, StashUnitData.UNITS.size());
	}

	@Test
	public void prettyNames()
	{
		assertEquals("Near a Shed in Lumbridge Swamp", StashUnitInfo.prettyName(STASHUnit.NEAR_A_SHED_IN_LUMBRIDGE_SWAMP));
		assertEquals("7th Chamber of Jalsavrah", StashUnitInfo.prettyName(STASHUnit._7TH_CHAMBER_OF_JALSAVRAH));
		assertEquals("Warriors Guild Bank", StashUnitInfo.prettyName(STASHUnit.WARRIORS_GUILD_BANK_29047));
	}
}
