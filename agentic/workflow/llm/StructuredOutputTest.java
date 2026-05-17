package agentic.workflow.llm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StructuredOutputTest {

    @Test
    public void testValidCreation() {
        SchemaType[] types = {SchemaType.INT, SchemaType.BOOLEAN};
        StructuredOutput output = new StructuredOutput(types);
        
        assertEquals(2, output.size(), "A size() metódusnak a helyes hosszt kell visszaadnia.");
        assertTrue(output.contains(SchemaType.INT), "A contains() metódusnak true-t kell adnia létező elemre.");
        assertTrue(output.contains(SchemaType.BOOLEAN));
        assertFalse(output.contains(SchemaType.STRING), "A contains() metódusnak false-t kell adnia nem létező elemre.");
    }

    @Test
    public void testEncapsulationOnConstructor() {
        SchemaType[] originalArray = { SchemaType.STRING };
        StructuredOutput output = new StructuredOutput(originalArray);

        originalArray[0] = SchemaType.INT;

        assertEquals(SchemaType.STRING, output.getSchemaTypes()[0], 
            "Enkapszulációs hiba: A konstruktor nem készített másolatot a bemeneti tömbről!");
    }

    @Test
    public void testEncapsulationOnGetter() {
        StructuredOutput output = new StructuredOutput(new SchemaType[]{ SchemaType.LIST_INT });
        
        SchemaType[] retrievedArray = output.getSchemaTypes();
        
        retrievedArray[0] = SchemaType.BOOLEAN;

        assertEquals(SchemaType.LIST_INT, output.getSchemaTypes()[0], 
            "Enkapszulációs hiba: A getSchemaTypes() nem másolatot adott vissza!");
    }

    @Test
    public void testNullOrEmptyArrayValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new StructuredOutput((SchemaType[]) null);
        }, "Null tömb esetén IllegalArgumentException-t kell dobni.");

        assertThrows(IllegalArgumentException.class, () -> {
            new StructuredOutput(new SchemaType[]{}); // Üres tömb
        }, "Üres tömb esetén IllegalArgumentException-t kell dobni.");
    }

    @Test
    public void testNullElementValidation() {
        assertThrows(NullPointerException.class, () -> {
            new StructuredOutput(new SchemaType[]{ SchemaType.INT, null });
        }, "Ha a tömbben null elem van, NullPointerException-t kell dobni.");
    }

    @Test
    public void testSize() {
        StructuredOutput output = new StructuredOutput(new SchemaType[]{SchemaType.INT, SchemaType.BOOLEAN});
        assertEquals(2, output.size(), "A size() metódusnak 2-t kell visszaadnia.");
    }

    @Test
    public void testContainsExistingType() {
        StructuredOutput output = new StructuredOutput(new SchemaType[]{SchemaType.INT});
        assertTrue(output.contains(SchemaType.INT), "Létező elemre true-t kell adnia.");
        assertFalse(output.contains(SchemaType.STRING), "Nem létező elemre false-t kell adnia.");
    }

    @Test
    public void testContainsMissingType() {
        StructuredOutput output = new StructuredOutput(new SchemaType[]{SchemaType.INT});
        assertFalse(output.contains(SchemaType.STRING), "A contains() metódusnak false-t kell adnia, ha az elem nincs a listában.");
    }
}