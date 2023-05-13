
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  RestTemplate restTemplate;

  StockQuotesService stockQuotesService;

  public PortfolioManagerImpl(){
    
  }

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }



  protected PortfolioManagerImpl(StockQuotesService stockQuotesService, RestTemplate restTemplate){
    this.stockQuotesService = stockQuotesService;
    this.restTemplate = restTemplate;
  }

  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF






  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {

       return stockQuotesService.getStockQuote(symbol, from, to);

  }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) {
        
    // TODO Auto-generated method stub
    return portfolioTrades.stream().map(trade -> {
      List<Candle> candles = null;
      try {
        candles = getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
      } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      Double startPrice = getOpeningPriceOnStartDate(candles);
      Double endPrice = getClosingPriceOnEndDate(candles);

     return calculateParticularAnnualizedReturns(endDate, trade, startPrice, endPrice);
    }).sorted(getComparator()).collect(Collectors.toList());
  }

  private AnnualizedReturn calculateParticularAnnualizedReturns(LocalDate endDate,
  PortfolioTrade trade, Double buyPrice, Double sellPrice){
     //calculate totalReturns'
     Double returns = (sellPrice - buyPrice) / buyPrice; //(sellPrice - buyPrice) / buyPrice; 
     Double totalReturns = 1.0 + returns;
    
     long days = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
     Double totalDays = days * 0.002738;
     double totalYears = (1.0 / totalDays);
     
   Double annualReturns = (Math.pow(totalReturns, totalYears) - 1);
   return new AnnualizedReturn(trade.getSymbol(), annualReturns, totalReturns);
  }

  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
 }


 public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size()-1).getClose();
 }


}
