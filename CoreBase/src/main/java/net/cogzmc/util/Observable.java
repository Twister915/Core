package net.cogzmc.util;

public interface Observable<T> {
    void registerObserver(T observer);
    void unregisterObserver(T observer);
    ImmutableSet<T> getObservers();
}
