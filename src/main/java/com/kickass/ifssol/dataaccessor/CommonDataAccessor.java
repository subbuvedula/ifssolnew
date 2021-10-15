package com.kickass.ifssol.dataaccessor;

import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.ifsproxy.IFSServerProxy;
import ifs.fnd.ap.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class CommonDataAccessor {
    //@Autowired
    private IFSServerProxy ifsServerProxy;
    private static Logger LOGGER = LogManager.getLogger(CommonDataAccessor.class);
    private static final String SCHEMA_PLACEHOLDER_VALUE = "<_SCHEMA_>";

    @Autowired
    public CommonDataAccessor(IFSServerProxy ifsServerProxy) {
        this.ifsServerProxy = ifsServerProxy;
    }

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

    public void runProc(String storedProcString ,
                        Map<String,Object> bindVarMapIn,
                        Map<String,Object> bindVarMapOut,
                        Map<String,Object> bindVarMapInOut) throws APException {
        storedProcString = storedProcString.replaceAll(SCHEMA_PLACEHOLDER_VALUE, ifsServerProxy.getSchema());
        PlsqlCommand cmd = new PlsqlCommand(ifsServerProxy.getServer(), storedProcString);
        // Set bind variables values and direction (a Record is used for bind variables)
        Record bindVars = cmd.getBindVariables();
        bind(bindVars, bindVarMapIn, BindVariableDirection.IN);
        bind(bindVars, bindVarMapOut, BindVariableDirection.OUT);
        bind(bindVars, bindVarMapInOut, BindVariableDirection.IN_OUT);
        cmd.execute();
    }

    public PlsqlCommand getPlSqlCommand(String storedProcString) {
        storedProcString = storedProcString.replaceAll(SCHEMA_PLACEHOLDER_VALUE, ifsServerProxy.getSchema());
        return new PlsqlCommand(ifsServerProxy.getServer(), storedProcString);
    }

    public void execute(PlsqlCommand plsqlCommand) throws APException {
        plsqlCommand.execute();
    }

    private void bind(Record bindVars, Map<String,Object> bindVarMap, BindVariableDirection direction ) {
        for(String key : bindVarMap.keySet()) {
            addByType(key, bindVarMap.get(key), bindVars, direction);
        }
    }

    private void addByType(String name, Object value, Record record, BindVariableDirection direction) {
        if (value instanceof String) {
                record.add(name, value.toString()).setBindVariableDirection(direction);
        }
        else if (value instanceof BigDecimal) {
            record.add(name, ((BigDecimal) value).floatValue()).setBindVariableDirection(direction);
        }
        else if (value instanceof Boolean) {
            record.add(name, (Boolean) value).setBindVariableDirection(direction);;
        }
        else if (value instanceof Long) {
            record.add(name, (Long) value).setBindVariableDirection(direction);;
        }
        else if (value instanceof Double) {
            record.add(name, (Double) value).setBindVariableDirection(direction);;
        }
    }
}
