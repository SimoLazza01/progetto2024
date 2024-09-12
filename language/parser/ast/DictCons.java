package language.parser.ast;

import language.visitors.Visitor;

public class DictCons extends BinaryOp {
	public DictCons(Exp left, Exp right) {
		super(left, right);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitDictCons(left, right);
	}
}
