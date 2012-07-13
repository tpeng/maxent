import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tpeng
 * Date: 7/5/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Instance {

    public String label;
    public List<String> features;

    public Instance(String label, List<String> features) {
        this.label = label;
        this.features = features;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getFeatures() {
        return features;
    }

    @Override
    public String toString() {
        return "Instance{" +
                "label='" + label + '\'' +
                ", features=" + features +
                '}';
    }
}
