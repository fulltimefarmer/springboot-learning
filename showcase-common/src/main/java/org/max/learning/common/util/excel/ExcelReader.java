package org.max.learning.common.util.excel;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ExcelReader extends XSSFReader{

    private List<XSSFSheetRef> validSheets;

    private Map<String, XSSFSheetRef> validSheetMap;

    /**
     * Creates a new XSSFReader, for the given package
     */
    public ExcelReader(OPCPackage pkg) throws IOException, OpenXML4JException {
        super(pkg);

        XMLSheetRefReader xmlSheetRefReader = new XMLSheetRefReader();
        XMLReader xmlReader;
        try {
            xmlReader = XMLHelper.newXMLReader();
        } catch (ParserConfigurationException | SAXException e) {
            throw new POIXMLException(e);
        }
        xmlReader.setContentHandler(xmlSheetRefReader);
        try {
            xmlReader.parse(new InputSource(workbookPart.getInputStream()));
        } catch (SAXException e) {
            throw new POIXMLException(e);
        }

        validSheets = new ArrayList<>();
        validSheetMap = new HashMap<>();
        for (XSSFSheetRef xssfSheetRef : xmlSheetRefReader.getSheetRefs()) {
            //if there's no relationship id, silently skip the sheet
            String sheetId = xssfSheetRef.getId();
            if (sheetId != null && sheetId.length() > 0) {
                validSheets.add(xssfSheetRef);
                validSheetMap.put(xssfSheetRef.getName(), xssfSheetRef);
            }
        }
    }


    /**
     * Returns an InputStream to read the contents of the
     *  specified Sheet.
     * @param index The index of the sheet, from a r:id on the workbook
     */
    public InputStream getSheet(int index)throws IOException, InvalidFormatException {
        if (index > validSheets.size() + 1) {
            return null;
        }
        XSSFSheetRef sheetRef = validSheets.get(index);
        PackageRelationshipCollection relationships = workbookPart.getRelationships();
        PackageRelationship rel = relationships.getRelationshipByID(sheetRef.getId());
        PackagePartName relName = PackagingURIHelper.createPartName(rel.getTargetURI());
        PackagePart sheet = pkg.getPart(relName);
        if(sheet == null) {
            throw new IllegalArgumentException("No data found for Sheet with r:id " + sheetRef.getId());
        }
        return sheet.getInputStream();
    }

    /**
     * Returns an InputStream to read the contents of the
     *  specified Sheet.
     * @param name The name of the sheet, from a name on the workbook
     */
    public InputStream getSheet(String name)throws IOException, InvalidFormatException {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        XSSFSheetRef sheetRef = validSheetMap.get(name);
        if (sheetRef == null)
            return null;
        PackageRelationshipCollection relationships = workbookPart.getRelationships();
        PackageRelationship rel = relationships.getRelationshipByID(sheetRef.getId());
        PackagePartName relName = PackagingURIHelper.createPartName(rel.getTargetURI());
        PackagePart sheet = pkg.getPart(relName);
        if(sheet == null) {
            throw new IllegalArgumentException("No data found for Sheet with r:id " + sheetRef.getId());
        }
        return sheet.getInputStream();
    }

    //scrapes sheet reference info and order from workbook.xml
    private static class XMLSheetRefReader extends DefaultHandler {
        private static final String SHEET = "sheet";
        private static final String ID = "id";
        private static final String NAME = "name";

        private final List<XSSFSheetRef> sheetRefs = new LinkedList<>();

        // read <sheet name="Sheet6" sheetId="4" r:id="rId6"/>
        // and add XSSFSheetRef(id="rId6", name="Sheet6") to sheetRefs
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
            if (localName.equalsIgnoreCase(SHEET)) {
                String name = null;
                String id = null;
                for (int i = 0; i < attrs.getLength(); i++) {
                    final String attrName = attrs.getLocalName(i);
                    if (attrName.equalsIgnoreCase(NAME)) {
                        name = attrs.getValue(i);
                    } else if (attrName.equalsIgnoreCase(ID)) {
                        id = attrs.getValue(i);
                    }
                    if (name != null && id != null) {
                        sheetRefs.add(new XSSFSheetRef(id, name));
                        break;
                    }
                }
            }
        }

        List<XSSFSheetRef> getSheetRefs() {
            return Collections.unmodifiableList(sheetRefs);
        }
    }

    public int getSheetNums() {
        return validSheets.size();
    }
}
