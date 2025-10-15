package com.zlup.commentsections

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages
import kotlin.text.isNotBlank
import com.intellij.openapi.vfs.VirtualFile

class CreateMultiLineCommentAction : AnAction("Create Multi-Line Comment Section") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)

        if (editor != null) {
            val project = e.project
            val prompt = Messages.showInputDialog(project, "Enter comment text:", "Multi-Line Comment", null)
            if (prompt != null && prompt.isNotBlank()) {
                createMultiLineComment(editor, file, prompt)
            }
        }
    }

    private fun createMultiLineComment(editor: Editor, file: VirtualFile?, text: String) {
        val fileType = file?.fileType?.name?.lowercase() ?: "unknown"
        val commentPrefix = getCommentPrefix(fileType)

        val settings = CommentSettings.getInstance()
        val multiLineCommentFirst = settings.state.multiCommentFirst
        val multiLineCommentMiddle = settings.state.multiCommentMiddle
        val multiLineCommentLast = settings.state.multiCommentLast

        val totalWidth = settings.state.totalWidth

        val firstLine = "$commentPrefix " + multiLineCommentFirst.repeat(totalWidth + (2 - commentPrefix.length))
        val lastLine = "$commentPrefix " + multiLineCommentLast.repeat(totalWidth + (2 - commentPrefix.length))
        val padSize = (totalWidth - text.length - 2).coerceAtLeast(0)
        val left = multiLineCommentMiddle.repeat(padSize / 2 + (2 - commentPrefix.length))
        val right = multiLineCommentMiddle.repeat(padSize - padSize / 2)
        val middle = "$commentPrefix $left $text $right"
        val comment = firstLine + "\n" + middle + "\n" + lastLine

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