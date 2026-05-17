package agentic.workflow;

import agentic.workflow.llm.SchemaType;
import agentic.workflow.llm.StructuredOutput;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WorkflowStepTest {

    private StructuredOutput createDummyOutput(SchemaType type) {
        return new StructuredOutput(new SchemaType[]{type});
    }

    @Test
    public void testValidCreationAndGetters() {
        StructuredOutput out = createDummyOutput(SchemaType.STRING);
        WorkflowStep step = new WorkflowStep("Lépés1", "Futtass valamit", "Te egy AI vagy", out);
        
        assertEquals("Lépés1", step.getName(), "A name getter hibás.");
        assertEquals("Futtass valamit", step.getPrompt(), "A prompt getter hibás.");
        assertEquals("Te egy AI vagy", step.getSystemPrompt(), "A systemPrompt getter hibás.");
        assertEquals(out, step.getStructuredOutput(), "A structuredOutput getter hibás.");
        assertTrue(step.expectsStructuredOutput(), "Az expectsStructuredOutput()-nak true-t kell adnia, ha van kimenet.");
    }

    @Test
    public void testNameValidation() {
        StructuredOutput out = createDummyOutput(SchemaType.STRING);
        
        assertThrows(IllegalArgumentException.class, () -> {
            new WorkflowStep(null, "prompt", "sysPrompt", out);
        }, "Null name esetén kivételt kell dobni.");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new WorkflowStep("   ", "prompt", "sysPrompt", out);
        }, "Csupa szóköz name esetén kivételt kell dobni.");
    }

    @Test
    public void testPromptValidation() {
        StructuredOutput out = createDummyOutput(SchemaType.STRING);
        
        assertThrows(IllegalArgumentException.class, () -> {
            new WorkflowStep("name", null, "sysPrompt", out);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new WorkflowStep("name", "", "sysPrompt", out);
        });
    }

    @Test
    public void testSystemPromptValidation() {
        StructuredOutput out = createDummyOutput(SchemaType.STRING);
        
        assertThrows(IllegalArgumentException.class, () -> {
            new WorkflowStep("name", "prompt", null, out);
        });
    }

    @Test
    public void testStructuredOutputValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new WorkflowStep("name", "prompt", "sysPrompt", null);
        }, "Null StructuredOutput esetén kivételt kell dobni.");
    }

    @Test
    public void testSimulateResponse() {
        // Kipróbáljuk a különböző dummy értékeket a metódusod alapján
        WorkflowStep stepInt = new WorkflowStep("n", "p", "sp", createDummyOutput(SchemaType.INT));
        assertEquals("0", stepInt.simulateResponse());

        WorkflowStep stepStr = new WorkflowStep("n", "p", "sp", createDummyOutput(SchemaType.STRING));
        assertEquals("sample", stepStr.simulateResponse());

        WorkflowStep stepBool = new WorkflowStep("n", "p", "sp", createDummyOutput(SchemaType.BOOLEAN));
        assertEquals("true", stepBool.simulateResponse());

        WorkflowStep stepListInt = new WorkflowStep("n", "p", "sp", createDummyOutput(SchemaType.LIST_INT));
        assertEquals("[1,2,3]", stepListInt.simulateResponse());
    }

    @Test
    public void testExpectsStructuredOutput() {
        StructuredOutput out = createDummyOutput(SchemaType.STRING);
        WorkflowStep step = new WorkflowStep("Step1", "Prompt", "System", out);
        
        assertTrue(step.expectsStructuredOutput(), "A metódusnak true-t kell visszaadnia, ha van StructuredOutput.");
    }
}