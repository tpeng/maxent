import com.google.common.collect.*;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tpeng
 * Date: 7/5/12
 * Time: 11:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class MaxEnt {

    private final int ITERATIONS = 1000;

    private static final double EPSILON = 0.001;

    // the labels and features in insertion order
    // so index of label/feature can match
    private List<String> labels;
    private List<String> features;

    // the number of the sample instances
    private int N;

    // the number of features
    private int n;

    // the number of labels
    private int m;

    // the number of feature functions.
    private int featureCount;

    // the empirical p(x)
    private double empirical_x[];

    // the empirical p[x][y]
    private double empirical_x_y[][];

    // the empirical expectation of fi
    private double empirical_expects[];

    // the f[x][y]: whether x and y has shown together
    private int f[][];

    // the #f[x][y] the count of x and y has shown together
    private int f_sharp[][];

    // the weight to learn. size is featureCount
    private double w[];

    // the delta to calculate in every iteration.
    // w[i] = w[i] + delta[i]
    // size is featureCount
    private double delta[];

    private List<Instance> instances;

    public static void main(String... args) throws FileNotFoundException {
        List<Instance> instances = DataSet.readDataSet("examples/zoo.train");
        MaxEnt me = new MaxEnt(instances);
        me.train();
    }

    public MaxEnt(List<Instance> instances) {

        this.instances = instances;

        // label:count
        Multiset<String> labelSet = LinkedHashMultiset.create();
        // feature: count
        Multiset<String> featureSet = LinkedHashMultiset.create();
        // label: feature count
        // Google guava doesn't provide a Multimap use Multiset as value?
        Map<String, Multiset<String>> labelFeatures = Maps.newHashMap();

        Multimap<String, List<String>> labelFeatSet = HashMultimap.create();

        for (Instance instance : instances) {
            labelSet.add(instance.getLabel());
            featureSet.addAll(instance.getFeatures());
            labelFeatSet.put(instance.getLabel(), instance.getFeatures());
        }

        for (String label : labelFeatSet.keySet()) {
            Collection<List<String>> features = labelFeatSet.get(label);
            Multiset<String> s = LinkedHashMultiset.create();
            for (List<String> f : features) {
                s.addAll(f);
            }
            labelFeatures.put(label, s);
        }

        labels = new ArrayList<String>(labelSet.elementSet());
        features = new ArrayList<String>(featureSet.elementSet());

        N = instances.size();
        n = features.size();
        m = labels.size();

        empirical_x = new double[n];
        // the count of feature has shown divided by N
        for (int i = 0; i < empirical_x.length; i++) {
            String x = features.get(i);
            empirical_x[i] = 1.0 * featureSet.count(x) / N;
        }

        empirical_x_y = new double[n][m];
        f = new int[n][m];
        f_sharp = new int[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                String x = features.get(i);
                String y = labels.get(j);
                Multiset<String> features = labelFeatures.get(y);
                int count = features.count(x);
                empirical_x_y[i][j] = 1.0 * count / N;
                f_sharp[i][j] = count;
                f[i][j] = count > 0 ? 1 : 0;
            }
        }

        // TODO: select the best features with information gain
        featureCount = n * m;
        w = new double[featureCount];
        delta = new double[featureCount];

        empirical_expects = calc_empirical_expects();
    }

    /**
     * Calculates the p(y|x)
     */
    private double[][] calc_prob_y_given_x() {
        double cond[][] = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                cond[i][j] = calc_prob_y_given_x(i, j);
            }
        }
        return cond;
    }

    // calculates the p(y|x)
    private double calc_prob_y_given_x(int x_index, int y_index) {
        double z_all = 0;
        double z = 0;
        double sum = 0;

        for (int j = 0; j < m; j++) {
            z_all = 0;
            for (int i = 0; i < featureCount; i++) {
                double zj = w[i] * eval_feature_fn(i, x_index, y_index);
                if (j == y_index) {
                    z += zj;
                }
                z_all += zj;
            }
            sum += Math.exp(z_all);
        }
        return Math.exp(z) / sum;
    }

    // Evaluates the feature function
    private int eval_feature_fn(int i, int x, int y) {
        // FIXME: simply if x and y co-occurrence
        if (i / n == y && i % n == x)
            return f[x][y];
        return 0;
    }

    public void train() {

        for (int k = 0; k < ITERATIONS; k++) {
            for (int i = 0; i < featureCount; i++) {
                double delta = iis_solve_delta(empirical_expects[i], i);
                w[i] += delta;
            }
            System.out.println("ITERATIONS: " + k + " " + Arrays.toString(w));
        }
    }

    private double[] calc_empirical_expects() {

        double[] expects = new double[featureCount];

        for (Instance instance : instances) {
            String y = instance.getLabel();
            int y_index = labels.indexOf(y);
            for (String x : instance.getFeatures()) {
                int x_index = features.indexOf(x);
                for (int i = 0; i < featureCount; i++) {
                    double fi = eval_feature_fn(i, x_index, y_index);
                    expects[i] += empirical_x_y[x_index][y_index] * fi;
                }
            }
        }
        return expects;
    }

    private double iis_solve_delta(double empirical, int fn_index) {

        double delta = 1.0;
        double f_newton = 0.0;
        double df_newton = 0.0;
        double p_yx[][] = calc_prob_y_given_x();

        int iters = 0;

        while (iters < 50) {
            f_newton = df_newton = 0.0;
            for (Instance instance : instances) {
                String y = instance.getLabel();
                int y_index = labels.indexOf(y);
                for (String x : instance.getFeatures()) {
                    int x_index = features.indexOf(x);
                    double fi = eval_feature_fn(fn_index, x_index, y_index);
                    double prod = empirical_x[x_index] * p_yx[x_index][y_index] * fi *
                            Math.exp(delta * f_sharp[x_index][y_index]);
                    f_newton += prod;
                    df_newton += prod * f_sharp[x_index][y_index];
                }
            }
            f_newton = empirical - f_newton;

            if (Math.abs(f_newton) < 0.000001) {
                // f_newton might = 0
                return delta;
            }

            df_newton = -df_newton;
            double ratio = f_newton / df_newton;

            delta -= ratio;

            if (Math.abs(ratio) < EPSILON) {
                return delta;
            }
            iters++;
        }
        if (iters == 50) {
            throw new RuntimeException("not converged");
        }
        return delta;
    }
}
