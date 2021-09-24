package com.kickass.ifssol.ifsproxy;

import ifs.fnd.ap.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class IFSServerProxy implements IIFSServerProxy {
    private boolean debugMode = false;

    private static final String DEFAULT_HOST = "https://ndevr10.nayotech.local:48080";
    private static final String DEFAULT_USERNAME = "SOLINT";
    private static final String DEFAULT_PASSWORD = "interface";
    private static final String DEFAULT_DEBUG_MODE = "true";
    private static final String DEFAULT_SCHEMA = "IFSAPP";

    private Server  server;
    private String  host = "";
    private String  username = "";
    private String  password = "";
    private String schema = "";

    @Autowired
    private Environment env;
    private static Logger LOGGER = LogManager.getLogger(IFSServerProxy.class);


    @PostConstruct
    public void createServer() throws  IFSServerException{
        if (env != null) {
            LOGGER.info("ifs.host : " + env.getProperty("ifs.host"));
        }

        this.host = getProperty("ifs.host", DEFAULT_HOST);
        this.username = getProperty("ifs.username", DEFAULT_USERNAME);
        this.password = getProperty("ifs.password", DEFAULT_PASSWORD);
        this.schema = getProperty("ifs.database.schema", DEFAULT_SCHEMA);

        String debugModeStr = getProperty("ifs.debugmode", DEFAULT_DEBUG_MODE);
        if ("true".equalsIgnoreCase(debugModeStr)) {
            this.debugMode = true;
        }

        this.server = new Server();
        this.server.setCredentials(username, password);
        this.server.setConnectionString(host);
        //testAccessProviderConnection();
    }

    public Server getServer() {
        return this.server;
    }

    private String getProperty(String key) {
        return getProperty(key, "");
    }
        private String getProperty(String key, String defaultValue) {
        if (env == null) {
            return defaultValue;
        }
        String val = env.getProperty(key);
        if (val == null) {
            return defaultValue;
        }
        return val;
    }

    public void setDebugMode(boolean debugMode){
        this.debugMode = debugMode;
    }

    public boolean getDebugMode(){
        return debugMode;
    }

    private void testAccessProviderConnection() throws IFSServerException{
        try{

            PlsqlSelectCommand cmd = new PlsqlSelectCommand(server, "SELECT * FROM FND_USER WHERE DESCRIPTION LIKE :DESC");
            cmd.getBindVariables().add("DESC", "A%");
            System.out.println();
            System.out.println("Invoking FndPLSQLSelectCommand..." );

            System.out.println(cmd.getCommandText());
            //System.out.println(cmd.get);

            RecordCollection result = cmd.executeQuery();

            System.out.println("Invoke done!" );
            System.out.println();

            if(result!=null){
                System.out.println("Users");
                System.out.println("======");
                for(int i = 0; i < result.size(); i++){
                    System.out.println((String)result.get(i).findValue("DESCRIPTION"));
                }
            }
            else
                System.out.println("No Users found!");
            System.out.println();
        } catch(Exception err) {
            throw new IFSServerException(err);
        }
    }

    //Send file data text/string to IFS using TEST_FILE_API.Insert_File_Str(filename,_fileData_)
    public void sendStringData(String filename, String fileData) throws IFSServerException {
        try{
            String cmdString = "BEGIN &AO.TEST_FILE_API.Insert_File_Str(:FILENAME_,:FILEDATA_); END;";

            if(server == null){
                server = new Server();
                server.setCredentials(username, password);
                server.setConnectionString(this.host);
            }

            PlsqlCommand cmd = new PlsqlCommand(server, cmdString);
            // Set bind variables values and direction (a Record is used for bind variables)
            Record bindVars = cmd.getBindVariables();
            bindVars.add("FILENAME_", filename).setBindVariableDirection(BindVariableDirection.IN);
            bindVars.add("FILEDATA_", fileData).setBindVariableDirection(BindVariableDirection.IN);

            cmd.execute();
            System.out.println("-- row added.");

            //Commit
            String cmdCommitString = "BEGIN COMMIT; END;";
            PlsqlCommand cmdCommit = new PlsqlCommand(server, cmdCommitString);
            cmdCommit.execute();
            System.out.println("--commited.");

        } catch(APException err){
            throw new IFSServerException(err);
        }
    }


    //send file data CLOB to IFS using TEST_FILE_API.Insert_File_Txt(filename_, fileData_)
    public void sendStringCLOBData(String filename, String fileData) throws IFSServerException {
        try{
            String cmdString = "BEGIN &AO.TEST_FILE_API.Insert_File_Txt(:FILENAME_,:FILEDATA_); END;";

            if(server == null){
                server = new Server();
                server.setCredentials(username, password);
                server.setConnectionString(this.host);
            }

            PlsqlCommand cmd = new PlsqlCommand(server, cmdString);
            // Set bind variables values and direction (a Record is used for bind variables)
            Record bindVars = cmd.getBindVariables();
            bindVars.add("FILENAME_", filename).setBindVariableDirection(BindVariableDirection.IN);
            //bindVars.add("FILEDATA_", fileData).setBindVariableDirection(BindVariableDirection.IN);
            bindVars.add("FILEDATA_", fileData, DataType.LONG_TEXT).setBindVariableDirection(BindVariableDirection.IN);

            cmd.execute();
            System.out.println("-- row added.");

            //Commit
            String cmdCommitString = "BEGIN COMMIT; END;";
            PlsqlCommand cmdCommit = new PlsqlCommand(server, cmdCommitString);
            cmdCommit.execute();
            System.out.println("--commited.");

        } catch(APException err){
            throw new IFSServerException(err);
        }
    }

    //send file data BINARY to IFS using TEST_FILE_API.Insert_File_Bin(filename_, file_)
    public void sendStringBLOBData(String filename, byte[] fileData) throws IFSServerException {
        try{
            //String cmdString = "BEGIN &AO.TEST_FILE_API.Insert_File_Bin(:FILENAME_,:FILEDATA_); END;";
            String cmdString = "BEGIN &AO.TEST_FILE_API.New_IFS_Doc(:FILENAME_, :FILEDATA_); END;";


            if(server == null){
                server = new Server();
                server.setCredentials(username, password);
                server.setConnectionString(this.host);
            }

            PlsqlCommand cmd = new PlsqlCommand(server, cmdString);
            // Set bind variables values and direction (a Record is used for bind variables)
            Record bindVars = cmd.getBindVariables();
            bindVars.add("FILENAME_", filename).setBindVariableDirection(BindVariableDirection.IN);
            //bindVars.add("FILEDATA_", fileData).setBindVariableDirection(BindVariableDirection.IN);
            bindVars.add("FILEDATA_", fileData, DataType.BINARY).setBindVariableDirection(BindVariableDirection.IN);

            cmd.execute();
            System.out.println("-- row added.");

            //Commit
            String cmdCommitString = "BEGIN COMMIT; END;";
            PlsqlCommand cmdCommit = new PlsqlCommand(server, cmdCommitString);
            cmdCommit.execute();
            System.out.println("--commited.");

        } catch(APException err){
            throw new IFSServerException(err);
        }
    }

    //send file data BINARY to IFS using TEST_FILE_API.Insert_File_Bin(filename_, file_)
    public void sendStringBLOBData2(String filename, byte[] fileData) throws IFSServerException {
        try{
            //String cmdString = "BEGIN &AO.TEST_FILE_API.Insert_File_Bin(:FILENAME_,:FILEDATA_); END;";
            String cmdString = "BEGIN &AO.TEST_FILE_API.New_IFS_Doc(:FILENAME_, :FILEDATA_); END;";


            if(server == null){
                server = new Server();
                server.setCredentials(username, password);
                server.setConnectionString(this.host);
            }

            PlsqlCommand cmd = new PlsqlCommand(server, cmdString);
            // Set bind variables values and direction (a Record is used for bind variables)
            Record bindVars = cmd.getBindVariables();
            bindVars.add("FILENAME_", filename).setBindVariableDirection(BindVariableDirection.IN);
            //bindVars.add("FILEDATA_", fileData).setBindVariableDirection(BindVariableDirection.IN);
            bindVars.add("FILEDATA_", fileData, DataType.BINARY).setBindVariableDirection(BindVariableDirection.IN);

            cmd.execute();
            System.out.println("-- row added.");

            //Commit
            String cmdCommitString = "BEGIN COMMIT; END;";
            PlsqlCommand cmdCommit = new PlsqlCommand(server, cmdCommitString);
            cmdCommit.execute();
            System.out.println("--commited.");

        } catch(APException err){
            throw new IFSServerException(err);
        }
    }

    public String getSchema() {
        return schema;
    }

}
