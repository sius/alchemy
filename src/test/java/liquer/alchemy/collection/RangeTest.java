package liquer.alchemy.collection;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class RangeTest {

	@Test
	public void rangeNull_Null() {
		Range<Integer> r = Range.of((Integer)null,(Integer)null);
		assertFalse(r.isValid());
		assertTrue(r.succ(0) == null);
		assertTrue(r.includes(0));
		assertTrue(r.start() == null);
		assertTrue(r.end() == null);
		assertTrue(r.toList().size() == 0);
		assertFalse(r.isAscending());
		assertFalse(r.isDescending());
	}

	@Test
	public void range0_Null() {
		Range<Integer> r = Range.of(0, null);
		assertFalse(r.isValid());
		assertTrue(r.succ(0) == null);
		assertTrue(r.includes(0));
		assertTrue(r.start() == 0);
		assertTrue(r.end() == null);
		assertTrue(r.toList().size() == 0);
		assertFalse(r.isAscending());
		assertFalse(r.isDescending());
	}

	@Test
	public void rangeNull_0() {
		Range<Integer> r = Range.of(null, 0);
		assertFalse(r.isValid());
		assertTrue(r.succ(0) == null);
		assertTrue(r.includes(0));
		assertTrue(r.start() == null);
		assertTrue(r.end() == 0);
		assertTrue(r.toList().size() == 0);
		assertFalse(r.isAscending());
		assertFalse(r.isDescending());
	}

	@Test
	public void range0_0() {
		assertTrue(Range.of(0,0).isValid());
		assertTrue(Range.of(0,0).succ(0) == 1);
		assertTrue(Range.of(0,0).includes(0));
		assertTrue(Range.of(0,0).start() == 0);
		assertTrue(Range.of(0,0).end() == 0);
		assertTrue(Range.of(0,0).toList().size() == 1);
		assertTrue(Range.of(0,0).toList().get(0) == 0);
		assertFalse(Range.of(0,0).isAscending());
		assertFalse(Range.of(0,0).isDescending());
	}
	@Test
	public void range1_1() {
		assertTrue(Range.of(1,1).isValid());
		assertTrue(Range.of(1,1).succ(1) == 2);
		assertTrue(Range.of(1,1).includes(1));
		assertTrue(Range.of(1,1).start() == 1);
		assertTrue(Range.of(1,1).end() == 1);
		assertTrue(Range.of(1,1).toList().size() == 1);
		assertTrue(Range.of(1,1).toList().get(0) == 1);
		assertFalse(Range.of(1,1).isAscending());
		assertFalse(Range.of(1,1).isDescending());
	}
	@Test
	public void range0_1() {
		assertTrue(Range.of(0,1).isValid());
		assertTrue(Range.of(0,1).succ(1) == 2);
		assertTrue(Range.of(0,1).includes(1));
		assertTrue(Range.of(0,1).start() == 0);
		assertTrue(Range.of(0,1).end() == 1);
		assertTrue(Range.of(0,1).toList().size() == 2);
		assertTrue(Range.of(0,1).isAscending());
		assertFalse(Range.of(0,1).isDescending());
	}
	@Test
	public void range1_0() {
		assertTrue(Range.of(1,0).isValid());
		assertTrue(Range.of(1,0).succ(1) == 0);
		assertTrue(Range.of(1,0).includes(1));
		assertTrue(Range.of(1,0).start() == 1);
		assertTrue(Range.of(1,0).end() == 0);
		assertTrue(Range.of(1,0).toList().size() == 2);
		assertFalse(Range.of(1,0).isAscending());
		assertTrue(Range.of(1,0).isDescending());
	}
}