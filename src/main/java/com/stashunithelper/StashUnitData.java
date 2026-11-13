package com.stashunithelper;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.cluescrolls.clues.emote.STASHUnit;

/**
 * Tier and emote clue for every STASH unit; the clue (from RuneLite's EmoteClue data) holds the items each unit
 * stores. StashUnitDataTest fails when RuneLite adds a unit or a shared-id clue can no longer be found.
 */
final class StashUnitData
{
	static final List<StashUnitInfo> UNITS = List.of(
		entry(STASHUnit.NEAR_A_SHED_IN_LUMBRIDGE_SWAMP, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE001, "Dance in the shack in Lumbridge Swamp. Equip a bronze dagger, iron full helmet and a gold ring."),
		entry(STASHUnit.ON_THE_BRIDGE_TO_THE_MISTHALIN_WIZARDS_TOWER, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE002, "Clap on the causeway to the Wizards' Tower. Equip an iron medium helmet, emerald ring and a white apron."),
		entry(STASHUnit.DRAYNOR_VILLAGE_MARKET, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE003, "Yawn in Draynor marketplace. Equip studded leather chaps, an iron kiteshield and a steel longsword."),
		entry(STASHUnit.LIMESTONE_MINE, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE004, "Panic in the Limestone Mine. Equip bronze platelegs, a steel pickaxe and a steel medium helmet."),
		entry(STASHUnit.OUTSIDE_THE_LEGENDS_GUILD_GATES, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE005, "Bow outside the entrance to the Legends' Guild. Equip iron platelegs, an emerald amulet and an oak longbow."),
		entry(STASHUnit.MUDSKIPPER_POINT, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE006, "Wave on Mudskipper Point. Equip a black cape, leather chaps and a steel mace."),
		entry(STASHUnit.NEAR_THE_ENTRANA_FERRY_IN_PORT_SARIM, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE007, "Cheer for the monks at Port Sarim. Equip a coif, steel plateskirt and a sapphire necklace."),
		entry(STASHUnit.AL_KHARID_SCORPION_MINE, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE008, "Headbang in the mine north of Al Kharid. Equip a desert shirt, leather gloves and leather boots."),
		entry(STASHUnit.DRAYNOR_MANOR_BY_THE_FOUNTAIN, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE009, "Spin in Draynor Manor by the fountain. Equip an iron platebody, studded leather chaps and a bronze full helmet."),
		entry(STASHUnit.WHEAT_FIELD_NEAR_THE_LUMBRIDGE_WINDMILL, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE010, "Think in middle of the wheat field by the Lumbridge mill. Equip a blue gnome robetop, a turquoise gnome robe bottom and an oak shortbow."),
		entry(STASHUnit.CROSSROADS_NORTH_OF_DRAYNOR_VILLAGE, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE011, "Dance at the crossroads north of Draynor. Equip an iron chain body, a sapphire ring and a longbow."),
		entry(STASHUnit.RIMMINGTON_MINE, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE012, "Shrug in the mine near Rimmington. Equip a gold necklace, a gold ring and a bronze spear."),
		entry(STASHUnit.VARROCK_PALACE_LIBRARY, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE013, "Yawn in the Varrock library. Equip a green gnome robe top, HAM robe bottom and an iron warhammer."),
		entry(STASHUnit.UPSTAIRS_IN_THE_ARDOUGNE_WINDMILL, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE014, "Clap on the top level of the mill, north of East Ardougne. Equip a blue gnome robe top, HAM robe bottom and an unenchanted tiara."),
		entry(STASHUnit.OUTSIDE_THE_FALADOR_PARTY_ROOM, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE015, "Dance in the Party Room. Equip a steel full helmet, steel platebody and an iron plateskirt."),
		entry(STASHUnit.TAVERLEY_STONE_CIRCLE, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE016, "Cheer at the Druids' Circle. Equip a blue wizard hat, a bronze two-handed sword and HAM boots."),
		entry(STASHUnit.CATHERBY_BEEHIVE_FIELD, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE018, "Jump for joy at the beehives. Equip a desert shirt, green gnome robe bottoms and a steel axe."),
		entry(STASHUnit.NEAR_THE_PARROTS_IN_ARDOUGNE_ZOO, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE019, "Blow a raspberry at the monkey cage in Ardougne Zoo. Equip a studded leather body, bronze platelegs and a normal staff with no orb."),
		entry(STASHUnit.ROAD_JUNCTION_NORTH_OF_RIMMINGTON, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE020, "Spin at the crossroads north of Rimmington. Equip a green gnome hat, cream gnome top and leather chaps."),
		entry(STASHUnit.OUTSIDE_THE_FISHING_GUILD, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE021, "Dance a jig by the entrance to the Fishing Guild. Equip an emerald ring, a sapphire amulet, and a bronze chain body."),
		entry(STASHUnit.OUTSIDE_KEEP_LE_FAYE, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE022, "Blow raspberries outside the entrance to Keep Le Faye. Equip a coif, an iron platebody and leather gloves."),
		entry(STASHUnit.ROAD_JUNCTION_SOUTH_OF_SINCLAIR_MANSION, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE024, "Laugh at the crossroads south of the Sinclair Mansion. Equip a cowl, a blue wizard robe top and an iron scimitar."),
		entry(STASHUnit.OUTSIDE_THE_DIGSITE_EXAM_CENTRE, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE025, "Clap in the main exam room in the Exam Centre. Equip a white apron, green gnome boots and leather gloves."),
		entry(STASHUnit.NEAR_THE_SAWMILL_OPERATORS_BOOTH, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE026, "Wave along the south fence of the Lumber Yard. Equip a hard leather body, leather chaps and a bronze axe."),
		entry(STASHUnit.EMIRS_ARENA_TICKET_OFFICE, StashTier.EASY, ItemID.TRAIL_CLUE_EASY_EMOTE027, "Bow in the office of the Emir's Arena. Equip an iron chain body, leather chaps and coif."),
		entry(STASHUnit.OUTSIDE_VARROCK_PALACE_COURTYARD, StashTier.EASY, ItemID.TRAIL_EASY_EMOTE_EXP1, "Spin in the Varrock Castle courtyard. Equip a black axe, a coif and a ruby ring."),
		entry(STASHUnit.NEAR_HERQUINS_SHOP_IN_FALADOR, StashTier.EASY, ItemID.TRAIL_EASY_EMOTE_EXP2, "Wave in the Falador gem store. Equip a Mithril pickaxe, Black platebody and an Iron Kiteshield."),
		entry(STASHUnit.SOUTH_OF_THE_GRAND_EXCHANGE, StashTier.EASY, ItemID.TRAIL_EASY_EMOTE_EXP3, "Dance at the entrance to the Grand Exchange. Equip a pink skirt, pink robe top and a body tiara."),
		entry(STASHUnit.AUBURYS_SHOP_IN_VARROCK, StashTier.EASY, ItemID.TRAIL_EASY_EMOTE_EXP4, "Do a jig in Varrock's rune store. Equip an air tiara and a staff of water."),
		entry(STASHUnit.CENTRE_OF_CANIFIS, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE001, "Dance in the centre of Canifis. Bow before you talk to me. Equip a green gnome robe top, mithril plate legs and an iron two-handed sword."),
		entry(STASHUnit.MAUSOLEUM_OFF_THE_MORYTANIA_COAST, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE002, "Panic by the mausoleum in Morytania. Wave before you speak to me. Equip a mithril plate skirt, a maple longbow and no boots."),
		entry(STASHUnit.EAST_OF_THE_BARBARIAN_VILLAGE_BRIDGE, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE003, "Spin on the bridge by the Barbarian Village. Salute before you talk to me. Equip purple gloves, a steel kiteshield and a mithril full helmet."),
		entry(STASHUnit.SOUTH_OF_THE_SHRINE_IN_TAI_BWO_WANNAI_VILLAGE, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE004, "Beckon in Tai Bwo Wannai. Clap before you talk to me. Equip green dragonhide chaps, a ring of dueling and a mithril medium helmet."),
		entry(STASHUnit.CASTLE_WARS_BANK, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE005, "Yawn in the Castle Wars lobby. Shrug before you talk to me. Equip a ruby amulet, a mithril scimitar and a Wilderness cape."),
		entry(STASHUnit.BARBARIAN_OUTPOST_OBSTACLE_COURSE, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE006, "Cheer in the Barbarian Agility Arena. Headbang before you talk to me. Equip a steel platebody, maple shortbow and a Wilderness cape."),
		entry(STASHUnit.GNOME_STRONGHOLD_BALANCING_ROPE, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE007, "Cry on top of the western tree in the Gnome Agility Arena. Indicate 'no' before you talk to me. Equip a steel kiteshield, ring of forging and green dragonhide chaps."),
		entry(STASHUnit.OUTSIDE_YANILLE_BANK, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE008, "Jump for joy in Yanille bank. Dance a jig before you talk to me. Equip a brown apron, adamantite medium helmet and snakeskin chaps."),
		entry(STASHUnit.OBSERVATORY, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE009, "Think in the centre of the Observatory. Spin before you talk to me. Equip a mithril chain body, green dragonhide chaps and a ruby amulet."),
		entry(STASHUnit.OGRE_CAGE_IN_KING_LATHAS_TRAINING_CAMP, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE010, "Cheer in the Ogre Pen in the Training Camp. Show you are angry before you talk to me. Equip a green dragonhide body and chaps and a steel square shield."),
		entry(STASHUnit.DIGSITE, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE011, "Beckon in the Digsite, near the eastern winch. Bow before you talk to me. Equip a green gnome hat, snakeskin boots and an iron pickaxe."),
		entry(STASHUnit.HICKTONS_ARCHERY_EMPORIUM, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE012, "Cry in the Catherby Ranging shop. Bow before you talk to me. Equip blue gnome boots, a hard leather body and an unblessed silver sickle."),
		entry(STASHUnit.SHANTAY_PASS, StashTier.MEDIUM, ItemID.TRAIL_CLUE_MEDIUM_EMOTE013, "Dance a jig under Shantay's Awning. Bow before you talk to me. Equip a pointed blue snail helmet, an air staff and a bronze square shield."),
		entry(STASHUnit.LUMBRIDGE_SWAMP_CAVES, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_EXP1, "Dance in the dark caves beneath Lumbridge Swamp. Blow a kiss before you talk to me. Equip an air staff, Bronze full helm and an amulet of power."),
		entry(STASHUnit.OUTSIDE_CATHERBY_BANK, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_EXP2, "Shrug in Catherby bank. Yawn before you talk to me. Equip a maple longbow, green d'hide chaps and an iron med helm."),
		entry(STASHUnit.OUTSIDE_THE_SEERS_VILLAGE_COURTHOUSE, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_EXP3, "Clap in Seers court house. Spin before you talk to me. Equip an adamant halberd, blue mystic robe bottom and a diamond ring."),
		entry(STASHUnit.OUTSIDE_HARRYS_FISHING_SHOP_IN_CATHERBY, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_EXP4, "Cry on the shore of Catherby beach. Laugh before you talk to me, equip an adamant sq shield, a bone dagger and mithril platebody."),
		entry(STASHUnit.TZHAAR_WEAPONS_STORE, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_EXP5, "Jump for joy in the TzHaar sword shop. Shrug before you talk to me. Equip a Steel longsword, Blue D'hide body and blue mystic gloves."),
		entry(STASHUnit.NORTH_OF_EVIL_DAVES_HOUSE_IN_EDGEVILLE, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_EXP6, "Cheer in the Edgeville general store. Dance before you talk to me. Equip a brown apron, leather boots and leather gloves."),
		entry(STASHUnit.WEST_OF_THE_SHAYZIEN_COMBAT_RING, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_EXP7, "Beckon in the Shayzien Combat Ring. Show your anger before you talk to me. Equip an adamant platebody, adamant full helm and adamant platelegs."),
		entry(STASHUnit.ENTRANCE_OF_THE_ARCEUUS_LIBRARY, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_EXP8, "Yawn in the centre of the Arceuus Library. Nod your head before you talk to me. Equip blue dragonhide vambraces, adamant boots and an adamant dagger."),
		entry(STASHUnit.OUTSIDE_DRAYNOR_VILLAGE_JAIL, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_EXP9, "Cry in the Draynor Village jail. Jump for joy before you talk to me. Equip an adamant sword, a sapphire amulet and an adamant plateskirt."),
		entry(STASHUnit.EAST_OF_THE_LEVEL_19_WILDERNESS_OBELISK, StashTier.HARD, ItemID.TRAIL_CLUE_HARD_EMOTE001, "Shrug in the woods east of the Level 19 Wilderness Obelisk. Beware of double agents! Equip rune platelegs, an iron platebody and blue dragonhide vambraces."),
		entry(STASHUnit.FISHING_GUILD_BANK, StashTier.HARD, ItemID.TRAIL_CLUE_HARD_EMOTE002, "Blow a raspberry in the Fishing Guild bank. Beware of double agents! Equip an elemental shield, blue dragonhide chaps and a rune warhammer."),
		entry(STASHUnit.TOP_FLOOR_OF_THE_LIGHTHOUSE, StashTier.HARD, ItemID.TRAIL_CLUE_HARD_EMOTE003, "Bow at the top of the lighthouse. Beware of double agents! Equip a blue dragonhide body, blue dragonhide vambraces and no jewelry."),
		entry(STASHUnit.OUTSIDE_THE_GREAT_PYRAMID_OF_SOPHANEM, StashTier.HARD, ItemID.TRAIL_CLUE_HARD_EMOTE005, "Dance at the cat-doored pyramid in Sophanem. Beware of double agents! Equip a ring of life, an uncharged amulet of glory and an adamant two-handed sword."),
		entry(STASHUnit.NOTERAZZOS_SHOP_IN_THE_WILDERNESS, StashTier.HARD, ItemID.TRAIL_CLUE_HARD_EMOTE006, "Yawn in the rogues' general store. Beware of double agents! Equip an adamant square shield, blue dragon vambraces and a rune pickaxe."),
		entry(STASHUnit.WEST_SIDE_OF_THE_KARAMJA_BANANA_PLANTATION, StashTier.HARD, ItemID.TRAIL_CLUE_HARD_EMOTE007, "Salute in the banana plantation. Beware of double agents! Equip a diamond ring, amulet of power, and nothing on your chest and legs."),
		entry(STASHUnit.MOUNTAIN_CAMP_GOAT_ENCLOSURE, StashTier.HARD, ItemID.TRAIL_CLUE_HARD_EMOTE008, "Laugh in Jokul's tent in the Mountain Camp. Beware of double agents! Equip a rune full helmet, blue dragonhide chaps and a fire battlestaff."),
		entry(STASHUnit.GNOME_GLIDER_ON_WHITE_WOLF_MOUNTAIN, StashTier.HARD, ItemID.TRAIL_CLUE_HARD_EMOTE009, "Panic by the pilot on White Wolf Mountain. Beware of double agents! Equip mithril platelegs, a ring of life and a rune axe."),
		entry(STASHUnit.SHILO_VILLAGE_BANK, StashTier.HARD, ItemID.TRAIL_CLUE_HARD_EMOTE010, "Blow a kiss between the tables in Shilo Village bank. Beware of double agents! Equip a blue mystic hat, bone spear and rune platebody."),
		entry(STASHUnit.INSIDE_THE_DIGSITE_EXAM_CENTRE, StashTier.HARD, ItemID.TRAIL_HARD_EMOTE_EXP1, "Headbang at the exam centre. Beware of double agents! Equip a mystic fire staff, a diamond bracelet and rune boots."),
		entry(STASHUnit.NORTHEAST_CORNER_OF_THE_KHARAZI_JUNGLE, StashTier.HARD, ItemID.TRAIL_HARD_EMOTE_EXP2, "Beckon on the east coast of the Kharazi Jungle. Beware of double agents! Equip any vestment stole and a heraldic rune shield."),
		entry(STASHUnit.VOLCANO_IN_THE_NORTHEASTERN_WILDERNESS, StashTier.HARD, ItemID.TRAIL_HARD_EMOTE_EXP3, "Panic on the Wilderness volcano bridge. Beware of double agents! Equip any headband and crozier."),
		entry(STASHUnit.IN_THE_MIDDLE_OF_JIGGIG, StashTier.HARD, ItemID.TRAIL_HARD_EMOTE_EXP4, "Jig at Jiggig. Beware of double agents! Equip a Rune spear, rune platelegs and any rune heraldic helm."),
		entry(STASHUnit.AGILITY_PYRAMID, StashTier.HARD, ItemID.TRAIL_HARD_EMOTE_EXP5, "Cheer at the top of the agility pyramid. Beware of double agents! Equip a blue mystic robe top and any rune heraldic shield."),
		entry(STASHUnit.HOSIDIUS_MESS, StashTier.HARD, ItemID.TRAIL_HARD_EMOTE_EXP6, "Salute in the centre of the mess hall. Beware of double agents! Equip a rune halberd rune platebody and an amulet of strength."),
		entry(STASHUnit.CHAPEL_IN_WEST_ARDOUGNE, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP1, "Spin in West Ardougne Church. Equip a dragon spear and red dragonhide chaps."),
		entry(STASHUnit.NEAR_A_RUNITE_ROCK_IN_THE_FREMENNIK_ISLES, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP2, "Jump for joy at the Neitiznot rune rock. Equip Rune boots, a proselyte hauberk and a dragonstone ring."),
		entry(STASHUnit.NEAR_A_LADDER_IN_THE_WILDERNESS_LAVA_MAZE, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP3, "Blow a kiss in the heart of the lava maze. Equip black dragonhide chaps, a spotted cape and a rolling pin."),
		entry(STASHUnit.ENTRANCE_OF_THE_CAVE_OF_DAMIS, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP4, "Cheer in the Shadow dungeon. Equip a rune crossbow, climbing boots and any mitre."),
		entry(STASHUnit.WARRIORS_GUILD_BANK, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP5, "Salute in the Warriors' Guild bank. Equip only a black salamander."),
		entry(STASHUnit.SOUTHEAST_CORNER_OF_THE_MONASTERY, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP6, "Bow upstairs in the Edgeville Monastery. Equip a completed prayer book."),
		entry(STASHUnit.SOUTHEAST_CORNER_OF_THE_FISHING_PLATFORM, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP7, "Dance on the Fishing Platform. Equip barrows gloves, an amulet of glory and a dragon med helm."),
		entry(STASHUnit.OUTSIDE_THE_SLAYER_TOWER_GARGOYLE_ROOM, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP8, "Headbang at the top of Slayer Tower. Equip a seercull, a combat bracelet and helm of Neitiznot."),
		entry(STASHUnit.ON_TOP_OF_TROLLHEIM_MOUNTAIN, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP9, "Yawn at the top of Trollheim. Equip a lava battlestaff, black dragonhide vambraces and a mind shield."),
		entry(STASHUnit.FOUNTAIN_OF_HEROES, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP10, "Laugh by the fountain of heroes. Equip splitbark legs, dragon boots and a Rune longsword."),
		entry(STASHUnit.ENTRANCE_OF_THE_CAVERN_UNDER_THE_WHIRLPOOL, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP11, "Jump for joy in the Ancient Cavern. Equip a granite shield, splitbark body and any rune heraldic helm."),
		entry(STASHUnit.HALFWAY_DOWN_TROLLWEISS_MOUNTAIN, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP12, "Panic at the area flowers meet snow. Equip Blue D'hide vambraces, a dragon spear and a rune plateskirt."),
		entry(STASHUnit.SHAYZIEN_WAR_TENT, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP13, "Shrug in the Shayzien war tent. Equip a blue mystic robe bottom, a rune kiteshield and any bob shirt."),
		entry(STASHUnit.OUTSIDE_THE_LEGENDS_GUILD_DOOR, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP14, "Bow on the ground floor of the Legends' Guild. Equip a Cape of Legends, a dragon battleaxe and an amulet of glory."),
		entry(STASHUnit.NEAR_THE_GEM_STALL_IN_ARDOUGNE_MARKET, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP15, "Laugh in front of the gem store in Ardougne market. Equip a Castlewars bracelet, a dragonstone amulet and a ring of forging."),
		entry(STASHUnit.OUTSIDE_THE_BAR_BY_THE_FIGHT_ARENA, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP16, "Headbang in the Fight Arena pub. Equip a pirate bandana, a dragonstone necklace and and a magic longbow."),
		entry(STASHUnit.SOUTHEAST_CORNER_OF_LAVA_DRAGON_ISLE, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Panic by the big egg where no one dare goes and the ground is burnt. Beware of double agents! Equip a dragon med helm, a TokTz-Ket-Xil, a brine sabre, rune platebody and an uncharged amulet of glory."),
		entry(STASHUnit.NEAR_THE_PIER_IN_ZULANDRA, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Jump for joy in the centre of Zul-Andra. Beware of double agents! Equip a dragon 2h sword, bandos boots and an obsidian cape."),
		entry(STASHUnit.BARROWS_CHEST, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Do a jig at the barrows chest. Beware of double agents! Equip any full barrows set."),
		entry(STASHUnit.WELL_OF_VOYAGE, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Dance in Iban's temple. Beware of double agents! Equip Iban's staff, a black mystic top and a black mystic bottom."),
		entry(STASHUnit.NORTHERN_WALL_OF_CASTLE_DRAKAN, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Wave on the northern wall of Castle Drakan. Beware of double agents! Wear a dragon sq shield, splitbark body and any boater."),
		entry(STASHUnit._7TH_CHAMBER_OF_JALSAVRAH, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Yawn in the 7th room of Pyramid Plunder. Beware of double agents! Equip a pharaoh sceptre and a full set of menaphite robes."),
		entry(STASHUnit.SOUL_ALTAR, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Spin in front of the Soul altar. Beware of double agents! Equip a dragon pickaxe, helm of neitiznot and a pair of rune boots."),
		entry(STASHUnit.WARRIORS_GUILD_BANK_29047, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Blow a raspberry in the bank of the Warriors' Guild. Beware of double agents! Equip a dragon battleaxe, a slayer helm of any kind and a dragon defender or avernic defender."),
		entry(STASHUnit.ENTRANA_CHAPEL, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Cheer in the Entrana church. Beware of double agents! Equip a full set of black dragonhide armour."),
		entry(STASHUnit.TZHAAR_GEM_STORE, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Cry in the TzHaar gem store. Beware of double agents! Equip a fire cape and TokTz-Xil-Ul."),
		entry(STASHUnit.TENT_IN_LORD_IORWERTHS_ENCAMPMENT, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Bow near Lord Iorwerth. Beware of double agents! Equip a charged crystal bow."),
		entry(STASHUnit.OUTSIDE_MUDKNUCKLES_HUT, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Goblin Salute in the Goblin Village. Beware of double agents! Equip a bandos godsword, a bandos cloak and a bandos platebody."),
		entry(STASHUnit.CENTRE_OF_THE_CATACOMBS_OF_KOUREND, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Slap your head in the centre of the Kourend catacombs. Beware of double agents! Equip arclight or emberlight along with the amulet of the damned."),
		entry(STASHUnit.KING_BLACK_DRAGONS_LAIR, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Dance in the King Black Dragon's lair. Beware of double agents! Equip a black dragonhide body, black dragonhide vambraces and a black dragon mask."),
		entry(STASHUnit.OUTSIDE_KRIL_TSUTSAROTHS_ROOM, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Blow a kiss outside K'ril Tsutsaroth's chamber. Beware of double agents! Equip a zamorak full helm and the shadow sword."),
		entry(STASHUnit.BY_THE_BEAR_CAGE_IN_VARROCK_PALACE_GARDENS, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Show your anger towards the Statue of Saradomin in Ellamaria's garden. Beware of double agents! Equip a zamorak godsword."),
		entry(STASHUnit.OUTSIDE_THE_WILDERNESS_AXE_HUT, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Clap in the magic axe hut. Beware of double agents! Equip only some flared trousers."),
		entry(STASHUnit.TOP_FLOOR_OF_THE_YANILLE_WATCHTOWER, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Swing a bullroarer at the top of the Watchtower. Beware of double agents! Equip a dragon plateskirt, climbing boots and a dragon chainbody."),
		entry(STASHUnit.DEATH_ALTAR, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Flap at the death altar. Beware of double agents! Equip a death tiara, a legend's cape and any ring of wealth."),
		entry(STASHUnit.BEHIND_MISS_SCHISM_IN_DRAYNOR_VILLAGE, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Show your anger at the Wise old man. Beware of double agents! Equip an abyssal whip, a legend's cape and some spined chaps."),
		entry(STASHUnit.NORTHWESTERN_CORNER_OF_THE_ENCHANTED_VALLEY, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Stamp in the Enchanted valley west of the waterfall. Beware of double agents! Equip a dragon axe."),
		entry(STASHUnit.NORTH_OF_MOUNT_KARUULM, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_EXP10, "Clap your hands north of Mount Karuulm Spin before you talk to me. Equip an adamant warhammer, a ring of life and a pair of mithril boots."),
		entry(STASHUnit.GYPSY_TENT_ENTRANCE, StashTier.BEGINNER, ItemID.TRAIL_CLUE_BEGINNER, "Blow a raspberry at Aris in her tent. Equip a gold ring and a gold necklace."),
		entry(STASHUnit.FINE_CLOTHES_ENTRANCE, StashTier.BEGINNER, ItemID.TRAIL_CLUE_BEGINNER, "Cheer at Iffie Nitter. Equip a chef hat and a red cape."),
		entry(STASHUnit.BOB_AXES_ENTRANCE, StashTier.BEGINNER, ItemID.TRAIL_CLUE_BEGINNER, "Clap at Bob's Brilliant Axes. Equip a bronze axe and leather boots."),
		entry(STASHUnit.CRYSTALLINE_MAPLE_TREES, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Beckon by a collection of crystalline maple trees. Beware of double agents! Equip Bryophyta's staff and a nature tiara."),
		entry(STASHUnit.CHARCOAL_BURNERS, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_EXP17, "Salute by the Charcoal Burners. Equip a Farmer's strawhat, Shayzien platebody (5) and Pyromancer robes."),
		entry(STASHUnit.FORTIS_GRAND_MUSEUM, StashTier.EASY, ItemID.TRAIL_EASY_EMOTE_VM01, "Yawn in the Fortis Grand Museum. Equip an emerald necklace, blue skirt, and turqoise gnome robe top."),
		entry(STASHUnit.TEMPLE_SOUTHEAST_OF_THE_BAZAAR, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_VM01, "Bow within the temple in Civitas illa Fortis. Equip any piece of sunfire fanatic armour."),
		entry(STASHUnit.CAM_TORUM_ENTRANCE, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Salute outside the gates of Cam Torum. Beware of double agents! Equip a full set of blue moon equipment."),
		entry(STASHUnit.WESTERN_SALVAGER_OVERLOOK, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Think on the western coast of Salvager Overlook. Beware of double agents! Equip a Hueycoatl hide coif and some Hueycoatl hide vambraces."),
		entry(STASHUnit.TWILIGHT_TEMPLE_MINE, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_23, "Jump for Joy in the mine near the Twilight Temple. Bow before you talk to me. Equip a maple longbow, a ruby amulet and some steel platelegs."),
		entry(STASHUnit.ORTUS_MEETS_PROUDSPIRE, StashTier.MEDIUM, ItemID.TRAIL_MEDIUM_EMOTE_24, "Nod your head where the River Ortus meets the Proudspire. Indicate 'no' before you talk to me. Equip a blue wizard hat, a blue wizard robe and wear nothing on your legs."),
		entry(STASHUnit.OUTSIDE_TWILIGHT_TEMPLE, StashTier.HARD, ItemID.TRAIL_HARD_EMOTE_VM2, "Panic outside the Twilight Temple. Beware of double agents! Equip a rune longsword, rune platebody and a rune plateskirt."),
		entry(STASHUnit.PANDEMONIUM_BAR, StashTier.EASY, ItemID.TRAIL_EASY_EMOTE_SAIL, "Dance a jig behind the bar on the Pandemonium. Equip a right eye patch and a bronze scimitar."),
		entry(STASHUnit.WINTUMBER_ISLAND, StashTier.ELITE, ItemID.TRAIL_ELITE_EMOTE_SAIL, "Do the crab dance by the monument on Wintumber Island. Equip a crab helmet and a crab claw."),
		entry(STASHUnit.BRITTLE_ISLE, StashTier.MASTER, ItemID.TRAIL_CLUE_MASTER, "Bow in front of the cave on Brittle Isle. Beware of double agents! Equip a Medallion of the Deep and a rosewood blowpipe.")
	);

	private static final Map<STASHUnit, StashUnitInfo> BY_UNIT;
	private static final Map<Integer, StashUnitInfo> BY_OBJECT_ID = new java.util.HashMap<>();

	static
	{
		Map<STASHUnit, StashUnitInfo> byUnit = new EnumMap<>(STASHUnit.class);
		for (StashUnitInfo info : UNITS)
		{
			byUnit.put(info.getUnit(), info);
			BY_OBJECT_ID.put(info.getUnit().getObjectId(), info);
		}
		BY_UNIT = Collections.unmodifiableMap(byUnit);
	}

	private StashUnitData()
	{
	}

	static StashUnitInfo get(STASHUnit unit)
	{
		return BY_UNIT.get(unit);
	}

	static StashUnitInfo forObjectId(int objectId)
	{
		return BY_OBJECT_ID.get(objectId);
	}

	private static StashUnitInfo entry(STASHUnit unit, StashTier tier, int clueItemId, String clueText)
	{
		return new StashUnitInfo(unit, tier, clueItemId, clueText);
	}
}