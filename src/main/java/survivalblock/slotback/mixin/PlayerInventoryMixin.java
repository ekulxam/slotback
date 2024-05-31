package survivalblock.slotback.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.component.HoldingBackToolComponent;
import survivalblock.slotback.common.init.SlotbackEntityComponents;
import survivalblock.slotback.common.slot.Backslot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Debug(export = true)
@Mixin(value = PlayerInventory.class, priority = 1100)
public abstract class PlayerInventoryMixin implements Inventory {
    @Shadow
    @Final
    @Mutable
    private List<DefaultedList<ItemStack>> combinedInventory;

    @Shadow @Final public PlayerEntity player;
    @Unique
    private DefaultedList<ItemStack> backslotList;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(PlayerEntity playerEntity, CallbackInfo info) {
        this.backslotList = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.combinedInventory = new ArrayList<>(combinedInventory);
        this.combinedInventory.add(backslotList);
        this.combinedInventory = ImmutableList.copyOf(this.combinedInventory);
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

    @Inject(method = "updateItems", at = @At("RETURN"))
    private void tickBackslot(CallbackInfo ci) {
        List<Slot> slots = new ArrayList<>(player.playerScreenHandler.slots);
        Collections.reverse(slots); // reverse the collection so that it finds the backslot earlier
        for (Slot slot : slots) {
            if (slot instanceof Backslot backslot) {
                backslot.tick();
                return;
            }
        }
    }

    @Inject(method = "getStack", at = @At("HEAD"), cancellable = true)
    private void getBackStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (Slotback.SLOT_ID == slot) {
            cir.setReturnValue(backslotList.get(0));
        }
    }

    @Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    private void setBackStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (Slotback.SLOT_ID == slot) {
            backslotList.set(0, stack);
            ci.cancel();
        }
    }

    @Inject(method = "removeStack(I)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void removeBackStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (Slotback.SLOT_ID == slot) {
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
        if (Slotback.SLOT_ID == slot) {
            cir.setReturnValue(backslotList != null && !backslotList.get(0).isEmpty() ? Inventories.splitStack(backslotList, 0, amount) : ItemStack.EMPTY);
        }
    }

    @ModifyExpressionValue(method = "removeOne", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private <E> Iterator<E> removeBackslotFromOne(Iterator<E> original) {
        if (this.combinedInventory.contains(backslotList)) {
            ArrayList<DefaultedList<ItemStack>> list = new ArrayList<>(this.combinedInventory);
            list.remove(backslotList);
            return (Iterator<E>) ImmutableList.of(list).iterator();
        }
        return original;
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"), cancellable = true)
    private void accountForHeldItem(BlockState state, CallbackInfoReturnable<Float> cir) {
        if (SlotbackEntityComponents.HOLDING_BACK_TOOL_COMPONENT.get(player).isHoldingBackWeapon()) {
            cir.setReturnValue(backslotList.get(0).getMiningSpeedMultiplier(state));
        }
    }
}