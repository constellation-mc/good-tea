package me.melontini.goodtea;

import com.unascribed.kahur.api.KahurImpactBehavior;
import me.melontini.goodtea.behaviors.KettleBlockBehaviour;
import me.melontini.goodtea.behaviors.TeaCupBehavior;
import me.melontini.goodtea.blocks.KettleBlock;
import me.melontini.goodtea.blocks.entity.KettleBlockEntity;
import me.melontini.goodtea.items.TeaCupItem;
import me.melontini.goodtea.screens.KettleScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class GoodTea implements ModInitializer {
    public static final String MODID = "good-tea";
    public static final EntityAttributeModifier OBSIDIAN_TOUGHNESS = new EntityAttributeModifier(UUID.fromString("36dae011-70d8-482a-b3b3-7bb12c871eae"), "Tea Modifier", 2, EntityAttributeModifier.Operation.ADDITION);
    public static final EntityAttributeModifier RABBITS_LUCK = new EntityAttributeModifier(UUID.fromString("57c5033e-c071-4b23-8f14-0551eb4c5b0a"), "Tea Modifier", 1, EntityAttributeModifier.Operation.ADDITION);
    public static final TagKey<Block> SHOW_SUPPORT = TagKey.of(Registry.BLOCK_KEY, new Identifier(MODID, "gt_kettle_show_support"));
    public static final TagKey<Block> HOT_BLOCKS = TagKey.of(Registry.BLOCK_KEY, new Identifier(MODID, "gt_hot_blocks"));
    public static final Item TEA_CUP = new Item(new FabricItemSettings().group(ItemGroup.MISC).maxCount(16));
    public static final TeaCupItem TEA_CUP_FILLED = new TeaCupItem(new FabricItemSettings().maxCount(16).rarity(Rarity.RARE).recipeRemainder(TEA_CUP));
    public static final KettleBlock KETTLE_BLOCK = new KettleBlock(FabricBlockSettings.of(Material.METAL));
    public static final BlockItem KETTLE_BLOCK_ITEM = new BlockItem(KETTLE_BLOCK, new FabricItemSettings().group(ItemGroup.DECORATIONS));
    public static final BlockEntityType<KettleBlockEntity> KETTLE_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(KettleBlockEntity::new, KETTLE_BLOCK).build();
    public static ScreenHandlerType<KettleScreenHandler> KETTLE_SCREEN_HANDLER = new ScreenHandlerType<>(KettleScreenHandler::new);

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(MODID, "cup"), TEA_CUP);
        Registry.register(Registry.ITEM, new Identifier(MODID, "filled_cup"), TEA_CUP_FILLED);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "kettle"), KETTLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(MODID, "kettle"), KETTLE_BLOCK_ITEM);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "kettle_block_entity"), KETTLE_BLOCK_ENTITY);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(MODID, "kettle"), KETTLE_SCREEN_HANDLER);

        TeaCupBehavior.INSTANCE.addDefaultBehaviours();
        TeaCupBehavior.INSTANCE.addDefaultTooltips();
        KettleBlockBehaviour.INSTANCE.addDefaultBlocks();

        FluidStorage.SIDED.registerForBlockEntity((kettle, direction) -> kettle.waterStorage, KETTLE_BLOCK_ENTITY);

        if (FabricLoader.getInstance().isModLoaded("kahur")) {
            KahurImpactBehavior.register((kahurShotEntity, itemStack, hitResult) -> {
                if (hitResult.getType() == HitResult.Type.ENTITY) {
                    if (((EntityHitResult) hitResult).getEntity() instanceof LivingEntity livingEntity) {
                        NbtCompound nbt = itemStack.getNbt();
                        ItemStack stack1 = TeaCupItem.getStackFromNbt(nbt);
                        if (stack1 != null) {
                            TeaCupBehavior.INSTANCE.getBehavior(stack1).run(livingEntity, stack1);
                        }
                    }
                } else if (hitResult.getType() == HitResult.Type.BLOCK){
                    Vec3d pos = hitResult.getPos();
                    List<LivingEntity> livingEntities = kahurShotEntity.world.getEntitiesByClass(LivingEntity.class, new Box(((BlockHitResult)hitResult).getBlockPos()).expand(1.5), LivingEntity::isAlive);
                    Optional<LivingEntity> winner = livingEntities.stream().min(Comparator.comparingDouble(livingEntity -> livingEntity.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())));
                    if (winner.isPresent()) {
                        NbtCompound nbt = itemStack.getNbt();
                        ItemStack stack1 = TeaCupItem.getStackFromNbt(nbt);
                        if (stack1 != null) {
                            TeaCupBehavior.INSTANCE.getBehavior(stack1).run(winner.get(), stack1);
                        }
                    }
                }
                return KahurImpactBehavior.ImpactResult.destroy(true);
            }, TEA_CUP_FILLED);
        }
    }
}
