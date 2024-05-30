package survivalblock.slotback.common.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import survivalblock.slotback.client.sprite.SlotbackSprites;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.compat.TrinketsCharmCompat;
import survivalblock.slotback.common.component.HoldingBackToolComponent;

public class Backslot extends Slot {

    private final PlayerEntity player;

    public Backslot(Inventory inventory, PlayerEntity player) {
        super(inventory, Slotback.SLOT_ID, 77, 44 - (Slotback.isTrinketsLoaded && TrinketsCharmCompat.isCharmLoaded(player) ? 18 : 0));
        this.player = player;
    }

    @Override
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, SlotbackSprites.EMPTY_BACK_SLOT_TEXTURE);
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        super.onTakeItem(player, stack);
        HoldingBackToolComponent.get(player).setHoldingBackWeapon(false, true);
    }

    @Override
    public void setStack(ItemStack stack) {
        super.setStack(stack);
        HoldingBackToolComponent.get(player).setHoldingBackWeapon(false, true);
    }
}
