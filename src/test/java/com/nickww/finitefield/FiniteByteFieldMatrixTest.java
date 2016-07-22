package com.nickww.finitefield;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class FiniteByteFieldMatrixTest
{
	private static final FiniteByteFieldMatrix matrix = new FiniteByteFieldMatrix(
			new byte[][] { { 1, 2 }, { 3, 4 }, { -10, -11 }, { 124, -123 } });
	private static final FiniteByteFieldMatrix square = new FiniteByteFieldMatrix(
			new byte[][] { { 1, 2, 3 }, { 4, -10, -11 }, { 124, -123, 5 } });
	
	@Test(expected = NullPointerException.class)
	public void testConstructorNullArray()
	{
		new FiniteByteFieldMatrix(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorEmptyArray()
	{
		new FiniteByteFieldMatrix(new byte[][] {});
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorEmptyColumns()
	{
		new FiniteByteFieldMatrix(new byte[][] { {}, {}, {} });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorRowsDifferentSizes()
	{
		new FiniteByteFieldMatrix(new byte[][] { { 1, 2 }, { 1 } });
	}
	
	@Test
	public void testNumRows()
	{
		assertEquals(4, matrix.numRows());
		assertEquals(3, square.numRows());
	}
	
	@Test
	public void testNumCols()
	{
		assertEquals(2, matrix.numCols());
		assertEquals(3, square.numCols());
	}
	
	@Test
	public void testIsSquare()
	{
		assertFalse(matrix.isSquare());
		assertTrue(square.isSquare());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testDeterminantOfRectangularMatrix()
	{
		matrix.determinant();
	}
	
	@Test
	public void testDeterminantOfSquareMatrix()
	{
		assertEquals(-99, square.determinant());
	}
	
	@Test
	public void testTranspose()
	{
		byte[][] transposedData = { { 1, 3, -10, 124 }, { 2, 4, -11, -123 } };
		FiniteByteFieldMatrix transposedMatrix = new FiniteByteFieldMatrix(transposedData);
		assertEquals(transposedMatrix, matrix.transpose());
	}
	
	@Test
	public void testCofactorSquareMatrix()
	{
		FiniteByteFieldMatrix expectedCofactor = new FiniteByteFieldMatrix(
				new byte[][] { { 29, 77, -1 }, { -98, -127, 125 }, { -16, -7, -2 } });
		assertEquals(expectedCofactor, square.cofactor());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testCofactorRectangularMatrix()
	{
		matrix.cofactor();
	}
	
	@Test
	public void testTimes()
	{
		byte c = 20;
		byte[][] times = new byte[][] { { 20, 40 }, { 60, 80 }, { 12, 48 }, { 106, -86 } };
		FiniteByteFieldMatrix expectedResult = new FiniteByteFieldMatrix(times);
		assertEquals(expectedResult, matrix.times(c));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTimesImproperDimensions()
	{
		matrix.times(square);
	}
	
	@Test
	public void testMinor()
	{
		byte[][] minorData = new byte[][] { { 4, -10 }, { 124, -123 } };
		FiniteByteFieldMatrix expectedResult = new FiniteByteFieldMatrix(minorData);
		assertEquals(expectedResult, square.minor(0, 2));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testMinorOutOfBoundsRow()
	{
		square.minor(3, 2);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testMinorOutOfBoundsCol()
	{
		square.minor(0, 3);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testMinorNegativeRow()
	{
		square.minor(-1, 2);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testMinorNegativeCol()
	{
		square.minor(0, -1);
	}
	
	@Test
	public void testTimesMatrix()
	{
		FiniteByteFieldMatrix m = new FiniteByteFieldMatrix(new byte[][] { { 1, 2 }, { 3, 4 } });
		FiniteByteFieldMatrix expectedResult = new FiniteByteFieldMatrix(
				new byte[][] { { 7, 10 }, { 15, 22 }, { -14, 14 }, { -24, -38 } });
		assertEquals(expectedResult, matrix.times(m));
	}
	
	@Test
	public void testSolve()
	{
		FiniteByteFieldMatrix m = new FiniteByteFieldMatrix(new byte[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } });
		FiniteByteFieldMatrix result = new FiniteByteFieldMatrix(new byte[][] { { 8, 0 }, { 9, -11 }, { -7, -60 } });
		assertEquals(m, square.solve(result));
		assertEquals(result, square.times(m));
	}
	
	@Test
	public void testInverse()
	{
		assertEquals(FiniteByteFieldMatrix.identity(3), square.times(square.inverse()));
		assertEquals(FiniteByteFieldMatrix.identity(3), square.inverse().times(square));
	}
	
	@Test(expected = IllegalStateException.class)
	public void testInverseRectangularMatrix()
	{
		matrix.inverse();
	}
	
	@Test
	public void testHashCodeEquals()
	{
		EqualsVerifier.forClass(FiniteByteFieldMatrix.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
				.withPrefabValues(FiniteByteFieldMatrix.class, FiniteByteFieldMatrix.identity(1),
						FiniteByteFieldMatrix.identity(2))
				.verify();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBuildWithNegativeRowCount()
	{
		FiniteByteFieldMatrix.build(-1, 10, (i, j) -> (byte) 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBuildWithNegativeColCount()
	{
		FiniteByteFieldMatrix.build(10, -1, (i, j) -> (byte) 1);
	}
	
	@Test(expected = NullPointerException.class)
	public void testBuildWithNullFunction()
	{
		FiniteByteFieldMatrix.build(10, 10, null);
	}
	
	@Test
	public void testBuild()
	{
		FiniteByteFieldMatrix builtMatrix = FiniteByteFieldMatrix.build(3, 3, (i, j) -> (byte) (i * j));
		FiniteByteFieldMatrix expectedMatrix = new FiniteByteFieldMatrix(
				new byte[][] { { 0, 0, 0 }, { 0, 1, 2 }, { 0, 2, 4 } });
		assertEquals(builtMatrix, expectedMatrix);
	}
	
	@Test(expected = NullPointerException.class)
	public void testColumnVectorNullArray()
	{
		FiniteByteFieldMatrix.columnVector(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testColumnVectorEmptyArray()
	{
		FiniteByteFieldMatrix matrix = FiniteByteFieldMatrix.columnVector(new byte[0]);
		assertEquals(0, matrix.numRows());
		assertEquals(0, matrix.numCols());
	}
	
	@Test
	public void testColumnVector()
	{
		FiniteByteFieldMatrix matrix = FiniteByteFieldMatrix.columnVector(new byte[] { 10, 20, 30, -10 });
		assertEquals(4, matrix.numRows());
		assertEquals(1, matrix.numCols());
	}
	
	@Test(expected = NullPointerException.class)
	public void testRowVectorNullArray()
	{
		FiniteByteFieldMatrix.rowVector(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRowVectorEmptyArray()
	{
		FiniteByteFieldMatrix matrix = FiniteByteFieldMatrix.rowVector(new byte[0]);
		assertEquals(0, matrix.numRows());
		assertEquals(0, matrix.numCols());
	}
	
	@Test
	public void testRowVector()
	{
		FiniteByteFieldMatrix matrix = FiniteByteFieldMatrix.rowVector(new byte[] { 10, 20, 30, -10 });
		assertEquals(1, matrix.numRows());
		assertEquals(4, matrix.numCols());
	}
	
	@Test
	public void testGetData()
	{
		byte[][] originalData = new byte[][] { { 10, 20 }, { 30, 40 } };
		byte[][] data = new byte[][] { { 10, 20 }, { 30, 40 } };
		FiniteByteFieldMatrix matrix = new FiniteByteFieldMatrix(data);
		
		// check immutability:
		matrix.getData()[0][0] = 2;
		assertArrayEquals(originalData, data);
		assertArrayEquals(data, matrix.getData());
	}
	
	@Test
	public void testGetRow()
	{
		byte[][] originalData = new byte[][] { { 10, 20 }, { 30, 40 } };
		byte[][] data = new byte[][] { { 10, 20 }, { 30, 40 } };
		FiniteByteFieldMatrix matrix = new FiniteByteFieldMatrix(data);
		
		// check immutability:
		matrix.getRow(0)[0] = 2;
		assertArrayEquals(originalData[0], data[0]);
		assertArrayEquals(data[0], matrix.getRow(0));
	}
	
	@Test
	public void testGetCol()
	{
		byte[][] originalData = new byte[][] { { 10, 20 }, { 30, 40 } };
		byte[][] data = new byte[][] { { 10, 20 }, { 30, 40 } };
		FiniteByteFieldMatrix matrix = new FiniteByteFieldMatrix(data);
		
		// check immutability:
		matrix.getCol(0)[0] = 2;
		assertEquals(originalData[0][0], data[0][0]);
		assertEquals(originalData[1][0], data[1][0]);
		assertEquals(data[0][0], matrix.getCol(0)[0]);
		assertEquals(data[1][0], matrix.getCol(0)[1]);
	}
	
	@Test(expected = NullPointerException.class)
	public void testAppendRowsNullParameter()
	{
		matrix.appendRows(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAppendRowsColumnsDontMatch()
	{
		matrix.appendRows(square);
	}
	
	@Test
	public void testAppendRows()
	{
		byte[][] data = matrix.getData();
		byte[][] resultData = new byte[][] { data[0], data[1], data[2], data[3], data[0], data[1], data[2], data[3] };
		FiniteByteFieldMatrix result = matrix.appendRows(matrix);
		assertArrayEquals(resultData, result.getData());
	}
	
	@Test(expected = NullPointerException.class)
	public void testWithoutRowsCollectionNull()
	{
		matrix.withoutRows((Collection<Integer>) null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testWithoutRowsCollectionNullIndices()
	{
		matrix.withoutRows(Arrays.asList(1, 2, null));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testWithoutRowsCollectionNegativeIndex()
	{
		matrix.withoutRows(Arrays.asList(-1));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testWithoutRowsCollectionLargeIndex()
	{
		matrix.withoutRows(Arrays.asList(1000));
	}
	
	@Test(expected = IllegalStateException.class)
	public void testWithoutRowsCollectionRemoveAllIndices()
	{
		matrix.withoutRows(Arrays.asList(0, 1, 2, 3));
	}
	
	@Test
	public void testWithoutRowsCollectionEmptyCollection()
	{
		FiniteByteFieldMatrix result = matrix.withoutRows(Arrays.asList());
		assertEquals(matrix, result);
	}
	
	@Test
	public void testWithoutRowsCollection()
	{
		FiniteByteFieldMatrix result = matrix.withoutRows(Arrays.asList(1, 3));
		byte[][] data = matrix.getData();
		byte[][] resultData = new byte[][] { data[0], data[2] };
		assertArrayEquals(resultData, result.getData());
	}
	
	@Test(expected = NullPointerException.class)
	public void testWithoutRowsArrayNull()
	{
		matrix.withoutRows((Integer[]) null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testWithoutRowsArrayNullIndices()
	{
		matrix.withoutRows(new Integer[] { 0, 1, null });
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testWithoutRowsArrayNegativeIndex()
	{
		matrix.withoutRows(-1);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testWithoutRowsArrayLargeIndex()
	{
		matrix.withoutRows(1000);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testWithoutRowsArrayRemoveAllIndices()
	{
		matrix.withoutRows(0, 1, 2, 3);
	}
	
	@Test
	public void testWithoutRowsArray()
	{
		FiniteByteFieldMatrix result = matrix.withoutRows(1, 3);
		byte[][] data = matrix.getData();
		byte[][] resultData = new byte[][] { data[0], data[2] };
		assertArrayEquals(resultData, result.getData());
	}
	
	@Test
	public void testWithoutRowsArrayEmptyArray()
	{
		FiniteByteFieldMatrix result = matrix.withoutRows(new Integer[0]);
		assertEquals(matrix, result);
	}
}
