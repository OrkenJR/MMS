### Get users list

curl --location --request GET 'localhost:8080/users/list' \
--header 'Authorization: Basic YWRtaW46dGVzdA=='

### Get user by username
curl --location --request GET 'localhost:8080/users/byUsername/doctor' \
--header 'Authorization: Basic YWRtaW46dGVzdA=='

### Creating new user 
curl --location --request POST 'localhost:8080/users' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdA==' \
--data-raw '{
"firstName": "fname",
"lastName": "lname",
"email": "email",
"username": "username",
"password": "password"
}'


### Deleting user by id
curl --location --request DELETE 'localhost:8080/users?userId=6' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdA==' \
--data-raw '{
"firstName": "fname",
"lastName": "lname",
"email": "email",
"username": "username",
"password": "password"
}'


### Deleting roles from user
curl --location --request DELETE 'localhost:8080/users/deleteRoles?userId=7' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdA==' \
--data-raw '["doctor"]'




