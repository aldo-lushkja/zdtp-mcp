package com.ibm.mcp.zdtp.relation.control;

import com.ibm.mcp.zdtp.relation.entity.Relation;
import com.ibm.mcp.zdtp.relation.entity.RelationDto;

import java.util.Optional;

public class RelationConverter {
    public RelationDto toDto(Relation rel) {
        return new RelationDto(
                rel.id(),
                Optional.ofNullable(rel.relationType()).map(Relation.RelationTypeRef::name).orElse("N/A"),
                Optional.ofNullable(rel.master()).map(Relation.GeneralRef::id).orElse(0),
                Optional.ofNullable(rel.master()).map(Relation.GeneralRef::name).orElse("N/A"),
                Optional.ofNullable(rel.master()).map(Relation.GeneralRef::resourceType).orElse("N/A"),
                Optional.ofNullable(rel.slave()).map(Relation.GeneralRef::id).orElse(0),
                Optional.ofNullable(rel.slave()).map(Relation.GeneralRef::name).orElse("N/A"),
                Optional.ofNullable(rel.slave()).map(Relation.GeneralRef::resourceType).orElse("N/A")
        );
    }
}
