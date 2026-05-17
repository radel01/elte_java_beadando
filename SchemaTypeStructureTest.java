import check.*;
import static check.Use.*;

import module org.junit.jupiter;

@Test
public void elements() {
    Use.theEnum("agentic.workflow.llm.SchemaType")
       .ofEnumElements("INT", "STRING", "BOOLEAN", "LIST_INT", "LIST_STRING", "MAP_STRING_STRING")
       .that(hasUsualModifiers())
       .info("A StrucutedOutput lehetséges típusai.")
       .info("Az INT, STRING és BOOLEAN egyszerű skalár értékeket jelentenek.")
       .info("A LIST_INT és LIST_STRING egész számokat vagy szövegeket tartalmazó listákat jelentenek.")
       .info("A MAP_STRING_STRING olyan leképezést jelent, amelynek kulcsai és értékei is szövegek.");
}

void main() {}


