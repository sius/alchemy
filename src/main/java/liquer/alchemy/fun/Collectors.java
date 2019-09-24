package liquer.alchemy.fun;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;
import java.util.stream.Collector;

public final class Collectors {
	private Collectors() {

	}

	public static <A> Collector<A, ?, Stack<A>> toStack() {
		return Collector.of(
			Stack::new,
			Stack::push,
			(res1, res2) -> {
				res1.addAll(res2);
				return res1;
			},
			Collector.Characteristics.CONCURRENT,
			Collector.Characteristics.IDENTITY_FINISH,
			Collector.Characteristics.UNORDERED
		);
	}

	public static <A> Collector<A, ?, Vector<A>> toVector() {
		return Collector.of(
			Vector::new,
			Vector::add,
			(res1, res2) -> {
				res1.addAll(res2);
				return res1;
			},
			Collector.Characteristics.CONCURRENT,
			Collector.Characteristics.IDENTITY_FINISH,
			Collector.Characteristics.UNORDERED
		);
	}


	public static <A> Collector<A, ?, Queue<A>> toQueue() {
		return Collector.of(
			LinkedList::new,
			Queue::add,
			(res1, res2) -> {
				res1.addAll(res2);
				return res1;
			},
			Collector.Characteristics.CONCURRENT,
			Collector.Characteristics.IDENTITY_FINISH,
			Collector.Characteristics.UNORDERED
		);
	}
}
