package com.zlup.commentsections

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.project.Project
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiFile

class CommentCommandHandler : EnterHandlerDelegate {

    override fun preprocessEnter(
        file: PsiFile,
        editor: Editor,
        caretOffset: Ref<Int>,
        caretAdvance: Ref<Int>,
        dataContext: DataContext,
        originalHandler: EditorActionHandler?
    ): EnterHandlerDelegate.Result {
        return EnterHandlerDelegate.Result.Continue
    }

    override fun postProcessEnter(
        file: PsiFile,
        editor: Editor,
        dataContext: DataContext
    ): EnterHandlerDelegate.Result {

        val document = editor.document
        val caret = editor.caretModel.currentCaret
        val offset = caret.offset

        val lineNumber = document.getLineNumber(offset) - 1
        val lineStart = document.getLineStartOffset(lineNumber)
        val lineEnd = document.getLineEndOffset(lineNumber)
        val lineText = document.getText(TextRange(lineStart, lineEnd)).trim()

        val fileType = file.fileType.name.lowercase()
        val commentPrefix = getCommentPrefix(fileType)

        val settings = CommentSettings.getInstance()
        val singleLineSymbol = settings.state.singleCommentChar
        val multiLineSymbolFirst = settings.state.multiCommentFirst
        val multiLineSymbolMiddle = settings.state.multiCommentMiddle
        val multiLineSymbolLast = settings.state.multiCommentLast

        val totalWidth = settings.state.totalWidth

        if (lineText.startsWith("cmts ")) {
            val text = lineText.removePrefix("cmts").trim()

            val padSize = (totalWidth - text.length - 2).coerceAtLeast(0)

            val left = singleLineSymbol.repeat(padSize / 2 + (2 - commentPrefix.length))
            val right = singleLineSymbol.repeat(padSize - padSize / 2)

            val comment = "$commentPrefix $left $text $right"

            document.replaceString(lineStart, lineEnd, comment)
        }
        else if (lineText.startsWith("cmtm ")) {
            val text = lineText.removePrefix("cmts").trim()

            val padSize = (totalWidth - text.length - 2).coerceAtLeast(0)

            val firstLine = "$commentPrefix " + multiLineSymbolFirst.repeat(totalWidth + (2 - commentPrefix.length))
            val lastLine = "$commentPrefix " + multiLineSymbolLast.repeat(totalWidth + (2 - commentPrefix.length))
            val left = multiLineSymbolMiddle.repeat(padSize / 2 + (2 - commentPrefix.length))
            val right = multiLineSymbolMiddle.repeat(padSize - padSize / 2)
            val middle = "$commentPrefix $left $text $right"

            val comment = firstLine + "\n" + middle + "\n" + lastLine

            document.replaceString(lineStart, lineEnd, comment)
        }

        return EnterHandlerDelegate.Result.Continue
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