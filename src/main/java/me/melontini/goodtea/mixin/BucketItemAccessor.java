package me.melontini.goodtea.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.item.EntityBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityBucketItem.class)
public interface BucketItemAccessor {
    @Accessor("entityType")
    EntityType<?> getEntityType();
}
