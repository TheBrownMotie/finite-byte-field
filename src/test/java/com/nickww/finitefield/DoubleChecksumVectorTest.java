package com.nickww.finitefield;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Implementation of {@link https://www.kernel.org/pub/linux/kernel/people/hpa/raid6.pdf}
 * 
 * @author Nick Wuensch
 *
 */
public class DoubleChecksumVectorTest
{
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
		byte[] data = new byte[] { 45, -123, 10 };
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		
		Byte[] missingData = new Byte[dataWithChecksums.length];
		for(int i = 0; i < dataWithChecksums.length; i++)
			missingData[i] = dataWithChecksums[i];
		
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveTwoMissingDataValues()
	{
		byte[] data = new byte[] { 45, -123, 10 };
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		
		Byte[] missingData = new Byte[dataWithChecksums.length];
		for(int i = 0; i < dataWithChecksums.length; i++)
			missingData[i] = dataWithChecksums[i];
		missingData[0] = null;
		missingData[1] = null;
		
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveMissingDataAndPValues()
	{
		byte[] data = new byte[] { 45, -123, 10 };
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		
		Byte[] missingData = new Byte[dataWithChecksums.length];
		for(int i = 0; i < dataWithChecksums.length; i++)
			missingData[i] = dataWithChecksums[i];
		missingData[0] = null;
		missingData[data.length] = null;
		
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveMissingDataAndQValues()
	{
		byte[] data = new byte[] { 45, -123, 10 };
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		
		Byte[] missingData = new Byte[dataWithChecksums.length];
		for(int i = 0; i < dataWithChecksums.length; i++)
			missingData[i] = dataWithChecksums[i];
		missingData[0] = null;
		missingData[data.length + 1] = null;
		
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveMissingChecksumValues()
	{
		byte[] data = new byte[] { 45, -123, 10 };
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		
		Byte[] missingData = new Byte[dataWithChecksums.length];
		for(int i = 0; i < dataWithChecksums.length; i++)
			missingData[i] = dataWithChecksums[i];
		missingData[data.length] = null;
		missingData[data.length + 1] = null;
		
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveOneMissingDataValues()
	{
		byte[] data = new byte[] { 45, -123, 10 };
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		
		Byte[] missingData = new Byte[dataWithChecksums.length];
		for(int i = 0; i < dataWithChecksums.length; i++)
			missingData[i] = dataWithChecksums[i];
		missingData[0] = null;
		
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveOneMissingPValue()
	{
		byte[] data = new byte[] { 45, -123, 10 };
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		
		Byte[] missingData = new Byte[dataWithChecksums.length];
		for(int i = 0; i < dataWithChecksums.length; i++)
			missingData[i] = dataWithChecksums[i];
		missingData[data.length] = null;
		
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
	
	@Test
	public void testSolveOneMissingQValue()
	{
		byte[] data = new byte[] { 45, -123, 10 };
		byte[] dataWithChecksums = new DoubleChecksumVector().withChecksums(data);
		
		Byte[] missingData = new Byte[dataWithChecksums.length];
		for(int i = 0; i < dataWithChecksums.length; i++)
			missingData[i] = dataWithChecksums[i];
		missingData[data.length + 1] = null;
		
		byte[] originalData = new DoubleChecksumVector().solveMissingValues(missingData);
		assertArrayEquals(data, originalData);
	}
}
