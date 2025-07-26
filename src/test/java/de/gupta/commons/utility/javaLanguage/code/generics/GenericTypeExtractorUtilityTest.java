package de.gupta.commons.utility.javaLanguage.code.generics;

import de.gupta.commons.utility.javaLanguage.code.type.TypeDeclaration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GenericTypeExtractorUtilityTest {

    @Nested
    @DisplayName("extractGenericTypes method tests")
    class ExtractGenericTypesTests {

        record ExtractGenericTypesTestCase(
                String typeName,
                List<String> expectedGenericTypes,
                String description
        ) {
            static ExtractGenericTypesTestCase of(String typeName, List<String> expectedGenericTypes, String description) {
                return new ExtractGenericTypesTestCase(typeName, expectedGenericTypes, description);
            }
        }

        @ParameterizedTest(name = "{2}")
        @MethodSource("extractGenericTypesProvider")
        @DisplayName("should extract generic types from type declarations")
        void extractGenericTypes(String typeName, List<String> expectedGenericTypes, String testDescription) {
            TypeDeclaration typeDeclaration = new TypeDeclaration(typeName, true);

            List<String> actualGenericTypes = GenericTypeExtractorUtility.extractGenericTypes(typeDeclaration);

            assertThat(actualGenericTypes)
                    .as("Extracted generic types from '%s'", typeName)
                    .isEqualTo(expectedGenericTypes);
        }

        private static Stream<Arguments> extractGenericTypesProvider() {
            return Stream.of(
                    simpleGenericTypes(),
                    complexGenericTypes(),
                    edgeCases()
            ).flatMap(stream -> stream);
        }

        private static Stream<Arguments> simpleGenericTypes() {
            return Stream.of(
                    ExtractGenericTypesTestCase.of(
                            "List<String>",
                            List.of("String"),
                            "Simple generic with one type parameter"
                    ),
                    ExtractGenericTypesTestCase.of(
                            "Map<String, Integer>",
                            List.of("String", "Integer"),
                            "Simple generic with two type parameters"
                    ),
                    ExtractGenericTypesTestCase.of(
                            "Tuple<String, Integer, Boolean>",
                            List.of("String", "Integer", "Boolean"),
                            "Simple generic with three type parameters"
                    )
            ).map(tc -> Arguments.of(tc.typeName(), tc.expectedGenericTypes(), tc.description()));
        }

        private static Stream<Arguments> complexGenericTypes() {
            return Stream.of(
                    ExtractGenericTypesTestCase.of(
                            "Map<String, List<Integer>>",
                            List.of("String", "List<Integer>"),
                            "Nested generic types"
                    ),
                    ExtractGenericTypesTestCase.of(
                            "Function<T, R extends Comparable<R>>",
                            List.of("T", "R extends Comparable<R>"),
                            "Generic with bounded type parameter"
                    ),
                    ExtractGenericTypesTestCase.of(
                            "Pair<? extends Number, ? super Integer>",
                            List.of("? extends Number", "? super Integer"),
                            "Generic with wildcard bounds"
                    ),
                    ExtractGenericTypesTestCase.of(
                            "Map<K extends Comparable<K>, V extends Serializable>",
                            List.of("K extends Comparable<K>", "V extends Serializable"),
                            "Multiple bounded type parameters"
                    ),
                    ExtractGenericTypesTestCase.of(
                            "Wrapper<Map<String, List<Set<Integer>>>>",
                            List.of("Map<String, List<Set<Integer>>>"),
                            "Deeply nested generic types"
                    )
            ).map(tc -> Arguments.of(tc.typeName(), tc.expectedGenericTypes(), tc.description()));
        }

        private static Stream<Arguments> edgeCases() {
            return Stream.of(
                    ExtractGenericTypesTestCase.of(
                            "SimpleClass",
                            List.of(),
                            "Non-generic class"
                    ),
                    ExtractGenericTypesTestCase.of(
                            "List<>",
                            List.of(),
                            "Generic with empty type parameter"
                    ),
                    ExtractGenericTypesTestCase.of(
                            "Map<,>",
                            List.of("", ""),
                            "Generic with empty multiple type parameters"
                    ),
                    ExtractGenericTypesTestCase.of(
                            "Class<<T>>",
                            List.of("<T>"),
                            "Generic with extra angle brackets"
                    ),
                    ExtractGenericTypesTestCase.of(
                            "Pair<T, U> implements Comparable<Pair<T, U>>",
                            List.of("T", "U"),
                            "Generic with implementation clause"
                    )
            ).map(tc -> Arguments.of(tc.typeName(), tc.expectedGenericTypes(), tc.description()));
        }
    }
}