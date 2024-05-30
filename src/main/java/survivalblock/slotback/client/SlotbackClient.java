package survivalblock.slotback.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import survivalblock.slotback.client.key.HoldBackToolKey;
import survivalblock.slotback.client.key.SwitchKey;
import survivalblock.slotback.client.sprite.SlotbackSprites;
import survivalblock.slotback.common.Slotback;

public class SlotbackClient implements ClientModInitializer {

    public static final Identifier BACK_TEXTURE = Slotback.id("textures/gui/blank.png");

    @Override
    public void onInitializeClient() {
        SlotbackSprites.init();
        SwitchKey.init();
        HoldBackToolKey.init();
    }

}