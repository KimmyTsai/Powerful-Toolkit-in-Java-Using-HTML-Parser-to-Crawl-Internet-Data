# Powerful-Toolkit-in-Java-Using-HTML-Parser-to-Crawl-Internet-Data

## 簡介

`Powerful-Toolkit-in-Java-Using-HTML-Parser-to-Crawl-Internet-Data` 是一個使用 `jsoup` 庫來從指定的網站上爬取股價資料的Java工具，並將這些資料保存到CSV檔案中。程式可以分析已蒐集的資料，並提供多種股價數據的分析功能，如5日移動平均、標準差和回歸分析。

## 功能

- **股價爬取**：從指定網站抓取當前時間的股票名稱與股價，並追加到`data.csv`檔案中。
- **股價分析**：分析已蒐集的股價數據，並根據輸入的命令輸出至`output.csv`。

## 爬取的資料格式

爬取的HTML檔案包含兩個重要資訊：

- **股票名稱**：例如：`AAPL`, `AMZN`, `GOOGL`等。
- **當前股價**：例如：`1.72`, `45.95`, `170.12`等。

這些數據將自動追加到`data.csv`檔案，供後續的數據分析使用。

## 使用方式

執行程式時，必須指定模式(`mode`)、任務(`task`)，以及相關參數。以下是程式的執行格式：

```bash
java HtmlParser {mode} {task} {stock} {start} {end}
```

### Input Arguments

- **mode**：選擇程式運行的模式。
  - `0`：爬蟲模式，爬取當前時間點的股價資料並追加至`data.csv`。
  - `1`：分析模式，根據`task`參數分析股價資料並輸出結果至`output.csv`。
  
- **task**：在分析模式下使用，指定分析任務。
  - `0`：輸出所有蒐集的股價資料。
  - `1`：輸出指定股票在指定時間段內的5日移動平均。
  - `2`：輸出指定股票在指定時間段內的股價標準差。
  - `3`：輸出指定時間段內標準差最大的前3名股票。
  - `4`：輸出指定股票在指定時間段內的股價回歸直線。
  
- **stock**：分析的股票代號（當`task == 3`時無需此參數）。
- **start**：分析的開始天數。
- **end**：分析的結束天數（含結束天）。

## 範例

### 1. 呼叫程式爬取當前股價資料

```bash
java HtmlParser 0
```

### 2. 輸出蒐集至今的所有股價資料至`output.csv`

```bash
java HtmlParser 1 0
```

### 3. 輸出蒐集至今的所有股價資料至`output.csv`

```bash
java HtmlParser 1 1 AAL 2 6
```

### 4. 輸出AAL第1天到第3天(含)的股價標準差至`output.csv`

```bash
java HtmlParser 1 2 AAL 1 3
```

### 5. 輸出第1天到第15天(含)的股價標準差Top-3至`output.csv`

```bash
java HtmlParser 1 3 AAL 1 15
```

### 6. 輸出AAL第1天到第30天(含)的股價回歸直線至`output.csv`

```bash
java HtmlParser 1 4 AAL 1 30
```

### 輸出文件格式

- `data.csv`：以爬蟲模式 (`mode=0`) 執行時，將爬取的股價資料追加至該文件。
  - 每次追加一行，格式為股票代號及其對應的股價。
  
- `output.csv`：以分析模式 (`mode=1`) 執行時，根據不同的 `task` 輸出對應結果。
  - **task=0**：輸出所有蒐集至今的股價資料。
  - **task=1**：輸出指定股票在指定時間段的5日移動平均。
  - **task=2**：輸出指定股票在指定時間段的股價標準差。
  - **task=3**：輸出指定時間段內股價標準差最大的Top-3股票。
  - **task=4**：輸出指定股票在指定時間段的股價回歸直線。

### 依賴

- `jsoup`：用於解析HTML和爬取網站數據的Java庫。
