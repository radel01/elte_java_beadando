import check.*;
import static check.Use.*;

import module org.junit.jupiter;

@BeforeAll
public static void init() {
    // usedLang = Lang.EN; // uncomment to enforce the message language
    Use.theClass("agentic.workflow.WorkflowStep")
       .that(hasUsualModifiers());
}

@Test
public void fieldName() {
    it.hasField("name: String")
      .that(hasUsualModifiers())
      .thatHas(GETTER, SETTER)
      .info("A lépés neve.");
}

@Test
public void fieldPrompt() {
    it.hasField("prompt: String")
      .that(hasUsualModifiers())
      .thatHas(GETTER, SETTER)
      .info("A felhasználónak szóló prompt, amely leírja, mit kell tennie ennek a lépésnek.");
}

@Test
public void fieldSystemPrompt() {
    it.hasField("systemPrompt: String")
      .that(hasUsualModifiers())
      .thatHas(GETTER, SETTER)
      .info("A lépéshez tartozó rendszerprompt.");
}

@Test
public void fieldStructuredOutput() {
    it.hasField("structuredOutput: agentic.workflow.llm.StructuredOutput")
      .that(hasUsualModifiers())
      .thatHas(GETTER, SETTER)
      .info("A lépéstől elvárt strukturált kimenet.");
}

@Test
public void constructor() {
    it.hasConstructor(withArgsLikeAllFields())
      .thatIs(VISIBLE_TO_ALL)
      .thatThrows("IllegalArgumentException", "a `name`, `prompt` és `systemPrompt` nem lehet üres, a `structuredOutput` pedig nem lehet `null`.")
      .info("Létrehoz egy workflow lépést ellenőrzött mezőkkel.");
}

@Test
public void methodExpectsStructuredOutput() {
    it.hasMethod("expectsStructuredOutput", withNoParams())
      .that(hasUsualModifiers())
      .thatReturns("boolean", """
          `true`, ha a lépéshez legalább egy sématípus tartozik, különben `false`.
          Mivel a `StructuredOutput` legalább egy sématípust megkövetel, egy érvényes lépésnél várhatóan `true` az eredmény.
      """)
      .testWith(testCase("testExpectsStructuredOutput"), "Ellenőrzi, hogy a strukturált kimenettel rendelkező lépés esetén `true` a válasz.");
}

@Test
public void methodSimulateResponse() {
    it.hasMethod("simulateResponse", withNoParams())
      .that(hasUsualModifiers())
      .thatReturns("String")
      .info("""
          Egy egyszerű szimulált választ ad vissza az elsőként megadott sématípus alapján.
          Az INT-re adott válasz `"0"`, a STRING `"sample"`, a BOOLEAN `"true"`, a LIST_INT `"[1,2,3]"`, a LIST_STRING `"[\"a\",\"b\"]"`, a MAP_STRING_STRING pedig `"{\"kulcs\":\"érték\"}"` mintaválaszt eredményez.
          test testSimulateResponseByPrimaryType Ellenőrzi, hogy a visszaadott válasz az első sématípusnak felel meg.
      """);
}

void main() {}


