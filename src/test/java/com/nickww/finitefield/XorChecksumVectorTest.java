package com.nickww.finitefield;

import static org.junit.Assert.*;

import org.junit.Test;

public class XorChecksumVectorTest
{
	@Test(expected=NullPointerException.class)
	public void testWithChecksumsNullParameter()
	{
		new XorChecksumVector().withChecksums(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWithChecksumsEmptyParameter()
	{
		new XorChecksumVector().withChecksums(new byte[0]);
	}
	
	@Test
	public void testWithChecksums()
	{
		byte[] data = new byte[] {45, -123, 10};
		byte[] dataWithChecksums = new byte[] {45, -123, 10, (45 ^ -123 ^ 10)};
		assertArrayEquals(dataWithChecksums, new XorChecksumVector().withChecksums(data));
	}
	
	@Test(expected=NullPointerException.class)
	public void testSolveMissingValuesNullParameter()
	{
		new XorChecksumVector().solveMissingValues(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSolveMissingValuesEmptyParameter()
	{
		new XorChecksumVector().solveMissingValues(new Byte[0]);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSolveMissingValuesTooSmallParameter()
	{
		new XorChecksumVector().solveMissingValues(new Byte[1]);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSolveMissingValuesTooManyNulls()
	{
		Byte[] data = new Byte[] {45, null, null};
		new XorChecksumVector().solveMissingValues(data);
	}
	
	@Test
	public void testSolveMissingDataValues()
	{
		byte[] data = new byte[] {45, -123, 10};
		Byte[] dataWithChecksums = new Byte[] {45, null, 10, (45 ^ -123 ^ 10)};
		assertArrayEquals(data, new XorChecksumVector().solveMissingValues(dataWithChecksums));
	}
	
	@Test
	public void testSolveMissingChecksumValues()
	{
		byte[] data = new byte[] {45, -123, 10};
		Byte[] dataWithChecksums = new Byte[] {45, -123, 10, null};
		assertArrayEquals(data, new XorChecksumVector().solveMissingValues(dataWithChecksums));
	}
}
