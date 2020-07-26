package populator;

import model.TestBean;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class LambdaPositionPopulator implements PositionPopulator {

    List<BiConsumer<TestBean, String>> setters = new ArrayList<>();

    public LambdaPositionPopulator() {
        List<Method> methods = new ReflectionPositionPopulator().getSetters();
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        try {
            for (Method method : methods) {
                MethodType setter = MethodType.methodType(void.class, method.getParameterTypes()[0]);
                MethodHandle target = lookup.findVirtual(TestBean.class, method.getName(), setter);

                final CallSite site = LambdaMetafactory.metafactory(lookup,
                        "accept", MethodType.methodType(BiConsumer.class, MethodHandle.class),
                        target.type().erase(), MethodHandles.exactInvoker(target.type()), target.type());

                BiConsumer<TestBean, String> r = (BiConsumer) site.getTarget().invokeExact(target);

                setters.add(r);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
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
