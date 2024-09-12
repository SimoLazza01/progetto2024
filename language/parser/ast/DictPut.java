package language.parser.ast;

import language.visitors.Visitor;

public class DictPut extends TernaryOp {
	public DictPut(Exp left, Exp middle, Exp right) {
		super(left, middle, right);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitDictPut(left, middle, right);
	}
}
