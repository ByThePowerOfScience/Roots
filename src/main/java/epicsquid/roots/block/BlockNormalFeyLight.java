package epicsquid.roots.block;

import epicsquid.mysticallib.util.Util;
import epicsquid.roots.particle.ParticleUtil;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockNormalFeyLight extends BlockFeyLight {
	private static FloatArrayList reds = new FloatArrayList(new float[]{177f, 255f, 255f, 219f, 122f});
	private static FloatArrayList greens = new FloatArrayList(new float[]{255f, 223f, 163f, 179f, 144f});
	private static FloatArrayList blues = new FloatArrayList(new float[]{117f, 163f, 255f, 255f, 255f});
	
	public BlockNormalFeyLight(@Nonnull Material mat, @Nonnull SoundType type, float hardness, @Nonnull String name) {
		super(mat, type, hardness, name);
		setTickRandomly(true);
	}
	
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
		int ind = Util.rand.nextInt(5);
		
		float r = reds.getFloat(ind);
		float g = greens.getFloat(ind);
		float b = blues.getFloat(ind);
		for (int i = 0; i < 2; i++) {
			ParticleUtil.spawnParticleGlow(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, (random.nextFloat() - 0.5f) * 0.003f, 0f, (random.nextFloat() - 0.5f) * 0.003f, r, g, b, 0.25f, 3.0f, 240);
		}
	}
}
