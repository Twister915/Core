package net.cogzmc.core.gui.view;

import net.cogzmc.core.gui.GraphicalInterface;
import net.cogzmc.core.gui.InventoryGraphicalInterface;
import net.cogzmc.core.player.CPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Deprecated
//TODO
final class InventoryViewController implements GraphicalInterface {
    private final List<CPlayer> observers = new ArrayList<>();
    private final TreeSet<InventoryView> viewStack = new TreeSet<>();
    private final InventoryGraphicalInterface container;
    @Getter private final Integer size;
    @Getter private final String title;

    public InventoryViewController(InventoryView rootView, String title, Integer size) {
        this.title = title;
        this.size = size;
        this.container = new InventoryGraphicalInterface(size, title);
        push(rootView);
    }

    public void pop() {

    }

    public void push(InventoryView view) {
        viewStack.add(view);

    }

    @Override
    public void open(CPlayer player) {
        this.observers.add(player);

    }

    @Override
    public void close(CPlayer player) {

    }

    @Override
    public void open(Iterable<CPlayer> players) {

    }

    @Override
    public void close(Iterable<CPlayer> players) {

    }

    @Override
    public ImmutableList<CPlayer> getCurrentObservers() {
        return null;
    }
}
