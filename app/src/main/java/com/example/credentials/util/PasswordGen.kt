package com.example.credentials.util

object PasswordGen {
    private var mCaps = false
    private var mSmall = false
    private var mSymbols = false
    private var mDigits = false
    @JvmStatic
    fun generate(length: Int, caps: Boolean, small: Boolean, digits: Boolean, symbols: Boolean): String {
        var length = length
        mCaps = caps
        mDigits = digits
        mSymbols = symbols
        mSmall = small
        val arr = ArrayList<String?>()
        if (caps) {
            arr.add(randomCapital())
            length--
        }
        if (small) {
            arr.add(randomSmall())
            length--
        }
        if (symbols) {
            arr.add(randomSymbol())
            length--
        }
        if (digits) {
            arr.add(randomDigit())
            length--
        }
        arr.add(randomAll(length))
        val pass = StringBuilder()
        while (arr.size > 0) {
            val i = 3 % arr.size
            pass.append(arr[i])
            arr.removeAt(i)
        }
        return pass.toString()
    }

    private fun randomCapital(): String {
        var r = (Math.random() * 100).toInt()
        r = r % 26
        return ('A'.code + r).toChar().toString()
    }

    private fun randomSmall(): String {
        var r = (Math.random() * 100).toInt()
        r = r % 26
        return ('a'.code + r).toChar().toString()
    }

    private fun randomDigit(): String {
        var r = (Math.random() * 10).toInt()
        r = r % 10
        return r.toString()
    }

    private fun randomSymbol(): String {
        var r = (Math.random() * 100).toInt()
        val arr = charArrayOf('@', '#', '$', '%', '=', ':', '?', '.', '/', '|', '~', '>', '*', '(', ')', '<')
        r = r % arr.size
        return arr[r].toString()
    }

    private fun randomAll(n: Int): String? {
        if (n < 1) return null
        val sb = StringBuilder()
        var i = 0
        while (i < n) {
            val r = (Math.random() * 4).toInt()
            when (r) {
                0 -> if (mCaps) {
                    sb.append(randomCapital())
                    i++
                }
                1 -> if (mDigits) {
                    sb.append(randomDigit())
                    i++
                }
                2 -> if (mSmall) {
                    sb.append(randomSmall())
                    i++
                }
                3 -> if (mSymbols) {
                    sb.append(randomSymbol())
                    i++
                }
            }
        }
        return sb.toString()
    }
}