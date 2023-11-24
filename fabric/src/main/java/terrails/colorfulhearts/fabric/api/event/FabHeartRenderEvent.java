package terrails.colorfulhearts.fabric.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import terrails.colorfulhearts.api.event.HeartRenderEvent;

import java.util.function.Consumer;

/**
 * A set of events useful to render any overlays
 */
public class FabHeartRenderEvent {

    public static final Event<Consumer<HeartRenderEvent.Pre>> PRE = createEvent();
    public static final Event<Consumer<HeartRenderEvent.Post>> POST = createEvent();

    private static <T> Event<Consumer<T>> createEvent() {
        return EventFactory.createArrayBacked(Consumer.class, listeners -> event -> {
            for (Consumer<T> listener : listeners) {
                listener.accept(event);
            }
        });
    }
}
