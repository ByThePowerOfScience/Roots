package epicsquid.roots.test;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import crafttweaker.mc1120.commands.CommandUtils;
import crafttweaker.mc1120.util.CraftTweakerPlatformUtils;
import epicsquid.roots.GuiHandler;
import epicsquid.roots.Roots;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestCommandHandler implements IClientCommand {
	@Override
	public String getName() {
		return "test";
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "/test";
	}
	
	@Override
	public List<String> getAliases() {
		return ImmutableList.of();
	}
	
	@Override
	public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
		return false;
	}
	
	public enum SubCommand {
		containers,
		guiimposer,
		guicrafter
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		try {
			
			Optional<SubCommand> subCommand = Enums.getIfPresent(SubCommand.class, args[0]);
			if (subCommand.isPresent()) {
				switch (subCommand.get()) {
					case containers:
						containers(server, sender);
						break;
					case guicrafter:
						openGui(server, sender, GuiHandler.CRAFTER_ID);
						break;
					case guiimposer:
						openGui(server, sender, GuiHandler.IMPOSER_ID);
						break;
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private void openGui(MinecraftServer server, ICommandSender sender, int guiId) {
		EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
		if (player == null)
			return;
		Vec3d eyes = player.getPositionEyes(1.0F);
		RayTraceResult rtr = player.getEntityWorld().rayTraceBlocks(eyes, eyes.add(player.getLookVec().scale(10.0)));
		if (rtr != null) {
			BlockPos pos = rtr.getBlockPos();
			
			TileEntity te = server.getEntityWorld().getTileEntity(pos);
			sender.sendMessage(new TextComponentString("Tile Entity at Position:" + te));
			
			player.openGui(Roots.instance, guiId, server.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ());
		}
		
	}
	
	private void containers(MinecraftServer server, ICommandSender sender) {
		EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
		assert player != null;
		
		sender.sendMessage(new TextComponentString("Player's active container: " + player.openContainer.toString()));
		sender.sendMessage(new TextComponentString("Server's active container: " + server.getEntityWorld().getPlayerEntityByUUID(player.getUniqueID()).openContainer.toString()));
		sender.sendMessage(new TextComponentString("Player's inventory: " + player.inventoryContainer.toString()));
		sender.sendMessage(new TextComponentString("Server's inventory: " + server.getEntityWorld().getPlayerEntityByUUID(player.getUniqueID()).inventoryContainer.toString()));
		
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return Arrays.stream(SubCommand.values()).map(Enum::toString).collect(Collectors.toList());
	}
	
	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}
	
	@Override
	public int compareTo(ICommand o) {
		return 0;
	}
}
