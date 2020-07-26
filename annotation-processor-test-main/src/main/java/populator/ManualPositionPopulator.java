package populator;

import model.TestBean;

public class ManualPositionPopulator implements PositionPopulator {

    public TestBean populate(String string) {
        String[] parts = string.split("\\|");

        TestBean testBean = new TestBean();
        testBean.setField1(parts[0]);
        testBean.setField2(parts[1]);
        testBean.setField3(parts[2]);
        testBean.setField4(parts[3]);

        return testBean;
    }
}
