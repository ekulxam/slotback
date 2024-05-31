package survivalblock.slotback.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import survivalblock.slotback.common.slot.Backslot;

import java.util.ArrayList;
import java.util.Collections;
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
    private DefaultedList<ItemStack> backSlot;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(PlayerEntity playerEntity, CallbackInfo info) {
        this.backSlot = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.combinedInventory = new ArrayList<>(combinedInventory);
        this.combinedInventory.add(backSlot);
        this.combinedInventory = ImmutableList.copyOf(this.combinedInventory);
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    public void writeBackslot(NbtList tag, CallbackInfoReturnable<NbtList> info) {
        if (this.backSlot.get(0).isEmpty()) {
            return;
        }
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("Slotback", true);
        nbt.putString("Backslot", "idwtialsimmoedm");
        this.backSlot.get(0).writeNbt(nbt);
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
                this.backSlot.set(0, itemStack);
            }
            // We set priority to 1100 to inject this specific method after the default 1000s
            tag.remove(compoundTag);
        }
    }

    @ModifyReturnValue(method = "size", at = @At("RETURN"))
    private int addBackslotSize(int original) {
        return original + backSlot.size();
    }

    @ModifyReturnValue(method = "isEmpty", at = @At(value = "RETURN", ordinal = 0))
    private boolean accountForBackslot(boolean original) {
        for (ItemStack stack : this.backSlot) {
            if (stack != null && !stack.isEmpty()) {
                return false; // it's not empty
            }
        }
        return original;
    }

    @WrapWithCondition(method = "dropAll", at = @At(value = "INVOKE", target = "Ljava/util/List;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private <E> boolean noDroppingBackslot(List<ItemStack> instance, int index, E stack) {
        if (instance.equals(backSlot)) {
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
}