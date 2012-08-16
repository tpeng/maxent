import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: tpeng  <pengtaoo@gmail.com>
 * Date: 8/13/12
 * Time: 10:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class Feature {

    private int[] values;

    public Feature(int[] xs) {
        values = new int[xs.length];
        System.arraycopy(xs, 0, values, 0, values.length);
    }

    public int[] getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feature feature = (Feature) o;

        if (!Arrays.equals(values, feature.values)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return values != null ? Arrays.hashCode(values) : 0;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }
}
