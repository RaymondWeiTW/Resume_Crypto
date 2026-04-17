package raymond;

import org.json.JSONArray;// 導入外部json格式 eclipse 要設定
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class GridService {
    private IndicatorService indicatorService = new IndicatorService();

    public MarketData analyze(String symbol) throws Exception {
        String cleanSymbol = symbol.trim().toUpperCase();
        
        String url = "https://api.binance.com/api/v3/klines?symbol=" + cleanSymbol + "USDT&interval=1d&limit=50";
        // 使用幣安 開放API  取得 為何不用bitget 原因不需要私鑰權限
        // 參數：interval=1d (日線), limit=50 (抓取最近 50 筆資料)
        // 相關內容在https://developers.binance.com/docs/binance-spot-api-docs/rest-api/market-data-endpoints#:~:text=%220%22%20//%20Unused%20field%2C,Ignore.
        String resp = HttpUtil.get(url);
        
        if (resp == null || !resp.trim().startsWith("[")) {
            throw new Exception("API 回傳異常，可能是幣種代號不存在。內容: " + (resp.length() > 20 ? resp.substring(0, 20) : resp));
        }
        //重點重點重點 startsWith是 Java String 類別的內建方法
        //用來檢查字串的開頭第一個字元（或一段字串）是否符合括號內的內容
        //避免解析失敗
        
        JSONArray arr = new JSONArray(resp);
        //使用外部的json格式
        List<double[]> klines = new ArrayList<>();
    
        for (int i = 0; i < arr.length(); i++) {
            JSONArray k = arr.getJSONArray(i);
            klines.add(new double[]{ 
                k.getDouble(1), k.getDouble(2), k.getDouble(3), k.getDouble(4), k.getDouble(7) 
            });
            //每筆 K 線存 開盤價 最高價, 最低價, 收盤價, 成交量
        }

        MarketData md = new MarketData(cleanSymbol);
        int last = klines.size() - 1;
        //最新一根 K 線的索引位置
        md.currentPrice = new BigDecimal(klines.get(last)[3]); 
        md.todayVolume = klines.get(last)[4]; 
        //設定當前價格
        double volSum = 0;
        int count = Math.min(7, klines.size());
        for (int i = last; i > last - count; i--) 
        {
            volSum += klines.get(i)[4];
        }
        md.avg7dVolume = volSum / count;
       //計算 7 日平均成交額
        
        md.ma7 = indicatorService.calculateMA(klines, 7);
        md.ma14 = indicatorService.calculateMA(klines, 14);
        //計算 7 日與 14 日 MA
        md.adx = new BigDecimal(indicatorService.calculateActualADX(klines)).setScale(2, RoundingMode.HALF_UP);
        // 計算 ADX 
        md.atrPercent = new BigDecimal(indicatorService.calculateATRPercent(klines)).setScale(2, RoundingMode.HALF_UP);
        // 計算 ATR
        md.score = indicatorService.calculateRankingScore(md);
        // 計算總分
        return md;
    }
}