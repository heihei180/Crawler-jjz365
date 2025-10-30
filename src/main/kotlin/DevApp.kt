package org.example


import java.io.*
import java.util.zip.GZIPInputStream

fun readGzFile(filePath: String): String {
    File(filePath).inputStream().use { inputStream ->
        GZIPInputStream(inputStream).use { gzIs ->
            return BufferedReader(InputStreamReader(gzIs)).use { reader ->
                reader.readText()
            }
        }
    }
}

// 使用示例
fun main() {
    try {
        val content = readGzFile("datas/camera_data_2025_10_29_10_14_07.json.gz")
        println(content) // 输出解压后的内容（比如 JSON）
    } catch (e: Exception) {
        println("读取 .gz 文件失败: ${e.message}")
    }
}
