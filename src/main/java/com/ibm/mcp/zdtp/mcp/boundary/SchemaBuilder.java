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

    public ArraySchemaBuilder array() {
        return new ArraySchemaBuilder(mapper);
    }

    public class ArraySchemaBuilder {
        private final ObjectNode node;

        public ArraySchemaBuilder(ObjectMapper mapper) {
            this.node = mapper.createObjectNode();
            this.node.put("type", "array");
        }

        public ArraySchemaBuilder items(JsonNode itemSchema) {
            this.node.set("items", itemSchema);
            return this;
        }

        public JsonNode build() {
            return node;
        }
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

        public ObjectSchemaBuilder prop(String name, ArraySchemaBuilder builder) {
            properties.set(name, builder.build());
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
            switch (defaultValue) {
                case String  s -> node.put("default", s);
                case Integer i -> node.put("default", i);
                case Double  d -> node.put("default", d);
                case Boolean b -> node.put("default", b);
                default        -> {}
            }
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

