package liquer.alchemy.fun;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An OO approach of the lambda calculus that includes currying
 */
public interface Func {

	/** () -&gt; b */
	@FunctionalInterface interface Func0<B> extends Supplier<B> {
		default B apply() { return this.get(); }
		B get();
	}
	/** (a0) -&gt; b */
	@FunctionalInterface interface Func1<A0, B> extends Function<A0, B> {
		default Func1<A0, B> andThen(Func1<B, B> next) {
			return (A0 arg0) -> next.apply(this.apply(arg0));
		}
		default Func1<A0, B> compose(Func1<A0, A0> next) {
			return (A0 arg0) -> this.apply(next.apply(arg0));
		}
		default Func1<A0, B> apply() { return this; }
		B apply(A0 arg0);
		
	}
	/** (a0, a1) -&gt; b */
	@FunctionalInterface interface Func2<A0, A1, B> extends BiFunction<A0, A1, B> {
		default Func2<A0, A1, B> apply() { return this; }
		default Func1<A1, B> apply(final A0 arg0) {
			return (arg1) -> this.apply(arg0, arg1); 
		}
		B apply(A0 arg0, A1 arg1);
	}
	/** (a0, a1, a2) -&gt; b */
	@FunctionalInterface interface Func3<A0, A1, A2, B> {
		default Func3<A0, A1, A2, B> apply() { return this; }
		default Func2<A1, A2, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2) -> this.apply(arg0, arg1, arg2);
		}
		default Func1<A2, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2) ->  this.apply(arg0, arg1, arg2);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2);
	}
	/** (a0, a1, a2, a3) -&gt; b */
	@FunctionalInterface interface Func4<A0, A1, A2, A3, B> {
		default Func4<A0, A1, A2, A3, B> apply() { return this; }
		default Func3<A1, A2, A3, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3) -> this.apply(arg0, arg1, arg2, arg3);
		}
		default Func2<A2, A3, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3) -> this.apply(arg0, arg1, arg2, arg3);
		}
		default Func1<A3, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3) -> this.apply(arg0, arg1, arg2, arg3);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3);
	}
	/** (a0, a1, a2, a3, a4) -&gt; b */
	@FunctionalInterface interface Func5<A0, A1, A2, A3, A4, B> {
		default  Func5<A0, A1, A2, A3, A4, B> apply() { return this; }
		default  Func4<A1, A2, A3, A4, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4) -> this.apply(arg0, arg1, arg2, arg3, arg4);
		}
		default  Func3<A2, A3, A4, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4) -> this.apply(arg0, arg1, arg2, arg3, arg4);
		}
		default  Func2<A3, A4, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4) -> this.apply(arg0, arg1, arg2, arg3, arg4);
		}
		default  Func1<A4, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4) -> this.apply(arg0, arg1, arg2, arg3, arg4);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);
	}
	/** (a0, a1, a2, a3, a4, a5) -&gt; b */
	@FunctionalInterface interface Func6<A0, A1, A2, A3, A4, A5, B> {
		default  Func6<A0, A1, A2, A3, A4, A5, B> apply() { return this; }
		default  Func5<A1, A2, A3, A4, A5, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5);
		}
		default  Func4<A2, A3, A4, A5, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5);
		}
		default  Func3<A3, A4, A5, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5);
		}
		default  Func2<A4, A5, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5);
		}
		default  Func1<A5, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);
	}
	/** (a0, a1, a2, a3, a4, a5, a6) -&gt; b */
	@FunctionalInterface interface Func7<A0, A1, A2, A3, A4, A5, A6, B> {
		
		default  Func7<A0, A1, A2, A3, A4, A5, A6, B> apply() { return this; }
		default  Func6<A1, A2, A3, A4, A5, A6, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		}
		default  Func5<A2, A3, A4, A5, A6, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		}
		default  Func4<A3, A4, A5, A6, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		}
		default  Func3<A4, A5, A6, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		}
		default  Func2<A5, A6, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		}
		default  Func1<A6, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7) -&gt; b */
	@FunctionalInterface interface Func8<A0, A1, A2, A3, A4, A5, A6, A7, B> {
		default  Func8<A0, A1, A2, A3, A4, A5, A6, A7, B> apply() { return this; }
		default  Func7<A1, A2, A3, A4, A5, A6, A7, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		}
		default  Func6<A2, A3, A4, A5, A6, A7, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		}
		default  Func5<A3, A4, A5, A6, A7, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		}
		default  Func4<A4, A5, A6, A7, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		}
		default  Func3<A5, A6, A7, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		}
		default  Func2<A6, A7, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7); 
		}
		default  Func1<A7, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8) -&gt; b */
	@FunctionalInterface interface Func9<A0, A1, A2, A3, A4, A5, A6, A7, A8, B> {
		default  Func9<A0, A1, A2, A3, A4, A5, A6, A7, A8, B> apply() { return this; }
		default  Func8<A1, A2, A3, A4, A5, A6, A7, A8, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}
		default  Func7<A2, A3, A4, A5, A6, A7, A8, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}
		default  Func6<A3, A4, A5, A6, A7, A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}
		default  Func5<A4, A5, A6, A7, A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}
		default  Func4<A5, A6, A7, A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}
		default  Func3<A6, A7, A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}
		default  Func2<A7, A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}
		default  Func1<A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9) -&gt; b */
	@FunctionalInterface interface Func10<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, B> {
		default  Func10<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, B> apply() { return this; }
		default  Func9<A1, A2, A3, A4, A5, A6, A7, A8, A9, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
		}
		default  Func8<A2, A3, A4, A5, A6, A7, A8, A9, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
		}
		default  Func7<A3, A4, A5, A6, A7, A8, A9, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
		}
		default  Func6<A4, A5, A6, A7, A8, A9, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
		}
		default  Func5<A5, A6, A7, A8, A9, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
		}
		default  Func4<A6, A7, A8, A9, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8, A9 arg9) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
		}
		default  Func3<A7, A8, A9, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8, A9 arg9) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
		}
		default  Func2<A8, A9, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8, A9 arg9) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
		}
		default  Func1<A9, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
			return (A9 arg9) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10) -&gt; b */
	@FunctionalInterface interface Func11<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, B> {
		default  Func11<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, B> apply() { return this; }
		default  Func10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
		default  Func9<A2, A3, A4, A5, A6, A7, A8, A9, A10, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
		default  Func8<A3, A4, A5, A6, A7, A8, A9, A10, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
		default  Func7<A4, A5, A6, A7, A8, A9, A10, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
		default  Func6<A5, A6, A7, A8, A9, A10, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
		default  Func5<A6, A7, A8, A9, A10, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
		default  Func4<A7, A8, A9, A10, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8, A9 arg9, A10 arg10) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
		default  Func3<A8, A9, A10, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8, A9 arg9, A10 arg10) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
		default  Func2<A9, A10, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
			return (A9 arg9, A10 arg10) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
		default  Func1<A10, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9) {
			return (A10 arg10) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11) -&gt; b */
	@FunctionalInterface interface Func12<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, B> {
		default  Func12<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, B> apply() { return this; }
		default  Func11<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
		}
		default   Func10<A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
		}
		default   Func9<A3, A4, A5, A6, A7, A8, A9, A10, A11, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
		}
		default   Func8<A4, A5, A6, A7, A8, A9, A10, A11, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
		}
		default   Func7<A5, A6, A7, A8, A9, A10, A11, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
		}
		default  Func6<A6, A7, A8, A9, A10, A11, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
		}
		default   Func5<A7, A8, A9, A10, A11, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
		}
		default   Func4<A8, A9, A10, A11, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8, A9 arg9, A10 arg10, A11 arg11) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
		}
		default   Func3<A9, A10, A11, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
			return (A9 arg9, A10 arg10, A11 arg11) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
		}
		default   Func2<A10, A11, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9) {
			return (A10 arg10, A11 arg11) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
		}
		default   Func1<A11, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10) {
			return (A11 arg11) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12) -&gt; b */
	@FunctionalInterface interface Func13<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, B> {
		default  Func13<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, B> apply() { return this; }
		default  Func12<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		default  Func11<A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		default  Func10<A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		default  Func9<A4, A5, A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		default  Func8<A5, A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		default  Func7<A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		default  Func6<A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		default  Func5<A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		default  Func4<A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
			return (A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		default  Func3<A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9) {
			return (A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		default  Func2<A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10) {
			return (A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		default  Func1<A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11) {
			return (A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13) -&gt; b */
	@FunctionalInterface interface Func14<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, B> {
		default  Func14<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, B> apply() { return this; }
		default  Func13<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func12<A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func11<A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func10<A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func9<A5, A6, A7, A8, A9, A10, A11, A12, A13, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func8<A6, A7, A8, A9, A10, A11, A12, A13, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func7<A7, A8, A9, A10, A11, A12, A13, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func6<A8, A9, A10, A11, A12, A13, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func5<A9, A10, A11, A12, A13, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
			return (A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func4<A10, A11, A12, A13, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9) {
			return (A10 arg10, A11 arg11, A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func3<A11, A12, A13, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10) {
			return (A11 arg11, A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func2<A12, A13, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11) {
			return (A12 arg12, A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		default  Func1<A13, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12) {
			return (A13 arg13) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) -&gt; b */
	@FunctionalInterface interface Func15<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, B> {
		default  Func15<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, B> apply() { return this; }
		default  Func14<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func13<A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func12<A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func11<A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func10<A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func9<A6, A7, A8, A9, A10, A11, A12, A13, A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func8<A7, A8, A9, A10, A11, A12, A13, A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func7<A8, A9, A10, A11, A12, A13, A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func6<A9, A10, A11, A12, A13, A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
			return (A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func5<A10, A11, A12, A13, A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9) {
			return (A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func4<A11, A12, A13, A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10) {
			return (A11 arg11, A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func3<A12, A13, A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11) {
			return (A12 arg12, A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		default  Func2<A13, A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12) {
			return (A13 arg13, A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14); 
		}
		default  Func1<A14, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13) {
			return (A14 arg14) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15) -&gt; b */
	@FunctionalInterface interface Func16<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, B> {
		default  Func16<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, B> apply() { return this; }
		default  Func15<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func14<A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func13<A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func12<A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func11<A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func10<A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func9<A7, A8, A9, A10, A11, A12, A13, A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func8<A8, A9, A10, A11, A12, A13, A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func7<A9, A10, A11, A12, A13, A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
			return (A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func6<A10, A11, A12, A13, A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9) {
			return (A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func5<A11, A12, A13, A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10) {
			return (A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func4<A12, A13, A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11) {
			return (A12 arg12, A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func3<A13, A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12) {
			return (A13 arg13, A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func2<A14, A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13) {
			return (A14 arg14, A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		default  Func1<A15, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14) {
			return (A15 arg15) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) -&gt; b */
	@FunctionalInterface interface Func17<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, B> {
		default  Func17<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, B> apply() { return this; }
		default  Func16<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func15<A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func14<A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func13<A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func12<A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func11<A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func10<A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func9<A8, A9, A10, A11, A12, A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func8<A9, A10, A11, A12, A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
			return (A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func7<A10, A11, A12, A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9) {
			return (A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func6<A11, A12, A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10) {
			return (A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func5<A12, A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11) {
			return (A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func4<A13, A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12) {
			return (A13 arg13, A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func3<A14, A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13) {
			return (A14 arg14, A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func2<A15, A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14) {
			return (A15 arg15, A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		default  Func1<A16, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14, final A15 arg15) {
			return (A16 arg16) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17) -&gt; b */
	@FunctionalInterface interface Func18<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, B> {
		default  Func18<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, B> apply() { return this; }
		default  Func17<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func16<A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func15<A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func14<A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func13<A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func12<A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func11<A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func10<A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17); 
		}
		default  Func9<A9, A10, A11, A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
			return (A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func8<A10, A11, A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9) {
			return (A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func7<A11, A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10) {
			return (A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func6<A12, A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11) {
			return (A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func5<A13, A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12) {
			return (A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func4<A14, A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13) {
			return (A14 arg14, A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func3<A15, A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14) {
			return (A15 arg15, A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func2<A16, A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14, final A15 arg15) {
			return (A16 arg16, A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		default  Func1<A17, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14, final A15 arg15, final A16 arg16) {
			return (A17 arg17) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18) -&gt; b */
	@FunctionalInterface interface Func19<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, B> {
		default  Func19<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, B> apply() { return this; }
		default  Func18<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func17<A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func16<A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func15<A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func14<A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func13<A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func12<A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func11<A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func10<A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
			return (A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func9<A10, A11, A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9) {
			return (A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func8<A11, A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10) {
			return (A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func7<A12, A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11) {
			return (A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func6<A13, A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12) {
			return (A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func5<A14, A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13) {
			return (A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func4<A15, A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14) {
			return (A15 arg15, A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func3<A16, A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14, final A15 arg15) {
			return (A16 arg16, A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func2<A17, A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14, final A15 arg15, final A16 arg16) {
			return (A17 arg17, A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		default  Func1<A18, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14, final A15 arg15, final A16 arg16, final A17 arg17) {
			return (A18 arg18) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18);
	}
	/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19) -&gt; b */
	@FunctionalInterface interface Func20<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> {
		default  Func20<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply() { return this; }
		default  Func19<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0) {
			return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func18<A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1) {
			return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func17<A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
			return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func16<A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
			return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func15<A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
			return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func14<A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
			return (A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func13<A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
			return (A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func12<A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
			return (A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func11<A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
			return (A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func10<A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9) {
			return (A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func9<A11, A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10) {
			return (A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func8<A12, A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11) {
			return (A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func7<A13, A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12) {
			return (A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func6<A14, A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13) {
			return (A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19); 
		}
		default  Func5<A15, A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14) {
			return (A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func4<A16, A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14, final A15 arg15) {
			return (A16 arg16, A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func3<A17, A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14, final A15 arg15, final A16 arg16) {
			return (A17 arg17, A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func2<A18, A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14, final A15 arg15, final A16 arg16, final A17 arg17) {
			return (A18 arg18, A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		default  Func1<A19, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11, final A12 arg12, final A13 arg13, final A14 arg14, final A15 arg15, final A16 arg16, final A17 arg17, final A18 arg18) {
			return (A19 arg19) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
		}
		B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12, A13 arg13, A14 arg14, A15 arg15, A16 arg16, A17 arg17, A18 arg18, A19 arg19);
	}
}
