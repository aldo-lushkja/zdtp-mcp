package com.ibm.mcp.zdtp.mcp.boundary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SchemaBuilder {
    private final ObjectMapper mapper;

    public SchemaBuilder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectSchemaBuilder object() {
        return new ObjectSchemaBuilder(mapper);
    }
    
    public SchemaNodeBuilder string() {
        return new SchemaNodeBuilder(mapper, "string");
    }
    
    public SchemaNodeBuilder integer() {
        return new SchemaNodeBuilder(mapper, "integer");
    }
    
    public SchemaNodeBuilder number() {
        return new SchemaNodeBuilder(mapper, "number");
    }

    public class ObjectSchemaBuilder {
        private final ObjectNode node;
        private final ObjectNode properties;
        private final ArrayNode required;

        public ObjectSchemaBuilder(ObjectMapper mapper) {
            this.node = mapper.createObjectNode();
            this.node.put("type", "object");
            this.properties = this.node.putObject("properties");
            this.required = mapper.createArrayNode();
        }

        public ObjectSchemaBuilder prop(String name, SchemaNodeBuilder builder) {
            properties.set(name, builder.build());
            if (builder.isRequired()) {
                required.add(name);
            }
            return this;
        }

        public JsonNode build() {
            if (!required.isEmpty()) {
                node.set("required", required);
            }
            return node;
        }
    }

    public class SchemaNodeBuilder {
        private final ObjectNode node;
        private boolean required = false;

        public SchemaNodeBuilder(ObjectMapper mapper, String type) {
            this.node = mapper.createObjectNode();
            this.node.put("type", type);
        }

        public SchemaNodeBuilder withDescription(String description) {
            node.put("description", description);
            return this;
        }

        public SchemaNodeBuilder withDefault(Object defaultValue) {
            if (defaultValue instanceof String) node.put("default", (String) defaultValue);
            else if (defaultValue instanceof Integer) node.put("default", (Integer) defaultValue);
            else if (defaultValue instanceof Double) node.put("default", (Double) defaultValue);
            else if (defaultValue instanceof Boolean) node.put("default", (Boolean) defaultValue);
            return this;
        }
        
        public SchemaNodeBuilder required() {
            this.required = true;
            return this;
        }

        public boolean isRequired() {
            return required;
        }

        public JsonNode build() {
            return node;
        }
    }
}
