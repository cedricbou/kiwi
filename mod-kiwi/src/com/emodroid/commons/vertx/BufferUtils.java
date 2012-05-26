package com.emodroid.commons.vertx;

import org.vertx.java.core.buffer.Buffer;

public class BufferUtils {

	private final static Buffer unixDelimiter = new Buffer(new byte[] { 10 });

	private final static Buffer windowsDelimiter = new Buffer(new byte[] { 13,
			10 });

	public final static Buffer EMPTY_BUFFER = new Buffer("");

	public static int indexOf(final Buffer subject, final Buffer[] lookedForSet) {
		for (int i = 0; i < subject.length(); ++i) {
			for (int k = 0; k < lookedForSet.length; ++k) {
				if (findAt(subject, i, lookedForSet[k])) {
					return i;
				}
			}
		}

		return -1;

	}

	private static boolean findAt(final Buffer buffer, final int atPos,
			final Buffer lookedFor) {
		int sameBytesCount = 0;

		for (int i = 0; i < lookedFor.length(); ++i) {
			if (buffer.getByte(atPos + i) == lookedFor.getByte(i)) {
				sameBytesCount++;
			} else {
				break;
			}
		}

		return sameBytesCount == lookedFor.length();
	}

	public static int indexOf(final Buffer subject, final Buffer lookedFor) {
		return indexOf(subject, new Buffer[] { lookedFor });
	}

	public static int count(final Buffer subject, final Buffer[] lookedForSet) {
		int count = 0;
		int i = 0;
		while (i < subject.length()) {
			for (int k = 0; k < lookedForSet.length; ++k) {
				if (findAt(subject, i, lookedForSet[k])) {
					i += lookedForSet[k].length() - 1;
					count++;
					break;
				}
			}
			i++;
		}

		return count;
	}

	public static Buffer[] split(final Buffer buffer, final Buffer[] delimiters) {
		final int countDelimiters = count(buffer, delimiters);
		final Buffer[] buffers = new Buffer[countDelimiters + 1];

		Buffer currentBuffer = buffer;

		for (int i = 0; i < countDelimiters; ++i) {
			final int found = indexOf(currentBuffer, delimiters);
			if (found >= 0) {
				buffers[i] = currentBuffer.getBuffer(0, found);
				for (int k = 0; k < delimiters.length; ++k) {
					if (findAt(currentBuffer, found, delimiters[k])) {
						currentBuffer = currentBuffer.getBuffer(found
								+ delimiters[k].length(),
								currentBuffer.length());
						break;
					}
				}
			}
		}

		buffers[countDelimiters] = currentBuffer;

		return buffers;
	}

	public static Buffer[] lineDelimiters() {
		return new Buffer[] { unixDelimiter, windowsDelimiter };
	}
}