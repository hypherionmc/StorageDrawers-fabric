package me.hypherionmc.storagedrawers.client.model.dynamic;/*package me.hypherionmc.storagedrawers.client.model.dynamic;

import me.hypherionmc.chameleon.render.ChamRender;
import me.hypherionmc.chameleon.render.ChamRenderState;
import me.hypherionmc.chameleon.render.helpers.ModularBoxRenderer;
import me.hypherionmc.chameleon.render.helpers.PanelBoxRenderer;
import me.hypherionmc.storagedrawers.api.storage.EnumBasicDrawer;
import me.hypherionmc.storagedrawers.block.BlockDrawers;
import me.hypherionmc.storagedrawers.block.BlockStandardDrawers;
import me.hypherionmc.storagedrawers.block.dynamic.StatusModelData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class CommonDrawerRenderer
{
    private final ChamRender renderHelper;
    private final PanelBoxRenderer panelRenderer;

    private double depth;
    private double trimWidth;
    private double trimDepth;
    private EnumBasicDrawer blockInfo;

    private static double unit7 = 0.4375;
    private static double unit9 = 0.5625;

    public CommonDrawerRenderer (ChamRender renderer) {
        this.renderHelper = renderer;
        this.panelRenderer = new PanelBoxRenderer(renderer);
    }

    private void start (IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing direction) {
        BlockDrawers block = (BlockDrawers) state.getBlock();
        StatusModelData status = block.getStatusInfo(state);
        blockInfo = (EnumBasicDrawer) state.getValue(BlockStandardDrawers.BLOCK);

        depth = blockInfo.isHalfDepth() ? .5 : 0;
        trimWidth = .0625;
        trimDepth = status.getFrontDepth() / 16f;
        unit7 = 0.4375;
        unit9 = 0.5625;

        panelRenderer.setTrimWidth(trimWidth);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);

        //if (world != null)
        //    renderHelper.setColorAndBrightness(world, state, pos);

        renderHelper.state.setRotateTransform(ChamRender.ZNEG, direction.getIndex());
        renderHelper.state.setUVRotation(ChamRender.YPOS, ChamRenderState.ROTATION_BY_FACE_FACE[ChamRender.ZNEG][direction.getIndex()]);
    }

    private void end () {
        renderHelper.state.clearRotateTransform();
        renderHelper.state.clearUVRotation(ChamRender.YPOS);
    }

    public void renderBasePass (IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing direction, TextureAtlasSprite iconSide, TextureAtlasSprite iconTrim, TextureAtlasSprite iconFront) {
        start(world, state, pos, direction);

        panelRenderer.setTrimIcon(iconTrim);
        panelRenderer.setPanelIcon(iconSide);

        for (EnumFacing dir : EnumFacing.VALUES) {
            if (dir != ChamRender.FACE_ZNEG) {
                renderHelper.targetFaceGroup(true);
                panelRenderer.renderFacePanel(dir, world, state, pos, 0, 0, depth, 1, 1, 1);
            }
            else if (dir == ChamRender.FACE_ZNEG && depth == 0)
                renderHelper.targetFaceGroup(true);

            panelRenderer.renderFaceTrim(dir, world, state, pos, 0, 0, depth, 1, 1, 1);
            renderHelper.targetFaceGroup(false);
        }

        panelRenderer.setTrimDepth(trimDepth);
        panelRenderer.renderInteriorTrim(ChamRender.FACE_ZNEG, world, state, pos, 0, 0, depth, 1, 1, 1);

        if (blockInfo.getDrawerCount() == 1) {
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconFront);
        }
        else if (blockInfo.getDrawerCount() == 2) {
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, 1 - trimWidth, unit7, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconFront);
            renderHelper.setRenderBounds(trimWidth, unit9, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconFront);

            renderHelper.setRenderBounds(trimWidth, unit7, depth + trimDepth, 1 - trimWidth, unit9, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconTrim);
        }
        else if (blockInfo.getDrawerCount() == 4) {
            renderHelper.state.flipTexture = true;
            renderHelper.state.autoFlipTexture = true;
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, unit7, unit7, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconFront);
            renderHelper.setRenderBounds(trimWidth, unit9, depth + trimDepth, unit7, 1 - trimWidth, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconFront);
            renderHelper.setRenderBounds(unit9, trimWidth, depth + trimDepth, 1 - trimWidth, unit7, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconFront);
            renderHelper.setRenderBounds(unit9, unit9, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconFront);

            renderHelper.setRenderBounds(trimWidth, unit7, depth + trimDepth, unit7, unit9, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconTrim);
            renderHelper.setRenderBounds(unit9, unit7, depth + trimDepth, 1 - trimWidth, unit9, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconTrim);
            renderHelper.setRenderBounds(unit7, trimWidth, depth + trimDepth, unit9, unit7, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconTrim);
            renderHelper.setRenderBounds(unit7, unit9, depth + trimDepth, unit9, 1 - trimWidth, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconTrim);
            renderHelper.setRenderBounds(unit7, unit7, depth + trimDepth, unit9, unit9, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconTrim);
            renderHelper.state.autoFlipTexture = false;
            renderHelper.state.flipTexture = false;
        }

        end();
    }

    public void renderOverlayPass (IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing direction, TextureAtlasSprite trimShadow, TextureAtlasSprite handle, TextureAtlasSprite faceShadow) {
        start(world, state, pos, direction);

        panelRenderer.setTrimIcon(trimShadow);

        if (depth == 0)
            renderHelper.targetFaceGroup(true);
        panelRenderer.renderFaceTrim(ChamRender.FACE_ZNEG, world, state, pos, 0, 0, depth, 1, 1, 1);
        renderHelper.targetFaceGroup(false);

        if (blockInfo.getDrawerCount() == 1) {
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, handle);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, faceShadow);
        }
        else if (blockInfo.getDrawerCount() == 2) {
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, 1 - trimWidth, unit7, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, handle);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, faceShadow);

            renderHelper.setRenderBounds(trimWidth, unit9, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, handle);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, faceShadow);

            renderHelper.setRenderBounds(trimWidth, unit7, depth + trimDepth, 1 - trimWidth, unit9, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, trimShadow);
        }
        else if (blockInfo.getDrawerCount() == 4) {
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, unit7, unit7, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, handle);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, faceShadow);

            renderHelper.setRenderBounds(trimWidth, unit9, depth + trimDepth, unit7, 1 - trimWidth, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, handle);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, faceShadow);

            renderHelper.setRenderBounds(unit9, trimWidth, depth + trimDepth, 1 - trimWidth, unit7, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, handle);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, faceShadow);

            renderHelper.setRenderBounds(unit9, unit9, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, handle);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, faceShadow);

            renderHelper.setRenderBounds(trimWidth, unit7, depth + trimDepth, unit7, unit9, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, trimShadow);
            renderHelper.setRenderBounds(unit9, unit7, depth + trimDepth, 1 - trimWidth, unit9, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, trimShadow);
            renderHelper.setRenderBounds(unit7, trimWidth, depth + trimDepth, unit9, unit7, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, trimShadow);
            renderHelper.setRenderBounds(unit7, unit9, depth + trimDepth, unit9, 1 - trimWidth, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, trimShadow);
            renderHelper.setRenderBounds(unit7, unit7, depth + trimDepth, unit9, unit9, 1);
            renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, trimShadow);
        }
        else
            renderHelper.renderEmptyPlane(pos);

        end();
    }
}
*/