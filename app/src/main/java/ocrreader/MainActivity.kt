/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ocrreader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.common.api.CommonStatusCodes
import ocrreader.processing.GridCalibrationActivity
import ocrreader.processing.OcrCaptureActivity
import ocrreader.processing.OcrProcessingActivity
import java.util.*


/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * recognizes text.
 */
class MainActivity : Activity() {

    // Use a compound button so either checkbox or switch widgets work.
    @BindView(R.id.autoFocus)
    lateinit var autoFocus: CompoundButton
    @BindView(R.id.useFlash)
    lateinit var useFlash: CompoundButton
    @BindView(R.id.status_message)
    lateinit var statusMessage: TextView
    @BindView(R.id.text_value)
    lateinit var textValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
    }

    @OnClick(R.id.btn_main_calibrationmode)
    fun startCalibrationActivity() {
        val intent = Intent(this, GridCalibrationActivity::class.java)
        startActivityForResult(intent, RC_OCR_CALIBRATE)
    }

    @OnClick(R.id.btn_main_serverconfig)
    fun startServerActivity() {
        val intent = Intent(this, ServerActivity::class.java)
        startActivity(intent)
    }

    @OnClick(R.id.btn_main_gamemode)
    fun startGameActivity() {
        // launch Ocr capture activity.
        val intent = Intent(this, OcrProcessingActivity::class.java)
        intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked)
        intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked)

        startActivity(intent)
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * [.RESULT_CANCELED] if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     *
     *
     *
     * You will receive this call immediately before onResume() when your
     * activity is re-starting.
     *
     *
     *
     * @param requestCode The integer request code originally supplied to
     * startActivityForResult(), allowing you to identify who this
     * result came from.
     * @param resultCode  The integer result code returned by the child activity
     * through its setResult().
     * @param data        An Intent, which can return result data to the caller
     * (various data can be attached to Intent "extras").
     * @see .startActivityForResult
     *
     * @see .createPendingResult
     *
     * @see .setResult
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != RC_OCR_CALIBRATE) {
            statusMessage.text = String.format(getString(R.string.ocr_error),
                    CommonStatusCodes.getStatusCodeString(resultCode))
            return
        }

        if (resultCode != CommonStatusCodes.SUCCESS) {
            return super.onActivityResult(requestCode, resultCode, data)
        }

        if (data != null) {
            val text = data.getSerializableExtra(GridCalibrationActivity.GridElements) as HashSet<*>

            statusMessage.setText(R.string.ocr_success)
            textValue.text = "${text.size}"
            Log.d(TAG, "Text read: $text")
        } else {
            statusMessage!!.setText(R.string.ocr_failure)
            Log.d(TAG, "No Text captured, intent data is null")
        }
    }

    companion object {
        private const val RC_OCR_CALIBRATE = 9003
        private const val TAG = "MainActivity"
    }
}