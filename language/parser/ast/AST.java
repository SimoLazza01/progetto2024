package language.parser.ast;

import language.visitors.Visitor;

public interface AST {
	<T> T accept(Visitor<T> visitor);
}
