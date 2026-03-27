package com.ibm.mcp.zdtp.release.control;


import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class ReleaseGetByIdService extends BaseService {
    private final ReleaseConverter converter;

    public ReleaseGetByIdService(QueryEngine engine, ReleaseConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public ReleaseDto getById(int id) {
        return get(id);
    }

    public ReleaseDto get(int id) {
        return engine.get(QueryEngine.RELEASE, id, converter::toDto, Release.class);
    }
}

