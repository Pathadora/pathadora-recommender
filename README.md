# pathadora

## Send requests
The server will expect three types of request on localhost:8080/pathadora URL. The requests will contain a json file that will be parsed on server side.
```json
{
    "action": "add", 

    "type": "learner",
  
    "class": "Learner",

  "annotation_properties":{
        "name": "Rossi",
        "last_name": "Guri",
        "birthdaye": 23,
        "gender": "male"
    },

    "object_properties":{
        "canRead": "yes",
        "canWrite": "yes"
    }
    
 }

```
```sh
action: [add, next_path, path_generation] 
type: [learner, course, lesson] 
```
In the json nested section, under annotations or object properties, can be declared any information. The information will be inserted into the ontology.
