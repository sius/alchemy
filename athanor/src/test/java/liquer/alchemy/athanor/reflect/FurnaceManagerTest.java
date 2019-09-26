package liquer.alchemy.athanor.reflect;

import liquer.alchemy.athanor.json.Json;
import liquer.alchemy.athanor.reflect.FurnaceConfig;
import liquer.alchemy.athanor.reflect.FurnaceManager;
import org.junit.Test;

import java.util.Locale;
import java.util.TimeZone;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;


public class FurnaceManagerTest {

	@Test
	public void getConfig() {
		
		FurnaceConfig config = FurnaceManager.getInstance().getConfig();
		System.out.println(Json.stringify(config));
		assertTrue(Locale.getDefault().equals(Locale.US));
		assertTrue(config.getTimeZone().equals(TimeZone.getTimeZone("GMT")));
		assertNotNull(config.getSimpleDateFormat());
		
		FurnaceManager.getInstance().reloadConfig("/furnaceCET.json");
		config = FurnaceManager.getInstance().getConfig();
		System.out.println(Json.stringify(config));
		assertTrue(config.getTimeZone().equals(TimeZone.getTimeZone("CET")));

		FurnaceManager.getInstance().reloadConfig("/furnace.json");
		config = FurnaceManager.getInstance().getConfig();
		System.out.println(Json.stringify(config));
		assertTrue(config.getTimeZone().equals(TimeZone.getTimeZone("GMT")));
		
	}
}
