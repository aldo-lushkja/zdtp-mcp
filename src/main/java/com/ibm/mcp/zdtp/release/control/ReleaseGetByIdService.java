package com.ibm.mcp.zdtp.release.control;

import java.util.Map;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class ReleaseGetByIdService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,StartDate,EndDate,Effort,Owner[Id,Login]]";
    private final ReleaseConverter converter;

    public ReleaseGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, ReleaseConverter converter) {
        super(properties, httpClient);
        this.converter = converter;
    }

    public ReleaseDto getById(int id) {
        return get(id);
    }

    public ReleaseDto get(int id) {
        Map<String, String> parameters = Map.of("include", INCLUDE);
        return fetchSingle("Releases/" + id, parameters, Release.class, converter::toDto);
    }
}
