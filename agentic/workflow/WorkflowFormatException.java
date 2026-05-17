package agentic.workflow;

public class WorkflowFormatException extends Exception {

    public WorkflowFormatException(String message) {
        super(message);
    }

    public WorkflowFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}