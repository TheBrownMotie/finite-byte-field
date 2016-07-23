package com.nickww.finitefield.checksum;

import static com.nickww.finitefield.FiniteByteField.*;

import java.util.List;

import com.nickww.finitefield.FiniteByteFieldMatrix;

/**
 * Creates 3+ checksums for data, using the general principles outlined in
 * {@link http://web.eecs.utk.edu/~plank/plank/papers/CS-96-332.pdf}
 * 
 * @author Nick Wuensch
 */
class MatrixChecksumVector extends ChecksumVector
{
	/**
	 * Generates the elements of a vandermonde matrix.<br />
	 * <br />
	 * Precondition: row and col are both less than the finite field's maximum value.
	 */
	private static byte vandermondeElements(int row, int col)
	{
		byte base = (byte) ((col + 1) & 0xff);
		byte exponent = (byte) (row & 0xff);
		return pow(base, exponent);
	}
	
	private final int size;
	private final FiniteByteFieldMatrix[] vandermonde;
	
	public MatrixChecksumVector(int size)
	{
		this.size = size;
		this.vandermonde = new FiniteByteFieldMatrix[MAX_VALUE];
		for(int i = 3; i < MAX_VALUE; i++)
			this.vandermonde[i] = FiniteByteFieldMatrix.build(size, i, MatrixChecksumVector::vandermondeElements);
	}
	
	@Override
	public byte[] withChecksums(byte[] data)
	{
		if(data.length >= MAX_VALUE)
			throw new IllegalArgumentException("Cannot checksum more than " + MAX_VALUE + " bytes");
		if(data.length < 3)
			throw new IllegalArgumentException("Data array must have at least three elements");
		
		FiniteByteFieldMatrix dataColumn = FiniteByteFieldMatrix.columnVector(data);
		FiniteByteFieldMatrix checksums = vandermonde[data.length].times(dataColumn);
		return dataColumn.appendRows(checksums).getCol(0);
	}
	
	@Override
	public byte[] solveMissingValues(Byte[] dataWithChecksums)
	{
		final int dataLength = dataWithChecksums.length - size;
		if(dataLength < 3)
			throw new IllegalArgumentException("Array too small to include both data and checksums.");
		
		List<Integer> nullIndices = super.missingIndices(dataWithChecksums);
		if(nullIndices.size() > size)
			throw new IllegalArgumentException("Too many missing values - can only handle " + size);
		
		FiniteByteFieldMatrix identity = FiniteByteFieldMatrix.identity(dataLength);
		FiniteByteFieldMatrix knownRows = identity.appendRows(vandermonde[dataLength]).withoutRows(nullIndices);
		while(!knownRows.isSquare())
			knownRows = knownRows.withoutRows(knownRows.numRows() - 1);
		
		// knownData's length must match knownRow's row-length for matrix multiplication, so keep no more than
		// 'dataLength' bytes regardless
		byte[] knownData = new byte[dataLength];
		int knownDataIndex = 0;
		for(int i = 0; i < dataWithChecksums.length && knownDataIndex < knownData.length; i++)
			if(dataWithChecksums[i] != null)
				knownData[knownDataIndex++] = dataWithChecksums[i];
		
		return knownRows.solve(FiniteByteFieldMatrix.columnVector(knownData)).getCol(0);
	}
}
