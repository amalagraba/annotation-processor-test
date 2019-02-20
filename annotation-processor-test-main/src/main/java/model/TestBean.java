package model;

import annotations.Position;
import lombok.Data;

/**
 * Created by amalagraba on 19/02/2019.
 * Arteco Consulting Sl
 * mailto: info@arteco-consulting.com
 */
@Data
public class TestBean {

    @Position(0)
    private String field1;
    @Position(1)
    private String field2;
    @Position(2)
    private String field3;
    @Position(3)
    private String field4;
}
