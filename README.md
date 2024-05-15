# Chore-Champions-In-Memory-Database
### What is this?
A short full stack project using an H2 in memory database. Build in Java and SQL, utilising maven and springboot to host the database temporarily.

This project contains GUI elements interacting with a database in the backend. It works to both insert new entries into the database and select existing entries from the database. It makes use of PreparedStatements and has numerous try catch blocks to handle all manner of sql errors.

 ### Why make this project?
This project was used to test my knowledge of databases, temporary in-memory ones in this case. It also tested my ability to connect front end to back end, utilising the java GUI tools to create interactions between users and the database I learned a lot about full stack development and databases as well as JBDI functionality. It also made me aware of what H2 and SpringBoot are and how they are used. 

My knowledge of build tools was almost specifically gradle so I decided to branch out and use something different just to see what I was missing and learn something new, hence the use of Maven.

### How to build the project
I'm running the SpringBoot extension within VSCode to get this built and running so I would reccomend you do the same. The extension can be found by a quick search within the extensions library. From there simply fork the repo and you should be able to build the application using the SpringBoot dashboard's "run" button.

### Where could I improve?
* Error Handling: Because this was more a test and experiment with database interaction I did not focus on making the GUI error free. If I was to go through and make improvements my first stop would be adding checks that input fields are always getting the correct values. For example I would check that the input ID and number of chores were both Integers otherwise these will obviously cause errors.
* GUI: Going into the future I may attempt the rest of my GUI work in html, css and js. While the java GUI libraries are simple and allow for coding quickly they are far from what I will see and use in the actual industry so it may be better to focus my work on more widely used GUI languages and frameworks.
