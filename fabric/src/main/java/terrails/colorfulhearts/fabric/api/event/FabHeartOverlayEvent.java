package terrails.colorfulhearts.fabric.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import terrails.colorfulhearts.api.event.HeartOverlayEvent;

import java.util.function.Consumer;

/**
 * A set of events useful to render any overlays
 */
public class FabHeartOverlayEvent {

    public static final Event<Consumer<HeartOverlayEvent.Pre>> PRE = createEvent();
    public static final Event<Consumer<HeartOverlayEvent.Post>> POST = createEvent();

    private static <T> Event<Consumer<T>> createEvent() {
        return EventFactory.createArrayBacked(Consumer.class, listeners -> event -> {
            for (Consumer<T> listener : listeners) {
                listener.accept(event);
            }
        });
    }
}
