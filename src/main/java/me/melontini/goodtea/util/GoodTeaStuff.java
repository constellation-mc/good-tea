package me.melontini.goodtea.util;

import me.melontini.dark_matter.api.base.util.MathStuff;
import me.melontini.dark_matter.api.content.ContentBuilder;
import me.melontini.dark_matter.api.content.RegistryUtil;
import me.melontini.dark_matter.api.content.interfaces.AnimatedItemGroup;
import me.melontini.dark_matter.api.content.interfaces.DarkMatterEntries;
import me.melontini.dark_matter.api.minecraft.client.util.DrawUtil;
import me.melontini.dark_matter.api.minecraft.data.NbtBuilder;
import me.melontini.goodtea.behaviors.TeaBehavior;
import me.melontini.goodtea.blocks.FilledTeaMugBlock;
import me.melontini.goodtea.blocks.KettleBlock;
import me.melontini.goodtea.blocks.TeaMugBlock;
import me.melontini.goodtea.blocks.entity.FilledTeaMugBlockEntity;
import me.melontini.goodtea.blocks.entity.KettleBlockEntity;
import me.melontini.goodtea.items.TeaMugItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static me.melontini.goodtea.GoodTea.MODID;

public class GoodTeaStuff {

    public static EntityAttributeModifier OBSIDIAN_TOUGHNESS = new EntityAttributeModifier(UUID.fromString("36dae011-70d8-482a-b3b3-7bb12c871eae"), "Tea Modifier", 2, EntityAttributeModifier.Operation.ADDITION);
    public static EntityAttributeModifier RABBITS_LUCK = new EntityAttributeModifier(UUID.fromString("57c5033e-c071-4b23-8f14-0551eb4c5b0a"), "Tea Modifier", 1, EntityAttributeModifier.Operation.ADDITION);

    public static TagKey<Block> SHOW_SUPPORT = TagKey.of(Registry.BLOCK_KEY, new Identifier(MODID, "gt_kettle_show_support"));
    public static TagKey<Block> HOT_BLOCKS = TagKey.of(Registry.BLOCK_KEY, new Identifier(MODID, "gt_hot_blocks"));

    public static TeaMugBlock TEA_MUG_BLOCK = ContentBuilder.BlockBuilder.create(new Identifier(MODID, "mug"), () -> new TeaMugBlock(AbstractBlock.Settings.of(Material.DECORATION, MapColor.PALE_YELLOW).nonOpaque().strength(0.1F).sounds(BlockSoundGroup.CANDLE)))
            .item((block, identifier) -> ContentBuilder.ItemBuilder.create(identifier, () -> new BlockItem(block, new Item.Settings().maxCount(16))).itemGroup(ItemGroup.MISC)).build();
    public static BlockItem TEA_MUG = RegistryUtil.asItem(TEA_MUG_BLOCK);
    public static KettleBlock KETTLE_BLOCK = ContentBuilder.BlockBuilder.create(new Identifier(MODID, "kettle"), () -> new KettleBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.STONE_GRAY).requiresTool().strength(2f).nonOpaque()))
            .item((block, identifier) -> ContentBuilder.ItemBuilder.create(identifier, () -> new BlockItem(block, new Item.Settings())).itemGroup(ItemGroup.DECORATIONS))
            .blockEntity((block, identifier) -> ContentBuilder.BlockEntityBuilder.create(identifier, KettleBlockEntity::new, block)).build();
    public static BlockItem KETTLE_BLOCK_ITEM = RegistryUtil.asItem(KETTLE_BLOCK);
    public static BlockEntityType<KettleBlockEntity> KETTLE_BLOCK_ENTITY = RegistryUtil.getBlockEntityFromBlock(KETTLE_BLOCK);
    public static FilledTeaMugBlock FILLED_TEA_MUG_BLOCK = ContentBuilder.BlockBuilder.create(new Identifier(MODID, "filled_mug"), () -> new FilledTeaMugBlock(AbstractBlock.Settings.of(Material.DECORATION, MapColor.PALE_YELLOW).sounds(BlockSoundGroup.CANDLE).strength(0.1f).nonOpaque()))
            .item((block, identifier) -> ContentBuilder.ItemBuilder.create(identifier, () -> new TeaMugItem(block, new Item.Settings().maxCount(16).rarity(Rarity.RARE).recipeRemainder(TEA_MUG))))
            .blockEntity((block, identifier) -> ContentBuilder.BlockEntityBuilder.create(identifier, FilledTeaMugBlockEntity::new, block)).build();
    public static TeaMugItem TEA_MUG_FILLED = RegistryUtil.asItem(FILLED_TEA_MUG_BLOCK);
    public static BlockEntityType<FilledTeaMugBlockEntity> FILLED_TEA_MUG_BLOCK_ENTITY = RegistryUtil.getBlockEntityFromBlock(FILLED_TEA_MUG_BLOCK);
    public static final ItemStack KETTLE = KETTLE_BLOCK_ITEM.getDefaultStack();
    public static final ItemStack MUG = TEA_MUG.getDefaultStack();

    public static ItemGroup GROUP = ContentBuilder.ItemGroupBuilder.create(new Identifier(MODID, "item_group"))
            .entries(stacks -> {
                List<ItemStack> teaStarterPack = new ArrayList<>();
                teaStarterPack.add(KETTLE);
                teaStarterPack.add(MUG);

                teaStarterPack.add(ItemStack.EMPTY);

                teaStarterPack.add(Items.CAMPFIRE.getDefaultStack());
                teaStarterPack.add(Items.SOUL_CAMPFIRE.getDefaultStack());
                teaStarterPack.add(Items.LAVA_BUCKET.getDefaultStack());
                appendStacks(stacks, teaStarterPack, true);

                var help = DefaultedList.<ItemStack>of();
                var list = TeaBehavior.INSTANCE.TEA_BEHAVIOR.keySet();
                for (Item item : list) {
                    item.appendStacks(ItemGroup.SEARCH, help);

                    for (ItemStack stack : help) {
                        var mug = TEA_MUG_FILLED.getDefaultStack();
                        mug.setNbt(NbtBuilder.create().put("GT-TeaItem", stack.writeNbt(new NbtCompound())).build());
                        stacks.add(mug, DarkMatterEntries.Visibility.TAB);
                        stacks.add(stack, DarkMatterEntries.Visibility.TAB);
                        stacks.add(ItemStack.EMPTY, DarkMatterEntries.Visibility.TAB);
                    }
                    help.clear();
                }
            }).icon(KETTLE).build();

    public static void init() {
    }

    private static void appendStacks(DarkMatterEntries entries, Collection<ItemStack> list, boolean lineBreak) {
        if (list == null || list.isEmpty()) return; //we shouldn't add line breaks if there are no items.

        int rows = MathStuff.fastCeil(list.size() / 9d);
        entries.addAll(list, DarkMatterEntries.Visibility.TAB);
        int left = (rows * 9) - list.size();
        for (int i = 0; i < left; i++) {
            entries.add(ItemStack.EMPTY, DarkMatterEntries.Visibility.TAB); //fill the gaps
        }
        if (lineBreak) entries.addAll(DefaultedList.ofSize(9, ItemStack.EMPTY), DarkMatterEntries.Visibility.TAB); //line break
    }

}
