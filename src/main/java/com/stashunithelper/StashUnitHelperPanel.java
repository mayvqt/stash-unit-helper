package com.stashunithelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import lombok.Value;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.cluescrolls.clues.emote.STASHUnit;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

/** Side panel: pick a tier, start routing, and see that tier's units. */
class StashUnitHelperPanel extends PluginPanel
{
	private static final int GAP = 6;
	private static final int PAD = 6;
	private static final int ACCENT = 3;
	private static final Color MUTED = ColorScheme.LIGHT_GRAY_COLOR.darker();
	/** A fixed HTML width makes long text wrap instead of widening the panel. */
	private static final int TEXT_WIDTH = PANEL_WIDTH - 50;
	/** Leaves room for the state label on the right of a unit row. */
	private static final int ROW_NAME_WIDTH = PANEL_WIDTH - 100;

	@Value
	static class Row
	{
		STASHUnit unit;
		String name;
		StashState state;
		boolean skipped;
		boolean target;
		String tooltip;
	}

	@Value
	static class Model
	{
		StashTier tier;
		int built;
		int filled;
		/** Null when the player has the Construction level for this tier. */
		String levelWarning;
		/** Action and unit name of the current target, or null when there is none. */
		String action;
		String targetName;
		/** A route is shown or being looked up. */
		boolean routing;
		String notice;
		List<Row> rows;
	}

	private final StashUnitHelperPlugin plugin;
	private final ClientThread clientThread;
	private final JComboBox<StashTier> tierBox = new JComboBox<>(StashTier.values());
	private final JButton skipButton;
	private final JButton stopButton;
	private final JPanel content = new JPanel();
	private boolean updating;

	StashUnitHelperPanel(StashUnitHelperPlugin plugin, ClientThread clientThread)
	{
		this.plugin = plugin;
		this.clientThread = clientThread;
		setLayout(new BorderLayout(0, GAP));

		tierBox.setToolTipText("Tier to build and fill");
		tierBox.addActionListener(e ->
		{
			StashTier tier = (StashTier) tierBox.getSelectedItem();
			if (!updating && tier != null)
			{
				clientThread.invokeLater(() -> plugin.setTier(tier));
			}
		});

		JPanel buttons = new JPanel(new GridLayout(1, 3, 4, 0));
		buttons.add(button("Start", "Route to the closest unit of this tier", plugin::requestRoute));
		skipButton = button("Skip", "Skip this unit until you unskip it", plugin::skipTarget);
		stopButton = button("Stop", "Clear the route", plugin::clearRoute);
		buttons.add(skipButton);
		buttons.add(stopButton);

		JPanel controls = new JPanel(new BorderLayout(0, 4));
		controls.add(tierBox, BorderLayout.NORTH);
		controls.add(buttons, BorderLayout.SOUTH);
		add(controls, BorderLayout.NORTH);

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		add(content, BorderLayout.CENTER);
	}

	void update(Model model)
	{
		updating = true;
		tierBox.setSelectedItem(model.getTier());
		updating = false;
		skipButton.setEnabled(model.getTargetName() != null);
		stopButton.setEnabled(model.isRouting());

		content.removeAll();
		if (model.getTargetName() != null)
		{
			section(targetCard(model));
		}
		if (model.getNotice() != null)
		{
			section(text(model.getNotice(), MUTED, FontManager.getRunescapeSmallFont(), TEXT_WIDTH));
		}
		if (!model.getRows().isEmpty())
		{
			section(progressCard(model));
			section(text("Units", Color.WHITE, FontManager.getRunescapeBoldFont(), TEXT_WIDTH));
			JPanel list = new JPanel(new DynamicGridLayout(0, 1, 0, 2));
			list.setAlignmentX(LEFT_ALIGNMENT);
			model.getRows().forEach(r -> list.add(unitRow(r)));
			content.add(Box.createVerticalStrut(2));
			content.add(list);
		}
		content.revalidate();
		content.repaint();
	}

	private void section(JComponent component)
	{
		if (content.getComponentCount() > 0)
		{
			content.add(Box.createVerticalStrut(GAP));
		}
		component.setAlignmentX(LEFT_ALIGNMENT);
		content.add(component);
	}

	private static JPanel targetCard(Model model)
	{
		JPanel card = card(true);
		card.setLayout(new BorderLayout(0, 2));
		card.add(text(model.getAction(), ColorScheme.BRAND_ORANGE, FontManager.getRunescapeSmallFont(), TEXT_WIDTH),
			BorderLayout.NORTH);
		card.add(text(model.getTargetName(), Color.WHITE, FontManager.getRunescapeBoldFont(), TEXT_WIDTH),
			BorderLayout.CENTER);
		return card;
	}

	private static JPanel progressCard(Model model)
	{
		int total = model.getRows().size();
		JPanel card = card(false);
		card.setLayout(new DynamicGridLayout(0, 1, 0, 2));
		card.add(stat("Built", model.getBuilt(), total));
		card.add(stat("Filled", model.getFilled(), total));
		if (model.getLevelWarning() != null)
		{
			card.add(text(model.getLevelWarning(), ColorScheme.PROGRESS_ERROR_COLOR, FontManager.getRunescapeSmallFont(),
				TEXT_WIDTH));
		}
		return card;
	}

	private static JPanel stat(String name, int count, int total)
	{
		JPanel line = new JPanel(new BorderLayout());
		line.setOpaque(false);
		line.add(text(name, ColorScheme.LIGHT_GRAY_COLOR, FontManager.getRunescapeSmallFont(), 0), BorderLayout.WEST);
		Color color = count == total ? ColorScheme.PROGRESS_COMPLETE_COLOR : Color.WHITE;
		line.add(text(count + " / " + total, color, FontManager.getRunescapeSmallFont(), 0), BorderLayout.EAST);
		return line;
	}

	private JPanel unitRow(Row row)
	{
		JPanel panel = card(row.isTarget());
		panel.setLayout(new BorderLayout(GAP, 0));
		panel.setToolTipText(row.getTooltip());
		panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		String name = row.isSkipped() ? "<s>" + row.getName() + "</s>" : row.getName();
		panel.add(text(name, row.isSkipped() ? MUTED : Color.WHITE, FontManager.getRunescapeSmallFont(), ROW_NAME_WIDTH),
			BorderLayout.CENTER);
		JLabel state = text(row.isSkipped() ? "Skipped" : row.getState().getLabel(),
			row.isSkipped() ? MUTED : row.getState().getColor(), FontManager.getRunescapeSmallFont(), 0);
		state.setVerticalAlignment(JLabel.TOP);
		panel.add(state, BorderLayout.EAST);

		panel.setComponentPopupMenu(menu(row));
		panel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					clientThread.invokeLater(() -> plugin.routeTo(row.getUnit()));
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				panel.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}
		});
		return panel;
	}

	private JPopupMenu menu(Row row)
	{
		JPopupMenu menu = new JPopupMenu();
		STASHUnit unit = row.getUnit();
		menu.add(menuItem(row.isSkipped() ? "Unskip" : "Skip", () -> plugin.setSkipped(unit, !row.isSkipped())));
		boolean filled = row.getState() == StashState.FILLED;
		menu.add(menuItem(filled ? "Mark not filled" : "Mark filled", () -> plugin.setFilled(unit, !filled)));
		return menu;
	}

	private JMenuItem menuItem(String text, Runnable action)
	{
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(e -> clientThread.invokeLater(action));
		return item;
	}

	private JButton button(String text, String tooltip, Runnable action)
	{
		JButton button = new JButton(text);
		button.setToolTipText(tooltip);
		button.setFocusable(false);
		button.addActionListener(e -> clientThread.invokeLater(action));
		return button;
	}

	/** A dark card; the current target gets an orange bar on its left edge. */
	private static JPanel card(boolean accent)
	{
		JPanel card = new JPanel();
		card.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		Border padding = BorderFactory.createEmptyBorder(PAD, accent ? PAD - ACCENT : PAD, PAD, PAD);
		card.setBorder(accent
			? BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, ACCENT, 0, 0, ColorScheme.BRAND_ORANGE), padding)
			: padding);
		return card;
	}

	/** A label; with a width, its text wraps at that many pixels. */
	private static JLabel text(String text, Color color, Font font, int width)
	{
		JLabel label = new JLabel(width > 0 ? "<html><body style='width:" + width + "px'>" + text + "</body></html>" : text);
		label.setForeground(color);
		label.setFont(font);
		return label;
	}
}
