package com.example.demo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Locale;
import org.springframework.format.annotation.DateTimeFormat;

public class ProfileForm {
    @NotBlank(message = "ユーザー名を入力してください。")
    @Size(max = 100, message = "ユーザー名は100文字以内で入力してください。")
    private String name = "";

    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "メールアドレスの形式を確認してください。")
    @Size(max = 254, message = "メールアドレスは254文字以内で入力してください。")
    private String email = "";

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @PastOrPresent(message = "誕生日には今日以前の日付を入力してください。")
    private LocalDate birthday;

    @Size(max = 20, message = "電話番号は20文字以内で入力してください。")
    @Pattern(regexp = "[0-9+()\\- ]*", message = "電話番号は半角数字・ハイフンなどで入力してください。")
    private String telNo = "";

    public ProfileForm() {}

    public ProfileForm(String name, String email, LocalDate birthday, String telNo) {
        setName(name);
        setEmail(email);
        setBirthday(birthday);
        setTelNo(telNo);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? "" : name.strip(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? "" : email.strip().toLowerCase(Locale.ROOT); }
    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public String getTelNo() { return telNo; }
    public void setTelNo(String telNo) { this.telNo = telNo == null ? "" : telNo.strip(); }
}
