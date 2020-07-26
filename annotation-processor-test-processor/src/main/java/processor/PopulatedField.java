package processor;

import annotations.Position;
import lombok.Getter;

import javax.lang.model.element.VariableElement;

@Getter
public class PopulatedField {

    private int position;
    private String name;

    public PopulatedField(VariableElement field) {
        Position position = field.getAnnotation(Position.class);
        this.position = position.value();
        this.name = field.getSimpleName().toString();
    }

    public String getSetterName() {
        return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
