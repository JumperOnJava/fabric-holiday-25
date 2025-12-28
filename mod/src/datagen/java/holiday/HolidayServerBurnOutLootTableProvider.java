package holiday;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import holiday.item.HolidayServerItems;
import holiday.loot.HolidayServerLootContextTypes;
import holiday.loot.HolidayServerLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

public class HolidayServerBurnOutLootTableProvider extends SimpleFabricLootTableProvider {
    public HolidayServerBurnOutLootTableProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, registries, HolidayServerLootContextTypes.BURN_OUT);
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> registry) {
        registry.accept(HolidayServerLootTables.HOPPER_MITE_ATTRACTION_GAMEPLAY, LootTable.builder()
                .pool(LootPool.builder().with(ItemEntry.builder(HolidayServerItems.HOPPER_MITE)))
                .randomSequenceId(HolidayServerLootTables.HOPPER_MITE_ATTRACTION_GAMEPLAY.getValue()));
    }
}
