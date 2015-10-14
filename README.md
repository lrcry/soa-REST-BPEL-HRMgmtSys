# soa-REST-BPEL-HRMgmtSys
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
  ```GET /users  
With this URL you can get all the users information responsed in a JSON array like the following:
```
[
  {
    "user": {
      "_uid": "001",
      "_pwd": "001",
      "shortKey": "001-manager",
      "lastName": "Nima",
      "firstName": "Nishad",
      "role": "manager",
      "department": "HR"
    },
    "links": [
      {
        "rel": "self",
        "href": "http://localhost:8080/HRMgmtSysREST/users/001"
      }
    ]
  },
  {
    "user": {
      "_uid": "002",
      "_pwd": "002",
      "shortKey": "002-reviewer",
      "lastName": "Chopra",
      "firstName": "Mimi",
      "role": "reviewer",
      "department": "AIMS Project"
    },
    "links": [
      {
        "rel": "self",
        "href": "http://localhost:8080/HRMgmtSysREST/users/002"
      }
    ]
  },
  {
    "user": {
      "_uid": "003",
      "_pwd": "003",
      "shortKey": "003-reviewer",
      "lastName": "Danks",
      "firstName": "Tara",
      "role": "reviewer",
      "department": "AIMS Project"
    },
    "links": [
      {
        "rel": "self",
        "href": "http://localhost:8080/HRMgmtSysREST/users/003"
      }
    ]
  },
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
This will retrieve all users involved in the given hiring team.
