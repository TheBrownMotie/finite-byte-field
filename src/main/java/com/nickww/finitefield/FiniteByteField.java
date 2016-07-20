package com.nickww.finitefield;

/**
 * A class to perform finite field arithmetic for Galois fields of order 2<sup>8</sup>. The implementation of this class
 * works by treating Java's byte as an unsigned byte, where the positive numbers are 0-127 and the negative numbers are
 * 128-255. Credit to {@link http://www.cs.utsa.edu/~wagner/laws/FFM.html} for providing the logic and pseudocode of
 * this class.
 * 
 * @author Nick Wuensch
 */
public class FiniteByteField
{
	public static final int MAX_VALUE = 255;
	
	private static final byte[] exp = new byte[MAX_VALUE + 1];
	private static final byte[] log = new byte[MAX_VALUE + 1];
	private static final byte[] sqr = new byte[MAX_VALUE + 1];
	private static final byte[] sqrt = new byte[MAX_VALUE + 1];
	
	static
	{
		byte x = (byte) 0x01;
		exp[0] = x;
		
		final byte generator = 0x03;
		for(int i = 1; i <= MAX_VALUE; i++)
		{
			byte y = slowMul(x, generator);
			exp[i] = y;
			x = y;
		}
		
		for(int i = 0; i <= MAX_VALUE; i++)
			log[exp[i] & 0xff] = (byte) i;
		
		// #mul should now work
		for(int i = 0; i <= MAX_VALUE; i++)
			sqr[i] = mul((byte)(i & 0xff), (byte)(i & 0xff));
		for(int i = 0; i <= MAX_VALUE; i++)
			sqrt[sqr[i] & 0xff] = (byte) i;
	}
	
	private FiniteByteField() { /* Prevent instantiation */ }
	
	/**
	 * Returns the given number raised to the power of 2.
	 * 
	 * @see #pow(byte, byte)
	 * @param a The base
	 * @return The GF(2<sup>8</sup>) square of the given number;
	 */
	public static byte sqr(byte a)
	{
		return sqr[a & 0xff];
	}
	
	/**
	 * Returns the square root of the given number. This is calculated as the inverse of {@link #sqr(byte)}, but could also
	 * have been calculated as:<pre>
	 * <code>
	 * In GF(2<sup>m</sup>),
	 * a = a<sup>2<sup>m</sup></sup>
	 * Therefore,
	 * a<sup>1/2</sup> = a<sup>2<sup>m</sup>/2</sup>
	 * a<sup>1/2</sup> = a<sup>2<sup>m-1</sup></sup>
	 * In GF(2<sup>8</sup>),
	 * sqrt(a) = a<sup>128
	 * </code></pre>
	 * 
	 * @param a The base
	 * @return The GF(2<sup>8</sup>) square root of the given number;
	 */
	public static byte sqrt(byte a)
	{
		return sqrt[a & 0xff];
	}
	
	/**
	 * This is a version of {@link #mul(byte, byte)} optimized for multiplying by two.
	 * @param a The byte value to double.
	 * @return The given byte valued, multiplied by two.
	 */
	public static byte dbl(byte a)
	{
		return (byte) ((a << 1) ^ ((a & 0x80) != 0 ? 0x1b : 0));
	}
	
	/**
	 * This multiplies the two bytes, as if they were unsigned, using GF(2<sup>8</sup>) logarithms and inverse
	 * logarithms: <code>
	 * product = ilog( log(multiplier) + log(multiplicand) )
	 * </code>
	 * 
	 * @param a The multiplier
	 * @param b The multiplicand
	 * @return The GF(2<sup>8</sup>) product
	 */
	public static byte mul(byte a, byte b)
	{
		if(a == 0 || b == 0)
			return 0;
		int t = (log[a & 0xff] & 0xff) + (log[b & 0xff] & 0xff);
		if(t > 255)
			t -= 255;
		return exp[t & 0xff];
	}
	
	/**
	 * This multiplies the two bytes, as if they were unsigned, using GF(2<sup>8</sup>) logarithms and inverse
	 * logarithms: <code>
	 * product = ilog( log(dividend) - log(divisor) )
	 * </code>
	 * 
	 * @param a The dividend
	 * @param b The divisor
	 * @return The GF(2<sup>8</sup>) quotient
	 */
	public static byte div(byte a, byte b)
	{
		if(a == 0 || b == 0)
			return 0;
		int t = (log[a & 0xff] & 0xff) - (log[b & 0xff] & 0xff);
		if(t < 0)
			t += 255;
		return exp[t & 0xff];
	}
	
	/**
	 * This adds the given bytes, which in GF(2<sup>8</sup>) is simply the same as applying XOR.
	 * 
	 * @param a The augend
	 * @param b The addend
	 * @return The GF(2<sup>8</sup>) sum
	 */
	public static byte add(byte a, byte... bytes)
	{
		byte sum = a;
		for(byte bb : bytes)
			sum ^= bb;
		return sum;
	}
	
	/**
	 * This adds the given bytes, which in GF(2<sup>8</sup>) is simply the same as applying XOR.
	 * 
	 * @param bytes The bytes to add
	 * @return The GF(2<sup>8</sup>) sum
	 */
	public static byte add(byte[] bytes)
	{
		byte sum = 0;
		for(byte bb : bytes)
			sum ^= bb;
		return sum;
	}
	
	/**
	 * This subtracts two bytes, which in GF(2<sup>8</sup>) is simply the same as applying XOR. Note that this is the
	 * same as addition.
	 * 
	 * @param a The minuend
	 * @param b The subtrahend
	 * @return The GF(2<sup>8</sup>) difference
	 */
	public static byte sub(byte a, byte... bytes)
	{
		return add(a, bytes);
	}
	
	/**
	 * This repeatedly multiplies the two bytes to arrive at the power.
	 * 
	 * @param i The base
	 * @param e The exponent
	 * @return The GF(2<sup>8</sup>) power
	 */
	public static byte pow(byte i, byte e)
	{
		if(e == 0)
			return 1;
		byte product = i;
		for(int count = 1; count < e; count++)
			product = mul(product, i);
		return (byte) product;
	}
	
	/**
	 * Calculates the dot product of the two given byte arrays.
	 * 
	 * @param vector1 The first byte array
	 * @param vector2 The second byte array
	 * @return The dot product, arrived at with addition and multiplication in GF(2<sup>8</sup>)
	 * @throws IllegalArgumentExecption if the vectors are not the same length
	 */
	public static byte dot(byte[] vector1, byte[] vector2)
	{
		if(vector1.length != vector2.length)
			throw new IllegalArgumentException("Byte vector lengths must be equal");
		
		int length = vector1.length;
		byte product = 0;
		for(int i = 0; i < length; i++)
			product = add(product, mul(vector1[i], vector2[i]));
		return product;
	}
	
	/**
	 * This multiplies the two bytes, as if they were unsigned, using the Russian peasant technique (modified to work
	 * with the Galois field)
	 * {@link https://en.wikipedia.org/wiki/Ancient_Egyptian_multiplication#Russian_peasant_multiplication}.
	 * 
	 * @param a The multiplier
	 * @param b The multiplicand
	 * @return The GF(2<sup>8</sup>) product
	 */
	private static byte slowMul(byte a, byte b)
	{
		byte r = 0;
		byte t;
		while(a != 0)
		{
			if((a & 1) != 0) // if a is odd
				r = (byte) (r ^ b); // add value of b to the result
			t = (byte) (b & 0x80);
			b = (byte) (b << 1); // double b
			if(t != 0)
				b = (byte) (b ^ 0x1b); // add polynomial representation to b
			a = (byte) ((a & 0xff) >> 1); // divide a in half ( "& 0xff >>" equivalent to ">>>" for unsigned byte)
		}
		return r;
	}
}
