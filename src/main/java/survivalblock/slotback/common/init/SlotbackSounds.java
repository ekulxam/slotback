package survivalblock.slotback.common.init;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import survivalblock.slotback.common.Slotback;

public class SlotbackSounds {

    public static final SoundEvent PACK_UP_ITEM = new SoundEvent(Slotback.id("pack_up_item"));

    public static void init() {
        Registry.register(Registry.SOUND_EVENT, PACK_UP_ITEM.getId(), PACK_UP_ITEM);
    }

}
