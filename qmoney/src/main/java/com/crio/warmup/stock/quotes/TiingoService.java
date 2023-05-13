
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  public TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    // TODO Auto-generated method stub
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModules(new JavaTimeModule());

    String url = buildUri(symbol, from, to);
    String data = restTemplate.getForObject(url, String.class);
    TiingoCandle[] candles = mapper.readValue(data, TiingoCandle[].class);
    return Arrays.asList(candles); 
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    final String TOKEN = "8bb389d0890c9bf97da4eaebd6548f39ae98050d";//"8bb389d0890c9bf97da4eaebd6548f39ae98050d";
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol
    +"/prices?startDate="+startDate+"&endDate="+endDate+"&token="+TOKEN;
   return uriTemplate;
}


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

}
