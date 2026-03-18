import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.FileWriter
import java.net.URL
import javax.script.ScriptEngineManager
import javax.script.ScriptException

/**
 * 辽宁省纪委监委审查调查数据爬虫（Kotlin 版本）
 *
 * 功能：
 * - 爬取 https://www.lnsjjjc.gov.cn/network/ejsctc/index.shtml 页面数据
 * - 三个大类：省管干部、市管干部、其他公职人员
 * - 每个大类两个小类：执纪审查、党纪政务处分
 * - 爬取所有分页数据
 * - 保存标题和内容页超链接到 CSV
 *
 * 运行前确保已安装 Jsoup:
 * 下载 jsoup jar 包并添加到 classpath，或使用 Maven/Gradle 管理依赖
 *
 * 使用方法：
 * kotlin LiaoningScraper.kt
 */

data class ScrapedData(
    val category: String,
    val subCategory: String,
    val title: String,
    val url: String
)

data class SubCategory(
    val category: String,
    val subCategory: String,
    val url: String
)

class LiaoningScraper {
    private val baseUrl = "https://www.lnsjjjc.gov.cn"
    private val indexUrl = "$baseUrl/network/ejsctc/index.shtml"

    fun fetchPage(url: String): String? {
        return try {
            val response = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .timeout(10000)
                .execute()
            response.body()
        } catch (e: Exception) {
            println("请求失败：$url, 错误：${e.message}")
            null
        }
    }

    fun getSubCategories(): List<SubCategory> {
        println("正在获取首页分类...")
        val html = fetchPage(indexUrl) ?: return emptyList()

        val doc = Jsoup.parse(html)
        val rightDiv = doc.selectFirst("div.l-ny-right.right") ?: return emptyList()

        val boxes = rightDiv.select("div.l-ny-box")
        val subCategories = mutableListOf<SubCategory>()

        for (box in boxes) {
            val title1 = box.selectFirst("div.title_1 div.logo.wr") ?: continue
            val categoryName = title1.text().trim()

            val title3s = box.select("div.title_3")
            for (title3 in title3s) {
                val subNameElem = title3.selectFirst("div.logo.wr") ?: continue
                val subName = subNameElem.text().trim()

                val link = title3.selectFirst("a") ?: continue
                var href = link.attr("href")
                if (!href.startsWith("http")) {
                    href = "$baseUrl$href"
                }

                subCategories.add(SubCategory(categoryName, subName, href))
            }
        }

        return subCategories
    }

    fun parseListPage(html: String, category: String, subCategory: String): List<ScrapedData> {
        val dataList = mutableListOf<ScrapedData>()
        val doc = Jsoup.parse(html)

        // 查找 .sftit 下的所有 li
        val listItems = doc.select(".sftit li")

        for (item in listItems) {
            val link = item.selectFirst("a") ?: continue

            val title = link.text().trim()
            var href = link.attr("href")
            if (!href.startsWith("http")) {
                href = "$baseUrl$href"
            }

            dataList.add(ScrapedData(category, subCategory, title, href))
        }

        return dataList
    }

    fun scrapeList(url: String, category: String, subCategory: String): List<ScrapedData> {
        val allData = mutableListOf<ScrapedData>()

        // 访问第一页
        println("  访问：$url")
        val html = fetchPage(url) ?: return allData

        // 解析第一页数据
        val pageData = parseListPage(html, category, subCategory)
        allData.addAll(pageData)
        println("    第 1 页：${pageData.size}条")

        // 从 HTML 中提取总页数
        val totalPageMatch = Regex("""var pages = '(\d+)""").find(html)
        val totalPages = totalPageMatch?.groupValues?.get(1)?.toIntOrNull() ?: 1

        println("  $category - $subCategory: 共$totalPages 页")

        // 爬取剩余页（根据实际 URL 规律）
        // URL 规律：第 1 页 indexHome.shtml
        // 第 2 页开始：index(总页数 - (当前页码 - 1)).shtml
        // 例如：共 10 页，第 2 页是 index9.shtml (10-(2-1)=9)，第 3 页是 index8.shtml (10-(3-1)=8)
        val baseUrl = url.replace("indexHome.shtml", "index")

        for (pageNum in 2..totalPages) {
            // 构造分页 URL：总页数 - (当前页码 - 1)
            val pageFileNum = totalPages - (pageNum - 1)
            val pageUrl = "${baseUrl}${pageFileNum}.shtml"

            val pageHtml = fetchPage(pageUrl) ?: continue

            val pageData = parseListPage(pageHtml, category, subCategory)
            allData.addAll(pageData)
            println("    第$pageNum 页：${pageData.size}条")

            // 间隔 5 秒
            if (pageNum < totalPages) {
                Thread.sleep(5000)
            }
        }

        return allData
    }

    fun run() {
        println("=" + "=".repeat(59))
        println("开始爬取辽宁省纪委监委审查调查数据")
        println("=" + "=".repeat(59))

        // 获取所有小类
        val subCategories = getSubCategories()

        if (subCategories.isEmpty()) {
            println("未找到任何分类，可能是网站结构变化或网络问题")
            return
        }

        println("\n找到 ${subCategories.size} 个小类:")
        for (sc in subCategories) {
            println("  - ${sc.category} > ${sc.subCategory}")
        }

        val allData = mutableListOf<ScrapedData>()

        // 爬取每个小类
        subCategories.forEachIndexed { index, sc ->
            println("\n[${index + 1}/${subCategories.size}] 正在爬取：${sc.category} - ${sc.subCategory}")
            val data = scrapeList(sc.url, sc.category, sc.subCategory)

            if (data.isNotEmpty()) {
                println("  ✓ 成功爬取 ${data.size} 条记录")
                allData.addAll(data)
            } else {
                println("  ✗ 未找到数据")
            }

            // 间隔 5 秒
            if (index < subCategories.size - 1) {
                println("  等待 5 秒...")
                Thread.sleep(5000)
            }
        }

        // 保存数据
        if (allData.isNotEmpty()) {
            saveData(allData)
        }
    }

    fun saveData(data: List<ScrapedData>) {
        val timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        )
        val csvFile = File("liaoning_scrape_${timestamp}.csv")

        FileWriter(csvFile).use { writer ->
            writer.append("category,sub_category,title,url\n")

            for (item in data) {
                // 转义标题中的逗号和引号
                val escapedTitle = item.title.replace("\"", "\"\"")
                writer.append("${item.category},${item.subCategory},\"$escapedTitle\",${item.url}\n")
            }
        }

        println("\n✓ 数据已保存到 CSV: ${csvFile.name}")

        // 打印统计
        println("\n" + "=" + "=".repeat(59))
        println("爬取完成！总记录数：${data.size}")

        // 分类统计
        val stats = data.groupingBy { "${it.category} - ${it.subCategory}" }
            .eachCount()

        println("\n分类统计:")
        for ((key, value) in stats) {
            println("  - $key: $value 条")
        }
    }
}

fun main() {
    val scraper = LiaoningScraper()
    scraper.run()
}
