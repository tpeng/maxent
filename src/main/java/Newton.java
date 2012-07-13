/**
 * Created with IntelliJ IDEA.
 * User: tpeng
 * Date: 6/30/12
 * Time: 9:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Newton {

    private static final double EPSILON = 0.00001;

    /** x^6 - x - 1 */
    public static double f(double x) {
        return Math.pow(x, 6) - x - 1;
    }

    /** the derivation of function: 6*x^5 - 1 */
    public static double deriv_f(double x) {
        return 6 * Math.pow(x, 5) - 1;
    }

    public static void main(String... args) {
        double x0 = 1.5;    // first guess of the root

        while (Math.abs(f(x0) / deriv_f(x0)) > EPSILON) {
            System.out.println(x0);
            x0 = x0 - f(x0) / deriv_f(x0);
        }
        System.out.println("x0 = " + f(x0));
    }
}
