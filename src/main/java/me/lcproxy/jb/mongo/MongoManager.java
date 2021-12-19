package me.lcproxy.jb.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class MongoManager {
    private final MongoClient client;
    private DB database;
    private DBCollection profileCollection;

    @SneakyThrows
    public MongoManager() {
        // Obtain a free mongodb from clever-cloud
        MongoClientURI uri = new MongoClientURI(
                "mongoURI here");
        this.client = new MongoClient(uri);

        try {
            this.database = this.client.getDB("LCProxy");
            this.profileCollection = this.database.getCollection("profiles");
            System.out.println("Loaded mongo successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
