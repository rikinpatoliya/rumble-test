package com.rumble.network.queryHelpers

enum class VideoCollectionId(val value: String) {
    Viral("_s1"),
    Cooking("_s2"),
    Sports("_s3"),
    Gaming("_s4"),
    News("_s5"),
    Science("_s6"),
    Technology("_s7"),
    Auto("_s9"),
    HowTo("_s10"),
    Travel("_s11"),
    Music("_s12"),
    Vlogs("_s13"),
    Podcasts("_s14"),
    Entertainment("_s15"),
    Finance("_s16"),
    EditorPicks("_f2"),
    Live("_f4"),
    MyVideos(""),

    @Deprecated(
        message = "Don't use. This is leftover from TV app and should not be in the network " +
                "layer. Blank string actually represents 'my videos' but TV was using to " +
                "represent live"
    )
    DoNotUseLive("")
}