package org.example.client

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream

class TestUI : Application() {
    private val datasDir = File("datas").absoluteFile
    private val listView = ListView<String>().apply {
        prefHeight=600.0
    }
    private val comboBox = ComboBox<String>()
//    将内容框设置为上下滑动 多行显示
    private val contentArea = TextArea().apply {
//        isWrapText = false
        prefRowCount = 20
//    高度
        prefColumnCount = 70
        scrollTop = 0.0
        scrollLeft = 0.0
    }

    override fun start(stage: Stage) {
        if (!datasDir.exists()) {
            Alert(Alert.AlertType.ERROR).apply {
                title = "错误"
                headerText = "数据目录不存在"
                contentText = "请创建 'datas' 目录并放入 .gz 文件"
            }.showAndWait()
            return
        }

        setupUI(stage)
        loadGzFiles()
    }

    private fun setupUI(stage: Stage) {
        contentArea.isEditable = false
        contentArea.font = javafx.scene.text.Font.font(12.0)

        // 左侧：GZ 文件列表
        listView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                comboBox.value = newValue
                previewGzContent(newValue)
            }
        }

        val leftPanel = VBox(Label("📦 GZ 文件列表"), listView)
        leftPanel.spacing = 8.0
        leftPanel.padding = javafx.geometry.Insets(10.0)

        // 右侧：下拉框 + 内容预览
        comboBox.promptText = "选择 .gz 文件查看内容"
        comboBox.onAction = javafx.event.EventHandler {
            val selected = comboBox.value
            if (selected != null) {
                listView.selectionModel.select(selected)
                previewGzContent(selected)
            }
        }

        val rightPanel = VBox(
            Label("🔍 查看内容"),
            comboBox,
            Label("📄 文件内容预览"),
            contentArea
        )
        rightPanel.spacing = 8.0
        rightPanel.padding = javafx.geometry.Insets(10.0)
        // 右侧内容区域占满剩余空间
        VBox.setVgrow(contentArea, javafx.scene.layout.Priority.ALWAYS)

        // 解压按钮
        val extractBtn = Button("⏏️ 解压选中文件")

        extractBtn.onAction = javafx.event.EventHandler {
            val selected = listView.selectionModel.selectedItem
            if (selected == null) {
                Alert(Alert.AlertType.WARNING).apply {
                    title = "提示"
                    headerText = "未选择文件"
                    contentText = "请先在左侧选择一个 .gz 文件"
                }.show()
//                return@Button
            }
            extractGzFile(selected)
        }

        // 布局
        val center = HBox(leftPanel, Separator().apply { orientation = javafx.geometry.Orientation.VERTICAL }, rightPanel)
        center.spacing = 10.0
        center.padding = javafx.geometry.Insets(10.0)

        val root = BorderPane()
        root.center = center
        root.bottom = HBox(extractBtn).apply {
            padding = javafx.geometry.Insets(10.0)
            spacing = 10.0
        }

        stage.title = "GZ 文件查看器"
        stage.scene = Scene(root, 1000.0, 600.0)
        stage.centerOnScreen()
        stage.show()
    }

    private fun loadGzFiles() {
        val gzFiles = datasDir.listFiles { file -> file.name.endsWith(".gz", ignoreCase = true) }
            ?.map { it.name }
            ?.sorted()
            ?: emptyList()

        listView.items = FXCollections.observableArrayList(gzFiles)
        comboBox.items = FXCollections.observableArrayList(gzFiles)
    }

    private fun previewGzContent(gzFilename: String) {
        val gzFile = File(datasDir, gzFilename)
        if (!gzFile.exists()) {
            contentArea.text = "文件不存在: $gzFilename"
            return
        }

        try {
            GZIPInputStream(FileInputStream(gzFile)).use { gzis ->
                // 尝试读取前 1024 字节作为预览
                val buffer = ByteArray(1024)
                val bytesRead = gzis.read(buffer)
                if (bytesRead <= 0) {
                    contentArea.text = "[文件为空]"
                    return
                }

                // 假设是文本
                val preview = String(buffer, 0, bytesRead, Charsets.UTF_8)
                contentArea.text = "【原始文件名】: ${stripGzExtension(gzFilename)}\n" +
                        "【预览】:\n$preview" +
                        if (bytesRead == 1024) "\n...(内容截断)" else ""
            }
        } catch (e: Exception) {
            contentArea.text = "❌ 无法读取内容: ${e.javaClass.simpleName}\n${e.message}"
        }
    }

    private fun extractGzFile(gzFilename: String) {
        val gzFile = File(datasDir, gzFilename)
        if (!gzFile.exists()) {
            Alert(Alert.AlertType.ERROR).apply {
                title = "错误"
                contentText = "文件不存在: $gzFilename"
            }.show()
            return
        }

        val outputFileName = stripGzExtension(gzFilename)
        val outputFile = File(datasDir, outputFileName)

        try {
            GZIPInputStream(FileInputStream(gzFile)).use { gzis ->
                FileOutputStream(outputFile).use { fos ->
                    gzis.copyTo(fos)
                }
            }

            Alert(Alert.AlertType.INFORMATION).apply {
                title = "成功"
                contentText = "已解压为: $outputFileName"
            }.show()
        } catch (e: Exception) {
            Alert(Alert.AlertType.ERROR).apply {
                title = "解压失败"
                contentText = "${e.javaClass.simpleName}: ${e.message}"
            }.show()
        }
    }

    private fun stripGzExtension(filename: String): String {
        return if (filename.endsWith(".gz", ignoreCase = true)) {
            filename.substring(0, filename.length - 3)
        } else {
            filename
        }
    }
}

// JVM 入口
fun main() {
    Application.launch(TestUI::class.java)
}