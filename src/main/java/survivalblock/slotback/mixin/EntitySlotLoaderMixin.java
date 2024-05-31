package survivalblock.slotback.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.emi.trinkets.TrinketsMain;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.data.EntitySlotLoader;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.slotback.common.Slotback;

@Debug(export = true)
@Mixin(EntitySlotLoader.class)
public class EntitySlotLoaderMixin {

    @WrapWithCondition(method = "lambda$apply$8", at = @At(value = "INVOKE", target = "Ldev/emi/trinkets/api/SlotGroup$Builder;addSlot(Ljava/lang/String;Ldev/emi/trinkets/api/SlotType;)Ldev/emi/trinkets/api/SlotGroup$Builder;"), remap = false)
    private static boolean removeCharmSlotOne(SlotGroup.Builder instance, String name, SlotType slot) {
        if (slot.getGroup().equals("extra1") && name.equals("charm")) {
            TrinketsMain.LOGGER.warn(name + " slot in group " + slot.getGroup() + " was removed by slotback!");
            Slotback.LOGGER.info("I can confirm, this in fact did happen.");
            return true;
        }
        return true;
    }
}
