package com.rumble.utils

import org.intellij.lang.annotations.Language
import java.util.concurrent.TimeUnit

object RumbleConstants {
    const val SCREEN_OFF_DELAY = 100L
    const val SPLASH_DELAY = 1000L
    const val RETRY_DELAY_USER_UPLOAD_CHANNELS = 3000L

    const val PAGINATION_MAX_ITEMS_PER_REQUEST = 100
    const val PAGINATION_PAGE_SIZE = 50
    const val PAGINATION_VIDEO_PAGE_SIZE = 24
    const val PAGINATION_VIDEO_PAGE_SIZE_TABLET_TV = 72
    const val PAGINATION_VIDEO_PAGE_SIZE_PLAYLIST_VIDEO_DETAILS = 100

    const val MINIMUM_PASSWORD_LENGTH = 8
    const val MINIMUM_AGE_REQUIREMENT = 13
    const val MAX_LINES_TITLE_REGULAR_VIDEO_CARD = 2
    const val VIDEO_CARD_THUMBNAIL_ASPECT_RATION = 16f / 9f
    const val RUMBLE_AD_CARD_ASPECT_RATION = 336f / 280f
    const val FACEBOOK_REGISTRATION_TO_FACEBOOK_LOGIN_DELAY = 2000L
    const val FACEBOOK_REGISTRATION_EMAIL_REQUEST_FIELD = "email"
    const val RUMBLE_CAMERA_REDIRECT_URL = "https://rumble.com/upload.php"
    const val RUMBLE_ANALYTICS_TAG = "com.rumble.analytics"
    const val EMAIL_VALIDATION_REGEX = "[A-Z0-9a-z._+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
    const val URL_PATTERN_REGEX = "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)"
    const val API_FORMAT_DATE_PATTERN = "yyyy-MM-dd"
    const val UPLOAD_DATE_PATTERN = "dd LLLL yyyy"
    const val UPLOAD_TIME_PATTERN = "KK:mm aaa"
    const val BIRTHDAY_DATE_PATTERN = "dd.MM.yyyy"
    const val VIDEO_FILE_NAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    const val VIDEO_UPLOAD_PREVIEW_THUMBNAILS_QUANTITY = 5
    const val RUMBLE_VIDEO_MEDIAFILE_PREFIX = "Rumble-recording-"
    const val RUMBLE_VIDEO_MEDIASTORE_FILE_LOCATION = "Movies/Rumble Video"
    const val RUMBLE_VIDEO_MYME_TYPE = "video/mp4"
    const val RUMBLE_VIDEO_EXTENSION = "mp4"
    const val RUMBLE_MINIMUM_VIDEO_TRIM_LENGTH_MILLIS = 1000
    const val GALLERY_ROWS_QUANTITY = 3
    const val MAIN_CATEGORY_ITEMS_IN_ROW_QUANTITY = 2
    const val SUBCATEGORY_ROWS_QUANTITY = 3
    const val GALLERY_THUMB_SIZE = 600
    const val ACTIVITY_RESULT_CONTRACT_IMAGE_INPUT_TYPE = "image/*"
    const val TAG_URL = "URL"
    const val TAG_EMAIL = "EMAIL"
    const val LIMIT_TO_ONE = 1
    const val LIMIT_TO_THREE = 3
    const val LIMIT_TO_FIVE = 5
    const val RANT_STATE_UPDATE_RATIO = 1_000L
    const val PLAYER_STATE_UPDATE_RATIO = 2_00L
    const val FIRST_AD_VIDEO_INDEX = 1
    const val AD_STEP = 5
    const val VIEWER_ID_LENGTH = 8
    const val PLAYER_LIVE_PING = 45_000L
    const val PROFILE_IMAGE_CLICK_TIMES = 5
    const val MAX_CHARACTERS_UPLOAD_TITLE = 100
    const val MAX_CHARACTERS_UPLOAD_DESCRIPTION = 10000
    const val MAX_CHARACTERS_PLAYLIST_TITLE = 255
    const val MAX_CHARACTERS_PLAYLIST_DESCRIPTION = 10000
    const val LIVE_MESSAGE_MAX_CHARACTERS = 200
    const val LOCK_EMOTE_CODE = "\uD83D\uDD12"
    const val EMOTE_PATTERN = ":(r\\+){0,1}[A-Za-z0-9]+:"
    const val GIF_SUFFIX = ".gif"
    val EMOTE_BADGES = listOf("admin", "recurring_subscription")
    const val BADGE_RECURRING_SUBSCRIPTION = "recurring_subscription"
    val VIEWER_CHAR_POOL = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '_' + '.' + '-'
    const val LOGS_DIR_NAME = "/logs"
    const val LOG_PREFIX = "rumble_log"
    const val LOG_FILE_SIZE = "1MB"
    const val LOG_ENCODER_PATTERN = "%date %level [%thread] %msg%n"
    const val PLAYER_MIN_VISIBILITY = 0.7f
    const val LIVE_CATEGORIES_LIMIT = 10
    const val PROFILE_IMAGE_BITMAP_MAX_WIDTH = 500
    const val CATEGORY_CARD_ASPECT_RATIO = 10f / 16f
    const val CATEGORY_THRESHOLDER = 100
    const val MAX_LOG_FILES_INDEX = 15
    val WATCHED_TIME_INTERVAL = TimeUnit.MINUTES.toMillis(5)
    const val LIVE_CHAT_ANIMATION_DURATION = 500
    const val LIBRARY_SHORT_LIST_SIZE = 4
    const val MAX_UNREAD_MESSAGE = 99
    const val TV_LIBRARY_SCREEN_GRID_WIDTH = 3
    const val TV_RECOMMENDED_CHANNELS_GRID_WIDTH = 4
    const val HTTP_CONFLICT = 409
    const val DEFAULT_VIDEO_DETAILS_NAV_PATH_NON_PLAYLIST = -1
    const val SEARCH_INITIAL_MAX_LENGTH = 100
    const val SEARCH_UPDATE_DELAY_MS = 500L
    const val SUPPORT_EMAIL = "support@rumble.com"
    const val VERSION_CODE_LIBRARY_FEATURE = 400
    const val LIVE_TIME_UPDATE = 1_000L
    const val TV_SEARCH_DEBOUNCE_TIME_MS = 250L
    const val TV_ALERT_DIALOG_DISPLAY_TIME: Long = 3000L
    const val WATCHED_TIME_OFFSET = 5
    const val LIVE_CHAT_MAX_MESSAGE_COUNT = 500
    const val PREMIUM_VIDEO_AVAILABILITY = "premium"
    const val LOGIN_PROMPT_PERIOD = 30L
    const val LOGIN_PROMPT_PERIOD_KEY = "auth_display_delay_days"
    const val TESTING_LAUNCH_UIT_FLAG = "uit"
    const val TESTING_LAUNCH_UIT_USERNAME = "uit_username"
    const val TESTING_LAUNCH_UIT_PASSWORD = "uit_password"
    const val TESTING_SUBDOMAIN = "webe27"
    const val TV_MAIN_MENU_FOCUS_DELAY_TIME = 300L

    @Language("JSON")
    val countriesList = """
      [
        {
        "countryID": 1,
        "countryAbbrev": "US",
        "countryName": "United States",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 148
        },
        {
        "countryID": 244,
        "countryAbbrev": "AF",
        "countryName": "Afghanistan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 35
        },
        {
        "countryID": 15,
        "countryAbbrev": "AX",
        "countryName": "Aland Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 6,
        "countryAbbrev": "AL",
        "countryName": "Albania",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 68
        },
        {
        "countryID": 59,
        "countryAbbrev": "DZ",
        "countryName": "Algeria",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 95
        },
        {
        "countryID": 12,
        "countryAbbrev": "AS",
        "countryName": "American Samoa",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 242,
        "countryAbbrev": "AD",
        "countryName": "Andorra",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 9,
        "countryAbbrev": "AO",
        "countryName": "Angola",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 22
        },
        {
        "countryID": 246,
        "countryAbbrev": "AI",
        "countryName": "Anguilla",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 10,
        "countryAbbrev": "AQ",
        "countryName": "Antarctica",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 245,
        "countryAbbrev": "AG",
        "countryName": "Antigua and Barbuda",
        "countryAliasName": "Antigua",
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 11,
        "countryAbbrev": "AR",
        "countryName": "Argentina",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 92
        },
        {
        "countryID": 7,
        "countryAbbrev": "AM",
        "countryName": "Armenia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 37
        },
        {
        "countryID": 14,
        "countryAbbrev": "AW",
        "countryName": "Aruba",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 3,
        "countryAbbrev": "AU",
        "countryName": "Australia",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 135
        },
        {
        "countryID": 13,
        "countryAbbrev": "AT",
        "countryName": "Austria",
        "countryAliasName": null,
        "countryEligible": 1,
        "countryOrder": 72
        },
        {
        "countryID": 16,
        "countryAbbrev": "AZ",
        "countryName": "Azerbaijan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 31
        },
        {
        "countryID": 30,
        "countryAbbrev": "BS",
        "countryName": "Bahamas",
        "countryAliasName": "The Bahamas",
        "countryEligible": -1,
        "countryOrder": 44
        },
        {
        "countryID": 23,
        "countryAbbrev": "BH",
        "countryName": "Bahrain",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 96
        },
        {
        "countryID": 19,
        "countryAbbrev": "BD",
        "countryName": "Bangladesh",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 110
        },
        {
        "countryID": 18,
        "countryAbbrev": "BB",
        "countryName": "Barbados",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 34,
        "countryAbbrev": "BY",
        "countryName": "Belarus",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 76
        },
        {
        "countryID": 20,
        "countryAbbrev": "BE",
        "countryName": "Belgium",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 80
        },
        {
        "countryID": 35,
        "countryAbbrev": "BZ",
        "countryName": "Belize",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 25,
        "countryAbbrev": "BJ",
        "countryName": "Benin",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 18
        },
        {
        "countryID": 26,
        "countryAbbrev": "BM",
        "countryName": "Bermuda",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 31,
        "countryAbbrev": "BT",
        "countryName": "Bhutan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 11
        },
        {
        "countryID": 28,
        "countryAbbrev": "BO",
        "countryName": "Bolivia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 250,
        "countryAbbrev": "BQ",
        "countryName": "Bonaire, Sint Eustatius and Saba",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 17,
        "countryAbbrev": "BA",
        "countryName": "Bosnia and Herzegovina",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 144
        },
        {
        "countryID": 33,
        "countryAbbrev": "BW",
        "countryName": "Botswana",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 41
        },
        {
        "countryID": 32,
        "countryAbbrev": "BV",
        "countryName": "Bouvet Island",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 29,
        "countryAbbrev": "BR",
        "countryName": "Brazil",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 90
        },
        {
        "countryID": 102,
        "countryAbbrev": "IO",
        "countryName": "British Indian Ocean Territory",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 229,
        "countryAbbrev": "VG",
        "countryName": "British Virgin Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 27,
        "countryAbbrev": "BN",
        "countryName": "Brunei Darussalam",
        "countryAliasName": "Brunei",
        "countryEligible": -1,
        "countryOrder": 2
        },
        {
        "countryID": 22,
        "countryAbbrev": "BG",
        "countryName": "Bulgaria",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 147
        },
        {
        "countryID": 21,
        "countryAbbrev": "BF",
        "countryName": "Burkina Faso",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 42
        },
        {
        "countryID": 24,
        "countryAbbrev": "BI",
        "countryName": "Burundi",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 111,
        "countryAbbrev": "KH",
        "countryName": "Cambodia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 33
        },
        {
        "countryID": 44,
        "countryAbbrev": "CM",
        "countryName": "Cameroon",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 13
        },
        {
        "countryID": 2,
        "countryAbbrev": "CA",
        "countryName": "Canada",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 146
        },
        {
        "countryID": 50,
        "countryAbbrev": "CV",
        "countryName": "Cape Verde",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 118,
        "countryAbbrev": "KY",
        "countryName": "Cayman Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 15
        },
        {
        "countryID": 38,
        "countryAbbrev": "CF",
        "countryName": "Central African Republic",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 205,
        "countryAbbrev": "TD",
        "countryName": "Chad",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 43,
        "countryAbbrev": "CL",
        "countryName": "Chile",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 16
        },
        {
        "countryID": 45,
        "countryAbbrev": "CN",
        "countryName": "China",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 100
        },
        {
        "countryID": 51,
        "countryAbbrev": "CX",
        "countryName": "Christmas Island",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 36,
        "countryAbbrev": "CC",
        "countryName": "Cocos (Keeling) Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 46,
        "countryAbbrev": "CO",
        "countryName": "Colombia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 94
        },
        {
        "countryID": 113,
        "countryAbbrev": "KM",
        "countryName": "Comoros",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 39,
        "countryAbbrev": "CG",
        "countryName": "Congo",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 42,
        "countryAbbrev": "CK",
        "countryName": "Cook Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 47,
        "countryAbbrev": "CR",
        "countryName": "Costa Rica",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 20
        },
        {
        "countryID": 41,
        "countryAbbrev": "CI",
        "countryName": "Côte d'Ivoire",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 3
        },
        {
        "countryID": 95,
        "countryAbbrev": "HR",
        "countryName": "Croatia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 138
        },
        {
        "countryID": 49,
        "countryAbbrev": "CU",
        "countryName": "Cuba",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 253,
        "countryAbbrev": "CW",
        "countryName": "Curaçao",
        "countryAliasName": "curacao",
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 52,
        "countryAbbrev": "CY",
        "countryName": "Cyprus",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 82
        },
        {
        "countryID": 53,
        "countryAbbrev": "CZ",
        "countryName": "Czech Republic",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 91
        },
        {
        "countryID": 37,
        "countryAbbrev": "CD",
        "countryName": "Democratic Republic of the Congo",
        "countryAliasName": "Republic of the Congo",
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 56,
        "countryAbbrev": "DK",
        "countryName": "Denmark",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 118
        },
        {
        "countryID": 55,
        "countryAbbrev": "DJ",
        "countryName": "Djibouti",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 38
        },
        {
        "countryID": 57,
        "countryAbbrev": "DM",
        "countryName": "Dominica",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 58,
        "countryAbbrev": "DO",
        "countryName": "Dominican Republic",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 53
        },
        {
        "countryID": 215,
        "countryAbbrev": "TP",
        "countryName": "East Timor",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 60,
        "countryAbbrev": "EC",
        "countryName": "Ecuador",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 52
        },
        {
        "countryID": 62,
        "countryAbbrev": "EG",
        "countryName": "Egypt",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 101
        },
        {
        "countryID": 201,
        "countryAbbrev": "SV",
        "countryName": "El Salvador",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 34
        },
        {
        "countryID": 85,
        "countryAbbrev": "GQ",
        "countryName": "Equatorial Guinea",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 64,
        "countryAbbrev": "ER",
        "countryName": "Eritrea",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 61,
        "countryAbbrev": "EE",
        "countryName": "Estonia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 98
        },
        {
        "countryID": 66,
        "countryAbbrev": "ET",
        "countryName": "Ethiopia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 30
        },
        {
        "countryID": 69,
        "countryAbbrev": "FK",
        "countryName": "Falkland Islands (Malvinas)",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 71,
        "countryAbbrev": "FO",
        "countryName": "Faroe Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 70,
        "countryAbbrev": "FM",
        "countryName": "Federated States of Micronesia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 68,
        "countryAbbrev": "FJ",
        "countryName": "Fiji",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 17
        },
        {
        "countryID": 67,
        "countryAbbrev": "FI",
        "countryName": "Finland",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 128
        },
        {
        "countryID": 72,
        "countryAbbrev": "FR",
        "countryName": "France",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 123
        },
        {
        "countryID": 73,
        "countryAbbrev": "FX",
        "countryName": "France, Metropolitan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 78,
        "countryAbbrev": "GF",
        "countryName": "French Guiana",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 167,
        "countryAbbrev": "PF",
        "countryName": "French Polynesia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 24
        },
        {
        "countryID": 206,
        "countryAbbrev": "TF",
        "countryName": "French Southern Territories",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 74,
        "countryAbbrev": "GA",
        "countryName": "Gabon",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 82,
        "countryAbbrev": "GM",
        "countryName": "Gambia",
        "countryAliasName": "The Gambia",
        "countryEligible": -1,
        "countryOrder": 14
        },
        {
        "countryID": 77,
        "countryAbbrev": "GE",
        "countryName": "Georgia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 49
        },
        {
        "countryID": 54,
        "countryAbbrev": "DE",
        "countryName": "Germany",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 136
        },
        {
        "countryID": 79,
        "countryAbbrev": "GH",
        "countryName": "Ghana",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 40
        },
        {
        "countryID": 80,
        "countryAbbrev": "GI",
        "countryName": "Gibraltar",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 75,
        "countryAbbrev": "GB",
        "countryName": "Great Britain (UK)",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 141
        },
        {
        "countryID": 86,
        "countryAbbrev": "GR",
        "countryName": "Greece",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 129
        },
        {
        "countryID": 81,
        "countryAbbrev": "GL",
        "countryName": "Greenland",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 76,
        "countryAbbrev": "GD",
        "countryName": "Grenada",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 84,
        "countryAbbrev": "GP",
        "countryName": "Guadeloupe",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 89,
        "countryAbbrev": "GU",
        "countryName": "Guam",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 48
        },
        {
        "countryID": 88,
        "countryAbbrev": "GT",
        "countryName": "Guatemala",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 248,
        "countryAbbrev": "GG",
        "countryName": "Guernsey",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 83,
        "countryAbbrev": "GN",
        "countryName": "Guinea",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 90,
        "countryAbbrev": "GW",
        "countryName": "Guinea-Bissau",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 91,
        "countryAbbrev": "GY",
        "countryName": "Guyana",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 96,
        "countryAbbrev": "HT",
        "countryName": "Haiti",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 93,
        "countryAbbrev": "HM",
        "countryName": "Heard Island and McDonald Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 94,
        "countryAbbrev": "HN",
        "countryName": "Honduras",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 92,
        "countryAbbrev": "HK",
        "countryName": "Hong Kong",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 88
        },
        {
        "countryID": 97,
        "countryAbbrev": "HU",
        "countryName": "Hungary",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 83
        },
        {
        "countryID": 105,
        "countryAbbrev": "IS",
        "countryName": "Iceland",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 93
        },
        {
        "countryID": 101,
        "countryAbbrev": "IN",
        "countryName": "India",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 143
        },
        {
        "countryID": 98,
        "countryAbbrev": "ID",
        "countryName": "Indonesia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 133
        },
        {
        "countryID": 104,
        "countryAbbrev": "IR",
        "countryName": "Iran",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 103,
        "countryAbbrev": "IQ",
        "countryName": "Iraq",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 58
        },
        {
        "countryID": 99,
        "countryAbbrev": "IE",
        "countryName": "Ireland",
        "countryAliasName": null,
        "countryEligible": 1,
        "countryOrder": 89
        },
        {
        "countryID": 252,
        "countryAbbrev": "IM",
        "countryName": "Isle Of Man",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 100,
        "countryAbbrev": "IL",
        "countryName": "Israel",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 84
        },
        {
        "countryID": 106,
        "countryAbbrev": "IT",
        "countryName": "Italy",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 139
        },
        {
        "countryID": 107,
        "countryAbbrev": "JM",
        "countryName": "Jamaica",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 108
        },
        {
        "countryID": 5,
        "countryAbbrev": "JP",
        "countryName": "Japan",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 120
        },
        {
        "countryID": 249,
        "countryAbbrev": "JE",
        "countryName": "Jersey",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 108,
        "countryAbbrev": "JO",
        "countryName": "Jordan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 56
        },
        {
        "countryID": 119,
        "countryAbbrev": "KZ",
        "countryName": "Kazakhstan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 45
        },
        {
        "countryID": 109,
        "countryAbbrev": "KE",
        "countryName": "Kenya",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 78
        },
        {
        "countryID": 112,
        "countryAbbrev": "KI",
        "countryName": "Kiribati",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 115,
        "countryAbbrev": "KP",
        "countryName": "Korea (North)",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 116,
        "countryAbbrev": "KR",
        "countryName": "Korea (South)",
        "countryAliasName": "South Korea",
        "countryEligible": -1,
        "countryOrder": 87
        },
        {
        "countryID": 117,
        "countryAbbrev": "KW",
        "countryName": "Kuwait",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 65
        },
        {
        "countryID": 110,
        "countryAbbrev": "KG",
        "countryName": "Kyrgyzstan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 8
        },
        {
        "countryID": 120,
        "countryAbbrev": "LA",
        "countryName": "Laos",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 129,
        "countryAbbrev": "LV",
        "countryName": "Latvia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 130
        },
        {
        "countryID": 121,
        "countryAbbrev": "LB",
        "countryName": "Lebanon",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 85
        },
        {
        "countryID": 126,
        "countryAbbrev": "LS",
        "countryName": "Lesotho",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 125,
        "countryAbbrev": "LR",
        "countryName": "Liberia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 130,
        "countryAbbrev": "LY",
        "countryName": "Libya",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 39
        },
        {
        "countryID": 123,
        "countryAbbrev": "LI",
        "countryName": "Liechtenstein",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 127,
        "countryAbbrev": "LT",
        "countryName": "Lithuania",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 116
        },
        {
        "countryID": 128,
        "countryAbbrev": "LU",
        "countryName": "Luxembourg",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 23
        },
        {
        "countryID": 140,
        "countryAbbrev": "MO",
        "countryName": "Macao",
        "countryAliasName": "Macau",
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 136,
        "countryAbbrev": "MK",
        "countryName": "Macedonia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 111
        },
        {
        "countryID": 134,
        "countryAbbrev": "MG",
        "countryName": "Madagascar",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 148,
        "countryAbbrev": "MW",
        "countryName": "Malawi",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 63
        },
        {
        "countryID": 150,
        "countryAbbrev": "MY",
        "countryName": "Malaysia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 114
        },
        {
        "countryID": 147,
        "countryAbbrev": "MV",
        "countryName": "Maldives",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 57
        },
        {
        "countryID": 137,
        "countryAbbrev": "ML",
        "countryName": "Mali",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 19
        },
        {
        "countryID": 145,
        "countryAbbrev": "MT",
        "countryName": "Malta",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 61
        },
        {
        "countryID": 135,
        "countryAbbrev": "MH",
        "countryName": "Marshall Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 142,
        "countryAbbrev": "MQ",
        "countryName": "Martinique",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 143,
        "countryAbbrev": "MR",
        "countryName": "Mauritania",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 26
        },
        {
        "countryID": 146,
        "countryAbbrev": "MU",
        "countryName": "Mauritius",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 50
        },
        {
        "countryID": 236,
        "countryAbbrev": "YT",
        "countryName": "Mayotte",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 149,
        "countryAbbrev": "MX",
        "countryName": "Mexico",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 122
        },
        {
        "countryID": 133,
        "countryAbbrev": "MD",
        "countryName": "Moldova",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 66
        },
        {
        "countryID": 132,
        "countryAbbrev": "MC",
        "countryName": "Monaco",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 139,
        "countryAbbrev": "MN",
        "countryName": "Mongolia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 46
        },
        {
        "countryID": 251,
        "countryAbbrev": "ME",
        "countryName": "Montenegro",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 144,
        "countryAbbrev": "MS",
        "countryName": "Montserrat",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 131,
        "countryAbbrev": "MA",
        "countryName": "Morocco",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 104
        },
        {
        "countryID": 151,
        "countryAbbrev": "MZ",
        "countryName": "Mozambique",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 73
        },
        {
        "countryID": 138,
        "countryAbbrev": "MM",
        "countryName": "Myanmar",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 32
        },
        {
        "countryID": 152,
        "countryAbbrev": "NA",
        "countryName": "Namibia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 4
        },
        {
        "countryID": 161,
        "countryAbbrev": "NR",
        "countryName": "Nauru",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 160,
        "countryAbbrev": "NP",
        "countryName": "Nepal",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 142
        },
        {
        "countryID": 158,
        "countryAbbrev": "NL",
        "countryName": "Netherlands",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 117
        },
        {
        "countryID": 8,
        "countryAbbrev": "AN",
        "countryName": "Netherlands Antilles",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 153,
        "countryAbbrev": "NC",
        "countryName": "New Caledonia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 163,
        "countryAbbrev": "NZ",
        "countryName": "New Zealand",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 103
        },
        {
        "countryID": 157,
        "countryAbbrev": "NI",
        "countryName": "Nicaragua",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 1
        },
        {
        "countryID": 154,
        "countryAbbrev": "NE",
        "countryName": "Niger",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 156,
        "countryAbbrev": "NG",
        "countryName": "Nigeria",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 99
        },
        {
        "countryID": 162,
        "countryAbbrev": "NU",
        "countryName": "Niue",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 155,
        "countryAbbrev": "NF",
        "countryName": "Norfolk Island",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 141,
        "countryAbbrev": "MP",
        "countryName": "Northern Mariana Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 47
        },
        {
        "countryID": 159,
        "countryAbbrev": "NO",
        "countryName": "Norway",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 64
        },
        {
        "countryID": 164,
        "countryAbbrev": "OM",
        "countryName": "Oman",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 51
        },
        {
        "countryID": 170,
        "countryAbbrev": "PK",
        "countryName": "Pakistan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 126
        },
        {
        "countryID": 177,
        "countryAbbrev": "PW",
        "countryName": "Palau",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 175,
        "countryAbbrev": "PS",
        "countryName": "Palestine",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 71
        },
        {
        "countryID": 165,
        "countryAbbrev": "PA",
        "countryName": "Panama",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 81
        },
        {
        "countryID": 168,
        "countryAbbrev": "PG",
        "countryName": "Papua New Guinea",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 178,
        "countryAbbrev": "PY",
        "countryName": "Paraguay",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 36
        },
        {
        "countryID": 166,
        "countryAbbrev": "PE",
        "countryName": "Peru",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 67
        },
        {
        "countryID": 169,
        "countryAbbrev": "PH",
        "countryName": "Philippines",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 140
        },
        {
        "countryID": 173,
        "countryAbbrev": "PN",
        "countryName": "Pitcairn",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 171,
        "countryAbbrev": "PL",
        "countryName": "Poland",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 124
        },
        {
        "countryID": 176,
        "countryAbbrev": "PT",
        "countryName": "Portugal",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 109
        },
        {
        "countryID": 174,
        "countryAbbrev": "PR",
        "countryName": "Puerto Rico",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 106
        },
        {
        "countryID": 179,
        "countryAbbrev": "QA",
        "countryName": "Qatar",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 74
        },
        {
        "countryID": 180,
        "countryAbbrev": "RE",
        "countryName": "Réunion",
        "countryAliasName": "Reunion",
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 181,
        "countryAbbrev": "RO",
        "countryName": "Romania",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 127
        },
        {
        "countryID": 182,
        "countryAbbrev": "RU",
        "countryName": "Russia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 97
        },
        {
        "countryID": 183,
        "countryAbbrev": "RW",
        "countryName": "Rwanda",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 87,
        "countryAbbrev": "GS",
        "countryName": "S. Georgia and S. Sandwich Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 190,
        "countryAbbrev": "SH",
        "countryName": "Saint Helena",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 114,
        "countryAbbrev": "KN",
        "countryName": "Saint Kitts and Nevis",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 122,
        "countryAbbrev": "LC",
        "countryName": "Saint Lucia",
        "countryAliasName": "St. Lucia",
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 255,
        "countryAbbrev": "MF",
        "countryName": "Saint Martin",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 172,
        "countryAbbrev": "PM",
        "countryName": "Saint Pierre and Miquelon",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 10
        },
        {
        "countryID": 227,
        "countryAbbrev": "VC",
        "countryName": "Saint Vincent and the Grenadines",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 234,
        "countryAbbrev": "WS",
        "countryName": "Samoa",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 195,
        "countryAbbrev": "SM",
        "countryName": "San Marino",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 199,
        "countryAbbrev": "ST",
        "countryName": "Sao Tome and Principe",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 184,
        "countryAbbrev": "SA",
        "countryName": "Saudi Arabia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 131
        },
        {
        "countryID": 196,
        "countryAbbrev": "SN",
        "countryName": "Senegal",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 28
        },
        {
        "countryID": 247,
        "countryAbbrev": "RS",
        "countryName": "Serbia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 48,
        "countryAbbrev": "CS",
        "countryName": "Serbia and Montenegro",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 186,
        "countryAbbrev": "SC",
        "countryName": "Seychelles",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 6
        },
        {
        "countryID": 194,
        "countryAbbrev": "SL",
        "countryName": "Sierra Leone",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 189,
        "countryAbbrev": "SG",
        "countryName": "Singapore",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 86
        },
        {
        "countryID": 254,
        "countryAbbrev": "SX",
        "countryName": "Sint Maarten",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 193,
        "countryAbbrev": "SK",
        "countryName": "Slovakia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 105
        },
        {
        "countryID": 191,
        "countryAbbrev": "SI",
        "countryName": "Slovenia",
        "countryAliasName": null,
        "countryEligible": 1,
        "countryOrder": 107
        },
        {
        "countryID": 185,
        "countryAbbrev": "SB",
        "countryName": "Solomon Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 197,
        "countryAbbrev": "SO",
        "countryName": "Somalia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 238,
        "countryAbbrev": "ZA",
        "countryName": "South Africa",
        "countryAliasName": null,
        "countryEligible": 1,
        "countryOrder": 69
        },
        {
        "countryID": 65,
        "countryAbbrev": "ES",
        "countryName": "Spain",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 125
        },
        {
        "countryID": 124,
        "countryAbbrev": "LK",
        "countryName": "Sri Lanka",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 137
        },
        {
        "countryID": 187,
        "countryAbbrev": "SD",
        "countryName": "Sudan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 43
        },
        {
        "countryID": 198,
        "countryAbbrev": "SR",
        "countryName": "Suriname",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 192,
        "countryAbbrev": "SJ",
        "countryName": "Svalbard and Jan Mayen",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 203,
        "countryAbbrev": "SZ",
        "countryName": "Swaziland",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 188,
        "countryAbbrev": "SE",
        "countryName": "Sweden",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 102
        },
        {
        "countryID": 40,
        "countryAbbrev": "CH",
        "countryName": "Switzerland",
        "countryAliasName": null,
        "countryEligible": 2,
        "countryOrder": 70
        },
        {
        "countryID": 202,
        "countryAbbrev": "SY",
        "countryName": "Syria",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 54
        },
        {
        "countryID": 219,
        "countryAbbrev": "TW",
        "countryName": "Taiwan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 60
        },
        {
        "countryID": 209,
        "countryAbbrev": "TJ",
        "countryName": "Tajikistan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 220,
        "countryAbbrev": "TZ",
        "countryName": "Tanzania",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 5
        },
        {
        "countryID": 208,
        "countryAbbrev": "TH",
        "countryName": "Thailand",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 77
        },
        {
        "countryID": 211,
        "countryAbbrev": "TL",
        "countryName": "Timor-Leste",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 207,
        "countryAbbrev": "TG",
        "countryName": "Togo",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 210,
        "countryAbbrev": "TK",
        "countryName": "Tokelau",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 214,
        "countryAbbrev": "TO",
        "countryName": "Tonga",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 217,
        "countryAbbrev": "TT",
        "countryName": "Trinidad and Tobago",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 9
        },
        {
        "countryID": 213,
        "countryAbbrev": "TN",
        "countryName": "Tunisia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 79
        },
        {
        "countryID": 216,
        "countryAbbrev": "TR",
        "countryName": "Turkey",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 119
        },
        {
        "countryID": 212,
        "countryAbbrev": "TM",
        "countryName": "Turkmenistan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 204,
        "countryAbbrev": "TC",
        "countryName": "Turks and Caicos Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 218,
        "countryAbbrev": "TV",
        "countryName": "Tuvalu",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 222,
        "countryAbbrev": "UG",
        "countryName": "Uganda",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 27
        },
        {
        "countryID": 221,
        "countryAbbrev": "UA",
        "countryName": "Ukraine",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 121
        },
        {
        "countryID": 243,
        "countryAbbrev": "AE",
        "countryName": "United Arab Emirates",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 113
        },
        {
        "countryID": 4,
        "countryAbbrev": "UK",
        "countryName": "United Kingdom",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 223,
        "countryAbbrev": "UM",
        "countryName": "United States Minor Outlying Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 224,
        "countryAbbrev": "UY",
        "countryName": "Uruguay",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 59
        },
        {
        "countryID": 230,
        "countryAbbrev": "VI",
        "countryName": "US Virgin Islands",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 25
        },
        {
        "countryID": 200,
        "countryAbbrev": "SU",
        "countryName": "USSR (former)",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 225,
        "countryAbbrev": "UZ",
        "countryName": "Uzbekistan",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 232,
        "countryAbbrev": "VU",
        "countryName": "Vanuatu",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 21
        },
        {
        "countryID": 226,
        "countryAbbrev": "VA",
        "countryName": "Vatican City State (Holy See)",
        "countryAliasName": "Vatican City",
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 228,
        "countryAbbrev": "VE",
        "countryName": "Venezuela",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 75
        },
        {
        "countryID": 231,
        "countryAbbrev": "VN",
        "countryName": "Viet Nam",
        "countryAliasName": "Vietnam",
        "countryEligible": -1,
        "countryOrder": 132
        },
        {
        "countryID": 233,
        "countryAbbrev": "WF",
        "countryName": "Wallis and Futuna",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 63,
        "countryAbbrev": "EH",
        "countryName": "Western Sahara",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 235,
        "countryAbbrev": "YE",
        "countryName": "Yemen",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 7
        },
        {
        "countryID": 237,
        "countryAbbrev": "YU",
        "countryName": "Yugoslavia (former)",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 240,
        "countryAbbrev": "ZR",
        "countryName": "Zaire (former)",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 239,
        "countryAbbrev": "ZM",
        "countryName": "Zambia",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 0
        },
        {
        "countryID": 241,
        "countryAbbrev": "ZW",
        "countryName": "Zimbabwe",
        "countryAliasName": null,
        "countryEligible": -1,
        "countryOrder": 29
        }
    
    ]
""".trimIndent()
}