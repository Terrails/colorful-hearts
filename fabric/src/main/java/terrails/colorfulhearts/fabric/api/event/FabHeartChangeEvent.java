package terrails.colorfulhearts.fabric.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class FabHeartChangeEvent {

    public static final Event<Runnable> EVENT = EventFactory.createArrayBacked(Runnable.class, listeners -> () -> {
        for (Runnable listener : listeners) {
            listener.run();
        }
    });
}
