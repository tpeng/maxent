import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tpeng  <pengtaoo@gmail.com>
 * Date: 7/5/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Instance {

    public int label;
    public Feature feature;

    public Instance(int label, int[] xs) {
        this.label = label;
        this.feature = new Feature(xs);
    }

    public int getLabel() {
        return label;
    }

    public Feature getFeature() {
        return feature;
    }

    @Override
    public String toString() {
        return "Instance{" +
                "label=" + label +
                ", feature=" + feature +
                '}';
    }
}
