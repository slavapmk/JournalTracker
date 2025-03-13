package ru.slavapmk.journalTracker.viewModels

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.backend.RetrofitInstance

data class VersionInfo(
    val tag: Int,
    val version: String,
    val url: String
)

class MainActivityViewModel : ViewModel() {
    companion object {
        const val REPO_OWNER = "slavapmk"
        const val REPO_NAME = "JournalTracker"
    }

    val checkLiveData by lazy { MutableLiveData<VersionInfo>() }

    fun performCheckUpdates(context: Context) {
        val appVersionInfo = getAppVersionInfo(context)
        viewModelScope.launch {
            val checkUpdates = checkUpdates(appVersionInfo)
            if (checkUpdates == null) {
                return@launch
            } else {
                checkLiveData.postValue(checkUpdates)
            }
        }
    }

    private fun getAppVersionInfo(context: Context): Int? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.longVersionCode.toInt()
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private suspend fun checkUpdates(current: Int?): VersionInfo? {
        val latest = getLatestVersion()
        if (current == null) {
            return latest
        }
        if (latest == null) {
            return null
        }
        if (latest.tag > current) {
            return latest
        }
        return null
    }

    private suspend fun getLatestVersion(): VersionInfo? {
        val releases = RetrofitInstance.githubApi.getReleases(REPO_OWNER, REPO_NAME)
        val firstOrNull = releases.firstOrNull()
        return firstOrNull?.let {
            VersionInfo(
                it.tagName.toInt(),
                it.name,
                it.htmlUrl
            )
        }
    }
}