package com.nickww.finitefield.checksum;

import static org.junit.Assert.*;
import org.junit.Test;

import com.nickww.finitefield.checksum.DoubleChecksumVector;

/**
 * Implementation of {@link https://www.kernel.org/pub/linux/kernel/people/hpa/raid6.pdf}
 * 
 * @author Nick Wuensch
 *
 */
public class DoubleChecksumVectorTest
{
	private static final byte[] data = { 45, -123, 10 };
	
	@Test(expected = NullPointerException.class)
	public void testWithChecksumsNullParameter()
	{
		new DoubleChecksumVector().withChecksums(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testWithChecksumsEmptyParameter()
	{
		new DoubleChecksumVector().withChecksums(new byte[0]);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testWithChecksumsParameterTooSmall()
	{
		new DoubleChecksumVector().withChecksums(new byte[1]);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSolveMissingValuesNullParameter()
	{
		new DoubleChecksumVector().solveMissingValues(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSolveMissingValuesEmptyParameter()
	{
		new DoubleChecksumVector().solveMissingValues(new Byte[0]);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSolveMissingValuesTooSmallParameter1()
	{
		new DoubleChecksumVector().solveMissingValues(new Byte[1]);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSolveMissingValuesTooSmallParameter2()
	{
		new DoubleChecksumVector().solveMissingValues(new Byte[2]);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSolveMissingValuesTooManyNulls()
	{
		Byte[] data = new Byte[] { 45, null, null, null };
		new DoubleChecksumVector().solveMissingValues(data);
	}

	@Test
	public void testSolveNoMissingValues()
	{
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		Byte[] missingData = copyWithSomeMissing(dataWithChecksums);
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveTwoMissingDataValues()
	{
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		Byte[] missingData = copyWithSomeMissing(dataWithChecksums, 0, 1);
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveMissingDataAndPValues()
	{
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		Byte[] missingData = copyWithSomeMissing(dataWithChecksums, 0, data.length);
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveMissingDataAndQValues()
	{
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		Byte[] missingData = copyWithSomeMissing(dataWithChecksums, 0, data.length + 1);
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveMissingChecksumValues()
	{
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		Byte[] missingData = copyWithSomeMissing(dataWithChecksums, data.length, data.length + 1);
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveOneMissingDataValues()
	{
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		Byte[] missingData = copyWithSomeMissing(dataWithChecksums, 0);
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveOneMissingPValue()
	{
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		Byte[] missingData = copyWithSomeMissing(dataWithChecksums, data.length);
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveOneMissingQValue()
	{
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		Byte[] missingData = copyWithSomeMissing(dataWithChecksums, data.length + 1);
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	private Byte[] copyWithSomeMissing(byte[] array, int... indicesToNull)
	{
		Byte[] missingData = new Byte[array.length];
		for(int i = 0; i < array.length; i++)
			missingData[i] = array[i];
		for(int i = 0; i < indicesToNull.length; i++)
			missingData[indicesToNull[i]] = null;
		return missingData;
	}
}
