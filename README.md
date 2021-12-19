# Backend
Backend which handles emotes and cosmetics

# NOTICE MAKE SURE THE PROXY VPS/PC IS DIFFERENT TO THE BACKEND VPS/PC

# Installation / Setup
- git clone https://github.com/LunarProxy/Backend
- cd Backend
- Goto line 20 in mongo/MongoManager.java (https://github.com/LunarProxy/Backend/blob/main/src/main/java/me/lcproxy/jb/mongo/MongoManager.java#L20) and edit to your credentials
- Goto line 27 in Discord.java (https://github.com/LunarProxy/Backend/blob/main/src/main/java/me/lcproxy/jb/Discord.java#L27) and edit to your discord bot token!
- mvn clean install
- cd target
- java -jar lcProxyJB-1.0-SNAPSHOT.jar (if file is a different name just replace lcProxyJB-1.0-SNAPSHOT.jar to the file name)
