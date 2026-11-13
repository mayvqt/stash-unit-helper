package com.stashunithelper;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

/** Launches a development client with the plugin loaded (`gradlew run`). */
public class StashUnitHelperPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(StashUnitHelperPlugin.class);
		RuneLite.main(args);
	}
}
