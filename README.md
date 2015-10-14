# soa-REST-BPEL-HRMgmtSys
This project is a set of RESTful service of a Human Resources Management System which constructed based on REST &amp; BPEL.  
## Build and package via Maven  
- Make sure you have JDK/JRE 1.7, Apache Maven 3 and Apache Tomcat 7 installed on your computer or server.  
- Clone the project to some place on your computer.  
- In the root directory of the project, run the following command to compile and package the project:  
  ```mvn package```  
- Find the WAR package built in the following folder:  
  ```target/HRMgmtSysREST.war```  
- Move it to your Tomcat webapp folder.
- Start your Tomcat. In an opening browser or Postman, type  
  ```http://localhost:8080/HRMgmtSysREST/```  
which will give you a "Hello REST" welcome page.
## API documentation  
