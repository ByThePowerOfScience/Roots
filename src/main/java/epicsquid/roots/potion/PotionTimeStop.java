package epicsquid.roots.potion;

import epicsquid.roots.Roots;
import epicsquid.roots.entity.spell.EntityTimeStop;
import epicsquid.roots.init.ModDamage;
import epicsquid.roots.spell.SpellTimeStop;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionTimeStop extends Potion {
	private ResourceLocation texture = new ResourceLocation(Roots.MODID, "textures/gui/potions.png");
	
	public PotionTimeStop() {
		super(false, 0x737373);
		setPotionName("Time Stop");
		setIconIndex(1, 0);
		setBeneficial();
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}
	
	@Override
	public boolean shouldRender(PotionEffect effect) {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getStatusIconIndex() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		return super.getStatusIconIndex();
	}
	
	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		if (!EntityTimeStop.refreshing) {
			entityLivingBaseIn.attackEntityFrom(ModDamage.COLD_DAMAGE, SpellTimeStop.instance.cold_damage);
		}
	}
}
