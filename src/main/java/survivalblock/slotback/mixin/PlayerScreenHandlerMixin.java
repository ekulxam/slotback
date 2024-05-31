package survivalblock.slotback.mixin;

import dev.emi.trinkets.data.EntitySlotLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.slotback.common.compat.TrinketsCharmCompat;
import survivalblock.slotback.common.slot.Backslot;

@Debug(export = true)
@Mixin(value = PlayerScreenHandler.class, priority = 2000) // inject after trinkets
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory> {

    public PlayerScreenHandlerMixin(ScreenHandlerType<PlayerScreenHandler> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addBackslot(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        int xOff = 0;
        int yOff = 0;
        if (FabricLoader.getInstance().isModLoaded("trinkets") && TrinketsCharmCompat.isCharmLoaded(owner)) {
            xOff += 4 * 19 - 1;
            yOff += 19;
        }
        this.addSlot(new Backslot(inventory, owner, xOff, yOff, onServer));
    }
}