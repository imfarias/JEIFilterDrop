package br.com.imfarias.jeifilterdrop.compatibility;
import net.minecraftforge.fml.ModList;

public class Enables {
    public static boolean XNET = false;

    public static void init() {
        Enables.XNET = ModList.get().isLoaded("xnet");
    }
}
