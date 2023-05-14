package com.example.credentials.data.model

class Entry {
    var NAME: String? = null
        private set
    var PASS: String? = null
        private set
    var URL: String? = null
        private set
    var DOC_ID: String? = null
        private set

    constructor() {}
    constructor(NAME: String?, PASS: String?, URL: String?, DOC_ID: String?) {
        this.NAME = NAME
        this.PASS = PASS
        this.URL = URL
        this.DOC_ID = DOC_ID
    }
}