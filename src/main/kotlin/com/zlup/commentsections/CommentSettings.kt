package com.zlup.commentsections

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "CommentSettings", storages = [Storage("CommentSettings.xml")])
class CommentSettings: PersistentStateComponent<CommentSettings.State> {

    data class State (
        var singleCommentChar: String = "=",

        var multiCommentFirst: String = "=",
        var multiCommentMiddle: String = "=",
        var multiCommentLast: String = "=",

        var totalWidth: Int = 117
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): CommentSettings {
            return com.intellij.openapi.application.ApplicationManager.getApplication().getService(CommentSettings::class.java)
        }
    }
}