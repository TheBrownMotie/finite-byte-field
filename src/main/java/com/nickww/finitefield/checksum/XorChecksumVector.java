package com.nickww.finitefield.checksum;

import static com.nickww.finitefield.FiniteByteField.*;

import java.util.List;

final class XorChecksumVector extends ChecksumVector
{
	@Override
	public byte[] withChecksums(byte[] data)
	{
		if(data.length < 1)
			throw new IllegalArgumentException("Data array must have at least one element");
		
		byte[] checksummed = new byte[data.length + 1];
		System.arraycopy(data, 0, checksummed, 0, data.length);
		checksummed[checksummed.length - 1] = add(data);
		return checksummed;
	}
	
	@Override
	public byte[] solveMissingValues(Byte[] dataWithChecksums)
	{
		if(dataWithChecksums.length < 2)
			throw new IllegalArgumentException("Array too small to include both data and checksums.");
		
		byte[] data = super.copy(dataWithChecksums, dataWithChecksums.length - 1);
		
		List<Integer> nullIndices = super.missingIndices(dataWithChecksums);
		if(nullIndices.size() > 1)
			throw new IllegalArgumentException("Too many missing values - can only handle 1");
		
		if(!nullIndices.isEmpty() && nullIndices.get(0) != data.length)
			data[nullIndices.get(0)] = add(dataWithChecksums[dataWithChecksums.length - 1], add(data));
		return data;
	}
}
