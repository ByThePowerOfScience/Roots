package epicsquid.roots.network.fx;

import epicsquid.mysticallib.util.Util;
import epicsquid.roots.network.ClientMessageHandler;
import epicsquid.roots.particle.ParticleUtil;
import epicsquid.roots.spell.SpellNaturesScythe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageFallBladesFX implements IMessage {
	private static final float[] colors1 = new float[]{227 / 255F, 179 / 255F, 66 / 255F, 1f};
	private static final float[] colors2 = new float[]{209 / 255F, 113 / 255F, 10 / 255F, 1f};
	
	private double x;
	private double y;
	private double z;
	private boolean isFall;
	
	@SuppressWarnings("unused")
	public MessageFallBladesFX() {
	}
	
	public MessageFallBladesFX(double x, double y, double z, boolean isFall) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.isFall = isFall;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		isFall = buf.readBoolean();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeBoolean(isFall);
	}
	
	public static class Handler extends ClientMessageHandler<MessageFallBladesFX> {
		@SideOnly(Side.CLIENT)
		@Override
		protected void handleMessage(MessageFallBladesFX message, MessageContext ctx) {
			World world = Minecraft.getMinecraft().world;
			
			for (int i = 0; i < 10; i++) {
				if (message.isFall) {
					if (Util.rand.nextBoolean()) {
						ParticleUtil.spawnParticlePetal(
								world, (float) message.x + Util.rand.nextFloat(), (float) message.y + Util.rand.nextFloat(), (float) message.z + Util.rand.nextFloat(),
								0, -0.05F, 0,
								colors1,
								1F, 160);
					}
					
					if (Util.rand.nextBoolean()) {
						ParticleUtil.spawnParticlePetal(
								world, (float) message.x + Util.rand.nextFloat(), (float) message.y + Util.rand.nextFloat(), (float) message.z + Util.rand.nextFloat(),
								0, -0.05F, 0,
								colors2,
								0.5F, 160);
					}
				} else {
					if (Util.rand.nextBoolean()) {
						ParticleUtil.spawnParticlePetal(
								world, (float) message.x + Util.rand.nextFloat(), (float) message.y + Util.rand.nextFloat(), (float) message.z + Util.rand.nextFloat(),
								0, 0.05F, 0,
								SpellNaturesScythe.instance.getRed1(), SpellNaturesScythe.instance.getGreen1(), SpellNaturesScythe.instance.getBlue1(), 1.0f,
								1F, 160);
					}
					
					if (Util.rand.nextBoolean()) {
						ParticleUtil.spawnParticlePetal(
								world, (float) message.x + Util.rand.nextFloat(), (float) message.y + Util.rand.nextFloat(), (float) message.z + Util.rand.nextFloat(),
								0, 0.05F, 0,
								SpellNaturesScythe.instance.getRed2(), SpellNaturesScythe.instance.getGreen2(), SpellNaturesScythe.instance.getBlue2(), 1.0f,
								0.5F, 160);
					}
				}
			}
		}
	}
}
