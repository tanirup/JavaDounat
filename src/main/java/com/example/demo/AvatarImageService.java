package com.example.demo;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AvatarImageService {
    public static final long MAX_BYTES = 2 * 1024 * 1024;

    public byte[] normalize(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("画像を選択してください。");
        if (file.getSize() > MAX_BYTES) throw new IllegalArgumentException("保存する画像は2MB以内にしてください。");
        // 拡張子やContent-Typeを信用せず、実際のPNGデータを検証する。
        try (var input = ImageIO.createImageInputStream(new ByteArrayInputStream(file.getBytes()))) {
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IllegalArgumentException("画像を読み込めません。別の画像を選んでください。");
            var reader = readers.next();
            try {
                if (!"png".equalsIgnoreCase(reader.getFormatName())) {
                    throw new IllegalArgumentException("調整画面で画像を保存し直してください。");
                }
                reader.setInput(input, true, true);
                int width = reader.getWidth(0), height = reader.getHeight(0);
                if (width < 1 || height < 1 || width > 4096 || height > 4096) {
                    throw new IllegalArgumentException("画像のサイズが大きすぎます。別の画像を選んでください。");
                }
                var source = reader.read(0);
                var normalized = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
                var graphics = normalized.createGraphics();
                try {
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    int edge = Math.min(width, height), x = (width - edge) / 2, y = (height - edge) / 2;
                    graphics.drawImage(source, 0, 0, 512, 512, x, y, x + edge, y + edge, null);
                } finally {
                    graphics.dispose();
                }
                var output = new ByteArrayOutputStream();
                ImageIO.write(normalized, "png", output);
                return output.toByteArray();
            } finally {
                reader.dispose();
            }
        }
    }
}
