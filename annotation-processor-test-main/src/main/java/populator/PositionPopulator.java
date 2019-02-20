package populator;

import annotations.Populator;
import model.TestBean;

/**
 * Created by amalagraba on 19/02/2019.
 * Arteco Consulting Sl
 * mailto: info@arteco-consulting.com
 */
@Populator
public interface PositionPopulator {

    TestBean populate(String string);
}
