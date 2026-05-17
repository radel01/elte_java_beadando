package agentic.workflow;

import agentic.workflow.llm.SchemaType;
import agentic.workflow.llm.StructuredOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AgentTest {

    // Segédmetódus lépés létrehozásához
    private WorkflowStep createDummyStep(String name) {
        StructuredOutput out = new StructuredOutput(new SchemaType[]{SchemaType.STRING});
        return new WorkflowStep(name, "prompt", "sys", out);
    }

    @Test
    public void testValidCreationAndNameValidation() {
        Agent agent = new Agent("Teszt Agens");
        assertEquals("Teszt Agens", agent.getName());

        assertThrows(IllegalArgumentException.class, () -> new Agent(null), "Null név esetén kivétel kell.");
        assertThrows(IllegalArgumentException.class, () -> new Agent("   "), "Üres név esetén kivétel kell.");
    }

    @Test
    public void testAddAndFindStep() {
        Agent agent = new Agent("Agens");
        WorkflowStep step1 = createDummyStep("Lepes1");
        agent.addStep(step1);

        assertEquals(1, agent.getStepCount());
        assertEquals(step1, agent.findStepByName("Lepes1"));
        assertNull(agent.findStepByName("NemLetezo"));
    }

    @Test
    public void testAddDuplicateStepRejected() {
        Agent agent = new Agent("Agens");
        WorkflowStep step1 = createDummyStep("KozosNev");
        WorkflowStep step2 = createDummyStep("KozosNev");

        agent.addStep(step1);
        assertThrows(IllegalArgumentException.class, () -> agent.addStep(step2), "Duplikált lépésnév hozzáadása kivételt kell dobjon.");
    }

    @Test
    public void testEncapsulationGetSteps() {
        Agent agent = new Agent("Agens");
        agent.addStep(createDummyStep("Lepes1"));

        List<WorkflowStep> stepsCopy = agent.getSteps();
        try {
            stepsCopy.clear(); 
        } catch (UnsupportedOperationException e) {
        }

        assertEquals(1, agent.getStepCount(), "Enkapszulációs hiba: A getSteps() módosítása nem érintheti az eredetit!");
    }

    @Test
    public void testLoadAgentSuccess(@TempDir Path tempDir) throws IOException, WorkflowFormatException {
        Path file = tempDir.resolve("valid_agent.txt");
        List<String> lines = List.of(
            "AGENT: Hotel Booker",
            "STEP",
            "name=Kereses",
            "prompt=Keress hotelt",
            "systemPrompt=Te egy utazasi ugynok vagy",
            "output=STRING",
            "ENDSTEP"
        );
        Files.write(file, lines);

        Agent agent = Agent.loadAgent(file.toString());
        assertEquals("Hotel Booker", agent.getName());
        assertEquals(1, agent.getStepCount());
    }

    @Test
    public void testLoadAgentRejectsMissingHeader(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("no_header.txt");
        Files.write(file, List.of("STEP", "name=Hiba", "ENDSTEP"));

        assertThrows(WorkflowFormatException.class, () -> Agent.loadAgent(file.toString()), 
            "Ha nincs AGENT sor az elején, WorkflowFormatException-t kell dobni.");
    }

    @Test
    public void testLoadAgentRejectsDuplicateStepNames(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("dup_steps.txt");
        Files.write(file, List.of(
            "AGENT: Test",
            "STEP", "name=A", "prompt=p", "systemPrompt=s", "output=INT", "ENDSTEP",
            "STEP", "name=A", "prompt=p", "systemPrompt=s", "output=INT", "ENDSTEP"
        ));

        assertThrows(WorkflowFormatException.class, () -> Agent.loadAgent(file.toString()), 
            "Fájlból való betöltéskor a duplikált neveknek hibát kell dobniuk.");
    }
    
    @Test
    public void testLoadAgentInvalidFormatMissingEndStep(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("missing_end.txt");
        Files.write(file, List.of(
            "AGENT: Rossz",
            "STEP", "name=Hiba", "prompt=p", "systemPrompt=s", "output=STRING"
        ));

        assertThrows(WorkflowFormatException.class, () -> Agent.loadAgent(file.toString()));
    }


    @Test
    public void findStepByName() {
        Agent agent = new Agent("Agens");
        WorkflowStep step1 = createDummyStep("Lepes1");
        agent.addStep(step1);
        assertEquals(step1, agent.findStepByName("Lepes1"));
    }

    @Test
    public void findStepByNameMissing() {
        Agent agent = new Agent("Teszt");
        assertNull(agent.findStepByName("NincsIlyenLepes"));
    }

    @Test
    public void testStepCount() {
        Agent agent = new Agent("Agens");
        assertEquals(0, agent.getStepCount());
        agent.addStep(createDummyStep("Step1"));
        assertEquals(1, agent.getStepCount());
    }

    @Test
    public void testAddStep() {
        Agent agent = new Agent("Agens");
        WorkflowStep step = createDummyStep("Step1");
        agent.addStep(step);
        assertEquals(step, agent.findStepByName("Step1"));
    }

}