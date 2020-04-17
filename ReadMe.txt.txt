Hi,

The framework is developed using Maven and TESTNG

All the java codes comes under src/test/java under which we have 3 packages:

context->To Store Temporary Values
utils->read data from JSON Files and Property files
test->All API tests


You can run all the test by MVN Command "mvn test"
***************************************************************

Defects:
1. Even if Phone Number is Wrong  API /api/v1/users/{phone} Request gives status code as 200
2. Even though new token is generated user can still get response with old token (Token Expiration Issue)



