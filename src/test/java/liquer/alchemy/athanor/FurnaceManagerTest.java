package liquer.alchemy.athanor;

import liquer.alchemy.json.Json;
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
		assertTrue(config.getLocale().equals(Locale.US));
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
