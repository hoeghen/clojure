# minlokalebutik backend - clojure 2019    

A clojure based gateway to load from minlokalebutik to firebase
Also it provides cleaning of the firebase

## Usage
###code
    https://github.com/hoeghen/shopgungateway

###build
    lein uberjar
 
###run
    java -jar minlokalebutik-0.1.0-standalone.jar [args]

### host as lambda on aws
    https://console.aws.amazon.com/lambda/home?region=us-east-1#/functions/minlokalebutik-daily-update?tab=configuration
    
### loads into firebase
    https://console.firebase.google.com/u/0/project/firebase-minlokalebutik/database/minlokalebutik/data    