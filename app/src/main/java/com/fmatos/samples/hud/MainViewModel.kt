package com.fmatos.samples.hud

import android.graphics.drawable.Drawable
import android.text.format.Formatter
import android.text.format.Formatter.formatIpAddress
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmatos.samples.hud.io.controller.ServoController
import com.fmatos.samples.hud.service.WallpaperService
import com.squareup.picasso.Picasso
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import timber.log.Timber
import java.net.Inet4Address
import java.net.InetAddress

/**
 * @author : Fabio de Matos
 * @since : 07/09/2019
 **/
@FlowPreview
class MainViewModel(
    private val timezone: DateTimeZone,
    private val wallpaperService: WallpaperService,
    private val dataRepository: DataRepository
) : ViewModel() {

    val date = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    val am = MutableLiveData<Int>()
    val pm = MutableLiveData<Int>()

    val wifi = MutableLiveData<String>()

    val imgUrl = MutableLiveData<String>()
    val imgPlaceholder: LiveData<Int>


    init {

        imgPlaceholder = MutableLiveData(R.drawable.rocket_diamonds)
        startTicking()
    }

    private fun startTicking() {

        viewModelScope.launch {
            while (true) {

                delay(1_000)
                updateTime(true)
                delay(1_000)
                updateTime(false)
            }
        }

        viewModelScope.launch {
            while (true) {
                updateWifi()
                delay(60_000)
            }
        }

        viewModelScope.launch {
            while (true) {
                updateImage()
            }
        }


    }

    private fun updateWifi() {

        dataRepository.getWifiInfo()
            .let {

                @Suppress("DEPRECATION")
                val address = formatIpAddress(it?.ipAddress ?: 0)
                "Wifi: %s - %s".format(it?.ssid, address)
            }
            .let { wifi.postValue(it) }
    }

    @FlowPreview
    private suspend fun updateImage() {

        wallpaperService
            .getList()
            .asFlow()
            .collect {
                imgUrl.postValue(it)
                Timber.v("adapter 11 -- $it")
                delay(60_000)
            }
    }

    private fun updateTime(isShowColon: Boolean) {

        val dateTime = DateTime.now().toDateTime(timezone)
        val locale = java.util.Locale("en", "AU")

        DateTimeFormat.forPattern("h:mm")
            .withLocale(locale).print(dateTime)
            .let {
                if (isShowColon)
                    it.replace(':', ' ')
                else it
            }
            .let { time.postValue(it) }


        DateTimeFormat.forPattern("EEEEE, dd MMMM yyyy")
            .withLocale(locale).print(dateTime)
            .let {
                date.postValue(it)
            }

        DateTimeFormat.forPattern("a")
            .withLocale(locale).print(dateTime)
            .let { it.equals("AM") }
            .let { isAm ->

                if (isAm) {
                    View.VISIBLE to View.INVISIBLE
                } else {
                    View.GONE to View.VISIBLE
                }
            }
            .let {
                am.postValue(it.first)
                pm.postValue(it.second)
            }

    }


}

@BindingAdapter(value = ["imageUrl", "placeholder"], requireAll = false)
fun setImageUrl(imageView: ImageView, url: String?, placeHolder: Drawable?) {

    Timber.i("Set image from adapter: $url / $placeHolder")
    if (url == null) {
        imageView.setImageDrawable(placeHolder)
    } else {
        Picasso
            .get()
            .load(url)
            .into(imageView)
    }
}
