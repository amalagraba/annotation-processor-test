package processor;

import annotations.Position;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PopulatorImplementationGenerator {

    private final Elements elementUtils;
    private final Filer filer;


    void generate(TypeElement element) throws GenerationException, IOException {
        TypeSpec.Builder builder = TypeSpec
                .classBuilder(element.getSimpleName().toString() + "Impl")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(TypeName.get(element.asType()));

        for (PopulatorMethod method : getDeclaredMethods(element)) {
            builder.addMethod(generateMethod(method));
        }
        generateFile(element, builder.build()).writeTo(filer);
    }

    private JavaFile generateFile(TypeElement element, TypeSpec typeSpec) {
        return JavaFile.builder(elementUtils.getPackageOf(element).getQualifiedName().toString(), typeSpec).build();
    }

    private MethodSpec generateMethod(PopulatorMethod method) throws GenerationException {
        TypeElement returnType = method.getReturnType();

        ClassName returnClass = ClassName.get(returnType);

        MethodSpec.Builder builder = MethodSpec.methodBuilder(method.getName())
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(returnType.asType()))
                .addParameter(String.class, "string")
                .addStatement("String[] parts = string.split(\"\\\\|\")")
                .addStatement("$T result = new $T()", returnClass, returnClass);

        getDeclaredFields(method.getReturnType()).stream()
                .map(this::generateFieldMapping)
                .forEach(builder::addStatement);

        builder.addStatement("return result");

        return builder.build();
    }

    private CodeBlock generateFieldMapping(PopulatedField field) {
        return CodeBlock.of("result." + field.getSetterName() + "(parts[" + field.getPosition() + "])");
    }

    private List<PopulatedField> getDeclaredFields(TypeElement populated) throws GenerationException {
        List<PopulatedField> fields = new ArrayList<>();
        Set<Integer> positions = new HashSet<>();

        for (Element element : populated.getEnclosedElements()) {
            if (element.getKind() == ElementKind.FIELD && element.getAnnotation(Position.class) !=  null) {
                PopulatedField field = new PopulatedField((VariableElement) element);

                if (positions.contains(field.getPosition())) {
                    throw new GenerationException("There are colliding positions in " + populated.getQualifiedName());
                } else {
                    fields.add(field);
                    positions.add(field.getPosition());
                }
            }
        }
        if (fields.isEmpty()) {
            throw new GenerationException("Populated object must have at least one field annotated with @Position");
        }
        fields.sort(Comparator.comparing(PopulatedField::getPosition));

        return fields;
    }

    private List<PopulatorMethod> getDeclaredMethods(TypeElement populator) throws GenerationException {
        List<PopulatorMethod> methods = new ArrayList<>();

        for (Element element : populator.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) element;

                if (!method.getModifiers().contains(Modifier.STATIC)
                        && !method.getModifiers().contains(Modifier.DEFAULT)) {
                    methods.add(new PopulatorMethod(elementUtils, method));
                }
            }
        }
        if (methods.isEmpty()) {
            throw new GenerationException("Declared populator methods must have at least one method");
        }
        return methods;
    }
}
