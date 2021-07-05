package me.hypherionmc.storagedrawers.api.event;

import me.hypherionmc.storagedrawers.api.storage.IDrawer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event is called when a drawer has been bound to a new item.  This is
 * and opportunity for mods to cache extended data with the drawer.
 *
 * This event is also called when the drawer is changed to empty.
 */
public interface DrawerPopulatedEvent
{

    Event<DrawerPopulatedEvent> EVENT = EventFactory.createArrayBacked(DrawerPopulatedEvent.class, listeners -> (drawer) -> {
        for (DrawerPopulatedEvent drawerPopulatedEvent : listeners) {
            drawerPopulatedEvent.drawerPopulatedEvent(drawer);
        }
    });

    void drawerPopulatedEvent(IDrawer drawer1);
}
