##Run the tests:##

    sbt test


##Run the application:##

    sbt run


##Test requests:##

    $ curl -X POST -H "Content-Type: application/json" -d '{
        "title": "Boo",
        "author": "Dr. Yada",
        "topic": "Banana banana",
        "content": "book"
    }' 'http://localhost:9000/watermark'


	$ curl -X GET 'http://localhost:9000/watermark/cfc6f574-9597-4bde-b500-f8e6504ee4f2'



