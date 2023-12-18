package com.cuboidcat.intro;

import com.cuboidcat.intro.bot.botManage;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mongoManager {
    private static MongoClient client;
    private static MongoDatabase database;
    private static MongoCollection servers;

    private JDA bot;
    public mongoManager() {
        client = MongoClients.create("insert connection string here");
        database = client.getDatabase("discord");
        servers = database.getCollection("servers");
        bot = botManage.getBot();
        List<Guild> guilds = bot.getGuilds();
        testIfNew(guilds);
    }

    public static void testIfNew(List<Guild> guilds) {
        for (Guild e: guilds) {
            Boolean flag = false;
            try (MongoCursor cursor = servers.find(Filters.eq("guildID", e.getId())).cursor()) {
                while (cursor.hasNext()) {
                    Document doc = (Document) cursor.next();
                    flag = true;
                }
                if (flag) {
                    continue;
                }
            }
            Document temp = new Document();
            Map<String, String> messages = new HashMap<>();
            temp.put("guildID", e.getId());
            temp.put("introID", "0");
            temp.put("messageID", e.getDefaultChannel().getId());
            temp.put("newRoleID", "0");
            temp.put("pingID", "0");
            temp.put("tempID", "0");
            temp.put("messages", messages);
            servers.insertOne(temp);
        }
    }

    public static void setIntroChannel(String guildID, String introID) {
        Bson updatedvalue  = new Document("introID", introID);
        Bson updateoperation = new Document("$set", updatedvalue);
        servers.updateOne(Filters.eq("guildID", guildID), updateoperation);
    }
    public static void setMessageChannel(String guildID, String messageID) {
        Bson updatedvalue  = new Document("messageID", messageID);
        Bson updateoperation = new Document("$set", updatedvalue);
        servers.updateOne(Filters.eq("guildID", guildID), updateoperation);
    }
    public static void setRole(String guildID, String introID) {
        Bson updatedvalue  = new Document("newRoleID", introID);
        Bson updateoperation = new Document("$set", updatedvalue);
        servers.updateOne(Filters.eq("guildID", guildID), updateoperation);
    }
    public static void setPing(String guildID, String messageID) {
        Bson updatedvalue  = new Document("pingID", messageID);
        Bson updateoperation = new Document("$set", updatedvalue);
        servers.updateOne(Filters.eq("guildID", guildID), updateoperation);
    }
    public static void setTemp(String guildID, String messageID) {
        Bson updatedvalue  = new Document("tempID", messageID);
        Bson updateoperation = new Document("$set", updatedvalue);
        servers.updateOne(Filters.eq("guildID", guildID), updateoperation);
    }

    public static String getNotifcation(String guildID) {
        String channeID = "";
        try (MongoCursor cursor = servers.find(Filters.eq("guildID", guildID)).cursor()){
            while (cursor.hasNext()) {
                Document doc = (Document) cursor.next();
                channeID = (String) doc.get("messageID");
            }
        }
        return channeID;
    }
    public static String getIntro(String guildID) {
        String channeID = "";
        try (MongoCursor cursor = servers.find(Filters.eq("guildID", guildID)).cursor()){
            while (cursor.hasNext()) {
                Document doc = (Document) cursor.next();
                channeID = (String) doc.get("introID");
            }
        }
        return channeID;
    }
    public static String getRole(String guildID) {
        String roleID = "";
        try (MongoCursor cursor = servers.find(Filters.eq("guildID", guildID)).cursor()){
            while (cursor.hasNext()) {
                Document doc = (Document) cursor.next();
                roleID = (String) doc.get("newRoleID");
            }
        }
        return roleID;
    }
    public static String getPing(String guildID) {
        String roleID = "";
        try (MongoCursor cursor = servers.find(Filters.eq("guildID", guildID)).cursor()){
            while (cursor.hasNext()) {
                Document doc = (Document) cursor.next();
                roleID = (String) doc.get("pingID");
            }
        }
        return roleID;
    }
    public static String getTemp(String guildID) {
        String roleID = "";
        try (MongoCursor cursor = servers.find(Filters.eq("guildID", guildID)).cursor()){
            while (cursor.hasNext()) {
                Document doc = (Document) cursor.next();
                roleID = (String) doc.get("tempID");
            }
        }
        return roleID;
    }




    public static Map<String, String> getMessages(String guildID) {
        Map<String, String> list = new HashMap<>();
        try (MongoCursor cursor = servers.find(Filters.eq("guildID", guildID)).cursor()){
            while (cursor.hasNext()) {
                Document doc = (Document) cursor.next();
                list = (Map<String, String>) doc.get("messages");
            }
        }
        return list;
    }

    public static void removeMessage(String guildID, String messageID) {
        Map<String, String> list = new HashMap<>();
        try (MongoCursor cursor = servers.find(Filters.eq("guildID", guildID)).cursor()){
            while (cursor.hasNext()) {
                Document doc = (Document) cursor.next();
                list = (Map<String, String>) doc.get("messages");
            }
        }
        list.remove(messageID);
        Bson updatedvalue  = new Document("messages", list);
        Bson updateoperation = new Document("$set", updatedvalue);
        servers.updateOne(Filters.eq("guildID", guildID), updateoperation);
    }
    public static void addMessage(String guildID, String messageID, String userID) {
        Map<String, String> list = new HashMap<>();
        try (MongoCursor cursor = servers.find(Filters.eq("guildID", guildID)).cursor()){
            while (cursor.hasNext()) {
                Document doc = (Document) cursor.next();
                list = (Map<String, String>) doc.get("messages");
            }
        }
        list.put(messageID, userID);
        Bson updatedvalue  = new Document("messages", list);
        Bson updateoperation = new Document("$set", updatedvalue);
        servers.updateOne(Filters.eq("guildID", guildID), updateoperation);
    }
}

