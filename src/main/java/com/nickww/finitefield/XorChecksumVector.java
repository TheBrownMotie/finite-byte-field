package com.nickww.finitefield;

import static com.nickww.finitefield.FiniteByteField.*;

final class XorChecksumVector extends ChecksumVector
{
	@Override
	public byte[] withChecksums(byte[] data)
	{
		if(data.length < 1)
			throw new IllegalArgumentException("Data array must have at least one element");
		
		byte[] checksummed = new byte[data.length + 1];
		byte checksum = 0;
		for(int i = 0; i < data.length; i++)
		{
			checksum = add(checksum, data[i]);
			checksummed[i] = data[i];
		}
		checksummed[data.length] = checksum;
		return checksummed;
	}
	
	@Override
	public byte[] solveMissingValues(Byte[] dataWithChecksums)
	{
		if(dataWithChecksums.length < 2)
			throw new IllegalArgumentException("Array too small to include both data and checksums.");
		
		byte[] data = new byte[dataWithChecksums.length - 1];
		byte checksum = 0;
		int indexOfNull = -1;
		for(int i = 0; i < dataWithChecksums.length; i++)
		{
			if(dataWithChecksums[i] == null)
			{
				if(indexOfNull != -1)
					throw new IllegalArgumentException("Too many nulls - can only handle 1");
				indexOfNull = i;
			}
			else
			{
				checksum = add(checksum, dataWithChecksums[i]);
				if(i < data.length)
					data[i] = dataWithChecksums[i];
			}
		}
		
		if(indexOfNull >= data.length)
			return data;
		data[indexOfNull] = checksum;
		return data;
	}
}
