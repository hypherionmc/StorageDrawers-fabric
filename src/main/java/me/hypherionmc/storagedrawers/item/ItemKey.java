package me.hypherionmc.storagedrawers.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import me.hypherionmc.storagedrawers.api.storage.EmptyDrawerAttributes;
import me.hypherionmc.storagedrawers.api.storage.IDrawerAttributes;
import me.hypherionmc.storagedrawers.api.storage.IDrawerAttributesModifiable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemKey extends Item
{
    @CapabilityInject(IDrawerAttributes.class)
    public static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = null;

    private final Multimap<EntityAttribute, EntityAttributeModifier> modifiers;

    public ItemKey(Item.Settings properties) {
        super(properties);

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)2, EntityAttributeModifier.Operation.ADDITION));
        modifiers = builder.build();
    }

    @Override
    public boolean canMine(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new LiteralText("").append(getName()).formatted(Formatting.GRAY));
    }

    @Override
    public Text getName() {
        return new TranslatableText(this.getTranslationKey() + ".desc");
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? modifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockEntity tile = context.getWorld().getBlockEntity(context.getBlockPos());
        if (tile == null)
            return ActionResult.PASS;

        IDrawerAttributes attrs = tile.getCapability(DRAWER_ATTRIBUTES_CAPABILITY, null).orElse(EmptyDrawerAttributes.EMPTY);
        if (!(attrs instanceof IDrawerAttributesModifiable))
            return ActionResult.PASS;

        handleDrawerAttributes((IDrawerAttributesModifiable)attrs);

        return ActionResult.SUCCESS;
    }

    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) { }
}
