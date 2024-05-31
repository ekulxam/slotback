package survivalblock.slotback.common.compat;

import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.data.EntitySlotLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;

public class TrinketsCharmCompat {

    public static boolean isCharmLoaded(PlayerEntity player) {
        return isCharmLoaded(player.getWorld().isClient());
    }

    public static boolean isCharmLoaded(boolean shouldUseClient) {
        EntitySlotLoader loader = shouldUseClient ? EntitySlotLoader.CLIENT : EntitySlotLoader.SERVER;
        Map<String, SlotGroup> slots = loader.getEntitySlots(EntityType.PLAYER);
        for (var entry : slots.entrySet()) {
            Map<String, SlotType> slotTypes = entry.getValue().getSlots();
            for (var entryOne : slotTypes.entrySet()) {
                if (entryOne.getKey().toLowerCase().contains("charm")) {
                    return true;
                }
            }
        }
        return false;
    }
}
