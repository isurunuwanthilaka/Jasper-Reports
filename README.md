## Jasper-Reports
Sample Codes for Jasper Reporting with SpringBoot (Java)

##Descreiption
This is for generating reports : pdf,xslx,csv

## features:
1. geting data from database
2. update from existing collection of data
3. generating reports from .jrxml
4. exporting to given path
5. exporting to web browser
6. can be downloaded through web browser or directly get the saved report at the relevent path.

## Issues
When deploying docker images some fonts cannot parse from the jasper report classes, because Open JDK 8 - alpine have bugs. Therefore build from OpenJDk 7 versions or OpenJRE 8
