import check.*;
import static check.Use.*;

import module org.junit.jupiter;

@BeforeAll
public static void init() {
    // usedLang = Lang.EN; // uncomment to enforce the message language
    Use.theClass("agentic.workflow.llm.StructuredOutput")
       .that(hasUsualModifiers());
}

@Test
public void fieldSchemaTypes() {
    it.hasField("schemaTypes: array of SchemaType")
      .thatIs(INSTANCE_LEVEL, FULLY_IMPLEMENTED, NOT_MODIFIABLE, VISIBLE_TO_NONE)
      .thatHas(GETTER)
      .thatHasNo(SETTER)
      .info("A StructuredOutput sématípusait tárolja.")
      .info("A tömb közvetlenül soha nem kerül kiadásra; a példány adatszivárgásának elkerülésére használj védő másolatokat.");
}

@Test
public void constructor() {
    it.hasConstructor(withArgsLikeAllFields())
      .thatIs(VISIBLE_TO_ALL)
      .thatThrows("IllegalArgumentException", "legalább egy sématípust meg kell adni.")
      .thatThrows("NullPointerException", "a megadott sématípusok között nem lehet `null`.")
      .info("Létrehoz egy StructuredOutput példányt egy vagy több sématípus tárolására.");
}

@Test
public void methodContains() {
    it.hasMethod("contains", withParams("schemaType: SchemaType"))
      .that(hasUsualModifiers())
      .thatReturns("boolean", "`true`, ha a megadott sématípus szerepel ebben a példányban, különben `false`.")
      .info("Az összehasonlítás enum azonosság alapján történik.")
      .testWith(testCase("testContainsExistingType"), "Ellenőrzi, hogy a benne levő sématípus megtalálható.")
      .testWith(testCase("testContainsMissingType"), "Ellenőrzi, hogy a nem szereplő sématípus nem található meg.");
}

@Test
public void methodSize() {
    it.hasMethod("size", withNoParams())
      .that(hasUsualModifiers())
      .thatReturns("int", "A tárolt sématípusok száma.")
      .testWith(testCase("testSize"), "Ellenőrzi, hogy a méret megegyezik a konstruktor argumentumainak számával.");
}

void main() {}


