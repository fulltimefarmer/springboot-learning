package org.max.learning.common.controller;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;

import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
public class DownloadController {

	@ApiOperationSupport(author = "Max")
    @ApiOperation(value = "download file", notes = "")
    @GetMapping("/download")
    public void downloadReport(
            HttpServletRequest request,
            HttpServletResponse response) {
        try (ServletOutputStream out = response.getOutputStream()) {
            //TODO
            byte[] fileBytes = new byte[0];
            out.write(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}
