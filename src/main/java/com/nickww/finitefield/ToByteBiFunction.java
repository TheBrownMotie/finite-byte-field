package com.nickww.finitefield;

@FunctionalInterface
public interface ToByteBiFunction<T, U>
{
	byte applyAsByte(T t, U u);
}
