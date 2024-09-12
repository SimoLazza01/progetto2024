package language.parser.ast;

import static java.util.Objects.requireNonNull;

import language.visitors.Visitor;

public class ForStmt implements Stmt {
	private final Variable var; // non-optional field
	private final Exp dict; // non-optional field
	private final Block block; // optional field

	public ForStmt(Variable var, Exp dict, Block block) {
		this.var = requireNonNull(var);
		this.dict = requireNonNull(dict);
		this.block = requireNonNull(block);
	}
  
	@Override
	public String toString() {
		return String.format("%s(%s,%s,%s)", getClass().getSimpleName(), var, dict, block);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitForStmt(var, dict, block);
	}
}
