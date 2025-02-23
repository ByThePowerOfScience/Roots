package epicsquid.roots.client;

import epicsquid.mysticallib.util.CycleTimer;
import epicsquid.mysticallib.util.ItemUtil;
import epicsquid.roots.Roots;
import epicsquid.roots.config.ElementalSoilConfig;
import epicsquid.roots.init.ModItems;
import epicsquid.roots.integration.IntegrationUtil;
import epicsquid.roots.modifiers.Modifier;
import epicsquid.roots.modifiers.ModifierRegistry;
import epicsquid.roots.properties.PropertyTable;
import epicsquid.roots.ritual.RitualBase;
import epicsquid.roots.ritual.RitualRegistry;
import epicsquid.roots.spell.SpellBase;
import epicsquid.roots.spell.SpellChrysopoeia;
import epicsquid.roots.spell.SpellRegistry;
import epicsquid.roots.spell.info.storage.DustSpellStorage;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookCategory;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.common.book.Book;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class PatchouliHack {
	public static final CycleTimer timer = new CycleTimer(10);
	
	public static void init() {
		Map<String, BookTextParser.CommandProcessor> COMMANDS = ObfuscationReflectionHelper.getPrivateValue(BookTextParser.class, null, "COMMANDS");
		Map<String, BookTextParser.FunctionProcessor> FUNCTIONS = ObfuscationReflectionHelper.getPrivateValue(BookTextParser.class, null, "FUNCTIONS");
		int spaceWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(" ");
		
		BookTextParser.FunctionProcessor categoryFunction = (parameter, state) -> {
			state.prevColor = state.color;
			state.color = state.book.linkColor;
			ResourceLocation cat = new ResourceLocation(state.book.getModNamespace(), parameter);
			BookCategory entry = state.book.contents.categories.get(cat);
			if (entry != null) {
				state.tooltip = entry.isLocked() ? (TextFormatting.GRAY + I18n.format("patchouli.gui.lexicon.locked")) : entry.getName();
				GuiBook gui = state.gui;
				Book book = state.book;
				state.onClick = () -> {
					GuiBookCategory entryGui = new GuiBookCategory(book, entry);
					gui.displayLexiconGui(entryGui, true);
					GuiBook.playBookFlipSound(book);
				};
			} else {
				state.tooltip = "BAD LINK: INVALID CATEGORY " + parameter;
			}
			
			return "";
		};
		BookTextParser.CommandProcessor reset = (state) -> {
			state.color = state.prevColor;
			state.cluster = null;
			state.tooltip = "";
			state.onClick = null;
			state.isExternalLink = false;
			return "";
		};
		FUNCTIONS.put("cat", categoryFunction);
		COMMANDS.put("/cat", reset);
		
		BookTextParser.FunctionProcessor jeiUses = (parameter, state) -> {
			state.prevColor = state.color;
			if (Loader.isModLoaded("jei")) {
				String[] parts = parameter.split(":");
				ItemStack stack;
				if (parts[0].equals("roots") && parts[1].equals("chrysopoeia")) {
					stack = new ItemStack(ModItems.spell_icon);
					DustSpellStorage.fromStack(stack).setSpellToSlot(SpellChrysopoeia.instance);
				} else {
					stack = ItemUtil.stackFromString(parts);
				}
				if (!stack.isEmpty()) {
					state.color = state.book.linkColor;
					state.tooltip = stack.getDisplayName();
					state.onClick = () -> {
						IntegrationUtil.showUses(stack);
					};
				}
			}
			
			return "";
		};
		
		FUNCTIONS.put("uses", jeiUses);
		COMMANDS.put("/uses", reset);
		
		BookTextParser.FunctionProcessor jeiSources = (parameter, state) -> {
			state.prevColor = state.color;
			if (Loader.isModLoaded("jei")) {
				String[] parts = parameter.split(":");
				ItemStack stack = ItemUtil.stackFromString(parts);
				if (!stack.isEmpty()) {
					state.color = state.book.linkColor;
					state.tooltip = stack.getDisplayName();
					state.onClick = () -> {
						IntegrationUtil.showSources(stack);
					};
				}
			}
			
			return "";
		};
		
		FUNCTIONS.put("sources", jeiSources);
		COMMANDS.put("/sources", reset);
		
		Function<String, BookTextParser.FunctionProcessor> prop = (type) ->
				(parameter, state) -> {
					if (parameter.contains("/")) {
						String[] parts = parameter.split("/");
						RitualBase ritual = RitualRegistry.getRitual(parts[0]);
						SpellBase spell = SpellRegistry.getSpell(parts[0]);
						if (spell == null) {
							spell = SpellRegistry.getSpell("spell_" + parts[0]);
						}
						PropertyTable props;
						String propName = parts[1];
						boolean seconds = false;
						boolean minutes = false;
						boolean bool = false;
						boolean night = false;
						boolean day = false;
						boolean heart = false;
						boolean chance = false;
						boolean multiplier = false;
						boolean roman = false;
						boolean percent = false;
						boolean in_x = false;
						boolean bubbles = false;
						if (propName.equals("BUBBLES")) {
							propName = parts[2];
							bubbles = true;
						}
						if (propName.equals("HEART")) {
							propName = parts[2];
							heart = true;
						}
						if (propName.equals("NIGHT")) {
							propName = parts[2];
							night = true;
						}
						if (propName.equals("DAY")) {
							propName = parts[2];
							day = true;
						}
						if (propName.equals("BOOL")) {
							propName = parts[2];
							bool = true;
						}
						if (propName.equals("SECONDS")) {
							propName = parts[2];
							seconds = true;
						}
						if (propName.equals("MINUTES")) {
							propName = parts[2];
							minutes = true;
						}
						if (propName.equals("CHANCE")) {
							propName = parts[2];
							chance = true;
						}
						if (propName.equals("MULT")) {
							propName = parts[2];
							multiplier = true;
						}
						if (propName.equals("ROMAN")) {
							propName = parts[2];
							roman = true;
						}
						if (propName.equals("PERCENT")) {
							propName = parts[2];
							percent = true;
						}
						if (propName.equals("IN_X")) {
							propName = parts[2];
							in_x = true;
						}
						
						Object value = null;
						if (type.equals("ritual") && ritual != null) {
							props = ritual.getProperties();
							if (props.hasProperty(propName)) {
								value = props.getValue(propName);
							}
						} else if (type.equals("spell") && spell != null) {
							props = spell.getProperties();
							if (props.hasProperty(propName)) {
								value = props.getValue(propName);
							}
						} else if (type.equals("ritual")) {
							return "INVALID RITUAL";
						} else if (type.equals("spell")) {
							return "INVALID SPELL";
						}
						
						if (value != null) {
							if (seconds) {
								try {
									double val = ((Number) value).doubleValue();
									if (val != 0) {
										value = String.format("%.01f", val / 20);
									}
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR SECONDS: " + propName;
								}
							} else if (minutes) {
								try {
									double val = ((Number) value).doubleValue();
									value = val / 20 / 60;
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR MINUTES: " + propName;
								}
							} else if (bool) {
								try {
									boolean val = (boolean) value;
									// TODO: Translate this
									value = val ? I18n.format("roots.patchouli.true") : I18n.format("roots.patchouli.false");
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR BOOL: " + propName;
								}
							} else if (night) {
								try {
									double val = 100.0 / ((((Number) value).doubleValue() + 1));
									value = (int) val + "%";
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR NIGHT: " + propName;
								}
							} else if (day) {
								try {
									double val = 100.0 / ((Number) value).doubleValue();
									value = (int) val + "%";
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR DAY: " + propName;
								}
							} else if (heart) {
								try {
									float val = ((Number) value).floatValue() * 0.5f;
									value = String.format("%.01f", val);
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR HEARTS: " + propName;
								}
							} else if (chance) {
								try {
									double val = 1.0 / ((Number) value).doubleValue();
									value = String.format("%.03f", val) + "%";
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR CHANCE: " + propName;
								}
							} else if (multiplier) {
								try {
									value = ((Number) value).intValue() + 1;
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR MULTIPLIER: " + propName;
								}
							} else if (roman) {
								try {
									value = I18n.format("enchantment.level." + (((Number) value).intValue() + 1));
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR ROMAN NUMERAL: " + propName;
								}
							} else if (percent) {
								try {
									value = String.format("%.02f", ((Number) value).floatValue() * 100) + "%";
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR PERCENT: " + propName;
								}
							} else if (in_x) {
								try {
									value = String.format("%.02f", ((1.0 / ((Number) value).doubleValue())) * 100) + "%";
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR IN_X: " + propName;
								}
							} else if (bubbles) {
								try {
									value = String.valueOf((((Number) value).intValue() / 30));
								} catch (ClassCastException e) {
									Roots.logger.error("Couldn't convert property value: " + propName + " " + value, e);
									return "INVALID PROPERTY FOR BUBBLES: " + propName;
								}
							}
							
							if (value != null) {
								return value.toString();
							} else {
								return "INVALID COMMAND";
							}
						}
					}
					return "INVALID " + (type.equals("spell") ? "SPELL" : "RITUAL") + " COMMAND, REQUIRES PROPERTY TOO";
				};
		
		FUNCTIONS.put("spell", prop.apply("spell"));
		COMMANDS.put("/spell", reset);
		
		FUNCTIONS.put("ritual", prop.apply("ritual"));
		COMMANDS.put("/ritual", reset);
		
		BookTextParser.FunctionProcessor config = (parameter, state) -> {
			switch (parameter.toLowerCase(Locale.ROOT)) {
				case "earth_max_y":
					return "" + ElementalSoilConfig.EarthSoilMaxY;
				case "air_min_y":
					return "" + ElementalSoilConfig.AirSoilMinY;
				case "air_delay":
					return "" + String.format("%.01f", ElementalSoilConfig.AirSoilDelay / 20.0f);
				case "earth_delay":
					return "" + String.format("%.01f", ElementalSoilConfig.EarthSoilDelay / 20.0f);
				case "water_delay":
					return "" + String.format("%.01f", ElementalSoilConfig.WaterSoilDelay / 20.0f);
				default:
					return "" + 0;
			}
		};
		
		FUNCTIONS.put("config", config);
		COMMANDS.put("/config", reset);
		
		BookTextParser.FunctionProcessor link = FUNCTIONS.get("l");
		
		BookTextParser.FunctionProcessor modifier = (parameter, state) -> {
			ResourceLocation modid = !parameter.contains("roots:") ? new ResourceLocation(Roots.MODID, parameter) : new ResourceLocation(parameter);
			Modifier mod = ModifierRegistry.get(modid);
			if (mod == null) {
				return "INVALID MODIFIER: " + parameter;
			}
			SpellBase spell = ModifierRegistry.getSpellFromModifier(mod);
			if (spell == null) {
				return "MODIFIER " + parameter + " IS NOT CONNECTED TO A SPELL";
			}
			state.cluster = new LinkedList<>();
			state.prevColor = state.color;
			state.color = state.book.linkColor;
			String anchor = mod.getRegistryName().getPath();
			ResourceLocation href = new ResourceLocation(state.book.getModNamespace(), "spells/" + spell.getName());
			BookEntry entry = state.book.contents.entries.get(href);
			if (entry != null) {
				state.tooltip = entry.isLocked() ? TextFormatting.GRAY + I18n.format("patchouli.gui.lexicon.locked", new Object[0]) : entry.getName() + ": " + I18n.format(mod.getTranslationKey());
				GuiBook gui = state.gui;
				Book book = state.book;
				int page = 0;
				int anchorPage = entry.getPageFromAnchor(anchor);
				if (anchorPage >= 0) {
					page = anchorPage / 2;
				} else {
					state.tooltip = state.tooltip + " (INVALID ANCHOR:" + anchor + ")";
				}
				
				final int page2 = page;
				
				state.onClick = () -> {
					GuiBookEntry entryGui = new GuiBookEntry(book, entry, page2);
					gui.displayLexiconGui(entryGui, true);
					GuiBook.playBookFlipSound(book);
				};
			} else {
				state.tooltip = "BAD LINK: " + parameter;
			}
			return I18n.format(mod.getTranslationKey());
		};
		
		FUNCTIONS.put("modifier", modifier);
		COMMANDS.put("/modifier", reset);
		
		BookTextParser.FunctionProcessor advancement =
				(parameter, state) -> {
					ResourceLocation rl = new ResourceLocation(parameter.toLowerCase(Locale.ROOT));
					Minecraft mc = Minecraft.getMinecraft();
					if (mc == null || mc.player == null) {
						return "INVALID MINECRAFT";
					}
					ClientAdvancementManager manager = Minecraft.getMinecraft().player.connection.getAdvancementManager();
					AdvancementList list = manager.getAdvancementList();
					Advancement adv = list.getAdvancement(rl);
					if (adv == null || adv.getDisplay() == null) {
						return "INVALID ADVANCEMENT: " + rl;
					}
					String name = adv.getDisplay().getTitle().getFormattedText();
					state.color = state.book.linkColor;
					state.tooltip = adv.getDisplay().getDescription().getFormattedText();
					state.onClick = () -> {
						GuiScreenAdvancements screen = new GuiScreenAdvancements(mc.player.connection.getAdvancementManager());
						screen.setSelectedTab(adv);
						mc.displayGuiScreen(screen);
					};
					return name;
				};
		
		FUNCTIONS.put("adv", advancement);
		COMMANDS.put("/adv", reset);
	}
}
