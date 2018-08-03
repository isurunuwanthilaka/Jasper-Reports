package com.edge.reports.api;

import com.millenniumit.iotedge.analytics.model.CountByHourEntity;
import com.millenniumit.iotedge.analytics.model.CountByHourRepository;
import com.millenniumit.iotedge.analytics.utils.JsonHelper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReportService {

    private CountByHourRepository countByHourRepository;
    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    public ReportService(CountByHourRepository countByHourRepository) {
        this.countByHourRepository = countByHourRepository;
    }

    public String getDailyStats(LocalDate date){
        List<CountByHourEntity> records = countByHourRepository.findAllByDateOrderByLocation(date);
        List map = new ArrayList();
        records.forEach(v -> {
            try {
                Map m = new HashMap<String, Object>();
                m.put("location", v.getLocation());
                m.put("time", v.getHourRange());
                m.put("count", v.getCount());
                map.add(m);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        });

        return JsonHelper.mapToJson2(map);
    }

    public HttpEntity downloadDailyStats(LocalDate date, String type) throws IOException {
        List<CountByHourEntity> records = countByHourRepository.findAllByDateOrderByLocation(date);
        String msg;

        //new jasper collection of countbyhourentity
        JRBeanCollectionDataSource jasperRecords = new JRBeanCollectionDataSource(records);

        //jasper report implementation
        InputStream employeeReportStream = getClass().getResourceAsStream("/ReportTemplate.jrxml");
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
            //JRSaver.saveObject(jasperReport, "ReportTemplate.jasper");

            //populating some required data
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("title", "Daily Report - " + date.toString());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,jasperRecords);

            //web view
            HttpHeaders header = new HttpHeaders();
            final byte[] data;

            switch (type){
                case "pdf":
                    //exporting pdf
                    JRPdfExporter exporterPDF = new JRPdfExporter();

                    exporterPDF.setExporterInput(new SimpleExporterInput(jasperPrint));
                    exporterPDF.setExporterOutput(new SimpleOutputStreamExporterOutput("DailyReport-" + date.toString() + ".pdf"));

                    SimplePdfReportConfiguration reportConfigPDF = new SimplePdfReportConfiguration();
                    reportConfigPDF.setSizePageToContent(true);
                    reportConfigPDF.setForceLineBreakPolicy(false);

                    SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
                    exportConfig.setMetadataAuthor("Millennium IoT");
                    exportConfig.setEncrypted(true);
                    exportConfig.setAllowedPermissionsHint("PRINTING");

                    exporterPDF.setConfiguration(reportConfigPDF);
                    exporterPDF.setConfiguration(exportConfig);

                    exporterPDF.exportReport();
                    log.info("REPORT : PDF File sent to Report Folder");

                    //exporting data to web browser
                    data = JasperExportManager.exportReportToPdf(jasperPrint);

                    header.setContentType(MediaType.APPLICATION_PDF);
                    header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=DailyReport.pdf");
                    header.setContentLength(data.length);

                    log.info("REPORT : PDF File sent to web browser");

                    break;
                case "xlsx":
                    JRXlsxExporter exporterXLS = new JRXlsxExporter();

                    exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));
                    exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput("DailyReport-" + date.toString() + ".xlsx"));


                    SimpleXlsxReportConfiguration reportConfigXLS= new SimpleXlsxReportConfiguration();
                    reportConfigXLS.setSheetNames(new String[] { "Daily Report Data" });

                    exporterXLS.setConfiguration(reportConfigXLS);
                    exporterXLS.exportReport();

                    log.info("REPORT : EXCEL File sent to Report Folder");

                    //sending excel file to browser
                    JRXlsxExporter xlsxExporter = new JRXlsxExporter();
                    final byte[] rawBytes;

                    try(ByteArrayOutputStream xlsReport = new ByteArrayOutputStream()){
                        xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsReport));
                        xlsxExporter.exportReport();

                        rawBytes = xlsReport.toByteArray();
                    }
                    data= rawBytes;
                    header.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                    header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=DailyReport.xlsx");
                    header.setContentLength(data.length);

                    log.info("REPORT : EXCEL File sent to web browser");

                    break;

                case "csv":
                    //creating csv file
                    JRCsvExporter exporterCSV = new JRCsvExporter();

                    exporterCSV.setExporterInput(new SimpleExporterInput(jasperPrint));
                    exporterCSV.setExporterOutput(new SimpleWriterExporterOutput("Reports/DailyReport-" + date.toString() + ".csv"));

                    exporterCSV.exportReport();

                    log.info("REPORT : CSV File sent to Report Folder");

                    //sending PDF to browser : why? No csv view support for browser : need to research more !
                    data = JasperExportManager.exportReportToPdf(jasperPrint);

                    header.setContentType(MediaType.APPLICATION_PDF);
                    header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=DailyReport.pdf");
                    header.setContentLength(data.length);

                    log.info("REPORT : PDF File sent to web browser");
                    break;
                default:
                    data = JasperExportManager.exportReportToPdf(jasperPrint);
                    header.setContentType(MediaType.APPLICATION_PDF);
                    header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=DailyReport.pdf");
                    header.setContentLength(data.length);
                    log.info("REPORT : No File Created , PDF sent to browser.");
                    break;
            }
            return new HttpEntity<byte[]>(data, header);
        } catch (JRException e) {
            e.printStackTrace();
            log.error(e.getMessage());

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_PDF);
            header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=DailyReport.pdf");
            header.setContentLength(10);

            log.error("REPORT : No File Created , empty PDF sent to browser.");
            return new HttpEntity<byte[]>(header);
        }


    }

}
