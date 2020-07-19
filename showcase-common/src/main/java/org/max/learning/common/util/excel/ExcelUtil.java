package org.max.learning.common.util.excel;

import java.io.File;
import java.io.InputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.XMLHelper;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class ExcelUtil {

    /**
     *
     * @param file Excel File (.xlsx).
     * @param sheetIndex index of sheet num. 0 -> first sheet.
     * @param startRow the header's row index. start from 1.
     * @return ExcelHandler data summary of Excel. could get all structures in Excel.
     */
    public static ExcelHandler processSheet(File file, int sheetIndex, int startRow) throws Exception {
        ExcelReader reader = new ExcelReader(OPCPackage.open(file, PackageAccess.READ));
        XMLReader parser = XMLHelper.newXMLReader();
        parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
        parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        ExcelHandler handler = new ExcelHandler(reader, startRow);
        parser.setContentHandler(handler);
        try (InputStream input = reader.getSheet(sheetIndex)) {
            parser.parse(new InputSource(input));
        }
        return handler;
    }

    /**
     *
     * @param input An sheet of Excel. Process Excel by Sheet accordingly
     * @param reader a Excel Reader object which analyzes an Excel File
     * @param startRow the header's row index. start from 1.
     * @return ExcelHandler data summary of Excel. could get all structures in Excel.
     */
    public static ExcelHandler processSheet(InputStream input, ExcelReader reader, int startRow) throws Exception {
        try {
            XMLReader parser = XMLHelper.newXMLReader();
            parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            ExcelHandler handler = new ExcelHandler(reader, startRow);
            parser.setContentHandler(handler);
            parser.parse(new InputSource(input));
            return handler;
        } finally {
            input.close();
        }
    }

    /**
     *
     * @param inputStream an input stream of Excel.
     * @param sheetIndex index of sheet num. 0 -> first sheet.
     * @param startRow the header's row index. start from 1.
     * @return ExcelHandler data summary of Excel. could get all structures in Excel.
     */
    public static ExcelHandler processSheet(InputStream inputStream, int sheetIndex, int startRow) throws Exception {
        OPCPackage pack = OPCPackage.open(inputStream);
        try {
            if (pack.getParts() == null) {
                pack.getParts();
            }
        } catch (InvalidFormatException | RuntimeException e) {
            IOUtils.closeQuietly(pack);
            throw e;
        }
        ExcelReader reader = new ExcelReader(pack);
        XMLReader parser = XMLHelper.newXMLReader();
        parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
        parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        ExcelHandler handler = new ExcelHandler(reader, startRow);
        parser.setContentHandler(handler);
        try (InputStream input = reader.getSheet(sheetIndex)) {
            parser.parse(new InputSource(input));
        }
        return handler;
    }

}
