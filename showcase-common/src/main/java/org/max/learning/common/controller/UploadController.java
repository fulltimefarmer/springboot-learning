package org.max.learning.common.controller;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.max.learning.common.util.excel.ExcelHandler;
import org.max.learning.common.util.excel.ExcelReader;
import org.max.learning.common.util.excel.ExcelUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;

import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
public class UploadController {

	@ApiOperationSupport(author = "Max")
	@ApiOperation(value = "upload file", notes = "")
	@PostMapping("/upload")
	public ResponseEntity<String> upload(HttpServletRequest request, HttpServletResponse response,
			@RequestPart(name = "file") MultipartFile file) {
		try {
			OPCPackage opcp = OPCPackage.open(file.getInputStream());
			ExcelReader reader = new ExcelReader(opcp);
			ExcelHandler handler = ExcelUtil.processSheet(reader.getSheet(0), reader, 1);
			List<Map<String, String>> rows = handler.getRows();
			int count = 0;
            for (Map<String, String> row : rows) {
            	for(Entry<String, String> entry : row.entrySet()) {
            		System.out.println(entry.getKey() + " : " + entry.getValue());
            	}
            	System.out.println("===================");
            	count++;
            }
            System.out.println("total count: " + count);

//			InputSource sheetSource = new InputSource(sheetInputStream);
//	        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
//	        SAXParser saxParser = saxFactory.newSAXParser();
//	        XMLReader sheetParser = saxParser.getXMLReader();
//	        ContentHandler handler = new XSSFSheetHandler(styles, strings,this.minColumns, this.output, this.clazz);
//	        sheetParser.setContentHandler(handler);
//	        sheetParser.parse(sheetSource);

//			XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
//			XSSFSheet ws = wb.getSheetAt(0);
//			int max = ws.getPhysicalNumberOfRows();
//			System.out.println("Max row: "+max);
//			int count = 0;
//			for(int i=4;i<max;i++) {
//				Row row = ws.getRow(i);
//				System.out.println(row.getCell(0).toString());
//				count++;
//			}
//			System.out.println("Total count: "+count);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.gc();
		}
		return null;
	}

}
