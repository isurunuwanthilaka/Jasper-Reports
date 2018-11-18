package DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReportDTO {

    private String orgName;
    private String depName;
    private String devName;
    private String groupName;
    private String dayStart;
    private String dayEnd;
    private int dtoOM;
    private int dtoON;
    private int dtoOL;
    private int dtoDL;
    private int dtoDN;
    private int dtoD;
    private int totalHrs;
    private int dtoOMamount;
    private int dtoONamount;
    private int dtoOLamount;
    private int dtoDLamount;
    private int dtoDNamount;
    private int dtoDamount;
    private int toltalAm;
}
