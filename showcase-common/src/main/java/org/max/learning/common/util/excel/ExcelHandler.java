package org.max.learning.common.util.excel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ExcelHandler extends DefaultHandler {

    private int startRow = 1;

    private int currentRow = 1;

    private StylesTable stylesTable;

    private SharedStringsTable sst;

    private boolean nextIsString;
    // 上一个单元格的内容
    private String lastContents;

    private boolean isBlankSheet = true;
    // 一行数据的列表
    private Map<Integer, String> rowList = new LinkedHashMap<>();
    private Map<Integer, String> headerIndex = new LinkedHashMap<>();

    // 定义一个 List 用来保存所有内容
    private List<Map<String, String>> rows = new ArrayList<>();

    private List<Map<Integer, String>> remainRows = new LinkedList<>();

    // 定义前一个单元格和当前单元格的位置，用来计算其中空的单元格数量
    private String preRef = null, ref = null;

    private CellDataType nextDataType = CellDataType.SSTINDEX;
    private final DataFormatter formatter = new DataFormatter();
    private short formatIndex;
    private String formatString;

    // 用一个枚举表示单元格可能的数据类型
    protected enum CellDataType {
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL
    }



    public ExcelHandler(ExcelReader reader) throws Exception {
        init(reader);
    }

    public ExcelHandler(ExcelReader reader, int startRow) throws Exception {
        this.startRow = startRow;
        init(reader);
    }

    public List<Map<String, String>> getRows() {
        return this.rows;
    }

    public List<Map<Integer, String>> getRemainRows() {
        return this.remainRows;
    }

    /**
     * 解析一个单元格开始时触发事件
     */
    public void startElement(String uri, String localName, String name, Attributes attributes) {
        // c => cell
        if (name.equals("c")) {
            // 前一个单元格的位置
            if (preRef == null) {
                preRef = attributes.getValue("r");
            }
            // 当前单元格的位置
            ref = attributes.getValue("r");
            this.setNextDataType(attributes);
            // 找出这个值是否是SST中的一个索引
            String cellType = attributes.getValue("t");
            nextIsString = cellType != null && cellType.equals("s");
        }
        // 清除上一个单元格的内容
        lastContents = "";
    }

    /**
     * 获取单元格的文本数据
     */
    public void characters(char[] ch, int start, int length) {
        lastContents = lastContents.concat(new String(ch, start, length));
    }

    /**
     * 解析一个单元格结束时触发事件
     */
    public void endElement(String uri, String localName, String name) {

        // if a cell type is s, then should get value from shared string table.
        if (nextIsString) {
            int idx = Integer.parseInt(lastContents);
            lastContents = sst.getItemAt(idx).toString();
            nextIsString = false;
        }

        if (Objects.equals(name, "t")) {
            rowList.put(getColumn(ref), lastContents);
            preRef = ref; //prevent missing blank cell
        } else if (name.equals("v")) { // v => value of a cell
            String value = this.getDataValue(lastContents.trim());
            rowList.put(getColumn(ref), value);
            preRef = ref; //prevent missing blank cell
        } else {
            // <row> means end of a row.
            if (name.equals("row")) {
                isBlankSheet = false;
                if (currentRow == startRow) {
                    boolean isBlank = true;
//                    for (int i = 1; i <= rowList.size(); i ++) {
//                        if (StringUtils.isNotEmpty(rowList.get(i))) {
//                            isBlank = false;
//                        }
//                        headerIndex.put(i, rowList.get(i));
//                    }
                    for (Map.Entry<Integer, String> entry : rowList.entrySet()) {
                        if (StringUtils.isNotEmpty(entry.getValue())) {
                            isBlank = false;
                        }
                        headerIndex.put(entry.getKey(), entry.getValue());
                    }
                    if (rowList.size() == 0) {
                        throw new RuntimeException();
                    }
                } else if (currentRow > startRow) {
                    boolean isBlankRow = true;
                    for (String value: rowList.values()) {
                        if (StringUtils.isNotEmpty(value))
                            isBlankRow = false;
                    }
                    if (isBlankRow) return;
                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 1; i <= headerIndex.size(); i ++) {
                        String value = rowList.get(i);
                        row.put(headerIndex.get(i), value == null ? "" : value);
                    }
                    rows.add(row);
                } else {
                    remainRows.add(new LinkedHashMap<>(rowList));
                }
                rowList.clear();
                preRef = null;
                ref = null;
                currentRow ++;
            }
        }
    }

    public boolean isBlankSheet() {
        return isBlankSheet;
    }

    public Collection<String> getHeaders() {
        return headerIndex.values();
    }

    public Map<Integer, String> getHeaderMap(){
        return headerIndex;
    }

    private void init(ExcelReader reader) throws Exception {
        stylesTable = reader.getStylesTable();
        sst = reader.getSharedStringsTable();
        if (sst == null) {
            throw new RuntimeException();
        }
    }

    /**
     * 根据单元格属性设置数据类型
     *
     */
    private void setNextDataType(Attributes attributes) {
        nextDataType = CellDataType.NUMBER;
        formatIndex = -1;
        formatString = null;
        String cellType = attributes.getValue("t");
        String cellStyleStr = attributes.getValue("s");
        if (null != cellType) {
            switch (cellType) {
                case "b":
                    nextDataType = CellDataType.BOOL;
                    break;
                case "e":
                    nextDataType = CellDataType.ERROR;
                    break;
                case "s":
                    nextDataType = CellDataType.SSTINDEX;
                    break;
                case "inlineStr":
                    nextDataType = CellDataType.INLINESTR;
                    break;
                case "str":
                    nextDataType = CellDataType.FORMULA;
                    break;
            }
        }

        if (cellStyleStr != null) {
            int styleIndex = Integer.parseInt(cellStyleStr);
            XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
            formatIndex = style.getDataFormat();
            formatString = style.getDataFormatString();
            if (Objects.equals("m/d/yy", formatString)) {
//                nextDataType = CellDataType.DATE;
//                //"yyyy-MM-dd hh：mm：ss.SSS"
                formatString = "yyyy-MM-dd";
            }
            if (formatString == null) {
                nextDataType = CellDataType.NULL;
                formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
            }
        }
    }

    /**
     * 根据数据类型获取数据
     */
    private String getDataValue(String value) {
        String thisStr;
        switch (nextDataType) {
            // follow the steps below.
            case BOOL:
                char first = value.charAt(0);
                thisStr = first == '0' ? "FALSE" : "TRUE";
                break;
            case ERROR:
                thisStr = "\"ERROR:" + value + '"';
                break;
            case FORMULA:
                thisStr = value;
                break;
            case INLINESTR:
                XSSFRichTextString rtsi = new XSSFRichTextString(value);
                thisStr = rtsi.toString();
                break;
            case SSTINDEX:
                thisStr = value;
                break;
            case NUMBER:
                if (formatString != null) {
                    thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString).trim();
                } else {
                    thisStr = value.trim();
                }
                break;
            default:
                thisStr = "";
                break;
        }
        return thisStr;
    }

    private int getColumn(String content) {
        content = content.replaceAll("\\d+", "");
        int num;
        int result = 0;
        int length = content.length();
        for(int i = 0; i < length; i++) {
            char ch = content.charAt(length - i - 1);
            num = ch - 'A' + 1;
            num *= Math.pow(26, i);
            result += num;
        }
        return result;
    }
}
