package com.example.floodfill.floodfill

import com.example.floodfill.floodfill.algorithm.EightDirectionsFloodFillAlgorithm
import com.example.floodfill.floodfill.algorithm.ForestFireFloodFillAlgorithm
import com.example.floodfill.floodfill.algorithm.IFloodFillAlgorithm
import com.example.floodfill.floodfill.algorithm.ScanLineFloodFillAlgorithm

class FloodFill {

    enum class Algorithm {
        SCAN_LINE,
        FOREST_FIRE,
        EIGHT_DIRECTIONS
    }

    companion object {

        @JvmStatic
        fun from(type: Algorithm): IFloodFillAlgorithm = when (type) {
            Algorithm.SCAN_LINE -> ScanLineFloodFillAlgorithm()
            Algorithm.FOREST_FIRE -> ForestFireFloodFillAlgorithm()
            Algorithm.EIGHT_DIRECTIONS -> EightDirectionsFloodFillAlgorithm()
        }

    }

}