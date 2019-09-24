package liquer.alchemy.athanor;

public interface ISource {
	String getName();
	Object getValue(Object instance);
}
