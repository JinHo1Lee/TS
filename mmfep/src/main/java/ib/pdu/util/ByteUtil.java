package ib.pdu.util;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtil {
	
	public static final int MAX_PDU_BYTE_LEN = 1024*1024*10;

	private ByteUtil() {
	}

	public static String addNull(String str) {

		if (str == null) {
			return str;
		} else {
			str = str + (char) 0x00;
			return (str);
		}
	}

	public static final byte[] short2byte(short s) {
		byte dest[] = new byte[2];
		dest[1] = (byte) (s & 0xff);
		dest[0] = (byte) (s >>> 8 & 0xff);
		return dest;
	}

	public static final byte[] int2byte(int i) {
		byte dest[] = new byte[4];
		dest[3] = (byte) (i & 0xff);
		dest[2] = (byte) (i >>> 8 & 0xff);
		dest[1] = (byte) (i >>> 16 & 0xff);
		dest[0] = (byte) (i >>> 24 & 0xff);
		return dest;
	}

	public static final byte[] long2byte(long l) {
		byte dest[] = new byte[8];
		dest[7] = (byte) (int) (l & 255L);
		dest[6] = (byte) (int) (l >>> 8 & 255L);
		dest[5] = (byte) (int) (l >>> 16 & 255L);
		dest[4] = (byte) (int) (l >>> 24 & 255L);
		dest[3] = (byte) (int) (l >>> 32 & 255L);
		dest[2] = (byte) (int) (l >>> 40 & 255L);
		dest[1] = (byte) (int) (l >>> 48 & 255L);
		dest[0] = (byte) (int) (l >>> 56 & 255L);
		return dest;
	}

	public static final byte[] float2byte(float f) {
		byte dest[] = new byte[4];
		return setfloat(dest, 0, f);
	}

	public static final byte[] double2byte(double d) {
		byte dest[] = new byte[8];
		return setdouble(dest, 0, d);
	}

	public static final byte getbyte(byte src[], int offset) {
		return src[offset];
	}

	public static final byte[] getbytes(byte src[], int offset, int length) {
		byte dest[] = new byte[length];
		System.arraycopy(src, offset, dest, 0, length);
		return dest;
	}

	public static final short getshort(byte src[], int offset) {
		return (short) ((src[offset] & 0xff) << 8 | src[offset + 1] & 0xff);
	}

	public static final int getint(byte src[], int offset) {
		return (src[offset] & 0xff) << 24 | (src[offset + 1] & 0xff) << 16 | (src[offset + 2] & 0xff) << 8 | src[offset + 3] & 0xff;
	}

	public static final int getint(byte src[]) {
		return getint(src, 0);
	}

	public static String byteToHex(byte[] src) {
		String hex = "";
		if (src.length > 16)
			hex = "\r\n";
		for (int i = 0, j = 1; i < src.length; i++, j++) {
			hex += pad0(Integer.toHexString(src[i] & 0xff), 2) + " ";

			if (src.length > 16 && (j % 16) == 0) {
				// hex += " " + new String(codes, j-16, 16);
				hex += "\r\n";
			}
		}
		return hex;
	}

	public static String pad0(String str, int size) {
		char[] zeros = new char[size - str.length()];
		for (int i = 0; i < zeros.length; i++)
			zeros[i] = '0';
		return new String(zeros) + str;
	}

	public static final long getlong(byte src[], int offset) {
		return (long) getint(src, offset) << 32 | (long) getint(src, offset + 4) & 0xffffffffL;
	}

	public static final float getfloat(byte src[], int offset) {
		return Float.intBitsToFloat(getint(src, offset));
	}

	public static final double getdouble(byte src[], int offset) {
		return Double.longBitsToDouble(getlong(src, offset));
	}

	public static final byte[] setbyte(byte dest[], int offset, byte b) {
		dest[offset] = b;
		return dest;
	}

	public static final byte[] setbytes(byte dest[], int offset, byte src[]) {
		System.arraycopy(src, 0, dest, offset, src.length);
		return dest;
	}

	public static final byte[] setbytes(byte dest[], int offset, byte src[], int len) {
		System.arraycopy(src, 0, dest, offset, len);
		return dest;
	}

	public static final byte[] setshort(byte dest[], int offset, short s) {
		dest[offset] = (byte) (s >>> 8 & 0xff);
		dest[offset + 1] = (byte) (s & 0xff);
		return dest;
	}

	public static final byte[] setint(byte dest[], int offset, int i) {
		dest[offset] = (byte) (i >>> 24 & 0xff);
		dest[offset + 1] = (byte) (i >>> 16 & 0xff);
		dest[offset + 2] = (byte) (i >>> 8 & 0xff);
		dest[offset + 3] = (byte) (i & 0xff);
		return dest;
	}

	public static final byte[] setlong(byte dest[], int offset, long l) {
		setint(dest, offset, (int) (l >>> 32));
		setint(dest, offset + 4, (int) (l & 0xffffffffL));
		return dest;
	}

	public static final byte[] setfloat(byte dest[], int offset, float f) {
		return setint(dest, offset, Float.floatToIntBits(f));
	}

	public static final byte[] setdouble(byte dest[], int offset, double d) {
		return setlong(dest, offset, Double.doubleToLongBits(d));
	}

	public static final boolean isEquals(byte b[], String s) {
		if (b == null || s == null)
			return false;
		int slen = s.length();
		if (b.length != slen)
			return false;
		for (int i = slen; i-- > 0;)
			if (b[i] != s.charAt(i))
				return false;

		return true;
	}

	private static final String STR_NEW_LINE = "\r\n";
	private static final String STR_WHITE_SPACE = " ";

	private static final String STR_PACKET_LOG_LINE = "==============================================================================";
	private static final String STR_PACKET_LOG_TITLE = "   PACKET DUMP";

	public static String getHexString(byte[] source) {
		byte[] logArray = new byte[source.length];
		byte[] tranArray = new byte[source.length];

		System.arraycopy(source, 0, logArray, 0, source.length);
		System.arraycopy(source, 0, tranArray, 0, source.length);

		int lastIdx = 0, appendLen = 0;
		;
		StringBuilder hex = new StringBuilder();

		for (int i = 0, j = 1; i < logArray.length; i++, j++) {

			if (j - 1 == lastIdx) {
				hex.append("0x");
				hex.append(fillChar(Integer.toHexString(i & 0xfffffff).toLowerCase(), 8, 2, '0'));
				hex.append(":");
				hex.append(STR_WHITE_SPACE);
			}

			if (Character.isWhitespace(logArray[i]) || logArray[i] == 0)
				tranArray[i] = (byte) '.';

			hex.append(ByteUtil.pad0(Integer.toHexString(logArray[i] & 0xff), 2));
			hex.append(STR_WHITE_SPACE);
			appendLen++;

			if (j == logArray.length && (j % 16) != 0) {
				for (; (j % 16) != 0; j++) {
					hex.append("   ");
				}
			}

			if ((j % 16) == 0) {
				hex.append(";");
				hex.append(STR_WHITE_SPACE);
				hex.append(new String(ByteUtil.getBytes(tranArray, lastIdx, appendLen)));
				hex.append(STR_NEW_LINE);
				lastIdx += appendLen;
				appendLen = 0;
			}
		}
		return hex.toString();
	}

	public static final byte[] getBytes(byte src[], int offset, int length) {
		byte dest[] = new byte[length];
		System.arraycopy(src, offset, dest, 0, length);
		return dest;
	}

	public static String fillChar(String src, int digit, int align, char add) {
		String s_value = src;
		int ii = 0;

		ii = s_value.length();

		if (ii > digit) {
			return s_value.substring(0, digit);
		}

		for (ii = s_value.length(); ii < digit; ii++)
			if (align == 0)
				s_value = s_value + add; //LEFT 정렬
			else if (align == 2)
				s_value = add + s_value; //RIGHT 정렬

		return s_value;
	}

	public static final boolean isEquals(byte a[], byte b[]) {
		if (a == null || b == null)
			return false;
		if (a.length != b.length)
			return false;
		for (int i = a.length; i-- > 0;)
			if (a[i] != b[i])
				return false;

		return true;
	}

	public static byte[] ByteBufferToByte(ByteBuffer buffer) {
		final byte[] array = buffer.array();
		return Arrays.copyOfRange(array, 0, buffer.position());

	}
}