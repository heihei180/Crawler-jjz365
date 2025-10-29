package org.example

import java.io.*
import java.util.zip.GZIPOutputStream
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

fun writeCompressedJson(data: Any, filePath: String) {
    val mapper = ObjectMapper().registerKotlinModule()
    FileOutputStream(filePath).use { fos ->
        GZIPOutputStream(fos).use { gz ->
            mapper.writeValue(gz, data)
        }
    }
    println("✅ Compressed JSON saved to: $filePath")
}


fun main() {
    val changping = requestData("昌平区")
    println("昌平区摄像头数量：${changping.size}")
    val haidian = requestData("海淀区")
    println("海淀区摄像头数量：${haidian.size}")
    val chaoyang = requestData("朝阳区")
    println("朝阳区摄像头数量：${chaoyang.size}")
    val fengtai = requestData("丰台区")
    println("丰台区摄像头数量：${fengtai.size}")
    val tongzhou = requestData("通州区")
    println("通州区摄像头数量：${tongzhou.size}")
    val daxing = requestData("大兴区")
    println("大兴区摄像头数量：${daxing.size}")

    val shijingshan = requestData("石景山区")
    println("石景山区摄像头数量：${shijingshan.size}")

    val mentougou = requestData("门头沟区")
    println("门头沟区摄像头数量：${mentougou.size}")

    val huairou = requestData("怀柔区")
    println("怀柔区摄像头数量：${huairou.size}")

    val pinggu = requestData("平谷区")
    println("平谷区摄像头数量：${pinggu.size}")

    val yanqing = requestData("延庆区")
    println("延庆区摄像头数量：${yanqing.size}")

    val miyun = requestData("密云区")
    println("密云区摄像头数量：${miyun.size}")

    val dongcheng = requestData("东城区")
    println("东城区摄像头数量：${dongcheng.size}")

    val xicheng = requestData("西城区")
    println("西城区摄像头数量：${xicheng.size}")

    val fangshan = requestData("房山区")
    println("房山区摄像头数量：${fangshan.size}")

    val shunyi = requestData("顺义区")
    println("顺义区摄像头数量：${shunyi.size}")

    val total = changping.size + haidian.size + chaoyang.size + fengtai.size + tongzhou.size + daxing.size +
            shijingshan.size + mentougou.size + huairou.size + pinggu.size + yanqing.size +
            miyun.size + dongcheng.size + xicheng.size + fangshan.size + shunyi.size

    println("北京市总摄像头数量：$total")

    val data = mapOf(
        "昌平区" to changping,
        "海淀区" to haidian,
        "朝阳区" to chaoyang,
        "丰台区" to fengtai,
        "通州区" to tongzhou,
        "大兴区" to daxing,
        "石景山区" to shijingshan,
        "门头沟区" to mentougou,
        "怀柔区" to huairou,
        "平谷区" to pinggu,
        "延庆区" to yanqing,
        "密云区" to miyun,
        "东城区" to dongcheng,
        "西城区" to xicheng,
        "房山区" to fangshan,
        "顺义区" to shunyi
    )

    writeCompressedJson(data, "datas/camera_data.json.gz")
}