import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.get;

public class Main {

  public static void main(String[] args) {

    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/public");

    get("/hello", (req, res) -> {
      return "hello world";
    });

    get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");

            return new ModelAndView(attributes, "index.ftl");
        }, new FreeMarkerEngine());

    post("/", new Route() { 
        @Override
        public Object handle(Request request, Response response) throws Exception {
            CatRequest rq = new CatRequest(request, response);
            rq.processCatRequest();
            return "";
        }
    });
    
  }
}
