package model;

import annotations.Position;
import lombok.Data;

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
