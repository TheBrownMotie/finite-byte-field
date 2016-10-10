# finite-byte-field
Fast and complete finite field arithmetic over Galois Field 256, with matrices and an implementation of RAID6 checksums 

[![Build Status](https://travis-ci.org/TheBrownMotie/finite-byte-field.svg?branch=master)](https://travis-ci.org/TheBrownMotie/finite-byte-field) [![codecov](https://codecov.io/gh/TheBrownMotie/finite-byte-field/branch/master/graph/badge.svg)](https://codecov.io/gh/TheBrownMotie/finite-byte-field)

## What is Finite Field arithmetic?

Unlike regular arithmetic, which assumes an infinite pool of numbers, finite field arithmetic assumes only a limited pool of numbers to use. This is convenient for computers, because bytes/shorts/ints/longs can only represent a limited number of values. By default, using standard arithmetic that results in a number too large for an "int" will simply "wrap-around" to the bottom value, which usually yields the wrong result, and has to be checked for. In finite field arithmetic, no checks are ever required, and functions which rely on standard arithmetic can be replaced with finite field arithmetic without issue.

To achieve this goal, math doesn't work quite the same way as with standard integer arithmetic, and it can be unintuitive.

For example, in the 256 finite field:
- Addition and subtraction are the same function (XOR)
- Multiplication is not the same as repeated addition
- Exponentiation IS the same as repeated multiplication

Finite field arithmetic is used in the real world to calculate cryptographic hashes and RAID checksums. This library includes an implementation of the RAID6 checksum algorithm.

## Unsigned bytes

Java does not support unsigned bytes, but I do. This library will treat your bytes as though they were "unsigned".

Simply put:
- The numbers 0..127 are themselves.
- The numbers 128..255 are the negative values, with -1 being 255.
- You can figure this out by applying: & 0xff

Examples:
```Java
byte a = 112; // 112
byte b = -1;  // 255
byte c = -56; // 200
```

## Basic arithmetic

```Java
import static com.nickww.finitefield.FiniteByteField.*;

// Addition is the same as XOR:
add(a, b, c); // 71
add(new byte[] {a, b, c});

// Subtraction is the same as addition:
sub(a, b, c); // 71

// Multiplication:
mul(a, b);    // 4
mul(a, b, c); // 13
dbl(a);       // -32 (224) - same as multiplying by two, but much faster

// Division:
div(a, b); // 55

// Exponentiation:
pow(a, c); // 37
sqr(a);    // -36 (220) - same as raising to the power of two, but much faster
sqrt(a);   // -55 (201) - inverse of square

// Other: 
dot(new byte[] {a, c}, new byte[] {b, c}); // 117 - calculates dot product, same as add(mul(a, b), mul(c, c))
```

## Matrices

Until I fill this in, feel free to read the JavaDoc in the code.

## Checksums

Until I fill this in, feel free to read the JavaDoc in the code.

## Further reading

This library implements the algorithms described in these papers:

- https://www.kernel.org/pub/linux/kernel/people/hpa/raid6.pdf
- http://web.eecs.utk.edu/~plank/plank/papers/CS-96-332.pdf
