package survivalblock.slotback.common;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import survivalblock.slotback.common.packet.HoldBackToolPacket;
import survivalblock.slotback.common.packet.SwitchPacket;

public class Slotback implements ModInitializer {
	public static final String MOD_ID = "slotback";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + " " + "\uD83D\uDD25");
	public static final int SLOT_ID = 51;

	@Override
	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(SwitchPacket.ID, new SwitchPacket.Receiver());
		ServerPlayNetworking.registerGlobalReceiver(HoldBackToolPacket.ID, new HoldBackToolPacket.Receiver());
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}