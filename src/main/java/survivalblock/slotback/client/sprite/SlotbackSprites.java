package survivalblock.slotback.client.sprite;

import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import survivalblock.slotback.common.Slotback;

public class SlotbackSprites {

    public static final Identifier EMPTY_BACK_SLOT_TEXTURE = Slotback.id("gui/empty_back_slot");

    public static void init() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlas, registry) -> {
            registry.register(EMPTY_BACK_SLOT_TEXTURE);
        });
    }

}
