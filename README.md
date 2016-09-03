# finite-byte-field
Fast and complete finite field arithmetic over Galois Field 256, with matrices and an implementation of RAID6 checksums 

[![Build Status](https://travis-ci.org/TheBrownMotie/finite-byte-field.svg?branch=master)](https://travis-ci.org/TheBrownMotie/finite-byte-field) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/48b6f98d6b204f3abf25c4b5b653dce2)](https://www.codacy.com/app/nwuensch/finite-byte-field?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=TheBrownMotie/finite-byte-field&amp;utm_campaign=Badge_Grade) [![codecov](https://codecov.io/gh/TheBrownMotie/finite-byte-field/branch/master/graph/badge.svg)](https://codecov.io/gh/TheBrownMotie/finite-byte-field)

## Basic arithmetic

```Java
import static com.nickww.finitefield.*;

// This library treats all Java bytes as if they were "unsigned"
// Positive numbers are the same, and negative numbers are 128..255, with -1 being 255.

// One way to quickly determine the "unsigned byte" value is by applying "& 0xff"
// Example: "unsigned value" in parentheses

byte a = 112; // 112 (112)
byte b = -1;  // -1  (255)
byte c = -56; // -56 (200)

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
FiniteByteField.div(a, b); // 55

// Exponentiation:
pow(a, c); // 37
sqr(a);    // -36 (220) - same as raising to the power of two, but much faster
sqrt(a);   // -55 (201) - inverse of square

// Other: 
dot(new byte[] {a, c}, new byte[] {b, c}); // 117 - calculates dot product, same as add(mul(a, b), mul(c, c))
```