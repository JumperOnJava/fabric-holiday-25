package holiday.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import holiday.WhitelistLogger;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.WhitelistCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.function.Predicate;

@Mixin(WhitelistCommand.class)
public class WhitelistCommandMixin {
    @Inject(
            method = "executeAdd",
            at = @At("HEAD")
    )
    private static void logAdds(ServerCommandSource source, Collection<PlayerConfigEntry> targets, CallbackInfoReturnable<Integer> cir) {
        var whitelister = source.getName();
        targets.forEach(whiteliste -> {
            var whitelisteName = whiteliste.name();
            WhitelistLogger.logWhitelisting(whitelister, whitelisteName);
        });
    }

    @WrapOperation(
            method = "register", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;requires(Ljava/util/function/Predicate;)Lcom/mojang/brigadier/builder/ArgumentBuilder;", remap = false), remap = true
    )
    private static ArgumentBuilder makeExecutableForEveryone(LiteralArgumentBuilder instance, Predicate predicate, Operation<ArgumentBuilder> original) {

        return instance;
    }
}
