package epicsquid.roots.spell;

import epicsquid.roots.Roots;
import epicsquid.roots.init.ModBlocks;
import epicsquid.roots.init.ModItems;
import epicsquid.roots.init.ModPotions;
import epicsquid.roots.modifiers.*;
import epicsquid.roots.modifiers.instance.staff.StaffModifierInstanceList;
import epicsquid.roots.properties.Property;
import epicsquid.roots.util.EntityUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class SpellGeas extends SpellBase {
  public static Property.PropertyCooldown PROP_COOLDOWN = new Property.PropertyCooldown(80);
  public static Property.PropertyCastType PROP_CAST_TYPE = new Property.PropertyCastType(EnumCastType.INSTANTANEOUS);
  public static Property.PropertyCost PROP_COST_1 = new Property.PropertyCost(0, new SpellCost("baffle_cap", 0.5));
  public static Property<Integer> PROP_DURATION = new Property<>("geas_duration", 400).setDescription("duration in ticks of this spell effect on entities");
  public static Property<Integer> PROP_EXTENSION = new Property<>("extension", 600).setDescription("additional duration in ticks to be added to the base duration");
  public static Property.PropertyDamage PROP_DAMAGE = new Property.PropertyDamage(3.5f).setDescription("how much damage the spell should do before applying the potion effect");

  public static Modifier DURATION = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "extended_geas"), ModifierCores.PERESKIA, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.PERESKIA, 1)));
  public static Modifier PEACEFUL = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "animal_servants"), ModifierCores.WILDEWHEET, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.WILDEWHEET, 1)));
  public static Modifier PARALYSIS = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "rooted_response"), ModifierCores.WILDROOT, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.WILDROOT, 1)));
  public static Modifier GUARDIANS = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "unholy_command"), ModifierCores.MOONGLOW_LEAF, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.MOONGLOW_LEAF, 1)));
  public static Modifier TRIO = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "triumvirate"), ModifierCores.SPIRIT_HERB, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.SPIRIT_HERB, 1)));
  public static Modifier TARGET = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "targeted_geas"), ModifierCores.TERRA_MOSS, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.TERRA_MOSS, 1)));
  public static Modifier DUO = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "duumvirate"), ModifierCores.CLOUD_BERRY, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.CLOUD_BERRY, 1)));
  public static Modifier FIRE = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "inferno"), ModifierCores.INFERNAL_BULB, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.INFERNAL_BULB, 1)));
  public static Modifier PHYSICAL = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "avalanche"), ModifierCores.STALICRIPE, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.STALICRIPE, 1)));
  public static Modifier WATER = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "waterfall"), ModifierCores.DEWGONIA, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.DEWGONIA, 1)));

  static {
    // Conflicts
    TARGET.addConflicts(TRIO, DUO);
    PEACEFUL.addConflicts(FIRE, PHYSICAL, WATER);
  }

  public static ResourceLocation spellName = new ResourceLocation(Roots.MODID, "spell_geas");
  public static SpellGeas instance = new SpellGeas(spellName);

  private float damage;
  private int duration, extension;

  public SpellGeas(ResourceLocation name) {
    super(name, TextFormatting.DARK_RED, 128f / 255f, 32f / 255f, 32f / 255f, 32f / 255f, 32f / 255f, 32f / 255f);
    properties.addProperties(PROP_COOLDOWN, PROP_CAST_TYPE, PROP_COST_1, PROP_DURATION, PROP_EXTENSION, PROP_DAMAGE);
    acceptsModifiers(DURATION, PEACEFUL, PARALYSIS, GUARDIANS, TRIO, TARGET, DUO, FIRE, PHYSICAL, WATER);
  }

  @Override
  public void init() {
    addIngredients(
        new ItemStack(Item.getItemFromBlock(Blocks.WEB)),
        new ItemStack(Items.LEAD),
        new ItemStack(Items.CARROT_ON_A_STICK),
        new ItemStack(ModItems.terra_spores),
        new ItemStack(Item.getItemFromBlock(ModBlocks.baffle_cap_mushroom))
    );
  }

  @Override
  public boolean cast(EntityPlayer player, StaffModifierInstanceList info, int ticks) {
    int count = 1;
    int affected = 0;
    int dur = duration;
    if (info.has(DURATION)) {
      dur += extension;
    }
    if (!info.has(TARGET)) {
      if (info.has(DUO)) {
        count = 2;
      }
      if (info.has(TRIO)) {
        count = 3;
      }
      for (int i = 0; i < 20; i++) {
        if (affected == count) {
          break;
        }
        double x = player.posX + player.getLookVec().x * 3.0 * (float) i;
        double y = player.posY + player.getEyeHeight() + player.getLookVec().y * 3.0 * (float) i;
        double z = player.posZ + player.getLookVec().z * 3.0 * (float) i;
        List<EntityLivingBase> entities = player.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x - 4.0, y - 4.0, z - 4.0, x + 5.0, y + 5.0, z + 5.0));
        for (EntityLivingBase e : entities) {
          if (e == player) {
            continue;
          }
          if (e.getActivePotionEffect(ModPotions.geas) == null) {
            if (EntityUtil.isFriendlyTo(e, player) && info.has(PEACEFUL)) {
              affected++;
              e.getEntityData().setIntArray(getCachedName(), info.snapshot());
              e.addPotionEffect(new PotionEffect(ModPotions.geas, ampInt(dur), 0, false, false));
            } else if (EntityUtil.isHostileTo(e, player)) {
              affected++;
              if (!player.world.isRemote) {
                e.addPotionEffect(new PotionEffect(ModPotions.geas, ampInt(dur), 0, false, false));
                if (e instanceof EntityLiving) {
                  ((EntityLiving) e).setAttackTarget(null);
                }
                break;
              }
            }
          }
        }
      }
    }
    return affected > 0;
  }

  @Override
  public void doFinalise() {
    this.castType = properties.get(PROP_CAST_TYPE);
    this.cooldown = properties.get(PROP_COOLDOWN);
    this.duration = properties.get(PROP_DURATION);
    this.extension = properties.get(PROP_EXTENSION);
    this.damage = properties.get(PROP_DAMAGE);
  }
}
