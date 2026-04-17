package raymond;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public static String get(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 使用 java 變成http 一定要這樣寫
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        //定義連線參數 避免一直沒有回應
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        //重點重點重點重點 避免網站伺服器拒絕程式讀取資料 (偽裝成瀏覽器)重點重點重點重點
        int status = conn.getResponseCode();
        if (status != 200 && status !=202) {
            throw new RuntimeException("HTTP 錯誤: " + status);
        }
        //判定網頁是否連線正常
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        //網路獲取二進位資料用UTF-8編碼整行讀取
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        //重點重點重點重點 存儲資料 重點重點重點重點 
        in.close();
        conn.disconnect();
        return content.toString();
        //關閉資源並將資料tostring 
    }
}
