package com.ibm.mcp.zdtp.impediment.control;

import com.ibm.mcp.zdtp.impediment.entity.Impediment;
import com.ibm.mcp.zdtp.impediment.entity.ImpedimentDto;

public class ImpedimentConverter {
    public ImpedimentDto toDto(Impediment i) {
        return new ImpedimentDto(
                i.id() != null ? i.id() : 0,
                i.name(),
                i.description(),
                i.state() != null ? i.state().name() : null
        );
    }
}
