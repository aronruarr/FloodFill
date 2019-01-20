package com.example.floodfill.ui.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.floodfill.R
import com.example.floodfill.floodfill.FloodFill
import com.example.floodfill.ui.util.getIntNumber
import com.example.floodfill.ui.view.FloodFillView
import com.example.floodfill.viewmodel.FloodFillViewModel
import com.example.floodfill.viewmodel.FloodFillViewModel.Companion.DEFAULT_IMAGE_HEIGHT_PX
import com.example.floodfill.viewmodel.FloodFillViewModel.Companion.DEFAULT_IMAGE_WIDTH_PX

class FloodFillFragment : Fragment() {

    private lateinit var viewModel: FloodFillViewModel

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(FloodFillViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_flood_fill, container, false)

        bind(view)
        bindFirstFloodFill(view)
        bindSecondFloodFill(view)

        viewModel.apply {
            isLoading.observe(this@FloodFillFragment, imageLoadingObserver)
            image.observe(this@FloodFillFragment, imageObserver)
            getImage(false)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.apply {
            image.removeObserver(imageObserver)
            isLoading.removeObserver(imageLoadingObserver)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_flood_fill, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> viewModel.getImage()
            R.id.changeSize -> dialog = newChangeSizeDialog().apply { show() }
        }

        return true
    }

    private val imageObserver = Observer<Bitmap> { bitmap ->
        if (bitmap.isRecycled) {
            return@Observer
        }

        val text = getImageSizeText(bitmap.width, bitmap.height)

        firstFloodFillView.setImageBitmap(bitmap)
        firstImageSizeTextView.text = text

        secondFloodFillView.setImageBitmap(bitmap)
        secondImageSizeTextView.text = text
    }

    private val imageLoadingObserver = Observer<Boolean> { isLoading ->
        firstProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        secondProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun newChangeSizeDialog(): Dialog {
        val view = layoutInflater.inflate(R.layout.view_generate_image, null)

        val widthEditText = view.findViewById<EditText>(R.id.widthEditText)
        widthEditText.apply {
            setText("")
            append(viewModel.imageWidth.toString())
            requestFocus()
        }

        val heightEditText = view.findViewById<EditText>(R.id.heightEditText)
        heightEditText.apply {
            setText("")
            append(viewModel.imageHeight.toString())
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_generate_image_title)
            .setView(view)
            .setOnDismissListener { dialog = null }
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.apply {
                    imageWidth = getIntNumber(widthEditText.text.toString(), DEFAULT_IMAGE_WIDTH_PX)
                    imageHeight = getIntNumber(heightEditText.text.toString(), DEFAULT_IMAGE_HEIGHT_PX)
                    getImage()
                }
            }
            .create()
            .apply {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

                setOnShowListener {
                    val positiveButton = this.getButton(DialogInterface.BUTTON_POSITIVE)

                    val textWatcher = object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {
                        }

                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                            positiveButton?.isEnabled = if (s.isNotEmpty()) {
                                val number = getIntNumber(s.toString())
                                viewModel.isValidImageWidth(number) && viewModel.isValidImageHeight(number)
                            } else {
                                false
                            }
                        }
                    }

                    widthEditText.requestFocus()
                    widthEditText.addTextChangedListener(textWatcher)
                    heightEditText.addTextChangedListener(textWatcher)
                }
            }
    }

    private fun newChangeAlgorithmDialog(view: FloodFillView, titleView: TextView): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.common_algorithm)
            .setItems(R.array.flood_fill_algorithms) { _, which ->
                val algorithm = when (which) {
                    0 -> FloodFill.Algorithm.SCAN_LINE
                    1 -> FloodFill.Algorithm.FOREST_FIRE
                    2 -> FloodFill.Algorithm.EIGHT_DIRECTIONS
                    else -> throw IllegalArgumentException("Algorithm not found for value: $which")
                }

                viewModel.floodFillAlgorithm = algorithm
                view.floodFillAlgorithm = algorithm
                titleView.text = getTitleTextFor(algorithm)
            }
            .setOnDismissListener { dialog = null }
            .create()
    }

    private fun getImageSizeText(width: Int, height: Int) =
        "$width ${getString(R.string.common_symbols_multiplication_x)} $height"

    private fun getTitleTextFor(type: FloodFill.Algorithm) = getString(
        when (type) {
            FloodFill.Algorithm.SCAN_LINE -> R.string.flood_fill_algorithm_scan_line
            FloodFill.Algorithm.FOREST_FIRE -> R.string.flood_fill_algorithm_forest_fire
            FloodFill.Algorithm.EIGHT_DIRECTIONS -> R.string.flood_fill_algorithm_eight_directions
        }
    )

    private fun getAnimationSpeedText(format: String, value: Int) = String.format(format, value)

//    Boring stuff goes here

    private lateinit var firstTitleTextView: TextView
    private lateinit var firstFloodFillView: FloodFillView
    private lateinit var firstProgressBar: ProgressBar
    private lateinit var firstImageSizeTextView: TextView
    private lateinit var firstAlgorithmButton: Button

    private lateinit var secondTitleTextView: TextView
    private lateinit var secondFloodFillView: FloodFillView
    private lateinit var secondProgressBar: ProgressBar
    private lateinit var secondImageSizeTextView: TextView
    private lateinit var secondAlgorithmButton: Button

    private lateinit var animationSpeedValueTextView: TextView
    private lateinit var animationSpeedSeekBar: SeekBar

    private fun bind(view: View) {
        val formatMs = getString(R.string.common_format_ms)

        animationSpeedValueTextView = view.findViewById(R.id.animationSpeedValueTextView)
        animationSpeedValueTextView.text = getAnimationSpeedText(formatMs, 0)

        animationSpeedSeekBar = view.findViewById(R.id.animationSpeedSeekBar)
        animationSpeedSeekBar.max = 1000

        animationSpeedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val value = progress.toLong()

                animationSpeedValueTextView.text = String.format(formatMs, value)

                firstFloodFillView.updateIntervalMs = value
                secondFloodFillView.updateIntervalMs = value
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun bindFirstFloodFill(view: View) {
        firstFloodFillView = view.findViewById(R.id.firstFloodFillView)
        firstFloodFillView.viewModel = viewModel

        firstTitleTextView = view.findViewById(R.id.firstTitleTextView)
        firstTitleTextView.text = getTitleTextFor(firstFloodFillView.floodFillAlgorithm)

        firstProgressBar = view.findViewById(R.id.firstProgressBar)

        firstImageSizeTextView = view.findViewById(R.id.firstImageSizeTextView)

        firstAlgorithmButton = view.findViewById(R.id.firstAlgorithmButton)
        firstAlgorithmButton.setOnClickListener {
            dialog = newChangeAlgorithmDialog(firstFloodFillView, firstTitleTextView).apply { show() }
        }
    }

    private fun bindSecondFloodFill(view: View) {
        secondFloodFillView = view.findViewById(R.id.secondFloodFillView)
        secondFloodFillView.viewModel = viewModel

        secondTitleTextView = view.findViewById(R.id.secondTitleTextView)
        secondTitleTextView.text = getTitleTextFor(secondFloodFillView.floodFillAlgorithm)

        secondProgressBar = view.findViewById(R.id.secondProgressBar)

        secondImageSizeTextView = view.findViewById(R.id.secondImageSizeTextView)

        secondAlgorithmButton = view.findViewById(R.id.secondAlgorithmButton)
        secondAlgorithmButton.setOnClickListener {
            dialog = newChangeAlgorithmDialog(secondFloodFillView, secondTitleTextView).apply { show() }
        }
    }

}