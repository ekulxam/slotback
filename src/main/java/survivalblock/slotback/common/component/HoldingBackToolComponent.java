package survivalblock.slotback.common.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.init.SlotbackEntityComponents;
import survivalblock.slotback.common.packet.HoldBackToolPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class HoldingBackToolComponent implements AutoSyncedComponent {

    private final PlayerEntity obj;
    private boolean isHoldingBackWeapon = false;


    public HoldingBackToolComponent(PlayerEntity obj) {
        this.obj = obj;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.isHoldingBackWeapon = tag.getBoolean("IsHoldingBackWeapon");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("IsHoldingBackWeapon", this.isHoldingBackWeapon);
    }

    public void setHoldingBackWeapon(boolean value, boolean shouldSync) {
        if (value) {
            ItemStack stack = this.obj.getInventory().getStack(Slotback.SLOT_ID);
            if (stack == null || stack.isEmpty()) {
                value = false;
            }
        }
        if (this.isHoldingBackWeapon != value) {
            this.isHoldingBackWeapon = value;
            if (shouldSync) sync();
        }
    }

    public boolean isHoldingBackWeapon() {
        return this.isHoldingBackWeapon;
    }

    private void sync() {
        if (this.obj.getWorld().isClient()) {
            // because apparently CCA doesn't do client -> server syncing, only server -> client
            // Sorry, Up and Pyrofab, but it must be done
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(isHoldingBackWeapon);
            ClientPlayNetworking.send(HoldBackToolPacket.ID, buf);
        } else {
            SlotbackEntityComponents.HOLDING_BACK_TOOL_COMPONENT.sync(this.obj);
        }
    }

    public static HoldingBackToolComponent get(PlayerEntity player) {
        return SlotbackEntityComponents.HOLDING_BACK_TOOL_COMPONENT.get(player);
    }
}
