package survivalblock.slotback.common;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import survivalblock.slotback.common.component.HoldingBackToolComponent;
import survivalblock.slotback.common.packet.HoldBackToolPacket;
import survivalblock.slotback.common.packet.SwitchPacket;

public class Slotback implements ModInitializer {
	public static final String MOD_ID = "slotback";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + " " + "\uD83D\uDD25");
	public static final int SLOT_ID = 51;
	public static boolean keepBackslotOnDeath = true;

	@Override
	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(SwitchPacket.ID, new SwitchPacket.Receiver());
		ServerPlayNetworking.registerGlobalReceiver(HoldBackToolPacket.ID, new HoldBackToolPacket.Receiver());
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> HoldingBackToolComponent.get(handler.getPlayer()).setHoldingBackWeapon(false, true));
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}