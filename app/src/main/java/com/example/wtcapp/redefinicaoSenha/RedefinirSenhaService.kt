
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String,
    val confirmPassword: String
)