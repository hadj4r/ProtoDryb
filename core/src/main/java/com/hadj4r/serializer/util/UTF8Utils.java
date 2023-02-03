package com.hadj4r.serializer.util;

public final class UTF8Utils {
    private UTF8Utils() {
    }

    // for context https://stackoverflow.com/questions/8511490/calculating-length-in-utf-8-of-java-string-without-actually-encoding-it
    public static int utf8StringByteArrayLength(CharSequence sequence) {
        int count = 0;
        for (int i = 0, len = sequence.length(); i < len; ++i) {
            char ch = sequence.charAt(i);
            if (ch <= 0x7F) {
                ++count;
            } else if (ch <= 0x7FF) {
                count += 2;
            } else if (Character.isHighSurrogate(ch)) {
                count += 4;
                ++i;
            } else {
                count += 3;
            }
        }
        return count;
    }

    public static void stringToUTF8BytesArray(final CharSequence sequence, final byte[] bytes, final int offset) {
        int count = offset;
        for (int i = 0, len = sequence.length(); i < len; ++i) {
            char ch = sequence.charAt(i);
            if (ch <= 0x7F) {
                bytes[++count] = (byte) ch;
            } else if (ch <= 0x7FF) {
                bytes[++count] = (byte) (0xC0 | (0x1F & (ch >> 6)));
                bytes[++count] = (byte) (0x80 | (0x3F & ch));
            } else if (Character.isHighSurrogate(ch)) {
                int codePoint = Character.toCodePoint(ch, sequence.charAt(++i));
                bytes[++count] = (byte) (0xF0 | (0x07 & (codePoint >> 18)));
                bytes[++count] = (byte) (0x80 | (0x3F & (codePoint >> 12)));
                bytes[++count] = (byte) (0x80 | (0x3F & (codePoint >> 6)));
                bytes[++count] = (byte) (0x80 | (0x3F & codePoint));
            } else {
                bytes[++count] = (byte) (0xE0 | (0x0F & (ch >> 12)));
                bytes[++count] = (byte) (0x80 | (0x3F & (ch >> 6)));
                bytes[++count] = (byte) (0x80 | (0x3F & ch));
            }
        }
        bytes[offset] = (byte) (count - offset); // write length
    }
}
