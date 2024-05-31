package survivalblock.slotback.client.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import survivalblock.slotback.client.sprite.SlotbackSprites;
import survivalblock.slotback.common.slot.Backslot;

public class CreativeBackslot extends CreativeInventoryScreen.CreativeSlot {

    public CreativeBackslot(Backslot backslot, int index, int x, int y) {
        super(backslot, index, x, y);
    }

    @Nullable
    @Override
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, SlotbackSprites.EMPTY_BACK_SLOT_TEXTURE);
    }
}
