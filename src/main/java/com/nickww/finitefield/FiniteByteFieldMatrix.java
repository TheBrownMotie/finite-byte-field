package com.nickww.finitefield;

import static com.nickww.finitefield.FiniteByteField.*;
import java.util.Arrays;

/**
 * 
 * @author Nick Wuensch
 */
public final class FiniteByteFieldMatrix
{
	public static FiniteByteFieldMatrix identity(int size)
	{
		byte[][] identity = new byte[size][size];
		for(int i = 0; i < size; i++)
			identity[i][i] = 1;
		return new FiniteByteFieldMatrix(identity);
	}
	
	private final byte[][] data;
	
	public FiniteByteFieldMatrix(byte[][] data)
	{
		this.data = data;
		if(data.length == 0 || data[0].length == 0)
			throw new IllegalArgumentException("Both dimensions of matrix must be non-zero");
		
		int cols = data[0].length;
		for(int i = 1; i < data.length; i++)
			if(data[i].length != cols)
				throw new IllegalArgumentException("All rows in array must be the same length");
	}
	
	public FiniteByteFieldMatrix minor(int deletedRow, int deletedCol)
	{
		if(deletedRow >= numRows() || deletedRow < 0)
			throw new IndexOutOfBoundsException(
					"Row index " + deletedRow + " out of bounds for matrix with " + numRows() + " rows");
		if(deletedCol >= numCols() || deletedCol < 0)
			throw new IndexOutOfBoundsException(
					"Column index " + deletedCol + " out of bounds for matrix with " + numCols() + " cols");
		
		byte[][] minor = new byte[numRows() - 1][numCols() - 1];
		
		int minorRow = 0;
		int minorCol = 0;
		for(int row = 0; row < numRows(); row++)
		{
			if(row != deletedRow)
			{
				for(int col = 0; col < numCols(); col++)
				{
					if(col != deletedCol)
					{
						minor[minorRow][minorCol] = data[row][col];
						minorCol++;
					}
				}
				minorRow++;
			}
			minorCol = 0;
		}
		return new FiniteByteFieldMatrix(minor);
	}
	
	public byte determinant()
	{
		if(!isSquare())
			throw new IllegalStateException("Only a square matrix has a determinant");
		
		int size = numRows(); // must be same as numCols()
		if(size == 2)
			return add(mul(data[0][0], data[1][1]), mul(data[0][1], data[1][0]));
		
		byte sum = 0;
		byte[] firstRow = data[0];
		for(int col = 0; col < size; col++)
			sum = add(sum, mul(firstRow[col], minor(0, col).determinant()));
		return sum;
	}
	
	public FiniteByteFieldMatrix transpose()
	{
		byte[][] transpose = new byte[numCols()][numRows()];
		for(int row = 0; row < numRows(); row++)
			for(int col = 0; col < numCols(); col++)
				transpose[col][row] = data[row][col];
		return new FiniteByteFieldMatrix(transpose);
	}
	
	public FiniteByteFieldMatrix cofactor()
	{
		byte[][] cofactor = new byte[numRows()][numCols()];
		for(int i = 0; i < numRows(); i++)
			for(int j = 0; j < numCols(); j++)
				cofactor[i][j] = this.minor(i, j).determinant();
		
		return new FiniteByteFieldMatrix(cofactor);
	}
	
	public FiniteByteFieldMatrix times(final byte constant)
	{
		byte[][] newData = new byte[numRows()][numCols()];
		for(int row = 0; row < numRows(); row++)
			for(int col = 0; col < numCols(); col++)
				newData[row][col] = mul(data[row][col], constant);
		return new FiniteByteFieldMatrix(newData);
	}
	
	public FiniteByteFieldMatrix times(FiniteByteFieldMatrix matrix)
	{
		byte[][] cols = this.transpose().data;
		byte[][] thatCols = matrix.transpose().data;
		
		if(cols.length != matrix.data.length)
			throw new IllegalArgumentException("Matrix dimensions must match to be multiplied");
		
		byte[][] product = new byte[this.data.length][thatCols.length];
		for(int row = 0; row < this.data.length; row++)
			for(int col = 0; col < thatCols.length; col++)
				product[row][col] = dot(this.data[row], thatCols[col]);
		return new FiniteByteFieldMatrix(product);
	}
	
	public FiniteByteFieldMatrix divideBy(final byte constant)
	{
		byte[][] newData = new byte[numRows()][numCols()];
		for(int row = 0; row < numRows(); row++)
			for(int col = 0; col < numCols(); col++)
				newData[row][col] = div(data[row][col], constant);
		return new FiniteByteFieldMatrix(newData);
	}
	
	public FiniteByteFieldMatrix solve(FiniteByteFieldMatrix product)
	{
		return this.inverse().times(product);
	}
	
	public FiniteByteFieldMatrix inverse()
	{
		return transpose().cofactor().divideBy(determinant());
	}
	
	public int numRows()
	{
		return data.length;
	}
	
	public int numCols()
	{
		return data[0].length;
	}
	
	public boolean isSquare()
	{
		return numRows() == numCols();
	}
	
	@Override
	public final boolean equals(Object o)
	{
		if(!(o instanceof FiniteByteFieldMatrix))
			return false;
		
		FiniteByteFieldMatrix that = (FiniteByteFieldMatrix) o;
		return Arrays.deepEquals(this.data, that.data);
	}
	
	@Override
	public final int hashCode()
	{
		return Arrays.deepHashCode(this.data);
	}
	
	@Override
	public final String toString()
	{
		StringBuilder string = new StringBuilder();
		for(int i = 0; i < this.data.length; i++)
			string.append(Arrays.toString(this.data[i])).append("\n");
		return string.toString();
	}
}
