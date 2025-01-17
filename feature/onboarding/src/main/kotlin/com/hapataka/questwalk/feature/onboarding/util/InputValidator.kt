package com.hapataka.questwalk.feature.onboarding.util

import android.util.Patterns
import java.util.regex.Pattern

internal fun String.isEmailPattern() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

internal fun String.isPasswordPattern() = Pattern.compile("""^(?=.*[!@#\${'$'}%^&*()_+\-=\[\]{};':"\\|,.<>\/?])[a-zA-Z0-9!@#\${'$'}%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{9,}${'$'}""").matcher(this).matches()