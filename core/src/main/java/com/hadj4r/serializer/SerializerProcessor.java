package com.hadj4r.serializer;

import com.google.auto.service.AutoService;
import com.hadj4r.serializer.util.Node;
import com.hadj4r.serializer.util.Pair;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import static javax.lang.model.SourceVersion.RELEASE_17;

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
    private static final String DOUBLE = "double";
    private static final String ARRAY = "[]";
    private static final String ARRAY_LENGTH = ".length";
    private static final String BOOLEAN_ARRAY = BOOLEAN + ARRAY;
    private static final String BYTE_ARRAY = BYTE + ARRAY;
    private static final String CHAR_ARRAY = CHAR + ARRAY;
    private static final String SHORT_ARRAY = SHORT + ARRAY;
    private static final String INT_ARRAY = INT + ARRAY;
    private static final String LONG_ARRAY = LONG + ARRAY;
    private static final String FLOAT_ARRAY = FLOAT + ARRAY;
    private static final String DOUBLE_ARRAY = DOUBLE + ARRAY;

    private static final Set<String> PRIMITIVE_ARRAY_TYPES = Set.of(
            BOOLEAN_ARRAY,
            BYTE_ARRAY,
            CHAR_ARRAY,
            SHORT_ARRAY,
            INT_ARRAY,
            LONG_ARRAY,
            FLOAT_ARRAY,
            DOUBLE_ARRAY
    );

    private static final Set<String> PRIMITIVE_TYPES = Set.of(
            BOOLEAN,
            BYTE,
            CHAR,
            SHORT,
            INT,
            LONG,
            FLOAT,
            DOUBLE
    );

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

    private static final Map<String, Integer> PRIMITIVE_ARRAY_TYPE_TO_SIZE = Map.of(
            BOOLEAN_ARRAY, 1,
            CHAR_ARRAY, 2,
            BYTE_ARRAY, 1,
            SHORT_ARRAY, 2,
            INT_ARRAY, 4,
            LONG_ARRAY, 8,
            FLOAT_ARRAY, 4,
            DOUBLE_ARRAY, 8
    );

    private static final Function<String, String> BOOLEAN_CONVERTER = """
            \t\tresult[offset] = (byte) (%s ? 1 : 0);
            \t\toffset += 1;
            """::formatted;

    private static final Function<String, String> CHAR_CONVERTER = getterName -> """
            \t\tresult[offset] = (byte) (%s >>> 8);
            \t\tresult[offset + 1] = (byte) %s;
            \t\toffset += 2;
            """.formatted(getterName, getterName);

    private static final Function<String, String> BYTE_CONVERTER = """
            \t\tresult[offset] = %s;
            \t\toffset += 1;
            """::formatted;

    private static final Function<String, String> SHORT_CONVERTER = getterName -> """
            \t\tresult[offset] = (byte) (%s >>> 8);
            \t\tresult[offset + 1] = (byte) %s;
            \t\toffset += 2;
            """.formatted(getterName, getterName);

    private static final Function<String, String> INT_CONVERTER = getterName -> """
            \t\tresult[offset] = (byte) (%s >>> 24);
            \t\tresult[offset + 1] = (byte) (%s >>> 16);
            \t\tresult[offset + 2] = (byte) (%s >>> 8);
            \t\tresult[offset + 3] = (byte)  %s;
            \t\toffset += 4;
            """.formatted(getterName, getterName, getterName, getterName);

    private static final Function<String, String> LONG_CONVERTER = getterName -> """
            \t\tresult[offset] = (byte) (%s >>> 56);
            \t\tresult[offset + 1] = (byte) (%s >>> 48);
            \t\tresult[offset + 2] = (byte) (%s >>> 40);
            \t\tresult[offset + 3] = (byte) (%s >>> 32);
            \t\tresult[offset + 4] = (byte) (%s >>> 24);
            \t\tresult[offset + 5] = (byte) (%s >>> 16);
            \t\tresult[offset + 6] = (byte) (%s >>> 8);
            \t\tresult[offset + 7] = (byte)  %s;
            \t\toffset += 8;
            """.formatted(getterName, getterName, getterName, getterName, getterName, getterName, getterName, getterName);

    private static final Function<String, String> FLOAT_CONVERTER = """
            \t\tfloatBits = Float.floatToIntBits(%s);
            \t\tresult[offset] = (byte) (floatBits >>> 24);
            \t\tresult[offset + 1] = (byte) (floatBits >>> 16);
            \t\tresult[offset + 2] = (byte) (floatBits >>> 8);
            \t\tresult[offset + 3] = (byte)  floatBits;
            \t\toffset += 4;
            """::formatted;

    private static final Function<String, String> DOUBLE_CONVERTER = """
            \t\tdoubleBits = Double.doubleToLongBits(%s);
            \t\tresult[offset] = (byte) (doubleBits >>> 56);
            \t\tresult[offset + 1] = (byte) (doubleBits >>> 48);
            \t\tresult[offset + 2] = (byte) (doubleBits >>> 40);
            \t\tresult[offset + 3] = (byte) (doubleBits >>> 32);
            \t\tresult[offset + 4] = (byte) (doubleBits >>> 24);
            \t\tresult[offset + 5] = (byte) (doubleBits >>> 16);
            \t\tresult[offset + 6] = (byte) (doubleBits >>> 8);
            \t\tresult[offset + 7] = (byte)  doubleBits;
            \t\toffset += 8;
            """::formatted;

    private static final Function<String, String> STRING_CONVERTER = """
            \t\tcom.hadj4r.serializer.util.UTF8Utils.stringToUTF8BytesArray(%s, result, offset);
            """::formatted;
    private static final Function<String, String> BOOLEAN_ARRAY_CONVERTER = booleanArrayConverter();
    private static final Function<String, String> CHAR_ARRAY_CONVERTER = charArrayConverter();
    private static final Function<String, String> BYTE_ARRAY_CONVERTER = byteArrayConverter();
    private static final Function<String, String> SHORT_ARRAY_CONVERTER = shortArrayConverter();
    private static final Function<String, String> INT_ARRAY_CONVERTER = intArrayConverter();
    private static final Function<String, String> LONG_ARRAY_CONVERTER = longArrayConverter();
    private static final Function<String, String> FLOAT_ARRAY_CONVERTER = floatArrayConverter();
    private static final Function<String, String> DOUBLE_ARRAY_CONVERTER = doubleArrayConverter();

    private static final Map<String, Function<String, String>> PRIMITIVE_TYPE_TO_CONVERTER = Map.of(
            BOOLEAN, BOOLEAN_CONVERTER,
            CHAR, CHAR_CONVERTER,
            BYTE, BYTE_CONVERTER,
            SHORT, SHORT_CONVERTER,
            INT, INT_CONVERTER,
            LONG, LONG_CONVERTER,
            FLOAT, FLOAT_CONVERTER,
            DOUBLE, DOUBLE_CONVERTER
    );

    private static final Map<String, Function<String, String>> PRIMITIVE_ARRAY_TYPE_TO_CONVERTER = Map.of(
            BOOLEAN_ARRAY, BOOLEAN_ARRAY_CONVERTER,
            CHAR_ARRAY, CHAR_ARRAY_CONVERTER,
            BYTE_ARRAY, BYTE_ARRAY_CONVERTER,
            SHORT_ARRAY, SHORT_ARRAY_CONVERTER,
            INT_ARRAY, INT_ARRAY_CONVERTER,
            LONG_ARRAY, LONG_ARRAY_CONVERTER,
            FLOAT_ARRAY, FLOAT_ARRAY_CONVERTER,
            DOUBLE_ARRAY, DOUBLE_ARRAY_CONVERTER
    );

    private static final BiFunction<Integer, Byte, String> TYPE_ID = """
            \t\tresult[%s] = %s;
            \t\toffset += 1;
            """::formatted;

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
        final TypeElement typeElement = (TypeElement) element;
        final DeclaredType declaredType = (DeclaredType) typeElement.getInterfaces().get(0);
        final String modelFullName = declaredType.getTypeArguments().get(0).toString();

        final Element rootModel = roundEnv.getRootElements().stream()
                .filter(e -> modelFullName.equals(e.toString()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Cannot find the model class " + modelFullName));

        final Node root = createTreeOfFieldNodes(rootModel, rootModel, null, roundEnv);
        root.setFieldName("model");

        final StringBuilder sb = new StringBuilder();

        try (final PrintWriter writer = getPrintWriter(serializerFullName)) {
            processHeader(className, packageName, serializerName, modelFullName, sb);

            final Map<String, String> stringGetterToSize = new HashMap<>();

            calculateFinalByteArraySize(sb, root, stringGetterToSize);
            addFloatAndDoubleFieldsForCachingIfNecessary(sb, root);
            processFieldsSerialization(sb, root, stringGetterToSize);
            processTail(sb);

            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node createTreeOfFieldNodes(final Element classElement, final Element fieldElement, final Node parent,
                                        final RoundEnvironment roundEnv) {
        if (classElement == null) {
            return new Node(fieldElement.asType(), fieldElement, parent);
        }

        final Node n = new Node(classElement.asType(), fieldElement, parent);
        classElement.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(e -> new Pair<>(getElementOrNull(roundEnv, e), e)) // element & field name
                .map(e -> createTreeOfFieldNodes(e.getFirst(), e.getSecond(), n, roundEnv))
                .forEach(n::addChild);

        return n;
    }

    private static Element getElementOrNull(final RoundEnvironment roundEnv, final Element e) {
        return roundEnv.getRootElements().stream()
                .filter(r -> r.toString().equals(e.asType().toString()))
                .findAny()
                .orElse(null);
    }

    private static boolean isCustomType(final String type) {
        return !isPrimitive(type) && !isPrimitiveArray(type) && !isString(type);
    }

    private static boolean isPrimitive(final String type) {
        return PRIMITIVE_TYPES.contains(type);
    }

    private static boolean isPrimitiveArray(final String type) {
        return PRIMITIVE_ARRAY_TYPES.contains(type);
    }

    private static boolean isString(final String type) {
        return String.class.getName().equals(type);
    }

    private static void processFieldsSerialization(final StringBuilder sb, final Node root,
                                                   final Map<String, String> stringFieldGetterToSize) {
        sb.append("\t\tint offset = 0;\n\n");
        final Stack<Node> stack = new Stack<>();
        stack.add(root);

        while (!stack.isEmpty()) {
            final Node node = stack.pop();
            final String fieldType = node.getType();
            final String getterName = node.getFieldNameAsGetter();

            if (isCustomType(fieldType)) {
                stack.addAll(node.getChildren());
            } else if (isPrimitive(fieldType)) {
                final Function<String, String> primitiveToFunction = PRIMITIVE_TYPE_TO_CONVERTER.get(fieldType);
                final String primitiveVariableConversation = primitiveToFunction.apply(getterName);
                sb
                        .append(primitiveVariableConversation)
                        .append('\n');

            } else if (isString(fieldType)) {
                sb
                        .append(STRING_CONVERTER.apply(getterName).formatted(getterName))
                        // increase offset by the size of the byte array (already calculated)
                        .append("\t\toffset += %s;%n".formatted(stringFieldGetterToSize.get(getterName)))
                        .append('\n');
            } else if (isPrimitiveArray(fieldType)) {
                sb
                        .append("\t\tresult[offset++] = (byte) %s.length;%n".formatted(getterName))
                        .append(PRIMITIVE_ARRAY_TYPE_TO_CONVERTER.get(fieldType).apply(getterName))
                        // increase offset by the size of the byte array (already calculated)
                        .append("\t\toffset += %s * %s;%n"
                                .formatted(PRIMITIVE_ARRAY_TYPE_TO_SIZE.get(fieldType), getterName + ARRAY_LENGTH))
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

    private static void addFloatAndDoubleFieldsForCachingIfNecessary(final StringBuilder sb,
                                                                     final Node root) {
        if (hasType(root, FLOAT, FLOAT_ARRAY)) {
            sb
                    .append(FLOAT_BITS_VARIABLE_DECLARATION)
                    .append('\n');
        }

        if (hasType(root, DOUBLE, DOUBLE_ARRAY)) {
            sb
                    .append(DOUBLE_BITS_VARIABLE_DECLARATION)
                    .append('\n');
        }
    }

    private static boolean hasType(final Node root, final String ... types) {
        final Predicate<String> hasAnyType = Arrays.stream(types)
                .map(type -> (Predicate<String>) type::equals)
                .reduce(Predicate::or)
                .orElse(e -> false);
        final Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            final Node node = queue.poll();
            if (hasAnyType.test(node.getType())) {
                return true;
            }

            queue.addAll(node.getChildren());
        }

        return false;
    }

    private static void calculateFinalByteArraySize(final StringBuilder sb, final Node root,
                                                    final Map<String, String> stringGettersToSize) {
        // using list to preserve the order of the fields
        final List<String> stringFields = new ArrayList<>();
        final List<String> primitiveArrays = new ArrayList<>();
        final Map<String, String> primitiveArrayFieldsToType = new HashMap<>();
        int modelSize = 0;

        final Deque<Node> stack = new LinkedList<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            final Node node = stack.pop();
            final String type = node.getType();
            final String fieldGetter = node.getFieldNameAsGetter();

            if (isCustomType(type)) {
                node.getChildren().forEach(stack::push);
            } else if (isPrimitive(type)) {
                modelSize += PRIMITIVE_TYPE_TO_SIZE.get(type);
            } else if (isString(type)) {
                stringFields.add(fieldGetter);
            } else if (isPrimitiveArray(type)) {
                primitiveArrays.add(fieldGetter);
                primitiveArrayFieldsToType.put(fieldGetter, type);
            }
        }

        boolean hasDynamicSize = !stringFields.isEmpty() || !primitiveArrays.isEmpty();
        if (hasDynamicSize) {
            sb.append("\t\tint modelDynamicSize = 0;\n");
            int i = 0;
            for (final String stringGetter : stringFields) {
                final String byteArraySize = getByteArrSizeVariableName(i);
                stringGettersToSize.put(stringGetter, byteArraySize);
                sb
                        .append("\t\tint %s = 1 + com.hadj4r.serializer.util.UTF8Utils.utf8StringByteArrayLength(%s);%n"
                                .formatted(byteArraySize, stringGetter))
                        .append("\t\tmodelDynamicSize += %s;%n"
                                .formatted(byteArraySize));
                ++i;
            }
            for (String arrayGetter : primitiveArrays) {
                final String arraySize = arrayGetter + ARRAY_LENGTH;
                sb
                        .append("\t\tmodelDynamicSize += 1 + %s * %s;%n"
                                .formatted(PRIMITIVE_ARRAY_TYPE_TO_SIZE.get(primitiveArrayFieldsToType.get(arrayGetter)), arraySize));
                ++i;
            }
        }

        sb
                .append("\t\tbyte[] result = new byte[")
                .append(modelSize)
                .append(" + ")
                .append("modelDynamicSize")
                .append("];\n\n");
    }

    private static String getByteArrSizeVariableName(final int i) {
        return "byteArrSize" + i;
    }

    private static void processHeader(final String className, final String packageName, final String serializerName,
                                      final String modelFullName, final StringBuilder sb) {
        sb
                .append(BUILDER_CLASS_SIGNATURE.formatted(packageName, serializerName, className))
                .append(SERIALIZE_METHOD_SIGNATURE.formatted(modelFullName));
    }

    private PrintWriter getPrintWriter(final String serializerFullName) throws IOException {
        return new PrintWriter(processingEnv.getFiler().createSourceFile(serializerFullName).openWriter());
    }

    private static Function<String, String> booleanArrayConverter() {
        return getterName -> {
            final String arraySize = getterName + ARRAY_LENGTH;
            return """
                    \t\tfor (int i = 0; i < %s; i++) {
                    \t\t\tresult[offset + i] = (byte) (%s[i] ? 1 : 0);
                    \t\t}
                    """.formatted(arraySize, getterName);
        };
    }

    private static Function<String, String> byteArrayConverter() {
        return getterName -> {
            final String arraySize = getterName + ARRAY_LENGTH;
            return """
                    \t\tfor (int i = 0; i < %s; i++) {
                    \t\t\tresult[offset + i] = %s[i];
                    \t\t}
                    """.formatted(arraySize, getterName);
        };
    }

    private static Function<String, String> charArrayConverter() {
        return getterName -> {
            final String arraySize = getterName + ARRAY_LENGTH;
            return """
                    \t\tfor (int i = 0, idx = 0; idx < %s; ++idx, i += 2) {
                    \t\t\tresult[offset + i]     = (byte) (%s[idx] >>> 8);
                    \t\t\tresult[offset + i + 1] = (byte) %s[idx];
                    \t\t}
                    """.formatted(arraySize, getterName, getterName);
        };
    }

    private static Function<String, String> shortArrayConverter() {
        return getterName -> {
            final String arraySize = getterName + ARRAY_LENGTH;
            return """
                    \t\tfor (int i = 0, idx = 0; idx < %s; ++idx, i += 2) {
                    \t\t\tresult[offset + i]     = (byte) (%s[idx] >>> 8);
                    \t\t\tresult[offset + i + 1] = (byte) %s[idx];
                    \t\t}
                    """.formatted(arraySize, getterName, getterName);
        };
    }

    private static Function<String, String> intArrayConverter() {
        return getterName -> {
            final String arraySize = getterName + ARRAY_LENGTH;
            return """
                    \t\tfor (int i = 0, idx = 0; idx < %s; ++idx, i += 4) {
                    \t\t\tresult[offset + i]     = (byte) (%s[idx] >>> 24);
                    \t\t\tresult[offset + i + 1] = (byte) (%s[idx] >>> 16);
                    \t\t\tresult[offset + i + 2] = (byte) (%s[idx] >>> 8);
                    \t\t\tresult[offset + i + 3] = (byte) %s[idx];
                    \t\t}
                    """.formatted(arraySize, getterName, getterName, getterName, getterName);
        };
    }

    private static Function<String, String> longArrayConverter() {
        return getterName -> {
            final String arraySize = getterName + ARRAY_LENGTH;
            return """
                    \t\tfor (int i = 0, idx = 0; idx < %s; ++idx, i += 8) {
                    \t\t\tresult[offset + i]     = (byte) (%s[idx] >>> 56);;
                    \t\t\tresult[offset + i + 1] = (byte) (%s[idx] >>> 48);
                    \t\t\tresult[offset + i + 2] = (byte) (%s[idx] >>> 40);
                    \t\t\tresult[offset + i + 3] = (byte) (%s[idx] >>> 32);
                    \t\t\tresult[offset + i + 4] = (byte) (%s[idx] >>> 24);
                    \t\t\tresult[offset + i + 5] = (byte) (%s[idx] >>> 16);
                    \t\t\tresult[offset + i + 6] = (byte) (%s[idx] >>> 8);
                    \t\t\tresult[offset + i + 7] = (byte) %s[idx];
                    \t\t}
                    """.formatted(arraySize, getterName, getterName, getterName, getterName, getterName, getterName,
                    getterName, getterName);
        };
    }

    private static Function<String, String> floatArrayConverter() {
        return getterName -> {
            final String arraySize = getterName + ARRAY_LENGTH;
            return """
                    \t\tfor (int i = 0, idx = 0; idx < %s; ++idx, i += 4) {
                    \t\t\tfloatBits = Float.floatToIntBits(%s[idx]);
                    \t\t\tresult[offset + i]     = (byte) (floatBits >>> 24);
                    \t\t\tresult[offset + i + 1] = (byte) (floatBits >>> 16);
                    \t\t\tresult[offset + i + 2] = (byte) (floatBits >>> 8);
                    \t\t\tresult[offset + i + 3] = (byte) floatBits;
                    \t\t}
                    """.formatted(arraySize, getterName);
        };
    }

    private static Function<String, String> doubleArrayConverter() {
        return getterName -> {
            final String arraySize = getterName + ARRAY_LENGTH;
            return """
                    \t\tfor (int i = 0, idx = 0; idx < %s; ++idx, i += 8) {
                    \t\t\tdoubleBits = Double.doubleToLongBits(%s[idx]);
                    \t\t\tresult[offset + i]     = (byte) (doubleBits >>> 56);
                    \t\t\tresult[offset + i + 1] = (byte) (doubleBits >>> 48);
                    \t\t\tresult[offset + i + 2] = (byte) (doubleBits >>> 40);
                    \t\t\tresult[offset + i + 3] = (byte) (doubleBits >>> 32);
                    \t\t\tresult[offset + i + 4] = (byte) (doubleBits >>> 24);
                    \t\t\tresult[offset + i + 5] = (byte) (doubleBits >>> 16);
                    \t\t\tresult[offset + i + 6] = (byte) (doubleBits >>> 8);
                    \t\t\tresult[offset + i + 7] = (byte) doubleBits;
                    \t\t}
                    """.formatted(arraySize, getterName);
        };
    }
}

