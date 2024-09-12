package language.parser.ast;

import language.visitors.Visitor;

public class DictDel extends BinaryOp {
	public DictDel(Exp left, Exp right) {
		super(left, right);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitDictDel(left, right);
	}
}
