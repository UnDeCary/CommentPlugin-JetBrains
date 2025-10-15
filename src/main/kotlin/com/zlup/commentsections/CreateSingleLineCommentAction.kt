package com.zlup.commentsections

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages
import kotlin.text.isNotBlank
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.actionSystem.CommonDataKeys

class CreateSingleLineCommentAction : AnAction("Create Single-Line Comment Section"){
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)

        if (editor != null) {
            val project = e.project
            val prompt = Messages.showInputDialog(project, "Enter comment text:", "Single-Line Comment", null)
            if (prompt != null && prompt.isNotBlank()) {
                createSingleLineComment(editor, file, prompt)
            }
        }
    }

    private fun createSingleLineComment(editor: Editor, file: VirtualFile?, text: String) {

        val fileType = file?.fileType?.name?.lowercase() ?: "unknown"
        val commentPrefix = getCommentPrefix(fileType)

        val settings = CommentSettings.getInstance()
        val singleLineSymbol = settings.state.singleCommentChar

        val totalWidth = settings.state.totalWidth

        val padSize = (totalWidth - text.length - 2).coerceAtLeast(0)
        val left = singleLineSymbol.repeat(padSize / 2 + (2 - commentPrefix.length))
        val right = singleLineSymbol.repeat(padSize - padSize / 2)
        val comment = "$commentPrefix $left $text $right\n"

        WriteCommandAction.runWriteCommandAction(editor.project) {
            editor.document.insertString(editor.caretModel.offset, comment)
        }
    }

    private fun getCommentPrefix(fileType: String): String {
        return when (fileType) {
            "cpp", "java", "javascript", "kotlin", "c" -> "//"
            "sql" -> "--"
            "python", "shell script", "bash" -> "#"
            else -> "//"
        }
    }
}