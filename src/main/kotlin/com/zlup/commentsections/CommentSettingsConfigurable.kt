package com.zlup.commentsections

import com.intellij.openapi.options.Configurable
import javax.swing.*
import javax.swing.text.*
import java.awt.*

class CommentSettingsConfigurable : Configurable {
    private var panel: JPanel? = null

    private var singleLineCharField: JTextField? = null
    private var multiLineCharFieldFirstLine: JTextField? = null
    private var multiLineCharFieldMiddleLine: JTextField? = null
    private var multiLineCharFieldLastLine: JTextField? = null
    private var totalWidth: JTextField? = null

    override fun createComponent(): JComponent {
        panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.CENTER
            insets = Insets(0, 10, 5, 10)
            weightx = 1.0
        }

        val titleFont = panel!!.font.deriveFont(15f).deriveFont(Font.BOLD)

        // 🔹 Заголовок "Single Line comment"
        val singleLineLabel = JLabel("Single Line Comment").apply { font = titleFont }
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 2
        panel!!.add(singleLineLabel, gbc)

        // 🔹 Подпись и поле для однострочного комментария
        gbc.gridwidth = 1
        gbc.gridy++
        gbc.gridx = 0
        panel!!.add(JLabel("Single-line comment symbol:"), gbc)

        singleLineCharField = JTextField(2)
        limitInputToOneChar(singleLineCharField!!)
        gbc.gridx = 1
        panel!!.add(singleLineCharField, gbc)

        // 🔹 Разделитель
        gbc.gridy++
        gbc.gridx = 0
        gbc.gridwidth = 2
        panel!!.add(JSeparator(), gbc)

        // 🔹 Заголовок "Multi Line comment"
        gbc.gridy++
        val multiLineLabel = JLabel("Multi Line Comment").apply { font = titleFont }
        panel!!.add(multiLineLabel, gbc)

        gbc.gridwidth = 1

        // 🔹 First line
        gbc.gridy++
        gbc.gridx = 0
        panel!!.add(JLabel("Multi-line comment symbol (first line):"), gbc)
        multiLineCharFieldFirstLine = JTextField(2)
        limitInputToOneChar(multiLineCharFieldFirstLine!!)
        gbc.gridx = 1
        panel!!.add(multiLineCharFieldFirstLine, gbc)

        // 🔹 Middle line
        gbc.gridy++
        gbc.gridx = 0
        panel!!.add(JLabel("Multi-line comment symbol (middle line):"), gbc)
        multiLineCharFieldMiddleLine = JTextField(2)
        limitInputToOneChar(multiLineCharFieldMiddleLine!!)
        gbc.gridx = 1
        panel!!.add(multiLineCharFieldMiddleLine, gbc)

        // 🔹 Last line
        gbc.gridy++
        gbc.gridx = 0
        panel!!.add(JLabel("Multi-line comment symbol (last line):"), gbc)
        multiLineCharFieldLastLine = JTextField(2)
        limitInputToOneChar(multiLineCharFieldLastLine!!)
        gbc.gridx = 1
        panel!!.add(multiLineCharFieldLastLine, gbc)

        // 🔹 Разделитель
        gbc.gridy++
        gbc.gridx = 0
        gbc.gridwidth = 2
        panel!!.add(JSeparator(), gbc)

        // 🔹 Заголовок "Comments Width"
        gbc.gridy++
        val commentsWidthLable = JLabel("Comment Width").apply { font = titleFont }
        panel!!.add(commentsWidthLable, gbc)

        // 🔹 Width
        gbc.gridy++
        gbc.gridx = 0
        panel!!.add(JLabel("Width:"), gbc)
        totalWidth = JTextField(2)
        gbc.gridx = 1
        panel!!.add(totalWidth, gbc)

        return JPanel(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
            add(panel, BorderLayout.NORTH)
        }
    }

    override fun isModified(): Boolean {
        val settings = CommentSettings.getInstance().state

        val currentSingle = singleLineCharField!!.text
        val currentFirst = multiLineCharFieldFirstLine!!.text
        val currentMiddle = multiLineCharFieldMiddleLine!!.text
        val currentLast = multiLineCharFieldLastLine!!.text
        val currentWidth = totalWidth!!.text.toInt()

        return currentSingle != settings.singleCommentChar ||
                currentFirst != settings.multiCommentFirst ||
                currentMiddle != settings.multiCommentMiddle ||
                currentLast != settings.multiCommentLast ||
                currentWidth != settings.totalWidth
    }

    override fun apply() {
        val settings = CommentSettings.getInstance().state
        settings.singleCommentChar = singleLineCharField!!.text
        // При желании можно добавить:
        settings.multiCommentFirst = multiLineCharFieldFirstLine!!.text
        settings.multiCommentMiddle = multiLineCharFieldMiddleLine!!.text
        settings.multiCommentLast = multiLineCharFieldLastLine!!.text
        settings.totalWidth = totalWidth!!.text.toInt()
    }

    override fun reset() {
        val settings = CommentSettings.getInstance().state
        singleLineCharField!!.text = settings.singleCommentChar

        multiLineCharFieldFirstLine!!.text = settings.multiCommentFirst
        multiLineCharFieldMiddleLine!!.text = settings.multiCommentMiddle
        multiLineCharFieldLastLine!!.text = settings.multiCommentLast
        totalWidth!!.text = settings.totalWidth.toString()
    }

    override fun getDisplayName(): String = "Comment Sections"

    /**
     * Ограничивает ввод в JTextField одним символом.
     */
    private fun limitInputToOneChar(textField: JTextField) {
        val doc = textField.document as AbstractDocument
        doc.documentFilter = object : DocumentFilter() {
            override fun replace(
                fb: FilterBypass,
                offset: Int,
                length: Int,
                text: String?,
                attrs: AttributeSet?
            ) {
                val currentText = fb.document.getText(0, fb.document.length)
                val newText = ((currentText.substring(0, offset) + (text ?: "") +
                        currentText.substring(offset + length))).take(1)

                fb.replace(0, fb.document.length, newText, attrs) // ✅ корректно
            }

            override fun insertString(fb: FilterBypass, offset: Int, text: String?, attrs: AttributeSet?) {
                if (!text.isNullOrEmpty()) {
                    val currentText = fb.document.getText(0, fb.document.length)
                    val newText = (currentText + text).take(1)
                    fb.replace(0, fb.document.length, newText, attrs) // ✅ без рекурсии
                }
            }
        }
    }
}
