package com.karizal.ads_channel_unity

import android.app.Activity
import com.karizal.ads_base.AdsBaseConst
import com.karizal.ads_base.contract.InterstitialContract
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds


class UnityInterstitial(private val data: UnityData) : InterstitialContract {
    override val name: String = AdsBaseConst.unity
    override var isDebug: Boolean = false
    override var activity: Activity? = null
    override var onInitializeOK: (name: String) -> Unit = {}
    override var onInitializeError: (name: String) -> Unit = {}
    private var onHide: () -> Unit = {}
    private var onFailure: (Activity) -> Unit = {}

    private val showListener = object : IUnityAdsShowListener {
        override fun onUnityAdsShowFailure(
            placementId: String?,
            error: UnityAds.UnityAdsShowError?,
            message: String?
        ) {
            activity?.let { onFailure.invoke(it) }
        }

        override fun onUnityAdsShowStart(placementId: String?) {
        }

        override fun onUnityAdsShowClick(placementId: String?) {
        }

        override fun onUnityAdsShowComplete(
            placementId: String?,
            state: UnityAds.UnityAdsShowCompletionState?
        ) {
            onHide.invoke()
            activity?.let { initialize(it, isDebug, onInitializeOK, onInitializeError) }
        }
    }

    val loadListener = object : IUnityAdsLoadListener {
        override fun onUnityAdsAdLoaded(placementId: String?) {
            UnityAds.show(activity, "Interstitial_Android", showListener)
        }

        override fun onUnityAdsFailedToLoad(
            placementId: String?,
            error: UnityAds.UnityAdsLoadError?,
            message: String?
        ) {
            onInitializeError.invoke(name)
        }
    }

    private val initializeListener = object : IUnityAdsInitializationListener {
        override fun onInitializationComplete() {
            onInitializeOK(name)
        }

        override fun onInitializationFailed(
            error: UnityAds.UnityAdsInitializationError?,
            message: String?
        ) {
            onInitializeError(name)
        }

    }

    override fun initialize(
        activity: Activity,
        isDebug: Boolean,
        onInitializeOK: (name: String) -> Unit,
        onInitializeError: (name: String) -> Unit
    ) {
        super.initialize(activity, isDebug, onInitializeOK, onInitializeError)
        if (!UnityAds.isInitialized) {
            UnityAds.initialize(
                activity.applicationContext,
                data.app_id,
                isDebug,
                initializeListener
            )
        }
    }

    override fun show(
        activity: Activity,
        possibleToShow: (channel: String) -> Boolean,
        onHide: () -> Unit,
        onFailure: (activity: Activity) -> Unit
    ) {
        if (possibleToShow.invoke(name).not()) {
            return onFailure.invoke(activity)
        }

        this.onHide = onHide
        this.onFailure = onFailure

        UnityAds.load("Interstitial_Android", loadListener)
    }
}