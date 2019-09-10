package com.mcal.kotlin.data;

import com.mcal.kotlin.BuildConfig;
import com.mcal.kotlin.utils.Utils;

import static com.mcal.kotlin.data.Preferences.isOffline;

public final class Constants {

    public static final String PACKAGE_NAME = Utils.xor("てどと〨とづつな〫ねなひどはに"); // com.mcal.kotlin
    public static final String UPDATE_PATH = Utils.fromBase64("aHR0cHM6Ly90aW1zY3JpcHRvdi5naXRodWIuaW8va3QvY29uZmlnL3VwZGF0ZS54bWw="); // https://timscriptov.github.io/kt/config/update.xml
    public static final String PREMIUM = BuildConfig.DEBUG ? "android.test.purchased" : Utils.xor("のどぱなぬとずぶぷっとはばに"); // kotlin_premium
    public static final String INITIALIZE = Utils.fromBase64("Y2EtYXBwLXB1Yi0xNDExNDk1NDI3NzQxMDU1fjMyMTY5NTQxMTg="); // ca-app-pub-1411495427741055~3216954118
    public static final String ADMOB_BANNER = Utils.fromBase64("Y2EtYXBwLXB1Yi0xNDExNDk1NDI3NzQxMDU1LzU5NTkwOTI1NTQ="); // ca-app-pub-1411495427741055/5959092554
    public static final String ADMOB_INTERSTITIAL = Utils.fromBase64("Y2EtYXBwLXB1Yi0xNDExNDk1NDI3NzQxMDU1LzMyNDEwMDQ4NDI="); // ca-app-pub-1411495427741055/3241004842
    public static final String MCAL_LLC = Utils.fromBase64("aHR0cHM6Ly90aW1zY3JpcHRvdi5naXRodWIuaW8va3QvcGFnZXM="); // https://timscriptov.github.io/kt/pages
    public static final String SIGNATURE = BuildConfig.DEBUG ? "E4rlq/Cbh9SGum7fjqhTXof30eY" : Utils.fromBase64("czdtaWFCaWZ6U2RpZHlEa1JlWW5PSXM4UXBM"); // s7miaBifzSdidyDkReYnOIs8QpL
    public static final String DOWNLOAD_ZIP = Utils.fromBase64("aHR0cHM6Ly90aW1zY3JpcHRvdi5naXRodWIuaW8vbGVzc29ucy9rb3RsaW4uemlw"); // https://timscriptov.github.io/lessons/kotlin.zip
    public static final String RATE = Utils.fromBase64("bWFya2V0Oi8vZGV0YWlscz9pZD1jb20ubWNhbC5rb3RsaW4="); // market://details?id=com.mcal.kotlin

    public static final String LK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjwVwPnZ+iwu4+dAUMoO+SOY4XKsLIzsE9hCgxcgjZdKY0poUgqcadOAebfW+LkZfced9VaRodxQIXVZcdYruFW9mDMW69/dqnvjY7adYMMBpEh58E6Mq2Yt4AZYbOaquW+I6DmtvlHa34Vh3eyj3RDiaY3bS2yhpeCd5Rdwcnhjk7mE15gY+wM9bSe9AGmqx+FKlU6xww8drqWYR/xnov5seF1NuIQTzWoe0muKB+lnqhOeN9q8cOawoZqlSRagqYe9zrQ0SrfJ7N3e5nT2LpgQ33IC+Tvzkz3TwfFPbVGheuTlcWV0PweXwvaMkf2dYRcfiF4zFhIZnzKmm46aELwIDAQAB";
    public static final String MI = "00384380151574298327";
    public static final String IS_PREMIUM = Utils.xor("ぬふさぴだにぬびと"); // isPremium
    public static final String ANTIPATCH = Utils.fromBase64("Y2MuYmlubXQuc2lnbmF0dXJlLlBtc0hvb2tBcHBsaWNhdGlvbg=="); // cc.binmt.signature.PmsHookApplication
    public static final String ANTIPATCH1 = Utils.fromBase64("YW55bXkuc2lnbi5CaW5TaWduYXR1cmVGaXg="); // anymy.sign.BinSignatureFix
    public static final String ANTIPATCH2 = Utils.fromBase64("YXBrZWRpdG9yLnBhdGNoLnNpZ25hdHVyZS5GaXg="); // apkeditor.patch.signature.Fix
    public static final String ANTIPATCH3 = Utils.fromBase64("Y29tLmFueW15LnJlZmxlY3Rpb24="); // com.anymy.reflection
    public static final String ANTIPATCH4 = Utils.fromBase64("YmluLm10LmFwa3NpZ25hdHVyZWtpbGxlcnBsdXMuSG9va0FwcGxpY2F0aW9u"); // bin.mt.apksignaturekillerplus.HookApplication
    public static final String ANTIPATCH5 = Utils.fromBase64("Y2MuYmlubXQuc2lnbmF0dXJlLkhvb2s="); // cc.binmt.signature.Hook
    public static final String META_INF = Utils.xor("えぃけぇ〨くか" + "ぃ〩") + Utils.xor("ぃ〩"); // META-INF/
    public static final String RSA = Utils.xor("ござぇ"); // .RSA
    public static final String DSA = Utils.xor("あざぇ"); // .DSA
    public static final String SHA = Utils.xor("ざぎい"); // SHA
    public static final String MORE_APPS = Utils.fromBase64("bWFya2V0Oi8vc2VhcmNoP3E9cHViOtCY0LLQsNC9INCi0LjQvNCw0YjQutC+0LI="); // market://search?q=pub:Иван Тимашков
    public static final String HTML = Utils.xor("〫のぱにど"); // .html
    public static final String LESSON_PATH = Utils.xor("〪なだふぶどにす"); // /lesson_
    public static final String LESSON = Utils.xor("どっぶふなと"); // lesson
    public static final String OFFLINE = Utils.xor("なだっなぬとだ"); // offline
    public static final String FONT_SIZE = Utils.xor("っどにひずふぬぼだ"); // font_size
    public static final String LANG = Utils.xor("どでにち"); // lang
    public static final String IS_RATED = Utils.xor("ぬふしでぱっち"); // isRated
    static final String FIRSTLAUNCH = Utils.xor("っはぷふぱおつびにづね"); // firstLaunch
    static final String FONT = Utils.xor("っどにひ"); // font
    static final String BOOKMARK = Utils.xor("でどなねとでぷね"); // bookmark
    static final String FULLSCREEN_MODE = Utils.xor("っびどなぶづぷっだとずになぢだ"); // fullscreen_mode
    static final String NIGHT_MODE = Utils.xor("にはぢのぱすとどちっ"); // night_mode
    static final String GRID_MODE = Utils.xor("ぢぴぬぢずになぢだ"); // grid_mode

    public static String getResPath() {
        if (isOffline()) return "data/data/" + PACKAGE_NAME + "/files/resources/pages";
        else return MCAL_LLC;
    }
}
