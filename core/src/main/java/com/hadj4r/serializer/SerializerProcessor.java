package com.hadj4r.serializer;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import static javax.lang.model.SourceVersion.RELEASE_17;
import static javax.lang.model.element.ElementKind.FIELD;

@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_17)
@SupportedAnnotationTypes("com.hadj4r.serializer.Serializer")
public class SerializerProcessor extends AbstractProcessor {
    private static final String BUILDER_CLASS_SIGNATURE = """
            package %s;
            
            public class %s implements %s {
            """;

    private static final String SERIALIZE_METHOD_SIGNATURE = """
            \t@Override
            \tpublic byte[] serialize(final %s model) {
            """;

    private static final String FLOAT_BITS_VARIABLE_DECLARATION = "\t\tint floatBits;";

    private static final String DOUBLE_BITS_VARIABLE_DECLARATION = "\t\tlong doubleBits;";

    private static final String END_OF_ELEMENT_TYPE = "}";

    private static final String BOOLEAN = "boolean";
    private static final String BYTE = "byte";
    private static final String CHAR = "char";
    private static final String SHORT = "short";
    private static final String INT = "int";
    private static final String LONG = "long";
    private static final String FLOAT = "float";
    public static final String DOUBLE = "double";

    private static final Map<String, Integer> PRIMITIVE_TYPE_TO_SIZE = Map.of(
            BOOLEAN, 1,
            CHAR, 2,
            BYTE, 1,
            SHORT, 2,
            INT, 4,
            LONG, 8,
            FLOAT, 4,
            DOUBLE, 8
    );

    private static final Map<Integer, String> SHIFT_TO_HEX_COVER = Map.of(
            0, "0xFF",
            1, "0x7F",
            2, "0x3F",
            3, "0x1F",
            4, "0x0F",
            5, "0x07",
            6, "0x03",
            7, "0x01"
    );

    private static final BiFunction<Integer, String, String> BOOLEAN_CONVERTER = """
            \t\tresult[%s] = (byte) (model.%s ? 1 : 0);
            \t\toffset += 1;
            """::formatted;

    private static final BiFunction<Integer, String, String> CHAR_CONVERTER = (offset, getterName) -> """
            \t\tresult[%s] = (byte) (model.%s >> 8);
            \t\tresult[%s] = (byte) model.%s;
            \t\toffset += 2;
            """.formatted(offset, getterName, offset + 1, getterName);

    private static final BiFunction<Integer, String, String> BYTE_CONVERTER = """
            \t\tresult[%s] = model.%s;
            \t\toffset += 1;
            """::formatted;

    private static final BiFunction<Integer, String, String> SHORT_CONVERTER = (offset, getterName) -> """
            \t\tresult[%s] = (byte) (model.%s >> 8);
            \t\tresult[%s] = (byte) model.%s;
            \t\toffset += 2;
            """.formatted(offset, getterName, offset + 1, getterName);

    private static final BiFunction<Integer, String, String> INT_CONVERTER = (offset, getterName) -> """
            \t\tresult[%s] = (byte) (model.%s >> 24);
            \t\tresult[%s] = (byte) (model.%s >> 16);
            \t\tresult[%s] = (byte) (model.%s >> 8);
            \t\tresult[%s] = (byte)  model.%s;
            \t\toffset += 4;
            """.formatted(offset, getterName, offset + 1, getterName, offset + 2, getterName, offset + 3, getterName);

    private static final BiFunction<Integer, String, String> LONG_CONVERTER = (offset, getterName) -> """
            \t\tresult[%s] = (byte) (model.%s >> 56);
            \t\tresult[%s] = (byte) (model.%s >> 48);
            \t\tresult[%s] = (byte) (model.%s >> 40);
            \t\tresult[%s] = (byte) (model.%s >> 32);
            \t\tresult[%s] = (byte) (model.%s >> 24);
            \t\tresult[%s] = (byte) (model.%s >> 16);
            \t\tresult[%s] = (byte) (model.%s >> 8);
            \t\tresult[%s] = (byte)  model.%s;
            \t\toffset += 8;
            """.formatted(offset, getterName, offset + 1, getterName, offset + 2, getterName, offset + 3, getterName, offset + 4, getterName, offset + 5, getterName, offset + 6, getterName, offset + 7, getterName);

    private static final BiFunction<Integer, String, String> FLOAT_CONVERTER = (offset, getterName) -> """
            \t\tfloatBits = Float.floatToIntBits(model.%s);
            \t\tresult[%s] = (byte) (floatBits >> 24);
            \t\tresult[%s] = (byte) (floatBits >> 16);
            \t\tresult[%s] = (byte) (floatBits >> 8);
            \t\tresult[%s] = (byte)  floatBits;
            \t\toffset += 4;
            """.formatted(getterName, offset, offset + 1, offset + 2, offset + 3);

    private static final BiFunction<Integer, String, String> DOUBLE_CONVERTER = (offset, getterName) -> """
            \t\tdoubleBits = Double.doubleToLongBits(model.%s);
            \t\tresult[%s] = (byte) (doubleBits >> 56);
            \t\tresult[%s] = (byte) (doubleBits >> 48);
            \t\tresult[%s] = (byte) (doubleBits >> 40);
            \t\tresult[%s] = (byte) (doubleBits >> 32);
            \t\tresult[%s] = (byte) (doubleBits >> 24);
            \t\tresult[%s] = (byte) (doubleBits >> 16);
            \t\tresult[%s] = (byte) (doubleBits >> 8);
            \t\tresult[%s] = (byte)  doubleBits;
            \t\toffset += 8;
            """.formatted(getterName, offset, offset + 1, offset + 2, offset + 3, offset + 4, offset + 5, offset + 6, offset + 7);

    private static final Function<String, String> STRING_CONVERTER = """
            \t\tcom.hadj4r.serializer.util.UTF8Utils.stringToUTF8BytesArray(model.%s, result, offset);
            """::formatted;
    private static final Map<String, BiFunction<Integer, String, String>> PRIMITIVE_TYPE_TO_FUNCTION = Map.of(
            BOOLEAN, BOOLEAN_CONVERTER,
            CHAR, CHAR_CONVERTER,
            BYTE, BYTE_CONVERTER,
            SHORT, SHORT_CONVERTER,
            INT, INT_CONVERTER,
            LONG, LONG_CONVERTER,
            FLOAT, FLOAT_CONVERTER,
            DOUBLE, DOUBLE_CONVERTER
            );

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        annotations.stream()
                .flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation).stream())
                .forEach(e -> generateSerializerServiceImplementationClass(e, roundEnv));

        return true;
    }

    private void generateSerializerServiceImplementationClass(final Element element, final RoundEnvironment roundEnv) {
        final String className = element.getSimpleName().toString();
        final String packageName = element.getEnclosingElement().toString();
        final String serializerName = className + "Impl";
        final String serializerFullName = packageName + "." + serializerName;

        // retrieve the interface class
        final TypeElement typeElement = (TypeElement)element;
        final DeclaredType declaredType = (DeclaredType)typeElement.getInterfaces().get(0);
        final String modelFullName = declaredType.getTypeArguments().get(0).toString();

        final StringBuilder sb = new StringBuilder();

        try (final PrintWriter writer = getPrintWriter(serializerFullName)) {
            processHeader(className, packageName, serializerName, modelFullName, sb);

            final Element modelClass = getModelClass(roundEnv, modelFullName);
            final List<? extends Element> modelFields = getModelFields(modelClass);
            final Map<String, String> stringFieldFirstLevelGetterToSize = new HashMap<>();

            calculateFinalByteArraySize(sb, modelFields, stringFieldFirstLevelGetterToSize);
            addFloatAndDoubleFieldsForCachingIfNecessary(sb, modelFields);
            processFieldsSerialization(sb, modelFields, stringFieldFirstLevelGetterToSize);
            processTail(sb);

            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFieldsSerialization(final StringBuilder sb, final List<? extends Element> modelFields, final Map<String, String> stringFieldFirstLevelGetterToSize) {
        sb.append("\t\tint offset = 0;\n\n");
        int offset = 0;
        for (final Element field : modelFields) {
            final String fieldType = field.asType().toString();
            final String fieldName = field.getSimpleName().toString();
            final String getterName = (BOOLEAN.equals(fieldType) ? "is" : "get") +
                                      fieldName.substring(0, 1).toUpperCase() +
                                      fieldName.substring(1) + "()";

            final Optional<BiFunction<Integer, String, String>> primitiveToFunction = Optional.ofNullable(
                    PRIMITIVE_TYPE_TO_FUNCTION.get(fieldType));
            if (primitiveToFunction.isPresent()) {
                final String primitiveVariableConversation = primitiveToFunction.get().apply(offset, getterName);
                sb
                        .append(primitiveVariableConversation)
                        .append('\n');

                offset += PRIMITIVE_TYPE_TO_SIZE.get(fieldType); // doesn't recheck the type because it is already checked above
            } else if (fieldType.equals("java.lang.String")) {
                sb
                        .append(STRING_CONVERTER.apply(getterName).formatted(getterName))
                        // increase offset by the size of the byte array (already calculated)
                        .append("\t\toffset += %s;%n".formatted(stringFieldFirstLevelGetterToSize.get(getterName)))
                        .append('\n');
            }
        }
    }

    private static void processTail(final StringBuilder sb) {
        sb
                .append("\t\treturn result;\n\t")
                .append(END_OF_ELEMENT_TYPE) // end of serialize method
                .append('\n')
                .append(END_OF_ELEMENT_TYPE); // end of class
    }

    private static void addFloatAndDoubleFieldsForCachingIfNecessary(final StringBuilder sb, final List<? extends Element> modelFields) {
        final boolean hasFloatField = modelFields.stream()
                .anyMatch(e -> FLOAT.equals(e.asType().toString()));

        if (hasFloatField) {
            sb
                    .append(FLOAT_BITS_VARIABLE_DECLARATION)
                    .append('\n');
        }

        final boolean hasDoubleField = modelFields.stream()
                .anyMatch(e -> DOUBLE.equals(e.asType().toString()));

        if (hasDoubleField) {
            sb
                    .append(DOUBLE_BITS_VARIABLE_DECLARATION)
                    .append('\n');
        }
    }

    private static void calculateFinalByteArraySize(final StringBuilder sb, final List<? extends Element> modelFields, final Map<String, String> stringFieldFirstLevelGetterToSize) {
        final Set<String> stringFieldsFirstLevel = new HashSet<>();
        int modelSize = 0;
        for (final Element field : modelFields) {
            final String fieldType = field.asType().toString();
            final Optional<Integer> optionalPrimitiveSize = Optional.ofNullable(PRIMITIVE_TYPE_TO_SIZE.get(fieldType));
            if (optionalPrimitiveSize.isPresent()) {
                modelSize += optionalPrimitiveSize.get();
            } else if (fieldType.equals("java.lang.String")) {
                stringFieldsFirstLevel.add(field.getSimpleName().toString());
            }
        }

        boolean hasDynamicSize = !stringFieldsFirstLevel.isEmpty();
        if (hasDynamicSize) {
            sb
                    .append("\t\tint modelDynamicSize = 0;\n");
            final List<String> stringFieldsFirstLayerGetters = stringFieldsFirstLevel.stream()
                    .map(s -> "get" + s.substring(0, 1).toUpperCase() + s.substring(1) + "()")
                    .toList();
            for (int i = 0; i < stringFieldsFirstLayerGetters.size(); ++i) {
                // create unique variable name containing the byte array size
                final String byteArraySize = "byteArraySize" + i;
                stringFieldFirstLevelGetterToSize.put(stringFieldsFirstLayerGetters.get(i), byteArraySize);
                sb
                        .append("\t\tint %s = 1 + com.hadj4r.serializer.util.UTF8Utils.utf8StringByteArrayLength(model.%s);%n"
                                .formatted(byteArraySize, stringFieldsFirstLayerGetters.get(i)))
                        .append("\t\tmodelDynamicSize += %s;%n"
                                .formatted(byteArraySize));
            }
        }

        sb
                .append("\t\tbyte[] result = new byte[")
                .append(modelSize)
                .append(" + ")
                .append("modelDynamicSize")
                .append("];\n\n");
    }

    private static List<? extends Element> getModelFields(final Element modelClass) {
        return modelClass.getEnclosedElements().stream()
                .filter(e -> FIELD.equals(e.getKind()))
                .toList();
    }

    private static Element getModelClass(final RoundEnvironment roundEnv, final String modelFullName) {
        return roundEnv.getRootElements().stream()
                .filter(e -> modelFullName.equals(e.toString())).findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find model class"));
    }

    private static void processHeader(final String className, final String packageName, final String serializerName, final String modelFullName, final StringBuilder sb) {
        sb
                .append(BUILDER_CLASS_SIGNATURE.formatted(packageName, serializerName, className))
                .append(SERIALIZE_METHOD_SIGNATURE.formatted(modelFullName));
    }

    private PrintWriter getPrintWriter(final String serializerFullName) throws IOException {
        return new PrintWriter(processingEnv.getFiler().createSourceFile(serializerFullName).openWriter());
    }
}

