package me.hypherionmc.storagedrawers.client.model.component;/*package me.hypherionmc.storagedrawers.client.model.component;

import me.hypherionmc.chameleon.Chameleon;
import me.hypherionmc.chameleon.geometry.Area2D;
import me.hypherionmc.chameleon.render.ChamRender;
import me.hypherionmc.chameleon.render.ChamRenderManager;
import me.hypherionmc.storagedrawers.StorageDrawers;
import me.hypherionmc.storagedrawers.api.storage.IDrawerGeometry;
import me.hypherionmc.storagedrawers.block.BlockDrawers;
import me.hypherionmc.storagedrawers.block.dynamic.StatusModelData;
import me.hypherionmc.storagedrawers.block.modeldata.DrawerStateModelData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DrawerDecoratorModel implements IBakedModel
{
    public static final ResourceLocation iconLock = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/lock_icon");
    public static final ResourceLocation iconClaim = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/claim_icon");
    public static final ResourceLocation iconClaimLock = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/claim_lock_icon");
    public static final ResourceLocation iconVoid = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/void_icon");
    public static final ResourceLocation iconShroudCover = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/drawers_oak_trim");

    private IBakedModel baseModel;
    private IExtendedBlockState blockState;
    private IDrawerGeometry drawer;
    private EnumFacing dir;
    private DrawerStateModelData modelData;
    private Set<BlockRenderLayer> renderLayers;

    public DrawerDecoratorModel (IBakedModel baseModel, IExtendedBlockState blockState, IDrawerGeometry drawer, EnumFacing dir, DrawerStateModelData modelData) {
        this.baseModel = baseModel;
        this.blockState = blockState;
        this.drawer = drawer;
        this.dir = dir;
        this.modelData = modelData;

        this.renderLayers = new HashSet<BlockRenderLayer>();
        this.renderLayers.add(BlockRenderLayer.CUTOUT_MIPPED);
    }

    public void addBaseRenderLayer(BlockRenderLayer layer) {
        this.renderLayers.add(layer);
    }

    public static boolean shouldHandleState (DrawerStateModelData stateModel) {
        return stateModel != null && (stateModel.isShrouded() || stateModel.isVoid() || stateModel.isItemLocked() || stateModel.getOwner() != null);
    }

    @Override
    public List<BakedQuad> getQuads (IBlockState state, EnumFacing side, long rand) {
        BlockRenderLayer renderLayer = MinecraftForgeClient.getRenderLayer();

        ChamRender renderer = ChamRenderManager.instance.getRenderer(null);
        renderer.startBaking(DefaultVertexFormats.ITEM);
        if (renderLayer == BlockRenderLayer.TRANSLUCENT) {
            if (modelData.isShrouded())
                buildShroudGeometry(renderer);
        }
        else if (renderLayer == BlockRenderLayer.CUTOUT_MIPPED) {
            if (modelData.isItemLocked() || modelData.getOwner() != null)
                buildLockGeometry(renderer);
            if (modelData.isVoid())
                buildVoidGeometry(renderer);
        }

        renderer.stopBaking();
        List<BakedQuad> quads = renderer.takeBakedQuads(null);

        if (renderLayers.contains(renderLayer))
            quads.addAll(baseModel.getQuads(state, side, rand));

        ChamRenderManager.instance.releaseRenderer(renderer);

        return quads;
    }

    @Override
    public boolean isAmbientOcclusion () {
        return baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d () {
        return baseModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer () {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture () {
        return baseModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms () {
        return baseModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides () {
        return baseModel.getOverrides();
    }

    private void buildLockGeometry (ChamRender renderer) {
        double depth = drawer.isHalfDepth() ? .5 : 1;

        TextureAtlasSprite lockIcon;
        if (modelData.isItemLocked() && modelData.getOwner() != null)
            lockIcon = Chameleon.instance.iconRegistry.getIcon(iconClaimLock);
        else if (modelData.isItemLocked())
            lockIcon = Chameleon.instance.iconRegistry.getIcon(iconLock);
        else if (modelData.getOwner() != null)
            lockIcon = Chameleon.instance.iconRegistry.getIcon(iconClaim);
        else
            return;

        renderer.setRenderBounds(0.46875, 0.9375, 0, 0.53125, 1, depth + .003);
        renderer.state.setRotateTransform(ChamRender.ZPOS, dir.getIndex());
        renderer.bakePartialFace(ChamRender.FACE_ZPOS, blockState, lockIcon, 0, 0, 1, 1, false, 1, 1, 1);
        renderer.state.clearRotateTransform();
    }

    private void buildVoidGeometry (ChamRender renderer) {
        double depth = drawer.isHalfDepth() ? .5 : 1;
        TextureAtlasSprite icon = Chameleon.instance.iconRegistry.getIcon(iconVoid);

        renderer.setRenderBounds(1 - .0625, 0.9375, 0, 1, 1, depth + .003);
        renderer.state.setRotateTransform(ChamRender.ZPOS, dir.getIndex());
        renderer.bakePartialFace(ChamRender.FACE_ZPOS, blockState, icon, 0, 0, 1, 1, false, 1, 1, 1);
        renderer.state.clearRotateTransform();
    }

    private void buildShroudGeometry (ChamRender renderer) {
        if (!(blockState.getBlock() instanceof BlockDrawers))
            return;

        BlockDrawers block = (BlockDrawers)blockState.getBlock();
        StatusModelData data = block.getStatusInfo(blockState);
        int count = drawer.getDrawerCount();
        double depth = drawer.isHalfDepth() ? .5 : 1;

        double unit = 0.0625;
        double frontDepth = data.getFrontDepth() * unit;

        TextureAtlasSprite iconCover = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconConcealmentOverlayResource);

        for (int i = 0; i < count; i++) {
            if (modelData.isDrawerEmpty(i))
                continue;

            StatusModelData.Slot slot = data.getSlot(i);
            Area2D bounds = slot.getIconArea();

            renderer.setRenderBounds(bounds.getX() * unit, bounds.getY() * unit, 0,
                (bounds.getX() + bounds.getWidth()) * unit, (bounds.getY() + bounds.getHeight()) * unit, depth - frontDepth + .003);
            renderer.state.setRotateTransform(ChamRender.ZPOS, dir.getIndex());
            renderer.bakeFace(ChamRender.FACE_ZPOS, blockState, iconCover, false, 1, 1, 1);
            renderer.state.clearRotateTransform();
        }
    }
}
*/