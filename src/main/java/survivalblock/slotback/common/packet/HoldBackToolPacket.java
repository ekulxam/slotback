package survivalblock.slotback.common.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.init.SlotbackEntityComponents;

public class HoldBackToolPacket {

    public static final Identifier ID = Slotback.id("hold_back_weapon");

    public static class Receiver implements ServerPlayNetworking.PlayChannelHandler {

        @Override
        public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            SlotbackEntityComponents.HOLDING_BACK_TOOL_COMPONENT.get(player).setHoldingBackWeapon(buf.readBoolean(), false);
            player.world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }
}
