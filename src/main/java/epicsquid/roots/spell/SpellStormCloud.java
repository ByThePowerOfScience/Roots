package epicsquid.roots.spell;

import epicsquid.roots.Roots;
import epicsquid.roots.init.ModItems;
import epicsquid.roots.init.ModPotions;
import epicsquid.roots.modifiers.*;
import epicsquid.roots.modifiers.instance.staff.StaffModifierInstanceList;
import epicsquid.roots.properties.Property;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class SpellStormCloud extends SpellBase {
  public static Property.PropertyCooldown PROP_COOLDOWN = new Property.PropertyCooldown(100);
  public static Property.PropertyCastType PROP_CAST_TYPE = new Property.PropertyCastType(EnumCastType.INSTANTANEOUS);
  public static Property.PropertyCost PROP_COST_1 = new Property.PropertyCost(0, new SpellCost("cloud_berry", 0.015));
  public static Property.PropertyDuration PROP_DURATION = new Property.PropertyDuration(600).setDescription("the duration of the spell effect on the player");
  public static Property<Integer> PROP_RADIUS = new Property<>("radius", 2).setDescription("the radius of the area covered by the spring storm");
  public static Property<Integer> PROP_RADIUS_EXTENDED = new Property<>("extended_radius", 2).setDescription("the additional radius of the storm");
  public static Property<Integer> PROP_LIGHTNING_CHANCE = new Property<>("lightning_chance", 4).setDescription("chance for each lightning to spawn (the higher the number is the lower the chance becomes: 1/x) [default: 1/4]");
  public static Property<Integer> PROP_LIGHTNING_STRIKES = new Property<>("lightning_strikes", 3).setDescription("number of lightning strikes attempts each time");
  public static Property<Integer> PROP_FIRE_RESISTANCE = new Property<>("fire_resistance", 2).setDescription("the level of fire resistance given entities for the duration");
  public static Property<Float> PROP_LIGHTNING_DAMAGE = new Property<>("lightning_damage", 2.5f).setDescription("the amount of damage done when struck while in the storm");
  public static Property<Integer> PROP_HEAL_INTERVAL = new Property<>("heal_interval", 5 * 20).setDescription("the interval between each heal");
  public static Property<Float> PROP_HEAL_AMOUNT = new Property<>("heal_amount", 1f).setDescription("how much healing should be done");
  public static Property<Float> PROP_POISON_DAMAGE = new Property<>("poison_damage", 1f).setDescription("how much raw poison damage should be dealt to entities");
  public static Property<Integer> PROP_POISON_DURATION = new Property<>("poison_duration", 4 * 20).setDescription("the duration of the poison effect when applied to creatures");
  public static Property<Integer> PROP_POISON_AMPLIFIER = new Property<>("poison_amplifier", 0).setDescription("the amplifier that should be used for the poison effect");
  public static Property<Integer> PROP_PILLAR_DISTANCE = new Property<>("pillar_distance", 6).setDescription("how far away entities can be affected by pillars");
  public static Property<Float> PROP_PILLAR_DAMAGE = new Property<>("pillar_damage", 2.5f).setDescription("how much damage pillars should do to an entity");
  public static Property<Float> PROP_PILLAR_KNOCKUP = new Property<>("pillar_knockup", 6f).setDescription("the velocity at which entities should be knocked up");

  public static Modifier RADIUS = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "spread"), ModifierCores.PERESKIA, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.PERESKIA, 1)));
  public static Modifier PEACEFUL = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "peaceful_storm"), ModifierCores.WILDEWHEET, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.WILDEWHEET, 1)));
  public static Modifier MAGNETISM = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "magnetic_storm"), ModifierCores.WILDROOT, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.WILDROOT, 1)));
  public static Modifier LIGHTNING = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "lightning_strikes"), ModifierCores.MOONGLOW_LEAF, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.MOONGLOW_LEAF, 1)));
  // TODO: Update documentation
  public static Modifier JOLT = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "reactive_jolt"), ModifierCores.SPIRIT_HERB, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.SPIRIT_HERB, 1)));
  public static Modifier HEALING = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "healing_rain"), ModifierCores.TERRA_MOSS, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.TERRA_MOSS, 1)));
  public static Modifier POISON = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "poison_storm"), ModifierCores.BAFFLE_CAP, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.BAFFLE_CAP, 1)));
  public static Modifier OBSIDIAN = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "ignification"), ModifierCores.INFERNAL_BULB, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.INFERNAL_BULB, 1)));
  public static Modifier EARTHQUAKE = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "earthquake"), ModifierCores.STALICRIPE, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.STALICRIPE, 1)));
  public static Modifier ICE = ModifierRegistry.register(new Modifier(new ResourceLocation(Roots.MODID, "freezing_rain"), ModifierCores.DEWGONIA, ModifierCost.of(CostType.ADDITIONAL_COST, ModifierCores.DEWGONIA, 1)));

  public static ResourceLocation spellName = new ResourceLocation(Roots.MODID, "spell_storm_cloud");
  public static SpellStormCloud instance = new SpellStormCloud(spellName);

  public float lightning_damage, heal_amount, poison_damage, pillar_damage, pillar_knockup;
  public int radius, duration, fire_resistance, lightning_strikes, lightning_chance, radius_extended, heal_interval, poison_duration, poison_amplifier, pillar_distance;

  public SpellStormCloud(ResourceLocation name) {
    super(name, TextFormatting.DARK_AQUA, 22f / 255f, 142f / 255f, 255f / 255f, 255f / 255f, 255f / 255f, 255f / 255f);
    properties.addProperties(PROP_COOLDOWN, PROP_CAST_TYPE, PROP_COST_1, PROP_DURATION, PROP_RADIUS, PROP_RADIUS_EXTENDED, PROP_LIGHTNING_CHANCE, PROP_LIGHTNING_DAMAGE, PROP_LIGHTNING_STRIKES, PROP_FIRE_RESISTANCE, PROP_HEAL_AMOUNT, PROP_HEAL_INTERVAL, PROP_POISON_AMPLIFIER, PROP_POISON_DURATION, PROP_POISON_DAMAGE, PROP_PILLAR_DAMAGE, PROP_PILLAR_DISTANCE, PROP_PILLAR_KNOCKUP);
    acceptsModifiers(RADIUS, PEACEFUL, MAGNETISM, LIGHTNING, JOLT, HEALING, POISON, OBSIDIAN, EARTHQUAKE, ICE);
  }

  @Override
  public void init() {
    addIngredients(
        new ItemStack(ModItems.dewgonia),
        new ItemStack(Item.getItemFromBlock(Blocks.SNOW)),
        new ItemStack(Item.getItemFromBlock(Blocks.SNOW)),
        new ItemStack(Items.SNOWBALL),
        new ItemStack(Item.getItemFromBlock(Blocks.RED_FLOWER), 1, BlockFlower.EnumFlowerType.BLUE_ORCHID.getMeta())
    );
  }

  @Override
  public boolean cast(EntityPlayer player, StaffModifierInstanceList info, int ticks) {
    World world = player.world;
    if (!world.isRemote) {
      player.addPotionEffect(new PotionEffect(ModPotions.freeze, info.ampInt(duration), 0, false, false));
      player.getEntityData().setIntArray(getCachedName(), info.snapshot());
      world.playSound(null, player.getPosition(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS, 0.3f, 2f);
    }
    return true;
  }

  @Override
  public void doFinalise() {
    this.castType = properties.get(PROP_CAST_TYPE);
    this.cooldown = properties.get(PROP_COOLDOWN);
    this.duration = properties.get(PROP_DURATION);
    this.radius = properties.get(PROP_RADIUS);
    this.fire_resistance = properties.get(PROP_FIRE_RESISTANCE);
    this.lightning_chance = properties.get(PROP_LIGHTNING_CHANCE);
    this.lightning_strikes = properties.get(PROP_LIGHTNING_STRIKES);
    this.radius_extended = properties.get(PROP_RADIUS_EXTENDED);
    this.lightning_damage = properties.get(PROP_LIGHTNING_DAMAGE);
    this.heal_amount = properties.get(PROP_HEAL_AMOUNT);
    this.poison_damage = properties.get(PROP_POISON_DAMAGE);
    this.poison_amplifier = properties.get(PROP_POISON_AMPLIFIER);
    this.poison_duration = properties.get(PROP_POISON_DURATION);
    this.pillar_damage = properties.get(PROP_PILLAR_DAMAGE);
    this.pillar_knockup = properties.get(PROP_PILLAR_KNOCKUP);
    this.pillar_distance = properties.get(PROP_PILLAR_DISTANCE);
    this.heal_interval = properties.get(PROP_HEAL_INTERVAL);
  }
}

