/**
 * The classes that are marked with <b>live</b> are ones that exhibit behaviour only necessary in a live (mostly Bukkit)
 * environment. Classes in the module {@code CoreBase} are all independent of Bukkit and do not need certain behavior to
 * be implemented.
 *
 * An example of this behavior is the {@link net.cogzmc.core.player.mongo.COfflineMongoPlayer} versus {@link net.cogzmc.core.player.mongo.COfflineLiveMongoPlayer}
 *  you can see that the <b>live</b> class registers as a {@link net.cogzmc.core.player.mongo.GroupReloadObserver} whereas the regular class does not.
 */
package net.cogzmc.core.player.mongo;