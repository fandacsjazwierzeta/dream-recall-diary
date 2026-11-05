package com.dreamrecall.diary.presentation.backup

data class BackupUiState(
    val dreamCount: Int = 0,
    val exportPassphrase: String = "",
    val exportCode: String = "",
    val importCode: String = "",
    val importPassphrase: String = "",
    val working: Boolean = false,
    val added: Int = -1,
    val skipped: Int = 0,
    val failed: Boolean = false,
    val loaded: Boolean = false
) {
    val canExport: Boolean get() = exportPassphrase.length >= 6 && !working
    val canImport: Boolean
        get() = importPassphrase.length >= 6 && importCode.isNotBlank() && !working
}
