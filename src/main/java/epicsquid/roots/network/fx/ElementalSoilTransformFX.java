package epicsquid.roots.network.fx;

import epicsquid.roots.network.ClientMessageHandler;
import epicsquid.roots.particle.ParticleUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class ElementalSoilTransformFX implements IMessage {
	
	private static Random random = new Random();
	
	private double x, y, z;
	private int elementId;
	
	public ElementalSoilTransformFX() {
	}
	
	public ElementalSoilTransformFX(double x, double y, double z, int elementId) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.elementId = elementId;
	}
	
	@Override
	public void fromBytes(ByteBuf byteBuf) {
		x = byteBuf.readDouble();
		y = byteBuf.readDouble();
		z = byteBuf.readDouble();
		elementId = byteBuf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf byteBuf) {
		byteBuf.writeDouble(x);
		byteBuf.writeDouble(y);
		byteBuf.writeDouble(z);
		byteBuf.writeInt(elementId);
	}
	
	public static float[] getColor(int elementId) {
		int r, g, b;
		
		switch (elementId) {
			//Fire
			case 0:
				r = 196;
				g = 90;
				b = 13;
				break;
			//Water
			case 1:
				r = 43;
				g = 159;
				b = 206;
				break;
			//Air
			case 2:
				r = 164;
				g = 196;
				b = 203;
				break;
			//Earth
			case 3:
				r = 81;
				g = 55;
				b = 8;
				break;
			//CHAOS (NULL) >:)
			default:
				r = 0;
				g = 0;
				b = 0;
		}
		
		return new float[]{r / 255.0f, g / 255.0f, b / 255.0f, 0.75f};
	}
	
	public static class Handler extends ClientMessageHandler<ElementalSoilTransformFX> {
		@SideOnly(Side.CLIENT)
		@Override
		protected void handleMessage(ElementalSoilTransformFX message, MessageContext ctx) {
			
			World world = Minecraft.getMinecraft().world;
			
			float[] color = getColor(message.elementId);
			
			for (int k = 0; k < 10; k++) {
				if (random.nextBoolean()) {
					ParticleUtil.spawnParticleGlow(world, (float) message.x, (float) message.y + 0.5F, (float) message.z, 0.125f * (random.nextFloat() - 0.5f), 0.125f * (random.nextFloat() - 0.5f), 0.125f * (random.nextFloat() - 0.5f), color, 9f, 30);
				} else {
					ParticleUtil.spawnParticleGlow(world, (float) message.x, (float) message.y + 0.5F, (float) message.z, 0.125f * (random.nextFloat() - 0.5f), 0.125f * (random.nextFloat() - 0.5f), 0.125f * (random.nextFloat() - 0.5f), color, 9f, 30);
				}
			}
		}
	}
}
