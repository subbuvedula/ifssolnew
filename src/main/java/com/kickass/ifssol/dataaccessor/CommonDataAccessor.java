package com.kickass.ifssol.dataaccessor;

import com.kickass.ifssol.ifsproxy.IFSServerProxy;
import ifs.fnd.ap.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonDataAccessor {
    @Autowired
    private IFSServerProxy ifsServerProxy;
    private static Logger LOGGER = LogManager.getLogger(CommonDataAccessor.class);
    private static final String SCHEMA_PLACEHOLDER_VALUE = "<_SCHEMA_>";

    public RecordCollection getData(String query) throws APException {
        query = query.replaceAll(SCHEMA_PLACEHOLDER_VALUE, ifsServerProxy.getSchema());
        PlsqlSelectCommand cmd = new PlsqlSelectCommand(ifsServerProxy.getServer(), query);
        cmd.getBindVariables().add("STATE", "NEW");
        return cmd.executeQuery();
    }

    public void runProc(String storedProcString , String logId) throws APException {
        storedProcString = storedProcString.replaceAll(SCHEMA_PLACEHOLDER_VALUE, ifsServerProxy.getSchema());
        PlsqlCommand cmd = new PlsqlCommand(ifsServerProxy.getServer(), storedProcString);
        // Set bind variables values and direction (a Record is used for bind variables)
        Record bindVars = cmd.getBindVariables();
        bindVars.add("LOG_ID", logId).setBindVariableDirection(BindVariableDirection.IN);
        cmd.execute();
    }
}
