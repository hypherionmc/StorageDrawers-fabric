package me.hypherionmc.storagedrawers.block.modeldata;/*package me.hypherionmc.storagedrawers.block.modeldata;

import me.hypherionmc.chameleon.model.ModelData;
import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawers;
import me.hypherionmc.storagedrawers.block.tile.TileEntityTrim;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class MaterialModelData extends ModelData
{
    @Nonnull
    private final ItemStack matFront;
    @Nonnull
    private final ItemStack matSide;
    @Nonnull
    private final ItemStack matTrim;
    @Nonnull
    private final ItemStack effectiveMatFront;
    @Nonnull
    private final ItemStack effectiveMatSide;
    @Nonnull
    private final ItemStack effectiveMatTrim;

    public MaterialModelData (TileEntityDrawers tile) {
        if (tile == null) {
            matFront = ItemStack.EMPTY;
            matSide = ItemStack.EMPTY;
            matTrim = ItemStack.EMPTY;
            effectiveMatFront = ItemStack.EMPTY;
            effectiveMatSide = ItemStack.EMPTY;
            effectiveMatTrim = ItemStack.EMPTY;
        }
        else {
            matFront = tile.material().getFront();
            matSide = tile.material().getSide();
            matTrim = tile.material().getTrim();
            effectiveMatFront = tile.material().getEffectiveFront();
            effectiveMatSide = tile.material().getEffectiveSide();
            effectiveMatTrim = tile.material().getEffectiveTrim();
        }
    }

    public MaterialModelData (TileEntityTrim tile) {
        matFront = ItemStack.EMPTY;
        effectiveMatFront = ItemStack.EMPTY;

        if (tile == null) {
            matSide = ItemStack.EMPTY;
            matTrim = ItemStack.EMPTY;
            effectiveMatSide = ItemStack.EMPTY;
            effectiveMatTrim = ItemStack.EMPTY;
        }
        else {
            matSide = tile.material().getSide();
            matTrim = tile.material().getTrim();
            effectiveMatSide = tile.material().getEffectiveSide();
            effectiveMatTrim = tile.material().getEffectiveTrim();
        }
    }

    @Nonnull
    public ItemStack getMaterialFront () {
        return matFront;
    }

    @Nonnull
    public ItemStack getMaterialSide () {
        return matSide;
    }

    @Nonnull
    public ItemStack getMaterialTrim() {
        return matTrim;
    }

    @Nonnull
    public ItemStack getEffectiveMaterialFront () {
        return effectiveMatFront;
    }

    @Nonnull
    public ItemStack getEffectiveMaterialSide () {
        return effectiveMatSide;
    }

    @Nonnull
    public ItemStack getEffectiveMaterialTrim() {
        return effectiveMatTrim;
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == null || obj.getClass() != this.getClass())
            return false;

        MaterialModelData other = (MaterialModelData)obj;
        if (!ItemStack.areItemsEqual(matFront, other.matFront))
            return false;
        if (!ItemStack.areItemsEqual(matSide, other.matSide))
            return false;
        if (!ItemStack.areItemsEqual(matTrim, other.matTrim))
            return false;

        return true;
    }

    @Override
    public int hashCode () {
        int c = 0;
        if (matFront != null) {
            c = 37 * c + (matFront.getItem() != null ? matFront.getItem().hashCode() : 0);
            c = 37 * c + matFront.getItemDamage();
        }
        else
            c = 37 * c;

        if (matSide != null) {
            c = 37 * c + (matSide.getItem() != null ? matSide.getItem().hashCode() : 0);
            c = 37 * c + matSide.getItemDamage();
        }
        else
            c = 37 * c;

        if (matTrim != null) {
            c = 37 * c + (matTrim.getItem() != null ? matTrim.getItem().hashCode() : 0);
            c = 37 * c + matTrim.getItemDamage();
        }
        else
            c = 37 * c;

        return c;
    }
}
*/