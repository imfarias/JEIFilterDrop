package br.com.imfarias.jeifilterdrop.jei;

import br.com.imfarias.jeifilterdrop.compatibility.Enables;
import br.com.imfarias.jeifilterdrop.compatibility.xnet.XNetControllerGhostHandler;
import com.mojang.logging.LogUtils;
import mcjty.xnet.modules.controller.client.GuiController;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

@mezz.jei.api.JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public ResourceLocation getPluginUid() {
        return null;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        LOGGER.info("##JEIFilterDrop -> JEIPlugin registerGuiHandlers {}", Enables.XNET);

        if (Enables.XNET) {
            registration.addGhostIngredientHandler(GuiController.class, new XNetControllerGhostHandler());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Screen> void registerByName(IGuiHandlerRegistration registry, String className, IGhostIngredientHandler<T> handler) {
        try {
            registry.addGhostIngredientHandler(
                    (Class<T>) Class.forName(className),
                    handler);
        } catch (ClassNotFoundException e) {
            LOGGER.error("##JEIFilterDrop -> ", e);
        }
    }

}
