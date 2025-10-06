package com.taskboard.api.constants;

/**
 * Константы для ключей сообщений в системе. Используются для интернационализации и избежания
 * магических строк.
 */
public final class MessageKeys {

  // Приватный конструктор для утилитарного класса
  private MessageKeys() {
    throw new UnsupportedOperationException("Utility class");
  }

  // Аутентификация
  public static final String AUTH_LOGIN_SUCCESS = "auth.login.success";
  public static final String AUTH_LOGIN_FAILED = "auth.login.failed";
  public static final String AUTH_REGISTER_SUCCESS = "auth.register.success";
  public static final String AUTH_REGISTER_FAILED = "auth.register.failed";
  public static final String AUTH_LOGOUT_SUCCESS = "auth.logout.success";
  public static final String AUTH_INVALID_CREDENTIALS = "auth.error.invalid.credentials";
  public static final String AUTH_USER_NOT_FOUND = "auth.error.user.not.found";
  public static final String AUTH_ACCOUNT_DISABLED = "auth.error.account.disabled";
  public static final String AUTH_ACCOUNT_LOCKED = "auth.error.account.locked";
  public static final String AUTH_CREDENTIALS_EXPIRED = "auth.error.credentials.expired";

  // Валидация
  public static final String VALIDATION_REQUEST_INVALID = "validation.request.invalid";
  public static final String VALIDATION_USERNAME_REQUIRED = "validation.username.required";
  public static final String VALIDATION_USERNAME_SIZE = "validation.username.size";
  public static final String VALIDATION_EMAIL_REQUIRED = "validation.email.required";
  public static final String VALIDATION_EMAIL_INVALID = "validation.email.invalid";
  public static final String VALIDATION_PASSWORD_REQUIRED = "validation.password.required";
  public static final String VALIDATION_PASSWORD_SIZE = "validation.password.size";
  public static final String VALIDATION_PASSWORD_CONFIRM_REQUIRED =
      "validation.password.confirm.required";
  public static final String VALIDATION_PASSWORD_MISMATCH = "validation.password.mismatch";

  // Пользователи
  public static final String USER_NOT_FOUND = "user.error.not.found";
  public static final String USER_ALREADY_EXISTS = "user.error.already.exists";
  public static final String USER_EMAIL_EXISTS = "user.error.email.exists";
  public static final String USER_USERNAME_EXISTS = "user.error.username.exists";
  public static final String USER_PROFILE_UPDATED = "user.profile.updated";
  public static final String USER_PROFILE_UPDATE_FAILED = "user.profile.update.failed";
  public static final String USER_PASSWORD_CHANGED = "user.password.changed";
  public static final String USER_PASSWORD_CHANGE_FAILED = "user.password.change.failed";
  public static final String USER_AVATAR_UPDATED = "user.avatar.updated";
  public static final String USER_AVATAR_UPDATE_FAILED = "user.avatar.update.failed";
  public static final String USER_AVATAR_DELETED = "user.avatar.deleted";

  // Задачи
  public static final String TASK_NOT_FOUND = "task.error.not.found";
  public static final String TASK_CREATED = "task.created";
  public static final String TASK_UPDATED = "task.updated";
  public static final String TASK_DELETED = "task.deleted";
  public static final String TASK_ACCESS_DENIED = "task.error.access.denied";

  // Проекты
  public static final String PROJECT_NOT_FOUND = "project.error.not.found";
  public static final String PROJECT_CREATED = "project.created";
  public static final String PROJECT_UPDATED = "project.updated";
  public static final String PROJECT_DELETED = "project.deleted";
  public static final String PROJECT_ACCESS_DENIED = "project.error.access.denied";

  // Файлы и загрузки
  public static final String FILE_UPLOAD_SUCCESS = "file.upload.success";
  public static final String FILE_UPLOAD_FAILED = "file.upload.failed";
  public static final String FILE_SIZE_EXCEEDED = "file.error.size.exceeded";
  public static final String FILE_TYPE_NOT_ALLOWED = "file.error.type.not.allowed";
  public static final String FILE_NOT_FOUND = "file.error.not.found";
  public static final String FILE_DELETE_SUCCESS = "file.delete.success";
  public static final String FILE_DELETE_FAILED = "file.delete.failed";

  // Общие ошибки
  public static final String ERROR_INTERNAL_SERVER = "error.internal.server";
  public static final String ERROR_UNAUTHORIZED = "error.unauthorized";
  public static final String ERROR_FORBIDDEN = "error.forbidden";
  public static final String ERROR_NOT_FOUND = "error.not.found";
  public static final String ERROR_BAD_REQUEST = "error.bad.request";
  public static final String ERROR_VALIDATION_FAILED = "error.validation.failed";

  // Информационные сообщения
  public static final String INFO_TEST_PUBLIC = "info.test.public";
  public static final String INFO_HEALTH_CHECK = "info.health.check";
  public static final String INFO_SYSTEM_READY = "info.system.ready";

  // WebAuthn
  public static final String WEBAUTHN_REGISTRATION_STARTED = "webauthn.registration.started";
  public static final String WEBAUTHN_REGISTRATION_COMPLETED = "webauthn.registration.completed";
  public static final String WEBAUTHN_REGISTRATION_FAILED = "webauthn.registration.failed";
  public static final String WEBAUTHN_AUTHENTICATION_SUCCESS = "webauthn.authentication.success";
  public static final String WEBAUTHN_AUTHENTICATION_FAILED = "webauthn.authentication.failed";
  public static final String WEBAUTHN_CREDENTIAL_DELETED = "webauthn.credential.deleted";

  // One-Time Tokens
  public static final String OTT_GENERATED = "ott.generated";
  public static final String OTT_VALIDATED = "ott.validated";
  public static final String OTT_EXPIRED = "ott.expired";
  public static final String OTT_INVALID = "ott.invalid";
  public static final String OTT_ALREADY_USED = "ott.already.used";

  // Уведомления
  public static final String NOTIFICATION_SENT = "notification.sent";
  public static final String NOTIFICATION_SEND_FAILED = "notification.send.failed";
  public static final String NOTIFICATION_MARKED_READ = "notification.marked.read";
  public static final String NOTIFICATION_DELETED = "notification.deleted";
}
