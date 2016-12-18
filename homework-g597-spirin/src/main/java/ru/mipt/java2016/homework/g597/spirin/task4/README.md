# Java REST Calculator (in progress)

# Usage

First run application, then follow commands below:

To authorize run in shell:

`curl http://localhost:9001/signup?args="mountain-viewer, 1234567" -X POST -H "Content-Type: text/plain" -H "Authorization: Basic $(echo -n "username:password" | base64)"`


To put variable/function run in shell:

`curl http://localhost:9001/variable/x?value="19" -X PUT -H "Content-Type: text/plain" -H "Authorization: Basic $(echo -n "username:password" | base64)"`

`curl http://localhost:9001/function/sum?valency="2" -X PUT -H "Content-Type: text/plain" -H "Authorization: Basic $(echo -n "username:password" | base64)" --data "x+y"`


To get value of a variable/body of function run in shell:

`curl http://localhost:9001/variable/x -X GET -H "Content-Type: text/plain" -H "Authorization: Basic $(echo -n "username:password" | base64)"`

`curl http://localhost:9001/function/sum -X GET -H "Content-Type: text/plain" -H "Authorization: Basic $(echo -n "username:password" | base64)"`


To evaluate expressions run in shell:

`curl http://localhost:9001/eval -X POST -H "Content-Type: text/plain" -H "Authorization: Basic $(echo -n "username:password" | base64)" --data "44*3+2"`

`curl http://localhost:9001/eval -X POST -H "Content-Type: text/plain" -H "Authorization: Basic $(echo -n "mountain-viewer:1234567" | base64)" --data "sum(sum(3, 4), 4)"`
