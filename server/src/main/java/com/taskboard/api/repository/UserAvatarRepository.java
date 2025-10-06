package com.taskboard.api.repository;

import com.taskboard.user.model.UserAvatar;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repository для работы с аватарами пользователей. */
@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {

  /** Найти активный аватар пользователя */
  Optional<UserAvatar> findByUserIdAndIsActiveTrue(Long userId);

  /** Найти все аватары пользователя (включая неактивные) */
  List<UserAvatar> findByUserIdOrderByVersionDesc(Long userId);

  /** Найти аватар по storage key */
  Optional<UserAvatar> findByStorageKey(String storageKey);

  /** Найти все активные аватары */
  List<UserAvatar> findByIsActiveTrue();

  /** Найти аватары, загруженные после указанной даты */
  List<UserAvatar> findByUploadedAtAfter(LocalDateTime date);

  /** Найти аватары по размеру файла (больше указанного) */
  List<UserAvatar> findByFileSizeGreaterThan(Long fileSize);

  /** Найти аватары по типу контента */
  List<UserAvatar> findByContentType(String contentType);

  /** Деактивировать все аватары пользователя */
  @Modifying
  @Query("UPDATE UserAvatar ua SET ua.isActive = false WHERE ua.userId = :userId")
  void deactivateAllByUserId(@Param("userId") Long userId);

  /** Найти аватары для удаления (старше указанной даты и неактивные) */
  @Query("SELECT ua FROM UserAvatar ua WHERE ua.uploadedAt < :date AND ua.isActive = false")
  List<UserAvatar> findOldInactiveAvatars(@Param("date") LocalDateTime date);

  /** Получить максимальную версию аватара для пользователя */
  @Query("SELECT COALESCE(MAX(ua.version), 0) FROM UserAvatar ua WHERE ua.userId = :userId")
  Integer getMaxVersionByUserId(@Param("userId") Long userId);

  /** Найти аватары по диапазону дат загрузки */
  @Query("SELECT ua FROM UserAvatar ua WHERE ua.uploadedAt BETWEEN :startDate AND :endDate")
  List<UserAvatar> findByUploadDateRange(
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  /** Подсчитать количество аватаров пользователя */
  long countByUserId(Long userId);

  /** Подсчитать общий размер всех аватаров пользователя */
  @Query("SELECT COALESCE(SUM(ua.fileSize), 0) FROM UserAvatar ua WHERE ua.userId = :userId")
  Long getTotalSizeByUserId(@Param("userId") Long userId);

  /** Найти аватары без CDN URL */
  @Query("SELECT ua FROM UserAvatar ua WHERE ua.cdnUrl IS NULL OR ua.cdnUrl = ''")
  List<UserAvatar> findAvatarsWithoutCdnUrl();

  /** Обновить CDN URL для аватара */
  @Modifying
  @Query("UPDATE UserAvatar ua SET ua.cdnUrl = :cdnUrl WHERE ua.id = :id")
  void updateCdnUrl(@Param("id") Long id, @Param("cdnUrl") String cdnUrl);

  /** Проверить существование активного аватара для пользователя */
  boolean existsByUserIdAndIsActiveTrue(Long userId);
}
