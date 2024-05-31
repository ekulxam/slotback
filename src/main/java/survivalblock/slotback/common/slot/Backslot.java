package survivalblock.slotback.common.slot;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.loader.api.FabricLoader;
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
import survivalblock.slotback.mixin.SlotAccessor;

public class Backslot extends Slot {

    public static final int DEFAULT_X = 77;
    public static final int DEFAULT_Y = 44;

    private final PlayerEntity player;
    private final boolean onServer;

    public Backslot(Inventory inventory, PlayerEntity player, int xOffset, int yOffset, boolean onServer) {
        super(inventory, Slotback.SLOT_ID, DEFAULT_X + xOffset, DEFAULT_Y + yOffset);
        this.player = player;
        this.onServer = onServer;
    }

    public void tick() {
        if (onServer) {
            return;
        }
        if (this.x != DEFAULT_X || this.y != DEFAULT_Y) {
            return;
        }
        if (!FabricLoader.getInstance().isModLoaded("trinkets")) {
            return;
        }
        if (TrinketsCharmCompat.isCharmLoaded(this.player)) {
            ((SlotAccessor) this).setX(this.x + 4 * 19 - 1);
            ((SlotAccessor) this).setY(this.y + 19);
        }
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
