package com.millenniumitesp.report;

import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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
    public ReportApi(ReportService reportService){
        this.reportService = reportService;
    }


    //http://localhost:8080/api/reports?date=2018-07-30&type=pdf
    @GetMapping(value = "reports")
    @ResponseBody
    public HttpEntity<byte[]> getReport(@RequestParam(value = "date") String date,
                                        @RequestParam(value = "type") String type,
                                        final HttpServletResponse response) throws JRException, IOException, ClassNotFoundException {
        LocalDate localDate = LocalDate.parse(date);
        log.info("Request Received for Report");

        return reportService.getReport(localDate,type);
    }

}
