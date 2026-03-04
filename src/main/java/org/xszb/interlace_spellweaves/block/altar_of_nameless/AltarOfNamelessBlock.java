package org.xszb.interlace_spellweaves.block.altar_of_nameless;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.xszb.interlace_spellweaves.block.altar_of_nameless.AltarOfNamelessEntity;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.entity.utils.SummonNamelessWizards;
import org.xszb.interlace_spellweaves.registries.RegistryBlock;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;
import org.xszb.interlace_spellweaves.registries.RegistryItem;

import javax.annotation.Nullable;

public class AltarOfNamelessBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public AltarOfNamelessBlock() {
        super(Properties.copy(Blocks.ENCHANTING_TABLE)
                .lightLevel(state -> state.getValue(LIT) ? 8 : 0)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, true)
                .setValue(POWERED, true));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!state.getValue(POWERED)) {
            if (!world.isClientSide) {
                player.displayClientMessage(Component.translatable("ui.iss_csw.altar_cooling").withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResult.FAIL;
        }
        if (!world.isClientSide) {
            world.setBlock(pos, state.setValue(LIT, false).setValue(POWERED, false), 3);
            SummonNamelessWizards boss = new SummonNamelessWizards(world,pos.getX(),pos.getY()+1,pos.getZ(),100,player);
            world.addFreshEntity(boss);
            if (world.getBlockEntity(pos) instanceof AltarOfNamelessEntity entity) {
                entity.startCooling();
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarOfNamelessEntity(pos, state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, RegistryBlock.ALTAR_OF_NAMELESE_TILE.get(), AltarOfNamelessEntity::commonTick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, POWERED);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

}