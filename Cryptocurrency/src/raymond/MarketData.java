package raymond;

import java.math.BigDecimal;

public class MarketData {
    public String symbol;
    public BigDecimal currentPrice; // 當前價格
    public BigDecimal score;        // 最終權重總得分
    public BigDecimal adx;          // ADX 趨勢強度
    public BigDecimal atrPercent;   // ATR% 波動率百分比
    
    // 計算所需的中間數據  先double 但處理完要變回BigDecimal
    public double todayVolume;      // 今日成交額
    public double avg7dVolume;      // 7日平均成交額
    public double ma7;              // 7日均線
    public double ma14;             // 14日均線

    public MarketData(String symbol)  //建立建構子
    {
        this.symbol = symbol;
        this.score = BigDecimal.ZERO;
    }
}
