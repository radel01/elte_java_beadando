package agentic.workflow.llm;

public class StructuredOutput {
    private final SchemaType[] schemaTypes;

    public StructuredOutput(SchemaType[] schemaTypes) {
        if (schemaTypes == null || schemaTypes.length == 0) {
            throw new IllegalArgumentException("legalább egy sématípust meg kell adni.");
        }
        for (int i = 0; i < schemaTypes.length; i++) {
            if (schemaTypes[i] == null) {
                throw new NullPointerException("a megadott sématípusok között nem lehet null.");
            }
        }
        
        this.schemaTypes = new SchemaType[schemaTypes.length];
        for (int i = 0; i < schemaTypes.length; i++) {
            this.schemaTypes[i] = schemaTypes[i];
        }
    }

    public SchemaType[] getSchemaTypes() {
        SchemaType[] masolat = new SchemaType[this.schemaTypes.length];
        for (int i = 0; i < this.schemaTypes.length; i++) {
            masolat[i] = this.schemaTypes[i];
        }
        return masolat;
    }

    public boolean contains(SchemaType schemaType) {
        if (schemaType == null) {
            return false;
        }
        
        for (int i = 0; i < this.schemaTypes.length; i++) {
            if (this.schemaTypes[i] == schemaType) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return this.schemaTypes.length;
    }
}