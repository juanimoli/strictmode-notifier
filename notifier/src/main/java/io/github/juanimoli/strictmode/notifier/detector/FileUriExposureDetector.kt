package io.github.juanimoli.strictmode.notifier.detector

import io.github.juanimoli.strictmode.notifier.StrictModeLog

class FileUriExposureDetector : Detector {
    override fun detect(log: StrictModeLog): Boolean {
        return log.message.contains("StrictMode.onFileUriExposed")
    }
}
