package raymond;

import java.util.*;

public class CryptocurrencyApp {
    private static Scanner scanner = new Scanner(System.in);
    private static RateService rateService = new RateService();
    private static GridService gridService = new GridService(); // 網格獨立服務

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=======================================");
            System.out.println("         Cryptocurrency 主選單					");
            System.out.println("=========================================");
            System.out.println("  1. 匯率計算 (TWD -> 加密貨幣)			");
            System.out.println("  2. 網格成績排行										");
            System.out.println("  3. 離開														");
            System.out.println("=========================================");
            System.out.print("請輸入選項 (1-3): ");

            String choice = scanner.next().trim();

            switch (choice) {
                case "1":
                    runExchangeCalculator();
                    break;
                case "2":
                    runGridRanker(); // 執行網格排行
                    break;
                case "3":
                    System.out.println("程式已退出。");
                    scanner.close();
                    return;
                default:
                    System.out.println(">> 無效選項，請重新輸入！");
                    break;
            }
        }
    }

    private static void runExchangeCalculator() {
        while (true) {
            try {
                System.out.print("\n請輸入轉換幣別 (如 POL, BTC) 或輸入 exit 回選單: ");
                String input = scanner.next().trim();
                if (input.equalsIgnoreCase("exit")) break;
                String targetCrypto = input.toUpperCase();

                System.out.print("請輸入台幣金額 (TWD): ");
                if (!scanner.hasNextDouble()) {
                    String check = scanner.next();
                    if (check.equalsIgnoreCase("exit")) break;
                    System.out.println(">> 請輸入有效數字！");
                    continue;
                }
                double twdAmount = scanner.nextDouble();
                scanner.nextLine(); // 消耗換行符

                System.out.println(">> 正在取得即時匯率...");
                double twdToUsdtRate = rateService.getTwdToUsdtFromBitoPro();
                double usdtToTargetRate = rateService.getUsdtToCryptoFromBitget(targetCrypto);

                // --- 手續費輸入邏輯 ---
                double defaultBitoFee = 0.001; // 預設 0.1%
                System.out.printf("BitoPro 預設手續費: %.3f%%，按 Enter 使用或輸入新值 (如 0.1): ", defaultBitoFee * 100);
                double bitoFee = getFeeInput(defaultBitoFee);

                double defaultBitgetFee = 0.001; // 預設 0.1%
                System.out.printf("Bitget 預設手續費: %.3f%%，按 Enter 使用或輸入新值 (如 0.05): ", defaultBitgetFee * 100);
                double bitgetFee = getFeeInput(defaultBitgetFee);

                // 計算流程
                double usdtBeforeFee = twdAmount * twdToUsdtRate;
                double usdtAfterFee = usdtBeforeFee * (1.0 - bitoFee);
                double targetAfterFee = (usdtAfterFee * usdtToTargetRate) * (1.0 - bitgetFee);

                // 輸出結果
                System.out.println("\n-------------------------------------------");
                System.out.printf("【即時報價】%n");
                System.out.printf("  USDT/TWD: %.2f (BitoPro)%n", 1.0 / twdToUsdtRate);
                System.out.printf("  %s/USDT : %.4f (Bitget)%n", targetCrypto, 1.0 / usdtToTargetRate);
                
                System.out.printf("%n【手續費】%n");
                System.out.printf("  BitoPro: %.3f%%%n", bitoFee * 100);
                System.out.printf("  Bitget : %.3f%%%n", bitgetFee * 100);

                System.out.printf("%n【計算結果】%n");
                System.out.printf("  TWD %,.0f -> %.4f USDT (扣費後)%n", twdAmount, usdtAfterFee);
                System.out.printf("  預估獲得: **%.6f %s**%n", targetAfterFee, targetCrypto);
                System.out.println("-------------------------------------------");

            } catch (Exception e) {
                System.out.println(e.getMessage() + " 請確認幣別代號是否正確");
            }
        }
    }

    private static void runGridRanker() {
        try {
            scanner.nextLine(); // 消耗緩衝區
            List<String> coins = new ArrayList<>(Arrays.asList("BTC"));
            while (true) {
                System.out.println("\n>> 當前監控: " + coins);
                System.out.print(">> [+幣種]增加, [-幣種]刪除, [Enter]開始分析: ");
                String input = scanner.nextLine().trim().toUpperCase();
                if (input.isEmpty()) break;
                if (input.startsWith("-")) {
                    coins.remove(input.substring(1));
                } else {
                    String c = input.replace("+", "");
                    if (!coins.contains(c)) coins.add(c);
                }
            }

            List<MarketData> results = new ArrayList<>();
            System.out.println("正在分析數據，請稍候...");
            for (String s : coins) {
                try {
                    results.add(gridService.analyze(s));
                    System.out.println(">> " + s + " [完成]");
                } catch (Exception e) {
                    System.out.println(">> " + s + " [失敗: " + e.getMessage() + "]");
                }
            }
            results.sort((a, b) -> b.score.compareTo(a.score));
            ReportService.printConsoleTable(results);
            ReportService.saveToTxt(results);
        } catch (Exception e) {
            System.out.println(">> 網格排行執行出錯: " + e.getMessage());
        }
    }

    private static double getFeeInput(double defaultValue) {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(input) / 100.0;
        } catch (NumberFormatException e) {
            System.out.println(">> 格式錯誤，採用預設值。");
            return defaultValue;
        }
    }
}
