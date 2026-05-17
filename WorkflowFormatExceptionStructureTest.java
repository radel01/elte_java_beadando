import check.*;
import static check.Use.*;

import module org.junit.jupiter;

@BeforeAll
public static void init() {
    // usedLang = Lang.EN; // uncomment to enforce the message language
    Use.theCheckedException("agentic.workflow.WorkflowFormatException")
       .that(hasUsualModifiers())
       .info("Akkor dobódik, ha egy workflow leíró fájl vagy annak tartalma hibás formátumú.")
       .info("Ez egy ellenőrzött kivétel, amely jelzi, hogy a hívónak külön kezelnie kell a hibás workflow bemenetet.");
}

@Test
public void constructor01() {
    it.hasConstructor(withParams("message: String"))
      .thatIs(VISIBLE_TO_ALL)
      .info("Létrehozza a kivételt egy magyarázó hibaüzenettel.");
}

@Test
public void constructor02() {
    it.hasConstructor(withParams("message: String", "cause: Throwable"))
      .thatIs(VISIBLE_TO_ALL)
      .info("Létrehozza a kivételt egy magyarázó hibaüzenettel és az eredeti okot leíró kivétellel.")
      .info("Hasznos akkor, amikor a workflow feldolgozása egy alacsonyabb szintű kivétel miatt hiúsul meg.");
}

void main() {}


