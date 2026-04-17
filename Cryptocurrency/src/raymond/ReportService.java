package raymond;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportService {
    public static void printConsoleTable(List<MarketData> list) {
        System.out.println("\n 網格2排行分析結果");
        System.out.println("------------------------------------------------------------------");
        System.out.println("| 排名 | 幣種 | 得分 | ADX | ATR | 趨勢|");
        System.out.println("------------------------------------------------------------------");
     
        for (int i = 0; i < list.size(); i++) {
            MarketData d = list.get(i);
            String trend = (d.ma7 > d.ma14) ? "多頭" : "空頭";
            System.out.printf("| %-4d | %-8s | %-6s | %-8.2f | %-5.2f%% | %-6s |%n", 
                i + 1, d.symbol, d.score, d.adx, d.atrPercent, trend);
        }
    }

    public static void saveToTxt(List<MarketData> list) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String fileName = "Grid_Rank_" + timestamp + ".txt";
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("===== 網格篩選報告 =====");
            pw.println("時間: " + LocalDateTime.now());
            pw.println("------------------------------------------------------------------");
            pw.println("| 排名 | 幣種 | 得分 | ADX | ATR |");
            pw.println("------------------------------------------------------------------");
            
            for (int i = 0; i < list.size(); i++) {
                MarketData d = list.get(i);
                pw.printf("%-4d %-10s %-12.4f %-8.2f %-8.2f%n", 
                    i + 1, d.symbol, d.score, d.adx, d.atrPercent);
            }
        }
        System.out.println();
        System.out.println(">>  報告已儲存 名稱為: " + fileName);
    }

}
