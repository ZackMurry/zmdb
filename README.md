# zmdb
A database platform that operates with HTTP requests. Made with Java Spring.

# running
First, download the .jar from the latest release. To run without viewing logs, just run the .jar. To view logs, run the file in cmd by typing "java -jar 'FILEPATH'", where FILEPATH is the location where the .jar was downloaded.

# starting out
Download an HTTP request sender to start out (like Postman). You can make your first database by sending a POST request to localhost:9001/databases with a body of:  
{  
"name": "myFirstDatabase"  
}  

This will create a database with name "myFirstDatabase". You can find further documentation in [HELP.md](HELP.md).
