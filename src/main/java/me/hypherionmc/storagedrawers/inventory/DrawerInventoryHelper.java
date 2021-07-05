package me.hypherionmc.storagedrawers.inventory;

import me.hypherionmc.storagedrawers.api.storage.IDrawer;
import me.hypherionmc.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class DrawerInventoryHelper
{
    private static final Random RANDOM = new Random();

    public static void dropInventoryItems (World world, BlockPos pos, IDrawerGroup group) {
        for (int i = 0; i < group.getDrawerCount(); i++) {
            IDrawer drawer = group.getDrawer(i);
            if (!drawer.isEnabled())
                continue;

            while (drawer.getStoredItemCount() > 0) {
                ItemStack stack = drawer.getStoredItemPrototype().copy();
                int storedCount = drawer.getStoredItemCount();
                int stackLimit = stack.getMaxCount();
                int stackSize = Math.min(storedCount, stackLimit);

                stack.setCount(stackSize);
                if (stack.isEmpty())
                    break;

                spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                drawer.adjustStoredItemCount(-stackSize);
            }
        }
    }

    private static void spawnItemStack (World world, double x, double y, double z, ItemStack stack)
    {
        float xOff = RANDOM.nextFloat() * 0.8F + 0.1F;
        float yOff = RANDOM.nextFloat() * 0.8F + 0.1F;
        float zOff = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (!stack.isEmpty()) {
            ItemEntity entityitem = new ItemEntity(world, x + xOff, y + yOff, z + zOff, stack.split(RANDOM.nextInt(21) + 10));

            float velocity = 0.05F;
            entityitem.setVelocity(
                RANDOM.nextGaussian() * (double)velocity,
                RANDOM.nextGaussian() * (double)velocity + 0.20000000298023224D,
                RANDOM.nextGaussian() * (double)velocity
            );

            world.spawnEntity(entityitem);
        }
    }
}
