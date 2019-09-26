package liquer.alchemy.athanor.json;

import liquer.alchemy.athanor.json.Json;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JsonAssignMaps {
	
	private Map<String, Object> m1 = new HashMap<>();
	private Map<String, Object> m2 = new HashMap<>();
	
	public JsonAssignMaps() {
		m1.put("a", "b");
		m1.put("b", "c");
		
		m2.put("c", "c");
		m2.put("b", "b");
	}
	
	@Test
	public void assignMap() {
		System.out.println(Json.stringify(Json.assign(Map.class, m2, m1), 2));
		
	}
	
	@Test
	public void reassignMap() {
		System.out.println(Json.stringify(Json.assign(m1, m2), 2));
		System.out.println(Json.stringify(Json.reassign(m1, m2), 2));
	}
}
