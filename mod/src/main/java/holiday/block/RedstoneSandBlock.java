package holiday.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import holiday.tag.HolidayServerBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ColoredFallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.ColorCode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class RedstoneSandBlock extends ColoredFallingBlock {
    private static final int CRYING_IMPACTORS_RANGE = 5;
    private static final int MIN_CRYING_IMPACTORS = 6;

    private static final BlockState OBSIDIAN = Blocks.OBSIDIAN.getDefaultState();
    private static final BlockState CRYING_OBSIDIAN = Blocks.CRYING_OBSIDIAN.getDefaultState();

    public static final MapCodec<RedstoneSandBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(
            ColorCode.CODEC.fieldOf("falling_dust_color").forGetter(block -> block.color),
            createSettingsCodec()
        ).apply(instance, RedstoneSandBlock::new);
    });

    public RedstoneSandBlock(ColorCode color, Settings settings) {
        super(color, settings);
    }

    // Concrete powder-like behavior
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockView world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        BlockState state = world.getBlockState(pos);

        if (shouldHarden(world, pos, state)) {
            return getResultState(world, pos);
        }

        return super.getPlacementState(context);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (shouldHardenFromNeighbor(world, pos)) {
            return getResultState(world, neighborPos);
        }

        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        if (shouldHarden(world, pos, currentStateInPos)) {
            world.setBlockState(pos, getResultState(world, pos));
        }
    }

    private static boolean shouldHarden(BlockView world, BlockPos pos, BlockState state) {
        return isHardeningFluid(state) || shouldHardenFromNeighbor(world, pos);
    }

    private static boolean shouldHardenFromNeighbor(BlockView world, BlockPos pos) {
        BlockPos.Mutable offsetPos = pos.mutableCopy();

        for (Direction direction : Direction.values()) {
            BlockState state = world.getBlockState(offsetPos);

            if (direction != Direction.DOWN || isHardeningFluid(state)) {
                offsetPos.set(pos, direction);

                state = world.getBlockState(offsetPos);

                if (isHardeningFluid(state) && !state.isSideSolidFullSquare(world, pos, direction.getOpposite())) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isHardeningFluid(BlockState state) {
        return state.getFluidState().isIn(FluidTags.LAVA);
    }

    private static BlockState getResultState(BlockView world, BlockPos origin) {
        int crying = 0;

        for (BlockPos pos : BlockPos.iterateOutwards(origin, CRYING_IMPACTORS_RANGE, CRYING_IMPACTORS_RANGE, CRYING_IMPACTORS_RANGE)) {
            BlockState state = world.getBlockState(pos);

            if (state.isIn(HolidayServerBlockTags.CRYING_IMPACTORS)) {
                crying += 1;
            }
        }

        return crying >= MIN_CRYING_IMPACTORS ? CRYING_OBSIDIAN : OBSIDIAN;
    }

    // Redstone block-like behavior
    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 15;
    }

    @Override
    public MapCodec<? extends RedstoneSandBlock> getCodec() {
        return CODEC;
    }
}
