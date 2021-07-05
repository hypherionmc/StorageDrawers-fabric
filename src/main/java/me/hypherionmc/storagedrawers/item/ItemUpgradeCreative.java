package me.hypherionmc.storagedrawers.item;/*package me.hypherionmc.storagedrawers.item;

import me.hypherionmc.chameleon.resources.IItemMeshMapper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ItemUpgradeCreative extends ItemUpgrade implements IItemMeshMapper
{
    public ItemUpgradeCreative (String registryName, String unlocalizedName) {
        super(registryName, unlocalizedName);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName (ItemStack itemStack) {
        return super.getUnlocalizedName() + "." + EnumUpgradeCreative.byMetadata(itemStack.getMetadata()).getUnlocalizedName();
    }

    @Override
    public int getMetadata (int damage) {
        return damage;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (CreativeTabs creativeTabs, NonNullList<ItemStack> list) {
        if (isInCreativeTab(creativeTabs)) {
            for (EnumUpgradeCreative upgrade : EnumUpgradeCreative.values())
                list.add(new ItemStack(this, 1, upgrade.getMetadata()));
        }
    }

    @Override
    public List<Pair<ItemStack, ModelResourceLocation>> getMeshMappings () {
        List<Pair<ItemStack, ModelResourceLocation>> mappings = new ArrayList<Pair<ItemStack, ModelResourceLocation>>();

        for (EnumUpgradeCreative type : EnumUpgradeCreative.values()) {
            ModelResourceLocation location = new ModelResourceLocation(getRegistryName().toString() + '_' + type.getName(), "inventory");
            mappings.add(Pair.of(new ItemStack(this, 1, type.getMetadata()), location));
        }

        return mappings;
    }
}
*/