package processor;

import annotations.Populator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class PopulatorAnnotationProcessor extends AbstractProcessor {

    private PopulatorImplementationGenerator generator;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        generator = new PopulatorImplementationGenerator(processingEnv.getElementUtils(), processingEnv.getFiler());
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
        return Set.of(Populator.class.getCanonicalName());
    }

    private void generateImplementation(TypeElement populator) {
        try {
            generator.generate(populator);
        } catch (IOException | GenerationException e) {
            error(populator, e.getMessage());
        }
    }

    private void error(Element e, String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
}
