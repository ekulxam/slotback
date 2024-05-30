package survivalblock.slotback.common.compat;

import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;

public class TrinketsCharmCompat {

    public static boolean isCharmLoaded(PlayerEntity player) {
        Map<String, SlotGroup> slots = TrinketsApi.getPlayerSlots(player);
        for (var entry : slots.entrySet()) {
            if (entry.getKey().toLowerCase().contains("extra")) {
                return true;
            }
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
