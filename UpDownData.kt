package com.example.updown
import android.net.TrafficStats
import java.math.RoundingMode

class UpDownData {

    private var updata = 0L
    private var downdata = 0L
    private var diffUp = 0L
    private var diffDown = 0L

    init {
        downdata  = TrafficStats.getTotalRxBytes()
        updata    = TrafficStats.getTotalTxBytes()
    }

    fun updateData() {
        val down  = TrafficStats.getTotalRxBytes()
        val up    = TrafficStats.getTotalTxBytes()
        diffUp    = up - updata
        diffDown  = down - downdata
        updata    = up
        downdata  = down

    }

    fun getSpeed(type: TYPE) : Pair<Any, String> {

        val diff = when(type) {
            TYPE.TYPE_UP -> diffUp
            TYPE.TYPE_DOWN -> diffDown
            else -> diffDown + diffUp
        }
        return parse(diff)
    }

    private fun parse(diff: Long) = when {

        diff > 1024*1024 -> Pair((diff/(1024*1024)).toBigDecimal().setScale(1, RoundingMode.UP),"MB/s")
        else -> Pair(diff/(1024), "KB/s")

    }
    }

enum class TYPE {
    TYPE_UP,
    TYPE_DOWN,
    TYPE_ALL
}