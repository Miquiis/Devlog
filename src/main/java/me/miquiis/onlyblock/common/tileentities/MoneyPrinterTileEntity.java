package me.miquiis.onlyblock.common.tileentities;

import me.miquiis.onlyblock.common.registries.TileEntityRegister;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class MoneyPrinterTileEntity extends TileEntity implements IAnimatable, ITickableTileEntity {

    private final AnimationFactory factory = new AnimationFactory(this);
    private PrinterState currentState;
    private int lastPrintTick;

    public MoneyPrinterTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        currentState = PrinterState.IDLE;
        lastPrintTick = 0;
    }

    public MoneyPrinterTileEntity()
    {
        super(TileEntityRegister.MONEY_PRINTER_TILE_ENTITY.get());
        currentState = PrinterState.IDLE;
        lastPrintTick = 0;
    }

    @Override
    public void tick() {
        if (lastPrintTick == 0)
        {
            currentState = PrinterState.PRINTING;
        }

        if (lastPrintTick >= 20)
        {
            lastPrintTick = 0;
        }

        lastPrintTick++;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        switch (currentState)
        {
            case IDLE:
            {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", true));
                break;
            }
            case PRINTING:
            {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("print", true));
                break;
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    private enum PrinterState {
        IDLE,
        PRINTING
    }
}
