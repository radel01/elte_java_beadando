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
            throw new IllegalArgumentException("ures vagy null nevet adtal meg");
        }
        this.name = name;
        this.steps = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("ures vagy null nevet adtal meg");
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
            throw new IllegalArgumentException("null lepest nem lehet hozzaadni");
        }
        
        for (WorkflowStep v : this.steps) {
            if (v.getName().equals(step.getName())) {
                throw new IllegalArgumentException("mar van ilyen nevu lepes");
            }
        }
        
        this.steps.add(step);
    }

    public WorkflowStep findStepByName(String stepName) {
        if (stepName == null || stepName.trim().isEmpty()) {
            throw new IllegalArgumentException("ures nevet kerestel");
        }
        
        String tNev = stepName.trim();
        for (WorkflowStep v : this.steps) {
            if (v.getName().equals(tNev)) {
                return v;
            }
        }
        return null;
    }

    public void run() {
        for (WorkflowStep s : this.steps) {
            System.out.println("Lépés neve: " + s.getName());
            System.out.println("Szimulált válasz: " + s.simulateResponse());
        }
    }

    public static Agent loadAgent(String filename) throws IOException, WorkflowFormatException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("nincs megadva fajlnev");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String fejlec = br.readLine();
            if (fejlec == null) {
                throw new WorkflowFormatException("ures a fajl");
            }
            
            fejlec = fejlec.trim();
            if (!fejlec.startsWith("AGENT:")) {
                throw new WorkflowFormatException("nincs agent fejlec az elejen");
            }
            
            Agent a = new Agent(fejlec.substring(6).trim());
            
            String sor;
            while ((sor = br.readLine()) != null) {
                sor = sor.trim();
                if (sor.isEmpty()) continue;
                
                if (sor.equals("STEP")) {
                    WorkflowStep uj = parseStep(br);
                    if (a.findStepByName(uj.getName()) != null) {
                        throw new WorkflowFormatException("duplikalt lepes a fajlban");
                    }
                    a.addStep(uj);
                } else {
                    throw new WorkflowFormatException("varatlan sor jott");
                }
            }
            return a;
        }
    }

    private static WorkflowStep parseStep(BufferedReader br) throws IOException, WorkflowFormatException {
        String n = null;
        String p = null;
        String sp = null;
        String out = null;
        boolean megvan = false;
        
        String sor;
        while ((sor = br.readLine()) != null) {
            sor = sor.trim();
            if (sor.isEmpty()) continue;
            
            if (sor.equals("ENDSTEP")) {
                megvan = true;
                break;
            }
            
            int egyenlo = sor.indexOf('=');
            if (egyenlo == -1) {
                throw new WorkflowFormatException("nincs egyenlosegjel");
            }
            
            String kulcs = sor.substring(0, egyenlo).trim();
            String ertek = sor.substring(egyenlo + 1).trim();
            
            switch (kulcs) {
                case "name" -> n = ertek;
                case "prompt" -> p = ertek;
                case "systemPrompt" -> sp = ertek;
                case "output" -> out = ertek;
                default -> throw new WorkflowFormatException("ismeretlen kulcs");
            }
        }
        
        if (!megvan) {
            throw new WorkflowFormatException("hianyzik az endstep");
        }
        
        if (n == null || p == null || sp == null || out == null) {
            throw new WorkflowFormatException("hianyos adatok a lepeshez");
        }
        
        SchemaType st;
        try {
            st = SchemaType.valueOf(out);
        } catch (IllegalArgumentException e) {
            throw new WorkflowFormatException("rossz kimeneti tipus");
        }
        
        StructuredOutput so = new StructuredOutput(new SchemaType[]{st});
        return new WorkflowStep(n, p, sp, so);
    }
}