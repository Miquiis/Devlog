package me.miquiis.onlyblock.common.classes;

import me.miquiis.onlyblock.common.capability.CurrencyCapability;
import me.miquiis.onlyblock.common.capability.interfaces.ICurrency;
import me.miquiis.onlyblock.common.entities.SedanEntity;
import me.miquiis.onlyblock.common.entities.SedanTwoEntity;
import me.miquiis.onlyblock.common.entities.VanEntity;
import me.miquiis.onlyblock.common.entities.VanTwoEntity;
import me.miquiis.onlyblock.common.registries.BlockRegister;
import me.miquiis.onlyblock.common.registries.ItemRegister;
import me.miquiis.onlyblock.common.registries.SoundRegister;
import me.miquiis.onlyblock.common.utils.MathUtils;
import me.miquiis.onlyblock.common.utils.WorldEditUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AmazonIsland implements IUnlockable {

    private static final List<Vector3d> POSSIBLE_DELIVERIES = new ArrayList<>(Arrays.asList(
            new Vector3d(1000, 66, 947),
            new Vector3d(977, 66, 1009),
            new Vector3d(977, 66, 1048),
            new Vector3d(1027, 66, 1048),
            new Vector3d(1050, 66, 1020),
            new Vector3d(1024, 66, 978),
            new Vector3d(1028, 66, 947),
            new Vector3d(1050, 66, 970),
            new Vector3d(957, 66, 977),
            new Vector3d(950, 66, 947),
            new Vector3d(926, 66, 982),
            new Vector3d(949, 66, 1048),
            new Vector3d(926, 66, 1009),
            new Vector3d(1023, 66, 1024),
            new Vector3d(926, 66, 1032)
    ));

    private static final List<Vector3d> CAR_SPAWNS = new ArrayList<>(Arrays.asList(
            new Vector3d(997, 66, 974),
            new Vector3d(997, 66, 1021),
            new Vector3d(1003, 66, 974),
            new Vector3d(1003, 66, 1021),
            new Vector3d(1023, 66, 961),
            new Vector3d(1000, 66, 955),
            new Vector3d(976, 66, 961),
            new Vector3d(1000, 66, 955),
            new Vector3d(976, 66, 961),
            new Vector3d(945, 66, 955),
            new Vector3d(931, 66, 980),
            new Vector3d(937, 66, 966),
            new Vector3d(937, 66, 1003),
            new Vector3d(931, 66, 1032),
            new Vector3d(950, 66, 1035),
            new Vector3d(977, 66, 1041),
            new Vector3d(1000, 66, 1036),
            new Vector3d(1020, 66, 1041),
            new Vector3d(1039, 66, 1029),
            new Vector3d(1045, 66, 1002),
            new Vector3d(1039, 66, 968),
            new Vector3d(1037, 66, 955)
    ));

    private static final List<Integer> PROGRESSIVE_PRICE = new ArrayList<>(Arrays.asList(
            5000,
            15000,
            30000,
            50000,
            100000,
            250000,
            350000,
            500000,
            1000000,
            1000000000
    ));

    private final Vector3d CENTER_ISLAND = new Vector3d(176, 100, 0);

    private final long TIME_MAX = 60 * 20 * 3;

    private Vector3d currentDelivery;
    private boolean isLocked;
    private int currentPackage;
    private long currentTime;

    private List<Entity> spawnedCars;

    public AmazonIsland()
    {
        currentPackage = 0;
        currentTime = 0;
        currentDelivery = null;
        spawnedCars = new ArrayList<>();
    }

    public void startMinigame(PlayerEntity player, World world)
    {
        currentPackage = 0;
        currentTime = 0;
        currentDelivery = POSSIBLE_DELIVERIES.get(0);
        player.inventory.addItemStackToInventory(createNextPackage());
        player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1f);
        spawnCars(world);
    }

    private void spawnCars(World world)
    {
        if (spawnedCars != null) spawnedCars.forEach(Entity::remove);
        spawnedCars = new ArrayList<>();
        CAR_SPAWNS.forEach(vector3d -> {
            if (MathUtils.chance(30))
            {
                if (MathUtils.chance(50))
                {
                    SedanEntity sedanEntity = new SedanEntity(world);
                    sedanEntity.setPosition(vector3d.getX() + 0.5, vector3d.getY() + 0.5, vector3d.getZ() + 0.5);
                    sedanEntity.enablePersistence();
                    world.addEntity(sedanEntity);
                    spawnedCars.add(sedanEntity);
                } else
                {
                    SedanTwoEntity sedanEntity = new SedanTwoEntity(world);
                    sedanEntity.setPosition(vector3d.getX() + 0.5, vector3d.getY() + 0.5, vector3d.getZ() + 0.5);
                    sedanEntity.enablePersistence();
                    world.addEntity(sedanEntity);
                    spawnedCars.add(sedanEntity);
                }
            } else
            {
                if (MathUtils.chance(50))
                {
                    VanEntity sedanEntity = new VanEntity(world);
                    sedanEntity.setPosition(vector3d.getX() + 0.5, vector3d.getY() + 0.5, vector3d.getZ() + 0.5);
                    sedanEntity.enablePersistence();
                    world.addEntity(sedanEntity);
                    spawnedCars.add(sedanEntity);
                } else
                {
                    VanTwoEntity sedanEntity = new VanTwoEntity(world);
                    sedanEntity.setPosition(vector3d.getX() + 0.5, vector3d.getY() + 0.5, vector3d.getZ() + 0.5);
                    sedanEntity.enablePersistence();
                    world.addEntity(sedanEntity);
                    spawnedCars.add(sedanEntity);
                }
            }
        });
    }

    public void reset()
    {
        currentDelivery = POSSIBLE_DELIVERIES.get(0);
        currentPackage = 0;
        currentTime = 0;
        spawnedCars = new ArrayList<>();
    }

    public void deliver(ServerPlayerEntity player)
    {
        if (currentPackage != 9)
        {
            ICurrency currency = player.getCapability(CurrencyCapability.CURRENCY_CAPABILITY).orElse(null);
            currency.addOrSubtractAmount(PROGRESSIVE_PRICE.get(currentPackage));
            player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundRegister.KATCHING.get(), SoundCategory.PLAYERS, 0.5f, 1f);

            currentDelivery = POSSIBLE_DELIVERIES.get(MathUtils.getRandomMax(POSSIBLE_DELIVERIES.size()));
            currentPackage++;
            player.inventory.addItemStackToInventory(createNextPackage());
            player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(player.getAttributeValue(Attributes.MOVEMENT_SPEED) + 0.001);
        } else
        {
            endMinigame(player);
        }
    }

    private void endMinigame(PlayerEntity player)
    {
        player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1f);
        currentDelivery = null;
        currentPackage = 0;
        currentTime = 0;
        spawnedCars.forEach(entity -> {
            if (entity == null) return;
            entity.remove();
        });
    }

    public Vector3d getRandomTNTLocation()
    {
        double randomX = MathUtils.getRandomMinMax(-30d, 30d);
        double randomZ = MathUtils.getRandomMinMax(-80d, 80d);

        if (randomX < 0) randomX-= 10;
        if (randomX > 0) randomX+= 10;
        if (randomZ < 0) randomZ-= 20;
        if (randomZ > 0) randomZ+= 20;

        return CENTER_ISLAND.add(randomX, MathUtils.getRandomMinMax(0, 30), randomZ);
    }

    public Vector3d getCurrentDelivery() {
        return currentDelivery;
    }

    public void deserializeNBT(CompoundNBT compoundNBT)
    {
        if (compoundNBT.contains("DeliverX"))
        {
            currentDelivery = new Vector3d(compoundNBT.getDouble("DeliverX"), compoundNBT.getDouble("DeliverY"), compoundNBT.getDouble("DeliverZ"));
        } else
        {
            currentDelivery = null;
        }
        if (compoundNBT.contains("IsLocked"))
        {
            isLocked = compoundNBT.getBoolean("IsLocked");
        }
        if (compoundNBT.contains("CurrentPackage"))
        {
            currentPackage = compoundNBT.getInt("CurrentPackage");
        } else currentPackage = 0;
        if (compoundNBT.contains("CurrentTime"))
        {
            currentTime = compoundNBT.getLong("CurrentTime");
        } else currentTime = 0;
    }

    private ItemStack createNextPackage()
    {
        ItemStack packageItem = new ItemStack(BlockRegister.AMAZON_PACKAGE.get().asItem(), 1);
        packageItem.setDisplayName(new StringTextComponent("\u00A76\u00A7lPackage: \u00A7a\u00A7l$" + PROGRESSIVE_PRICE.get(currentPackage)));
        return packageItem;
    }

    public void tickTime(PlayerEntity player)
    {
        if (currentTime < TIME_MAX)
        {
            currentTime++;
        } else
        {
            endMinigame(player);
        }
    }

    public float getPercentage()
    {
        return 1.0f - (currentTime / (float)TIME_MAX);
    }

    public CompoundNBT serializeNBT()
    {
        CompoundNBT compoundNBT = new CompoundNBT();
        if (getCurrentDelivery() != null){
            compoundNBT.putDouble("DeliverX", getCurrentDelivery().getX());
            compoundNBT.putDouble("DeliverY", getCurrentDelivery().getY());
            compoundNBT.putDouble("DeliverZ", getCurrentDelivery().getZ());
        }
        compoundNBT.putInt("CurrentPackage", currentPackage);
        compoundNBT.putBoolean("IsLocked", isLocked);
        compoundNBT.putLong("CurrentTime", currentTime);
        return compoundNBT;
    }

    @Override
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public void unlock(World world) {
        isLocked = false;
        WorldEditUtils.pasteSchematic("amazon_no_entity", world, 185.48, 68.00, -0.53);
    }

    @Override
    public void lock(World world) {
        isLocked = true;
        WorldEditUtils.pasteSchematic("b_amazon_no_entity", world, 185.48, 68.00, -0.53);
    }
}
