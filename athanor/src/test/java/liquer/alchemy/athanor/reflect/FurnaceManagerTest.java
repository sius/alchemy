package liquer.alchemy.athanor.reflect;

import liquer.alchemy.athanor.json.Json;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class FurnaceManagerTest {

	@Test
	void getConfig() {
		
		FurnaceConfig config = FurnaceManager.getInstance().getConfig();
		System.out.println(Json.stringify(config));
		//assertTrue(Locale.getDefault().equals(Locale.US));
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
