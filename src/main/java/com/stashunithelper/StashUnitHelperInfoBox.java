package com.stashunithelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import javax.inject.Inject;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

/** On-screen box with what the current unit needs: build materials and the items it stores. */
class StashUnitHelperInfoBox extends OverlayPanel
{
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

		// A line rather than a title: long unit names wrap instead of running past the box.
		panelComponent.getChildren().add(LineComponent.builder()
			.left(target.getInfo().getName())
			.leftColor(Color.WHITE)
			.leftFont(FontManager.getRunescapeBoldFont())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left(plugin.actionLabel(target))
			.leftColor(ColorScheme.BRAND_ORANGE)
			.build());
		section("Build", plugin.getBuildItems());
		section("Fill", plugin.getFillItems());
		return super.render(graphics);
	}

	private void section(String title, List<NeededItem> items)
	{
		if (items.isEmpty())
		{
			return;
		}
		panelComponent.getChildren().add(LineComponent.builder()
			.left(title)
			.leftColor(ColorScheme.LIGHT_GRAY_COLOR)
			.leftFont(FontManager.getRunescapeBoldFont())
			.build());
		for (NeededItem item : items)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left(item.getName())
				.right(item.getWhere().getLabel())
				.rightColor(item.getWhere().getColor())
				.build());
		}
	}
}
