package holiday.mixin;

import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventoryStorageImpl.class)
public interface InventoryStorageImplAccessor {
    @Accessor("inventory")
    Inventory getInventory();
}
