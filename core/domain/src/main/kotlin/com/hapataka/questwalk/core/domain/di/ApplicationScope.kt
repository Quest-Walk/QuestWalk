package com.hapataka.questwalk.core.domain.di

import javax.inject.Qualifier

/** 화면 수명과 무관하게 살아야 하는 작업에 쓰는 코루틴 스코프. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
