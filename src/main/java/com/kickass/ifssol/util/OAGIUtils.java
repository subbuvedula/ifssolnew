package com.kickass.ifssol.util;

/**
 * Proprietary and Confidential
 * Copyright 1995-2010 iBASEt, Inc.
 * Unpublished-rights reserved under the Copyright Laws of the United States
 * US Government Procurements:
 * Commercial Software licensed with Restricted Rights.
 * Use, reproduction, or disclosure is subject to restrictions set forth in
 * license agreement and purchase contract.
 * iBASEt, Inc. 27442 Portola Parkway, Suite 300, Foothill Ranch, CA 92610
 *
 * Solumina software may be subject to United States Dept of Commerce Export Controls.
 * Contact iBASEt for specific Expert Control Classification information.
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import com.kickass.ifssol.mapper.MappingException;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.GDurationBuilder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.openapplications.oagis.x9.ActionCriteriaType;
import org.openapplications.oagis.x9.ActionExpressionType;
import org.openapplications.oagis.x9.ApplicationAreaType;
import org.openapplications.oagis.x9.DateTimeType;
import org.openapplications.oagis.x9.LocationType;
import org.openapplications.oagis.x9.QuantityType;
import org.openapplications.oagis.x9.ResponseExpressionType;
import org.openapplications.oagis.x9.SenderType;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import com.ibaset.solumina.oagis.UserConfigurableDateTimeType;
import com.ibaset.solumina.oagis.UserConfigurableFieldType;
import com.ibaset.solumina.oagis.UserConfigurableIdentifierType;
import com.ibaset.solumina.oagis.UserConfigurableIndicatorType;
import com.ibaset.solumina.oagis.UserConfigurableNumberType;
import com.ibaset.solumina.oagis.UserConfigurableTextType;

@Component
public class OAGIUtils
{
    public static final String OAGI_NAMESPACE_URL = "http://www.openapplications.org/oagis/9";

    public static final String SOLUMINA_NAMESPACE_URL = "http://www.ibaset.com/solumina/oagis";

    public static final String OAGI_NAMESPACE_PREFIX = "oag";

    public static final String SOLUMINA_NAMESPACE_PREFIX = "ibts";

    public static final String RELEASE_ID = "9_4";

    public static final String LOGICAL_ID = "SOLUMINA";

    public static final String DEFAULT_CONFIRMATION_TYPE = "OnError";

    public static final int DEFAULT_BIG_DEC_SCALE = 5;

    /**
     * Generates a unique identifier for BOD
     *
     * @return a GUID
     */
    public  String generateBODId()
    {
        return UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * Create an XmlOption to be used for outbound xml messages
     *
     * @return XmlOptions
     */
    public  XmlOptions getDefaultXmlOptions()
    {
        XmlOptions opts = new XmlOptions();
        // opts.setSavePrettyPrint(); //This option actually trims white spaces as well
        opts.setSaveAggressiveNamespaces();
        opts.setUseDefaultNamespace();

        HashMap suggestedPrefixes = new HashMap();
        suggestedPrefixes.put(SOLUMINA_NAMESPACE_URL, SOLUMINA_NAMESPACE_PREFIX);
        opts.setSaveSuggestedPrefixes(suggestedPrefixes);

        return opts;
    }

    /**
     * Gets the XML representation of the BOD with all elements namespace
     * prefixed.
     *
     * @return String inbound XML object
     */
    public  String convertToXmlText(XmlObject xmlObject)
    {
        return convertToXmlText(xmlObject, getDefaultXmlOptions());
    }

    /**
     * Gets the XML representation of the BOD with all elements namespace
     * prefixed using supplied XmlOptions
     *
     * @return String inbound XML object
     */
    public  String convertToXmlText(XmlObject xmlObject, XmlOptions opts)
    {
        return xmlObject.xmlText(opts);
    }

    public void validateXml(XmlObject xmlObject) throws MappingException
    {
        // create an XmlOptions instance and set the error listener.
        XmlOptions validateOptions = new XmlOptions();
        ArrayList errorList = new ArrayList();
        validateOptions.setErrorListener(errorList);

        String errorMessage = "Xml document is not valid against "
                + xmlObject.schemaType() + "schema." + "\n";

        if (!xmlObject.validate(validateOptions))
        {
            XmlError[] xmlErrors = (XmlError[]) errorList.toArray(new XmlError[errorList.size()]);
            for (int i = 0; i < xmlErrors.length; i++)
            {
                XmlError error = (XmlError) errorList.get(i);

                errorMessage += error.getMessage() + "\n" + "      at "
                        + error.getCursorLocation().xmlText() + "\n";

            }

            throw new MappingException(errorMessage, xmlErrors);
        }
    }

    /**
     * Validates XmlObject
     *
     * @param doc
     *            XMLBean To Validate
     *
     * @return boolean whether the doc is valid
     * @throws XmlException
     */
    public  boolean validate(XmlObject doc, XmlError[] xmlErrors)
    {
        // Create an XmlOptions instance and set the error listener.
        XmlOptions validateOptions = new XmlOptions();
        ArrayList errorList = new ArrayList();
        validateOptions.setErrorListener(errorList);

        // Validate the XML.
        boolean isValid = doc.validate(validateOptions);

        // If the XML isn't valid
        if (!isValid)
        {
            xmlErrors = (XmlError[]) errorList.toArray(new XmlError[errorList.size()]);
        }

        return isValid;
    }

    /**
     * Validates XmlObject with incoming XmlOptions. Returns any errors found in
     * a HashMap.
     *
     * @param doc
     *            XMLBean To Validate
     * @param validateOptions
     * @return HashMap with 2 elements:<br>
     *         <ul>
     *         <li><b>VALID</b> - Boolean</li>
     *         <li><b>ERRORS</B> - ArrayList of String error messages</li>
     *         </ul>
     */
    public  HashMap validate(XmlObject doc, XmlOptions validateOptions)
    {
        ArrayList errors = new ArrayList();
        HashMap result = new HashMap();

        // Create an XmlOptions instance and set the error listener.
        ArrayList errorList = new ArrayList();
        validateOptions.setErrorListener(errorList);

        // Validate the XML.
        boolean isValid = doc.validate(validateOptions);

        // If the XML isn't valid, loop through the listener's contents,
        // printing contained messages.
        if (!isValid)
        {
            for (int i = 0; i < errorList.size(); i++)
            {
                XmlError error = (XmlError) errorList.get(i);

                errors.add("Error:" + error.getMessage() + "\n" + "At:   "
                        + error.getCursorLocation().xmlText());
            }
        }

        // Set Return Value
        result.put("VALID", new Boolean(isValid));
        result.put("ERRORS", errors);

        return result;
    }

    /**
     * Build the Standard Application Area for a BOD
     *
     * @param logicalId
     *            The name of application that created the BOD. Set to SOLUMINA.
     * @param componentId
     *            The application area that initiated the BOD generation (e.g.,
     *            Planning, WID, QA, SystemAdmin, etc.)
     * @param taskId
     *            The specific name of the touch point
     * @param referenceId
     *            The instance identifier of the event or task that caused the
     *            BOD generation
     * @param confirmationType
     *            The type of confirmation required from the other system. Valid
     *            values are Never, OnError, Always. OnError is the default if
     *            not provided.
     * @param authorizationId
     *            The user who performed the task that generated the BOD
     * @param bodId
     *            Unique identifier for the message. Usually a GUID.
     * @param creationDate
     *            The date that message is generated
     */
    public  void initalizeApplicationArea(ApplicationAreaType applicationArea,
                                          String logicalId,
                                          String componentId,
                                          String taskId,
                                          String referenceId,
                                          String confirmationType,
                                          String authorizationId,
                                          String bodId,
                                          Date creationDate)
    {
        // Verify ApplicationArea exists
        if (applicationArea == null)
        {
            throw new RuntimeException("Application Area must already have been created");
        }

        // Create new Sender
        SenderType sender = applicationArea.addNewSender();

        // Create new Logical Id
        sender.addNewLogicalID().setStringValue(logicalId);

        if (componentId != null)
        {
            // Create new Component Id
            sender.addNewComponentID().setStringValue(componentId);
        }

        // Create new Task Id
        if (taskId != null)
        {
            sender.addNewTaskID().setStringValue(taskId);
        }

        // Create new Reference Id
        if (referenceId != null)
        {
            sender.addNewReferenceID().setStringValue(referenceId);
        }

        // Create new Confirm Code
        if (confirmationType != null)
        {
            sender.addNewConfirmationCode().setStringValue(confirmationType);
        }
        else
        {
            sender.addNewConfirmationCode()
                    .setStringValue(DEFAULT_CONFIRMATION_TYPE);
        }

        if (authorizationId != null)
        {
            // Create new Authorization Id
            sender.addNewAuthorizationID().setStringValue(authorizationId);
        }

        // Create new BOD ID
        applicationArea.addNewBODID().setStringValue(bodId);

        // Create new Creation Date Time
        XmlCalendar cal = new XmlCalendar();
        cal.setTimeInMillis(creationDate.getTime());
        DateTimeType dateTime = DateTimeType.Factory.newInstance();
        dateTime.setStringValue(cal.toString());
        applicationArea.xsetCreationDateTime(dateTime);
    }

    public  void updateApplicationArea(ApplicationAreaType applicationArea,
                                       String logicalId,
                                       String componentId,
                                       String taskId,
                                       String referenceId,
                                       String confirmationType,
                                       String authorizationId,
                                       String bodId,
                                       Date creationDate)
    {
        // Verify ApplicationArea exists
        if (applicationArea == null)
        {
            throw new RuntimeException("Application Area must already have been created");
        }

        SenderType sender = applicationArea.getSender();

        // Update Logical Id
        sender.getLogicalID().setStringValue(logicalId);

        // Update Component Id
        sender.getComponentID().setStringValue(componentId);

        // Update Task Id
        sender.getTaskID().setStringValue(taskId);

        // Update Task Id
        sender.getReferenceID().setStringValue(referenceId);

        // Update Confirmation Code
        sender.getConfirmationCode().setStringValue(confirmationType);

        // Update Authorization Id
        if (sender.isSetAuthorizationID())
        {
            sender.getAuthorizationID().setStringValue(authorizationId);
        }
        else
        {
            sender.addNewAuthorizationID().setStringValue(authorizationId);
        }

        // Update BOD ID
        applicationArea.getBODID().setStringValue(bodId);

        // Update Creation Date Time
        XmlCalendar cal = new XmlCalendar();
        cal.setTimeInMillis(creationDate.getTime());
        DateTimeType dateTime = DateTimeType.Factory.newInstance();
        dateTime.setStringValue(cal.toString());
        applicationArea.xsetCreationDateTime(dateTime);
    }

    /**
     * Retrieves the ApplicationArea in the BOD
     *
     * @param xmlDoc
     *            Business Object Document
     * @return ApplicationArea
     */
    public  ApplicationAreaType getApplicationArea(XmlObject xmlDoc)
    {
        ApplicationAreaType applicationArea = null;

        String namespacePrefix = getPrefix(xmlDoc, OAGI_NAMESPACE_URL);
        if (StringUtils.isNotEmpty(namespacePrefix))
        {
            namespacePrefix = namespacePrefix + ":";
        }
        String xpath = "//" + namespacePrefix + "ApplicationArea";

        XmlObject[] searchResult = getObjectFromXpath(xmlDoc, xpath);

        if (searchResult != null && searchResult.length > 0)
        {
            applicationArea = (ApplicationAreaType) searchResult[0];
        }

        return applicationArea;
    }

    /**
     * Gets all ActionExpressions from ActionCriteriaArray Returns null if no
     * ActionExpressions are found
     *
     * @param actionCriteriaArray
     * @return ActionExpressionType[]
     */
    public  ActionExpressionType[] getActionExpressions(ActionCriteriaType[] actionCriteriaArray)
    {
        List actionExpressions = new ArrayList();

        if (actionCriteriaArray != null && actionCriteriaArray.length > 0)
        {
            for (int i = 0; i < actionCriteriaArray.length; i++)
            {

                if (actionCriteriaArray[i].getActionExpressionList() != null
                        && !actionCriteriaArray[i].getActionExpressionList()
                        .isEmpty())
                {
                    for (Iterator iterator = actionCriteriaArray[i].getActionExpressionList()
                            .iterator(); iterator.hasNext();)
                    {
                        ActionExpressionType nextElement = (ActionExpressionType) iterator.next();
                        actionExpressions.add(nextElement);
                    }
                }
            }
        }

        if (actionExpressions.size() == 0)
        {
            return null;
        }
        else
        {
            return (ActionExpressionType[]) actionExpressions.toArray(new ActionExpressionType[actionExpressions.size()]);
        }
    }

    /**
     * Gets all ActionExpressions from an XmlObject Returns null if no
     * ActionExpressions are found
     *
     * @param xmlDoc
     * @return ActionExpressionType[]
     */
    public  ActionExpressionType[] getActionExpressionArray(XmlObject xmlDoc)
    {
        String namespacePrefix = getPrefix(xmlDoc, OAGI_NAMESPACE_URL);
        if (StringUtils.isNotEmpty(namespacePrefix))
        {
            namespacePrefix = namespacePrefix + ":";
        }
        String xpath = "//" + namespacePrefix + "ActionExpression";

        XmlObject[] searchResult = getObjectFromXpath(xmlDoc, xpath);

        if (searchResult != null && searchResult.length > 0)
        {
            ActionExpressionType[] actionExpressions = new ActionExpressionType[searchResult.length];
            for (int i = 0; i < searchResult.length; i++)
            {
                actionExpressions[i] = (ActionExpressionType) searchResult[i];
            }

            return actionExpressions;
        }
        else
        {
            return null;
        }
    }

    public boolean isActionExpressionExistInXml(XmlObject xmlDoc)
    {
        XmlObject[] searchResult = getObjectFromXpath(xmlDoc,"//ActionExpression", getNamespaceDeclaration());
        return (searchResult != null && searchResult.length > 0);
    }

    /**
     * Adds action code to the ActionCriteria section in BOD
     *
     * @param actionCode
     *            valid action code, e.g., Add, Delete, Replace, Change, etc.
     * @param xpathExpression
     *            XPATH expression pointing to the location where the action
     *            should be applied
     */
    public  void setActionCode(String actionCode,
                               String xpathExpression,
                               ActionCriteriaType actionCriteria)
    {
        ActionExpressionType actionExpression = actionCriteria.addNewActionExpression();
        actionExpression.setActionCode(actionCode);
        actionExpression.setStringValue(xpathExpression);
    }


    public  XmlObject[] getObjectFromXpath(XmlObject xmlDoc,
                                           String xPath,
                                           String namespaceDeclaration)
    {
        HashMap map = new HashMap();
        XmlOptions options = new XmlOptions();
        ArrayList res = new ArrayList();

        // Map objects with no namespace defined to OAGI
        map.put("", OAGI_NAMESPACE_URL);
        options.setLoadSubstituteNamespaces(map);

        // Build xPath
        String qualifiedXPath = namespaceDeclaration + "$this" + xPath;

        // Get a Cursor for the XML
        XmlCursor c = xmlDoc.newCursor();

        // Try to find the object by the supplied xpath
        c.selectPath(qualifiedXPath, options);

        // Loop through hits and store objects
        while (c.hasNextSelection())
        {
            c.toNextSelection();
            res.add(c.getObject());
        }

        c.dispose();

        // Return Array of XmlObjects
        return (XmlObject[]) res.toArray(new XmlObject[res.size()]);
    }

    /**
     * Gets the XmlObject referenced by the value of the XPATH. This routine
     * adjusts the prefix for the namespaces based on the content of the "doc".
     * This allows for the sender to use whatever prefix they desire for a
     * namespace.
     *
     * @param xmlDoc
     *            XMLBeans Instance of the OAGI BOD
     * @param xPath
     *            XPATH retrieved from various type of expressions
     * @return Array of objects that match the XPATH in the ActionExpression
     */
    public  XmlObject[] getObjectFromXpath(XmlObject xmlDoc, String xPath)
    {
        return getObjectFromXpath(xmlDoc,
                xPath,
                getNameSpaceDeclarations(xmlDoc));
    }

    /**
     * Gets the XmlObject referenced by the XPATH in the ActionExpression.
     *
     * @param xmlDoc
     *            XMLBeans Instance of the OAGIS BOD
     * @param actionExpression
     *            ActionExpression with a valid XPATH
     * @return Array of objects that match the XPATH in the ActionExpression
     */

    public  XmlObject[] getObjectFromActionExpression(XmlObject xmlDoc,
                                                      ActionExpressionType actionExpression)
    {
        return getObjectFromXpath(xmlDoc, actionExpression.getStringValue());
    }

    /**
     * @param childXmlObject
     * @param expectedNounType
     * @return
     */
    public  XmlObject getRootNoun(XmlObject childXmlObject,
                                  Class expectedNounType)
    {
        if (expectedNounType.isAssignableFrom(childXmlObject.getClass()))
        {
            return childXmlObject;
        }
        else
        {
            return getRootNoun((XmlObject) childXmlObject.selectPath("..")[0],
                    expectedNounType);
        }
    }

    /**
     * Gets the prefix used for the supplied Namespace URL in the "doc" content.
     *
     * @param doc
     *            XMLBeans Instance of the OAGI BOD
     * @param url
     *            Namespace URL
     * @return
     */

    public  String getPrefix(XmlObject doc, String url)
    {
        String prefix;

        // Get Cursor for XMLBean
        XmlCursor cur = doc.newCursor();
        cur.toLastChild();

        // Extract defined Prefix for incoming url
        prefix = cur.prefixForNamespace(url);

        // Dispose of the Cursor
        cur.dispose();

        // Return Prefix
        return prefix;
    }

    /**
     * Builds the Namespace declaration for use in XPATH evaluation. Declaration
     * uses the prefixes defined in the "doc" content and returns declarations
     * for both OAGI and Solumina Namespaces.
     *
     * @param doc
     *            XMLBeans Instance of the OAGI BOD
     * @return
     */
    public  String getNameSpaceDeclarations(XmlObject doc)
    {
        Map<String, String> suggestedPrefix = new HashMap();
        suggestedPrefix.put(OAGI_NAMESPACE_URL, getPrefix(doc, OAGI_NAMESPACE_URL));
        suggestedPrefix.put(SOLUMINA_NAMESPACE_URL,getPrefix(doc, SOLUMINA_NAMESPACE_URL));

        // Build Declare String
        return getNamespaceDeclaration(suggestedPrefix);
    }

    private  String buildDeclareString(String url, String prefix)
    {
        String declaration = null;

        if (StringUtils.isEmpty(prefix) && StringUtils.isNotEmpty(url))
        {
            declaration = "declare default element namespace '" + url + "'; ";
        }
        else if (StringUtils.isNotEmpty(prefix) && StringUtils.isNotEmpty(url))
        {
            declaration = "declare namespace " + prefix + "='" + url + "'; ";
        }

        return declaration;
    }

    public  String getNamespaceDeclaration()
    {
        Map<String, String> suggestedPrefix = new HashMap();
        suggestedPrefix.put(OAGI_NAMESPACE_URL, null);
        suggestedPrefix.put(SOLUMINA_NAMESPACE_URL, SOLUMINA_NAMESPACE_PREFIX);

        // Build Declare String
        return getNamespaceDeclaration(suggestedPrefix);
//        return "declare default element namespace '" + OAGI_NAMESPACE_URL
//                + "'; " + "declare default element namespace '"
//                + SOLUMINA_NAMESPACE_URL + "'; ";
    }

    /**
     * @param suggestedPrefix is a Map<namespaceUrl, Prefix>
     * @return
     */
    public  String getNamespaceDeclaration(Map<String, String> suggestedPrefix)
    {
        String nameSpaceDeclaration = null;
        if (suggestedPrefix != null && suggestedPrefix.size() > 0 )
        {
            for (Iterator iter = suggestedPrefix.keySet().iterator(); iter.hasNext();)
            {
                String url = (String) iter.next();
                if (StringUtils.isNotEmpty(url))
                {
                    String prefix = suggestedPrefix.get(url);
                    String nameSpace = buildDeclareString(url, prefix);
                    if (nameSpaceDeclaration == null )
                    {
                        nameSpaceDeclaration = nameSpace;
                    }
                    else
                    {
                        nameSpaceDeclaration = nameSpaceDeclaration + nameSpace;
                    }
                }
            }
        }
        else
        {
            nameSpaceDeclaration = getNamespaceDeclaration();
        }

        return nameSpaceDeclaration;
    }

    /**
     * Add the Contents the child XMLBean to a Parent XMLBean. This method is
     * defined for populating an XMLBean with the type of xs:Any or populating
     * it with a derived type. NOTE: the childObject will no longer be usable.
     *
     * @param parentObject
     *            Parent XMLBean defined as xs:Any
     * @param childObject
     *            Child XMLBeans whose content will be placed in the Parent
     *            XMLBean
     * @return reference to copied instance of childObject
     */
    public  XmlObject addObjectToAnyType(XmlObject parentObject,
                                         XmlObject childObject)
    {
        XmlCursor pCursor, cCursor;
        // Get Parent Cursor
        pCursor = parentObject.newCursor();
        pCursor.toEndToken();

        // Get Child Cursor
        cCursor = childObject.newCursor();
        // Move to first element
        cCursor.toFirstChild();
        // Loop through getting all siblings to the child element
        do
        {
            // Copy the XML from the Child to the Parent
            cCursor.copyXml(pCursor);
        } while (cCursor.toNextSibling());

        // Dispose of the Cursors
        pCursor.dispose();
        cCursor.dispose();
        // reopen the cursor so we can return the newly copied object
        pCursor = parentObject.newCursor();
        pCursor.toLastChild();
        return XmlBeans.nodeToXmlObject(pCursor.getDomNode());
    }

    /**
     * Extracts the Child XML Types from the supplied XML Object and returns
     * instantiated XmlBean Objects.
     *
     * @param source
     *            Xml Object with child elements to extract
     * @return the first XmlBean instances in the AnyType
     */
    public  XmlObject getObjectFromAnyType(XmlObject source)
    {
        Node base = null;
        XmlCursor cur = null;

        // Get DOM to traverse source XmlObject
        cur = source.newCursor();
        if (cur.toFirstChild())
        {
            base = cur.getDomNode();
        }

        // Dispose Cursor
        cur.dispose();

        return (base == null ? null : XmlBeans.nodeToXmlObject(base));
    }

    /**
     * Extracts the Child XML Types from the supplied XML Object and returns
     * instantiated XmlBean Objects.
     *
     * @param source
     *            Xml Object with child elements to extract
     * @return Array of XmlBean instances
     */
    public  XmlObject[] getObjectsFromAnyType(XmlObject source)
    {
        Node base = null;
        XmlCursor cur = null;
        ArrayList objects = new ArrayList();
        XmlObject[] xmlObjectArray = null;

        // Get DOM to traverse source XmlObject
        cur = source.newCursor();

        if (cur.toFirstChild())
        {
            base = cur.getDomNode();
            objects.add(XmlBeans.nodeToXmlObject(base));

            while (cur.toNextSibling())
            {
                base = cur.getDomNode();
                objects.add(XmlBeans.nodeToXmlObject(base));
            }

        }

        // Dispose Cursor
        cur.dispose();
        if (objects.size() > 0)
        {
            xmlObjectArray = new XmlObject[objects.size()];
            for (int i = 0; i < objects.size(); i++)
            {
                xmlObjectArray[i] = (XmlObject) objects.get(i);
            }
        }

        return xmlObjectArray;
    }

    /**
     * Extracts the XMLBean which matches the SchemaType of searchType. If the
     * SchemaType may appear more than once use getOjbectsFromAnyType Note that
     * it will only search one level deep and does not traverse more levels
     *
     * @param source
     *            Xml Object with child elements to extract
     * @param searchType
     *            The SchemaType to search for and return
     * @return XmlBean which matches the class type of searchType. returns null
     *         if not found.
     */
    public  XmlObject getObjectFromAnyTypeByType(XmlObject source,
                                                 SchemaType searchType)
    {
        Node base = null;
        XmlCursor cur = null;
        XmlObject testObject = null;
        XmlObject result = null;

        if (source != null)
        {
            // Get DOM to traverse source XmlObject
            cur = source.newCursor();

            if (cur.toFirstChild())
            {
                base = cur.getDomNode();
                testObject = XmlBeans.nodeToXmlObject(base);

                if (searchType.getName()
                        .getNamespaceURI()
                        .equals(base.getNamespaceURI())
                        && searchType.getName()
                        .getLocalPart()
                        .equals(base.getLocalName() + "Type"))
                {
                    result = testObject;
                }
                else
                {
                    while (cur.toNextSibling())
                    {
                        base = cur.getDomNode();
                        testObject = XmlBeans.nodeToXmlObject(base);

                        if (searchType.getName()
                                .getNamespaceURI()
                                .equals(base.getNamespaceURI())
                                && searchType.getName()
                                .getLocalPart()
                                .equals(base.getLocalName()
                                        + "Type"))
                        {
                            result = testObject;
                            break;
                        }
                    }
                }
            }

            // Dispose Cursor
            cur.dispose();
        }

        return result;
    }

    public  boolean getObjectFromAnyTypeByType(XmlObject userArea,
                                               XmlCursor toCur,
                                               SchemaType typeToFind)
    {

        // System.out.println("source " + userArea);
        Node base = null;
        XmlCursor cur = null;

        try
        {
            toCur.toLastChild();
            cur = userArea.newCursor();

            // Get DOM to traverse source XmlObject
            if (cur.toFirstChild())
            {
                base = cur.getDomNode();
                if (typeToFind.getName()
                        .getNamespaceURI()
                        .equals(base.getNamespaceURI())
                        && typeToFind.getName()
                        .getLocalPart()
                        .equals(base.getLocalName() + "Type"))
                {
                    cur.copyXml(toCur);
                    return true;
                }
                else
                {
                    while (cur.toNextSibling())
                    {
                        base = cur.getDomNode();

                        if (typeToFind.getName()
                                .getNamespaceURI()
                                .equals(base.getNamespaceURI())
                                && typeToFind.getName()
                                .getLocalPart()
                                .equals(base.getLocalName()
                                        + "Type"))
                        {
                            cur.copyXml(toCur);
                            return true;

                        }
                    }
                }
            }
            // Dispose Cursor
        }
        finally
        {
            if (cur != null)
            {
                cur.dispose();
            }
            if (toCur != null)
            {
                toCur.dispose();
            }
        }

        return false;
    }

    /**
     * Adds the supplied attribute to the XMLBean.
     *
     * @param obj
     *            XMLBean
     * @param name
     *            Attribute name
     * @param value
     *            Attribute value
     * @return
     */
    public  XmlObject addObjectAttribute(XmlObject obj,
                                         String name,
                                         String value)
    {
        XmlCursor cur = obj.newCursor();

        cur.setAttributeText(new QName(name), value);

        cur.dispose();

        return obj;
    }

    /**
     * Returns a HashMap of all Attributes on an XMLBean.
     *
     * @param obj
     *            XMLBean to get Attributes from
     * @return
     */
    public  HashMap getObjectAttributes(XmlObject obj)
    {
        HashMap atts = new HashMap();
        XmlCursor cur = obj.newCursor();

        if (cur.toFirstAttribute())
        {
            do
            {
                atts.put(cur.getName().getLocalPart(), cur.getTextValue());
            } while (cur.toNextAttribute());
        }

        cur.dispose();

        return atts;

    }

    /**
     * Returns the value of specific attribute in an XMLBean Returns null if the
     * attribute is not found
     *
     * @param obj
     *            XMLBean to get Attributes from
     * @return String
     */
    public  String getObjectAttribute(XmlObject obj, String attributeName)
    {
        String result = null;
        boolean found = false;
        XmlCursor cur = obj.newCursor();

        if (cur.toFirstAttribute())
        {
            do
            {
                if (StringUtils.equals(cur.getName().getLocalPart(),
                        attributeName))
                {
                    result = cur.getTextValue();
                    found = true;
                }
            } while (cur.toNextAttribute() && !found);
        }

        cur.dispose();

        return result;
    }

    /**
     * Get boolean equivalent of a "Y", "N", "true", or "false" string. This is
     * not case sensitive.
     *
     * @param bool
     *            String boolean indicator in "Y", "N", "true", or "false"
     *            format.
     * @return boolean equivalent of string
     */
    public  boolean stringToBoolean(String bool)
    {
        boolean isTrue = false;
        if (bool != null)
        {
            if ("Y".equalsIgnoreCase(bool))
            {
                isTrue = true;
            }
            else if ("N".equalsIgnoreCase(bool))
            {
                isTrue = false;
            }
            else if ("true".equalsIgnoreCase(bool))
            {
                isTrue = true;
            }
            else if ("false".equalsIgnoreCase(bool))
            {
                isTrue = false;
            }
        }

        return isTrue;
    }

    public  Character stringToCharacter(String value)
    {
        return new Character(value.charAt(0));
    }

    /**
     * Accepts a BigDecimal and converts it to a GDuration object in hours and
     * minutes.
     *
     * @param hoursTimeSpan
     *            BigDecimal time span to be converted.
     * @return GDuration with hours and minutes set in the GDuration hour and
     *         minute properties.
     */
    public  GDuration hoursToDuration(BigDecimal hoursTimeSpan)
    {
        final BigDecimal SECONDS_PER_HOUR = new BigDecimal("3600");
        final BigDecimal MINUTES_PER_HOUR = new BigDecimal("60");

        GDuration duration = null;
        if (hoursTimeSpan != null)
        {
            BigDecimal totalSeconds = hoursTimeSpan.multiply(SECONDS_PER_HOUR);
            BigDecimal[] hoursAndRemainder = totalSeconds.divideAndRemainder(SECONDS_PER_HOUR);
            BigDecimal[] minutesAndRemainder = hoursAndRemainder[1].divideAndRemainder(MINUTES_PER_HOUR);

            GDurationBuilder db = new GDurationBuilder();
            db.setHour(hoursAndRemainder[0].intValue());
            db.setMinute(minutesAndRemainder[0].intValue());
            db.setSecond(minutesAndRemainder[1].intValue());
            duration = db.toGDuration();
        }
        else
        {
            GDurationBuilder db = new GDurationBuilder();
            db.setHour(0);
            duration = db.toGDuration();
        }
        return duration;
    }

    /**
     * Accepts a BigDecimal and converts it to a GDuration object in days, hours
     * and minutes.
     *
     * @param daysTimeSpan
     *            BigDecimal time span to be converted.
     * @return GDuration with days, hours and minutes set in the GDuration day,
     *         hour and minute properties.
     */
    public  GDuration daysToDuration(BigDecimal daysTimeSpan)
    {
        GDuration duration = null;
        if (daysTimeSpan != null)
        {
            // BigDecimal days = new BigDecimal(daysTimeSpan.intValue());
            BigDecimal days = new BigDecimal(daysTimeSpan.toString());
            GDuration hourDuration = hoursToDuration(daysTimeSpan.subtract(days));
            GDurationBuilder db = new GDurationBuilder(hourDuration);
            db.setDay(days.intValue());
            duration = db.toGDuration();
        }
        else
        {
            GDurationBuilder db = new GDurationBuilder();
            db.setDay(0);
            duration = db.toGDuration();
        }
        return duration;
    }

    /**
     * Accepts GDuration object and returns an BigDecimal value assembled from
     * the hour and minute properties of the GDuration.
     *
     * @param duration
     *            GDuration object
     * @return BigDecimal with decimal scale 5 hour and minute duration value
     */
    public  BigDecimal durationToHours(GDuration duration)
    {
        BigDecimal hourDuration = BigDecimal.valueOf(0);
        double minsDuration = 0;
        double secondsDuration = 0;

        if (duration.getHour() > 0)
        {
            Integer integer = new Integer(duration.getHour());
            hourDuration = new BigDecimal(integer.toString());
        }
        if (duration.getMinute() > 0)
        {
            minsDuration = ((float) duration.getMinute()) / ((float) 60);
        }
        if (duration.getSecond() > 0)
        {
            secondsDuration = ((float) duration.getSecond()) / ((float) 3600);
        }
        BigDecimal totalDuration = BigDecimal.valueOf(minsDuration)
                .add(hourDuration)
                .add(BigDecimal.valueOf(secondsDuration));

        return totalDuration;
    }

    /**
     * Accepts GDuration object and returns an BigDecimal value assembled from
     * the hour and minute properties of the GDuration.
     *
     * @param duration
     *            GDuration object
     * @return BigDecimal with decimal scale of 5 hour and minute duration value
     */
    public  BigDecimal durationToDays(GDuration duration)
    {
        BigDecimal dayDuration = BigDecimal.valueOf(0);
        if (duration.getDay() > 0)
        {
            Integer integer = new Integer(duration.getDay());
            dayDuration = new BigDecimal(integer.toString());
            // dayDuration = new BigDecimal(duration.getDay());
        }

        // BigDecimal hourDuration = durationToHours(duration).divide(new
        // BigDecimal(24), DEFAULT_BIG_DEC_SCALE);
        BigDecimal hourDuration = durationToHours(duration).divide(new BigDecimal("24"),
                DEFAULT_BIG_DEC_SCALE);
        BigDecimal totalDuration = dayDuration.add(hourDuration);

        return totalDuration;
    }

    /**
     * Converts BigDecimal to XMLBean QuantityType format to be used in XMLBean
     *
     * @param val
     *            BigDecimal value
     * @return QuantityType
     */
    public  QuantityType bigDecimalToQuantity(BigDecimal val)
    {
        QuantityType qt = QuantityType.Factory.newInstance();
        qt.setStringValue(val.toString());
        return qt;
    }

    /**
     * Converts Date to Calendar format to be used in XMLBean
     *
     * @param date
     *            Date value
     * @return DateTimeType
     */
    public  DateTimeType dateToDateTimeType(Date date)
    {
        XmlCalendar cal = new XmlCalendar();
        cal.setTimeInMillis(date.getTime());
        DateTimeType dateTime = DateTimeType.Factory.newInstance();
        dateTime.setStringValue(cal.toString());
        return dateTime;
    }

    public  DateTimeType getDateTimeTypeForTimeStampString(String timeStampString)
    {
        DateTimeType dateTimeType = DateTimeType.Factory.newInstance();
        dateTimeType.setStringValue(timeStampString);
        return dateTimeType;
    }

    /**
     * Adds values to LocationType XMLBean
     *
     * @param loc
     *            LocationType XMLBean
     * @param name
     *            atribute name for the value
     * @param value
     *            the value to add to LocationType
     */
    public  void addLocation(LocationType loc, String name, String value)
    {
        addObjectAttribute(loc, "type", name);
        loc.addNewID().setStringValue(value);
    }

    public  HashMap getLocations(LocationType[] loc)
    {
        if (loc != null & loc.length > 0)
        {
            HashMap locationMap = new HashMap();
            for (int i = 0; i < loc.length; i++)
            {
                String attr = getObjectAttribute(loc[i], "type");
                locationMap.put(attr, loc[i].getIDArray(0).getStringValue());
            }
            return locationMap;
        }
        else
        {
            return null;
        }
    }

    public  void addUserConfigurableTextType(UserConfigurableFieldType ucf,
                                             String value,
                                             String nameAttribute,
                                             String aliasAttribute)
    {
        if (value != null && !"".equals(value))
        {
            UserConfigurableTextType ucid = ucf.addNewUserConfigurableText();
            if (nameAttribute != null)
            {
                ucid.setName(nameAttribute);
            }
            if (aliasAttribute != null)
            {
                ucid.setAlias(aliasAttribute);
            }

            ucid.setStringValue(value);
        }
    }

    public  void addUserConfigurableIdentifierType(UserConfigurableFieldType ucf,
                                                   String value,
                                                   String nameAttribute,
                                                   String aliasAttribute)
    {
        if (value != null && !"".equals(value))
        {
            UserConfigurableIdentifierType ucid = ucf.addNewUserConfigurableIdentifier();
            if (nameAttribute != null)
            {
                ucid.setName(nameAttribute);
            }
            if (aliasAttribute != null)
            {
                ucid.setAlias(aliasAttribute);
            }

            ucid.setStringValue(value);
        }
    }

    public  void addUserConfigurableIndicatorType(UserConfigurableFieldType ucf,
                                                  Character value,
                                                  String nameAttribute,
                                                  String aliasAttribute)
    {
        if (value != null)
        {
            UserConfigurableIndicatorType ucind = ucf.addNewUserConfigurableIndicator();
            if (nameAttribute != null)
            {
                ucind.setName(nameAttribute);
            }
            if (aliasAttribute != null)
            {
                ucind.setAlias(aliasAttribute);
            }

            ucind.setStringValue(value.toString());
        }
    }

    public  void addUserConfigurableNumberType(UserConfigurableFieldType ucf,
                                               BigDecimal value,
                                               String nameAttribute,
                                               String aliasAttribute)
    {
        if (value != null)
        {
            UserConfigurableNumberType ucn = ucf.addNewUserConfigurableNumber();
            if (nameAttribute != null)
            {
                ucn.setName(nameAttribute);
            }
            if (aliasAttribute != null)
            {
                ucn.setAlias(aliasAttribute);
            }
            ucn.setBigDecimalValue(value);
        }
    }

    public  void addUserConfigurableDateTimeType(UserConfigurableFieldType ucf,
                                                 Date value,
                                                 String nameAttribute,
                                                 String aliasAttribute)
    {
        if (value != null)
        {
            UserConfigurableDateTimeType ucdt = ucf.addNewUserConfigurableDateTime();
            if (nameAttribute != null)
            {
                ucdt.setName(nameAttribute);
            }
            if (aliasAttribute != null)
            {
                ucdt.setAlias(aliasAttribute);
            }
            ucdt.setObjectValue(value);
        }
    }

    public  HashMap getUserConfigurableFields(UserConfigurableFieldType ucfs)
    {

        HashMap ucfList = new HashMap();

        if (ucfs != null)
        {
            if (ucfs.getUserConfigurableIdentifierList() != null
                    && !ucfs.getUserConfigurableIdentifierList().isEmpty())
            {
                for (Iterator iterator = ucfs.getUserConfigurableIdentifierList()
                        .iterator(); iterator.hasNext();)
                {
                    UserConfigurableIdentifierType nextElement = (UserConfigurableIdentifierType) iterator.next();
                    ucfList.put(nextElement.getName(),
                            nextElement.getStringValue());
                }
            }

            if (ucfs.getUserConfigurableNumberList() != null
                    && !ucfs.getUserConfigurableNumberList().isEmpty())
            {
                for (Iterator iterator = ucfs.getUserConfigurableNumberList()
                        .iterator(); iterator.hasNext();)
                {
                    UserConfigurableNumberType nextElement = (UserConfigurableNumberType) iterator.next();
                    ucfList.put(nextElement.getName(),
                            nextElement.getBigDecimalValue());
                }
            }

            if (ucfs.getUserConfigurableDateTimeList() != null
                    && !ucfs.getUserConfigurableDateTimeList().isEmpty())
            {
                for (Iterator iterator = ucfs.getUserConfigurableDateTimeList()
                        .iterator(); iterator.hasNext();)
                {
                    UserConfigurableDateTimeType nextElement = (UserConfigurableDateTimeType) iterator.next();
                    XmlCalendar xmlCalendar = new XmlCalendar(nextElement.getStringValue());
                    Date date = new Date(xmlCalendar.getTimeInMillis());
                    ucfList.put(nextElement.getName(), date);
                }
            }

            if (ucfs.getUserConfigurableTextList() != null
                    && !ucfs.getUserConfigurableTextList().isEmpty())
            {
                for (Iterator iterator = ucfs.getUserConfigurableTextList()
                        .iterator(); iterator.hasNext();)
                {
                    UserConfigurableTextType nextElement = (UserConfigurableTextType) iterator.next();
                    ucfList.put(nextElement.getName(),
                            nextElement.getStringValue());
                }

            }

            if (ucfs.getUserConfigurableIndicatorList() != null
                    && !ucfs.getUserConfigurableIndicatorList().isEmpty())
            {
                for (Iterator iterator = ucfs.getUserConfigurableIndicatorList()
                        .iterator(); iterator.hasNext();)
                {
                    UserConfigurableIndicatorType nextElement = (UserConfigurableIndicatorType) iterator.next();
                    if (!nextElement.getStringValue().equalsIgnoreCase("true") && !nextElement.getStringValue().equalsIgnoreCase("false") )
                    {
                        throw new RuntimeException("UserConfigurableIndicator can have only Boolean value");
                    }
                    else
                    {
                        if(nextElement.getStringValue().equalsIgnoreCase("true"))
                        {
                            ucfList.put(nextElement.getName(), new Character('Y'));
                        }
                        else
                        {
                            ucfList.put(nextElement.getName(), new Character('N'));
                        }
                    }
                }
            }

        }

        return ucfList;
    }

    public  HashMap getUserConfigurableIdentifier(UserConfigurableFieldType oagiUcfs)
    {
        HashMap idList = new HashMap();

        if (oagiUcfs.getUserConfigurableIdentifierList() != null
                && !oagiUcfs.getUserConfigurableIdentifierList().isEmpty())
        {
            for (Iterator iterator = oagiUcfs.getUserConfigurableIdentifierList()
                    .iterator(); iterator.hasNext();)
            {
                UserConfigurableIdentifierType nextElement = (UserConfigurableIdentifierType) iterator.next();
                idList.put(nextElement.getName(), nextElement.getStringValue());
            }

        }

        return idList;
    }

    public  HashMap getUserConfigurableNumber(UserConfigurableFieldType oagiUcfs)
    {
        HashMap numList = new HashMap();

        if (oagiUcfs.getUserConfigurableNumberList() != null
                && !oagiUcfs.getUserConfigurableNumberList().isEmpty())
        {
            for (Iterator iterator = oagiUcfs.getUserConfigurableNumberList()
                    .iterator(); iterator.hasNext();)
            {
                UserConfigurableNumberType nextElement = (UserConfigurableNumberType) iterator.next();
                numList.put(nextElement.getName(),
                        nextElement.getBigDecimalValue());
            }
        }
        return numList;
    }

    public  HashMap getUserConfigurableDateTime(UserConfigurableFieldType oagiUcfs)
    {
        HashMap dtList = new HashMap();

        if (oagiUcfs.getUserConfigurableDateTimeList() != null
                && !oagiUcfs.getUserConfigurableDateTimeList().isEmpty())
        {
            for (Iterator iterator = oagiUcfs.getUserConfigurableDateTimeList()
                    .iterator(); iterator.hasNext();)
            {
                UserConfigurableDateTimeType nextElement = (UserConfigurableDateTimeType) iterator.next();
                dtList.put(nextElement.getName(),
                        (Date) nextElement.getObjectValue());
            }
        }

        return dtList;
    }

    public  HashMap getUserConfigurableIndicator(UserConfigurableFieldType oagiUcfs)
    {
        HashMap indList = new HashMap();

        if (oagiUcfs.getUserConfigurableIndicatorList() != null
                && !oagiUcfs.getUserConfigurableIndicatorList().isEmpty())
        {
            for (Iterator iterator = oagiUcfs.getUserConfigurableIndicatorList()
                    .iterator(); iterator.hasNext();)
            {
                UserConfigurableIndicatorType nextElement = (UserConfigurableIndicatorType) iterator.next();

                indList.put(nextElement.getName(),
                        new Character(nextElement.getStringValue()
                                .charAt(0)));
            }
        }

        return indList;
    }

    /**
     * Gets all ResponseExpressions from an XmlObject Returns null if no
     * ResponseExpressions are found
     *
     * @param xmlDoc
     * @return ResponseExpressionType[]
     */
    public  ResponseExpressionType[] getResponseExpressionArray(XmlObject xmlDoc)
    {
        String namespacePrefix = getPrefix(xmlDoc, OAGI_NAMESPACE_URL);
        if (StringUtils.isNotEmpty(namespacePrefix))
        {
            namespacePrefix = namespacePrefix + ":";
        }
        String xpath = "//" + namespacePrefix + "ResponseExpression";

        XmlObject[] searchResult = getObjectFromXpath(xmlDoc, xpath);

        if (searchResult != null && searchResult.length > 0)
        {
            ResponseExpressionType[] responseExpressions = new ResponseExpressionType[searchResult.length];
            for (int i = 0; i < searchResult.length; i++)
            {
                responseExpressions[i] = (ResponseExpressionType) searchResult[i];
            }

            return responseExpressions;
        }
        else
        {
            return null;
        }
    }

    public boolean isResponseExpressionExistInXml(XmlObject xmlDoc)
    {
        XmlObject[] searchResult = getObjectFromXpath(xmlDoc,"//ResponseExpression", getNamespaceDeclaration());
        return (searchResult != null && searchResult.length > 0);
    }

    /**
     * Gets the XmlObject referenced by the XPATH in the ActionExpression.
     *
     * @param xmlDoc
     *            XMLBeans Instance of the OAGIS BOD
     * @param
     * @return Array of objects that match the XPATH in the ActionExpression
     */

    public  XmlObject[] getObjectFromResponseExpression(XmlObject xmlDoc,
                                                        ResponseExpressionType responseExpressionType)
    {
        return getObjectFromXpath(xmlDoc, responseExpressionType.getStringValue());
    }

}
