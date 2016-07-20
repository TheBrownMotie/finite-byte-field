package com.nickww.finitefield;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class ChecksumVectorTest
{
	private static class ChecksumVectorImplementation extends ChecksumVector
	{
		@Override
		public byte[] withChecksums(byte[] data)
		{
			return null;
		}
		
		@Override
		public byte[] solveMissingValues(Byte[] dataWithChecksums)
		{
			return null;
		}
	}
	
	private static ChecksumVectorImplementation clazz;
	
	@BeforeClass
	public static void setup()
	{
		clazz = new ChecksumVectorImplementation();
	}
	
	@Test
	public void testMissingIndices()
	{
		assertNull(clazz.missingIndices(null));
	}
	
	@Test
	public void testMissingIndicesNoNulls()
	{
		assertEquals(0, clazz.missingIndices(new Byte[] { 10, 12, -120 }).size());
	}
	
	@Test
	public void testMissingIndicesOneNull()
	{
		List<Integer> missingIndices = clazz.missingIndices(new Byte[] { 10, 12, null, -120 });
		assertEquals(1, missingIndices.size());
		assertEquals(Integer.valueOf(2), missingIndices.get(0));
	}
	
	@Test
	public void testMissingIndicesManyNulls()
	{
		List<Integer> missingIndices = clazz.missingIndices(new Byte[] { 10, 12, null, -120, null, null });
		assertEquals(3, missingIndices.size());
		assertEquals(Integer.valueOf(2), missingIndices.get(0));
		assertEquals(Integer.valueOf(4), missingIndices.get(1));
		assertEquals(Integer.valueOf(5), missingIndices.get(2));
	}
	
	@Test
	public void testCopyWithTooLargeLength()
	{
		Byte[] array = new Byte[] { 10, 123, 45 };
		byte[] copy = clazz.copy(array, array.length + 10);
		assertArrayEquals(new byte[] { 10, 123, 45 }, copy);
	}
	
	@Test
	public void testCopyWithSmallerLength()
	{
		Byte[] array = new Byte[] { 10, 123, 45 };
		byte[] copy = clazz.copy(array, 2);
		assertArrayEquals(new byte[] { 10, 123 }, copy);
	}
	
	@Test
	public void testCopyWithNulls()
	{
		Byte[] array = new Byte[] { 10, 123, null, 45, null };
		byte[] copy = clazz.copy(array, array.length);
		assertArrayEquals(new byte[] { 10, 123, 0, 45, 0 }, copy);
	}
	
	@Test
	public void testCopyNullArray()
	{
		assertNull(clazz.copy(null, 10));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCopyWithNegativeLength()
	{
		assertNull(clazz.copy(null, -1));
	}
}
