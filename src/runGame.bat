call cleanFolder.bat

javac MyBot.java
javac MyBotOld1.java
javac RandomBot.java
.\halite.exe -d "60 60" "java MyBot" "java MyBotOld1" "java MyBot" "java MyBotOld1"
pause