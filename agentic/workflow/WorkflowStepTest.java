package agentic.workflow;

import agentic.workflow.llm.SchemaType;
import agentic.workflow.llm.StructuredOutput;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WorkflowStepTest {

    private StructuredOutput dummy(SchemaType t) {
        return new StructuredOutput(new SchemaType[]{t});
    }

    @Test
    public void testValidCreationAndGetters() {
        StructuredOutput o = dummy(SchemaType.STRING);
        WorkflowStep s = new WorkflowStep("l1", "p1", "s1", o);
        
        assertEquals("l1", s.getName(), "nev hiba");
        assertEquals("p1", s.getPrompt(), "prompt hiba");
        assertEquals("s1", s.getSystemPrompt(), "sysprompt hiba");
        assertEquals(o, s.getStructuredOutput(), "output hiba");
        assertTrue(s.expectsStructuredOutput(), "kene hogy varjon outputot");
    }

    @Test
    public void testNameValidation() {
        StructuredOutput o = dummy(SchemaType.STRING);
        assertThrows(IllegalArgumentException.class, () -> new WorkflowStep(null, "p", "s", o), "null nev hiba");
        assertThrows(IllegalArgumentException.class, () -> new WorkflowStep("  ", "p", "s", o), "ures nev hiba");
    }

    @Test
    public void testPromptValidation() {
        StructuredOutput o = dummy(SchemaType.STRING);
        assertThrows(IllegalArgumentException.class, () -> new WorkflowStep("n", null, "s", o));
        assertThrows(IllegalArgumentException.class, () -> new WorkflowStep("n", "", "s", o));
    }

    @Test
    public void testSystemPromptValidation() {
        StructuredOutput o = dummy(SchemaType.STRING);
        assertThrows(IllegalArgumentException.class, () -> new WorkflowStep("n", "p", null, o));
    }

    @Test
    public void testStructuredOutputValidation() {
        assertThrows(IllegalArgumentException.class, () -> new WorkflowStep("n", "p", "s", null), "null kimenet hiba");
    }

    @Test
    public void testSimulateResponse() {
        WorkflowStep s1 = new WorkflowStep("n", "p", "s", dummy(SchemaType.INT));
        assertEquals("0", s1.simulateResponse());

        WorkflowStep s2 = new WorkflowStep("n", "p", "s", dummy(SchemaType.STRING));
        assertEquals("sample", s2.simulateResponse());

        WorkflowStep s3 = new WorkflowStep("n", "p", "s", dummy(SchemaType.BOOLEAN));
        assertEquals("true", s3.simulateResponse());

        WorkflowStep s4 = new WorkflowStep("n", "p", "s", dummy(SchemaType.LIST_INT));
        assertEquals("[1,2,3]", s4.simulateResponse());
    }

    @Test
    public void testExpectsStructuredOutput() {
        StructuredOutput o = dummy(SchemaType.STRING);
        WorkflowStep s = new WorkflowStep("s1", "p", "s", o);
        assertTrue(s.expectsStructuredOutput(), "visszajelzes hiba");
    }
}