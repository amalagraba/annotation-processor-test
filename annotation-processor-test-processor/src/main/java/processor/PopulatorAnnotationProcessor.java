package processor;

import annotations.Populator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by amalagraba on 20/02/2019.
 * Arteco Consulting Sl
 * mailto: info@arteco-consulting.com
 */
public class PopulatorAnnotationProcessor extends AbstractProcessor {

    private Messager messager;
    private PopulatorImplementationGenerator generator;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        generator = new PopulatorImplementationGenerator(
                processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getFiler());
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element populator : roundEnv.getElementsAnnotatedWith(Populator.class)) {
            if (populator.getKind() != ElementKind.INTERFACE) {
                error(populator, "Annotated type must be an interface");
            }
            generateImplementation((TypeElement) populator);
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>(1);
        annotations.add(Populator.class.getCanonicalName());

        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void generateImplementation(TypeElement populator) {
        try {
            generator.generate(populator);
        } catch (IOException | GenerationException e) {
            error(populator, e.getMessage());
        }
    }

    private void error(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
}
