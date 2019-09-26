/*
Copyright (c) 2014 dive

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

There are no usage restrictions.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/*
Copyright (c) 2005 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package liquer.alchemy.athanor.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

/**
 * This implementation is a Java implementation and parser extension of the
 * <a href="http://www.json.org/JSON_checker/">JSON_checker</a> program written in C.
 * JSON_checker is a push down Automaton that very quickly determines if a JSON text is syntactically correct.
 * It could be used to filter inputs to a system, or to verify that the outputs of a system are syntactically correct.
 * It could be adapted to produce a very fast JSON parser.
 */
public final class JsonReader implements AutoCloseable{

	private enum Mode { ARRAY, DONE, KEY, OBJECT }

	private static final class JVal {
		private final Object value;
		private boolean dropVal;
		boolean dropVal() {return dropVal;}
		private JVal(StringBuilder val) {
			final String strVal = val == null ? "" : val.toString();
			final String trimVal = leftTrim(strVal, ' ', '\n', '\r', '\t', '\f');
			if (trimVal.startsWith("\"")) value = trimVal.substring(1);
			else {
				switch (trimVal) {
					case "true":
						value = true;
						break;
					case "false":
						value = false;
						break;
					case "null":
						value = null;
						break;
					default:
						Object obj = null;
						dropVal = strVal.trim().isEmpty();
						try {
							obj = dropVal ? null : Double.parseDouble(strVal);
						} catch (NumberFormatException e) {
							// no operation
						} finally {
							value = obj;
						}
						break;
				}
			}
		}
		private static String leftTrim(String value, char... array) {
			if (value == null || value.length() == 0 || array.length == 0) return "";
			int trim = 0;
			Arrays.sort(array);
			for (int i = 0; i < value.length(); i++) {
				if (Arrays.binarySearch(array, value.charAt(i)) > -1) {
					trim = i+1;
				} else {
					break;
				}
			}
			return value.substring(trim);
		}
		private JVal(Object val) { value = val; }
	}

	protected static final int __ = -1;     /* the universal error code */

	/* Characters are mapped into these 31 character classes. This allows for
	   a significant reduction in the size of the state transition table. */
	private static final int C_SPACE = 0;  /* space */
	private static final int C_WHITE = 1;  /* other whitespace */
	private static final int C_LCURB = 2;  /* {  */
	private static final int C_RCURB = 3;  /* } */
	private static final int C_LSQRB = 4;  /* [ */
	private static final int C_RSQRB = 5;  /* ] */
	private static final int C_COLON = 6;  /* : */
	private static final int C_COMMA = 7;  /* , */
	private static final int C_QUOTE = 8;  /* " */
	private static final int C_BACKS = 9;  /* \ */
	private static final int C_SLASH = 10;  /* / */
	private static final int C_PLUS = 11;  /* + */
	private static final int C_MINUS = 12;  /* - */
	private static final int C_POINT = 13;  /* . */
	private static final int C_ZERO = 14;  /* 0 */
	private static final int C_DIGIT = 15;  /* 123456789 */
	private static final int C_LOW_A = 16;  /* a */
	private static final int C_LOW_B = 17;  /* b */
	private static final int C_LOW_C = 18;  /* c */
	private static final int C_LOW_D = 19;  /* d */
	private static final int C_LOW_E = 20;  /* e */
	private static final int C_LOW_F = 21;  /* f */
	private static final int C_LOW_L = 22;  /* l */
	private static final int C_LOW_N = 23;  /* n */
	private static final int C_LOW_R = 24;  /* r */
	private static final int C_LOW_S = 25;  /* s */
	private static final int C_LOW_T = 26;  /* t */
	private static final int C_LOW_U = 27;  /* u */
	private static final int C_ABCDF = 28;  /* ABCDF */
	private static final int C_E = 29;  /* E */
	private static final int C_ETC = 30;  /* everything else */

	private static final Map<Integer, Character> CTRLS;
	static {
		CTRLS = new HashMap<>();
		CTRLS.put(C_QUOTE, '"');
		CTRLS.put(C_BACKS, '\\');
		CTRLS.put(C_SLASH, '/');
		CTRLS.put(C_LOW_B, '\b');
		CTRLS.put(C_LOW_F, '\f');
		CTRLS.put(C_LOW_N, '\n');
		CTRLS.put(C_LOW_R, '\r');
		CTRLS.put(C_LOW_T, '\t');
	}

	/*	This array maps the 128 ASCII characters into character classes.
		The remaining Unicode characters should be mapped to C_ETC.
		Non-whitespace control characters are errors. */
	private static final int[] ascii_class = new int[]  {
		__,      __,      __,      __,      __,      __,      __,      __,
		__,      C_WHITE, C_WHITE, __,      __,      C_WHITE, __,      __,
		__,      __,      __,      __,      __,      __,      __,      __,
		__,      __,      __,      __,      __,      __,      __,      __,

		C_SPACE, C_ETC,   C_QUOTE, C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,
		C_ETC,   C_ETC,   C_ETC,   C_PLUS,  C_COMMA, C_MINUS, C_POINT, C_SLASH,
		C_ZERO,  C_DIGIT, C_DIGIT, C_DIGIT, C_DIGIT, C_DIGIT, C_DIGIT, C_DIGIT,
		C_DIGIT, C_DIGIT, C_COLON, C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,

		C_ETC,   C_ABCDF, C_ABCDF, C_ABCDF, C_ABCDF, C_E,     C_ABCDF, C_ETC,
		C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,
		C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,
		C_ETC,   C_ETC,   C_ETC,   C_LSQRB, C_BACKS, C_RSQRB, C_ETC,   C_ETC,

		C_ETC,   C_LOW_A, C_LOW_B, C_LOW_C, C_LOW_D, C_LOW_E, C_LOW_F, C_ETC,
		C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_LOW_L, C_ETC,   C_LOW_N, C_ETC,
		C_ETC,   C_ETC,   C_LOW_R, C_LOW_S, C_LOW_T, C_LOW_U, C_ETC,   C_ETC,
		C_ETC,   C_ETC,   C_ETC,   C_LCURB, C_ETC,   C_RCURB, C_ETC,   C_ETC
	};

	/* The state codes. */
	private static final int GO = 0;  /* start    */
	private static final int OK = 1;  /* ok       */
	private static final int OB = 2;  /* object   */
	private static final int KE = 3;  /* key      */
	private static final int CO = 4;  /* colon    */
	private static final int VA = 5;  /* value    */
	private static final int AR = 6;  /* array    */
	private static final int ST = 7;  /* string   */
	private static final int ES = 8;  /* escape   */
	private static final int U1 = 9;  /* u1       */
	private static final int U2 = 10;  /* u2       */
	private static final int U3 = 11;  /* u3       */
	private static final int U4 = 12;  /* u4       */
	private static final int MI = 13;  /* minus    */
	private static final int ZE = 14;  /* zero     */
	private static final int IN = 15;  /* integer  */
	private static final int FR = 16;  /* fraction */
	private static final int E1 = 17;  /* e        */
	private static final int E2 = 18;  /* ex       */
	private static final int E3 = 19;  /* exp      */
	private static final int T1 = 20;  /* tr       */
	private static final int T2 = 21;  /* tru      */
	private static final int T3 = 22;  /* true     */
	private static final int F1 = 23;  /* fa       */
	private static final int F2 = 24;  /* fal      */
	private static final int F3 = 25;  /* fals     */
	private static final int F4 = 26;  /* false    */
	private static final int N1 = 27;  /* nu       */
	private static final int N2 = 28;  /* nul      */
	private static final int N3 = 29;  /* null     */

	private static final int[][] STATE_TRANSITION_TABLE = new int[][]  {
	/*
	The state transition table takes the current state and the current symbol,
	and returns either a new state or an action. An action is represented as a
	negative number. A JSON text is accepted if at the end of the text the
	state is OK and if the mode is Done.

	                     white                                      1-9                                   ABCDF  etc
	                 space |  {  }  [  ]  :  ,  "  \  /  +  -  .  0  |  a  b  c  d  e  f  l  n  r  s  t  u  |  E  |*/
	    /*start  GO*/ {GO,GO,-6,__,-5,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
	    /*ok     OK*/ {OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
	    /*object OB*/ {OB,OB,__,-9,__,__,__,__,ST,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
	    /*key    KE*/ {KE,KE,__,__,__,__,__,__,ST,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
	    /*colon  CO*/ {CO,CO,__,__,__,__,-2,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
	    /*value  VA*/ {VA,VA,-6,__,-5,__,__,__,ST,__,__,__,MI,__,ZE,IN,__,__,__,__,__,F1,__,N1,__,__,T1,__,__,__,__},
	    /*array  AR*/ {AR,AR,-6,__,-5,-7,__,__,ST,__,__,__,MI,__,ZE,IN,__,__,__,__,__,F1,__,N1,__,__,T1,__,__,__,__},
	    /*string ST*/ {ST,__,ST,ST,ST,ST,ST,ST,-4,ES,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST},
	    /*escape ES*/ {__,__,__,__,__,__,__,__,ST,ST,ST,__,__,__,__,__,__,ST,__,__,__,ST,__,ST,ST,__,ST,U1,__,__,__},
	    /*u1     U1*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,U2,U2,U2,U2,U2,U2,U2,U2,__,__,__,__,__,__,U2,U2,__},
	    /*u2     U2*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,U3,U3,U3,U3,U3,U3,U3,U3,__,__,__,__,__,__,U3,U3,__},
	    /*u3     U3*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,U4,U4,U4,U4,U4,U4,U4,U4,__,__,__,__,__,__,U4,U4,__},
	    /*u4     U4*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,ST,ST,ST,ST,ST,ST,ST,ST,__,__,__,__,__,__,ST,ST,__},
	    /*minus  MI*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,ZE,IN,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
	    /*zero   ZE*/ {OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,FR,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
	    /*int    IN*/ {OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,FR,IN,IN,__,__,__,__,E1,__,__,__,__,__,__,__,__,E1,__},
	    /*frac   FR*/ {OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,__,FR,FR,__,__,__,__,E1,__,__,__,__,__,__,__,__,E1,__},
	    /*e      E1*/ {__,__,__,__,__,__,__,__,__,__,__,E2,E2,__,E3,E3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
	    /*ex     E2*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,E3,E3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
	    /*exp    E3*/ {OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,__,E3,E3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
	    /*tr     T1*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,T2,__,__,__,__,__,__},
	    /*tru    T2*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,T3,__,__,__},
	    /*true   T3*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,OK,__,__,__,__,__,__,__,__,__,__},
	    /*fa     F1*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,F2,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
	    /*fal    F2*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,F3,__,__,__,__,__,__,__,__},
	    /*fals   F3*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,F4,__,__,__,__,__},
	    /*false  F4*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,OK,__,__,__,__,__,__,__,__,__,__},
	    /*nu     N1*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,N2,__,__,__},
	    /*nul    N2*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,N3,__,__,__,__,__,__,__,__},
	    /*null   N3*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,OK,__,__,__,__,__,__,__,__},
	};

	public static final int NO_DEPTH_LIMIT = 0;

	private static int defaultCharBufferSize = 8192;

	private int state;
	private final int depth;
	private final Deque<Mode> deque;

	private BufferedReader reader;
	private StringBuilder key;
	private StringBuilder val;
	private Object nestedVal;
	private boolean dropChars = false;

	private Deque<JsonReader> readers;
	private JsonReader parent;
	private Object data;
	private Map<String, Integer> dup;
	private int charBufferSize;

	public JsonReader() {
		this(null, null, NO_DEPTH_LIMIT, defaultCharBufferSize);
	}
	/**
	 * Starts the checking process by constructing a JReader
	 * object. It takes a depth parameter that restricts the level of maximum nesting.
	 * To continue the process, call Check for each character in the
	 * JSON text, and then call FinalCheck to obtain the final result.
	 * @param depth the max object depth to read
	 */
	public JsonReader(InputStream in, int depth) {
		this(null, in, depth, defaultCharBufferSize);
	}
	public JsonReader(InputStream in, int depth, int bufferSize) {
		this(null, in, depth, bufferSize);
	}
	private JsonReader(JsonReader parent, InputStream in, int depth, int bufferSize) {
		this.parent = parent;
		this.depth = parent == null ? depth : Math.max(0, depth -1);
		this.charBufferSize = Math.max(bufferSize, defaultCharBufferSize);
		if (this.depth < 0) throw new IndexOutOfBoundsException("depth: " + depth);

		state = GO;
		deque = new ArrayDeque<>();
		push(Mode.DONE);

		readers = new ArrayDeque<>();
		if (parent != null) {
			reader = parent.reader;
			readers = parent.readers;
		} else {
			if (in != null) {
				reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8), bufferSize);
			}
		}
		key = new StringBuilder();
		val = new StringBuilder();
	}
	/**
	 * After calling new_JSON_checker, call this function for each character (or
	 * partial character) in your JSON text. It can accept UTF-8, UTF-16, or
	 * UTF-32.
	 * @param ch the char
	 * @return true if input accepted
	 */
	public boolean check(int ch) {
		int nextClass;
		int nextState;

		// character's class.
		if (ch < 0) return false;
		if (ch >= 128) nextClass = C_ETC;
		else {
			nextClass = ascii_class[ch];
			if (nextClass <= __) return false;
		}

		// next state from the state transition table.
		nextState = STATE_TRANSITION_TABLE[state][nextClass];
		if (nextState >= 0) {
			// change the state.
			state = nextState;
		} else {
			// or perform an action
			switch (nextState) {
				/* empty } */
				case -9:
					pop(Mode.KEY);
					state = OK;
					break;
				/* } */
				case -8:
					pop(Mode.OBJECT);
					state = OK;
					break;

				/* ] */
				case -7:
					pop(Mode.ARRAY);
					state = OK;
					break;

				/* { */
				case -6:
					push(Mode.KEY);
					state = OB;
					break;

				/* [ */
				case -5:
					push(Mode.ARRAY);
					state = AR;
					break;

				/* " */
				case -4:
					Mode m1 = deque.peek();
					if (m1 == null)
						return false;
					else if (Mode.KEY.equals(m1))
						state = CO;
					else if (Mode.ARRAY.equals(m1) || Mode.OBJECT.equals(m1))
						state = OK;
					else
						return false;
					break;

				/* , */
				case -3:
					Mode m2 = deque.peek();
					if (m2 == null)
						return false;
					else if (m2.equals(Mode.OBJECT)) {
						/* a comma causes a flip from object mode to key mode. */
						pop(Mode.OBJECT);
						push(Mode.KEY);
						state = KE;
					} else if (m2.equals(Mode.ARRAY))
						state = VA;
					else return false;
					break;

				/* : */
				case -2:
					/* a colon causes a flip from key mode to object mode. */
					pop(Mode.KEY);
					push(Mode.OBJECT);
					state = VA;
					break;

				/* bad action.  */
				default:
					return false;
			}
		}
		return true;
	}
	/**
	 * The FinalCheck function should be called after all of the characters
	 * have been processed, but only if every call to {@link #check} returned true
	 * @return true if done
	 */
	public boolean finalCheck() { return state == OK && pop(Mode.DONE); }
	/**
	 * Push a mode on the stack but avoid a depth overflow.
	 * @param mode the mode
	 */
	private void push(Mode mode) {
		if (depth > 0 && deque.size() >= depth) {
			return;
		}
		deque.push(mode);
	}
	/**
	 * Pop the stack, assuring that the current mode matches the expectation.
	 * @param mode the mode
	 * @return false if there is an underflow or if the modes mismatch.
	 */
	private boolean pop(Mode mode) {
		return !deque.isEmpty() && deque.pop().equals(mode);
	}
	public Object read() throws IOException {
		int c;
		while ((c = reader.read()) > -1)
			if (!peekReader().read(c)) return null;
		return finalCheck() ? data : null;
	}
	public Object readCarefully() throws IOException, ParseException {
		int c;
		int x = 1;
		int y = 1;
		int off = 0;
		while ((c = reader.read()) > -1) {
			if (!peekReader().read(c))
				throw new ParseException(String.format("Unexpected character '%1$s' near position %2$d:%3$d (line:row).", (char)c, y, x), off);
			switch (c) {
				case '\n': y++; x = 0; break;
				case '\r': x = 0; break;
				case '\f': y++; break;
				case '\t': x+=4; break;
				case '\b': break;
				default: x++;
			}
			off++;
		}
		if (finalCheck()) {
			return data;
		} else {
			throw new ParseException(String.format("Unexpected final read state: expected state is 1 (OK) but was %1$d.", state), off);
		}
	}
	public void close() throws IOException { if (parent == null)  reader.close(); }
	private boolean read(int ch) {
		int nextClass;
		int nextState;

		// Determine the character's class.
		if (ch < 0) return false;
		if (ch >= 128) nextClass = C_ETC;
		else {
			nextClass = ascii_class[ch];
			if (nextClass <= __) return false;
		}

		// next state from the state transition table.
		nextState = STATE_TRANSITION_TABLE[state][nextClass];
		if (nextState >= 0) {
			merge((char)ch, deque.peek(), nextClass, state);
			// change the state.
			state = nextState;
		} else {
			// or perform one of the actions.
			switch (nextState) {
				/* empty } */
				case -9:
					if (nestedVal != null) addObjValue(new JVal(nestedVal));
					pop(Mode.KEY);
					popReader();
					//_state = OK;
					break;

				/* } */
				case -8:
					addObjValue(nestedVal == null
						? new JVal(val)
						: new JVal(nestedVal));
					popReader();
					pop(Mode.OBJECT);
					state = OK;
					break;

				/* ] */
				case -7:
					addSeqValue(nestedVal == null
						? new JVal(val)
						: new JVal(nestedVal));
					popReader();
					pop(Mode.ARRAY);
					state = OK;
					break;

				/* { */
				case -6:
					pushObjReader();
					break;

				/* [ */
				case -5:
					pushSeqReader();
					break;

				/* " */
				case -4:
					Mode m1 = deque.peek();
					if (Mode.KEY.equals(m1))
						state = CO;
					else if (Mode.ARRAY.equals(m1) || Mode.OBJECT.equals(m1)) {
						dropChars = true;
						state = OK;
					} else return false;
					break;

				/* , */
				case -3:
					Mode m2 = deque.peek();
					if (Mode.OBJECT.equals(m2)) {
						/* A comma causes a flip from object mode to key mode. */
						addObjValue(nestedVal != null ? new JVal(nestedVal) : new JVal(val));
						pop(Mode.OBJECT);
						push(Mode.KEY);
						state = KE;
					} else if(Mode.ARRAY.equals(m2)) {
						addSeqValue(nestedVal != null ? new JVal(nestedVal) : new JVal(val));
						state = VA;
					} else return false;
					break;

				/* : */
				case -2:
					/* A colon causes a flip from key mode to object mode. */
					pop(Mode.KEY);
					push(Mode.OBJECT);
					state = VA;
					break;

				/* Bad action.  */
				default:
					return false;
			}
		}
		return true;
	}
	private JsonReader peekReader() { return readers.isEmpty() ? this : readers.peek(); }
	private void merge(char ch, Mode mode, int asciiClass, int state) {
		if (Mode.KEY.equals(mode)) {
			if (state == ST) key.append(ch);
		} else if(Mode.OBJECT.equals(mode) || Mode.ARRAY.equals(mode)) {
			if (dropChars || (asciiClass == C_BACKS && state == ST)) return;
			if (state == ES) val.append(CTRLS.get(asciiClass));
			else val.append(ch);
		}
	}
	@SuppressWarnings("unchecked")
	private void addSeqValue(JVal jval) {
		if (data instanceof List && !jval.dropVal())
			((List<Object>)data).add(jval.value);

		dropChars = false;
		key = new StringBuilder();
		val = new StringBuilder();
		nestedVal = null;
	}
	@SuppressWarnings("unchecked")
	private void addObjValue(JVal jval) {
		if (data instanceof Map) {
			String k = key.toString();
			Map<String, Object> bnd = (Map<String, Object>)data;
			Integer i;
			// handle/ignore duplicate keys
			if (bnd.containsKey(k)) {
				i = dup.get(k);
				if (i != null )	{
					i += 1;
				} else {
					i = 0;
				}
				dup.put(k, i);
				k += "_" + i;
			}
			bnd.put(k, jval.value);
		}
		dropChars = false;
		key = new StringBuilder();
		val = new StringBuilder();
		nestedVal = null;
	}
	private void pushObjReader() {
		readers.push(readers.isEmpty() ? this : new JsonReader(this, null, Math.max(0, depth - 1), this.charBufferSize));
		JsonReader peek =  peekReader();
		peek.push(Mode.KEY);
		peek.state = OB;
		peek.data = new LinkedHashMap<String, Object>();
		peek.dup = new LinkedHashMap<>();
	}
	private void pushSeqReader() {
		readers.push(readers.isEmpty() ? this : new JsonReader(this, null, Math.max(0, depth - 1), this.charBufferSize));
		JsonReader peek = peekReader();
		peek.push(Mode.ARRAY);
		peek.state = AR;
		peek.data = new ArrayList<>();
	}
	private void popReader() {
		if (readers.size() > 1) {
			JsonReader pop = readers.pop();
			peekReader().nestedVal = pop.data;
		}
		peekReader().state = OK;
	}
}
