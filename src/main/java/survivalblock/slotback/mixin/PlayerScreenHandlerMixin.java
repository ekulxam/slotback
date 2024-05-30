package survivalblock.slotback.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.slotback.common.compat.TrinketsCharmCompat;
import survivalblock.slotback.common.slot.Backslot;

@Debug(export = true)
@Mixin(value = PlayerScreenHandler.class, priority = 2000) // inject after trinkets
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory> {

    public PlayerScreenHandlerMixin(ScreenHandlerType<PlayerScreenHandler> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void captureInventory(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        this.addBackslot(inventory, owner);
    }

    @Unique
    private void addBackslot(PlayerInventory inventory, PlayerEntity owner) {
        int xOff = 0;
        int yOff = 0;
        boolean hasTrinkets = FabricLoader.getInstance().isModLoaded("trinkets");
        if (hasTrinkets) {
            if (TrinketsCharmCompat.isCharmLoaded(owner)) {
                xOff += 4 * 19 - 1;
                yOff += 19;
            }
        }
        this.addSlot(new Backslot(inventory, owner, xOff, yOff));
    }
}