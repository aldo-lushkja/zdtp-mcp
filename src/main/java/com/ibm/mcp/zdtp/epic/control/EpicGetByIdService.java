package com.ibm.mcp.zdtp.epic.control;

import com.ibm.mcp.zdtp.epic.entity.Epic;
import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class EpicGetByIdService extends BaseService {
    private final EpicConverter converter;

    public EpicGetByIdService(QueryEngine engine, EpicConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public EpicDto get(int id) {
        return engine.get(QueryEngine.EPIC, id, converter::toDto, Epic.class);
    }
}
