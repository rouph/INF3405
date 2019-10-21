public class Changeable<T> {
    T value;

    public Changeable(T value) {
        this.value = value;
    }

    public String toString() {
        return value.toString();
    }

    public boolean equals(Object other) {
        if (other instanceof Changeable) {
            return value.equals(((Changeable<?>)other).value);
        } else {
            return value.equals(other);
        }
    }

    public int hashCode() {
        return value.hashCode();
    }
}