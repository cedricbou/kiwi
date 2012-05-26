import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.vertx.java.core.buffer.Buffer;

import com.emodroid.commons.vertx.BufferUtils;


public class BufferUtilsTest {

	final static Buffer unixDelimiter = new Buffer(new byte[] { 10 }); 
	final static Buffer windowsDelimiter = new Buffer(new byte[] { 13, 10 });
	final static Buffer wordDelimiter = new Buffer("chat", "ASCII");
	final static Buffer arbitraryDelimiter = new Buffer("arbre", "ASCII");

	final static Buffer sentence = new Buffer("le petit chat\n joue dans le jardin\r\n", "ASCII");
	final static Buffer text = new Buffer("le petit chat\njoue dans le jardin\r\net grimpe sur un arbre\net ne peut plus redescendre.", "ASCII");
	
	@Test
	public void testIndexOfOneByteBuffer() {
		final int index = BufferUtils.indexOf(sentence, unixDelimiter);
		assertEquals(13, index);
	}

	@Test
	public void testIndexOfTwoByteBuffer() {
		final int index = BufferUtils.indexOf(sentence, windowsDelimiter);
		assertEquals(34, index);
	}

	@Test
	public void testIndexOfWordByteBuffer() {
		final int index = BufferUtils.indexOf(sentence, wordDelimiter);
		assertEquals(9, index);
	}
	
	@Test
	public void testIndexOfUnexistantBuffer() {
		final int index = BufferUtils.indexOf(sentence, arbitraryDelimiter);
		assertEquals(-1, index);
	}
	
	@Test
	public void testLineDelimiters() {
		final Buffer[] lineDelimiters = BufferUtils.lineDelimiters();
		
		assertEquals(2, lineDelimiters.length);
		
		int found = lineDelimiters[0].equals(unixDelimiter)?1:0;
		found += lineDelimiters[0].equals(windowsDelimiter)?1:0;
		found += lineDelimiters[1].equals(unixDelimiter)?1:0;
		found += lineDelimiters[1].equals(windowsDelimiter)?1:0;
		
		assertEquals(2, found);
	}
	
	@Test
	public void testSplitAStringWithDelimiters() {
		final Buffer[] buffers = BufferUtils.split(text, BufferUtils.lineDelimiters());
		
		assertEquals(4, buffers.length);
		assertEquals(new Buffer("le petit chat", "ASCII"), buffers[0]);
		assertEquals(new Buffer("joue dans le jardin", "ASCII"), buffers[1]);
		assertEquals(new Buffer("et grimpe sur un arbre", "ASCII"), buffers[2]);
		assertEquals(new Buffer("et ne peut plus redescendre.", "ASCII"), buffers[3]);
	}
	
	@Test
	public void testSplitAStringEndingWithADelimiter() {
		final Buffer[] buffers = BufferUtils.split(sentence, BufferUtils.lineDelimiters());
		
		assertEquals(3, buffers.length);
		assertEquals(BufferUtils.EMPTY_BUFFER, buffers[buffers.length - 1]);
	}
	
}
