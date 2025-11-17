package com.example.devicehealth.data.repository

import com.example.devicehealth.data.local.Tip
import com.example.devicehealth.data.local.TipDao
import com.example.devicehealth.repository.TipApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TipRepository(
    private val api: TipApiService,
    private val dao: TipDao
) {
    val tips: Flow<List<Tip>> = dao.getTips()
    private val fallbackTips = listOf(
        "Keep your screen brightness optimized for longer battery life.",
        "Clear unused apps running in the background.",
        "Restart your device weekly to maintain performance.",
        "Avoid overcharging to extend battery lifespan.",
        "Disable GPS when not needed to save power.",
        "Turn off Bluetooth when it's not in use.",
        "Use Wi-Fi instead of mobile data to reduce battery drain.",
        "Limit notifications to essential apps only.",
        "Uninstall unused apps to free resources.",
        "Keep your OS updated for better optimization.",
        "Update apps regularly for performance improvements.",
        "Enable power saver mode during low battery.",
        "Reduce screen timeout duration.",
        "Disable unnecessary vibrations.",
        "Use dark mode on OLED screens to save power.",
        "Clear cache for heavy apps periodically.",
        "Disable auto-sync for apps that don't need real-time updates.",
        "Maintain at least 20% free storage space.",
        "Use lite versions of resource-heavy apps.",
        "Disable auto-download for media in messaging apps.",
        "Manage app permissions to prevent background abuse.",
        "Use certified chargers for power efficiency.",
        "Turn off NFC when you're not using it.",
        "Prevent apps from always accessing your location.",
        "Avoid live wallpapers that consume CPU cycles.",
        "Reduce widget usage on the home screen.",
        "Disable haptic feedback for the keyboard.",
        "Monitor battery health through system tools.",
        "Avoid gaming while charging to prevent overheating.",
        "Keep your phone away from extreme temperatures.",
        "Remove unnecessary custom launchers.",
        "Use built-in device optimization tools.",
        "Disable autoplay in video apps.",
        "Back up media to cloud storage and remove local copies.",
        "Disable continuous Wi-Fi scanning.",
        "Turn off hotspot after use.",
        "Simplify the home screen layout.",
        "Use efficient photo/video formats like HEIF/HEVC.",
        "Enable adaptive battery features.",
        "Disable raise-to-wake if it triggers too often.",
        "Stop apps from autostarting on boot.",
        "Uninstall duplicate utility apps.",
        "Use airplane mode in low-signal environments.",
        "Limit background data for specific apps.",
        "Close unnecessary browser tabs.",
        "Clear your downloads folder routinely.",
        "Reduce system animations for faster performance.",
        "Clean device ports and surfaces to reduce heat.",
        "Use antivirus only if you install apps from unknown sources.",
        "Avoid resource-draining third-party widgets.",
        "Use phone cases that dissipate heat efficiently.",
        "Offload rarely used apps instead of deleting them.",
        "Schedule Do Not Disturb during sleep hours.",
        "Use selective sync for cloud storage apps.",
        "Check battery usage stats to identify power-hungry apps."
    )


    private fun randomFallbackTip(): Tip {
        val msg = fallbackTips.random()
        println("Random fallback tip: $msg")
        return Tip(message = msg)
    }


    suspend fun syncTips() {
        try {
            val resp = api.fetchTip()
            val tip = Tip(message = resp.phrase)

            withContext(Dispatchers.IO) {
                dao.insertTips(listOf(tip))
            }

        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                dao.insertTips(listOf(randomFallbackTip()))
            }
        }
    }



    suspend fun refreshMultiple(count: Int = 1) {
        try {
            val resp = api.fetchTip()
            val tip = Tip(message = resp.phrase)

            withContext(Dispatchers.IO) {
                dao.insertTips(listOf(tip))
            }

        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                dao.insertTips(listOf(randomFallbackTip()))
            }
        }
    }
}
