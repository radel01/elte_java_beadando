package agentic.workflow.llm;

public class StructuredOutput {
    private final SchemaType[] schemaTypes;

    public StructuredOutput(SchemaType[] schemaTypes) {
        if (schemaTypes == null || schemaTypes.length == 0) {
            throw new IllegalArgumentException("ures vagy null tomb lett megadva");
        }
        
        for (int i = 0; i < schemaTypes.length; i++) {
            if (schemaTypes[i] == null) {
                throw new NullPointerException("van egy null a megadott tipusok kozott");
            }
        }
        
        this.schemaTypes = new SchemaType[schemaTypes.length];
        for (int i = 0; i < schemaTypes.length; i++) {
            this.schemaTypes[i] = schemaTypes[i];
        }
    }

    public SchemaType[] getSchemaTypes() {
        SchemaType[] masolat = new SchemaType[schemaTypes.length];
        for (int i = 0; i < schemaTypes.length; i++) {
            masolat[i] = schemaTypes[i];
        }
        return masolat;
    }

    public boolean contains(SchemaType schemaType) {
        if (schemaType == null) {
            return false;
        }
        
        for (int i = 0; i < schemaTypes.length; i++) {
            if (schemaTypes[i] == schemaType) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return schemaTypes.length;
    }
}