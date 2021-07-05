package me.hypherionmc.storagedrawers.client.model;

import com.google.common.collect.Lists;
import me.hypherionmc.storagedrawers.StorageDrawers;
import me.hypherionmc.storagedrawers.api.storage.IDrawerAttributes;
import me.hypherionmc.storagedrawers.api.storage.attribute.LockAttribute;
import me.hypherionmc.storagedrawers.block.BlockDrawers;
import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawers;
import me.hypherionmc.storagedrawers.core.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.Direction;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class BasicDrawerModel
{
    private static final Map<Direction, BakedModel> lockOverlaysFull = new HashMap<>();
    private static final Map<Direction, BakedModel> lockOverlaysHalf = new HashMap<>();

    private static boolean geometryDataLoaded = false;

    @Mod.EventBusSubscriber(modid = StorageDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Register // extends DefaultRegister
    {
        @SubscribeEvent
        public static void registerTextures (TextureStitchEvent.Pre event) {
            //if (event.getMap() != Minecraft.getInstance().getTextureMap())
            //    return;

            if (ModBlocks.OAK_FULL_DRAWERS_1 == null) {
                StorageDrawers.log.warn("Block objects not set in TextureStitchEvent.  Is your mod environment broken?");
                return;
            }

            BlockModel unbakedModel = getBlockModel(new ResourceLocation(StorageDrawers.MOD_ID, "models/block/full_drawers_lock.json"));

            for (Either<RenderMaterial, String> x : unbakedModel.textures.values()) {
                x.ifLeft((value) -> {
                    if (value.getAtlasLocation().equals(event.getMap().getTextureLocation()))
                        event.addSprite(value.getTextureLocation());
                });
                // x.ifRight((value) -> event.addSprite(new ResourceLocation(value)));
            }

            loadGeometryData();
        }

        private static void loadGeometryData () {
            if (geometryDataLoaded)
                return;

            geometryDataLoaded = true;

            populateGeometryData(new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/full_drawers_icon_area_1.json"),
                new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/full_drawers_count_area_1.json"),
                ModBlocks.OAK_FULL_DRAWERS_1,
                ModBlocks.SPRUCE_FULL_DRAWERS_1,
                ModBlocks.BIRCH_FULL_DRAWERS_1,
                ModBlocks.JUNGLE_FULL_DRAWERS_1,
                ModBlocks.ACACIA_FULL_DRAWERS_1,
                ModBlocks.DARK_OAK_FULL_DRAWERS_1);
            populateGeometryData(new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/full_drawers_icon_area_2.json"),
                new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/full_drawers_count_area_2.json"),
                ModBlocks.OAK_FULL_DRAWERS_2,
                ModBlocks.SPRUCE_FULL_DRAWERS_2,
                ModBlocks.BIRCH_FULL_DRAWERS_2,
                ModBlocks.JUNGLE_FULL_DRAWERS_2,
                ModBlocks.ACACIA_FULL_DRAWERS_2,
                ModBlocks.DARK_OAK_FULL_DRAWERS_2);
            populateGeometryData(new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/full_drawers_icon_area_4.json"),
                new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/full_drawers_count_area_4.json"),
                ModBlocks.OAK_FULL_DRAWERS_4,
                ModBlocks.SPRUCE_FULL_DRAWERS_4,
                ModBlocks.BIRCH_FULL_DRAWERS_4,
                ModBlocks.JUNGLE_FULL_DRAWERS_4,
                ModBlocks.ACACIA_FULL_DRAWERS_4,
                ModBlocks.DARK_OAK_FULL_DRAWERS_4);
            populateGeometryData(new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/half_drawers_icon_area_1.json"),
                new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/half_drawers_count_area_1.json"),
                ModBlocks.OAK_HALF_DRAWERS_1,
                ModBlocks.SPRUCE_HALF_DRAWERS_1,
                ModBlocks.BIRCH_HALF_DRAWERS_1,
                ModBlocks.JUNGLE_HALF_DRAWERS_1,
                ModBlocks.ACACIA_HALF_DRAWERS_1,
                ModBlocks.DARK_OAK_HALF_DRAWERS_1);
            populateGeometryData(new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/half_drawers_icon_area_2.json"),
                new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/half_drawers_count_area_2.json"),
                ModBlocks.OAK_HALF_DRAWERS_2,
                ModBlocks.SPRUCE_HALF_DRAWERS_2,
                ModBlocks.BIRCH_HALF_DRAWERS_2,
                ModBlocks.JUNGLE_HALF_DRAWERS_2,
                ModBlocks.ACACIA_HALF_DRAWERS_2,
                ModBlocks.DARK_OAK_HALF_DRAWERS_2);
            populateGeometryData(new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/half_drawers_icon_area_4.json"),
                new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/half_drawers_count_area_4.json"),
                ModBlocks.OAK_HALF_DRAWERS_4,
                ModBlocks.SPRUCE_HALF_DRAWERS_4,
                ModBlocks.BIRCH_HALF_DRAWERS_4,
                ModBlocks.JUNGLE_HALF_DRAWERS_4,
                ModBlocks.ACACIA_HALF_DRAWERS_4,
                ModBlocks.DARK_OAK_HALF_DRAWERS_4);
            populateGeometryData(new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/comp_drawers_icon_area_3.json"),
                new ResourceLocation(StorageDrawers.MOD_ID, "models/block/geometry/comp_drawers_count_area_3.json"),
                ModBlocks.COMPACTING_DRAWERS_3);
        }

        private static BlockModel getBlockModel (ResourceLocation location) {
            IResource iresource = null;
            Reader reader = null;
            try {
                iresource = Minecraft.getInstance().getResourceManager().getResource(location);
                reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
                return BlockModel.deserialize(reader);
            } catch (IOException e) {
                return null;
            } finally {
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly((Closeable)iresource);
            }
        }

        private static void populateGeometryData(ResourceLocation locationIcon, ResourceLocation locationCount, BlockDrawers... blocks) {
            BlockModel slotInfo = getBlockModel(locationIcon);
            BlockModel countInfo = getBlockModel(locationCount);
            for (BlockDrawers block : blocks) {
                if (block == null)
                    continue;

                for (int i = 0; i < block.getDrawerCount(); i++) {
                    Vector3f from = slotInfo.getElements().get(i).positionFrom;
                    Vector3f to = slotInfo.getElements().get(i).positionTo;
                    block.labelGeometry[i] = new AxisAlignedBB(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
                }
                for (int i = 0; i < block.getDrawerCount(); i++) {
                    Vector3f from = countInfo.getElements().get(i).positionFrom;
                    Vector3f to = countInfo.getElements().get(i).positionTo;
                    block.countGeometry[i] = new AxisAlignedBB(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
                }
            }
        }

        @SubscribeEvent
        public static void registerModels (ModelBakeEvent event) {
            // IUnbakedModel unbaked = event.getModelLoader().getUnbakedModel(new ResourceLocation(StorageDrawers.MOD_ID, "block/full_drawers_lock"));
            lockOverlaysFull.put(Direction.NORTH, event.getModelLoader().getBakedModel(new ResourceLocation(StorageDrawers.MOD_ID, "block/full_drawers_lock"), ModelRotation.X0_Y0, ModelLoader.defaultTextureGetter()));
            lockOverlaysFull.put(Direction.EAST, event.getModelLoader().getBakedModel(new ResourceLocation(StorageDrawers.MOD_ID, "block/full_drawers_lock"), ModelRotation.X0_Y90, ModelLoader.defaultTextureGetter()));
            lockOverlaysFull.put(Direction.SOUTH, event.getModelLoader().getBakedModel(new ResourceLocation(StorageDrawers.MOD_ID, "block/full_drawers_lock"), ModelRotation.X0_Y180, ModelLoader.defaultTextureGetter()));
            lockOverlaysFull.put(Direction.WEST, event.getModelLoader().getBakedModel(new ResourceLocation(StorageDrawers.MOD_ID, "block/full_drawers_lock"), ModelRotation.X0_Y270, ModelLoader.defaultTextureGetter()));
            lockOverlaysHalf.put(Direction.NORTH, event.getModelLoader().getBakedModel(new ResourceLocation(StorageDrawers.MOD_ID, "block/half_drawers_lock"), ModelRotation.X0_Y0, ModelLoader.defaultTextureGetter()));
            lockOverlaysHalf.put(Direction.EAST, event.getModelLoader().getBakedModel(new ResourceLocation(StorageDrawers.MOD_ID, "block/half_drawers_lock"), ModelRotation.X0_Y90, ModelLoader.defaultTextureGetter()));
            lockOverlaysHalf.put(Direction.SOUTH, event.getModelLoader().getBakedModel(new ResourceLocation(StorageDrawers.MOD_ID, "block/half_drawers_lock"), ModelRotation.X0_Y180, ModelLoader.defaultTextureGetter()));
            lockOverlaysHalf.put(Direction.WEST, event.getModelLoader().getBakedModel(new ResourceLocation(StorageDrawers.MOD_ID, "block/half_drawers_lock"), ModelRotation.X0_Y270, ModelLoader.defaultTextureGetter()));

            if (ModBlocks.OAK_FULL_DRAWERS_1 == null) {
                StorageDrawers.log.warn("Block objects not set in ModelBakeEvent.  Is your mod environment broken?");
                return;
            }

            replaceBlock(event, ModBlocks.OAK_FULL_DRAWERS_1);
            replaceBlock(event, ModBlocks.OAK_FULL_DRAWERS_2);
            replaceBlock(event, ModBlocks.OAK_FULL_DRAWERS_4);
            replaceBlock(event, ModBlocks.OAK_HALF_DRAWERS_1);
            replaceBlock(event, ModBlocks.OAK_HALF_DRAWERS_2);
            replaceBlock(event, ModBlocks.OAK_HALF_DRAWERS_4);
            replaceBlock(event, ModBlocks.SPRUCE_FULL_DRAWERS_1);
            replaceBlock(event, ModBlocks.SPRUCE_FULL_DRAWERS_2);
            replaceBlock(event, ModBlocks.SPRUCE_FULL_DRAWERS_4);
            replaceBlock(event, ModBlocks.SPRUCE_HALF_DRAWERS_1);
            replaceBlock(event, ModBlocks.SPRUCE_HALF_DRAWERS_2);
            replaceBlock(event, ModBlocks.SPRUCE_HALF_DRAWERS_4);
            replaceBlock(event, ModBlocks.BIRCH_FULL_DRAWERS_1);
            replaceBlock(event, ModBlocks.BIRCH_FULL_DRAWERS_2);
            replaceBlock(event, ModBlocks.BIRCH_FULL_DRAWERS_4);
            replaceBlock(event, ModBlocks.BIRCH_HALF_DRAWERS_1);
            replaceBlock(event, ModBlocks.BIRCH_HALF_DRAWERS_2);
            replaceBlock(event, ModBlocks.BIRCH_HALF_DRAWERS_4);
            replaceBlock(event, ModBlocks.JUNGLE_FULL_DRAWERS_1);
            replaceBlock(event, ModBlocks.JUNGLE_FULL_DRAWERS_2);
            replaceBlock(event, ModBlocks.JUNGLE_FULL_DRAWERS_4);
            replaceBlock(event, ModBlocks.JUNGLE_HALF_DRAWERS_1);
            replaceBlock(event, ModBlocks.JUNGLE_HALF_DRAWERS_2);
            replaceBlock(event, ModBlocks.JUNGLE_HALF_DRAWERS_4);
            replaceBlock(event, ModBlocks.ACACIA_FULL_DRAWERS_1);
            replaceBlock(event, ModBlocks.ACACIA_FULL_DRAWERS_2);
            replaceBlock(event, ModBlocks.ACACIA_FULL_DRAWERS_4);
            replaceBlock(event, ModBlocks.ACACIA_HALF_DRAWERS_1);
            replaceBlock(event, ModBlocks.ACACIA_HALF_DRAWERS_2);
            replaceBlock(event, ModBlocks.ACACIA_HALF_DRAWERS_4);
            replaceBlock(event, ModBlocks.DARK_OAK_FULL_DRAWERS_1);
            replaceBlock(event, ModBlocks.DARK_OAK_FULL_DRAWERS_2);
            replaceBlock(event, ModBlocks.DARK_OAK_FULL_DRAWERS_4);
            replaceBlock(event, ModBlocks.DARK_OAK_HALF_DRAWERS_1);
            replaceBlock(event, ModBlocks.DARK_OAK_HALF_DRAWERS_2);
            replaceBlock(event, ModBlocks.DARK_OAK_HALF_DRAWERS_4);
            replaceBlock(event, ModBlocks.COMPACTING_DRAWERS_3);

            event.getModelLoader().getBakedModel(new ResourceLocation(StorageDrawers.MOD_ID, "block/full_drawers_lock"), ModelRotation.X0_Y0, ModelLoader.defaultTextureGetter());
        }

        public static void replaceBlock(ModelBakeEvent event, BlockDrawers block) {
            for (BlockState state : block.getStateContainer().getValidStates()) {
                ModelResourceLocation modelResource = BlockModelShapes.getModelLocation(state);
                IBakedModel parentModel = event.getModelManager().getModel(modelResource);
                if (parentModel == null) {
                    StorageDrawers.log.warn("Got back null model from ModelBakeEvent.ModelManager for resource " + modelResource.toString());
                    continue;
                } else if (parentModel == event.getModelManager().getMissingModel())
                    continue;

                if (block.isHalfDepth())
                    event.getModelRegistry().put(modelResource, new Model2.HalfModel(parentModel));
                else
                    event.getModelRegistry().put(modelResource, new Model2.FullModel(parentModel));
            }
        }

        /*public Register () {
            super(ModBlocks.basicDrawers);
        }

        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<>();

            for (EnumBasicDrawer drawer : EnumBasicDrawer.values()) {
                for (EnumFacing dir : EnumFacing.HORIZONTALS) {
                    for (BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values()) {
                        states.add(ModBlocks.basicDrawers.getDefaultState()
                            .withProperty(BlockStandardDrawers.BLOCK, drawer)
                            .withProperty(BlockDrawers.FACING, dir)
                            .withProperty(BlockVariantDrawers.VARIANT, woodType));
                    }
                }
            }

            return states;
        }

        @Override
        public IBakedModel getModel (IBlockState state, IBakedModel existingModel) {
            return new CachedBuilderModel(new Model(existingModel));
        }

        @Override
        public IBakedModel getModel (ItemStack stack, IBakedModel existingModel) {
            return new CachedBuilderModel(new Model(existingModel));
        }

        @Override
        public List<ResourceLocation> getTextureResources () {
            List<ResourceLocation> resource = new ArrayList<>();
            resource.add(DrawerDecoratorModel.iconClaim);
            resource.add(DrawerDecoratorModel.iconClaimLock);
            resource.add(DrawerDecoratorModel.iconLock);
            resource.add(DrawerDecoratorModel.iconShroudCover);
            resource.add(DrawerDecoratorModel.iconVoid);
            resource.add(DrawerSealedModel.iconTapeCover);
            return resource;
        }*/
    }

    public static class MergedModel implements IBakedModel {
        protected final IBakedModel mainModel;
        protected final IBakedModel[] models;

        public MergedModel (IBakedModel mainModel, IBakedModel... models) {
            this.mainModel = mainModel;
            this.models = models;
        }

        @Override
        public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, Random rand) {
            List<BakedQuad> quads = Lists.newArrayList();
            quads.addAll(mainModel.getQuads(state, side, rand));
            for (IBakedModel model : models)
                quads.addAll(model.getQuads(state, side, rand));
            return quads;
        }

        @Override
        public boolean isAmbientOcclusion () {
            return mainModel.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d () {
            return mainModel.isGui3d();
        }

        @Override
        public boolean isSideLit () {
            return mainModel.isSideLit();
        }

        @Override
        public boolean isBuiltInRenderer () {
            return mainModel.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture () {
            return mainModel.getParticleTexture();
        }

        @Override
        public ItemOverrideList getOverrides () {
            return mainModel.getOverrides();
        }
    }

    public static abstract class Model2 implements IDynamicBakedModel {
        protected final IBakedModel mainModel;
        protected final Map<Direction, IBakedModel> lockOverlay;

        public static class FullModel extends Model2 {
            FullModel(IBakedModel mainModel) {
                super(mainModel, lockOverlaysFull);
            }
        }

        public static class HalfModel extends Model2 {
            HalfModel(IBakedModel mainModel) {
                super(mainModel, lockOverlaysHalf);
            }
        }

        private Model2(IBakedModel mainModel, Map<Direction, IBakedModel> lockOverlay) {
            this.mainModel = mainModel;
            this.lockOverlay = lockOverlay;
        }

        @Override
        public boolean isSideLit () {
            return mainModel.isSideLit();
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
            List<BakedQuad> quads = Lists.newArrayList();
            quads.addAll(mainModel.getQuads(state, side, rand));

            if (state != null && extraData.hasProperty(TileEntityDrawers.ATTRIBUTES)) {
                IDrawerAttributes attr = extraData.getData(TileEntityDrawers.ATTRIBUTES);
                Direction dir = state.get(BlockDrawers.HORIZONTAL_FACING);

                if (attr.isItemLocked(LockAttribute.LOCK_EMPTY) || attr.isItemLocked(LockAttribute.LOCK_POPULATED))
                    quads.addAll(lockOverlay.get(dir).getQuads(state, side, rand, extraData));
            }

            return quads;
        }

        @Override
        public boolean isAmbientOcclusion () {
            return mainModel.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d () {
            return mainModel.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer () {
            return mainModel.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture () {
            return mainModel.getParticleTexture();
        }

        @Override
        public ItemOverrideList getOverrides () {
            return mainModel.getOverrides();
        }
    }

    public static class Model extends ProxyBuilderModel
    {
        Direction side;
        Map<Direction, IBakedModel> overlays;

        public Model (IBakedModel parent, Map<Direction, IBakedModel> overlays, Direction side) {
            super(parent);
            this.overlays = overlays;
            this.side = side;
        }

        @Override
        protected IBakedModel buildModel (BlockState state, IBakedModel parent) {
            return new MergedModel(parent, overlays.get(side));
            /*try {
                //EnumBasicDrawer drawer = state.get(BlockStandardDrawers.BLOCK);
                Direction dir = state.get(BlockDrawers.HORIZONTAL_FACING);

                if (!(state instanceof IExtendedBlockState))
                    return new PassLimitedModel(parent, BlockRenderLayer.CUTOUT_MIPPED);

                IExtendedBlockState xstate = (IExtendedBlockState)state;
                DrawerStateModelData stateModel = xstate.getValue(BlockDrawers.STATE_MODEL);

                if (!DrawerDecoratorModel.shouldHandleState(stateModel))
                    return new PassLimitedModel(parent, BlockRenderLayer.CUTOUT_MIPPED);

                return new DrawerDecoratorModel(parent, xstate, drawer, dir, stateModel);
            }
            catch (Throwable t) {
                return new PassLimitedModel(parent, BlockRenderLayer.CUTOUT_MIPPED);
            }*/
        }

        /*@Override
        public ItemOverrideList getOverrides () {
            return itemHandler;
        }*/

        /*@Override
        public List<Object> getKey (BlockState state) {
            try {
                List<Object> key = new ArrayList<Object>();
                IExtendedBlockState xstate = (IExtendedBlockState)state;
                key.add(xstate.getValue(BlockDrawers.STATE_MODEL));

                return key;
            }
            catch (Throwable t) {
                return super.getKey(state);
            }
        }*/
    }

    /*private static class ItemHandler extends ItemOverrideList
    {
        public ItemHandler () {
            super(ImmutableList.<ItemOverride>of());
        }

        @Override
        public IBakedModel handleItemState (IBakedModel parent, @Nonnull ItemStack stack, World world, EntityLivingBase entity) {
            if (stack.isEmpty())
                return parent;

            if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("tile", Constants.NBT.TAG_COMPOUND))
                return parent;

            Block block = Block.getBlockFromItem(stack.getItem());
            IBlockState state = block.getStateFromMeta(stack.getMetadata());

            return new DrawerSealedModel(parent, state, true);
        }
    }

    private static final ItemHandler itemHandler = new ItemHandler();*/
}
