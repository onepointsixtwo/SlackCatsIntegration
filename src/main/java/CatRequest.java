
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author johnkartupelis
 */
public class CatRequest {
    
    private Request request;
    private Response response;
    private SlackData slackData;
    private Logger logger;
    
    public CatRequest(Request request, Response response) {
        logger = Logger.getLogger("CatRequest");
        this.request = request;
        this.response = response;
        slackData = SlackData.slackDataFromPostBody(this.request.body(), logger);
    }
    
    public void processCatRequest() {
        if(slackData != null) {
            response.status(200);
            this.makeGiphyWebRequestAsync();
        } else {
            logger.log(Level.INFO, "Slack data unparseable:\n" + this.request.body());
            response.status(404);
        }
    }
    
    public Response getResponse() {
        return response;
    }
    
    private void makeGiphyWebRequestAsync() {        
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String giphyUrl = getGiphyUrl();
        logger.log(Level.INFO, "Requesting data from giphy with URL " + giphyUrl);
        final Logger lgr = logger;
        final SlackData slkData = slackData;
        asyncHttpClient.prepareGet(giphyUrl).execute(new AsyncCompletionHandler<com.ning.http.client.Response>(){
            @Override
            public com.ning.http.client.Response onCompleted(com.ning.http.client.Response rspns) throws Exception {
                String giphyUrl = urlFromGiphyResponse(rspns, lgr);
                if(giphyUrl != null) {
                    sendSuccessToSlack(giphyUrl, lgr, slkData);
                } else {
                    sendErrorToSlack(lgr, slkData);
                }
                return rspns;
            }    
        });
    }
    
    private static String urlFromGiphyResponse(com.ning.http.client.Response response, Logger logger) {
        if(response != null) {
            String body = null;
            try {
                body = response.getResponseBody();
            } catch(Exception ex) {
                logger.log(Level.SEVERE, "Exception thrown reading giphy response body ", ex);
            }
            if(body != null) {
                JSONObject jsonBody = null;
                try {
                    jsonBody = new JSONObject(body);
                } catch(Exception ex) {
                    logger.log(Level.SEVERE, "Exception thrown getting json from giphy response body (body:"+body+")", ex);
                }
                
                if(jsonBody != null) {
                    return urlFromGiphyResponseBody(jsonBody, body, logger);
                }
            }
        }
        return null;
    }
    
    private static String urlFromGiphyResponseBody(JSONObject body, String bodyStr, Logger logger) {
        String url = null;
        try {
            JSONObject data = body.getJSONObject("data");
            url = data.getString("fixed_height_downsampled_url");
        } catch(Exception ex) {
            logger.log(Level.SEVERE, "Exception thrown getting image_url from giphy response body: " + bodyStr);
        }
        
        return url;
    }
    
    private static void sendErrorToSlack(Logger logger, SlackData slackData) {
        JSONObject object = new JSONObject();
        try {
            object.put("text", "Sorry, couldn't find a gif for what you wanted :(");
        } catch(Exception ex) {
            logger.log(Level.SEVERE, "Exception thrown trying to create JSONOBject error to send to slack", ex);
        }
        sendJsonObjectToSlack(object, logger, slackData);
    }
    
    private static void sendSuccessToSlack(String gifUrl, Logger logger, SlackData slackData) {
        JSONObject object = new JSONObject();
        try {
            object.put("text", gifUrl + "\nPowered by Giphy");
            object.put("response_type", "in_channel");
        } catch(Exception ex) {
            logger.log(Level.SEVERE, "Exception thrown trying to create JSONOBject success to send to slack", ex);
        }
        sendJsonObjectToSlack(object, logger, slackData);
    }
    
    private static void sendJsonObjectToSlack(JSONObject object, Logger logger, SlackData slackData) {
        String str = null;
        try {
            str = object.toString();
        } catch(Exception ex) {
            logger.log(Level.SEVERE, "Exception thrown getting text to send to slack from JSON object");
        }
        sendStringToSlack(str, logger, slackData);
    }
    
    private static void sendStringToSlack(String text, Logger logger, SlackData slackData) {
        logger.log(Level.INFO, "Posting text back to slack: " + text);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String slackUrl = slackData.slackUrl();
        logger.log(Level.INFO, "Posting to slack URL " + slackUrl);
        asyncHttpClient.preparePost(slackUrl).setBody(text).execute(new AsyncCompletionHandler<com.ning.http.client.Response>(){
            @Override
            public com.ning.http.client.Response onCompleted(com.ning.http.client.Response rspns) throws Exception {
                if(rspns != null) {
                    logger.log(Level.INFO, "Received response from Slack");
                } else {
                    logger.log(Level.INFO, "Failed to receive response from Slack");
                }
                return rspns;
            }    
        });
    }

    
    private String getGiphyUrl() {
        if(slackData.getText() != null) {
          try {
              return "http://api.giphy.com/v1/gifs/random?api_key=dc6zaTOxFJmzC&tag=" + "cat+" + URLEncoder.encode(slackData.getText(), "UTF-8");
          } catch(Exception ex) {
              return "http://api.giphy.com/v1/gifs/random?api_key=dc6zaTOxFJmzC&tag=" + "cat";
          }
        } else {
            return "http://api.giphy.com/v1/gifs/random?api_key=dc6zaTOxFJmzC&tag=" + "cat";
        }
    }
}
