package net.cogzmc.core.gui.view;

import com.google.common.collect.ImmutableList;
import net.cogzmc.core.gui.InventoryButton;
import net.cogzmc.core.gui.InventoryGraphicalInterface;
import net.cogzmc.core.player.CPlayer;

@Deprecated
//TODO
final class InventoryView extends InventoryGraphicalInterface {
    public InventoryView(InventoryViewController controller) {
        super(controller.getSize()-9, controller.getTitle());
    }

    @Override
    public void open(CPlayer player) {

    }

    @Override
    public void open(Iterable<CPlayer> players) {

    }

    @Override
    public void close(CPlayer player) {

    }

    @Override
    public void close(Iterable<CPlayer> players) {

    }

    @Override
    public ImmutableList<CPlayer> getCurrentObservers() {
        return super.getCurrentObservers();
    }

    @Override
    public void addButton(InventoryButton button) {

    }

    @Override
    public void removeButton(InventoryButton button) {

    }
}
