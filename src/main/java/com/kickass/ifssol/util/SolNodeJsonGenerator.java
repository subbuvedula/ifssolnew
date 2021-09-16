package com.kickass.ifssol.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kickass.ifssol.entity.IfsSolMapping;
import com.kickass.ifssol.entity.SolNode;
import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.mapper.incoming.WorkOrderSplitMapper;
import com.kickass.ifssol.util.reflect.DocTemplate;
import com.kickass.ifssol.util.reflect.DocTemplateMap;
import com.kickass.ifssol.util.reflect.Reflector;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SolNodeJsonGenerator {

    private static final String GENERATED_FOLDER_ROOT = "generated";

    @Autowired
    private Environment env;


    public void generate(String direction, String outFolderBase, String templateFolderName) {
        ClassLoader cl = WorkOrderSplitMapper.class.getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MMM_dd_hh_mm_ss");

        try {
            String leafFolderName = format.format(date);
            String outFolderName=outFolderBase + File.separator + direction + File.separator + leafFolderName;

            Resource resources[] = resolver.getResources("file:/"+ templateFolderName +"/*.xml");
            for(Resource resource : resources) {
                try {
                    String fileName = resource.getFilename();
                    fileName = fileName.replace("xml", "json");
                    File outputFolder = new File(outFolderName);
                    if (!outputFolder.exists()) {
                        outputFolder.mkdirs();
                    }
                    String outFileName = outFolderName + File.separator + fileName;
                    processInput(resource, new FileOutputStream(outFileName));
                } catch (XmlException | IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processInput(Resource resource, FileOutputStream fos) throws IOException, XmlException {
        InputStream is = resource.getInputStream();
        //InputStream is = WorkOrderSplitMapper.class.getResourceAsStream("/xml/incoming/WorkOrderSplit.xml");
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        String xmlString = new String(bytes);
        System.out.println(xmlString);
        XmlObject xmlObject = XmlObject.Factory.parse(xmlString);
        Node node = xmlObject.getDomNode();

        String nodeName = node.getNodeName();

        SolNodesRoot solNodesRoot = new SolNodesRoot();
        DocTemplate docTemplate = new Reflector().process(xmlObject.getClass());
        DocTemplateMap docTemplateMap = docTemplate.getDocTemplateMap();

        Object rootInstance = docTemplate.getRootInstance();

        SolNode solNode = new SolNode();
        traverse(node.getFirstChild(), solNode, null, docTemplateMap, rootInstance);

        solNodesRoot.addSolNode(solNode);

        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(fos, solNodesRoot);
        fos.flush();
        fos.close();
    }



    private void traverse(Node node, SolNode solNode,
                          SolNode parentNode,
                          DocTemplateMap docTemplateMap,
                          Object parentInstance) {
        String name = node.getNodeName();
        NamedNodeMap nameNodeMap = node.getAttributes();
        solNode.setName(name);

        for(int i=0; i<nameNodeMap.getLength(); i++) {
            Node attribute = nameNodeMap.item(i);
            if (attribute.getNodeType() == Node.ATTRIBUTE_NODE) {
                String attrName = attribute.getNodeName();
                String attrValue = attribute.getNodeValue();
                if (!attrName.equals("xmlns")) {
                    System.out.println(attrName + ":" + attrValue);
                }
                addSolMappings(solNode, attrName, docTemplateMap, parentInstance);
            }
        }

        if (parentNode != null) {
            parentNode.addSolNode(solNode);
        }

        NodeList nodeList = node.getChildNodes();

        for(int i=0; i<nodeList.getLength(); i++) {
            Node cnode = nodeList.item(i);
            String cnodename = cnode.getNodeName();

            if (cnode.getNodeType() == Node.TEXT_NODE){
                String nodevalue = cnode.getNodeValue().trim();
                if (nodevalue.length() == 0) {
                    continue;
                }
                addSolMappings(solNode, nodevalue, docTemplateMap, parentInstance);
                String nodeName = cnode.getNodeName();
                System.out.println(name + ":" + nodevalue);
            }
            else if (cnode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)cnode;
                String tagName = element.getTagName();

                SolNode csolNode = new SolNode();
                traverse(cnode, csolNode, solNode , docTemplateMap, parentInstance);
            }
        }
    }


    private String getType(SolNode solNode, DocTemplateMap docTemplateMap, Object parentInstance) {

        SolNode parentNode = solNode.getParentNode();
        String parentName = null;
        if (parentNode != null) {
            parentName = parentNode.getName();
        }
        if (parentName == null) {
            return "";
        }

        String clazzName = "org.openapplications.oagis.x9." + parentName + "Type";
        //DocTemplate docTemplate = docTemplateMap.get(parentInstance.getClass());
        DocTemplate docTemplate = docTemplateMap.getByClassName(clazzName);
        if (docTemplate != null) {
            Class returnType = docTemplate.getGetMethodReturnType(StringUtility.makeFirstLetterCap(solNode.getName()));
            /*
            Method getMethod = docTemplate.getGetMethod("get" + StringUtility.makeFirstLetterCap(solNode.getName()));
            if (getMethod == null) {
                return "";
            }
            Class returnType = getMethod.getReturnType();
             */
            if (returnType != null && XmlAnySimpleType.class.isAssignableFrom(returnType)) {
                return getSolFieldValueByType(returnType);
            }
        }
        return "";
    }

    private String getSolFieldValueByType(Class returnType) {
        if (XmlDecimal.class.isAssignableFrom(returnType)) {
            return "BigDecimalValue";
        }
        if (XmlString.class.isAssignableFrom(returnType)) {
            return "StringValue";
        }
        if (XmlDouble.class.isAssignableFrom(returnType)) {
            return "DoubleValue";
        }
        if (XmlFloat.class.isAssignableFrom(returnType)) {
            return "FloatValue";
        }
        if (XmlShort.class.isAssignableFrom(returnType)) {
            return "ShortValue";
        }
        if (XmlInt.class.isAssignableFrom(returnType)) {
            return "IntValue";
        }
        if (XmlBoolean.class.isAssignableFrom(returnType)) {
            return "BooleanValue";
        }
        if (XmlLong.class.isAssignableFrom(returnType)) {
            return "LongValue";
        }
        if (XmlInteger.class.isAssignableFrom(returnType)) {
            return "BigIntegerValue";
        }

        return "<REPLACE_MANUALLY>";
    }

    private void addSolMappings(SolNode solNode, String solName,
                                DocTemplateMap docTemplateMap, Object parentInstance) {

        String solValue = getType(solNode,docTemplateMap, parentInstance);
        IfsSolMapping ifsSolMapping = new IfsSolMapping();
        ifsSolMapping.setSol(solValue);
        solNode.addIfsSolMapping(ifsSolMapping);
    }

    private static void printUsage(String out, String templates, String direction) {
        if (StringUtils.isEmpty(out) || StringUtils.isEmpty(templates) || StringUtils.isEmpty(direction)) {
            System.out.println("Usage: java -Dout=<outfolder> -Dtemplates=<templates_folder> -Ddirection+<in|out> com.kickass.ifssol.util.SolNodeJsonGenerator");
        }
        System.exit(-1);
    }

    public static void main(String[] args) throws Exception {
        SolNodeJsonGenerator solNodeJsonGenerator = new SolNodeJsonGenerator();
        String out = System.getProperty("out");
        String templates = System.getProperty("templates");
        String direction = System.getProperty("direction");
        //printUsage(out, templates, direction);
         out = "C:\\IFSSOL\\templates\\generated";
        templates = "C:\\IFSSOL\\templates\\incoming";
        direction = "incoming";
        solNodeJsonGenerator.generate(direction, out, templates);
    }

}
