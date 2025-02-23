package epicsquid.roots.item.runed;

import epicsquid.mysticallib.item.tool.ItemExcavatorBase;
import epicsquid.roots.config.ToolConfig;
import epicsquid.roots.item.ILivingRepair;
import epicsquid.roots.recipe.ingredient.RootsIngredients;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

@SuppressWarnings("deprecation")
public class ItemRunedShovel extends ItemExcavatorBase implements ILivingRepair {
	public ItemRunedShovel(ToolMaterial material, String name) {
		super(name, 1992, material, () -> Ingredient.EMPTY);
		this.attackDamage = 5.5f;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		update(stack, worldIn, entityIn, itemSlot, isSelected, 90);
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		Block block = worldIn.getBlockState(pos).getBlock();
		
		// TODO: Make this AoE
		
		if (facing != EnumFacing.DOWN && worldIn.isAirBlock(pos.up()) && (block == Blocks.GRASS || block == Blocks.DIRT)) {
			if (!worldIn.isRemote) {
				worldIn.playSound(null, pos, Blocks.GRASS_PATH.getSoundType().getStepSound(), SoundCategory.BLOCKS, 1F, 1F);
				worldIn.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState());
				if (!player.capabilities.isCreativeMode) {
					stack.damageItem(1, player);
				}
			}
			return EnumActionResult.SUCCESS;
		}
		
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return toRepair.getItem() == this && RootsIngredients.RUNED_OBSIDIAN.test(repair);
	}
	
	@Override
	public Set<Block> getBlockBlacklist() {
		return ToolConfig.getRunicBlockBlacklist();
	}
}
