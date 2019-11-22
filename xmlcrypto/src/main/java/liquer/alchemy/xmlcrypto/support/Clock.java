package liquer.alchemy.xmlcrypto.support;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class Clock implements Timer, Log {

    public static Clock set() { return new Clock(); }
    public static Clock set(Consumer<Timer> block) {
        return new Clock(block);
    }

    private int lap;
    private long start;
    private List<Long> stops;
    private List<String> stopMessages;

    private final Consumer<Timer> block;

    private Clock() { this(t -> {}); }
    private Clock(Consumer<Timer> block) {
        this.block = block;
        reset();
    }

    public int getLap() {
        return lap;
    }

    public Clock reset() {
        this.lap = 0;
        this.stops = new ArrayList<>();
        this.stopMessages = new ArrayList<>();
        return this;
    }

    public Clock go() {
        start = System.currentTimeMillis();
        block.accept(this);
        return this;
    }

    public Clock stop() {
        return stop(null);
    }

    public Clock stop(String stopMessage) {
        stops.add(lap, System.currentTimeMillis());
        stopMessages.add(lap++, stopMessage == null ? "" : stopMessage);
        return this;
    }

    public long total() {
        if (stops.isEmpty()) {
            return 0;
        }
        return stops.get(stops.size() -1) - start;
    }

    public long milliseconds(int lap) {
        if (lap > stops.size() -1) {
            return stops.get(stops.size() -1) - start;
        }
        if (lap > 0) {
            return stops.get(lap) - stops.get(lap-1);
        }
        return stops.get(lap) - start;
    }

    @Override public String toString() {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < lap; i++) {
            ret.add("lap " + i + " (" + stopMessages.get(i) + "): " + milliseconds(i) + " ms");
        }
        ret.add("total :" + total());
        return String.join("\n", ret);
    }

    public Log println() {
        return println( toString(), System.out);
    }

    @Override
    public Log println(String info) {
        return println( info + ":\n" + toString(), System.out);
    }
}
