package survivalblock.slotback.common;

import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SlotbackUtil {

    public static class SlotComparator implements Comparator<Slot> {

        public static final SlotComparator INSTANCE = new SlotComparator();

        @Override
        public int compare(Slot o1, Slot o2) {
            return o1.getIndex() - o2.getIndex();
        }
    }

    public static List<Slot> sortSlotsByIndex(final List<Slot> original) {
        List<Slot> list = new ArrayList<>(original);
        list.sort(SlotComparator.INSTANCE);
        return list;
    }
}
