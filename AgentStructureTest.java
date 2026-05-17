import check.*;
import static check.Use.*;

import module org.junit.jupiter;

@BeforeAll
public static void init() {
    // usedLang = Lang.EN; // uncomment to enforce the message language
    Use.theClass("agentic.workflow.Agent")
       .that(hasUsualModifiers())
       .info("""
           Egy ágenst reprezentál, amely workflow lépések rendezett listájából épül fel.
           Az ágens létrehozható kézzel, vagy betölthető szövegfájlból.
       """);
}

@Test
public void fieldName() {
    it.hasField("name: String")
      .that(hasUsualModifiers())
      .thatHas(GETTER, SETTER)
      .info("Az ágens neve.");
}

@Test
public void fieldSteps() {
    it.hasField("steps: List of WorkflowStep")
      .thatIs(INSTANCE_LEVEL, FULLY_IMPLEMENTED, NOT_MODIFIABLE, VISIBLE_TO_NONE)
      .thatHas(GETTER)
      .thatHasNo(SETTER)
      .info("Az ágenshez tartozó workflow lépések rendezett listája.")
      .info("A steps példány nem szivároghat ki az osztályon kívülre.");
}

@Test
public void constructor() {
    it.hasConstructor(withArgsLikeFields("name"))
      .thatIs(VISIBLE_TO_ALL)
      .thatThrows("IllegalArgumentException", "a név nem lehet `null`, üres vagy csak szóközökből álló.")
      .info("Létrehoz egy ágenst a megadott, ellenőrzött névvel, kezdetben lépések nélkül.");
}

@Test
public void methodAddStep() {
    it.hasMethod("addStep", withParams("step: WorkflowStep"))
      .that(hasUsualModifiers())
      .thatReturnsNothing()
      .thatThrows("IllegalArgumentException", "a lépés nem lehet `null`, és nem létezhet már másik lépés ugyanazzal a névvel.")
      .info("""
          Új lépést ad a workflow végéhez.
          A lépésneveknek egy adott ágensen belül egyedinek kell lenniük.
      """)
      .testWith(testCase("testStepCount"), "Ellenőrzi, hogy egy lépés hozzáadása után a tárolt lépések száma növekszik.")
      .testWith(testCase("testAddDuplicateStepRejected"), "Ellenőrzi, hogy azonos nevű lépések nem elfogadhatók.");
}

@Test
public void methodGetStepCount() {
    it.hasMethod("getStepCount", withNoParams())
      .that(hasUsualModifiers())
      .thatReturns("int", "Az ágensben tárolt workflow lépések száma.")
      .testWith(testCase("testStepCount"), "Ellenőrzi, hogy a lépésszám megegyezik a hozzáadott lépések számával.");
}

@Test
public void methodFindStepByName() {
    it.hasMethod("findStepByName", withParams("stepName: String"))
      .that(hasUsualModifiers())
      .thatReturns("WorkflowStep", "A megtalált lépés, vagy `null`, ha nincs ilyen.")
      .thatThrows("IllegalArgumentException", "a lépés neve nem lehet `null`, üres vagy csak szóközökből álló.")
      .info("""
          `WorkflowStep` név szerinti keresése.
          Az összehasonlítás levágja a paraméter a kezdő- és záró szóközeit, majd pontos szövegegyezéssel keres.
      """)
      .testWith(testCase("findStepByName"), "Ellenőrzi, hogy egy meglévő lépés név alapján megtalálható.")
      .testWith(testCase("findStepByNameMissing"), "Ellenőrzi, hogy hiányzó lépés esetén `null` az eredmény.");
}

@Test
public void methodRun() {
    it.hasMethod("run", withNoParams())
      .that(hasUsualModifiers())
      .thatReturnsNothing()
      .info("Az ágens teljes futását szimulálja úgy, hogy kiírja minden lépés nevét a kapott mintaválaszt.")
      .info("A lépések a hozzáadás sorrendjében kerülnek feldolgozásra.");
}

@Test
public void methodLoadAgent() {
    it.hasMethod("loadAgent", withParams("filename: String"))
      .thatIs(USABLE_WITHOUT_INSTANCE, FULLY_IMPLEMENTED, MODIFIABLE, VISIBLE_TO_ALL)
      .thatReturns("Agent", "A betöltött ágens.")
      .thatThrows("IllegalArgumentException", "a fájlnév nem lehet `null`, üres vagy csak szóközökből álló.")
      .thatThrows("WorkflowFormatException", "ha a fájl tartalma hibás formátumú.")
      .thatThrows("IOException", "ha a fájl nem olvasható.")
      .info("""
          Betölt egy ágenst egy workflow leíró szövegfájlból.
          Az első nem üres sornak `AGENT: ...` formájúnak kell lennie.
          Minden lépés `STEP` sorral kezdődik és `ENDSTEP` sorral végződik.
          Az azonos lépésnevek nem megengedettek.
      """)
      .testWith(testCase("testLoadAgentSuccess"), "Ellenőrzi, hogy egy érvényes workflow fájl sikeresen betölthető.")
      .testWith(testCase("testLoadAgentRejectsMissingHeader"), "Ellenőrzi, hogy a hiányzó AGENT fejléc formátumhibát okoz.")
      .testWith(testCase("testLoadAgentRejectsDuplicateStepNames"), "Ellenőrzi, hogy a fájlban lévő azonos lépésnevek nem elfogadhatók.");
}

@Test
public void methodParseStep() {
    it.hasMethod("parseStep", withParams("reader: BufferedReader"))
      .thatIs(USABLE_WITHOUT_INSTANCE, FULLY_IMPLEMENTED, MODIFIABLE, VISIBLE_TO_NONE)
      .thatReturns("WorkflowStep", "A feldolgozott workflow lépés.")
      .thatThrows("IOException", "ha a fájl olvasása meghiúsul.")
      .thatThrows("WorkflowFormatException", "ha a lépés tartalma hibás vagy hiányos.")
      .info("""
          Egyetlen lépést olvas a readerből, egészen az `ENDSTEP` sorig.
          A kötelező tulajdonságok: `name`, `prompt`, `systemPrompt` és `output`.
          Az ismeretlen tulajdonságok és a hibás tulajdonságsorok formátumhibát okoznak.
          Az `output` értékének meg kell egyeznie a `SchemaType` enum valamelyik elemével.
      """);
}

void main() {}


