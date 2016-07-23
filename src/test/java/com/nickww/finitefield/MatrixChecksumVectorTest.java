package com.nickww.finitefield;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;

import static com.nickww.finitefield.FiniteByteField.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MatrixChecksumVectorTest
{
	private static final byte[] data = new byte[] { 10, 20, -123, -64, 92 };
	
	@Parameters
	public static Collection<Integer[]> sizesToTest()
	{
		return Arrays.asList(new Integer[] { 3 }, new Integer[] { 4 }, new Integer[] { 5 }, new Integer[] { 6 });
	}
	
	@AfterClass
	public static void checkByteArrayUnaltered()
	{
		assertArrayEquals(new byte[] { 10, 20, -123, -64, 92 }, data);
	}
	
	private final Integer size;
	
	public MatrixChecksumVectorTest(Integer size)
	{
		this.size = size;
	}
	
	@Test
	public void testSolveAllMissingData()
	{
		MatrixChecksumVector vector = new MatrixChecksumVector(size);
		byte[] checksummed = vector.withChecksums(data);
		
		Byte[] withMissing = copyWithSomeMissing(checksummed, 1, 2, 4);
		byte[] solution = vector.solveMissingValues(withMissing);
		assertArrayEquals(solution, data);
	}
	
	@Test
	public void testSolveSomeMissingDataAndChecksums()
	{
		MatrixChecksumVector vector = new MatrixChecksumVector(size);
		byte[] checksummed = vector.withChecksums(data);
		
		Byte[] withMissing = copyWithSomeMissing(checksummed, 1, 2, 6);
		byte[] solution = vector.solveMissingValues(withMissing);
		assertArrayEquals(solution, data);
	}
	
	@Test
	public void testSolveAllMissingChecksums()
	{
		MatrixChecksumVector vector = new MatrixChecksumVector(size);
		byte[] checksummed = vector.withChecksums(data);
		
		Byte[] withMissing = copyWithSomeMissing(checksummed, 5, 6, 7);
		byte[] solution = vector.solveMissingValues(withMissing);
		assertArrayEquals(solution, data);
	}
	
	@Test
	public void testSolvePartialAllMissingData()
	{
		MatrixChecksumVector vector = new MatrixChecksumVector(size);
		byte[] checksummed = vector.withChecksums(data);
		
		Byte[] withMissing = copyWithSomeMissing(checksummed, 2, 3);
		byte[] solution = vector.solveMissingValues(withMissing);
		assertArrayEquals(solution, data);
	}
	
	@Test
	public void testSolvePartialSomeMissingDataAndChecksums()
	{
		MatrixChecksumVector vector = new MatrixChecksumVector(size);
		byte[] checksummed = vector.withChecksums(data);
		
		Byte[] withMissing = copyWithSomeMissing(checksummed, 2, 6);
		byte[] solution = vector.solveMissingValues(withMissing);
		assertArrayEquals(solution, data);
	}
	
	@Test
	public void testSolvePartialAllMissingChecksums()
	{
		MatrixChecksumVector vector = new MatrixChecksumVector(size);
		byte[] checksummed = vector.withChecksums(data);
		
		Byte[] withMissing = copyWithSomeMissing(checksummed, 6, 7);
		byte[] solution = vector.solveMissingValues(withMissing);
		assertArrayEquals(solution, data);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTooFewValuesForChecksums()
	{
		MatrixChecksumVector vector = new MatrixChecksumVector(size);
		vector.withChecksums(new byte[] { 1, 2 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTooManyValuesForChecksums()
	{
		byte[] largeData = new byte[MAX_VALUE + 1];
		MatrixChecksumVector vector = new MatrixChecksumVector(size);
		vector.withChecksums(largeData);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSolveNullParameter()
	{
		MatrixChecksumVector vector = new MatrixChecksumVector(size);
		vector.solveMissingValues(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSolveTooManyMissingValues()
	{
		Byte[] nullChecksums = new Byte[size];
		Byte[] data = new Byte[] {10, 20, 30, 40, null};
		Byte[] valuesToSolve = new Byte[data.length + nullChecksums.length];
		System.arraycopy(data, 0, valuesToSolve, 0, data.length);
		System.arraycopy(nullChecksums, 0, valuesToSolve, data.length, nullChecksums.length);
		
		MatrixChecksumVector vector = new MatrixChecksumVector(size);
		vector.solveMissingValues(valuesToSolve);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidArrayTooSmall()
	{
		MatrixChecksumVector vector = new MatrixChecksumVector(size);
		vector.solveMissingValues(new Byte[] { 10, 20 });
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
