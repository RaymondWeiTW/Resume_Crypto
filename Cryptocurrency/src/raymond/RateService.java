package raymond;

public class RateService {

    /**
     * BitoPro：TWD -> USDT 匯率
     */
    public double getTwdToUsdtFromBitoPro() throws Exception {
        String url = "https://api.bitopro.com/v3/tickers/usdt_twd"; 
        //相關內容在https://github.com/bitoex/bitopro-official-api-docs/blob/master/ws/public/ticker_stream.md
        
        String json = HttpUtil.get(url);
        String key = "\"lastPrice\":\"";
        int start = json.indexOf(key) + key.length();
        int end = json.indexOf("\"", start);
        double price = Double.parseDouble(json.substring(start, end));
        // 定義位置標籤
        // 計算起點 找到位置並跳過 key 自身的長度共13(不算\)，因為我只要數值
        // 計算終點 從 start 往後找，在第一個雙引號為數字結束點
        // 擷取中間的數值並轉換為 double
        return 1.0 / price; 
        //拿到的數值是USDT 對NTD 必須取倒數 才對
    }

    /**
     * Bitget V2：USDT -> 目標幣種
     * 目標幣種是使用者自行決定所以用字元相加處理 且有可能出錯 
     */
    public double getUsdtToCryptoFromBitget(String symbol) throws Exception {
        String pair = symbol.toUpperCase() + "USDT";
        String url = "https://api.bitget.com/api/v2/spot/market/tickers?symbol=" + pair;
        //相關內容在https://www.bitget.com/api-doc/spot/market/Get-Tickers
        String json = HttpUtil.get(url);
        String key = "\"lastPr\":\"";
        if (!json.contains(key)) throw new Exception("Bitget 查無此幣種: " + pair);
        
        int start = json.indexOf(key) + key.length();
        int end = json.indexOf("\"", start);
        //計算起點 自身的長度共10(不算\) 因為我只要數值
        double cryptoPriceInUsdt = Double.parseDouble(json.substring(start, end));
        
        return 1.0 / cryptoPriceInUsdt;
    }
}