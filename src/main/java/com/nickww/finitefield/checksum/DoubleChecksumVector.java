package com.nickww.finitefield.checksum;

import static com.nickww.finitefield.FiniteByteField.*;

import java.util.List;

class DoubleChecksumVector extends ChecksumVector
{
	private static final byte[] powerOfTwo = new byte[MAX_VALUE + 1];
	private static final byte[][] A = new byte[MAX_VALUE + 1][MAX_VALUE + 1];
	private static final byte[][] B = new byte[MAX_VALUE + 1][MAX_VALUE + 1];
	
	static
	{
		powerOfTwo[0] = 1;
		for(int i = 1; i <= MAX_VALUE; i++)
			powerOfTwo[i] = dbl(powerOfTwo[i - 1]);
		
		for(int x = 0; x <= MAX_VALUE; x++)
			for(int y = 0; y <= MAX_VALUE; y++)
				A[x][y] = div(powerOfTwo[Math.abs(y - x)], add(powerOfTwo[Math.abs(y - x)], (byte) 1));
			
		for(int x = 0; x <= MAX_VALUE; x++)
			for(int y = 0; y <= MAX_VALUE; y++)
				B[x][y] = div(powerOfTwo[Math.min(x, y)], add(powerOfTwo[Math.abs(y - x)], (byte) 1));
	}
	
	@Override
	public byte[] withChecksums(byte[] data)
	{
		if(data.length < 2)
			throw new IllegalArgumentException("Data array must have at least two elements");
		
		byte[] checksummed = new byte[data.length + 2];
		System.arraycopy(data, 0, checksummed, 0, data.length);
		
		checksummed[data.length] = add(data);
		checksummed[data.length + 1] = getQ(data);
		
		return checksummed;
	}
	
	@Override
	public byte[] solveMissingValues(Byte[] dataWithChecksums)
	{
		if(dataWithChecksums.length < 3)
			throw new IllegalArgumentException("Array too small to include both data and checksums.");
		
		List<Integer> missingIndices = super.missingIndices(dataWithChecksums);
		if(missingIndices.size() > 2)
			throw new IllegalArgumentException("Too many missing values - can only handle 2");
		
		byte[] data = super.copy(dataWithChecksums, dataWithChecksums.length - 2);
		if(missingIndices.isEmpty() || missingIndices.get(0) >= data.length)
			return data;
		
		Byte p = dataWithChecksums[dataWithChecksums.length - 2];
		Byte q = dataWithChecksums[dataWithChecksums.length - 1];
		int x = missingIndices.get(0);
		
		if(missingIndices.size() == 1 || q == null)
			return withValue(data, x, add(p, data));
		if(p == null)
			return withValue(data, x, div(add(getQ(data), q), powerOfTwo[x]));
		
		int y = missingIndices.get(1);
		data[x] = add(mul(A[x][y], add(p, data)), mul(B[x][y], add(getQ(data), q)));
		data[y] = add(p, add(data));
		return data;
	}
	
	/**
	 * Calculates the value of the Reed-Solomon double parity byte from the given data. This is the sum of each byte
	 * multiplied by 2<sup>index</sup>. For example, the bytes (1, 24, 54) would produce the parity
	 * <code>2<sup>0</sup>*1 + 2<sup>1</sup>*23 + 2<sup>2</sup>*54 = 1*1 + 2*23 + 4*54</code>.<br/>
	 * <br/>
	 * Precondition: input parameter is not null
	 */
	private static byte getQ(byte[] data)
	{
		byte q = 0;
		for(int i = data.length - 1; i >= 0; i--)
			q = add(dbl(q), data[i]);
		return q;
	}
	
	/**
	 * Returns the given array, with the given value placed in the given index. This is just for the convenience of
	 * setting a value and returning the array in one line.<br/>
	 * <br/>
	 * Precondition: array is not null, index is a valid position in the array.
	 */
	private byte[] withValue(byte[] array, int index, byte value)
	{
		array[index] = value;
		return array;
	}
}
