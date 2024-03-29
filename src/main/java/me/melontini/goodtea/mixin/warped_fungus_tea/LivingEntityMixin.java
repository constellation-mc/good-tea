package me.melontini.goodtea.mixin.warped_fungus_tea;

import me.melontini.goodtea.ducks.HoglinRepellentAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements HoglinRepellentAccess {
    @Unique
    private int good_tea$hoglinRepellent = 0;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void good_tea$tick(CallbackInfo ci) {
        if (!world.isClient()) if (this.good_tea$hoglinRepellent > 0) this.good_tea$hoglinRepellent--;
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    private void good_tea$readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("GT-HoglinRepellent")) this.good_tea$hoglinRepellent = nbt.getInt("GT-HoglinRepellent");
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    private void good_tea$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        if (this.good_tea$hoglinRepellent > 0) nbt.putInt("GT-HoglinRepellent", this.good_tea$hoglinRepellent);
    }

    @Unique
    @Override
    public boolean good_tea$isHoglinRepellent() {
        return this.good_tea$hoglinRepellent > 0;
    }

    @Unique
    @Override
    public void good_tea$makeHoglinRepellent(int timeInTicks) {
        this.good_tea$hoglinRepellent = timeInTicks;
    }
}
