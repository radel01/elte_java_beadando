package agentic.workflow.llm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StructuredOutputTest {

    @Test
    public void testValidCreation() {
        SchemaType[] types = {SchemaType.INT, SchemaType.BOOLEAN};
        StructuredOutput out = new StructuredOutput(types);
        
        assertEquals(2, out.size(), "rossz a meret");
        assertTrue(out.contains(SchemaType.INT), "nem talalja meg az intet");
        assertTrue(out.contains(SchemaType.BOOLEAN));
        assertFalse(out.contains(SchemaType.STRING), "olyat is lat ami nincs");
    }

    @Test
    public void testEncapsulationOnConstructor() {
        SchemaType[] eredeti = { SchemaType.STRING };
        StructuredOutput out = new StructuredOutput(eredeti);

        eredeti[0] = SchemaType.INT;

        assertEquals(SchemaType.STRING, out.getSchemaTypes()[0], "konstruktor nem masol");
    }

    @Test
    public void testEncapsulationOnGetter() {
        StructuredOutput out = new StructuredOutput(new SchemaType[]{ SchemaType.LIST_INT });
        
        SchemaType[] kapott = out.getSchemaTypes();
        kapott[0] = SchemaType.BOOLEAN;

        assertEquals(SchemaType.LIST_INT, out.getSchemaTypes()[0], "getter nem masolatot ad");
    }

    @Test
    public void testNullOrEmptyArrayValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new StructuredOutput((SchemaType[]) null);
        }, "null parameterre nem fagy le");

        assertThrows(IllegalArgumentException.class, () -> {
            new StructuredOutput(new SchemaType[]{});
        }, "ures tombre nem fagy le");
    }

    @Test
    public void testNullElementValidation() {
        assertThrows(NullPointerException.class, () -> {
            new StructuredOutput(new SchemaType[]{ SchemaType.INT, null });
        }, "null elem a tombben nem zavarja");
    }

    @Test
    public void testSize() {
        StructuredOutput out = new StructuredOutput(new SchemaType[]{SchemaType.INT, SchemaType.BOOLEAN});
        assertEquals(2, out.size(), "nem jo a size");
    }

    @Test
    public void testContainsExistingType() {
        StructuredOutput out = new StructuredOutput(new SchemaType[]{SchemaType.INT});
        assertTrue(out.contains(SchemaType.INT), "nincs meg");
        assertFalse(out.contains(SchemaType.STRING), "ezt nem kene");
    }

    @Test
    public void testContainsMissingType() {
        StructuredOutput out = new StructuredOutput(new SchemaType[]{SchemaType.INT});
        assertFalse(out.contains(SchemaType.STRING), "rossz a contains");
    }
}