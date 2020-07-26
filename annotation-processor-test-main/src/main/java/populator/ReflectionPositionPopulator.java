package populator;

import model.TestBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReflectionPositionPopulator implements PositionPopulator {

    List<Method> setters = new ArrayList<>();

    public ReflectionPositionPopulator() {
        for (Method method : TestBean.class.getMethods()) {
            if (method.getName().startsWith("set")) {
                setters.add(method);
            }
        }
        setters.sort((Comparator.comparing(Method::getName)));
    }

    public TestBean populate(String string) {
        String[] parts = string.split("\\|");
        TestBean testBean = new TestBean();

        for (int i = 0; i < parts.length; i++) {
            if (setters.size() >= i) {
                try {
                    setters.get(i).invoke(testBean, parts[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return testBean;
    }

    public List<Method> getSetters() {
        return setters;
    }
}
