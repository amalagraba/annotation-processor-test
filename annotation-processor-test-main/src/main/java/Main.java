import model.TestBean;
import populator.LambdaPositionPopulator;
import populator.ManualLambdaPositionPopulator;
import populator.ManualPositionPopulator;
import populator.PositionPopulator;
import populator.PositionPopulatorImpl;
import populator.ReflectionPositionPopulator;

public class Main {

    private static final PositionPopulator manual = new ManualPositionPopulator();
    private static final PositionPopulator generated = new PositionPopulatorImpl();
    private static final PositionPopulator lambda = new LambdaPositionPopulator();
    private static final PositionPopulator manualLambda = new ManualLambdaPositionPopulator();
    private static final PositionPopulator reflection = new ReflectionPositionPopulator();

    private static final int elements = 10000000;

    public static void main(String[] args) {
        runTests("Initializing...", false);
        runTests("Starting tests...", true);
        runTests("Rerunning tests...", true);
    }

    private static void runTests(String title, boolean report) {
        System.out.println(title);
        testPopulator(manual, report);
        testPopulator(generated, report);
        testPopulator(lambda, report);
        testPopulator(manualLambda, report);
        testPopulator(reflection, report);
        System.out.println("");
    }

    private static void testPopulator(PositionPopulator populator, boolean report) {
        TestBean last = null;

        long init = System.currentTimeMillis();

        for (int i = 0; i < elements; i++) {
            last = populator.populate("1|2|3|4");
        }
        long elapsed = System.currentTimeMillis() - init;

        if (report) {
            System.out.println("Time to read " + elements + " elements using " + populator.getClass().getSimpleName()
                    + ": " + elapsed + "ms");
            System.out.println(last);
        }
    }
}
