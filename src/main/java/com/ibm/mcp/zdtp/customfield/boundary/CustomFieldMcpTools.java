package com.ibm.mcp.zdtp.customfield.boundary;

import com.ibm.mcp.zdtp.customfield.control.CustomFieldListService;
import com.ibm.mcp.zdtp.customfield.control.CustomFieldUpdateService;
import com.ibm.mcp.zdtp.customfield.entity.CustomFieldInfo;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class CustomFieldMcpTools {
    private final CustomFieldListService listService;
    private final CustomFieldUpdateService updateService;

    public CustomFieldMcpTools(CustomFieldListService listService, CustomFieldUpdateService updateService) {
        this.listService = listService;
        this.updateService = updateService;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private CustomFieldListService listService;
        private CustomFieldUpdateService updateService;

        public Builder listService(CustomFieldListService listService) { this.listService = listService; return this; }
        public Builder updateService(CustomFieldUpdateService updateService) { this.updateService = updateService; return this; }
        public CustomFieldMcpTools build() { return new CustomFieldMcpTools(listService, updateService); }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("custom_field_list", "List custom field definitions for an entity type or process.",
                schema.object()
                        .prop("entityTypeName", schema.string().withDescription("Entity kind (UserStory, Task, Bug, Feature, Epic, Request)."))
                        .prop("processId", schema.integer().withDescription("Process ID."))
                        .prop("take", schema.integer().withDescription("Max results (default: 10)."))
                        .build(),
                args -> {
                    List<CustomFieldInfo> list = listService.list(
                            args.path("entityTypeName").asText(null),
                            args.has("processId") ? args.path("processId").asInt() : null,
                            args.has("take") ? args.path("take").asInt() : 10
                    );
                    if (list.isEmpty()) return "No custom fields found.";
                    return String.join("\n", list.stream().map(cf -> "[%d] %s (%s, Kind: %s)".formatted(cf.id() != null ? cf.id() : 0, cf.name(), cf.fieldType(), cf.entityKind())).toList());
                });

        server.registerTool("custom_field_update", "Update a custom field value on an entity.",
                schema.object()
                        .prop("entityId", schema.integer().required().withDescription("ID of the entity."))
                        .prop("fieldName", schema.string().required().withDescription("Name of the custom field."))
                        .prop("fieldValue", schema.string().required().withDescription("Value to set for the custom field."))
                        .build(),
                args -> {
                    CustomFieldInfo info = updateService.updateCustomField(
                            args.path("entityId").asInt(),
                            args.path("fieldName").asText(),
                            args.path("fieldValue").asText()
                    );
                    return "Custom field '%s' updated to '%s' on entity [%d].".formatted(info.name(), info.value(), args.path("entityId").asInt());
                });
    }
}
