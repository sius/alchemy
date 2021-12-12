package liquer.alchemy.alembic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;

/**
 * Formats java data types into byte sequences (default: high byte first) and vice versa.
 * Shows implementation details.
 */
public final class BinarySupport {

	private final static Logger LOG = LoggerFactory.getLogger(BinarySupport.class);
	
	private BinarySupport() { }
	
	public static byte[] shortToLittleEndianByteOrder(short s) {
		byte[] ret_bytes = new byte[2];
		for(int n= 0; n < 2; n++)
			ret_bytes[n] = (byte)(s >>> (n * 8));
		return ret_bytes;
	}

	public static byte[] shortToBigEndianByteOrder(short s) {
		byte[] ret_bytes = new byte[2];
		for(int n= 0; n < 2; n++)
			ret_bytes[1 - n] = (byte)(s >>> (n * 8));
		return ret_bytes;
	}
	/**
	 * LittleEndian DWord
	 */
	public static byte[] intToLittleEndianByteOrder(int i) {
		byte[] dword = new byte[4];
		for(int n= 0; n < 4; n++)
			dword[n] = (byte)(i >>> (n * 8));
		return dword;
	}
	/**
	 * BigEndian DWord
	 */
	public static byte[] intToBigEndianByteOrder(int i) {
		byte[] dword = new byte[4];
		for(int n= 0; n < 4; n++)
			dword[3 - n] = (byte)(i >>> (n * 8));
		return dword;
	}

	public static byte[] longToLittleEndianByteOrder(long l) {
		byte[] bytes = new byte[8];
		for(int n= 0; n < 8; n++)
			bytes[n] = (byte)(l >>> (n * 8));
		return bytes;
	}

	public static byte[] longToBigEndianByteOrder(long l) {
		byte[] bytes = new byte[8];
		for(int n= 0; n < 8; n++)
			bytes[7 - n] = (byte)(l >>> (n * 8));
		return bytes;
	}

	/**
	 * Takes a Boolean and returns a single byte sequence.
	 * @param b the Boolean
	 * @return a single byte sequence
	 */
	public static byte[] format(Boolean b) {
		byte[] bytes = new byte[0];
		if (b!=null)
			bytes = new byte[]{ (byte)(b ? 1 : 0) } ;
		return bytes;
	}
	/**
	 * Takes a Boolean array {@code booleanList} and returns a sequence of {@code (booleanList.length)} bytes.
	 * @param booleanList the Boolean List
	 * @return a sequence of {@code (booleanList.length)} bytes
	 */
	public static byte[] format(Boolean[] booleanList) {
		byte[] bytes = new byte[0];
		if (booleanList != null) {
			bytes = new byte[booleanList.length];
			int index = 0;
			for(Boolean b : booleanList)
				bytes[index++] = (byte)(b ? 1 : 0);
		}
		return bytes;
	}
	/**
	 * Takes a Byte array {@code byteList} and returns a sequence of {@code (byteList.length)} bytes.
	 * @param byteList the Byte List
	 * @return a sequence of {@code (byteList.length)} bytes
	 */
	public static byte[] format(Byte[] byteList) {
		byte[] bytes = new byte[0];
		if (byteList != null) {
			bytes = new byte[byteList.length];
			int index = 0;
			for(Byte b : byteList)
				bytes[index++] = b;
		}
		return bytes;
	}
	/**
	 * Takes a Short and returns a sequence of two bytes, high byte first.
	 * @param s the Short value
	 * @return a sequence of two bytes, high byte first
	 */
	public static byte[] format(Short s) {
		return format(s, false);
	}
	/**
	 * Takes a Short and returns a sequence of two bytes.
	 * @param s the Short value
	 * @param littleEndianByteOrder {@code true} low byte first; {@code false} high byte first.
	 * @return a sequence of two bytes
	 */
	public static byte[] format(Short s, boolean littleEndianByteOrder) {
		byte[] bytes = new byte[0];

		if (s != null)
			bytes = (littleEndianByteOrder ? shortToLittleEndianByteOrder(s) : shortToBigEndianByteOrder(s));

		return bytes;
	}
	/**
	 * Takes a Short array {@code shortList} and returns a sequence of {@code (2 * shortList.length)} bytes.
	 * @param shortList the Short List
	 * @param littleEndianByteOrder {@code true} low byte first; {@code false} high byte first.
	 * @return a sequence of {@code (2 * shortList.length)} bytes
	 */
	public static byte[] format(Short[] shortList, boolean littleEndianByteOrder) {
		byte[] retBytes = new byte[0];
		if (shortList != null) {
			retBytes = new byte[2*shortList.length];
			int offset = 0;
			for(Short s : shortList) {
				byte[] bytes = (littleEndianByteOrder ? shortToLittleEndianByteOrder(s) : shortToBigEndianByteOrder(s));
				System.arraycopy(bytes, 0, retBytes, offset, 2);
				offset += bytes.length;
			}
		}
		return retBytes;
	}

	/**
	 * Takes an Integer and returns a sequence of four bytes, high byte first.
	 * @param i the Integer value
	 * @return a sequence of four bytes, high byte first
	 */
	public static byte[] format(Integer i) {
		return format(i, false);
	}

	/**
	 * Takes an Integer and returns a sequence of four bytes.
	 * @param i the Integer value
	 * @param littleEndianByteOrder {@code true} low byte first; {@code false} high byte first.
	 * @return a sequence of four bytes
	 */
	public static byte[] format(Integer i, boolean littleEndianByteOrder) {
		byte[] bytes = new byte[0];

		if (i != null)
			bytes = (littleEndianByteOrder ? intToLittleEndianByteOrder(i) : intToBigEndianByteOrder(i));

		return bytes;
	}

	/**
	 * Takes an Integer array {@code intList} and returns a sequence of {@code (4 * intList.length)} bytes.
	 * @param intList an Integer array
	 * @param littleEndianByteOrder {@code true} low byte first; {@code false} high byte first.
	 * @return a sequence of {@code (4 * intList.length)} bytes
	 */
	public static byte[] format(Integer[] intList, boolean littleEndianByteOrder) {
		byte[] retBytes = new byte[0];
		if (intList != null) {
			retBytes = new byte[4*intList.length];
			int offset = 0;
			for(Integer i : intList) {
				byte[] bytes = (littleEndianByteOrder ? intToLittleEndianByteOrder(i) : intToBigEndianByteOrder(i));
				System.arraycopy(bytes, 0, retBytes, offset, 4);
				offset += bytes.length;
			}
		}
		return retBytes;
	}

	/**
	 * Takes a Long and returns a sequence of eight bytes, high byte first.
	 * @param l a Long value
	 * @return a sequence of eight bytes, high byte first
	 */
	public static byte[] format(Long l) {
		return format(l, false);
	}
	/**
	 * Takes a Long and returns a sequence of eight bytes.
	 * @param l a Long value
	 * @param littleEndianByteOrder {@code true} low byte first; {@code false} high byte first.
	 * @return a sequence of eight bytes
	 */
	public static byte[] format(Long l, boolean littleEndianByteOrder) {
		byte[] ret_bytes = new byte[0];

		if (l != null)
			ret_bytes = (littleEndianByteOrder ? longToLittleEndianByteOrder(l) : longToBigEndianByteOrder(l));

		return ret_bytes;
	}
	/**
	 * Takes a Long array {@code longList} and returns a sequence of {@code (8 * longList.length)} bytes.
	 * @param longList a Long List
	 * @param littleEndianByteOrder {@code true} low byte first; {@code false} high byte first.
	 * @return a sequence of {@code (8 * longList.length)} bytes
	 */
	public static byte[] format(Long[] longList, boolean littleEndianByteOrder) {
		byte[] retBytes = new byte[0];
		if (longList != null) {
			retBytes = new byte[8*longList.length];
			int offset = 0;
			for(Long l : longList) {
				byte[] bytes = (littleEndianByteOrder ? longToLittleEndianByteOrder(l) : longToBigEndianByteOrder(l));
				System.arraycopy(bytes, 0, retBytes, offset, 8);
				offset += bytes.length;
			}
		}
		return retBytes;
	}
	/**
	 * Takes a Date and returns a Date String represented by this Date object.
	 * @param d a Date value
	 * @param dateFormat a DateFormat
	 * @param charset a Charset
	 * @return a Date String represented by this Date object
	 */
	public static byte[] format(Date d, DateFormat dateFormat, Charset charset) {
		byte[] bytes = new byte[0];

		if (d != null)
			bytes = format(dateFormat.format(d), charset);

		return bytes;
	}

	/**
	 * Takes a Date and returns a sequence of eight bytes representing the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object.
	 * @param d a Date value
	 * @param littleEndianByteOrder {@code true} the byte order will be reversed.
	 * @return a sequence of eight bytes representing the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object
	 */
	public static byte[] format(Date d, boolean littleEndianByteOrder) {
		byte[] bytes = new byte[0];

		if (d != null)
			bytes = format(d.getTime(), littleEndianByteOrder);

		return bytes;
	}
	/**
	 * Takes a Float {@code f} and returns a sequence of four bytes.
	 * @param f a Float value
	 * @return a sequence of four bytes
	 */
	public static byte[] format(Float f) {
		return format(new Float[] { f} );
	}
	/**
	 * Takes a Float array {@code floatList} and returns a sequence of {@code (4 * floatList.length)} bytes.
	 * @param floatList a Float List
	 * @return a sequence of {@code (4 * floatList.length)} bytes
	 */
	public static byte[] format(Float[] floatList) {
		byte[] bytes = new byte[0];
		if (floatList != null) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
			try {
				for(Float f : floatList)
					dataOutputStream.writeFloat(f);
				dataOutputStream.flush();
				bytes = byteArrayOutputStream.toByteArray();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			} finally {
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
		return bytes;
	}

	/**
	 * Takes a Double {@code d} and returns a sequence of eight bytes.
	 * @param d a Double value
	 * @return a sequence of eight bytes
	 */
	public static byte[] format(Double d) {
		return format(new Double[] {d} );
	}
	/**
	 * Takes a Double array {@code doubleList} and returns a sequence of {@code (8 * doubleList.length)} bytes.
	 * @param doubleList a Double List
	 * @return a sequence of {@code (8 * doubleList.length)} bytes
	 */
	public static byte[] format(Double[] doubleList) {
		byte[] bytes = new byte[0];
		if (doubleList != null) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
			try {
				for(Double d : doubleList)
					dataOutputStream.writeDouble(d);
				dataOutputStream.flush();
				bytes = byteArrayOutputStream.toByteArray();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			} finally {
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
		return bytes;
	}

	/**
	 * Takes a String and returns a sequence of {@link Charset} encoded bytes
	 * @param s a String value
	 * @param charset a Charset
	 * @return a sequence of bytes as if by the {@link String#format} with the boolean parameter set to {@code false}
	 */
	public static byte[] format(String s, Charset charset) {
		return StringSupport.getEncodedBytes(s, charset);
	}
	/**
	 * Takes at least one input byte and returns a Boolean.
	 * @param data a byte array
	 * @return true if data#length > 0 && data[0] != 0
	 */
	public static Boolean getBooleanValue(byte[] data) {
		Boolean b = null;
		if (data != null && data.length >= 1)
			b = (data[0] != 0);
		return b;
	}
	/**
	 * Takes one input byte and returns an unsigned byte value represented by an Integer
	 * @param data a byte value
	 * @return an Integer
	 */
	public static Integer getUnsignedByteValue(byte data) {
		return getUnsignedByteValue(new byte[] {data});
	}
	/**
	 * Takes at least one input byte and returns an unsigned byte value represented by an Integer
	 * @param data a byte array
	 * @return an Integer
	 */
	public static Integer getUnsignedByteValue(byte[] data) {
		Integer i = null;
		if (data != null && data.length >= 1)
			i = (data[0] & 0xff);
		return i;
	}
	/**
	 * Takes one input byte and returns a char
	 * @param data a byte value
	 * @return a char
	 */
	public static char getCharValue(byte data) {
		return (char)(data & 0xff);
	}
	/**
	 * Takes at least two input bytes and returns a char
	 * @param data a byte array
	 * @return a char
	 */
	public static char getCharValue(byte[] data) {
		char ch = 0;
		if (data != null && data.length >= 2)
			ch = (char)((data[0] << 8) | (data[1] & 0xff));
		return ch;
	}
	/**
	 * Takes at least two input bytes and returns a Short
	 * @param data a byte array
	 * @return a Short
	 */
	public static Short getShortValue(byte[] data) {
		Short s = null;
		if (data != null && data.length >= 2)
			s = (short)((data[0]  << 8) | (data[1] & 0xff));
		return s;
	}
	/**
	 * Takes at least two input bytes and returns an unsigned short value represented by an Integer
	 * @param data a byte array
	 * @return an unsigned short value represented by an Integer
	 */
	public static Integer getUnsignedShortValue(byte[] data) {
		Integer i = null;
		if (data != null && data.length >= 2)
			i = (((data[0] & 0xff) << 8) | (data[1] & 0xff));
		return i;
	}
	/**
	 * Takes at least four input bytes and returns an Integer
	 * @param data a byte array
	 * @return an Integer
	 */
	public static Integer getIntegerValue(byte[] data) {
		Integer i = null;
		if (data != null && data.length >= 4) {
			i = (((data[0] & 0xff) << 24) | ((data[1] & 0xff) << 16) | ((data[2] & 0xff) << 8) | (data[3] & 0xff));
		}
		return i;
	}
	/**
	 * Takes at least eight input bytes and returns a Long
	 * @param data a byte array
	 * @return a Long
	 */
	public static Long getLongValue(byte[] data) {
		Long l = null;
		if (data != null && data.length >= 8) {
			l = ((long)(data[0] & 0xff) << 56) | ((long)(data[1] & 0xff) << 48) | ((long)(data[2] & 0xff) << 40) | ((long)(data[3] & 0xff) << 32) |
					((long)(data[4] & 0xff) << 24) | ((long)(data[5] & 0xff) << 16) | ((long)(data[6] & 0xff) << 8) | (long)(data[7] & 0xff);
		}
		return l;
	}
	/**
	 * Takes at least four input bytes and returns a Float
	 * @param data a byte array
	 * @return a Float
	 */
	public static Float getFloatValue(byte[] data) {
		Float f = null;
		Integer i = getIntegerValue(data);
		if (i != null)
			f = Float.intBitsToFloat(i);
		return f;
	}
	/**
	 * Takes at least four input bytes and returns a Float.
	 * Shows implementation details.
	 * @param data a byte array
	 * @return a Float
	 */
	public static Float getFloatValueImpl(byte[] data) {
		Float f = null;
		Integer i = getIntegerValue(data);
		if (i != null) {
			if (i == 0x7f800000) {
				f = Float.POSITIVE_INFINITY; // 0x7f800000
			} else if (i == 0xff800000) {
				f = Float.NEGATIVE_INFINITY; // 0xff800000
			} else if ( (i >= 0x7f800001 /*&& i <= 0x7fffffff*/) || (i >= 0xff800001 && i <= 0xffffffff) ) {
				f = Float.NaN; // 0x7fc00000
			} else {
				int s = ((i >> 31) == 0) ? 1 : -1;
				int e = ((i >> 23) & 0xff);
				int m = (e == 0) ? (i & 0x7fffff) << 1 : (i & 0x7fffff) | 0x800000;
				f = (float)(Math.pow(2, Math.E-150) * s * m);
			}
		}
		return f;
	}
	/**
	 * Takes at least eight input bytes and returns a Double.
	 * @param data a byte array
	 * @return a Double
	 */
	public static Double getDoubleValue(byte[] data) {
		Double d = null;
		Long l = getLongValue(data);
		if (l != null) {
			d = Double.longBitsToDouble(l);
		}
		return d;
	}
	/**
	 * Takes at least eight input bytes and returns a Double.
	 * Shows implementation details.
	 * @param data a byte array
	 * @return a Double
	 */
	public static Double getDoubleValueImpl(byte[] data) {
		Double d = null;
		Long l = getLongValue(data);
		if (l != null) {
			if (l == 0x7ff0000000000000L)
				d = Double.POSITIVE_INFINITY; // 0x7ff0000000000000L
			else if (l == 0xfff0000000000000L)
				d = Double.NEGATIVE_INFINITY; // 0xfff0000000000000L
			else if ( (l >= 0x7ff0000000000001L /* && l <= 0x7fffffffffffffffL */) || (l >= 0xfff0000000000001L && l <= 0xffffffffffffffffL) )
				d = Double.NaN; // 0x7ff8000000000000L
			else {
				int s = ((l >> 63) == 0) ? 1 : -1;
				int e = (int)((l >> 52) & 0x7ffL);
				long m = (e == 0) ? (l & 0xfffffffffffffL) << 1 : (l & 0xfffffffffffffL) | 0x10000000000000L;
				d = (Math.pow(2, Math.E-1075) * s * m);
			}
		}
		return d;
	}
	/**
	 * Takes an even number of input bytes (two bytes per char) and returns a String.
	 * Shows implementation details.
	 * @param data a byte array
	 * @return a String
	 */
	public static String getStringValue(byte[] data) {
		String s = null;
		if (data != null && (data.length % 2) == 0) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < data.length; i+=2) {
				builder.append((char)((data[i] << 8) | (data[i+1] & 0xff)));
			}
			s = builder.toString();
		}
		return s;
	}
}
