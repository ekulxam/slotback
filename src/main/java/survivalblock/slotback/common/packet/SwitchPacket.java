package survivalblock.slotback.common.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.component.HoldingBackToolComponent;

public class SwitchPacket {

    public static final Identifier ID = Slotback.id("switch_item");

    public static class Receiver implements ServerPlayNetworking.PlayChannelHandler {

        /*
         * BEGIN CREDIT
         *
         * Code simplified from BackSlot
         * Backslot is licensed under the GNU General Public License v3.0
         */

        @Override
        public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            int slot = buf.readInt();
            PlayerInventory playerInventory = player.getInventory();
            HoldingBackToolComponent.get(player).setHoldingBackWeapon(false, true);
            int hotbarSlot = playerInventory.selectedSlot;
            if (hotbarSlot != 40) {
                ItemStack slotStack = playerInventory.getStack(slot);
                if (slotStack.isEmpty()) {
                    ItemStack mainHandStack = playerInventory.getStack(hotbarSlot);
                    boolean nothingsGonnaHappenWithTheMainHand = mainHandStack.isEmpty();
                    if (nothingsGonnaHappenWithTheMainHand) {
                        hotbarSlot = 40; // set slot to offhand
                    }
                }
            }

            ItemStack backslotStack = playerInventory.getStack(slot);
            ItemStack hotbarStack = playerInventory.getStack(hotbarSlot);

            // switching is meaningless if both slots are empty
            if (backslotStack.isEmpty() && hotbarStack.isEmpty()) {
                return;
            }

            playerInventory.setStack(hotbarSlot, backslotStack); // put the back stack in the hotbar
            playerInventory.setStack(slot, hotbarStack); // put the held stack in the backslot
            playerInventory.markDirty(); // markdirty
            player.clearActiveItem();

            if (!hotbarStack.isEmpty() || !backslotStack.isEmpty()) {
                player.world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
        }

        /*
         * END CREDIT
         *
         * A copy of the GNU General Public License v3.0 can be found at:
         * https://github.com/Globox1997/BackSlot/blob/1.20/LICENSE
         * (It is too long to put here)
         */
    }
}