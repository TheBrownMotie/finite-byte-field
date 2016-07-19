package com.nickww.finitefield;

import java.util.ArrayList;
import java.util.List;

public abstract class ChecksumVector
{
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
}
