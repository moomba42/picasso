package com.alexdl.picasso.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.alexdl.picasso.Utils.addRandomSpreadMovement;

@Mixin(Horse.class)
public abstract class HorseEntityMixin extends AbstractHorse implements VariantHolder<Variant> {
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_ID_SHEARABLE = SynchedEntityData.defineId(Horse.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private int picasso$unshearableTimeLeft = 0;

    protected HorseEntityMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    public void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getItemInHand(hand).is(Items.SHEARS) && !this.isBaby() && this.isAlive()) {
            if (!this.entityData.get(DATA_ID_SHEARABLE)) {
                this.makeMad();
                cir.setReturnValue(InteractionResult.CONSUME);
                return;
            }

            player.playSound(SoundEvents.SHEEP_SHEAR, 1.0f, 1.0f);
            player.playSound(SoundEvents.HORSE_BREATHE, 0.5f, 1.0f + (random.nextFloat() * 0.2f));
            ItemStack horsehair = new ItemStack(Items.LEATHER, this.getRandom().nextInt(4) == 1 ? 2 : 1);
            ItemEntity horsehairEntity = this.spawnAtLocation(horsehair);
            if (horsehairEntity != null) {
                addRandomSpreadMovement(horsehairEntity, this.getRandom());
            }


            if (!this.level().isClientSide()) {
                // this.picasso$unshearableTimeLeft = 1000 + random.nextInt(200); // max 1 minute wait time
                this.picasso$unshearableTimeLeft = 100; // 5 seconds
                this.entityData.set(DATA_ID_SHEARABLE, Boolean.FALSE);
            }

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    protected void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_ID_SHEARABLE, Boolean.FALSE);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (picasso$unshearableTimeLeft > 0) {
                picasso$unshearableTimeLeft--;
            }

            if (picasso$unshearableTimeLeft == 0) {
                this.entityData.set(DATA_ID_SHEARABLE, Boolean.TRUE);
            }
        }
    }
}
