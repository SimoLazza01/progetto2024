package language.parser.ast;

import language.visitors.Visitor;

public class DictGet extends BinaryOp {
	public DictGet(Exp left, Exp right) {
		super(left, right);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitDictGet(left, right);
	}
}
