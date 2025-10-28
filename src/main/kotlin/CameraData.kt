package org.example

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.file.Files
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/*
{
    "Id": "87c230dbd1774e4696e6ae8566989a9d",
    "CameraType": "拍进京证",
    "District": "海淀区",
    "LocationDescript": "会城门桥至莲花桥主出辅信悦华庭对面 [东向西]",
    "RoadName": null,
    "SupportCount": 1,
    "OpposeCount": 0,
    "Longitude": 38.97031299999999,
    "Latitude": 123.08713900000001,
    "CreateDateTime": "2020-12-31 21:39",
    "UpdateTimeFlag": "2021-01-01 00:39",
    "IsPass": "1",
    "Provider": "IN",
    "PictureCount": 0,
    "DiscussCount": 0,
    "IconFlag": "0",
    "Important": null,
    "IsSixRing": "0",
    "ViewCount": 24,
    "Description": null,
    "WXCreateTime": null
  },
 */
data class CameraData(
    @JsonProperty("Id")
    val id: String?,
    @JsonProperty("CameraType")
    val cameraType: String?,
    @JsonProperty("District")
    val district: String?,
    @JsonProperty("LocationDescript")
    val locationDescript: String?,
    @JsonProperty("RoadName")
    val roadName: String?,
    @JsonProperty("SupportCount")
    val supportCount: String?,
    @JsonProperty("OpposeCount")
    val opposeCount: String?,
    @JsonProperty("Longitude")
    val longitude: String?,
    @JsonProperty("Latitude")
    val latitude: String?,
    @JsonProperty("CreateDateTime")
    val createDateTime: String?,
    @JsonProperty("UpdateTimeFlag")
    val updateTimeFlag: String?,
    @JsonProperty("IsPass")
    val isPass: String?,
    @JsonProperty("Provider")
    val provider: String?,
    @JsonProperty("PictureCount")
    val pictureCount: String?,
    @JsonProperty("DiscussCount")
    val discussCount: String?,
    @JsonProperty("IconFlag")
    val iconFlag: String?,
    @JsonProperty("Important")
    val important: String?,
    @JsonProperty("IsSixRing")
    val isSixRing: String?,
    @JsonProperty("ViewCount")
    val viewCount: String?,
    @JsonProperty("Description")
    val description: String?,
    @JsonProperty("WXCreateTime")
    val wxCreateTime: String?
)


//curl --location --request POST 'https://www.jjz365.cn/CameraData/GetDistrictData' \
//--header 'Accept: application/json, text/javascript, */*; q=0.01' \
//--header 'Accept-Language: zh-CN,zh;q=0.9' \
//--header 'Cache-Control: no-cache' \
//--header 'Connection: keep-alive' \
//--header 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' \
//--header 'Origin: https://www.jjz365.cn' \
//--header 'Pragma: no-cache' \
//--header 'Referer: https://www.jjz365.cn/home/Map' \
//--header 'Sec-Fetch-Dest: empty' \
//--header 'Sec-Fetch-Mode: cors' \
//--header 'Sec-Fetch-Site: same-origin' \
//--header 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36' \
//--header 'X-Requested-With: XMLHttpRequest' \
//--header 'sec-ch-ua: "Google Chrome";v="141", "Not?A_Brand";v="8", "Chromium";v="141"' \
//--header 'sec-ch-ua-mobile: ?0' \
//--header 'sec-ch-ua-platform: "Windows"' \
//--header 'Cookie: ASP.NET_SessionId=bg21rry15igl52r5tawhojvq' \
//--data-raw 'district=%E6%B5%B7%E6%B7%80%E5%8C%BA'

val mapper = ObjectMapper()

fun requestData(district: String): List<CameraData> {

    val encodeCode = URLEncoder.encode(district, Charset.defaultCharset())
    val client = OkHttpClient.Builder().build()
    val mediaType: MediaType? = "application/x-www-form-urlencoded; charset=UTF-8".toMediaTypeOrNull()
//    val body: RequestBody = RequestBody.create(mediaType, "district=%E6%B5%B7%E6%B7%80%E5%8C%BA")
    val body: RequestBody = RequestBody.create(mediaType, "district=$encodeCode")

    val request = Request.Builder()
        .url("https://www.jjz365.cn/CameraData/GetDistrictData")
        .post(body)
        .header("Accept", "application/json, text/javascript, */*; q=0.01")
        .header("Accept-Language", "zh-CN,zh;q=0.9")
        .header("Cache-Control", "no-cache")
        .header("Connection", "keep-alive")
        .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        .header("Origin", "https://www.jjz365.cn")
        .header("Pragma", "no-cache")
        .header("Referer", "https://www.jjz365.cn/home/Map")
        .header("Sec-Fetch-Dest", "empty")
        .header("Sec-Fetch-Mode", "cors")
        .header("Sec-Fetch-Site", "same-origin")
        .header(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"
        )
        .header("X-Requested-With", "XMLHttpRequest")
        .header("sec-ch-ua", "\"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"")
        .header("sec-ch-ua-mobile", "?0")
        .header("sec-ch-ua-platform", "\"Windows\"")
        .build()

    val resp = client.newCall(request).execute()


    var bodyStr = resp.body!!.string()
        .replace("\\r\\n", "")
        .replace("\\n", "")
        .replace("\\r", "")
        // 替换转义符 \"
        .replace("\\\"", "\"")

    // 去掉首尾的 ”“
    bodyStr = bodyStr.substring(1, bodyStr.length - 1)

//    println(bodyStr)
    val cameraList: List<CameraData> = mapper.readValue(bodyStr, object : TypeReference<List<CameraData>>() {})

    return cameraList
}
fun writeFile(data: Map<String, List<CameraData>>) {
    val formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")
    val currentDate = LocalDateTime.now().format(formatter)

    // 输出到项目根目录的 datas/ 文件夹
    val file = File("datas", "camera_data_$currentDate.json")

    // 确保目录存在
    file.parentFile?.mkdirs()

    try {
        val jsonStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)
        file.writeText(jsonStr, Charsets.UTF_8)
        println("✅ 数据已保存至: ${file.absolutePath}")
    } catch (e: Exception) {
        System.err.println("❌ 文件保存失败: ${e.message}")
        e.printStackTrace()
    }
}

fun execute() {
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
    writeFile(data)
}