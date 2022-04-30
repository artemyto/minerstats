package misha.miner.domain

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import com.pluto.plugins.logger.PlutoLog
import javax.inject.Inject

class LaunchChromeCustomTabUseCase @Inject constructor() {

    fun execute(context: Context, url: String) {
        try {
            val uri = Uri.parse(url)
            val launched = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                launchNativeBeforeApi30(context, uri)
            } else {
                launchNativeApi30(context, uri)
            }

            if (!launched) {
                val customTabsIntent = CustomTabsIntent
                    .Builder()
                    .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                    .build()
                customTabsIntent.launchUrl(context, uri)
            }
        } catch (ex: ActivityNotFoundException) {
            PlutoLog.d(this.javaClass.simpleName, ex.message.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun launchNativeApi30(context: Context, uri: Uri?): Boolean {
        val nativeAppIntent =
            Intent(Intent.ACTION_VIEW, uri)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
                )
        return try {
            context.startActivity(nativeAppIntent)
            true
        } catch (ex: ActivityNotFoundException) {
            false
        }
    }

    private fun launchNativeBeforeApi30(context: Context, uri: Uri): Boolean {
        val pm = context.packageManager

        val browserActivityIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts("http", "", null))
        val genericResolvedList: Set<String> = extractPackageNames(
            pm.queryIntentActivities(browserActivityIntent, 0)
        )

        val specializedActivityIntent = Intent(Intent.ACTION_VIEW, uri)
            .addCategory(Intent.CATEGORY_BROWSABLE)
        val resolvedSpecializedList: MutableSet<String> = extractPackageNames(
            pm.queryIntentActivities(specializedActivityIntent, 0)
        )

        resolvedSpecializedList.removeAll(genericResolvedList)

        if (resolvedSpecializedList.isEmpty()) {
            return false
        }

        specializedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(specializedActivityIntent)
        return true
    }

    private fun extractPackageNames(listInfo: List<ResolveInfo>): MutableSet<String> {
        val packageNameSet: MutableSet<String> = HashSet()
        for (ri in listInfo) {
            packageNameSet.add(ri.activityInfo.packageName)
        }
        return packageNameSet.toMutableSet()
    }
}