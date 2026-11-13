package com.stashunithelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import lombok.Value;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.cluescrolls.clues.emote.STASHUnit;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

/** Side panel: pick a tier, start routing, and see that tier's units. */
class StashUnitHelperPanel extends PluginPanel
{
	private static final Color GREY = ColorScheme.LIGHT_GRAY_COLOR.darker();
	private static final int TEXT_WIDTH = PANEL_WIDTH - 50;

	/** One needed item and where the player has it. */
	@Value
	static class Line
	{
		String text;
		OwnedItems.Where where;
	}

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
		String status;
		String target;
		String notice;
		List<Row> rows;
	}

	private final StashUnitHelperPlugin plugin;
	private final ClientThread clientThread;
	private final JComboBox<StashTier> tierBox = new JComboBox<>(StashTier.values());
	private final JPanel content = new JPanel();
	private boolean updating;

	StashUnitHelperPanel(StashUnitHelperPlugin plugin, ClientThread clientThread)
	{
		this.plugin = plugin;
		this.clientThread = clientThread;
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(8, 8, 8, 8));

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
		buttons.setBorder(new EmptyBorder(4, 0, 0, 0));
		buttons.add(button("Start", "Route to the closest unit of this tier", plugin::requestRoute));
		buttons.add(button("Skip", "Skip this unit until you unskip it", plugin::skipTarget));
		buttons.add(button("Stop", "Clear the route", plugin::clearRoute));

		JPanel top = new JPanel(new BorderLayout());
		top.add(tierBox, BorderLayout.NORTH);
		top.add(buttons, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		add(content, BorderLayout.CENTER);
	}

	void update(Model model)
	{
		updating = true;
		tierBox.setSelectedItem(model.getTier());
		updating = false;

		content.removeAll();
		content.add(spacer());
		content.add(label(model.getStatus(), ColorScheme.LIGHT_GRAY_COLOR));
		if (model.getTarget() != null)
		{
			content.add(header(model.getTarget()));
		}
		if (model.getNotice() != null)
		{
			content.add(spacer());
			content.add(label(model.getNotice(), OwnedItems.Where.BANK.getColor()));
		}
		if (!model.getRows().isEmpty())
		{
			content.add(header("Units"));
			model.getRows().forEach(r -> content.add(row(r)));
		}
		content.revalidate();
		content.repaint();
	}

	private JButton button(String text, String tooltip, Runnable action)
	{
		JButton button = new JButton(text);
		button.setToolTipText(tooltip);
		button.addActionListener(e -> clientThread.invokeLater(action));
		return button;
	}

	private static JLabel spacer()
	{
		return label(" ", GREY);
	}

	private static JLabel header(String text)
	{
		JLabel label = label(text, ColorScheme.BRAND_ORANGE);
		label.setFont(FontManager.getRunescapeBoldFont());
		label.setBorder(new EmptyBorder(8, 0, 2, 0));
		return label;
	}

	private static JLabel label(String text, Color color)
	{
		// A fixed HTML width makes long text wrap instead of widening the panel.
		JLabel label = new JLabel("<html><body style='width:" + TEXT_WIDTH + "px'>" + text + "</body></html>");
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setForeground(color);
		label.setFont(FontManager.getRunescapeSmallFont());
		return label;
	}

	private JLabel row(Row row)
	{
		String mark;
		Color color;
		if (row.getState() == StashState.FILLED)
		{
			mark = "✓";
			color = OwnedItems.Where.CARRIED.getColor();
		}
		else if (row.getState() == StashState.BUILT)
		{
			mark = "◐";
			color = OwnedItems.Where.BANK.getColor();
		}
		else
		{
			mark = "✗";
			color = OwnedItems.Where.MISSING.getColor();
		}
		String text = mark + " " + row.getName();
		if (row.isSkipped())
		{
			text = "<s>" + text + "</s>";
			color = GREY;
		}
		if (row.isTarget())
		{
			text = "<b>" + text + "</b>";
		}

		JLabel label = label(text, color);
		label.setToolTipText(row.getTooltip());
		label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));
		label.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				menu(row).show(label, e.getX(), e.getY());
			}
		});
		return label;
	}

	private JPopupMenu menu(Row row)
	{
		JPopupMenu menu = new JPopupMenu();
		STASHUnit unit = row.getUnit();
		menu.add(item("Route here", () -> plugin.routeTo(unit)));
		menu.add(item(row.isSkipped() ? "Unskip" : "Skip", () -> plugin.setSkipped(unit, !row.isSkipped())));
		boolean filled = row.getState() == StashState.FILLED;
		menu.add(item(filled ? "Mark not filled" : "Mark filled", () -> plugin.setFilled(unit, !filled)));
		return menu;
	}

	private JMenuItem item(String text, Runnable action)
	{
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(e -> clientThread.invokeLater(action));
		return item;
	}
}
