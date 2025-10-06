package com.taskboard.api.config;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationInitializer implements CommandLineRunner {

  @Value("${app.user.default-avatar-url}")
  private String defaultAvatarUrl;

  @Value("${app.user.avatar.generate-default:true}")
  private boolean generateDefault;

  @Override
  public void run(String... args) throws Exception {
    if (generateDefault) {
      generateDefaultAvatarIfNotExists();
    }
  }

  private void generateDefaultAvatarIfNotExists() {
    try {
      // Извлекаем путь к файлу из URL
      String avatarPath = defaultAvatarUrl.replace("/images/", "");
      Path imagesDir = Paths.get("src/main/resources/static/images");
      Path avatarFile = imagesDir.resolve(avatarPath);

      // Создаем директорию если не существует
      if (!Files.exists(imagesDir)) {
        Files.createDirectories(imagesDir);
      }

      // Создаем аватар только если он не существует
      if (!Files.exists(avatarFile)) {
        BufferedImage avatar = createDefaultAvatar();
        ImageIO.write(avatar, "PNG", avatarFile.toFile());
        System.out.println("Дефолтный аватар создан: " + avatarFile.toAbsolutePath());
      }
    } catch (IOException e) {
      System.err.println("Ошибка при создании дефолтного аватара: " + e.getMessage());
    }
  }

  private BufferedImage createDefaultAvatar() {
    int size = 128;
    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();

    // Настройка качества рендеринга
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Градиентный фон
    GradientPaint gradient =
        new GradientPaint(
            0,
            0,
            new Color(59, 130, 246), // Blue
            size,
            size,
            new Color(139, 92, 246) // Purple
            );
    g2d.setPaint(gradient);
    g2d.fillOval(0, 0, size, size);

    // Иконка пользователя
    g2d.setColor(Color.WHITE);
    g2d.setStroke(new BasicStroke(3));

    // Рисуем простую иконку пользователя
    int centerX = size / 2;
    int centerY = size / 2;
    int radius = size / 4;

    // Голова
    g2d.fillOval(centerX - radius, centerY - radius - 10, radius * 2, radius * 2);

    // Тело
    g2d.fillOval(centerX - radius - 5, centerY + radius - 10, radius * 2 + 10, radius * 2);

    g2d.dispose();

    return image;
  }
}
