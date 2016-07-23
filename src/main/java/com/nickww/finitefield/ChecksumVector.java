package com.nickww.finitefield;

import java.util.ArrayList;
import java.util.List;

public abstract class ChecksumVector
{
	private static final XorChecksumVector xor = new XorChecksumVector();
	private static final DoubleChecksumVector raid6 = new DoubleChecksumVector();
	private static final MatrixChecksumVector[] general = new MatrixChecksumVector[FiniteByteField.MAX_VALUE];
	
	public static ChecksumVector build(int numChecksums)
	{
		if(numChecksums == 1)
			return xor;
		if(numChecksums == 2)
			return raid6;
		
		if(general[numChecksums] == null)
			general[numChecksums] = new MatrixChecksumVector(numChecksums);
		return general[numChecksums];
	}
	
	/**
	 * Calculates checksums for the given data, and returns a vector which is the given data bytes followed by the
	 * checksum bytes.
	 * 
	 * @param data The data to checksum.
	 * @return An array with the data and the checksums.
	 */
	public abstract byte[] withChecksums(byte[] data);
	
	/**
	 * Calculates the data bytes from the given array. The array must represent the data bytes, in order, followed by
	 * the checksum bytes, in order. Values which are missing (to be solved for) should be present as a
	 * <code>null</code>.
	 * 
	 * @param dataWithChecksums The data, followed by the checksums, with nulls for unknown values.
	 * @return An array with the original data bytes (no checksums).
	 * @throws IllegalArgumentException if the given byte array has too many missing values (nulls) to recalculate the
	 * original data, or if the array is not large enough to be valid.
	 */
	public abstract byte[] solveMissingValues(Byte[] dataWithChecksums);
	
	/**
	 * Returns a list of the indices of the given array whose values are null. If the given array is null, null is
	 * returned.
	 * 
	 * @param array The array in which to check for nulls.
	 * @return The indices of the given array which are null, sorted in ascending order, or null.
	 */
	protected List<Integer> missingIndices(Byte[] array)
	{
		if(array == null)
			return null;
		
		List<Integer> nulls = new ArrayList<>();
		for(int index = 0; index < array.length; index++)
			if(array[index] == null)
				nulls.add(index);
		return nulls;
	}
	
	/**
	 * Returns a primitive copy of the given byte-object array. The number of elements copied can be limited by a given
	 * parameter. Any values in the given array which are null will be copied as '0'.
	 * 
	 * @param array The array to copy from.
	 * @param length The maximum number of elements to copy. If this value is greater than the length of the given
	 * array, the returned array will be the same size as the given array.
	 * @return The primitive copy of the given array, with 0s in place of nulls.
	 * @throws IllegalArgumentException if the length limit is negative.
	 */
	protected byte[] copy(Byte[] array, int length)
	{
		if(length < 0)
			throw new IllegalArgumentException("Length of desired copy cannot be null");
		if(array == null)
			return null;
		
		byte[] copy = new byte[Math.min(array.length, length)];
		for(int i = 0; i < copy.length; i++)
			if(array[i] != null)
				copy[i] = array[i];
		return copy;
	}
}
