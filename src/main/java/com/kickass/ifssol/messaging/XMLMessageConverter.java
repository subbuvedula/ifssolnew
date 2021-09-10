package com.kickass.ifssol.messaging;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.openapplications.oagis.x9.SyncLocationDataAreaType;
import org.openapplications.oagis.x9.SyncLocationDocument;
import org.openapplications.oagis.x9.SyncLocationType;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.w3c.dom.Node;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.UUID;

public class XMLMessageConverter implements MessageConverter {
    private static final Logger LOGGER = LogManager.getLogger(XMLMessageConverter.class);
    public static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!--\n" +
            "\n" +
            "    ** Proprietary and Confidential **\n" +
            "    ** Copyright 1995-2012, iBASEt, Inc. All Rights Reserved **\n" +
            "\n" +
            "    Unpublished-rights reserved under the Copyright Laws of the United States\n" +
            "    US Government Procurements:\n" +
            "    Commercial Software licensed with Restricted Rights.\n" +
            "    Use, reproduction, or disclosure is subject to restrictions set forth in license agreement and purchase contract.\n" +
            "    iBASEt, Inc. 27442 Portola Parkway, Suite 300, Foothill Ranch, CA 92610\n" +
            "\n" +
            "    This is a Solumina BIS interface mapping document, which is a derived work based on Open Applications Group, Inc OAGIS Business Object Docment (BOD) content. \n" +
            "    See OAGi License Agreement on usage and distribution, available at www.oagi.org, or included in the OAGIS schema package.\n" +
            "\n" +
            "    This is an OAGIS XML instance with additional elements to document Solumina data mapped for interface transactions. Data are mapped to elements and to some attributes.\n" +
            "\n" +
            "    XML Schema (XSD) Definitions\n" +
            "        * OAGIS: OAGi-BPI-Platform/org_openapplications_oagis/9_4_1/Developer/BODs/SyncLocation.xsd    \n" +
            "        * iBASEt: OAGi-BPI-Platform/com_ibaset_solumina_oagis/9_4_1/Developer/UserArea/ibtsSchemas.xsd\n" +
            "\n" +
            "-->" +
            "<SyncLocation xmlns=\"http://www.openapplications.org/oagis/9\"\n" +
            "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "              xmlns:ibts=\"http://www.ibaset.com/solumina/oagis\"\n" +
            "              xsi:schemaLocation=\"http://www.openapplications.org/oagis/9 ../../../org_openapplications_oagis/9_4_1/Developer/BODs/SyncLocation.xsd http://www.ibaset.com/solumina/oagis ../Developer/UserArea/ibtsSchemas.xsd\"\n" +
            "              languageCode=\"en-US\"\n" +
            "              versionID=\"9_4\"\n" +
            "              releaseID=\"9_4\"\n" +
            "              systemEnvironmentCode=\"Production\">\n" +
            "   <ApplicationArea>\n" +
            "      <Sender>\n" +
            "          <LogicalID schemeAgencyName=\"String\" schemeAgencyID=\"normalizedString\" schemeVersionID=\"normalizedString\" schemeName=\"String\" schemeURI=\"http://www.openapplications.org\" schemeID=\"normalizedString\" schemeDataURI=\"http://www.openapplications.org\">normalizedString</LogicalID>\n" +
            "         <ComponentID>componentSender</ComponentID>\n" +
            "         <TaskID/>\n" +
            "         <ReferenceID/>\n" +
            "         <ConfirmationCode/>\n" +
            "         <AuthorizationID>authSender</AuthorizationID>\n" +
            "      </Sender>\n" +
            "      <CreationDateTime>1967-08-13</CreationDateTime>\n" +
            "      <BODID/>\n" +
            "   </ApplicationArea>\n" +
            "   <DataArea>\n" +
            "      <Sync>\n" +
            "         <ActionCriteria>\n" +
            "            <ActionExpression actionCode=\"Add | Delete | Replace\"/>\n" +
            "         </ActionCriteria>\n" +
            "      </Sync>\n" +
            "      <Location type=\"workLocation\">\n" +
            "         <ID>SFFND_WORK_LOC_DEF.WORK_LOC</ID>\n" +
            "         <Name>SFFND_WORK_LOC_DEF.LOC_TITLE</Name>\n" +
            "\t\t <Address>\n" +
            "\t\t\t<AddressLine sequenceName=\"Address_Line_1\">SFFND_WORK_LOC_DEF.ADDRESS</AddressLine>\n" +
            "\t\t\t<CityName>SFFND_WORK_LOC_DEF.CITY</CityName>\n" +
            "\t\t\t<CountrySubDivisionCode>SFFND_WORK_LOC_DEF.STATE</CountrySubDivisionCode>\n" +
            "\t\t\t<CountryCode>SFFND_WORK_LOC_DEF.COUNTRY</CountryCode>\n" +
            "\t\t\t<PostalCode>SFFND_WORK_LOC_DEF.ZIPCODE</PostalCode>\n" +
            "\t\t </Address>\n" +
            "         <UserArea>\n" +
            "            <ibts:LocationUserArea>\n" +
            "               <ibts:ParentLocation type=\"company\">\n" +
            "                  <ID>SFFND_COMPANY_DEF.COMPANY</ID>\n" +
            "               </ibts:ParentLocation>\n" +
            "               <ibts:RepairStation>SFFND_WORK_LOC_DEF.REPAIR_STATION</ibts:RepairStation>\n" +
            "\t\t\t   <ibts:LineOne>SFFND_WORK_LOC_DEF.ADDRESS_LINE_2</ibts:LineOne>\n" +
            "               <ibts:UserConfigurableFields>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkLocationVarChar1\" alias=\"WorkLocationVarChar1\">SFFND_WORK_LOC_DEF.UCF_WORK_LOC_VCH1</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkLocationVarChar2\" alias=\"WorkLocationVarChar2\">SFFND_WORK_LOC_DEF.UCF_WORK_LOC_VCH2</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkLocationVarChar3\" alias=\"WorkLocationVarChar3\">SFFND_WORK_LOC_DEF.UCF_WORK_LOC_VCH3</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkLocationVarChar4\" alias=\"WorkLocationVarChar4\">SFFND_WORK_LOC_DEF.UCF_WORK_LOC_VCH4</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkLocationVarChar5\" alias=\"WorkLocationVarChar5\">SFFND_WORK_LOC_DEF.UCF_WORK_LOC_VCH5</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkLocationFlag1\" alias=\"WorkLocationFlag1\">SFFND_WORK_LOC_DEF.UCF_WORK_LOC_FLAG1</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkLocationFlag2\" alias=\"WorkLocationFlag2\">SFFND_WORK_LOC_DEF.UCF_WORK_LOC_FLAG2</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkLocationFlag3\" alias=\"WorkLocationFlag3\">SFFND_WORK_LOC_DEF.UCF_WORK_LOC_FLAG3</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableText name=\"WorkLocationVarChar2551\" alias=\"WorkLocationVarChar2551\">SFFND_WORK_LOC_DEF.UCF_WORK_LOC_VCH255_1</ibts:UserConfigurableText>\n" +
            "                  <ibts:UserConfigurableText name=\"WorkLocationVarChar2552\" alias=\"WorkLocationVarChar2552\">SFFND_WORK_LOC_DEF.UCF_WORK_LOC_VCH255_2</ibts:UserConfigurableText>\n" +
            "                  <ibts:UserConfigurableNumber name=\"WorkLocationNumber1\" alias=\"WorkLocationNumber1\">3.141<!--SFFND_WORK_LOC_DEF.UCF_WORK_LOC_NUM1--></ibts:UserConfigurableNumber>\n" +
            "                  <ibts:UserConfigurableNumber name=\"WorkLocationNumber2\" alias=\"WorkLocationNumber2\">3.141<!--SFFND_WORK_LOC_DEF.UCF_WORK_LOC_NUM2--></ibts:UserConfigurableNumber>\n" +
            "                  <ibts:UserConfigurableNumber name=\"WorkLocationNumber3\" alias=\"WorkLocationNumber3\">3.141<!--SFFND_WORK_LOC_DEF.UCF_WORK_LOC_NUM3--></ibts:UserConfigurableNumber>\n" +
            "                  <ibts:UserConfigurableDateTime name=\"WorkLocationDate1\" alias=\"WorkLocationDate1\">1967-08-13<!--SFFND_WORK_LOC_DEF.UCF_WORK_LOC_DATE1--></ibts:UserConfigurableDateTime>\n" +
            "                  <ibts:UserConfigurableDateTime name=\"WorkLocationDate2\" alias=\"WorkLocationDate2\">1967-08-13<!--SFFND_WORK_LOC_DEF.UCF_WORK_LOC_DATE2--></ibts:UserConfigurableDateTime>\n" +
            "               </ibts:UserConfigurableFields>\n" +
            "            </ibts:LocationUserArea>\n" +
            "         </UserArea>\n" +
            "      </Location>\n" +
            "      <Location type=\"workDepartment\">\n" +
            "         <ID>SFFND_WORK_DEPT_DEF.WORK_DEPT</ID>\n" +
            "         <Name>SFFND_WORK_DEPT_DEF.DEPT_TITLE</Name>\n" +
            "         <UserArea>\n" +
            "            <ibts:LocationUserArea>\n" +
            "               <ibts:ParentLocation type=\"workLocation\">\n" +
            "                  <ID>SFFND_WORK_DEPT_DEF.WORK_LOC</ID>\n" +
            "               </ibts:ParentLocation>\n" +
            "               <ibts:UserConfigurableFields>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkDepartmentVarChar1\" alias=\"WorkDepartmentVarChar1\">SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_VCH1</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkDepartmentVarChar2\" alias=\"WorkDepartmentVarChar2\">SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_VCH2</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkDepartmentVarChar3\" alias=\"WorkDepartmentVarChar3\">SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_VCH3</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkDepartmentVarChar4\" alias=\"WorkDepartmentVarChar4\">SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_VCH4</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkDepartmentVarChar5\" alias=\"WorkDepartmentVarChar5\">SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_VCH5</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkDepartmentFlag1\" alias=\"WorkDepartmentFlag1\">SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_FLAG1</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkDepartmentFlag2\" alias=\"WorkDepartmentFlag2\">SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_FLAG2</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkDepartmentFlag3\" alias=\"WorkDepartmentFlag3\">SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_FLAG3</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableText name=\"WorkDepartmentVarChar2551\" alias=\"WorkDepartmentVarChar2551\">SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_VCH255_1</ibts:UserConfigurableText>\n" +
            "                  <ibts:UserConfigurableText name=\"WorkDepartmentVarChar2552\" alias=\"WorkDepartmentVarChar2552\">SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_VCH255_2</ibts:UserConfigurableText>\n" +
            "                  <ibts:UserConfigurableNumber name=\"WorkDepartmentNumber1\" alias=\"WorkDepartmentNumber1\">3.141<!--SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_NUM1--></ibts:UserConfigurableNumber>\n" +
            "                  <ibts:UserConfigurableNumber name=\"WorkDepartmentNumber2\" alias=\"WorkDepartmentNumber2\">3.141<!--SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_NUM2--></ibts:UserConfigurableNumber>\n" +
            "                  <ibts:UserConfigurableNumber name=\"WorkDepartmentNumber3\" alias=\"WorkDepartmentNumber3\">3.141<!--SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_NUM3--></ibts:UserConfigurableNumber>\n" +
            "                  <ibts:UserConfigurableDateTime name=\"WorkDepartmentDate1\" alias=\"WorkDepartmentDate1\">1967-08-13<!--SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_DATE1--></ibts:UserConfigurableDateTime>\n" +
            "                  <ibts:UserConfigurableDateTime name=\"WorkDepartmentDate2\" alias=\"WorkDepartmentDate2\">1967-08-13<!--SFFND_WORK_DEPT_DEF.UCF_WORK_DEPT_DATE2--></ibts:UserConfigurableDateTime>\n" +
            "               </ibts:UserConfigurableFields>\n" +
            "            </ibts:LocationUserArea>\n" +
            "         </UserArea>\n" +
            "      </Location>\n" +
            "      <Location type=\"workCenter\">\n" +
            "         <ID>SFFND_WORK_CENTER_DEF.WORK_CENTER</ID>\n" +
            "         <Name>SFFND_WORK_CENTER_DEF.CENTER_TITLE</Name>\n" +
            "         <Note>SFFND_WORK_CENTER_DEF.WORK_CENTER_TYPE</Note>\n" +
            "         <UserArea>\n" +
            "            <ibts:LocationUserArea>\n" +
            "               <ibts:ParentLocation type=\"workDepartment\">\n" +
            "                  <ID>SFFND_WORK_CENTER_DEF.WORK_DEPT</ID>\n" +
            "               </ibts:ParentLocation>\n" +
            "               <ibts:ParentLocation type=\"workLocation\">\n" +
            "                  <ID>SFFND_WORK_CENTER_DEF.WORK_LOC</ID>\n" +
            "               </ibts:ParentLocation>\n" +
            "               <ibts:UserConfigurableFields>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkCenterVarChar1\" alias=\"WorkCenterVarChar1\">SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_VCH1</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkCenterVarChar2\" alias=\"WorkCenterVarChar2\">SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_VCH2</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkCenterVarChar3\" alias=\"WorkCenterVarChar3\">SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_VCH3</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkCenterVarChar4\" alias=\"WorkCenterVarChar4\">SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_VCH4</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkCenterVarChar5\" alias=\"WorkCenterVarChar5\">SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_VCH5</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkCenterFlag1\" alias=\"WorkCenterFlag1\">SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_FLAG1</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkCenterFlag2\" alias=\"WorkCenterFlag2\">SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_FLAG2</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableIdentifier name=\"WorkCenterFlag3\" alias=\"WorkCenterFlag3\">SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_FLAG3</ibts:UserConfigurableIdentifier>\n" +
            "                  <ibts:UserConfigurableText name=\"WorkCenterVarChar2551\" alias=\"WorkCenterVarChar2551\">SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_VCH255_1</ibts:UserConfigurableText>\n" +
            "                  <ibts:UserConfigurableText name=\"WorkCenterVarChar2552\" alias=\"WorkCenterVarChar2552\">SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_VCH255_2</ibts:UserConfigurableText>\n" +
            "                  <ibts:UserConfigurableNumber name=\"WorkCenterNumber1\" alias=\"WorkCenterNumber1\">3.141<!--SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_NUM1--></ibts:UserConfigurableNumber>\n" +
            "                  <ibts:UserConfigurableNumber name=\"WorkCenterNumber2\" alias=\"WorkCenterNumber2\">3.141<!--SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_NUM2--></ibts:UserConfigurableNumber>\n" +
            "                  <ibts:UserConfigurableNumber name=\"WorkCenterNumber3\" alias=\"WorkCenterNumber3\">3.141<!--SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_NUM3--></ibts:UserConfigurableNumber>\n" +
            "                  <ibts:UserConfigurableDateTime name=\"WorkCenterDate1\" alias=\"WorkCenterDate1\">1967-08-13<!--SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_DATE1--></ibts:UserConfigurableDateTime>\n" +
            "                  <ibts:UserConfigurableDateTime name=\"WorkCenterDate2\" alias=\"WorkCenterDate2\">1967-08-13<!--SFFND_WORK_CENTER_DEF.UCF_WORK_CENTER_DATE2--></ibts:UserConfigurableDateTime>\n" +
            "               </ibts:UserConfigurableFields>\n" +
            "            </ibts:LocationUserArea>\n" +
            "         </UserArea>\n" +
            "      </Location>\n" +
            "   </DataArea>\n" +
            "</SyncLocation>";

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        TextMessage textMessage = session.createTextMessage();
        if (object instanceof XmlObject) {
            XmlObject xmlObject = (XmlObject)object;
            String xmlString = xmlObject.toString();
            textMessage.setText(xmlString);
            textMessage.setJMSCorrelationID(UUID.randomUUID().toString());
            //LOGGER.info("Converted the object to xml message : " + xmlString);
            return textMessage;
        }

        textMessage.setText("Test string");
        return textMessage;
    }

    public Object toObject(String xmlString) throws MessageConversionException {
        try {
            return XmlObject.Factory.parse(xmlString);
        } catch (XmlException e) {
           throw new MessageConversionException("", e);
        }
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        Object xmlObject = null;
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String xmlString = null;
            try {
                xmlString = textMessage.getText();
                if (!StringUtils.isEmpty(xmlString)) {
                    xmlObject = toObject(xmlString);
                }
            } catch (JMSException e) {
                throw new MessageConversionException("Conversion failed", e);
            }

        }
        return xmlObject;
    }
}
