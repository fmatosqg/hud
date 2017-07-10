package com.fmatos.samples.hud.service.model.amazingwallpapers

import com.fmatos.samples.hud.testability.Mockable

/**
 * Created by fmatos on 1/07/2017.
 */

@Mockable
data class Album(val name: String?, val niceName: String?, val photos: List<Photo>)