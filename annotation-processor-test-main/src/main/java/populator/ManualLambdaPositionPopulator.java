package populator;

import model.TestBean;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ManualLambdaPositionPopulator implements PositionPopulator {

    private List<BiConsumer<TestBean, String>> setters = new ArrayList<>();

    public ManualLambdaPositionPopulator() {
        setters.add(TestBean::setField1);
        setters.add(TestBean::setField2);
        setters.add(TestBean::setField3);
        setters.add(TestBean::setField4);
    }

    public TestBean populate(String string) {
        String[] parts = string.split("\\|");
        TestBean testBean = new TestBean();

        for (int i = 0; i < parts.length; i++) {
            if (setters.size() > i) {
                setters.get(i).accept(testBean, parts[i]);
            }
        }
        return testBean;
    }
}
