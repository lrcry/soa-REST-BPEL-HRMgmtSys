# soa-REST-BPEL-HRMgmtSys
Group Name: DadImWrong

Group Member: z5002065, Qian Weng

Group Member: z5003823, Ran Meng

Details of Collaboration:

Qian: 
    Mainly deals with the rest, soap, bpel part of the assignment.
    
    In the rest service part, Qian completes the job posting, review model. And in soap part, Qian completes all the pdv and crv soap service. In bpel part, he deploy the environment for ode server, and complete the whole bpel process for the assignment. 
    
    For all the parts above, he does the tests for them.

Ran:
  

This project is a set of RESTful service of a Human Resources Management System which constructed based on REST &amp; BPEL.  
It is constructed with Spring MVC, Spring HATEOAS project.  
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
### Users
- Get all the users  
```GET /users```  
With this URL you can get all the users information responsed in a JSON array.  
- Get a user by its ID  
```GET /users/{_uid}```  
This URL will response with a JSON object of a user.  
```
{
  "user": {
    "_uid": "004",
    "_pwd": "004",
    "shortKey": "004-reviewer",
    "lastName": "Zhang",
    "firstName": "Dang",
    "role": "reviewer",
    "department": "SAMBA Project"
  },
  "links": [
    {
      "rel": "self",
      "href": "http://localhost:8080/HRMgmtSysREST/users/004"
    }
  ]
}
```  
If the user ID does not exist in database, an error message will be returned in a JSON:  
```
{
  "errCode": "USER_NOT_FOUND",
  "errMessage": "User with _uid=111 not found in database."
}
```
- Get users who belongs to a given hiring team  
```GET /users?hireTeam={hireTeam}```  
This will retrieve all users involved in the given hiring team in a JSON array.  
```
[
  {
    "user": {
      "_uid": "004",
      "_pwd": "004",
      "shortKey": "004-reviewer",
      "lastName": "Zhang",
      "firstName": "Dang",
      "role": "reviewer",
      "department": "SAMBA Project"
    },
    "links": [
      {
        "rel": "self",
        "href": "http://localhost:8080/HRMgmtSysREST/users/004"
      }
    ]
  },
  {
    "user": {
      "_uid": "005",
      "_pwd": "005",
      "shortKey": "005-reviewer",
      "lastName": "Jones",
      "firstName": "Henry",
      "role": "reviewer",
      "department": "SAMBA Project"
    },
    "links": [
      {
        "rel": "self",
        "href": "http://localhost:8080/HRMgmtSysREST/users/005"
      }
    ]
  }
]
```  
### Job applications  
There are  different status of each application shown below:  
```
APP_SUBMITTED_NOT_PROCESSED, // can update
APP_AUTO_PROCESSED, 
APP_REVIEWING, 
APP_REVIEWED, 
APP_SHORTLISTED, 
APP_ACCEPTED_BY_CANDIDATE,
APP_INTERVIEW_PASSED, // can archive
APP_INTERVIEW_FAILED, // can archive
APP_REJECTED_BY_CANDIDATE, // can archive
APP_NOT_SHORTLISTED, // can archive
APP_CANCELLED, // can archive
APP_ARCHIVED; // cannot update or archive
```
- Get all applications:  
```
GET /jobapplications
```  
- Get applications by ID:  
```
GET /jobapplications/{_appId}
```  
- Get applications for a job ID:  
```GET /jobapplications?_jobId={_jobId}```  
- Create a new job application:  
```POST /jobapplications```  
  This method must be requested with a JSON body:  
```
{
  "_jobId": "1",
  "driverLicenseNumber": "e87654321",
  "fullName": "Hans Mong",
  "postCode": "2018",
  "textCoverLetter": "test text cover letter if it can run.",
  "textBriefResume": "test brief resume here"
}
```  
  textCoverLetter and textBriefResume are not required.  
- Update an existing application by its ID:  
```
PUT /jobapplications/{_appId}
```  
  Only when the application is in a status of ```APP_SUBMITTED_NOT_PROCESSED``` the update method is valid.  
- Update status of an existing application by its ID:  
```
PUT /jobapplications/status/{_appId}?status={status}
```  
  This method is requested with a parameter ```status```.  
- Archive an application:  
```
DELETE /jobapplications/{_appId}
```  
  Only when the application is in one of the following status it can be archived:  
```
APP_INTERVIEW_PASSED, // can archive
APP_INTERVIEW_FAILED, // can archive
APP_REJECTED_BY_CANDIDATE, // can archive
APP_NOT_SHORTLISTED, // can archive
APP_CANCELLED, // can archive
```
  
### BPEL Processes: AutoCheck
  All files are packed in AutoCheck.zip, they should be upzipped and put under path: 
  ```
  %TOMCAT_6%/webapps/ode/WEB-INF/processes
  ```
  
  
