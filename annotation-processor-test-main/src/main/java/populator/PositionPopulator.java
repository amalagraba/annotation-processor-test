package populator;

import annotations.Populator;
import model.TestBean;

@Populator
public interface PositionPopulator {

    TestBean populate(String string);
}
