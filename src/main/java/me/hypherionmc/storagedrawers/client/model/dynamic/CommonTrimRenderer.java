package me.hypherionmc.storagedrawers.client.model.dynamic;/*package me.hypherionmc.storagedrawers.client.model.dynamic;

import me.hypherionmc.chameleon.render.ChamRender;
import me.hypherionmc.chameleon.render.helpers.ModularBoxRenderer;
import me.hypherionmc.chameleon.render.helpers.PanelBoxRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class CommonTrimRenderer
{
    private final PanelBoxRenderer panelRenderer;

    public CommonTrimRenderer (ChamRender renderer) {
        this.panelRenderer = new PanelBoxRenderer(renderer);
    }

    private void start () {
        panelRenderer.setTrimWidth(.0625f);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);
    }

    public void render (IBlockAccess world, IBlockState state, BlockPos pos, TextureAtlasSprite iconSide, TextureAtlasSprite iconTrim) {
        start();

        panelRenderer.setTrimIcon(iconTrim);
        panelRenderer.setPanelIcon(iconSide);

        for (EnumFacing dir : EnumFacing.VALUES) {
            panelRenderer.renderFacePanel(dir, world, state, pos, 0, 0, 0, 1, 1, 1);
            panelRenderer.renderFaceTrim(dir, world, state, pos, 0, 0, 0, 1, 1, 1);
        }
    }
}
*/