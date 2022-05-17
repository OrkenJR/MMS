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


### Get medicines list 
curl --location --request GET 'localhost:8080/medicine/listMedicine' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdA==' \
--data-raw '["doctor"]'

### Get diseases list
curl --location --request GET 'localhost:8080/medicine/listDiseases' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdA=='

### Get treatments list
curl --location --request GET 'localhost:8080/medicine/listTreatment' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdA=='


### Get current user's treatment list
curl --location --request GET 'localhost:8080/medicine/myTreatments' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic Y3VzdG9tZXI6dGVzdA=='


### Get treatments by disease name
curl --location --request GET 'localhost:8080/medicine/treatmentsByDisease?name=Pneumonia' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic ZG9jdG9yOnRlc3Q='

### Create new disease
curl --location --request POST 'localhost:8080/medicine/disease' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdA==' \
--data-raw '{
"name": "disease"
}'

### Create new medicine
curl --location --request POST 'localhost:8080/medicine' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdA==' \
--data-raw '{
"name": "testkoi",
"price": 1000,
"diseases":[
{
"name":"testkoi"
}
]
}'

### Delete medicine by id
curl --location --request DELETE 'localhost:8080/medicine/23' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdA=='

### Delete disease by id
curl --location --request DELETE 'localhost:8080/medicine/disease/13' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdA=='

### Make an appointment with a doctor
curl --location --request POST 'localhost:8080/medicine/getTreatment' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic Y3VzdG9tZXI6dGVzdA==' \
--data-raw '{
"name": "cold"
}'

### Buy medicine 
curl --location --request POST 'localhost:8080/medicine/buyMedicine?medicineName=Aspirin' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic Y3VzdG9tZXI6dGVzdA=='