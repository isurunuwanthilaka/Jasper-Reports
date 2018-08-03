package com.edge.reports.api;

import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequestMapping(path = "/api")
@ResponseBody
public class ReportApi {

    private final Logger log = LoggerFactory.getLogger(ReportApi.class);


    private final ReportService reportService;

    @Autowired
    public ReportApi(ReportService reportService) {
        this.reportService = reportService;
    }


    @RequestMapping(value = "reports", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity onReportRequest(@RequestParam(value = "date") String date){
        LocalDate localDate = LocalDate.parse(date);
        log.info("Data Passed to Front Application for reporting.");
        return ResponseEntity.ok(reportService.getDailyStats(localDate));
    }

//    @RequestMapping(value = "reports/download", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity onReportDownloadRequest(@RequestParam(value = "date") String date,
//                                                  @RequestParam(value = "type") String type){
//        LocalDate localDate = LocalDate.parse(date);
//        log.info("Request received for downloadable reports.");
//        return ResponseEntity.ok(reportService.downloadDailyStats(localDate,type));
//    }

    //testing some stuffs
    @GetMapping(value = "reports/download")
    @ResponseBody
    public HttpEntity<byte[]> getViewReportAndPrintReport(@RequestParam(value = "date") String date,
                                                          @RequestParam(value = "type") String type,
                                                          final HttpServletResponse response) throws JRException, IOException, ClassNotFoundException {
        LocalDate localDate = LocalDate.parse(date);
        log.info("Request received for downloadable reports.");

        return reportService.downloadDailyStats(localDate,type);
    }

}
