package com.fmatos.samples.hud.utils.dagger

import dagger.Component
import javax.inject.Singleton


/**
 * Created by fmatos on 10/07/2017.
 */

@Singleton
@Component(modules = arrayOf(NetModule::class))
interface NetComponent {

}