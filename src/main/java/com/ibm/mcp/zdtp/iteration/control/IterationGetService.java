package com.ibm.mcp.zdtp.iteration.control;

import com.ibm.mcp.zdtp.iteration.entity.Iteration;
import com.ibm.mcp.zdtp.iteration.entity.IterationDto;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class IterationGetService {
    private final QueryEngine engine;

    public IterationGetService(QueryEngine engine) {
        this.engine = engine;
    }

    public Iteration get(int id) {
        return engine.get(
                QueryEngine.ITERATION,
                id,
                IterationConverter::toIteration,
                IterationDto.class
        );
    }
}
