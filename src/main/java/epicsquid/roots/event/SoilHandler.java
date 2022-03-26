package epicsquid.roots.event;

import epicsquid.mysticallib.util.ItemUtil;
import epicsquid.mysticallib.util.Util;
import epicsquid.roots.block.BlockElementalSoil;
import epicsquid.roots.config.ElementalSoilConfig;
import epicsquid.roots.init.ModBlocks;
import epicsquid.roots.mechanics.Harvest;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoilHandler {
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public static void onCropHarvest(final BlockEvent.HarvestDropsEvent event) {
		final IBlockState plantable = event.getState();
		
		if (event.getHarvester() == null || !(plantable.getBlock() instanceof IPlantable)) {
			return;
		}
		
		final BlockPos eventPos = event.getPos();
		final World eventWorld = event.getWorld();
		
		final IBlockState soil = eventWorld.getBlockState(eventPos.offset(EnumFacing.DOWN));
		final Block soilBlock = soil.getBlock();
		
		if (soilBlock == ModBlocks.elemental_soil_fire && soilCanSustainPlant(event, soilBlock, soil)) {
			final int cookingMultiplier = soil.getValue(BlockElementalSoil.FIRE_MULTIPLIER);
			if (cookingMultiplier > 0) {
				ItemStack seed = Harvest.getSeed(event.getState());
				
				event.getDrops().clear();
				
				List<ItemStack> drops = event.getDrops();
				final Random random = eventWorld.rand;
				
				boolean foundSeed = false;
				for (ItemStack stack : event.getDrops()) {
					
					if (!foundSeed) {
						if (!seed.isEmpty()) {
							if (ItemUtil.equalWithoutSize(seed, stack)) {
								foundSeed = true;
								drops.add(stack);
							}
						}
						else if (stack.getItem() instanceof IPlantable) {
							foundSeed = true;
							seed = stack;
							drops.add(stack);
						}
					}
					else {
						ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
						if (!result.isEmpty()) {
							ItemStack copy = result.copy();
							copy.setCount(cookingMultiplier - 1 > 0 ? random.nextInt(cookingMultiplier - 1) + 1 : 1);
							drops.add(copy);
						} else {
							drops.add(stack);
						}
					}
					
				}
				
				if (Harvest.isGrown(plantable)) {
					if (random.nextInt(3) == 0 && !eventWorld.isRemote) {
						eventWorld.spawnEntity(new EntityXPOrb(eventWorld, eventPos.getX() + 0.5, eventPos.getY() + 0.5, eventPos.getZ() + 0.5, 1));
					}
				}
			}
		} else if (soilBlock == ModBlocks.elemental_soil_earth && soilCanSustainPlant(event, soilBlock, soil)) {
			if (Harvest.isGrown(event.getState())) {
				int fertility = soil.getValue(BlockElementalSoil.EARTH_FERTILITY);
				if (fertility > 0) {
					final List<ItemStack> drops = event.getDrops();
					drops.clear();
					
					final Random random = eventWorld.rand;
					
					for (ItemStack stack : event.getDrops()) {
						drops.add(stack);
						if (ElementalSoilConfig.EarthSkipSeeds && stack.getItem() instanceof IPlantable) {
							continue;
						}
						if (random.nextInt(3) < fertility) {
							ItemStack copy = stack.copy();
							copy.setCount(fertility > 2 ? 2 : 1);
							drops.add(stack.copy());
						}
					}
				}
			}
		}
	}
	
	
	private static boolean soilCanSustainPlant(BlockEvent.HarvestDropsEvent event, Block soilBlock, IBlockState soilBlockState) {
		return (soilBlock.canSustainPlant(soilBlockState,
				event.getWorld(),
				event.getPos().offset(EnumFacing.DOWN),
				EnumFacing.UP,
				(IPlantable)event.getState().getBlock()));
	}
	
	
	
	@SubscribeEvent
	public static void onCropsGrowPost(BlockEvent.CropGrowEvent.Post cropGrowEvent) {
		if (cropGrowEvent.getWorld()
				.getBlockState(cropGrowEvent.getPos().offset(EnumFacing.DOWN))
				.getBlock() == ModBlocks.elemental_soil_water)
			((BlockElementalSoil) ModBlocks.elemental_soil_water).doHarvest(cropGrowEvent);
	}
	
	@SubscribeEvent
	public static void onCropsGrowPre(BlockEvent.CropGrowEvent.Pre cropGrowEvent) {
		final BlockPos downPos = cropGrowEvent.getPos().offset(EnumFacing.DOWN);
		final IBlockState soil = cropGrowEvent.getWorld().getBlockState(downPos);
		
		if (soil.getBlock() == ModBlocks.elemental_soil_air) 
		{
			final IBlockState plant = cropGrowEvent.getWorld().getBlockState(cropGrowEvent.getPos());
			
			if (plant.getBlock() instanceof IPlantable 
					&& soil.getBlock().canSustainPlant(soil, cropGrowEvent.getWorld(), downPos, EnumFacing.UP, (IPlantable) plant.getBlock())) 
			{
				int speed = soil.getValue(BlockElementalSoil.AIR_SPEED);
				if (speed > 0) {
					cropGrowEvent.setResult(cropGrowEvent.getWorld().rand.nextInt(3) == 0 ? Event.Result.ALLOW : Event.Result.DEFAULT);
				}
			}
		}
	}
}
