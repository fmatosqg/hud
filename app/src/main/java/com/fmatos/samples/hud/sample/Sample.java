package com.fmatos.samples.hud.sample;

import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Timed;

/**
 * Created by fmatos on 28/05/2017.
 */

public class Sample {

    private void useless() {

        BiFunction<? super Timed<Long>, ? super String, ? extends String> zipper = null;

        new DisposableObserver<Timed<Long>>(){

            @Override
            public void onNext(Timed<Long> longTimed) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
}
