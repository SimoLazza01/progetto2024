package language.parser.ast;

import static java.util.Objects.requireNonNull;

public abstract class TernaryOp implements Exp {
	protected final Exp left;
	protected final Exp middle;
	protected final Exp right;

	protected TernaryOp(Exp left, Exp middle, Exp right) {
		this.left = requireNonNull(left);
		this.middle = requireNonNull(middle);
		this.right = requireNonNull(right);
	}

	@Override
	public String toString() {
		return String.format("%s(%s,%s,%s)", getClass().getSimpleName(), left, middle, right);
	}

}
