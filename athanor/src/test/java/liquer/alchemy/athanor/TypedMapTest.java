package liquer.alchemy.athanor;

import liquer.alchemy.athanor.json.Json;
import org.junit.Assert;
import org.junit.Test;

public class TypedMapTest {

    @Test
    public void tryStringValue() {
        String[] args = Json.assign(String[].class, "[\"--file\", null, \"-f\", \"a.txt\"]");
        TypedMap tm = new TypedMap(Yash.of(args));
        Assert.assertEquals("a.txt", tm.tryStringValue("--file", "-f").get());
    }

    @Test
    public void tryIntegerValue() {
        String[] args = Json.assign(String[].class, "[\"--file\", \"1\", \"-f\", \"2\"]");
        TypedMap tm = new TypedMap(Yash.of(args));
        Assert.assertEquals(1, tm.tryIntegerValue("--file", "-f").get().intValue());
    }
}
