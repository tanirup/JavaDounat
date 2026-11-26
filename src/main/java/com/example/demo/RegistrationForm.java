package com.example.demo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record RegistrationForm(
        @NotBlank(message = "ユーザー名を入力してください。")
        @Size(max = 100, message = "ユーザー名は100文字以内で入力してください。") String name,
        @NotBlank(message = "メールアドレスを入力してください。")
        @Email(message = "メールアドレスの形式を確認してください。")
        @Size(max = 254, message = "メールアドレスは254文字以内で入力してください。") String email,
        @NotBlank(message = "パスワードを入力してください。")
        @Size(min = 8, max = 72, message = "パスワードは8〜72文字で入力してください。") String password,
        @NotBlank(message = "確認用パスワードを入力してください。") String passwordConfirm) {

    public RegistrationForm {
        name = name == null ? "" : name.strip();
        email = email == null ? "" : email.strip().toLowerCase(Locale.ROOT);
        password = password == null ? "" : password;
        passwordConfirm = passwordConfirm == null ? "" : passwordConfirm;
    }

    @Override
    public String toString() {
        return "RegistrationForm[password=REDACTED, passwordConfirm=REDACTED]";
    }
}
