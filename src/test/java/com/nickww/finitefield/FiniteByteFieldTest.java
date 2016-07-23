package com.nickww.finitefield;

import static org.junit.Assert.*;
import static com.nickww.finitefield.FiniteByteField.*;

import org.junit.Test;

public class FiniteByteFieldTest
{
	private static final byte fst = 122;
	private static final byte snd = -122;
	
	@Test
	public void testMaxValue()
	{
		assertEquals((int)Math.pow(2, 8) - 1, MAX_VALUE);
	}
	
	@Test
	public void testDbl()
	{
		assertEquals(-12, dbl(fst));
		assertEquals(23, dbl(snd));
	}
	
	@Test
	public void testMul()
	{
		byte product = mul(fst, snd);
		assertEquals(78, product);
	}
	
	@Test
	public void testDiv()
	{
		byte result = div(fst, snd);
		assertEquals(-117, result);
	}
	
	@Test
	public void testMulAndDivAreInverseOperations()
	{
		byte product = mul(fst, snd);
		assertEquals(fst, div(product, snd));
		assertEquals(snd, div(product, fst));
		
		byte result = div(fst, snd);
		assertEquals(fst, mul(result, snd));
		result = div(snd, fst);
		assertEquals(snd, mul(result, fst));
	}
	
	@Test
	public void testSubIsSameAsAdd()
	{
		byte sum = add(fst, snd);
		byte dif = sub(fst, snd);
		assertEquals(sum, dif);
	}
	
	@Test
	public void testAddBytes()
	{
		byte sum = add(fst, snd);
		assertEquals(-4, sum);
	}
	
	@Test
	public void testAddArray()
	{
		byte[] bytes = new byte[] {fst, snd};
		byte sum = add(bytes);
		assertEquals(-4, sum);
	}
	
	@Test
	public void testPow()
	{
		byte fst = 2;
		byte snd = 3;
		byte result = pow(fst, snd);
		assertEquals(8, result);
	}
	
	@Test
	public void testPow0()
	{
		assertEquals((byte) 1, pow(fst, (byte) 0));
	}
	
	@Test
	public void testMul0()
	{
		assertEquals((byte) 0, mul(fst, (byte) 0));
		assertEquals((byte) 0, mul((byte) 0, snd));
	}
	
	@Test
	public void testDiv0()
	{
		assertEquals((byte) 0, div(fst, (byte) 0));
		assertEquals((byte) 0, div((byte) 0, snd));
	}
	
	@Test
	public void testDot()
	{
		byte[] v1 = new byte[] { 1, 2, 3 };
		byte[] v2 = new byte[] { 4, 5, 6 };
		assertEquals(mul(v1[0], v2[0]) ^ mul(v1[1], v2[1]) ^ mul(v1[2], v2[2]), dot(v1, v2));
	}
	
	@Test
	public void testDotEmpty()
	{
		byte[] v1 = new byte[] {};
		byte[] v2 = new byte[] {};
		assertEquals(0, dot(v1, v2));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDotUnevenLength()
	{
		dot(new byte[]{1}, new byte[]{1, 2});
	}
	
	@Test
	public void testSqrt()
	{
		byte fstSquare = mul(fst, fst);
		byte sndSquare = mul(snd, snd);
		assertEquals(fst, sqrt(fstSquare));
		assertEquals(snd, sqrt(sndSquare));
	}
	
	@Test
	public void testSqr()
	{
		assertEquals(mul(fst, fst), sqr(fst));
		assertEquals(mul(snd, snd), sqr(snd));
	}
}
