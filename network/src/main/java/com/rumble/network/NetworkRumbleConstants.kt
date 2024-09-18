package com.rumble.network

object NetworkRumbleConstants {
    const val SUCCESS_STRING_RESPONSE = "success"
    const val PASSWORD_UPDATE_SUCCESS_MESSAGE = "Your password has been changed successfully."
    const val EMAIL_UPDATE_SUCCESS_MESSAGE = "Success, please check both your old and new email accounts for further instructions."
    const val UNVERIFIED_EMAIL_UPDATE_SUCCESS_MESSAGE = "Email has been changed"
    const val USER_HAS_ALREADY_VOTED_ON_THIS_CONTENT_ERROR_CODE = 409
    const val USER_KEY = "user"
    const val LOGGED_IN_KEY = "logged_in"
    const val WATCHING_NOW_KEY = "watching_now"
    const val LIVE_PING_ENDPOINT_KEY = "endpoint"
    const val INTERVAL_KEY = "ttl_post"
    const val RETRY_NUMBER = 5
    const val RETRY_DELAY = 500L
    const val COOKIES_HEADER = "Cookie"
    const val CHAT_KEY = "chat"
    const val ENDPOINT_KEY = "endpoint"
    const val DEBUG_KEY = "debug"
    const val CAN_SUBMIT_LOGS_KEY = "can_submit_logs"
    const val FIRST_VIDEO_START_REPORT_ERROR = 1001
    const val FETCH_CONFIG_INTERVAL_MINUTES = 30L
    const val ACCEPT_HEADER = "Accept"
    const val USER_AGENT = "User-Agent"
    const val APP_VERSION = "app_version"
    const val APP_REQUEST_NAME = "app_name"
    const val OS_VERSION = "os_version"
    const val API = "api"
    const val ADS_DEFAULT_COUNT = 5
    const val VIDEO_UPLOAD_CHUNK_SIZE = 5 * 1000 * 1000
    const val TIME_RANGE_UPLOAD_PERIOD = 60_000L
    const val WATCH_TIME_KEY = "watch_time"
    const val TIME_RANGE_INTERVAL_KEY = "interval"
    const val TIME_RANGE_REPORT_PATH = "-api/watch-time"
    const val PLAYLIST_ID = "playlist_id"
    const val ADS_DEBUG_URL = "https://object.us-east-1.rumble.cloud/3201adf86fca4a0db8affdeec1a3c170:vast/test_ad"
    const val EVENT_URL_KEY = "e"
    const val URL_KEY = "url"
    const val WATCH_PROGRESS_INTERVAL_KEY = "watch_progress_interval"
    const val TOO_MANY_REQUESTS = 429
    const val DEV_EVENT_SUBDOMAIN = "e17"
    const val PROD_EVENT_SUBDOMAIN = "e"
    const val CONTENT_TYPE = "Content-Type"
    const val JSON_CONTENT_VALUE = "application/json"
    const val URL_ENCODED_VALUE = "application/x-www-form-urlencoded"
}