package survivalblock.slotback.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.component.HoldingBackToolComponent;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;
    @Unique
    private boolean wasHoldingBackWeapon = false;

    @Inject(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    private void stopHoldingBackslotWhenDoingItemPick(CallbackInfo ci, @Local ItemStack pickStack) {
        // for some reason trying to do a pick block while holding backslot just duplicates the backslot stack
        if (this.player != null) {
            if (pickStack != null && !pickStack.isEmpty()) {
                ItemStack stack = this.player.getInventory().getStack(Slotback.SLOT_ID).copy();
                stack.setCount(1);
                ItemStack otherStack = pickStack.copy();
                otherStack.setCount(1);
                if (areStacksEqualEnough(stack, otherStack)) {
                    ci.cancel(); // cancel and return if the stacks are equal
                    return;
                }
            }
            HoldingBackToolComponent backToolComponent = HoldingBackToolComponent.get(this.player);
            wasHoldingBackWeapon = backToolComponent.isHoldingBackWeapon();
            backToolComponent.setHoldingBackWeapon(false, false);
        }
    }

    @Inject(method = "doItemPick", at = @At("RETURN"))
    private void resumeHoldingBackslotAfterItemPick(CallbackInfo ci) {
        if (wasHoldingBackWeapon) {
            wasHoldingBackWeapon = false;
            HoldingBackToolComponent.get(this.player).setHoldingBackWeapon(true, false);
        }
    }

    @Unique
    private static boolean areStacksEqualEnough(ItemStack one, ItemStack two) {
        if (one == null || one.isEmpty() || two == null || two.isEmpty()) {
            return false;
        }
        if (one.equals(two)) {
            return true;
        }
        boolean areItemsSame = one.getItem().equals(two.getItem()); // if the held item is equal to the pick item
        if (!areItemsSame) {
            return false; // if their items aren't equal, then they won't be equal
        }
        // if they both don't have nbt
        if (one.hasNbt() && two.hasNbt()) { // if they both have nbt
            return one.getNbt().equals(two.getNbt()); // check that their nbts and item are the same
        }
        return !one.hasNbt() && !two.hasNbt(); // if they don't have nbt, then just check that their items are the same
    }
}
