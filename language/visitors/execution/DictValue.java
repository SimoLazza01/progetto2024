package language.visitors.execution;

import java.util.TreeMap;

public class DictValue extends AtomicValue<TreeMap<Integer,Value>> {

	public DictValue() {
		super(new TreeMap<Integer,Value>());
	}

  public DictValue(TreeMap<Integer,Value> map) {
		super(map);
	}

  public DictValue getClone() {
    return new DictValue(new TreeMap<Integer,Value>(value));
  }

	@Override
	public TreeMap<Integer,Value> toDict() {
		return value;
	}

  @Override
	public String toString() {
    String s = "[";
		Integer[] keys = toDict().keySet().toArray(new Integer[0]);
    for (int i = 0; i < keys.length; i++) {
      s += keys[i] + ":" + toDict().get(keys[i]);
      if (i != keys.length-1) {
        s+=",";
      }
    }
    s += "]";
    return s;
	}
}
// package language.visitors.execution;

// import static java.util.Objects.requireNonNull;

// public abstract class DictValue<T> implements Value {
// 	protected TreeMap<Integer,T> dictionary = new TreeMap<>();

// 	@Override
// 	public final boolean equals(Object obj) {
// 		if (this == obj)
// 			return true;
// 		if (obj instanceof AtomicValue<?> sv)
// 			return value.equals(sv.value);
// 		return false;
// 	}
	
// 	@Override
// 	public int hashCode() {
// 		return dictionary.hashCode();
// 	}

// 	@Override
// 	public String toString() {
// 		return value.toString();
// 	}
// }

