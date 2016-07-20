package com.nickww.finitefield;

import static com.nickww.finitefield.FiniteByteField.*;

import java.util.List;

class DoubleChecksumVector extends ChecksumVector
{
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
		
		byte[] data = super.copy(dataWithChecksums, dataWithChecksums.length-2);
		
		List<Integer> missingIndices = super.missingIndices(dataWithChecksums);
		Byte p = dataWithChecksums[dataWithChecksums.length-2];
		Byte q = dataWithChecksums[dataWithChecksums.length-1];
		
		if(missingIndices.size() > 2)
		{
			throw new IllegalArgumentException("Too many missing values - can only handle 2");
		}
		else if(missingIndices.isEmpty())
		{
			return data;
		}
		else if(missingIndices.size() == 1)
		{
			if(p == null || q == null)
				return data;
			else
				data[missingIndices.get(0)] = add(p, data);
		}
		else if(missingIndices.size() == 2)
		{
			if(p == null && q == null)
				return data;
			else if(q == null)
				data[missingIndices.get(0)] = add(p, data);
			else if(p == null)
				data[missingIndices.get(0)] = div(add(getQ(data), q), powerOfTwo(missingIndices.get(0)));
			else
			{
				byte a = getA(missingIndices.get(0), missingIndices.get(1));
				byte b = getB(missingIndices.get(0), missingIndices.get(1));
				data[missingIndices.get(0)] = add(mul(a, add(p, data)), mul(b, add(getQ(data), q)));
				data[missingIndices.get(1)] = add(p, add(data));
			}
		}
		
		return data;
	}
	
	private static byte getQ(byte[] data)
	{
		byte q = 0;
		for(int i = data.length - 1; i >= 0; i--)
			q = add(dbl(q), data[i]);
		return q;
	}
	
	private static byte powerOfTwo(int exponent)
	{
		byte result = 1;
		for(int i = 0; i < exponent; i++)
			result = dbl(result);
		return result;
	}
	
	private static byte getA(int x, int y)
	{
		byte value = powerOfTwo(Math.abs(y - x));
		return div(value, add(value, (byte) 1));
	}
	
	private static byte getB(int x, int y)
	{
		byte value = powerOfTwo(Math.abs(y - x));
		return div(powerOfTwo(x), add(value, (byte) 1));
	}
}
