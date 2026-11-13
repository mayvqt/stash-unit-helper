package com.stashunithelper;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

/** Highlights the tile of the STASH unit currently being routed to. */
class StashUnitHelperOverlay extends Overlay
{
	private final Client client;
	private final StashUnitHelperPlugin plugin;
	private final StashUnitHelperConfig config;

	@Inject
	StashUnitHelperOverlay(Client client, StashUnitHelperPlugin plugin, StashUnitHelperConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		StashPlanner.Target target = plugin.getTarget();
		if (target == null)
		{
			return null;
		}

		String label = plugin.actionLabel(target);
		for (WorldPoint worldPoint : target.getInfo().getUnit().getWorldPoints())
		{
			LocalPoint local = LocalPoint.fromWorld(client, worldPoint);
			if (local == null || worldPoint.getPlane() != client.getTopLevelWorldView().getPlane())
			{
				continue;
			}
			Polygon poly = Perspective.getCanvasTilePoly(client, local);
			if (poly != null)
			{
				OverlayUtil.renderPolygon(graphics, poly, config.highlightColor());
			}
			Point text = Perspective.getCanvasTextLocation(client, graphics, local, label, 0);
			if (text != null)
			{
				OverlayUtil.renderTextLocation(graphics, text, label, config.highlightColor());
			}
		}
		return null;
	}
}
