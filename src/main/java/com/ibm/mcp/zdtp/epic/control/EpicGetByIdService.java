package com.ibm.mcp.zdtp.epic.control;

import java.util.Map;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.epic.entity.Epic;
import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class EpicGetByIdService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";
    private final EpicConverter converter;

    public EpicGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, EpicConverter converter) {
        super(properties, httpClient);
        this.converter = converter;
    }

    public EpicDto getById(int id) {
        return get(id);
    }

    public EpicDto get(int id) {
        Map<String, String> parameters = Map.of("include", INCLUDE);
        return fetchSingle("Epics/" + id, parameters, Epic.class, converter::toDto);
    }
}
