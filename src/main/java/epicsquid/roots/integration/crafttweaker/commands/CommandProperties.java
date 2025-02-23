package epicsquid.roots.integration.crafttweaker.commands;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import epicsquid.roots.properties.Property;
import epicsquid.roots.properties.PropertyTable;
import epicsquid.roots.recipe.SpiritDrops;
import epicsquid.roots.ritual.RitualBase;
import epicsquid.roots.ritual.RitualRegistry;
import epicsquid.roots.spell.SpellBase;
import epicsquid.roots.spell.SpellRegistry;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static crafttweaker.mc1120.commands.SpecialMessagesChat.getLinkToCraftTweakerLog;

public class CommandProperties extends CraftTweakerCommand {
	public CommandProperties() {
		super("roots_properties");
	}
	
	private enum SubCommand {
		all, ritual, spell, spirit_drops
	}
	
	@Override
	protected void init() {
		setDescription(new TextComponentTranslation("roots.commands.dump.desc"));
	}
	
	@Override
	public List<String> getSubSubCommand(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return Arrays.stream(SubCommand.values()).map(Enum::toString).collect(Collectors.toList());
	}
	
	private void logProperty(Map.Entry<String, Property<?>> entry) {
		String propName = entry.getKey();
		Property<?> property = entry.getValue();
		String propType = property.getType().toString();
		String desc = property.getDescription();
		switch (propName) {
			case "duration":
				desc = "duration of ritual or spell";
				break;
			case "radius_x":
				desc = "X-axis radius of ritual or spell";
				break;
			case "radius_y":
				desc = "Y-axis radius of ritual or spell";
				break;
			case "radius_z":
				desc = "Z-axis radius of ritual or spell";
				break;
			case "interval":
				desc = "frequency (in ticks) that ritual activates";
				break;
			case "cast_type":
				return;
			case "cooldown":
				desc = "cooldown(in ticks) before spell can be used again";
				break;
			case "cost_0":
				desc = "first spell herb cost";
				break;
			case "cost_1":
				desc = "second herb spell cost";
				break;
			case "damage":
				desc = "the amount of damage done by ritual or spell";
				break;
			default:
				break;
		}
		CraftTweakerAPI.logCommand("  - Property: " + propName + " (type: " + propType + "), default value: " + property.getDefaultValue() + ": " + desc);
	}
	
	@Override
	public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(new TextComponentTranslation("roots.commands.dump.usage"));
			return;
		}
		Optional<SubCommand> command = Enums.getIfPresent(SubCommand.class, args[0]);
		if (command.isPresent()) {
			switch (command.get()) {
				case all:
				case ritual:
					for (Map.Entry<String, RitualBase> entry : RitualRegistry.ritualRegistry.entrySet()) {
						String name = entry.getKey();
						RitualBase ritual = entry.getValue();
						PropertyTable props = ritual.getProperties();
						CraftTweakerAPI.logCommand("Ritual: " + name);
						props.forEach(this::logProperty);
						CraftTweakerAPI.logCommand("----------");
					}
					if (command.get() == SubCommand.ritual) {
						break;
					}
				case spell:
					for (Map.Entry<ResourceLocation, SpellBase> entry : SpellRegistry.spellRegistry.entrySet()) {
						String name = entry.getKey().getPath();
						SpellBase spell = entry.getValue();
						PropertyTable props = spell.getProperties();
						CraftTweakerAPI.logCommand("Spell: " + name);
						props.forEach(this::logProperty);
						CraftTweakerAPI.logCommand("----------");
					}
					if (command.get() == SubCommand.spell)
						break;
				case spirit_drops:
					CraftTweakerAPI.logCommand("Pouch:");
					SpiritDrops.getPouch().forEach(el -> CraftTweakerAPI.logCommand("\t-" + el.getItem().getDisplayName()));
					CraftTweakerAPI.logCommand("----------");
					CraftTweakerAPI.logCommand("Reliquary:");
					SpiritDrops.getReliquary().forEach(el -> CraftTweakerAPI.logCommand("\t-" + el.getItem().getDisplayName()));
					CraftTweakerAPI.logCommand("----------");
					if (command.get() == SubCommand.spirit_drops)
						break;
			}
			sender.sendMessage(getLinkToCraftTweakerLog("Dumped to CraftTweaker log.", sender));
		}
	}
}
