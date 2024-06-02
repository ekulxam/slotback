package survivalblock.slotback.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.component.HoldingBackToolComponent;
import survivalblock.slotback.common.init.SlotbackEntityComponents;
import survivalblock.slotback.common.slot.Backslot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Debug(export = true)
@Mixin(value = PlayerInventory.class, priority = 5000)
public abstract class PlayerInventoryMixin implements Inventory {

    @Shadow @Final public PlayerEntity player;

    @Shadow public abstract int size();

    @Unique
    private DefaultedList<ItemStack> backslotList;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(PlayerEntity playerEntity, CallbackInfo info) {
        this.backslotList = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    public void writeBackslot(NbtList tag, CallbackInfoReturnable<NbtList> info) {
        if (this.backslotList.get(0).isEmpty()) {
            return;
        }
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("Slotback", true);
        nbt.putString("Backslot", "idwtialsimmoedm");
        this.backslotList.get(0).writeNbt(nbt);
        tag.add(nbt);
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    public void readBackslot(NbtList tag, CallbackInfo info) {
        this.resetBackslot();
        for (int i = 0; i < tag.size(); ++i) {
            NbtCompound compoundTag = tag.getCompound(i);
            if (!compoundTag.contains("Slotback")) {
                continue;
            }
            if (!compoundTag.contains("Backslot")) {
                continue;
            }
            if (!compoundTag.getString("Backslot").equals("idwtialsimmoedm")) {
                return;
            }
            compoundTag.remove("Slotback");
            compoundTag.remove("Backslot");
            ItemStack itemStack = ItemStack.fromNbt(compoundTag);
            if (itemStack != null && !itemStack.isEmpty()) {
                this.backslotList.set(0, itemStack);
            }
            // We set priority to 1100 to inject this specific method after the default 1000s
            tag.remove(compoundTag);
        }
    }

    @ModifyReturnValue(method = "size", at = @At("RETURN"))
    private int addBackslotSize(int original) {
        return original + backslotList.size();
    }

    @ModifyReturnValue(method = "isEmpty", at = @At(value = "RETURN", ordinal = 0))
    private boolean accountForBackslot(boolean original) {
        for (ItemStack stack : this.backslotList) {
            if (stack != null && !stack.isEmpty()) {
                return false; // it's not empty
            }
        }
        return original;
    }

    @WrapWithCondition(method = "dropAll", at = @At(value = "INVOKE", target = "Ljava/util/List;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private <E> boolean noDroppingBackslot(List<ItemStack> instance, int index, E stack) {
        if (instance.equals(backslotList)) {
            // stop duplication
            this.resetBackslot();
            return false;
        }
        return true;
    }

    @Unique
    private void resetBackslot() {
        PlayerInventory playerInventory = player.getInventory();
        HoldingBackToolComponent.get(player).setHoldingBackWeapon(false, true);
        playerInventory.setStack(Slotback.SLOT_ID, ItemStack.EMPTY);
    }

    @ModifyArg(method = "updateItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;inventoryTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V"), index = 3)
    private boolean notSelectedIfHoldingBackslot(boolean selected) {
        return !HoldingBackToolComponent.get(this.player).isHoldingBackWeapon() && selected;
    }

    @Inject(method = "updateItems", at = @At("RETURN"))
    private void tickBackslot(CallbackInfo ci) {
        List<Slot> slots = new ArrayList<>(player.playerScreenHandler.slots);
        Collections.reverse(slots); // reverse the collection so that it finds the backslot earlier
        for (Slot slot : slots) {
            if (slot instanceof Backslot backslot) {
                backslot.getStack().inventoryTick(this.player.world, this.player, backslot.getIndex(), HoldingBackToolComponent.get(this.player).isHoldingBackWeapon());
                backslot.tick();
                return;
            }
        }
    }

    @ModifyReturnValue(method = "size", at = @At("RETURN"))
    private int addBackslotToSize(int original) {
        return original + this.backslotList.size();
    }

    @Unique
    private boolean equalsSlotId(int slot) {
        return Slotback.SLOT_ID == slot || (this.size() - 1) == slot;
    }

    @Inject(method = "getStack", at = @At("HEAD"), cancellable = true)
    private void getBackStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (equalsSlotId(slot)) {
            cir.setReturnValue(backslotList.get(0));
        }
    }

    @Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    private void setBackStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (equalsSlotId(slot)) {
            backslotList.set(0, stack);
            ci.cancel();
        }
    }

    @Inject(method = "removeStack(I)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void removeBackStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (equalsSlotId(slot)) {
            if (backslotList != null && !backslotList.get(0).isEmpty()) {
                ItemStack itemStack = backslotList.get(0);
                backslotList.set(0, ItemStack.EMPTY);
                cir.setReturnValue(itemStack);
            } else {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
    }

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void removeBackStackCount(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (equalsSlotId(slot)) {
            cir.setReturnValue(backslotList != null && !backslotList.get(0).isEmpty() ? Inventories.splitStack(backslotList, 0, amount) : ItemStack.EMPTY);
        }
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"), cancellable = true)
    private void accountForHeldBackStack(BlockState state, CallbackInfoReturnable<Float> cir) {
        if (SlotbackEntityComponents.HOLDING_BACK_TOOL_COMPONENT.get(player).isHoldingBackWeapon()) {
            cir.setReturnValue(backslotList.get(0).getMiningSpeedMultiplier(state));
        }
    }

    @Inject(method = "dropAll", at = @At("RETURN"))
    private void dropBackslot(CallbackInfo ci) {
        if (Slotback.keepBackslotOnDeath) {
            return;
        }
        this.player.dropItem(backslotList.get(0), true, false);
        backslotList.set(0, ItemStack.EMPTY);
    }
}