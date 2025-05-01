package creeperpookie.itemhelper.util;

import java.util.Objects;

public class Pair <T, U>
{
	private T left;
	private U right;

	public Pair(T left, U right)
	{
		this.left = left;
		this.right = right;
	}

	public T getLeft()
	{
		return left;
	}

	public void setLeft(T left)
	{
		this.left = left;
	}

	public U getRight()
	{
		return right;
	}

	public void setRight(U right)
	{
		this.right = right;
	}

	public boolean equalsLeft(T left)
	{
		return this.left.equals(left);
	}

	public boolean equalsRight(U right)
	{
		return this.right.equals(right);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(left, right);
	}

	@Override
	public String toString()
	{
		return "Pair{" + "left=" + left + ", right=" + right + '}';
	}
}
