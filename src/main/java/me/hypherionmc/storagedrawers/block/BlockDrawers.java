package me.hypherionmc.storagedrawers.block;

import me.hypherionmc.storagedrawers.StorageDrawers;
import me.hypherionmc.storagedrawers.api.storage.*;
import me.hypherionmc.storagedrawers.api.storage.attribute.LockAttribute;
import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawers;
import me.hypherionmc.storagedrawers.capabilities.CapabilityDrawerAttributes;
import me.hypherionmc.storagedrawers.config.ClientConfig;
import me.hypherionmc.storagedrawers.config.CommonConfig;
import me.hypherionmc.storagedrawers.core.ModItems;
import me.hypherionmc.storagedrawers.inventory.ContainerDrawers1;
import me.hypherionmc.storagedrawers.inventory.ContainerDrawers2;
import me.hypherionmc.storagedrawers.inventory.ContainerDrawers4;
import me.hypherionmc.storagedrawers.inventory.ContainerDrawersComp;
import me.hypherionmc.storagedrawers.item.ItemKey;
import me.hypherionmc.storagedrawers.item.ItemUpgrade;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BlockDrawers extends HorizontalFacingBlock implements INetworked, BlockEntityProvider
{

    // TODO: TE.getModelData()
    //public static final IUnlistedProperty<DrawerStateModelData> STATE_MODEL = UnlistedModelData.create(DrawerStateModelData.class);

    private static final VoxelShape AABB_FULL = Block.createCuboidShape(0, 0, 0, 16, 16, 16);
    private static final VoxelShape AABB_NORTH_FULL = VoxelShapes.combineAndSimplify(AABB_FULL, Block.createCuboidShape(1, 1, 0, 15, 15, 1), BooleanBiFunction.ONLY_FIRST);
    private static final VoxelShape AABB_SOUTH_FULL = VoxelShapes.combineAndSimplify(AABB_FULL, Block.createCuboidShape(1, 1, 15, 15, 15, 16), BooleanBiFunction.ONLY_FIRST);
    private static final VoxelShape AABB_WEST_FULL = VoxelShapes.combineAndSimplify(AABB_FULL, Block.createCuboidShape(0, 1, 1, 1, 15, 15), BooleanBiFunction.ONLY_FIRST);
    private static final VoxelShape AABB_EAST_FULL = VoxelShapes.combineAndSimplify(AABB_FULL, Block.createCuboidShape(15, 1, 1, 16, 15, 15), BooleanBiFunction.ONLY_FIRST);
    private static final VoxelShape AABB_NORTH_HALF = Block.createCuboidShape(0, 0, 8, 16, 16, 16);
    private static final VoxelShape AABB_SOUTH_HALF = Block.createCuboidShape(0, 0, 0, 16, 16, 8);
    private static final VoxelShape AABB_WEST_HALF = Block.createCuboidShape(8, 0, 0, 16, 16, 16);
    private static final VoxelShape AABB_EAST_HALF = Block.createCuboidShape(0, 0, 0, 8, 16, 16);

    private final int drawerCount;
    private final boolean halfDepth;
    private final int storageUnits;

    public final Box[] slotGeometry;
    public final Box[] countGeometry;
    public final Box[] labelGeometry;

    //@SideOnly(Side.CLIENT)
    //private StatusModelData[] statusInfo;

    private long ignoreEventTime;

    private static final ThreadLocal<Boolean> inTileLookup = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue () {
            return false;
        }
    };

    public BlockDrawers (int drawerCount, boolean halfDepth, int storageUnits, Block.Settings properties) {
        super(properties);
        this.setDefaultState(stateManager.getDefaultState()
            .with(FACING, Direction.NORTH));

        this.drawerCount = drawerCount;
        this.halfDepth = halfDepth;
        this.storageUnits = storageUnits;

        slotGeometry = new Box[drawerCount];
        countGeometry = new Box[drawerCount];
        labelGeometry = new Box[drawerCount];

        for (int i = 0; i < drawerCount; i++) {
            slotGeometry[i] = new Box(0, 0, 0, 0, 0, 0);
            countGeometry[i] = new Box(0, 0, 0, 0, 0, 0);
            labelGeometry[i] = new Box(0, 0, 0, 0, 0, 0);
        }
    }

    @Override
    protected void appendProperties (StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public boolean retrimBlock (World world, BlockPos pos, ItemStack prototype) {
        return false;
    }

    public BlockType retrimType () {
        return BlockType.Drawers;
    }

    // TODO: ABSTRACT?  Still need BlockState?
    public int getDrawerCount () {
        return drawerCount;
    }

    public boolean isHalfDepth () {
        return halfDepth;
    }

    public int getStorageUnits() {
        return storageUnits;
    }

    @Environment(EnvType.CLIENT)
    public void initDynamic () { }

    /*@OnlyIn(Dist.CLIENT)
    public StatusModelData getStatusInfo (BlockState state) {
        return null;
    }*/

    /*@Override
    public BlockRenderLayer getRenderLayer () {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }*/

    //@Override
    //public boolean canRenderInLayer (IBlockState state, BlockRenderLayer layer) {
    //    return layer == BlockRenderLayer.CUTOUT_MIPPED || layer == BlockRenderLayer.TRANSLUCENT;
    //}


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        switch (direction) {
            case EAST:
                return halfDepth ? AABB_EAST_HALF : AABB_EAST_FULL;
            case WEST:
                return halfDepth ? AABB_WEST_HALF : AABB_WEST_FULL;
            case SOUTH:
                return halfDepth ? AABB_SOUTH_HALF : AABB_SOUTH_FULL;
            case NORTH:
            default:
                return halfDepth ? AABB_NORTH_HALF : AABB_NORTH_FULL;
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    /*@Override
    public void onBlockAdded (World world, BlockPos pos, IBlockState state) {
        if (!world.isClient) {
            IBlockState blockNorth = world.getBlockState(pos.north());
            IBlockState blockSouth = world.getBlockState(pos.south());
            IBlockState blockWest = world.getBlockState(pos.west());
            IBlockState blockEast = world.getBlockState(pos.east());

            EnumFacing facing = state.getValue(FACING);

            if (facing == EnumFacing.NORTH && blockNorth.isFullBlock() && !blockSouth.isFullBlock())
                facing = EnumFacing.SOUTH;
            if (facing == EnumFacing.SOUTH && blockSouth.isFullBlock() && !blockNorth.isFullBlock())
                facing = EnumFacing.NORTH;
            if (facing == EnumFacing.WEST && blockWest.isFullBlock() && !blockEast.isFullBlock())
                facing = EnumFacing.EAST;
            if (facing == EnumFacing.EAST && blockEast.isFullBlock() && !blockWest.isFullBlock())
                facing = EnumFacing.WEST;

            TileEntityDrawers tile = getTileEntitySafe(world, pos);
            tile.setDirection(facing.ordinal());
            tile.markDirty();

            world.setBlockState(pos, state.withProperty(FACING, facing));
        }

        super.onBlockAdded(world, pos, state);
    }*/

    @Override
    public void onPlaced (World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("tile")) {
            TileEntityDrawers tile = getTileEntity(world, pos);
            if (tile != null)
                tile.readPortable(stack.getTag().getCompound("tile"));
        }

        if (stack.hasCustomName()) {
            TileEntityDrawers tile = getTileEntity(world, pos);
            //if (tile != null)
            //    tile.setCustomName(stack.getDisplayName());
        }

        if (entity != null && entity.getOffHandStack().getItem() == ModItems.DRAWER_KEY) {
            TileEntityDrawers tile = getTileEntity(world, pos);
            if (tile != null) {
                IDrawerAttributes _attrs = tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY).orElse(new EmptyDrawerAttributes());
                if (_attrs instanceof IDrawerAttributesModifiable) {
                    IDrawerAttributesModifiable attrs = (IDrawerAttributesModifiable) _attrs;
                    attrs.setItemLocked(LockAttribute.LOCK_EMPTY, true);
                    attrs.setItemLocked(LockAttribute.LOCK_POPULATED, true);
                }
            }
        }
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext useContext) {
        PlayerEntity player = useContext.getPlayer();
        if (player == null)
            return super.canReplace(state, useContext);

        if (useContext.getPlayer().isCreative() && useContext.getHand() == Hand.OFF_HAND) {
            double blockReachDistance = useContext.getPlayer().getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).getValue() + 1;
            BlockHitResult result = rayTraceEyes(useContext.getWorld(), useContext.getPlayer(), blockReachDistance);

            if (result.getType() == BlockHitResult.Type.MISS || result.getSide() != state.get(FACING))
                useContext.getWorld().setBlockState(useContext.getBlockPos(), Blocks.AIR.getDefaultState(), useContext.getWorld().isClient ? 11 : 3);
            else
                onBlockBreakStart(state, useContext.getWorld(), useContext.getBlockPos(), useContext.getPlayer());

            return false;
        }

        return super.canReplace(state, useContext);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack item = player.getStackInHand(hand);
        if (hand == Hand.OFF_HAND)
            return ActionResult.PASS;

        if (world.isClient && Util.milliTime() == ignoreEventTime) {
            ignoreEventTime = 0;
            return ActionResult.PASS;
        }

        TileEntityDrawers tileDrawers = getTileEntitySafe(world, pos);

        //if (!SecurityManager.hasAccess(player.getGameProfile(), tileDrawers))
        //    return false;

        if (CommonConfig.GENERAL.debugTrace.get()) {
            StorageDrawers.log.info("BlockDrawers.onBlockActivated");
            StorageDrawers.log.info((item.isEmpty()) ? "  null item" : "  " + item.toString());
        }


        if (!item.isEmpty()) {
            if (item.getItem() instanceof ItemKey)
                return ActionResult.PASS;

            /*if (item.getItem() instanceof ItemTrim && player.isSneaking()) {
                if (!retrimBlock(world, pos, item))
                    return false;

                if (!player.capabilities.isCreativeMode) {
                    item.shrink(1);
                    if (item.getCount() <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                }

                return true;
            }*/
            if (item.getItem() instanceof ItemUpgrade) {
                if (!tileDrawers.upgrades().canAddUpgrade(item)) {
                    if (!world.isClient)
                        player.sendMessage(new TranslatableText("message.storagedrawers.cannot_add_upgrade"), true);

                    return ActionResult.PASS;
                }

                if (!tileDrawers.upgrades().addUpgrade(item)) {
                    if (!world.isClient)
                        player.sendMessage(new TranslatableText("message.storagedrawers.max_upgrades"), true);

                    return ActionResult.PASS;
                }

                world.updateListeners(pos, state, state, 3);

                if (!player.isCreative()) {
                    item.shrink(1);
                    if (item.getCount() <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                }

                return ActionResult.SUCCESS;
            }
            /*else if (item.getItem() instanceof ItemPersonalKey) {
                String securityKey = ((ItemPersonalKey) item.getItem()).getSecurityProviderKey(item.getItemDamage());
                ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);

                if (tileDrawers.getOwner() == null) {
                    tileDrawers.setOwner(player.getPersistentID());
                    tileDrawers.setSecurityProvider(provider);
                }
                else if (SecurityManager.hasOwnership(player.getGameProfile(), tileDrawers)) {
                    tileDrawers.setOwner(null);
                    tileDrawers.setSecurityProvider(null);
                }
                else
                    return false;
                return true;
            }*/
        }
        else if (item.isEmpty() && player.isSneaking()) {
            /*if (tileDrawers.isSealed()) {
                tileDrawers.setIsSealed(false);
                return true;
            }
            else if (StorageDrawers.config.cache.enableDrawerUI) {
                player.openGui(StorageDrawers.instance, GuiHandler.drawersGuiID, world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }*/

            if (CommonConfig.GENERAL.enableUI.get() && !world.isClient) {
                NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider()
                {
                    @Override
                    public ITextComponent getDisplayName () {
                        return new TranslationTextComponent(getTranslationKey());
                    }

                    @Nullable
                    @Override
                    public Container createMenu (int windowId, PlayerInventory playerInv, PlayerEntity playerEntity) {
                        if (drawerCount == 1)
                            return new ContainerDrawers1(windowId, playerInv, tileDrawers);
                        else if (drawerCount == 2)
                            return new ContainerDrawers2(windowId, playerInv, tileDrawers);
                        else if (drawerCount == 4)
                            return new ContainerDrawers4(windowId, playerInv, tileDrawers);
                        else if (drawerCount == 3 && BlockDrawers.this instanceof BlockCompDrawers)
                            return new ContainerDrawersComp(windowId, playerInv, tileDrawers);
                        return null;
                    }
                }, extraData -> {
                    extraData.writeBlockPos(pos);
                });
                return ActionResult.SUCCESS;
            }
        }

        if (state.get(FACING) != hit.getSide())
            return ActionResult.PASS;

        //if (tileDrawers.isSealed())
        //    return false;

        int slot = getDrawerSlot(hit);
        tileDrawers.interactPutItemsIntoSlot(slot, player);

        if (item.isEmpty())
            player.setStackInHand(hand, ItemStack.EMPTY);

        return ActionResult.SUCCESS;
    }

    protected final int getDrawerSlot (BlockHitResult hit) {
        return getDrawerSlot(hit.getSide(), normalizeHitVec(hit.getPos()));
    }

    private Vec3d normalizeHitVec (Vec3d hit) {
        return new Vec3d(
            ((hit.x < 0) ? hit.x - Math.floor(hit.x) : hit.x) % 1,
            ((hit.y < 0) ? hit.y - Math.floor(hit.y) : hit.y) % 1,
            ((hit.z < 0) ? hit.z - Math.floor(hit.z) : hit.z) % 1
        );
    }

    protected int getDrawerSlot (Direction side, Vec3d hit) {
        return 0;
    }

    protected boolean hitTop (double hitY) {
        return hitY > .5;
    }

    protected boolean hitLeft (Direction side, double hitX, double hitZ) {
        switch (side) {
            case NORTH:
                return hitX > .5;
            case SOUTH:
                return hitX < .5;
            case WEST:
                return hitZ < .5;
            case EAST:
                return hitZ > .5;
            default:
                return true;
        }
    }

    protected BlockHitResult rayTraceEyes(World world, PlayerEntity player, double length) {
        Vec3d eyePos = player.getCameraPosVec(1);
        Vec3d lookPos = player.getRotationVec(1);
        Vec3d endPos = eyePos.add(lookPos.x * length, lookPos.y * length, lookPos.z * length);
        RaycastContext context = new RaycastContext(eyePos, endPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player);
        return world.raycast(context);
    }

    @Override
    public void onBlockBreakStart(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn) {
        if (worldIn.isClient)
            return;

        if (CommonConfig.GENERAL.debugTrace.get())
            StorageDrawers.log.info("onBlockClicked");

        BlockHitResult rayResult = rayTraceEyes(worldIn, playerIn, playerIn.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue() + 1);
        if (rayResult.getType() == BlockHitResult.Type.MISS)
            return;

        Direction side = rayResult.getSide();

        TileEntityDrawers tileDrawers = getTileEntitySafe(worldIn, pos);
        if (state.get(FACING) != rayResult.getSide())
            return;

        //if (tileDrawers.isSealed())
        //    return;

        //if (!SecurityManager.hasAccess(playerIn.getGameProfile(), tileDrawers))
        //    return;

        int slot = getDrawerSlot(rayResult);
        IDrawer drawer = tileDrawers.getDrawer(slot);

        ItemStack item;
        boolean invertShift = ClientConfig.GENERAL.invertShift.get();

        if (playerIn.isSneaking() != invertShift)
            item = tileDrawers.takeItemsFromSlot(slot, drawer.getStoredItemStackSize());
        else
            item = tileDrawers.takeItemsFromSlot(slot, 1);

        if (CommonConfig.GENERAL.debugTrace.get())
            StorageDrawers.log.info((item.isEmpty()) ? "  null item" : "  " + item.toString());

        if (!item.isEmpty()) {
            if (!playerIn.giveItemStack(item)) {
                dropItemStack(worldIn, pos.offset(side), playerIn, item);
                worldIn.updateListeners(pos, state, state, 3);
            }
            else
                worldIn.playSound(null, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, .2f, ((worldIn.random.nextFloat() - worldIn.random.nextFloat()) * .7f + 1) * 2);
        }
    }

    private void dropItemStack (World world, BlockPos pos, PlayerEntity player, @Nonnull ItemStack stack) {
        ItemEntity entity = new ItemEntity(world, pos.getX() + .5f, pos.getY() + .3f, pos.getZ() + .5f, stack);
        Vec3d motion = entity.getVelocity();
        entity.addVelocity(-motion.x, -motion.y, -motion.z);
        world.spawnEntity(entity);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        TileEntityDrawers tile = getTileEntity(world, pos);

        if (tile != null) {
            /*for (int i = 0; i < tile.upgrades().getSlotCount(); i++) {
                ItemStack stack = tile.upgrades().getUpgrade(i);
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof ItemUpgradeCreative)
                        continue;
                    spawnAsEntity(world, pos, stack);
                }
            }*/

            //if (!tile.getDrawerAttributes().isUnlimitedVending())
            //    DrawerInventoryHelper.dropInventoryItems(world, pos, tile.getGroup());
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public boolean removedByPlayer (BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        if (player.isCreative()) {
            if (creativeCanBreakBlock(state, world, pos, player))
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isClient ? 11 : 3);
            else
                onBlockBreakStart(state, world, pos, player);

            return false;
        }

        return willHarvest || super.removedByPlayer(state, world, pos, player, false, fluid);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        world.removeBlock(pos, false);
    }

    public boolean creativeCanBreakBlock (BlockState state, World world, BlockPos pos, PlayerEntity player) {
        double blockReachDistance = player.getAttributeInstance(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue() + 1;

        BlockHitResult rayResult = rayTraceEyes(world, player, blockReachDistance + 1);
        if (rayResult.getType() == BlockHitResult.Type.MISS || state.get(FACING) != rayResult.getSide())
            return true;
        else
            return false;
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        List<ItemStack> items = new ArrayList<>();
        items.add(getMainDrop(state, (TileEntityDrawers)builder.get(LootContextParameters.BLOCK_ENTITY)));
        return items;
    }

    protected ItemStack getMainDrop (BlockState state, TileEntityDrawers tile) {
        ItemStack drop = new ItemStack(this);
        if (tile == null)
            return drop;

        NbtCompound data = drop.getTag();
        if (data == null)
            data = new NbtCompound();

        boolean hasContents = false;
        for (int i = 0; i < tile.getGroup().getDrawerCount(); i++) {
            IDrawer drawer = tile.getGroup().getDrawer(i);
            if (drawer != null && !drawer.isEmpty())
                hasContents = true;
        }
        for (int i = 0; i < tile.upgrades().getSlotCount(); i++) {
            if (!tile.upgrades().getUpgrade(i).isEmpty())
                hasContents = true;
        }

        if (hasContents) {
            NbtCompound tiledata = new NbtCompound();
            tile.writeNbt(tiledata);

            tiledata.remove("x");
            tiledata.remove("y");
            tiledata.remove("z");

            data.put("tile", tiledata);
            drop.setTag(data);
        }

        return drop;
    }

    /*@Override
    public float getExplosionResistance (World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile != null) {
            for (int slot = 0; slot < 5; slot++) {
                ItemStack stack = tile.upgrades().getUpgrade(slot);
                if (stack.isEmpty() || !(stack.getItem() instanceof ItemUpgradeStorage))
                    continue;

                if (EnumUpgradeStorage.byMetadata(stack.getMetadata()) != EnumUpgradeStorage.OBSIDIAN)
                    continue;

                return 1000;
            }
        }

        return super.getExplosionResistance(world, pos, exploder, explosion);
    }*/

    public TileEntityDrawers getTileEntity (BlockView blockAccess, BlockPos pos) {
        if (inTileLookup.get())
            return null;

        inTileLookup.set(true);
        BlockEntity tile = blockAccess.getBlockEntity(pos);
        inTileLookup.set(false);

        return (tile instanceof TileEntityDrawers) ? (TileEntityDrawers) tile : null;
    }

    public TileEntityDrawers getTileEntitySafe (World world, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = (TileEntityDrawers) createBlockEntity(world);
            world.setBlockEntity(pos, tile);
        }

        return tile;
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects (IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        if (getDirection(worldObj, target.getBlockPos()) == target.sideHit)
            return true;

        return super.addHitEffects(state, worldObj, target, manager);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects (World world, BlockPos pos, ParticleManager manager) {
        //TileEntityDrawers tile = getTileEntity(world, pos);
        //if (tile != null && !tile.getWillDestroy())
        //    return true;

        return super.addDestroyEffects(world, pos, manager);
    }*/

    @Override
    @SuppressWarnings("deprecation")
    public boolean emitsRedstonePower (BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!emitsRedstonePower(state))
            return 0;

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null || !tile.isRedstone())
            return 0;

        return tile.getRedstoneLevel();
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (direction == Direction.UP) ? getWeakRedstonePower(state, world, pos, direction) : 0;
    }

}
