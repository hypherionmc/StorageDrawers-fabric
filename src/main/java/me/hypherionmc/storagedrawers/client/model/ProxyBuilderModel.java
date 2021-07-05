package me.hypherionmc.storagedrawers.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ProxyBuilderModel implements BakedModel
{
    private static final List<BakedQuad> EMPTY = new ArrayList<BakedQuad>(0);
    private static final List<Object> EMPTY_KEY = new ArrayList<Object>();

    private BakedModel parent;
    private BakedModel proxy;
    private BlockState stateCache;
    private Sprite iconParticle;

    public ProxyBuilderModel (Sprite iconParticle) {
        this.iconParticle = iconParticle;
    }

    public ProxyBuilderModel (BakedModel parent) {
        this.parent = parent;
    }

    @Override
    public List<BakedQuad> getQuads (BlockState state, Direction side, Random rand) {
        if (proxy == null || stateCache != state)
            setProxy(state);

        if (proxy == null)
            return EMPTY;

        return proxy.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion () {
        BakedModel model = getActiveModel();
        return model == null || model.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth () {
        BakedModel model = getActiveModel();
        return model != null && model.hasDepth();
    }

    @Override
    public boolean isBuiltin () {
        BakedModel model = getActiveModel();
        return model != null && model.isBuiltin();
    }

    @Override
    public Sprite getSprite () {
        BakedModel model = getActiveModel();
        return (model != null) ? model.getSprite() : iconParticle;
    }

    @Override
    public ModelTransformation getTransformation () {
        BakedModel model = getActiveModel();
        return (model != null) ? model.getTransformation() : ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides () {
        BakedModel model = getActiveModel();
        return (model != null) ? model.getOverrides() : ModelOverrideList.EMPTY;
    }

    @Override
    public boolean isSideLit () {
        BakedModel model = getActiveModel();
        return model != null && model.isSideLit();
    }

    public List<Object> getKey (BlockState state) {
        return EMPTY_KEY;
    }

    protected abstract BakedModel buildModel (BlockState state, BakedModel parent);

    public final BakedModel buildModel (BlockState state) {
        return this.buildModel(state, parent);
    }

    private void setProxy (BlockState state) {
        stateCache = state;
        if (state == null)
            proxy = parent;
        else
            proxy = buildModel(state, parent);
    }

    private BakedModel getActiveModel () {
        return (proxy != null) ? proxy : parent;
    }
}
