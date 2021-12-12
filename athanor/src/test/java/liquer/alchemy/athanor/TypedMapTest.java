package liquer.alchemy.athanor;

import liquer.alchemy.athanor.json.Json;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypedMapTest {

    @Test
    void tryStringValue() {
        String[] args = Json.assign(String[].class, "[\"--file\", null, \"-f\", \"a.txt\"]");
        TypedMap tm = new TypedMap(Yash.of(args));
        assertEquals("a.txt", tm.tryStringValue("--file", "-f").get());
    }

    @Test
    void tryIntegerValue() {
        String[] args = Json.assign(String[].class, "[\"--file\", \"1\", \"-f\", \"2\"]");
        TypedMap tm = new TypedMap(Yash.of(args));
        assertEquals(1, tm.tryIntegerValue("--file", "-f").get().intValue());
    }
}
