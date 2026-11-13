package com.stashunithelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import javax.inject.Inject;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

/** On-screen box with what the current unit needs: build materials and the items it stores. */
class StashUnitHelperInfoBox extends OverlayPanel
{
	private static final Color HEADER = ColorScheme.BRAND_ORANGE;
	/** Wide enough for most item names on one line; the default overlay width wraps them. */
	private static final int WIDTH = 220;

	private final StashUnitHelperPlugin plugin;
	private final StashUnitHelperConfig config;

	@Inject
	StashUnitHelperInfoBox(StashUnitHelperPlugin plugin, StashUnitHelperConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		panelComponent.setPreferredSize(new Dimension(WIDTH, 0));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		StashPlanner.Target target = plugin.getTarget();
		if (!config.infoBox() || target == null)
		{
			return null;
		}

		panelComponent.getChildren().add(TitleComponent.builder()
			.text(plugin.actionLabel(target) + ": " + target.getInfo().getName())
			.build());
		section("Build", plugin.getBuildNeeds());
		section("Stash items", plugin.getStashNeeds());
		return super.render(graphics);
	}

	private void section(String title, List<StashUnitHelperPanel.Line> lines)
	{
		if (lines.isEmpty())
		{
			return;
		}
		panelComponent.getChildren().add(LineComponent.builder().left(title).leftColor(HEADER).build());
		for (StashUnitHelperPanel.Line line : lines)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left(line.getText())
				.right(line.getWhere().getLabel())
				.rightColor(line.getWhere().getColor())
				.build());
		}
	}
}
