package agentic.workflow;

import agentic.workflow.llm.SchemaType;
import agentic.workflow.llm.StructuredOutput;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Agent {
    private String name;
    private final List<WorkflowStep> steps;

    public Agent(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("A név nem lehet üres.");
        }
        this.name = name;
        this.steps = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("A név nem lehet üres.");
        }
        this.name = name;
    }

    public List<WorkflowStep> getSteps() {
        return new ArrayList<>(this.steps);
    }

    public int getStepCount() {
        return this.steps.size();
    }

    public void addStep(WorkflowStep step) {
        if (step == null) {
            throw new IllegalArgumentException("A lépés nem lehet null.");
        }
        
        for (WorkflowStep vizsgalt : this.steps) {
            if (vizsgalt.getName().equals(step.getName())) {
                throw new IllegalArgumentException("Már létezik ilyen nevű lépés: " + step.getName());
            }
        }
        
        this.steps.add(step);
    }

    public WorkflowStep findStepByName(String stepName) {
        if (stepName == null || stepName.trim().isEmpty()) {
            throw new IllegalArgumentException("A keresett név nem lehet üres.");
        }
        
        String tisztaNev = stepName.trim();
        for (WorkflowStep vizsgalt : this.steps) {
            if (vizsgalt.getName().equals(tisztaNev)) {
                return vizsgalt;
            }
        }
        return null;
    }

    public void run() {
        for (WorkflowStep step : this.steps) {
            System.out.println("Lépés neve: " + step.getName());
            System.out.println("Szimulált válasz: " + step.simulateResponse());
        }
    }

    public static Agent loadAgent(String filename) throws IOException, WorkflowFormatException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("A fájlnév nem lehet üres.");
        }

        // Try-with-resources: biztosítja a fájl bezárását hiba esetén is
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String elsoSor = reader.readLine();
            if (elsoSor == null) {
                throw new WorkflowFormatException("A fájl üres.");
            }
            
            elsoSor = elsoSor.trim();
            if (!elsoSor.startsWith("AGENT:")) {
                throw new WorkflowFormatException("Hiányzik az AGENT: fejléc.");
            }
            
            Agent agent = new Agent(elsoSor.substring(6).trim());
            
            String sor;
            while ((sor = reader.readLine()) != null) {
                sor = sor.trim();
                if (sor.isEmpty()) continue;
                
                if (sor.equals("STEP")) {
                    WorkflowStep ujLepes = parseStep(reader);
                    // Duplikáció ellenőrzése betöltéskor is
                    if (agent.findStepByName(ujLepes.getName()) != null) {
                        throw new WorkflowFormatException("Duplikált lépésnév: " + ujLepes.getName());
                    }
                    agent.addStep(ujLepes);
                } else {
                    throw new WorkflowFormatException("Váratlan sor: " + sor);
                }
            }
            return agent;
        }
    }

    private static WorkflowStep parseStep(BufferedReader reader) throws IOException, WorkflowFormatException {
        String name = null;
        String prompt = null;
        String systemPrompt = null;
        String outputStr = null;
        boolean endStepFound = false;
        
        String sor;
        while ((sor = reader.readLine()) != null) {
            sor = sor.trim();
            if (sor.isEmpty()) continue;
            
            if (sor.equals("ENDSTEP")) {
                endStepFound = true;
                break;
            }
            
            int egyenlosegHelye = sor.indexOf('=');
            if (egyenlosegHelye == -1) {
                throw new WorkflowFormatException("Nincs '=' jel a sorban: " + sor);
            }
            
            String kulcs = sor.substring(0, egyenlosegHelye).trim();
            String ertek = sor.substring(egyenlosegHelye + 1).trim();
            
            switch (kulcs) {
                case "name" -> name = ertek;
                case "prompt" -> prompt = ertek;
                case "systemPrompt" -> systemPrompt = ertek;
                case "output" -> outputStr = ertek;
                default -> throw new WorkflowFormatException("Ismeretlen kulcs: " + kulcs);
            }
        }
        
        if (!endStepFound) {
            throw new WorkflowFormatException("Hianyozik az ENDSTEP a fajl vegerol.");
        }
        
        if (name == null || prompt == null || systemPrompt == null || outputStr == null) {
            throw new WorkflowFormatException("Hiányzó adat a lépésben.");
        }
        
        SchemaType st;
        try {
            st = SchemaType.valueOf(outputStr);
        } catch (IllegalArgumentException e) {
            throw new WorkflowFormatException("Rossz output típus: " + outputStr);
        }
        
        StructuredOutput so = new StructuredOutput(new SchemaType[]{st});
        return new WorkflowStep(name, prompt, systemPrompt, so);
    }
}