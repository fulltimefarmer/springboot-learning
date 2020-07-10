package org.max.learning.common.controller;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    public ResponseEntity<String> upload(@RequestPart(name = "file") MultipartFile file) {
		try {
			XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
			XSSFSheet ws = wb.getSheetAt(0);
			System.out.println(file.getOriginalFilename());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
