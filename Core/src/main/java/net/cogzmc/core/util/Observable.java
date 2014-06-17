package net.cogzmc.core.util;

import com.google.common.collect.ImmutableSet;

public interface Observable<T> {
    void registerObserver(T observer);
    void unregisterObserver(T observer);
    ImmutableSet<T> getObservers();
}
