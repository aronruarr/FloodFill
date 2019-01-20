package com.example.floodfill.floodfill.algorithm

interface IProgressListener {

    fun onPrepare()

    fun onProgress()

    fun onComplete()

}