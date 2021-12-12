package liquer.alchemy.athanor.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonAssignPrimitives {

	@Test
	void assignPrimitiveBoolean() {
		boolean actual0 = Json.assign(boolean.class, false);
		assertFalse(actual0);
		boolean actual1 = Json.assign(boolean.class, true);
		assertTrue(actual1);
		boolean actual2 = Json.assign(true, false);
		assertFalse(actual2);
		boolean actual3 = Json.assign(false, true);
		assertTrue(actual3);
		boolean actual4 = Json.assign(false, false, false, true);
		assertTrue(actual4);
	}
	
	@Test
	void assignBoolean() {
		Boolean actual0 = Json.assign(Boolean.class, false);
		assertFalse(actual0);
		Boolean actual1 = Json.assign(Boolean.class, true);
		assertTrue(actual1);
		Boolean actual2 = Json.assign(Boolean.FALSE, true);
		assertTrue(actual2);
		Boolean actual3 = Json.assign(Boolean.TRUE, false);
		assertFalse(actual3);
		boolean actual4 = Json.assign(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, true);
		assertTrue(actual4);
	}
	
	@Test
	void assignInt() {
		int actual0 = Json.assign(int.class, 1);
		assertEquals(1, actual0);
		int actual1 = Json.assign(int.class, 1, 2);
		assertEquals(2, actual1);
		Integer actual2 = Json.assign(Integer.class, 1);
		assertEquals((Integer)1, actual2);
		int actual3 = Json.assign(0, 1);
		assertEquals(1, actual3);
		int actual4 = Json.assign(0, 1, 2, 3, 4);
		assertEquals(4, actual4);
	}
	
	@Test
	void assignLong() {
		long actual0 = Json.assign(long.class, 1L);
		assertEquals(1L, actual0);
		long actual1 = Json.assign(long.class, 1L, 2L);
		assertEquals(2L, actual1);
		Long actual2 = Json.assign(Long.class, 1L);
		assertEquals((Long)1L, actual2);
		long actual3 = Json.assign(0L, 1L);
		assertEquals(1, actual3);
		long actual4 = Json.assign(0L, 1L);
		assertEquals(1, actual4);
		long actual5 = Json.assign(0L, 1L, 2L, 3L, 4L);
		assertEquals(4, actual5);
	}

}
