package me.miquiis.onlyblock.common.registries;

import me.miquiis.onlyblock.OnlyBlock;
import me.miquiis.onlyblock.common.items.*;
import me.miquiis.onlyblock.common.items.renderer.GoldenHelicopterItemRenderer;
import me.miquiis.onlyblock.common.items.renderer.JetpackItemRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegister {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OnlyBlock.MOD_ID);

    public static final RegistryObject<Item> CASH = ITEMS.register("cash", () ->
            new CashItem(new Item.Properties().group(ItemGroup.MISC).food(Foods.APPLE))
    );

    public static final RegistryObject<Item> BAD_STOCK = ITEMS.register("bad_stock", () ->
            new Item(new Item.Properties().group(ItemGroup.MISC))
    );

    public static final RegistryObject<Item> GOOD_STOCK = ITEMS.register("good_stock", () ->
            new Item(new Item.Properties().group(ItemGroup.MISC))
    );

    public static final RegistryObject<JetpackArmorItem> JETPACK = ITEMS.register("jetpack",
            () -> new JetpackArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.COMBAT).setISTER(() -> JetpackItemRenderer::new)));


    public static final RegistryObject<Item> GHAST_WHISTLE = ITEMS.register("whistle", () ->
            new WhistleItem(new Item.Properties().group(ItemGroup.MISC))
    );

    public static final RegistryObject<Item> LAPTOP_PASSWORD = ITEMS.register("laptop_password", () ->
            new LaptopPasscodeItem(new Item.Properties().group(ItemGroup.MISC).rarity(Rarity.EPIC))
    );

    public static final RegistryObject<Item> GOLDEN_BAZOOKA = ITEMS.register("golden_bazooka", () ->
            new GoldenBazooka(new Item.Properties().group(ItemGroup.COMBAT).maxStackSize(1))
    );

    public static final RegistryObject<Item> GOLDEN_HELICOPTER = ITEMS.register("golden_helicopter", () ->
            new GoldenHelicopterItem(new Item.Properties().group(ItemGroup.TRANSPORTATION).maxStackSize(1).setISTER(() -> GoldenHelicopterItemRenderer::new))
    );

    public static final RegistryObject<Item> CASH_SWORD = ITEMS.register("cash_sword", () ->
            new SwordItem(ItemTier.IRON, 5, -1.5F, (new Item.Properties()).group(ItemGroup.TOOLS))
    );

    public static final RegistryObject<Item> CASH_PICKAXE = ITEMS.register("cash_pickaxe", () ->
            new CashPickaxe(ItemTier.IRON, 3, -2.0F, (new Item.Properties()).group(ItemGroup.TOOLS))
    );

    public static final RegistryObject<Item> CRYPTO_PICKAXE = ITEMS.register("crypto_pickaxe", () ->
            new CashPickaxe(ItemTier.IRON, 3, -2.0F, (new Item.Properties()).group(ItemGroup.TOOLS))
    );

    public static final RegistryObject<Item> DEBIT_CARD_AXE = ITEMS.register("debit_card_axe", () ->
            new AxeItem(ItemTier.IRON, 5.0F, -2.6F, (new Item.Properties()).group(ItemGroup.TOOLS))
    );

    public static final RegistryObject<Item> STOCK_SWORD = ITEMS.register("stock_sword", () ->
            new SwordItem(ItemTier.DIAMOND, 6, -1.0F, (new Item.Properties()).group(ItemGroup.TOOLS))
    );

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
    }

}
