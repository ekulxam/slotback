package survivalblock.slotback.mixin;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Debug(export = true)
@Mixin(Slot.class)
public interface SlotAccessor {

    @Mutable
    @Accessor("x")
    void setX(int x);

    @Mutable
    @Accessor("y")
    void setY(int y);
}
