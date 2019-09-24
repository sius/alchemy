package liquer.alchemy.collection;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Range<A extends Comparable<A>> {

	public static Range<BigDecimal> of(final BigDecimal start, final BigDecimal end) { return of(start, end, 1); }
    public static Range<BigDecimal> of(final BigDecimal start, final BigDecimal end, final int step) {
    	Function<BigDecimal, BigDecimal> succ = x -> x;
			if (start != null && end != null) {
				final int s = (step == 0) ? 1 : step;
				succ = (start.compareTo(end) <= 0) ? x -> x.add(BigDecimal.valueOf(s)) : x -> x.add(BigDecimal.valueOf(-s));
			}
    	return new Range<>(start, end, succ);
    }
    public static Range<BigInteger> of(final BigInteger start, final BigInteger end) { return of(start, end, 1); }
    public static Range<BigInteger> of(final BigInteger start, final BigInteger end, final int step) {
	    Function<BigInteger, BigInteger> succ = x -> x;
			if (start != null && end != null) {
				final int s = (step == 0) ? 1 : step;
				succ = (start.compareTo(end) <= 0) ?
					x ->  x.add(BigInteger.valueOf(s)) : x -> x.add(BigInteger.valueOf(-s));
			}
    	return new Range<>(start, end, succ);
    }
    public static Range<Long> of(final Long start, final Long end) { return of(start, end, 1); }
    public static Range<Long> of(final Long start, final Long end, final int step) {
	    Function<Long, Long> succ = x -> x;
			if (start != null && end != null) {
				final int s = (step == 0) ? 1 : step;
				succ = (start.compareTo(end) <= 0) ? x ->  x + s : x -> x - s;

			}
    	return new Range<>(start, end, succ);
    }
    public static Range<Integer> of(final Integer start, final Integer end) { return of(start, end, 1); }
    public static Range<Integer> of(final Integer start, final Integer end, int step) {
	    Function<Integer, Integer> succ = x -> x;
			if (start != null && end != null) {
				final int s = (step == 0) ? 1 : step;
				succ = (start.compareTo(end) <= 0) ? x -> x + s : x -> x - s;

			}
    	return new Range<>(start, end, succ);
    }
    public static Range<Date> of(final Date start, final Date end) { return of(start, end, 1, Calendar.DATE); }
    public static Range<Date> of(final Date start, final Date end, final int step, final int unit) {
	    Function<Date, Date> succ = x -> x;
			if (start != null && end != null) {
				final int s = (step == 0) ? 1 : step;
				final Calendar c = Calendar.getInstance();
				succ = (start.compareTo(end) <= 0)
						? x -> { c.setTime(x); c.add(unit, s); return c.getTime(); }
						: x -> { c.setTime(x); c.add(unit, -s); return c.getTime(); };

			}
    	return new Range<>(start, end, succ);
    }

    private A s, e;
    private Function<A, A> succ;

    private Range(A start, A end, Function<A, A> succ) {
        s = start;
        e = end;
        this.succ = succ;
    }
    public Iterator<A> iterator() {
			return new Iterator<A>() {
				private A x = null;
				public boolean hasNext() {
					if (s == null || e == null) return false;
					else {
						return x == null || (isAscending() ? succ(x).compareTo(e) <= 0 :
								(isDescending() && succ(x).compareTo(e) >= 0));
					}
				}
				public A next() {
					x = (x == null) ? s : succ(x);
					return x;
				}
				public void remove() {  }
			};
		}
		public Stream<A> stream() {
    	return StreamSupport.stream(
			    Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED),
			    false);
		}
    public List<A> toList() {
	    return stream().collect(Collectors.toList());
    }

    public A start() { return s; }
    public A end() { return e; }
    public A succ(A x) { return isInfinite() ? null : succ.apply(x); }
		public boolean isValid() { return !isInfinite(); }
    public boolean isDescending() { return (!isInfinite() && (s.compareTo(e) != 0) && (succ(s).compareTo(s) < 0)); }
    public boolean isAscending() { return (!isInfinite() && (s.compareTo(e) != 0) && (succ(s).compareTo(s) > 0)); }
    public boolean isLeftInfinite() { return (s == null); }
    public boolean isRightInfinite() { return (e == null); }
    public boolean isLeftInfiniteClosed() { return (s == null && e != null); }
    public boolean isRightInfiniteClosed() { return (s != null && e == null); }
    public boolean isInfinite() { return (s == null || e == null); }
    public boolean includes(A x) {
    	boolean __ = false;
    	if (x != null) {
    		if (isAscending()) {
			    if (!isInfinite()) __ = ((x.compareTo(s) >= 0) && (x.compareTo(e) <= 0));
			    else if (isLeftInfiniteClosed()) __ = (x.compareTo(e) <= 0);
			    else if (isRightInfiniteClosed()) __ = (x.compareTo(e) >= 0);
			    else __ = isInfinite();
		    } else if (isDescending()) {
			    if (!isInfinite()) __ = ((x.compareTo(s) <= 0) && (x.compareTo(e) >= 0));
			    else if (isLeftInfiniteClosed()) __ = (x.compareTo(e) >= 0);
			    else if (isRightInfiniteClosed()) __ = (x.compareTo(e) <= 0);
			    else __ = isInfinite();
		    } else {
			    __ = (isInfinite() || x.compareTo(s) == 0 || x.compareTo(e) == 0);
		    }
    	}
    	return __;
    }
    /**
     * @param x the searched value
     * @return true if x is further to the left
     */
    public boolean isFurtherLeft(A x) {
    	return (x != null) && (!isLeftInfinite() && (x.compareTo(s) < 0));
    }
    /**
     * @param x the searched value
     * @return true if x is further to the left
     */
    public boolean isFurtherRight(A x) {
    	return (x != null) && (!isRightInfinite() && (x.compareTo(e) > 0));
    }
    /**
     *
     * @param other Range
     * @return the union
     */
    public Range<A> union(Range<A> other) {
    	return merge(other);
    }
    /**
     *
     * @param other Range
     * @return the merge result
     */
    public Range<A> merge(Range<A> other) {
    	Range<A> range = new Range<>(s, e, succ);
    	if (other != null) {
    		final A merged_start =
    			(other.isLeftInfinite() ? null :
    				(isFurtherLeft(other.start()) ? other.start() : start()));
    		final A merged_end =
    			(other.isRightInfinite() ? null :
    				(isFurtherRight(other.end()) ? other.end() : end()));
	    	range = new Range<>(merged_start, merged_end, succ);
    	}
    	return range;
    }
    /**
     *
     * @param other Range
     * @return the Intersection
     */
    public Range<A> intersect(Range<A> other) {
    	if (other == null) return null;
			A start, end;

			if (includes(other.start())) start = other.start();
			else if (other.includes(this.start())) start = this.start();
			else return null;

			if (includes(other.end())) end = other.end();
			else if (other.includes(this.end())) end = this.end();
			else return null;

			return new Range<>(start, end, succ);
    }
    @Override public String toString() {
    	final String left = (s == null ? ""  : s.toString());
    	final String right = (e == null ? ""  : e.toString());
        return String.format("%1$s..%2$s", left, right);
    }
}
