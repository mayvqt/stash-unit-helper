package com.stashunithelper;

import java.awt.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.ui.ColorScheme;

/** Progress of one STASH unit, with the label and colour the side panel shows for it. */
@Getter
@RequiredArgsConstructor
enum StashState
{
	NOT_BUILT("Not built", ColorScheme.LIGHT_GRAY_COLOR.darker()),
	BUILT("Built", ColorScheme.PROGRESS_INPROGRESS_COLOR),
	FILLED("Filled", ColorScheme.PROGRESS_COMPLETE_COLOR);

	private final String label;
	private final Color color;
}
