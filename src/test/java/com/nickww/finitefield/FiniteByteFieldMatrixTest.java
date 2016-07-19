package com.nickww.finitefield;

import static org.junit.Assert.*;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class FiniteByteFieldMatrixTest
{
	private static final FiniteByteFieldMatrix matrix = new FiniteByteFieldMatrix(new byte[][] {{1, 2}, {3, 4}, {-10, -11}, {124, -123}});
	private static final FiniteByteFieldMatrix square = new FiniteByteFieldMatrix(new byte[][] {{1, 2, 3}, {4, -10, -11}, {124, -123, 5}});
	
	@Test(expected=NullPointerException.class)
	public void testConstructorNullArray()
	{
		new FiniteByteFieldMatrix(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorEmptyArray()
	{
		new FiniteByteFieldMatrix(new byte[][] {});
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorEmptyColumns()
	{
		new FiniteByteFieldMatrix(new byte[][] {{}, {}, {}});
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorRowsDifferentSizes()
	{
		new FiniteByteFieldMatrix(new byte[][] {{1, 2}, {1}});
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
	
	@Test(expected=IllegalStateException.class)
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
		byte[][] transposedData = {{1, 3, -10, 124}, {2, 4, -11, -123}};
		FiniteByteFieldMatrix transposedMatrix = new FiniteByteFieldMatrix(transposedData);
		assertEquals(transposedMatrix, matrix.transpose());
	}
	
	@Test
	public void testCofactorSquareMatrix()
	{
		FiniteByteFieldMatrix expectedCofactor = new FiniteByteFieldMatrix(new byte[][]{{29, 77, -1}, {-98, -127, 125}, {-16, -7, -2}});
		assertEquals(expectedCofactor, square.cofactor());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testCofactorRectangularMatrix()
	{
		matrix.cofactor();
	}
	
	@Test
	public void testTimes()
	{
		byte c = 20;
		byte[][] times = new byte[][] {{20, 40}, {60, 80}, {12, 48}, {106, -86}};
		FiniteByteFieldMatrix expectedResult = new FiniteByteFieldMatrix(times);
		assertEquals(expectedResult, matrix.times(c));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testTimesImproperDimensions()
	{
		matrix.times(square);
	}
	
	@Test
	public void testMinor()
	{
		byte[][] minorData = new byte[][] {{4, -10}, {124, -123}};
		FiniteByteFieldMatrix expectedResult = new FiniteByteFieldMatrix(minorData);
		assertEquals(expectedResult, square.minor(0, 2));
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testMinorOutOfBoundsRow()
	{
		square.minor(3, 2);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testMinorOutOfBoundsCol()
	{
		square.minor(0, 3);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testMinorNegativeRow()
	{
		square.minor(-1, 2);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testMinorNegativeCol()
	{
		square.minor(0, -1);
	}
	
	@Test
	public void testTimesMatrix()
	{
		FiniteByteFieldMatrix m = new FiniteByteFieldMatrix(new byte[][] {{1, 2}, {3, 4}});
		FiniteByteFieldMatrix expectedResult = new FiniteByteFieldMatrix(new byte[][] {{7, 10}, {15, 22}, {-14, 14}, {-24, -38}});
		assertEquals(expectedResult, matrix.times(m));
	}
	
	@Test
	public void testSolve()
	{
		FiniteByteFieldMatrix m = new FiniteByteFieldMatrix(new byte[][] {{1, 2}, {3, 4}, {5, 6}});
		FiniteByteFieldMatrix result = new FiniteByteFieldMatrix(new byte[][] {{8, 0}, {9, -11}, {-7, -60}});
		assertEquals(m, square.solve(result));
		assertEquals(result, square.times(m));
	}
	
	@Test
	public void testInverse()
	{
		assertEquals(FiniteByteFieldMatrix.identity(3), square.times(square.inverse()));
		assertEquals(FiniteByteFieldMatrix.identity(3), square.inverse().times(square));
	}
	
	@Test
	public void testHashCodeEquals()
	{
		EqualsVerifier.forClass(FiniteByteFieldMatrix.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBuildWithNegativeRowCount()
	{
		FiniteByteFieldMatrix.build(-1, 10, (i, j) -> (byte)1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBuildWithNegativeColCount()
	{
		FiniteByteFieldMatrix.build(10, -1, (i, j) -> (byte)1);
	}
	
	@Test(expected=NullPointerException.class)
	public void testBuildWithNullFunction()
	{
		FiniteByteFieldMatrix.build(10, 10, null);
	}
	
	@Test
	public void testBuild()
	{
		FiniteByteFieldMatrix builtMatrix = FiniteByteFieldMatrix.build(3, 3, (i, j) -> (byte)(i * j));
		FiniteByteFieldMatrix expectedMatrix = new FiniteByteFieldMatrix(new byte[][] {{0, 0, 0}, {0, 1, 2}, {0, 2, 4}});
		assertEquals(builtMatrix, expectedMatrix);
	}
}
