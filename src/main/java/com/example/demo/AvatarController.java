package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AvatarController {
    private final AccountRepository accounts;
    private final AvatarImageService images;

    public AvatarController(AccountRepository accounts, AvatarImageService images) {
        this.accounts = accounts;
        this.images = images;
    }

    @GetMapping("/profile/avatar")
    public ResponseEntity<byte[]> avatar(Principal principal) {
        return accounts.findAvatar(principal.getName())
                .map(png -> ResponseEntity.ok().cacheControl(CacheControl.noStore())
                        .contentType(MediaType.IMAGE_PNG).body(png))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AvatarResult upload(@RequestParam(value = "avatar", required = false) MultipartFile avatar,
                               Principal principal, HttpServletRequest request) throws IOException {
        if (!accounts.updateAvatar(principal.getName(), images.normalize(avatar))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        long version = accounts.findProfile(principal.getName()).orElseThrow().avatarVersion();
        return new AvatarResult("アイコンを保存しました。", request.getContextPath() + "/profile/avatar?v=" + version);
    }

    @PostMapping("/profile/avatar/delete")
    public AvatarResult remove(Principal principal) {
        if (!accounts.updateAvatar(principal.getName(), null)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return new AvatarResult("名前のアイコンに戻しました。", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, String>> invalidImage(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    ResponseEntity<Map<String, String>> unreadableImage() {
        return ResponseEntity.badRequest().body(Map.of("message", "画像を読み込めません。別の画像を選んでください。"));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<Map<String, String>> oversizedImage() {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of("message", "保存する画像は2MB以内にしてください。"));
    }

    public record AvatarResult(String message, String avatarUrl) {}
}
