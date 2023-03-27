package br.com.imfarias.jeifilterdrop.compatibility.xnet;

import com.mojang.logging.LogUtils;
import mcjty.lib.gui.widgets.BlockRender;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Widget;
import mcjty.xnet.modules.controller.client.AbstractEditorPanel;
import mcjty.xnet.modules.controller.client.ConnectorEditorPanel;
import mcjty.xnet.modules.controller.client.GuiController;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XNetControllerGhostHandler implements IGhostIngredientHandler<GuiController> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static boolean findWidgetPosition(Panel parent, Widget<?> target, Rectangle out) {
        for (Widget<?> child : parent.getChildren()) {
            if (target == child) {
                out.setBounds(target.getBounds().x + 1, target.getBounds().y + 1, 14, 14);
                return true;
            }
            if (child instanceof Panel) {
                boolean hasTarget = findWidgetPosition((Panel) child, target, out);
                if (hasTarget) {
                    out.x += child.getBounds().x;
                    out.y += child.getBounds().y;
                    return true;
                }
            }
        }
        return false;
    }

    private static Rectangle findWidgetPosition(Widget<?> target) {
        Widget<?> toplevel = target.getWindow().getToplevel();
        if (!(toplevel instanceof Panel)) {
            return null;
        }
        Rectangle result = new Rectangle();
        if (findWidgetPosition((Panel) toplevel, target, result)) {
            result.x += toplevel.getBounds().x;
            result.y += toplevel.getBounds().y;
            return result;
        }
        return null;
    }

    @Override
    public <I> List<Target<I>> getTargets(GuiController gui, I ingredient, boolean doStart) {
        if (!(ingredient instanceof ItemStack)) {
            return Collections.emptyList();
        }
        List<Target<I>> result = new ArrayList<>();
        Panel panel = XNetReflectionHelper.getConnectorEditPanel(gui);
        if (panel == null) {
            return result;
        }
        for (Widget<?> child : panel.getChildren()) {
            if (!(child instanceof BlockRender)) {
                continue;
            }
            if (!child.isEnabledAndVisible()) {
                continue;
            }

            BlockRender blockRender = (BlockRender) child;
            Rectangle position = findWidgetPosition(blockRender);
            if (position == null) {
                continue;
            }

            Tuple<AbstractEditorPanel, String> ghostSlotInfo = XNetReflectionHelper.findGhostSlotInfo(blockRender);
            if (ghostSlotInfo != null) {

                String tag = ghostSlotInfo.getB();
                AbstractEditorPanel editorPanel = ghostSlotInfo.getA();
                if (editorPanel instanceof ConnectorEditorPanel) {
                    result.add(new Target<I>() {
                        @Override
                        public Rect2i getArea() {
                            return new Rect2i(position.x, position.y, position.width, position.height);
                        }

                        @Override
                        public void accept(I ingredient) {
                            if (ingredient instanceof ItemStack) {
                                ItemStack stack = ((ItemStack) ingredient).copy();
                                stack.setCount(1);
                                blockRender.renderItem(stack);
                                XNetReflectionHelper.updateEditorPanel(editorPanel, tag, stack);
                            }
                        }
                    });
                }

            }
        }


        return result;
    }

    @Override
    public void onComplete() {
        LOGGER.error("##JEIFilterDrop -> onComplete XNETCONTROLLERHANDLER");
    }

}
