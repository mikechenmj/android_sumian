package com.sumian.device.util

data class Cmd(var cmd: ByteArray, var resultCmds: MutableList<ByteArray> = mutableListOf(), var retry: Boolean = true, var priority: Priority = Priority.NORMAL, var timeMill: Long = 0, var retryTime: Int = 0) : Comparable<Cmd> {

    override fun compareTo(other: Cmd): Int {
        return if (priority > other.priority) {
            1
        } else if (priority < other.priority) {
            -1
        } else {
            if (timeMill > other.timeMill) {
                1
            } else {
                -1
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cmd

        if (!cmd.contentEquals(other.cmd)) return false
        if (resultCmds != other.resultCmds) return false
        if (retry != other.retry) return false
        if (priority != other.priority) return false
        if (timeMill != other.timeMill) return false
        if (retryTime != other.retryTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cmd.contentHashCode()
        result = 31 * result + resultCmds.hashCode()
        result = 31 * result + retry.hashCode()
        result = 31 * result + priority.hashCode()
        result = 31 * result + timeMill.hashCode()
        result = 31 * result + retryTime
        return result
    }

    enum class Priority {
        RETRY, NORMAL
    }
}