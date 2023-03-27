package br.com.imfarias.jeifilterdrop.compatibility.xnet;

import br.com.imfarias.jeifilterdrop.compatibility.Enables;
import com.mojang.logging.LogUtils;
import mcjty.lib.gui.widgets.BlockRender;
import mcjty.lib.gui.widgets.Panel;
import mcjty.xnet.modules.controller.client.AbstractEditorPanel;
import mcjty.xnet.modules.controller.client.GuiController;
import net.minecraft.util.Tuple;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class XNetReflectionHelper {
    private static Field fieldConnectorEditPanel;

    private static Field fieldSelectionEvents;

    private static Class<?> classGhostSelectionEvent;
    private static Field editorPanelVal;
    private static Field tagVal;

    private static Method methodUpdate;
    private static final Logger LOGGER = LogUtils.getLogger();


    public static void init() {
        LOGGER.info("##JEIFilterDrop -> INIT XNET REFLECTION {}", Enables.XNET);
        if (Enables.XNET) {
            try {
                classGhostSelectionEvent = Class.forName("mcjty.xnet.modules.controller.client.AbstractEditorPanel$1");
                editorPanelVal = classGhostSelectionEvent.getDeclaredField("this$0");
                tagVal = classGhostSelectionEvent.getDeclaredField("val$tag");
                editorPanelVal.setAccessible(true);
                tagVal.setAccessible(true);

                fieldConnectorEditPanel = GuiController.class.getDeclaredField("connectorEditPanel");
                fieldConnectorEditPanel.setAccessible(true);
                fieldSelectionEvents = BlockRender.class.getDeclaredField("selectionEvents");
                fieldSelectionEvents.setAccessible(true);
                methodUpdate = AbstractEditorPanel.class.getDeclaredMethod("update", String.class, Object.class);
                methodUpdate.setAccessible(true);

            } catch (NoSuchMethodException | NoSuchFieldException | ClassNotFoundException e) {
                LOGGER.error("##JEIFilterDrop -> ", e);
            }
        }
    }

    public static Panel getConnectorEditPanel(GuiController obj) {
        try {
            if (fieldConnectorEditPanel != null) {
                Object o = fieldConnectorEditPanel.get(obj);
                if (o instanceof Panel) {
                    return (Panel) o;
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException ignored) {
            LOGGER.error("##JEIFilterDrop -> ", ignored);
        }
        return null;
    }

    public static void updateEditorPanel(AbstractEditorPanel obj, String tag, Object value) {
        try {
            if (methodUpdate != null) {
                methodUpdate.invoke(obj, tag, value);
            }
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            LOGGER.error("##JEIFilterDrop -> ", ignored);
        }
    }


    public static Tuple<AbstractEditorPanel, String> findGhostSlotInfo(BlockRender obj) {

        try {
            if (fieldSelectionEvents != null) {
                Object o = fieldSelectionEvents.get(obj);
                if (o instanceof List<?>) {
                    for (Object event : ((List<?>) o)) {
                        if (!classGhostSelectionEvent.isInstance(event)) {
                            continue;
                        }
                        Object editorPanel = editorPanelVal.get(event);
                        Object tag = tagVal.get(event);
                        if (editorPanel instanceof AbstractEditorPanel && tag instanceof String) {
                            return new Tuple<>((AbstractEditorPanel) editorPanel, (String) tag);
                        }
                        break;
                    }

                }
            }
        } catch (IllegalAccessException | IllegalArgumentException ignored) {

        }
        return null;
    }
}
