
# Cryptocurrency 資訊

> **風險聲明**  
> 本內容僅為個人投資心得分享，相關排名仍具投資風險，使用者應自負風險控管責任。
  
---

## 🛠️ 環境配置
執行本程式前，**必須**下載並配置以下 Library：

*   **所需檔案**：`json-20240303.jar`
*   **下載連結**：[Maven Central Repository](https://repo1.maven.org/maven2/org/json/json/20240303/)
*   **使用說明**：請確保將此 `.jar` 檔匯入至專案的 Build Path 中。

---

### 1. 類別圖 (Class Diagram)
![類別圖](https://files.catbox.moe/7wfirk.png)

### 2. 時序圖 (Sequence Diagram)
![時序圖](https://files.catbox.moe/mv14gg.png)

---

### ⚠️ 分數排名邏輯 ⚠️

#### **趨勢與動能 (佔 40%)**
主要用來確認人氣以及進場時機點。
* **成交量 (Volume)**：計算「今日成交量 / 過去 7 日平均成交量」。若 數值 > 1 ，給高分數。
* **7日/14日均線 (MA)**：而是看是否有「黃金交叉」。
  * **得分邏輯**：如果 MA7 > MA14 且價格在均線上方，給予基礎分。 (若距離均線太遠會扣分)

#### **波動與盤整度 (佔 60%)**
主要用來確認是否有波動與趨勢
* ADX ：
  * **得分邏輯**：ADX 介於 15 到 25 之間 給高分數。 (根據 自身經驗 ADX > 30 容易出現反轉)
* ATR ：
  * **得分邏輯**：數值越高代表波動劇烈，網格可以在區間多次操作，給高分數。

---

### ⚠️ 使用不同 JSON 格式 ⚠️


| 處理對象 | 來源平台 | JSON 格式類型 | 處理技術 |
| :--- | :--- | :--- | :--- |
| K線數據 | Binance | 二維陣列 (Nested Array) | org.json.JSONArray |
| 即時匯率 | BitoPro / Bitget | 標準物件 (Object) | indexOf + substring |

#### **1. Binance (K線數據) - GridService 使用**
![API json格式說明](https://files.catbox.moe/56ck7o.png)

#### **2. BitoPro (USDT/TWD 匯率) - RateService 使用**
![API json格式說明](https://files.catbox.moe/mgmwzn.png)

#### **3. Bitget (USDT/目標幣) - RateService 使用**
![API json格式說明](https://files.catbox.moe/graef4.png)
