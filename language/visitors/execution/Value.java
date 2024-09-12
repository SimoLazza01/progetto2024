package language.visitors.execution;

import java.util.TreeMap;

public interface Value {
	/* default conversion methods */
	default int toInt() {
		throw new InterpreterException("Expecting an integer");
	}

	default boolean toBool() {
		throw new InterpreterException("Expecting a boolean");
	}

	default PairValue toPair() {
		throw new InterpreterException("Expecting a pair");
	}

	default TreeMap<Integer,Value> toDict() {
		throw new InterpreterException("Expecting a dictionary");
	}
}
