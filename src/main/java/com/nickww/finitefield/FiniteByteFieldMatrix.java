package com.nickww.finitefield;

import static com.nickww.finitefield.FiniteByteField.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * This class represents a 2d matrix of bytes, which exist in the finite field GF(2<sup>8</sup>). Instances of this
 * class are immutable and thread-safe.
 * 
 * @author Nick Wuensch
 */
public final class FiniteByteFieldMatrix
{
	/**
	 * Creates the identity matrix of the given size.
	 * 
	 * An identity matrix is a square matrix, such that multiplying it with any other matrix will result in the original
	 * matrix. It is constructed with 1s on the diagonal and 0s everywhere else.
	 * 
	 * @param size The size of the identity matrix to return.
	 * @return The identity matrix for the given size.
	 */
	public static FiniteByteFieldMatrix identity(int size)
	{
		byte[][] identity = new byte[size][size];
		for(int i = 0; i < size; i++)
			identity[i][i] = 1;
		return new FiniteByteFieldMatrix(identity);
	}
	
	/**
	 * Constructs a new matrix of the given size, where each element is provided by the given function. The function
	 * will be given a row and column 0-based index, and the result will be the value of the given matrix at that row
	 * and column. The given function will never be passed null values, and will only be passed values from 0
	 * (inclusive) to the given row/column limit (exclusive).
	 * 
	 * @param numRows The number of rows for the returned matrix.
	 * @param numCols The number of columns for the returned matrix.
	 * @param matrixElementFunction The function to produce the elements of the returned matrix.
	 * @return The constructed matrix of the appropriate size, with values returned from the given function.
	 * @throws NullPointerException if the given function to produce elements is null.
	 * @throws IllegalArgumentException if the given numRows or numCols values are less than 1.
	 */
	public static FiniteByteFieldMatrix build(int numRows, int numCols, ToByteBiFunction<Integer, Integer> element)
	{
		if(numRows < 1 || numCols < 1)
			throw new IllegalArgumentException("The given bounds for the matrix must be greater than 0");
		
		byte[][] data = new byte[numRows][numCols];
		for(int row = 0; row < numRows; row++)
			for(int col = 0; col < numCols; col++)
				data[row][col] = element.applyAsByte(row, col);
		return new FiniteByteFieldMatrix(data);
	}
	
	/**
	 * Constructs a new matrix consisting of only one column, such that each element of the given array is each element
	 * of the column, in order.
	 * 
	 * @param bytes The data with which to make a single-column matrix.
	 * @return The new matrix with the given data.
	 */
	public static FiniteByteFieldMatrix columnVector(byte[] bytes)
	{
		byte[][] column = new byte[bytes.length][1];
		for(int row = 0; row < bytes.length; row++)
			column[row][0] = bytes[row];
		return new FiniteByteFieldMatrix(column);
	}
	
	/**
	 * Constructs a new matrix consisting of only one row using the given byte array.
	 * 
	 * @param bytes The single row of data to exist in the new matrix.
	 * @return The new matrix with the given data.
	 */
	public static FiniteByteFieldMatrix rowVector(byte[] bytes)
	{
		return new FiniteByteFieldMatrix(new byte[][] { bytes });
	}
	
	private final byte[][] data;
	private final int rows;
	private final int cols;
	
	private volatile FiniteByteFieldMatrix transpose;
	private volatile FiniteByteFieldMatrix cofactor;
	private volatile FiniteByteFieldMatrix inverse;
	private volatile Byte determinant;
	
	/**
	 * Creates a new matrix with the given byte data. This class is immutable - the array can be safely altered after
	 * constructing this object without altering the object.
	 * 
	 * @param data The byte data of this matrix.
	 * @throws NullPointerException if the given array is null.
	 * @throws IllegalArgumentException if the matrix has no rows, no columns, or the rows are not all the same length.
	 */
	public FiniteByteFieldMatrix(byte[][] data)
	{
		if(data.length == 0 || data[0].length == 0)
			throw new IllegalArgumentException("Both dimensions of matrix must be non-zero");
		
		rows = data.length;
		cols = data[0].length;
		for(int i = 1; i < rows; i++)
			if(data[i].length != cols)
				throw new IllegalArgumentException("All rows in array must be the same length");
			
		this.data = copy(data);
	}
	
	/**
	 * Returns a matrix which has the same data as this matrix without the given row or column. In other words, a minor
	 * <code>M<sub>ij</sub></code> of matrix <code>A</code> has all the data of <code>A</code> without row
	 * <code>i</code> or column <code>j</code>.
	 * 
	 * @param deletedRow The row of this matrix to exclude in the returned matrix.
	 * @param deletedCol The column of this matrix to exclude in the returned matrix.
	 * @return The sub-matrix of this matrix without the given row and column.
	 * @throws IndexOutOfBoundsException if the given row or column is out of bounds for this matrix.
	 */
	public FiniteByteFieldMatrix minor(int deletedRow, int deletedCol)
	{
		if(deletedRow >= numRows() || deletedRow < 0)
			throw new IndexOutOfBoundsException("Row index out of bounds: " + deletedRow + " " + numRows());
		if(deletedCol >= numCols() || deletedCol < 0)
			throw new IndexOutOfBoundsException("Column index out of bounds: " + deletedCol + " " + numCols());
		
		byte[][] minor = new byte[numRows() - 1][numCols() - 1];
		
		int minorRow = 0;
		int minorCol = 0;
		for(int row = 0; row < numRows(); row++)
		{
			if(row != deletedRow)
			{
				for(int col = 0; col < numCols(); col++)
					if(col != deletedCol)
						minor[minorRow][minorCol++] = data[row][col];
				minorRow++;
			}
			minorCol = 0;
		}
		return new FiniteByteFieldMatrix(minor);
	}
	
	/**
	 * Returns the determinant of this matrix. This is calculated recursively. A square matrix with one row and one
	 * column only has one value, which is the determinant. Larger matrices are calculated recursively, by multiplying
	 * each value in the first row with the determinant of the matrix without its first row or the column of the value
	 * being multiplied, and then summing those products.
	 * 
	 * @return The determinant of this matrix.
	 * @throws IllegalStateException if this matrix is not a square matrix.
	 */
	public byte determinant()
	{
		if(!isSquare())
			throw new IllegalStateException("Only a square matrix has a determinant");
		if(determinant == null)
			this.determinant = calculateDeterminant();
		return determinant;
	}
	
	/**
	 * Returns the transpose of this matrix, which is the data "flipped", as though its rows were its columns and
	 * vice-versa.
	 * 
	 * @return The transposed matrix.
	 */
	public FiniteByteFieldMatrix transpose()
	{
		if(transpose == null)
			transpose = FiniteByteFieldMatrix.build(numCols(), numRows(), (r, c) -> data[c][r]);
		return transpose;
	}
	
	/**
	 * Returns the cofactor of this matrix, which is a new matrix of the same size such that each element
	 * <code>i, j</code> in the new matrix is the determinant of the {@link #minor(i, j)} of the original matrix.
	 * 
	 * @return The cofactor of this matrix.
	 */
	public FiniteByteFieldMatrix cofactor()
	{
		if(cofactor == null)
			cofactor = FiniteByteFieldMatrix.build(numRows(), numCols(), (r, c) -> minor(r, c).determinant());
		return cofactor;
	}
	
	/**
	 * Returns a new matrix where each element is multiplied by the given constant.
	 * 
	 * @param constant The constant to multiply each value with.
	 * @return A new matrix, with each value multiplied by the given constant.
	 * @see #divideBy(byte)
	 */
	public FiniteByteFieldMatrix times(final byte constant)
	{
		return FiniteByteFieldMatrix.build(numRows(), numCols(), (r, c) -> mul(data[r][c], constant));
	}
	
	/**
	 * Returns a new matrix, arrived at through the process of matrix multiplication. Matrix multiplication constructs a
	 * new matrix, where each element at position <code>i, j</code> is arrived by the dot product of the first matrix's
	 * row <code>i</code> and the second matrix's column <code>j</code>. Unlike typical arithmetic, order matters - the
	 * number of rows of the second matrix must match the number of columns of the first matrix. <br/>
	 * <br/>
	 * Illustration:
	 * 
	 * <pre>
	 *           [1 2]
	 *           [3 4]
	 *              |
	 *              v
	 *   [1, 2]--[->x]   // x = [1, 2] -dot- [2, 4]
	 *   [3, 4]  [   ]
	 *   [5, 6]  [   ]
	 * </pre>
	 * 
	 * @param matrix The matrix to multiply with.
	 * @return The product of this matrix with the given matrix.
	 * @throws IllegalArgumentException if the number of rows of the given matrix doesn't match the number of columns of
	 * this matrix.
	 * @see #solve(FiniteByteFieldMatrix)
	 */
	public FiniteByteFieldMatrix times(FiniteByteFieldMatrix matrix)
	{
		byte[][] cols = this.transpose().data;
		byte[][] thatCols = matrix.transpose().data;
		
		if(cols.length != matrix.data.length)
			throw new IllegalArgumentException("Matrix dimensions must match to be multiplied");
		
		return FiniteByteFieldMatrix.build(numRows(), matrix.numCols(), (r, c) -> dot(this.data[r], thatCols[c]));
	}
	
	/**
	 * Returns a new matrix where each element is divided by the given constant.
	 * 
	 * @param constant The constant to divide each value with.
	 * @return A new matrix, with each value divided by the given constant.
	 * @see #times(byte)
	 */
	public FiniteByteFieldMatrix divideBy(final byte constant)
	{
		return FiniteByteFieldMatrix.build(numRows(), numCols(), (r, c) -> div(data[r][c], constant));
	}
	
	/**
	 * Creates a new matrix which is the inverse of this matrix. A matrix is a true inverse if, when multiplied with the
	 * original matrix, produces an identity matrix. Only a square matrix has a true inverse.
	 * 
	 * @return The inverse of this matrix.
	 * @throws IllegalStateException if this matrix is not a square matrix.
	 */
	public FiniteByteFieldMatrix inverse()
	{
		if(!isSquare())
			throw new IllegalStateException("Only a square matrix has an inverse");
		if(inverse == null)
			inverse = transpose().cofactor().divideBy(determinant());
		return inverse;
	}
	
	/**
	 * "Divides" the given matrix with this matrix, such that if this matrix were multiplied with the result, the
	 * product would be the given matrix.<br/>
	 * <br/>
	 * In other words, this method provides a value for <code>x</code> which would satisfy the line:
	 * <code>product = this.times(x)</code>
	 * 
	 * @param product The value that would be the product of this matrix and the return value of this method.
	 * @return The value that would construct the given parameter when multiplied with this matrix.
	 * @see #times(FiniteByteFieldMatrix)
	 */
	public FiniteByteFieldMatrix solve(FiniteByteFieldMatrix product)
	{
		return this.inverse().times(product);
	}
	
	/**
	 * Constructs a new matrix beginning with the rows of this matrix, followed by the rows of the given matrix. The
	 * number columns of the given matrix must match the number of columns in this matrix.
	 * 
	 * @param matrix The matrix to append to the rows of this matrix.
	 * @return A new matrix, which consists of the rows of both matrices, in order.
	 * @throws NullPointerException if the given matrix is null.
	 * @throws IllegalArgumentException if the number of columns of the two matrices don't match.
	 */
	public FiniteByteFieldMatrix appendRows(FiniteByteFieldMatrix matrix)
	{
		if(this.numCols() != matrix.numCols())
			throw new IllegalArgumentException("To append another matrix, the number of columns must match");
		
		byte[][] result = new byte[this.numRows() + matrix.numRows()][this.numCols()];
		for(int row = 0; row < this.numRows(); row++)
			result[row] = copy(this.data[row]);
		for(int row = 0; row < matrix.numRows(); row++)
			result[this.numRows() + row] = copy(matrix.data[row]);
		
		return new FiniteByteFieldMatrix(result);
	}
	
	/**
	 * Returns a new matrix which contains rows of this matrix, in relative order, without the rows specified in the
	 * parameter.
	 * 
	 * @param rowsToRemove The rows of this matrix to not include in the returned matrix.
	 * @return A new sub-matrix of this matrix.
	 * @throws NullPointerException if the parameter is null, or contains any null values.
	 * @throws IndexOutOfBoundsException if any of the given indices are out of the bounds of this matrix.
	 * @throws IllegalStateException if the returned array would be completely empty.
	 * @see #withoutRows(Collection)
	 */
	public FiniteByteFieldMatrix withoutRows(Integer... rowsToRemove)
	{
		return withoutRows(Arrays.asList(rowsToRemove));
	}
	
	/**
	 * Returns a new matrix which contains rows of this matrix, in relative order, without the rows specified in the
	 * parameter.
	 * 
	 * @param rowsToRemove The rows of this matrix to not include in the returned matrix.
	 * @return A new sub-matrix of this matrix.
	 * @throws NullPointerException if the parameter is null, or contains any null values.
	 * @throws IndexOutOfBoundsException if any of the given indices are out of the bounds of this matrix.
	 * @throws IllegalStateException if the returned array would be completely empty.
	 * @see #withoutRows(int...)
	 */
	public FiniteByteFieldMatrix withoutRows(Collection<Integer> rowsToRemove)
	{
		if(rowsToRemove.size() == 0)
			return this;
		if(rowsToRemove.contains(null))
			throw new NullPointerException("Cannot remove a null index from the matrix");
		if(rowsToRemove.size() >= numRows())
			throw new IllegalStateException("Both dimensions of matrix must be non-zero");
		
		byte[][] dataWithoutRows = new byte[numRows() - rowsToRemove.size()][numCols()];
		int dataWithoutRowsIndex = 0;
		for(int row = 0; row < numRows(); row++)
			if(!rowsToRemove.contains(row))
				dataWithoutRows[dataWithoutRowsIndex++] = copy(data[row]);
		
		return new FiniteByteFieldMatrix(dataWithoutRows);
	}
	
	/**
	 * Returns the number of rows in this matrix.
	 * 
	 * @return The number of rows in this matrix.
	 */
	public int numRows()
	{
		return rows;
	}
	
	/**
	 * Returns the number of columns in this matrix.
	 * 
	 * @return The number of columns in this matrix.
	 */
	public int numCols()
	{
		return cols;
	}
	
	/**
	 * Returns whether or not this matrix has the same number of rows as columns.
	 * 
	 * @return True if the matrix is square, false otherwise.
	 */
	public boolean isSquare()
	{
		return numRows() == numCols();
	}
	
	public byte[][] getData()
	{
		return copy(data);
	}
	
	public byte[] getRow(int row)
	{
		return copy(data[row]);
	}
	
	public byte[] getCol(int col)
	{
		return transpose().getRow(col);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object o)
	{
		if(!(o instanceof FiniteByteFieldMatrix))
			return false;
		
		FiniteByteFieldMatrix that = (FiniteByteFieldMatrix) o;
		return Arrays.deepEquals(this.data, that.data);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode()
	{
		return Arrays.deepHashCode(this.data);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString()
	{
		StringBuilder string = new StringBuilder();
		for(int i = 0; i < this.data.length; i++)
			string.append(Arrays.toString(this.data[i])).append("\n");
		return string.toString();
	}
	
	/**
	 * Creates a copy of the given array.<br/>
	 * Precondition: the given array is not null or empty, and each column is the same size and not empty.<br/>
	 * Postcondition: the given array is unaltered, and the returned array is a completely unattached copy.
	 */
	private byte[][] copy(byte[][] data)
	{
		byte[][] copy = new byte[data.length][data[0].length];
		for(int row = 0; row < data.length; row++)
			for(int col = 0; col < data[0].length; col++)
				copy[row][col] = data[row][col];
		return copy;
	}
	
	private byte[] copy(byte[] data)
	{
		byte[] copy = new byte[data.length];
		System.arraycopy(data, 0, copy, 0, data.length);
		return copy;
	}
	
	/**
	 * Calculates the determinant.<br/>
	 * Precondition: matrix is square.
	 */
	private byte calculateDeterminant()
	{
		int size = numRows();
		if(size == 1)
			return data[0][0];
		
		byte sum = 0;
		byte[] firstRow = data[0];
		for(int col = 0; col < size; col++)
			sum = add(sum, mul(firstRow[col], minor(0, col).determinant()));
		return sum;
	}
}
