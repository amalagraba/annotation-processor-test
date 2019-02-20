package processor;

import lombok.Getter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.List;

/**
 * Created by amalagraba on 20/02/2019.
 * Arteco Consulting Sl
 * mailto: info@arteco-consulting.com
 */
@Getter
public class PopulatorMethod {

    private String name;
    private TypeElement returnType;


    public PopulatorMethod(Elements elementUtils, ExecutableElement method) throws GenerationException {
        validateParameterIsString(elementUtils, method.getParameters());
        this.returnType = getType(method.getReturnType());
        this.name = method.getSimpleName().toString();
    }

    private void validateParameterIsString(Elements elementUtils, List<? extends VariableElement> parameters) throws GenerationException {
        if (parameters.size() == 1) {
            if (elementUtils.getTypeElement(String.class.getCanonicalName()).equals(getType(parameters.get(0).asType()))) {
                return;
            }
        }
        throw new GenerationException("Populator parameter can only be a string");
    }

    private TypeElement getType(TypeMirror mirror) {
        return (TypeElement) ((DeclaredType) mirror).asElement();
    }
}