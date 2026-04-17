package raymond;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class IndicatorService {

    /**
     * 評分邏輯
     */
    public BigDecimal calculateRankingScore(MarketData data) {
        double total = 0;

        // --- A組：趨勢與動能 (佔 40%) ---
        // 1. 成交量動能 (20%): 今日成交量 / 7日平均成交量
        double volRatio = (data.avg7dVolume == 0) ? 0 : data.todayVolume / data.avg7dVolume;
        if (volRatio >= 1.0) {
            total += 20; // 動能增加給滿分
        } else {
            total += (20 * volRatio); // 動能不足則遞減
        }

        // 2. 均線乖離與趨勢 (20%): MA7 > MA14 且價格在上方
        if (data.ma7 > data.ma14 && data.currentPrice.doubleValue() > data.ma7) {
            double bias = (data.currentPrice.doubleValue() - data.ma7) / data.ma7;
            if (bias < 0.05) {
                total += 20; // 靠近均線(安全區域)給滿分
            } else {
                total += Math.max(0, 20 - (bias * 100)); // 乖離過大扣分，防範網格回撤
            }
        }

        // --- B組：波動與盤整度 (佔 60%) ---
        // 3. ADX 盤整度 (30%): 15 ~ 25 之間為黃金盤整區
        double adxVal = data.adx.doubleValue();
        if (adxVal >= 15 && adxVal <= 25) {
            total += 30; // 盤整最適合網格
        } else if (adxVal > 25) {
            total += Math.max(0, 30 - (adxVal - 25) * 2); // 趨勢太強(單邊)扣分
        } else {
            total += (adxVal / 15.0 * 15.0); // 波動沒有給低分
        }

        // 4. ATR% 波動利潤 (30%): 數值越高利潤越大
        double atrP = data.atrPercent.doubleValue();
        // 基準目標設為 6%，高於 6% 的波動給予高分
        total += Math.min(30, (atrP / 6.0) * 20);

        return new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 計算簡單移動平均線 (SMA)
     */
    public double calculateMA(List<double[]> data, int period) {
        if (data.size() < period) return 0;
        double sum = 0;
        for (int i = data.size() - period; i < data.size(); i++) {
            sum += data.get(i)[3]; // index 3 為收盤價 (Close)
        }
        return sum / period;
    }

    /**
     * 計算 14 日 ATR% (ATR / 當前價格)
     */
    public double calculateATRPercent(List<double[]> k) {
        if (k.size() < 15) return 0;
        double trSum = 0;
        int period = 14;
        for (int i = k.size() - period; i < k.size(); i++) {
            double h = k.get(i)[1];
            double l = k.get(i)[2];
            double pc = k.get(i - 1)[3];
            double tr = Math.max(h - l, Math.max(Math.abs(h - pc), Math.abs(l - pc)));
            trSum += tr;
        }
        double atr = trSum / period;
        double currentPrice = k.get(k.size() - 1)[3];
        return (atr / currentPrice) * 100.0;
    }

    /**
     * 計算 14 日 ADX (趨勢強度指標)
     */
    public double calculateActualADX(List<double[]> k) {
        if (k.size() < 15) return 0;
        double trS = 0, dpS = 0, dmS = 0;
        int p = 14;
        for (int i = k.size() - p; i < k.size(); i++) {
            double h = k.get(i)[1], l = k.get(i)[2], ph = k.get(i - 1)[1], pl = k.get(i - 1)[2], pc = k.get(i - 1)[3];
            trS += Math.max(h - l, Math.max(Math.abs(h - pc), Math.abs(l - pc)));
            double up = h - ph, dn = pl - l;
            dpS += (up > dn && up > 0) ? up : 0;
            dmS += (dn > up && dn > 0) ? dn : 0;
        }
        if (trS == 0) return 0;
        double dip = (dpS / trS) * 100, dim = (dmS / trS) * 100;
        return (dip + dim == 0) ? 0 : Math.abs(dip - dim) / (dip + dim) * 100;
    }
}
