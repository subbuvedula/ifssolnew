package com.kickass.ifssol.mapper.incoming;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kickass.ifssol.dataaccessor.CommonDataAccessor;
import com.kickass.ifssol.entity.SolNode;
import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.ifsproxy.IFSServerProxy;
import com.kickass.ifssol.mapper.SolToIfsMapper;
import com.kickass.ifssol.util.reflect.DocTemplate;
import com.kickass.ifssol.util.reflect.Reflector;
import ifs.fnd.ap.PlsqlCommand;
import ifs.fnd.ap.Record;
import org.apache.xmlbeans.XmlObject;
import org.openapplications.oagis.x9.ProcessSplitWIPDocument;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;

@Component
public class WorkOrderSplitMapper {

    public void map(XmlObject xmlObject) {
        ProcessSplitWIPDocument processSplitWIPDocument =
                (ProcessSplitWIPDocument) xmlObject;
        processSplitWIPDocument.getProcessSplitWIP();
    }

    public static void main(String[] args) throws Exception {
        ClassLoader cl = WorkOrderSplitMapper.class.getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        try {
            Resource resources[] = resolver.getResources("classpath:/xml/incoming/*/");
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream is = WorkOrderSplitMapper.class.getResourceAsStream("/xml/incoming/WorkOrderSplit.xml");
        InputStream is2 = WorkOrderSplitMapper.class.getResourceAsStream("/WorkOrderSplit.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

        SolNodesRoot solNodesRoot = objectMapper.readValue(is2, SolNodesRoot.class);
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        String xmlString = new String(bytes);
        System.out.println(xmlString);
        XmlObject xmlObject = XmlObject.Factory.parse(xmlString);
        DocTemplate dt = new Reflector().process(xmlObject.getClass(), true);

        IFSServerProxy serverProxy = new IFSServerProxy();
        serverProxy.createServer();

        CommonDataAccessor dataAccessor = new CommonDataAccessor(serverProxy);
        PlsqlCommand command = dataAccessor.getPlSqlCommand(solNodesRoot.getUpdateStatement());
        Record record = command.getBindVariables();

        SolToIfsMapper solToIfsMapper = new SolToIfsMapper(dataAccessor);
        solToIfsMapper.map(xmlObject, solNodesRoot, dt.getDocTemplateMap(), record);
        dataAccessor.execute(command);

        /*

        Node node = xmlObject.getDomNode();

        SolNodesRoot solNodesRoot = new SolNodesRoot();
        SolNode solNode = new SolNode();
        traverse(node.getFirstChild(), solNode, null);

        solNodesRoot.addSolNode(solNode);
        solToIfsMapper.map()


        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, solNodesRoot);
        StringBuffer buffer = writer.getBuffer();
        System.out.println(buffer);
         */
    }

    private static void traverse(Node node, SolNode solNode, SolNode parentNode) {
        String name = node.getNodeName();

        solNode.setName(name);
        if (parentNode != null) {
            parentNode.addSolNode(solNode);
        }

        NodeList nodeList = node.getChildNodes();
        for(int i=0; i<nodeList.getLength(); i++) {
            Node cnode = nodeList.item(i);
            if (cnode.getNodeType() == Node.ELEMENT_NODE) {
                SolNode csoldNode = new SolNode();
                traverse(cnode, csoldNode, solNode );
            }
        }
    }

}
