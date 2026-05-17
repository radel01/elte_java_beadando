import org.junit.jupiter.api.Tag;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@SelectClasses({
    AgenticWorkflowTestSuite.StructuralTests.class,
    AgenticWorkflowTestSuite.FunctionalTests.class,
})
@Suite(failIfNoTests=false) public class AgenticWorkflowTestSuite {
    @SelectClasses({
        SchemaTypeStructureTest.class,
        StructuredOutputStructureTest.class,

        WorkflowStepStructureTest.class,
        WorkflowFormatExceptionStructureTest.class,

        AgentStructureTest.class,
    })
    @Suite(failIfNoTests=false) @Tag("structural") public static class StructuralTests {}

    @SelectClasses({
        agentic.workflow.llm.StructuredOutputTest.class,
        agentic.workflow.WorkflowStepTest.class,
        // agentic.workflow.WorkflowFormatExceptionTest.class,
        agentic.workflow.AgentTest.class,
    })
    @Suite(failIfNoTests=false) @Tag("functional") public static class FunctionalTests {}
}

