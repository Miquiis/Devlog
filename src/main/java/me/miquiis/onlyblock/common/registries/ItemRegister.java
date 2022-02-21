package me.miquiis.onlyblock.common.registries;

import me.miquiis.onlyblock.OnlyBlock;
import me.miquiis.onlyblock.common.items.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;

public class ItemRegister {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OnlyBlock.MOD_ID);

    public static final RegistryObject<Item> AIRDROP_CALLER = ITEMS.register("airdrop_caller", () ->
            new Item(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1).food(new Food.Builder().fastToEat().setAlwaysEdible().build()))
    );

    public static final RegistryObject<Item> GOLDEN_WRAP = ITEMS.register("golden_wrap", () ->
            new Item(new Item.Properties().group(ItemGroup.MISC))
    );

    public static final RegistryObject<Item> WRAPPED_COOKED_BEEF = ITEMS.register("wrapped_cooked_beef", () ->
            new Item(new Item.Properties().food(new Food.Builder().hunger(8).saturation(0.6F).effect(() -> new EffectInstance(EffectRegister.MONEY_BOOST.get(), 3600, 1), 1.0f).setAlwaysEdible().build()).group(ItemGroup.FOOD))
    );

    public static final RegistryObject<Item> WRAPPED_APPLE = ITEMS.register("wrapped_apple", () ->
            new Item(new Item.Properties().food(new Food.Builder().hunger(5).saturation(0.4F).effect(new EffectInstance(Effects.REGENERATION, 400, 1), 1.0F).effect(new EffectInstance(Effects.RESISTANCE, 6000, 0), 1.0F).effect(new EffectInstance(Effects.FIRE_RESISTANCE, 6000, 0), 1.0F).effect(new EffectInstance(Effects.ABSORPTION, 2400, 3), 1.0F).effect(() -> new EffectInstance(EffectRegister.MONEY_BOOST.get(), 3600, 0), 1.0f).setAlwaysEdible().build()).group(ItemGroup.FOOD))
    );

    public static final RegistryObject<Item> GOLDEN_COOKED_BEEF = ITEMS.register("golden_cooked_beef", () ->
            new Item(new Item.Properties().food(new Food.Builder().hunger(20).saturation(0.6F).effect(() -> new EffectInstance(EffectRegister.MONEY_BOOST.get(), 3600, 2), 1.0f).setAlwaysEdible().build()).group(ItemGroup.FOOD))
    );

    public static final RegistryObject<Item> XP_SHEEP_EGG = ITEMS.register("xp_sheep_egg", () ->
            new ModSpawnEgg(EntityRegister.XP_SHEEP, 61793, 16777215, (new Item.Properties()).group(ItemGroup.MISC))
    );

    public static final RegistryObject<Item> XP_COW_EGG = ITEMS.register("xp_cow_egg", () ->
            new ModSpawnEgg(EntityRegister.XP_COW, 61793, 7295287, (new Item.Properties()).group(ItemGroup.MISC))
    );

    public static final RegistryObject<Item> XP_CHICKEN_EGG = ITEMS.register("xp_chicken_egg", () ->
            new ModSpawnEgg(EntityRegister.XP_CHICKEN, 61793, 16730420, (new Item.Properties()).group(ItemGroup.MISC))
    );

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
    }

}
