package com.mcal.kotlin.data;

import com.mcal.kotlin.BuildConfig;

import static com.mcal.kotlin.data.Preferences.isOffline;

public final class Constants {
    public static final String PREMIUM = BuildConfig.DEBUG ? "android.test.purchased" : "kotlin_premium";
    public static final String INITIALIZE = "ca-app-pub-1411495427741055~3216954118";
    public static final String ADMOB_BANNER = "ca-app-pub-1411495427741055/5959092554";
    public static final String ADMOB_INTERSTITIAL = "ca-app-pub-1411495427741055/3241004842";

    public static final String SIGNATURE = "s7miaBifzSdidyDkReYnOIs8QpL";
    public static final String SIGNATURE_2 = "s7miaBifzSdidyDkReYnOIs8QpL";
    public static final String LK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjwVwPnZ+iwu4+dAUMoO+SOY4XKsLIzsE9hCgxcgjZdKY0poUgqcadOAebfW+LkZfced9VaRodxQIXVZcdYruFW9mDMW69/dqnvjY7adYMMBpEh58E6Mq2Yt4AZYbOaquW+I6DmtvlHa34Vh3eyj3RDiaY3bS2yhpeCd5Rdwcnhjk7mE15gY+wM9bSe9AGmqx+FKlU6xww8drqWYR/xnov5seF1NuIQTzWoe0muKB+lnqhOeN9q8cOawoZqlSRagqYe9zrQ0SrfJ7N3e5nT2LpgQ33IC+Tvzkz3TwfFPbVGheuTlcWV0PweXwvaMkf2dYRcfiF4zFhIZnzKmm46aELwIDAQAB";

    public static String getResPath() {
        if (isOffline()) return "data/data/com.mcal.kotlin/files/resources/pages";
        else return "https://timscriptov.github.io/kt/pages";
    }
}