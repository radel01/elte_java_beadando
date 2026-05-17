package agentic.workflow;

import agentic.workflow.llm.SchemaType;
import agentic.workflow.llm.StructuredOutput;

public class WorkflowStep {
    private String name;
    private String prompt;
    private String systemPrompt;
    private StructuredOutput structuredOutput;

    public WorkflowStep(String name, String prompt, String systemPrompt, StructuredOutput structuredOutput) {
        this.setName(name);
        this.setPrompt(prompt);
        this.setSystemPrompt(systemPrompt);
        this.setStructuredOutput(structuredOutput);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("nev nem lehet ures");
        }
        this.name = name;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public void setPrompt(String prompt) {
        if (prompt == null || prompt.trim().length() == 0) {
            throw new IllegalArgumentException("prompt hianyzik");
        }
        this.prompt = prompt;
    }

    public String getSystemPrompt() {
        return this.systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        if (systemPrompt == null || systemPrompt.trim().length() == 0) {
            throw new IllegalArgumentException("systemprompt ures");
        }
        this.systemPrompt = systemPrompt;
    }

    public StructuredOutput getStructuredOutput() {
        return this.structuredOutput;
    }

    public void setStructuredOutput(StructuredOutput structuredOutput) {
        if (structuredOutput == null) {
            throw new IllegalArgumentException("output nem lehet null");
        }
        this.structuredOutput = structuredOutput;
    }

    public boolean expectsStructuredOutput() {
        if (this.structuredOutput == null) {
            return false;
        }
        if (this.structuredOutput.size() > 0) {
            return true;
        }
        return false;
    }

    public String simulateResponse() {
        if (!this.expectsStructuredOutput()) {
            return "";
        }

        SchemaType t = this.structuredOutput.getSchemaTypes()[0];

        if (t == SchemaType.INT) {
            return "0";
        } else if (t == SchemaType.STRING) {
            return "sample";
        } else if (t == SchemaType.BOOLEAN) {
            return "true";
        } else if (t == SchemaType.LIST_INT) {
            return "[1,2,3]";
        } else if (t == SchemaType.LIST_STRING) {
            return "[\"a\",\"b\"]";
        } else if (t == SchemaType.MAP_STRING_STRING) {
            return "{\"kulcs\":\"ertek\"}";
        } else {
            return "";
        }
    }
}