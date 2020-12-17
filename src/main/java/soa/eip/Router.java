package soa.eip;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Router extends RouteBuilder {

  public static final String DIRECT_URI = "direct:twitter";

  @Override
  public void configure() {
    from(DIRECT_URI)
      .log("Body contains \"${body}\"")
      .process(exchange -> {
        String regex = "max:[0-9]+";
        String[] query = (exchange.getIn().getBody(String.class)).split(" ");
        StringBuilder queryBody = new StringBuilder();
        String countHeader = "5";
        // Build new body and set count header
        for (String keyword : query) {
          if (keyword.matches(regex)) { countHeader = keyword.substring(4); }
          else { queryBody.append(keyword).append(" "); }
        }
        exchange.getIn().setBody(queryBody.toString());
        exchange.getIn().setHeader("count", countHeader);
      })
      .log("Searching twitter for \"${body}\"!")
      .toD("twitter-search:${body}?count=${header.count}")
      .log("Body now contains the response from twitter:\n${body}");
  }
}
