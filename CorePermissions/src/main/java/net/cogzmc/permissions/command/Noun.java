package net.cogzmc.permissions.command;

import net.cogzmc.core.player.CPermissible;

import java.util.List;
import java.util.Set;

public abstract class Noun<P extends CPermissible> {
    protected abstract String[] getNames();
    protected abstract Set<Verb<P>> getVerbs();
    protected abstract List<String> getTabCompleteFor(String arg);
    protected abstract P get(String s);
}
