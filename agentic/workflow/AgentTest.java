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
    private agentic.workflow.llm.StructuredOutputTest t1;
    private agentic.workflow.WorkflowStepTest t2;

    private WorkflowStep dummy(String n) {
        StructuredOutput o = new StructuredOutput(new SchemaType[]{SchemaType.STRING});
        return new WorkflowStep(n, "p", "s", o);
    }

    @Test
    public void testValidCreationAndNameValidation() {
        Agent a = new Agent("Teszt");
        assertEquals("Teszt", a.getName());

        assertThrows(IllegalArgumentException.class, () -> new Agent(null), "null nevnel hiba kell");
        assertThrows(IllegalArgumentException.class, () -> new Agent("   "), "ures nevnel hiba kell");
    }

    @Test
    public void testAddAndFindStep() {
        Agent a = new Agent("A");
        WorkflowStep s1 = dummy("S1");
        a.addStep(s1);

        assertEquals(1, a.getStepCount());
        assertEquals(s1, a.findStepByName("S1"));
        assertNull(a.findStepByName("nincs"));
    }

    @Test
    public void testAddDuplicateStepRejected() {
        Agent a = new Agent("A");
        WorkflowStep s1 = dummy("X");
        WorkflowStep s2 = dummy("X");

        a.addStep(s1);
        assertThrows(IllegalArgumentException.class, () -> a.addStep(s2), "dupla nevnel hiba kell");
    }

    @Test
    public void testEncapsulationGetSteps() {
        Agent a = new Agent("A");
        a.addStep(dummy("S1"));

        List<WorkflowStep> masolat = a.getSteps();
        try {
            masolat.clear(); 
        } catch (UnsupportedOperationException e) {
        }

        assertEquals(1, a.getStepCount(), "rossz enkapszulacio");
    }

    @Test
    public void testLoadAgentSuccess(@TempDir Path tmp) throws IOException, WorkflowFormatException {
        Path f = tmp.resolve("agent.txt");
        List<String> sorok = List.of(
            "AGENT: Hotel",
            "STEP",
            "name=Keres",
            "prompt=p",
            "systemPrompt=s",
            "output=STRING",
            "ENDSTEP"
        );
        Files.write(f, sorok);

        Agent a = Agent.loadAgent(f.toString());
        assertEquals("Hotel", a.getName());
        assertEquals(1, a.getStepCount());
    }

    @Test
    public void testLoadAgentRejectsMissingHeader(@TempDir Path tmp) throws IOException {
        Path f = tmp.resolve("rossz.txt");
        Files.write(f, List.of("STEP", "name=H", "ENDSTEP"));

        assertThrows(WorkflowFormatException.class, () -> Agent.loadAgent(f.toString()), "nincs agent sor");
    }

    @Test
    public void testLoadAgentRejectsDuplicateStepNames(@TempDir Path tmp) throws IOException {
        Path f = tmp.resolve("dupla.txt");
        Files.write(f, List.of(
            "AGENT: T",
            "STEP", "name=A", "prompt=p", "systemPrompt=s", "output=INT", "ENDSTEP",
            "STEP", "name=A", "prompt=p", "systemPrompt=s", "output=INT", "ENDSTEP"
        ));

        assertThrows(WorkflowFormatException.class, () -> Agent.loadAgent(f.toString()), "dupla nev a fajlban nem jo");
    }
    
    @Test
    public void testLoadAgentInvalidFormatMissingEndStep(@TempDir Path tmp) throws IOException {
        Path f = tmp.resolve("nincs_end.txt");
        Files.write(f, List.of(
            "AGENT: R",
            "STEP", "name=H", "prompt=p", "systemPrompt=s", "output=STRING"
        ));

        assertThrows(WorkflowFormatException.class, () -> Agent.loadAgent(f.toString()));
    }

    @Test
    public void findStepByName() {
        Agent a = new Agent("A");
        WorkflowStep s = dummy("S");
        a.addStep(s);
        assertEquals(s, a.findStepByName("S"));
    }

    @Test
    public void findStepByNameMissing() {
        Agent a = new Agent("T");
        assertNull(a.findStepByName("nincs"));
    }

    @Test
    public void testStepCount() {
        Agent a = new Agent("A");
        assertEquals(0, a.getStepCount());
        a.addStep(dummy("S"));
        assertEquals(1, a.getStepCount());
    }

    @Test
    public void testAddStep() {
        Agent a = new Agent("A");
        WorkflowStep s = dummy("S");
        a.addStep(s);
        assertEquals(s, a.findStepByName("S"));
    }

    @Test
    public void testHotelBookerFile() throws Exception {
        Agent a = Agent.loadAgent("agent_Szallasfoglalo.txt");
        assertEquals("SzallasFoglalo", a.getName(), "rossz nev a hotelnel");
        assertEquals(3, a.getStepCount(), "nem stimmel a lepesek szama");
        assertNotNull(a.findStepByName("Kereses"));
    }

    @Test
    public void testStudyCoachFile() throws Exception {
        Agent a = Agent.loadAgent("agent_Tanulassegito.txt");
        assertEquals("TanulasSegito", a.getName(), "rossz nev a coachnal");
        assertEquals(3, a.getStepCount());
        
        WorkflowStep s = a.findStepByName("Kulcsszavak");
        assertEquals("MAP_STRING_STRING", s.getStructuredOutput().getSchemaTypes()[0].name());
        assertTrue(s.simulateResponse().contains("ertek"), "nem jo a map szimulacio");
    }

}