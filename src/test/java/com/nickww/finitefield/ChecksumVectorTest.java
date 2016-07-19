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
		assertEquals(0, clazz.missingIndices(new Byte[]{10, 12, -120}).size());
	}
	
	@Test
	public void testMissingIndicesOneNull()
	{
		List<Integer> missingIndices = clazz.missingIndices(new Byte[]{10, 12, null, -120});
		assertEquals(1, missingIndices.size());
		assertEquals(Integer.valueOf(2), missingIndices.get(0));
	}
	
	@Test
	public void testMissingIndicesManyNulls()
	{
		List<Integer> missingIndices = clazz.missingIndices(new Byte[]{10, 12, null, -120, null, null});
		assertEquals(3, missingIndices.size());
		assertEquals(Integer.valueOf(2), missingIndices.get(0));
		assertEquals(Integer.valueOf(4), missingIndices.get(1));
		assertEquals(Integer.valueOf(5), missingIndices.get(2));
	}
}
