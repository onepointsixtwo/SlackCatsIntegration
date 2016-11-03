
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author johnkartupelis
 */
public class SlackData {
    private String token = null;
    private String teamId = null;
    private String teamDomain = null;
    private String channelId = null;
    private String channelName = null;
    private String userId = null;
    private String userName = null;
    private String command = null;
    private String text = null;
    private String responseUrl = null;
    private Logger logger;
    
    public static SlackData slackDataFromPostBody(String body, Logger logger) {
        String token = null;
        String teamId = null;
        String teamDomain = null;
        String channelId = null;
        String channelName = null;
        String userId = null;
        String userName = null;
        String command = null;
        String text = null;
        String responseUrl = null;
        
        if(body != null) {
            String[] lines = body.split("&");
            for(String line : lines) {
                String[] keyValue = line.split("=");
                if(keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    logger.log(Level.INFO, "Key=" + key + " and Value=" + value);
                    if(key.equals("token")) {
                        token = value;
                    } else if(key.equals("team_id")) {
                        teamId = value;
                    } else if(key.equals("team_domain")) {
                        teamDomain = value;
                    } else if(key.equals("channel_id")) {
                        channelId = value;
                    } else if(key.equals("channel_name")) {
                        channelName = value;
                    } else if(key.equals("user_id")) {
                        userId = value;
                    } else if(key.equals("user_name")) {
                        userName = value;
                    } else if(key.equals("command")) {
                        command = value;
                    } else if(key.equals("text")) {
                        text = value;
                    } else if(key.equals("response_url")) {
                        responseUrl = value;
                    }
                } else {
                    logger.log(Level.SEVERE, "KeyValue length is more than 2 " + line);
                }
            }
        } else {
            logger.log(Level.SEVERE, "Body is null - cannot parse");
        }
        
        SlackData slackData = null;
        
        if(token != null && 
                teamId != null &&
                teamDomain != null &&
                channelId != null &&
                channelName != null && 
                userId != null && 
                userName != null &&
                command != null && 
                responseUrl != null) {
            slackData = new SlackData(token, 
                    teamId, 
                    teamDomain, 
                    channelId, 
                    channelName, 
                    userId, 
                    userName, 
                    command, 
                    text, 
                    responseUrl);
            slackData.logger = logger;
        } else {
            logger.log(Level.SEVERE, "Unable to parse slack data - one or more null fields"
                    + "token=" + token + ","
                     + "teamId=" + teamId + ","
                     + "teamDomain=" + teamDomain + ","
                     + "channelId=" + channelId + ","
                     + "channelName=" + channelName + ","
                     + "userId=" + userId + ","
                     + "userName=" + userName + ","
                     + "command=" + command + ","
                     + "text=" + text + ","
                     + "responseUrl=" + responseUrl);
        }
        
        return slackData;
    }
    
    private SlackData(String token, 
            String teamId, 
            String teamDomain, 
            String channelId,  
            String channelName,
            String userId,
            String userName,
            String command, 
            String text,
            String responseUrl) {
        this.token = token;
        this.teamId = teamId;
        this.teamDomain = teamDomain;
        this.channelId = channelId;
        this.channelName = channelName;
        this.userId = userId;
        this.userName = userName;
        this.command = command;
        this.text = text;
        this.responseUrl = responseUrl;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public String getTeamId() {
        return this.teamId;
    }
    
    public String getTeamDomain() {
        return this.teamDomain;
    }
        
    public String getChannelId() {
        return this.getChannelId();
    }
      
    public String getChannelName() {
        return this.channelName;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getResponseUrl() {
        return this.responseUrl;
    }
    
    public String slackUrl() {
        String url = "";
        if(this.getResponseUrl() != null) {
            try {
               url = URLDecoder.decode(this.getResponseUrl(), "UTF-8");
            } catch(Exception ex) {
                logger.log(Level.INFO, "Failed to get url from slack data", ex);
            }
        }
        return url;
    }
}
