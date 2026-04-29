package org.xszb.interlace_spellweaves.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.xszb.interlace_spellweaves.entity.boss.UnRemoveBossEntity;
import org.xszb.interlace_spellweaves.util.EntityUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;


public class GuDingDao extends SwordItem {
    public GuDingDao(Properties properties) {
        super(Tiers.NETHERITE, 3, -2.4f, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && !(target instanceof Player)) {
            if (target instanceof UnRemoveBossEntity bossEntity) {
                bossEntity.setCanDie(true);
                bossEntity.setCanKill(true);
                bossEntity.remove(Entity.RemovalReason.DISCARDED);
            }else EntityUtil.forceRemoveEntity(target.level(), target);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!entity.level().isClientSide && !(entity instanceof Player)) {
            if (entity instanceof UnRemoveBossEntity bossEntity) {
                bossEntity.setAntiCheatMode(false);
                bossEntity.setCanDie(true);
                bossEntity.setCanKill(true);
                bossEntity.remove(Entity.RemovalReason.DISCARDED);
            }else EntityUtil.forceRemoveEntity(entity.level(), entity);
        }
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {

            AABB area = player.getBoundingBox().inflate(64.0);
            List<Entity> entities = level.getEntitiesOfClass(Entity.class, area, e -> !(e instanceof Player));
            for (Entity target : entities) {
                if (target instanceof UnRemoveBossEntity bossEntity) {
                    bossEntity.setAntiCheatMode(false);
                    bossEntity.setCanDie(true);
                    bossEntity.setCanKill(true);
                    bossEntity.remove(Entity.RemovalReason.DISCARDED);
                } else {
                    EntityUtil.forceRemoveEntity(level, target);
                }
            }
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.iss_csw.clearentity").withStyle(ChatFormatting.GREEN));
    }
}
