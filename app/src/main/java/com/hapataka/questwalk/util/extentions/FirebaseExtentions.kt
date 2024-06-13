package com.hapataka.questwalk.util.extentions

import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException

fun Throwable.getErrorMessage(): String {
    fun getErrorMsgFromCode(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "이메일 주소가 유효하지 않습니다."
            "ERROR_USER_NOT_FOUND" -> "계정을 찾을 수 없습니다. 가입되지 않은 이메일입니다."
            "ERROR_WRONG_PASSWORD" -> "비밀번호가 틀렸습니다. 다시 확인해주세요."
            "ERROR_USER_DISABLED" -> "이 계정은 비활성화되었습니다. 관리자에게 문의해주세요."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "이미 다른 인증 방법으로 등록된 이메일입니다."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "이 이메일은 이미 사용 중입니다. 다른 이메일을 사용해주세요."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "이 인증 정보는 이미 다른 계정에서 사용 중입니다."
            "ERROR_OPERATION_NOT_ALLOWED" -> "이메일 및 비밀번호 로그인이 활성화되지 않았습니다."
            "ERROR_TOO_MANY_REQUESTS" -> "요청이 너무 많습니다. 나중에 다시 시도해주세요."
            "ERROR_INVALID_CREDENTIAL" -> "아이디 또는 비밀번호를 확인해주세요."
            else -> "로그인 실패: 알 수 없는 오류가 발생했습니다. 다시 시도해주세요."
        }
    }

    return when (this) {
        is FirebaseAuthException -> getErrorMsgFromCode(errorCode)
        is FirebaseTooManyRequestsException -> "요청이 너무 많습니다. 나중에 다시 시도해주세요."
        else -> "알 수 없는 오류가 발생했습니다."
    }
}