package com.karizal.ads_channel_unity

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.karizal.ads_base.AdsBaseConst
import com.karizal.ads_base.contract.BannerContract
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize


class UnityBanner(
    private val data: UnityData
) : BannerContract {
    override val name: String = AdsBaseConst.unity
    override var isDebug: Boolean = false
    override var activity: Activity? = null

    override fun initialize(activity: Activity, isDebug: Boolean) {
        super.initialize(activity, isDebug)
        if (!UnityAds.isInitialized) {
            UnityAds.initialize(
                activity.applicationContext,
                data.app_id,
                isDebug,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        Log.i(this@UnityBanner.getClassName(), "Unity.initializeSdk Complete")

                    }

                    override fun onInitializationFailed(
                        error: UnityAds.UnityAdsInitializationError?,
                        message: String?
                    ) {
                        Log.i(this@UnityBanner.getClassName(), "Unity.initializeSdk Error")
                    }

                })
        }
    }

    override fun fetch(
        container: ViewGroup,
        preparing: () -> Unit,
        possibleToLoad: () -> Boolean,
        onSuccessLoaded: (channel: String) -> Unit,
        onFailedLoaded: () -> Unit
    ) {
        activity ?: return
        prepareContainerView(container)
        preparing.invoke()

        val banner = BannerView(activity, "Banner_Android", UnityBannerSize.standard)

        banner.listener = object : BannerView.Listener() {
            override fun onBannerLoaded(bannerView: BannerView?) {
                onSuccessLoaded.invoke(name)
                Log.i(this@UnityBanner.getClassName(), "Unity.banner.onAdLoaded")
            }

            override fun onBannerShown(bannerAdView: BannerView?) {
                Log.i(this@UnityBanner.getClassName(), "Unity.banner.onBannerShown")
            }

            override fun onBannerClick(bannerView: BannerView?) {
                Log.i(this@UnityBanner.getClassName(), "Unity.banner.onBannerClick")
            }

            override fun onBannerFailedToLoad(
                bannerView: BannerView?,
                bannerErrorInfo: BannerErrorInfo?
            ) {
                onFailedLoaded.invoke()
                Log.i(this@UnityBanner.getClassName(), "Unity.banner.onBannerFailedToLoad : ${bannerErrorInfo?.errorMessage}")
            }

            override fun onBannerLeftApplication(bannerView: BannerView?) {
                Log.i(this@UnityBanner.getClassName(), "Unity.banner.onBannerLeftApplication")
            }
        }

        if (possibleToLoad.invoke()) {
            banner.load()
            container.removeAllViewsInLayout()
            container.addView(banner)
            (banner.layoutParams as RelativeLayout.LayoutParams)
                .addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        }
    }
}