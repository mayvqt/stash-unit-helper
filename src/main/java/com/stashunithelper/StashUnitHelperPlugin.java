package com.stashunithelper;

import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.ScriptID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.banktags.BankTagsPlugin;
import net.runelite.client.plugins.banktags.BankTagsService;
import net.runelite.client.plugins.banktags.TagManager;
import net.runelite.client.plugins.cluescrolls.clues.emote.STASHUnit;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Stash Unit Helper",
	description = "Routes you to the STASH units you can build or fill, using the Shortest Path plugin",
	tags = {"clue", "stash", "construction", "treasure", "trails", "emote"}
)
@PluginDependency(BankTagsPlugin.class)
public class StashUnitHelperPlugin extends Plugin
{
	static final String CONFIG_GROUP = "stashunithelper";
	private static final String KEY_FILLED = "filled";
	private static final String KEY_FILLED_SYNCED = "filledSynced";
	private static final String KEY_SKIPPED = "skipped";
	private static final String KEY_BANK = "bank";
	private static final String SHORTEST_PATH = "shortestpath";
	/** Bank tag holding the current unit's items; only ever opened by this plugin, with its name hidden. */
	private static final String BANK_TAG = "stash unit helper";
	private static final String QUERY_PREFIX = "stash-unit-helper-";
	private static final String MSG_DEPOSIT = "You deposit your items into the STASH unit.";
	private static final String MSG_WITHDRAW = "You withdraw your items from the STASH unit.";
	/** Building finishes a few ticks after the click; keep re-checking until it lands. */
	private static final int REFRESH_TICKS_AFTER_CLICK = 10;
	/** Shortest Path answers within a tick or two; silence this long means it isn't running. */
	private static final int ROUTE_TIMEOUT_TICKS = 5;
	private static final Set<MenuAction> OBJECT_ACTIONS = EnumSet.of(
		MenuAction.GAME_OBJECT_FIRST_OPTION, MenuAction.GAME_OBJECT_SECOND_OPTION, MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION, MenuAction.GAME_OBJECT_FIFTH_OPTION, MenuAction.WIDGET_TARGET_ON_GAME_OBJECT);

	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private ConfigManager configManager;
	@Inject
	private EventBus eventBus;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private StashUnitHelperOverlay overlay;
	@Inject
	private StashUnitHelperInfoBox infoBox;
	@Inject
	private StashUnitHelperConfig config;
	@Inject
	private TagManager tagManager;
	@Inject
	private BankTagsService bankTagsService;

	private final Set<STASHUnit> built = EnumSet.noneOf(STASHUnit.class);
	private final Set<STASHUnit> filled = EnumSet.noneOf(STASHUnit.class);
	private final Set<STASHUnit> skipped = EnumSet.noneOf(STASHUnit.class);
	private final Map<Integer, Integer> inventory = new HashMap<>();
	private final Map<Integer, Integer> worn = new HashMap<>();
	private final Map<Integer, Integer> bank = new HashMap<>();
	private final Map<ItemRequirement, String> requirementNames = new HashMap<>();
	private boolean statesKnown;
	private boolean statesDirty;
	private boolean filledSynced;
	private int refreshTicks;

	@Getter
	private StashPlanner.Target target;
	@Getter
	private List<StashUnitHelperPanel.Line> buildNeeds = List.of();
	@Getter
	private List<StashUnitHelperPanel.Line> stashNeeds = List.of();
	/** Built from the containers on demand; null after any of them changes. */
	private OwnedItems ownedCache;
	private boolean pathShown;
	private boolean hintArrowShown;
	private boolean shortestPathMissing;
	private String pendingQueryId;
	private int pendingQueryTick;
	private List<StashPlanner.Target> pendingTargets = List.of();
	private int queryCounter;

	private boolean ownCheckRunning;
	private STASHUnit gameCheckUnit;
	private STASHUnit lastClicked;

	private StashUnitHelperPanel panel;
	private NavigationButton navButton;
	private StashUnitHelperPanel.Model lastModel;

	@Provides
	StashUnitHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StashUnitHelperConfig.class);
	}

	@Override
	protected void startUp()
	{
		panel = new StashUnitHelperPanel(this, clientThread);
		navButton = NavigationButton.builder()
			.tooltip("Stash Unit Helper")
			.icon(icon())
			.priority(7)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navButton);
		overlayManager.add(overlay);
		overlayManager.add(infoBox);
		tagManager.registerTag(BANK_TAG, this::neededForTarget);
		clientThread.invokeLater(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				loadProfile();
				readContainer(InventoryID.INV, inventory);
				readContainer(InventoryID.WORN, worn);
				refreshTicks = 1;
			}
			updatePanel();
		});
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(infoBox);
		clientToolbar.removeNavigation(navButton);
		clientThread.invokeLater(() ->
		{
			clearRoute();
			tagManager.unregisterTag(BANK_TAG);
			built.clear();
			filled.clear();
			skipped.clear();
			inventory.clear();
			worn.clear();
			bank.clear();
			ownedCache = null;
			statesKnown = false;
			refreshTicks = 0;
			lastModel = null;
		});
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged event)
	{
		clearRoute();
		built.clear();
		statesKnown = false;
		loadProfile();
		refreshTicks = Math.max(refreshTicks, 1);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			refreshTicks = Math.max(refreshTicks, 1);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (refreshTicks > 0)
		{
			refreshTicks--;
			refreshBuilt();
		}
		if (statesDirty)
		{
			statesDirty = false;
			onStatesChanged();
		}
		if (pendingQueryId != null && client.getTickCount() - pendingQueryTick >= ROUTE_TIMEOUT_TICKS)
		{
			log.debug("No answer from Shortest Path; using the nearest unit");
			pendingQueryId = null;
			shortestPathMissing = true;
			showTarget(nearest(pendingTargets));
		}
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		if (ownCheckRunning || event.getScriptId() != ScriptID.WATSON_STASH_UNIT_CHECK)
		{
			return;
		}
		// Args: object id, then three bitpacked state ints. The STASH chart and Watson's noticeboard pass real
		// states; RuneLite's clue overlay passes zeros, which says nothing about filled units.
		int size = client.getIntStackSize();
		int[] stack = client.getIntStack();
		gameCheckUnit = null;
		if (size >= 4 && (stack[size - 3] | stack[size - 2] | stack[size - 1]) != 0)
		{
			StashUnitInfo info = StashUnitData.forObjectId(stack[size - 4]);
			gameCheckUnit = info == null ? null : info.getUnit();
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (ownCheckRunning || event.getScriptId() != ScriptID.WATSON_STASH_UNIT_CHECK || gameCheckUnit == null)
		{
			return;
		}
		STASHUnit unit = gameCheckUnit;
		gameCheckUnit = null;
		int size = client.getIntStackSize();
		int[] stack = client.getIntStack();
		if (size < 2)
		{
			return;
		}
		int isBuilt = stack[size - 2];
		int isFilled = stack[size - 1];
		log.debug("STASH check from game: {} built={} filled={}", unit, isBuilt, isFilled);
		if ((isBuilt != 0 && isBuilt != 1) || (isFilled != 0 && isFilled != 1))
		{
			return;
		}
		if (!filledSynced)
		{
			filledSynced = true;
			configManager.setRSProfileConfiguration(CONFIG_GROUP, KEY_FILLED_SYNCED, true);
		}
		if (isFilled == 1 ? filled.add(unit) : filled.remove(unit))
		{
			saveUnits(KEY_FILLED, filled);
		}
		statesDirty = true;
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		StashUnitInfo info = StashUnitData.forObjectId(event.getId());
		if (info != null && OBJECT_ACTIONS.contains(event.getMenuAction()))
		{
			lastClicked = info.getUnit();
			refreshTicks = REFRESH_TICKS_AFTER_CLICK;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (lastClicked == null || (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM))
		{
			return;
		}
		String message = Text.removeTags(event.getMessage());
		boolean changed;
		if (message.equals(MSG_DEPOSIT))
		{
			changed = filled.add(lastClicked);
		}
		else if (message.equals(MSG_WITHDRAW))
		{
			changed = filled.remove(lastClicked);
		}
		else
		{
			return;
		}
		if (changed)
		{
			saveUnits(KEY_FILLED, filled);
			statesDirty = true;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		int id = event.getContainerId();
		if (id == InventoryID.INV)
		{
			copyItems(event.getItemContainer(), inventory);
		}
		else if (id == InventoryID.WORN)
		{
			copyItems(event.getItemContainer(), worn);
		}
		else if (id == InventoryID.BANK)
		{
			copyItems(event.getItemContainer(), bank);
			saveBank();
		}
		else
		{
			return;
		}
		ownedCache = null;
		updatePanel();
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.BANKMAIN)
		{
			// Bank Tags sets up its own view when the bank loads; open ours once that's done.
			clientThread.invokeLater(this::updateBankFilter);
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (event.getSkill() == Skill.CONSTRUCTION)
		{
			updatePanel();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (CONFIG_GROUP.equals(event.getGroup()))
		{
			clientThread.invokeLater(() ->
			{
				updateHintArrow();
				updateBankFilter();
				updatePanel();
			});
		}
	}

	@Subscribe
	public void onPluginMessage(PluginMessage event)
	{
		if (!SHORTEST_PATH.equals(event.getNamespace()) || !"result".equals(event.getName()))
		{
			return;
		}
		Object id = event.getData().get("id");
		Object point = event.getData().get("target");
		clientThread.invokeLater(() -> onRouteResult(id, point));
	}

	/**
	 * Asks Shortest Path which is the cheapest to reach of every unit of the tier that can be built or filled now.
	 * When none can, the closest unfinished one still shows what to get for it.
	 */
	void requestRoute()
	{
		if (!statesKnown)
		{
			updatePanel();
			return;
		}
		OwnedItems owned = owned();
		StashPlanner.Options options = options();
		List<StashPlanner.Target> targets = StashPlanner.targets(StashUnitData.UNITS, this::state, owned, options, true);
		if (targets.isEmpty())
		{
			targets = StashPlanner.targets(StashUnitData.UNITS, this::state, owned, options, false);
		}
		if (targets.isEmpty())
		{
			clearRoute();
			return;
		}
		pendingTargets = targets;
		pendingQueryId = QUERY_PREFIX + (++queryCounter);
		pendingQueryTick = client.getTickCount();
		Map<String, Object> data = new HashMap<>();
		data.put("id", pendingQueryId);
		data.put("target", points(targets));
		eventBus.post(new PluginMessage(SHORTEST_PATH, "query", data));
		updatePanel();
	}

	void routeTo(STASHUnit unit)
	{
		StashState state = state(unit);
		if (state == null)
		{
			return;
		}
		pendingQueryId = null;
		showTarget(StashPlanner.target(StashUnitData.get(unit), state, owned(), options()));
	}

	void setTier(StashTier tier)
	{
		if (tier != config.tier())
		{
			configManager.setConfiguration(CONFIG_GROUP, StashUnitHelperConfig.TIER, tier);
		}
		requestRoute();
	}

	void clearRoute()
	{
		target = null;
		pendingQueryId = null;
		if (pathShown)
		{
			eventBus.post(new PluginMessage(SHORTEST_PATH, "clear", new HashMap<>()));
			pathShown = false;
		}
		updateHintArrow();
		updateBankFilter();
		updatePanel();
	}

	void skipTarget()
	{
		if (target != null)
		{
			setSkipped(target.getInfo().getUnit(), true);
		}
	}

	void setSkipped(STASHUnit unit, boolean skip)
	{
		if (skip ? skipped.add(unit) : skipped.remove(unit))
		{
			saveUnits(KEY_SKIPPED, skipped);
		}
		if (skip && target != null && target.getInfo().getUnit() == unit)
		{
			advance();
		}
		else
		{
			updatePanel();
		}
	}

	void setFilled(STASHUnit unit, boolean isFilled)
	{
		if (isFilled ? filled.add(unit) : filled.remove(unit))
		{
			saveUnits(KEY_FILLED, filled);
			onStatesChanged();
		}
	}

	String actionLabel(StashPlanner.Target t)
	{
		StashState state = state(t.getInfo().getUnit());
		if (state == StashState.NOT_BUILT)
		{
			return t.isFill() ? "Build + fill" : "Build";
		}
		if (state == StashState.BUILT)
		{
			return "Fill";
		}
		return state == StashState.FILLED ? "Done" : "STASH";
	}

	private void onRouteResult(Object id, Object point)
	{
		if (pendingQueryId == null || !pendingQueryId.equals(id))
		{
			return;
		}
		pendingQueryId = null;
		shortestPathMissing = false;
		StashPlanner.Target chosen = pendingTargets.stream()
			.filter(t -> Arrays.asList(t.getInfo().getUnit().getWorldPoints()).contains(point))
			.findFirst()
			.orElseGet(() -> nearest(pendingTargets));
		showTarget(chosen);
	}

	private void showTarget(StashPlanner.Target t)
	{
		if (t == null)
		{
			clearRoute();
			return;
		}
		target = t;
		if (!shortestPathMissing)
		{
			Map<String, Object> data = new HashMap<>();
			data.put("target", points(List.of(t)));
			eventBus.post(new PluginMessage(SHORTEST_PATH, "path", data));
			pathShown = true;
		}
		updateHintArrow();
		updateBankFilter();
		updatePanel();
	}

	private void advance()
	{
		target = null;
		if (config.autoAdvance())
		{
			requestRoute();
		}
		else
		{
			clearRoute();
		}
	}

	private void onStatesChanged()
	{
		if (target != null)
		{
			StashState state = state(target.getInfo().getUnit());
			boolean done = state == StashState.FILLED
				|| (state == StashState.BUILT && !target.isFill())
				|| (state == StashState.NOT_BUILT && !target.isBuild());
			if (done)
			{
				advance();
				return;
			}
		}
		updatePanel();
	}

	private void refreshBuilt()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		Set<STASHUnit> now = EnumSet.noneOf(STASHUnit.class);
		ownCheckRunning = true;
		try
		{
			for (StashUnitInfo info : StashUnitData.UNITS)
			{
				// Zeros for the bitpacked states: the built flag is still right, the filled flag is not.
				client.runScript(ScriptID.WATSON_STASH_UNIT_CHECK, info.getUnit().getObjectId(), 0, 0, 0);
				if (client.getIntStack()[0] == 1)
				{
					now.add(info.getUnit());
				}
			}
		}
		finally
		{
			ownCheckRunning = false;
		}
		boolean changed = !statesKnown || !now.equals(built);
		built.clear();
		built.addAll(now);
		statesKnown = true;
		if (filled.retainAll(built))
		{
			saveUnits(KEY_FILLED, filled);
			changed = true;
		}
		if (changed)
		{
			onStatesChanged();
		}
	}

	private StashState state(STASHUnit unit)
	{
		if (!statesKnown)
		{
			return null;
		}
		if (!built.contains(unit))
		{
			return StashState.NOT_BUILT;
		}
		return filled.contains(unit) ? StashState.FILLED : StashState.BUILT;
	}

	private OwnedItems owned()
	{
		if (ownedCache == null)
		{
			Map<Integer, Integer> carried = new HashMap<>(inventory);
			worn.forEach((id, qty) -> carried.merge(id, qty, Integer::sum));
			ownedCache = new OwnedItems(carried, bank);
		}
		return ownedCache;
	}

	private StashPlanner.Options options()
	{
		return new StashPlanner.Options(client.getBoostedSkillLevel(Skill.CONSTRUCTION), config.build(), config.fill(),
			this::tierEnabled, EnumSet.copyOf(skipped));
	}

	private boolean tierEnabled(StashTier tier)
	{
		return tier == config.tier();
	}

	private StashPlanner.Target nearest(List<StashPlanner.Target> targets)
	{
		Player player = client.getLocalPlayer();
		if (player == null || targets.isEmpty())
		{
			return targets.isEmpty() ? null : targets.get(0);
		}
		WorldPoint here = player.getWorldLocation();
		return targets.stream()
			.min(Comparator.comparingInt(t -> Arrays.stream(t.getInfo().getUnit().getWorldPoints())
				.mapToInt(here::distanceTo2D)
				.min()
				.orElse(Integer.MAX_VALUE)))
			.orElse(null);
	}

	/**
	 * Destinations only. Item requirements would make Shortest Path print item lists on the player's tile;
	 * the items box covers that instead.
	 */
	private static List<WorldPoint> points(List<StashPlanner.Target> targets)
	{
		List<WorldPoint> points = new ArrayList<>();
		targets.forEach(t -> points.addAll(Arrays.asList(t.getInfo().getUnit().getWorldPoints())));
		return points;
	}

	/** While the bank is open, show only the current unit's items, or put the bank back when there's no unit. */
	private void updateBankFilter()
	{
		if (client.getWidget(InterfaceID.Bankmain.UNIVERSE) == null)
		{
			return;
		}
		if (target != null && config.filterBank())
		{
			// Re-opening refreshes the filter after the unit changes.
			bankTagsService.openBankTag(BANK_TAG, BankTagsService.OPTION_HIDE_TAG_NAME | BankTagsService.OPTION_NO_LAYOUT);
		}
		else if (BANK_TAG.equals(bankTagsService.getActiveTag()))
		{
			bankTagsService.closeBankTag();
		}
	}

	/** Bank filter: build materials while the unit is unbuilt, and the items it stores until it's filled. */
	private boolean neededForTarget(int itemId)
	{
		if (target == null)
		{
			return false;
		}
		StashUnitInfo info = target.getInfo();
		StashState state = state(info.getUnit());
		if (state == StashState.NOT_BUILT && StashPlanner.isBuildMaterial(info.getTier(), itemId))
		{
			return true;
		}
		return state != StashState.FILLED && info.getRequirements() != null
			&& info.getRequirements().stream().anyMatch(r -> r.fulfilledBy(itemId));
	}

	private void updateHintArrow()
	{
		if (target != null && config.hintArrow())
		{
			client.setHintArrow(target.getInfo().getUnit().getWorldPoints()[0]);
			hintArrowShown = true;
		}
		else if (hintArrowShown)
		{
			client.clearHintArrow();
			hintArrowShown = false;
		}
	}

	private void updatePanel()
	{
		updateNeeds();
		if (panel == null)
		{
			return;
		}
		StashUnitHelperPanel.Model model = buildModel();
		if (model.equals(lastModel))
		{
			return;
		}
		lastModel = model;
		SwingUtilities.invokeLater(() -> panel.update(model));
	}

	/** What the current unit needs, for the on-screen items box. */
	private void updateNeeds()
	{
		if (target == null || !statesKnown)
		{
			buildNeeds = List.of();
			stashNeeds = List.of();
			return;
		}
		OwnedItems owned = owned();
		StashState state = state(target.getInfo().getUnit());
		buildNeeds = state == StashState.NOT_BUILT ? buildLines(target.getInfo().getTier(), owned) : List.of();
		stashNeeds = state == StashState.FILLED ? List.of() : fillLines(target.getInfo(), owned);
	}

	private StashUnitHelperPanel.Model buildModel()
	{
		StashTier tier = config.tier();
		if (!statesKnown)
		{
			return new StashUnitHelperPanel.Model(tier, "Log in to load your STASH units.", null, null, List.of());
		}

		OwnedItems owned = owned();
		List<StashUnitHelperPanel.Row> rows = new ArrayList<>();
		int builtCount = 0;
		int filledCount = 0;
		for (StashUnitInfo info : StashUnitData.UNITS)
		{
			if (info.getTier() != tier)
			{
				continue;
			}
			StashState state = state(info.getUnit());
			builtCount += state != StashState.NOT_BUILT ? 1 : 0;
			filledCount += state == StashState.FILLED ? 1 : 0;
			String tooltip = "<html>Stores:<br>" + fillLines(info, owned).stream()
				.map(l -> (l.getWhere() == OwnedItems.Where.MISSING ? "✗ " : "✓ ") + l.getText())
				.collect(Collectors.joining("<br>"))
				+ "</html>";
			rows.add(new StashUnitHelperPanel.Row(info.getUnit(), info.getName(), state, skipped.contains(info.getUnit()),
				target != null && target.getInfo().getUnit() == info.getUnit(), tooltip));
		}

		String status = String.format("Built %d/%d · Filled %d/%d", builtCount, rows.size(), filledCount, rows.size());
		int level = client.getBoostedSkillLevel(Skill.CONSTRUCTION);
		if (level < tier.getConstructionLevel())
		{
			status += "<br>You need Construction " + tier.getConstructionLevel() + " to build these (you have " + level + ").";
		}

		String notice;
		if (pendingQueryId != null)
		{
			notice = "Finding the closest unit...";
		}
		else if (shortestPathMissing)
		{
			notice = "Shortest Path isn't answering, so this is the closest unit in a straight line. Enable the Shortest Path plugin for walked routes.";
		}
		else if (target != null && !target.isReady())
		{
			notice = "Nothing in this tier is ready yet. Get the items marked ✗ in the on-screen box.";
		}
		else if (target == null && filledCount == rows.size())
		{
			notice = "Every " + tier.getDisplayName().toLowerCase() + " unit is filled.";
		}
		else if (!filledSynced)
		{
			notice = "Open the S.T.A.S.H. chart in your house or Watson's noticeboard once to load filled units.";
		}
		else
		{
			notice = null;
		}

		String targetText = target == null ? null : actionLabel(target) + ": " + target.getInfo().getName();
		return new StashUnitHelperPanel.Model(tier, status, targetText, notice, List.copyOf(rows));
	}

	private List<StashUnitHelperPanel.Line> buildLines(StashTier tier, OwnedItems owned)
	{
		List<StashUnitHelperPanel.Line> lines = new ArrayList<>();
		lines.add(new StashUnitHelperPanel.Line(StashPlanner.PLANKS + " x " + itemName(tier.getPlankId()),
			owned.locate(Set.of(tier.getPlankId()), StashPlanner.PLANKS)));
		lines.add(new StashUnitHelperPanel.Line(StashPlanner.NAILS + " x nails (one metal)",
			owned.locate(StashPlanner.NAIL_IDS, StashPlanner.NAILS)));
		lines.add(new StashUnitHelperPanel.Line("Hammer", owned.locate(StashPlanner.HAMMERS, 1)));
		lines.add(new StashUnitHelperPanel.Line("Saw", owned.locate(StashPlanner.SAWS, 1)));
		if (tier.isGoldLeaf())
		{
			lines.add(new StashUnitHelperPanel.Line(itemName(ItemID.GOLD_LEAF), owned.locate(Set.of(ItemID.GOLD_LEAF), 1)));
		}
		return lines;
	}

	private List<StashUnitHelperPanel.Line> fillLines(StashUnitInfo info, OwnedItems owned)
	{
		if (info.getRequirements() == null)
		{
			return List.of(new StashUnitHelperPanel.Line("Items unknown (update RuneLite)", OwnedItems.Where.MISSING));
		}
		List<StashUnitHelperPanel.Line> lines = new ArrayList<>();
		for (ItemRequirement requirement : info.getRequirements())
		{
			String name = requirementNames.computeIfAbsent(requirement, r -> r.getCollectiveName(client));
			lines.add(new StashUnitHelperPanel.Line(name, owned.locate(requirement)));
		}
		return lines;
	}

	private String itemName(int itemId)
	{
		return client.getItemDefinition(itemId).getName();
	}

	private void readContainer(int containerId, Map<Integer, Integer> into)
	{
		ItemContainer container = client.getItemContainer(containerId);
		if (container != null)
		{
			copyItems(container, into);
			ownedCache = null;
		}
	}

	private static void copyItems(ItemContainer container, Map<Integer, Integer> into)
	{
		into.clear();
		for (Item item : container.getItems())
		{
			// Bank placeholders have quantity 0.
			if (item.getId() >= 0 && item.getQuantity() > 0)
			{
				into.merge(item.getId(), item.getQuantity(), Integer::sum);
			}
		}
	}

	private void loadProfile()
	{
		loadUnits(KEY_FILLED, filled);
		loadUnits(KEY_SKIPPED, skipped);
		filledSynced = Boolean.TRUE.equals(configManager.getRSProfileConfiguration(CONFIG_GROUP, KEY_FILLED_SYNCED, Boolean.class));
		bank.clear();
		ownedCache = null;
		String saved = configManager.getRSProfileConfiguration(CONFIG_GROUP, KEY_BANK);
		if (saved != null && !saved.isEmpty())
		{
			for (String entry : saved.split(","))
			{
				String[] parts = entry.split(":");
				try
				{
					bank.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
				}
				catch (NumberFormatException | ArrayIndexOutOfBoundsException e)
				{
					log.debug("Ignoring bad saved bank entry {}", entry);
				}
			}
		}
	}

	private void saveBank()
	{
		configManager.setRSProfileConfiguration(CONFIG_GROUP, KEY_BANK, bank.entrySet().stream()
			.map(e -> e.getKey() + ":" + e.getValue())
			.collect(Collectors.joining(",")));
	}

	private void loadUnits(String key, Set<STASHUnit> into)
	{
		into.clear();
		String saved = configManager.getRSProfileConfiguration(CONFIG_GROUP, key);
		if (saved == null || saved.isEmpty())
		{
			return;
		}
		for (String name : saved.split(","))
		{
			try
			{
				into.add(STASHUnit.valueOf(name));
			}
			catch (IllegalArgumentException e)
			{
				log.debug("Ignoring unknown saved STASH unit {}", name);
			}
		}
	}

	private void saveUnits(String key, Set<STASHUnit> units)
	{
		configManager.setRSProfileConfiguration(CONFIG_GROUP, key,
			units.stream().map(Enum::name).collect(Collectors.joining(",")));
	}

	private static BufferedImage icon()
	{
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(new Color(110, 72, 36));
		g.fillRect(1, 5, 14, 10);
		g.setColor(new Color(150, 105, 55));
		g.fillRect(1, 2, 14, 4);
		g.setColor(new Color(220, 190, 80));
		g.fillRect(7, 5, 2, 3);
		g.dispose();
		return image;
	}
}
