package language.visitors.typechecking;

import static java.util.Objects.requireNonNull;

public record DictType(Type valueTy) implements Type {
	public static final String TYPE_NAME = "DICT";

	public DictType {
		requireNonNull(valueTy);
	}

	@Override
	public String toString() {
		return String.format("%s DICT", valueTy);
	}
}
