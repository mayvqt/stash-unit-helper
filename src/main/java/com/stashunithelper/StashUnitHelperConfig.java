package com.stashunithelper;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(StashUnitHelperPlugin.CONFIG_GROUP)
public interface StashUnitHelperConfig extends Config
{
	String TIER = "tier";

	@ConfigItem(keyName = TIER, name = "Tier", description = "Tier to work on; picked in the side panel", hidden = true)
	default StashTier tier()
	{
		return StashTier.EASY;
	}

	@ConfigItem(keyName = "build", name = "Build units", description = "Route to units you can build", position = 0)
	default boolean build()
	{
		return true;
	}

	@ConfigItem(keyName = "fill", name = "Fill units", description = "Route to built units whose items you own", position = 1)
	default boolean fill()
	{
		return true;
	}

	@ConfigItem(keyName = "autoAdvance", name = "Auto next", description = "Route to the next unit after finishing the current one", position = 2)
	default boolean autoAdvance()
	{
		return true;
	}

	@ConfigItem(keyName = "infoBox", name = "Items box", description = "Show what the current unit needs in a box on screen", position = 3)
	default boolean infoBox()
	{
		return true;
	}

	@ConfigItem(keyName = "filterBank", name = "Filter bank", description = "Show only the current unit's items when you open your bank (needs the Bank Tags plugin)", position = 4)
	default boolean filterBank()
	{
		return true;
	}

	@ConfigItem(keyName = "hintArrow", name = "Hint arrow", description = "Point the game's hint arrow at the current unit", position = 5)
	default boolean hintArrow()
	{
		return true;
	}

	@Alpha
	@ConfigItem(keyName = "highlightColor", name = "Highlight colour", description = "Tile highlight for the current unit", position = 6)
	default Color highlightColor()
	{
		return new Color(0, 255, 255, 160);
	}
}
