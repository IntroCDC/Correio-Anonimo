package br.com.introcdc.correioanonimo;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public enum CA_IA {
    IA_1("WoLtnFBiHP9CdNkO3fNK5VBRD",
         "hibdCAM0srUIkcdVK26SKpYWuDcTskHUXBgDC7Or2WytISTUeE",
         "830921153329950721-BN10OZ0DPqX99K626TrOMzfpQ2TFMNY",
         "9PzzRHzFg8agC7kmSJ3bZO8FegZMYhTebWSYzXkjTb4U1"),
    IA_2("aJV6XMmSsYFnRFPCPNNFf7WpG",
         "M7NmbkljYuhiEgNvJrvdm3QnHzM1KGAuVbt0iLkhfdn6sDDLXa",
         "830921153329950721-4j1eMctlSkOeBcqvn6Vfk0nXhjS5Yx6",
         "xHswvu6yK8qEjaDlPZtcxkQjincDTWlsjx2FgFA9mP72c"),
    IA_3("Hj3FAcnPdMgVcCiuSnRAVXEht",
         "I1SbMjvGnbOWXWxxpl8acNRafPGBv95BzCS4aKFsZ6x2PlnGG1",
         "830921153329950721-EWj9MrtsFH1wYk3l7iQrpYmIx9oeVxI",
         "AExkSNVpOATUCoKoqBEiohxCkTBOFIVmHBM1sFoRakHDg");

    private boolean enabled;
    private final String OAuthAccessToken;
    private final String OAuthAccessTokenSecret;
    private final String OAuthConsumerKey;
    private final String OAuthConsumerSecret;
    private Twitter twitter;

    CA_IA(final String OAuthConsumerKey, final String OAuthConsumerSecret, final String OAuthAcessToken, final String OAuthAcessTokenSecret) {
        this.enabled = true;
        this.OAuthConsumerKey = OAuthConsumerKey;
        this.OAuthConsumerSecret = OAuthConsumerSecret;
        this.OAuthAccessToken = OAuthAcessToken;
        this.OAuthAccessTokenSecret = OAuthAcessTokenSecret;
    }

    public String getOAuthAccessToken() {
        return this.OAuthAccessToken;
    }

    public String getOAuthAccessTokenSecret() {
        return this.OAuthAccessTokenSecret;
    }

    public String getOAuthConsumerKey() {
        return this.OAuthConsumerKey;
    }

    public String getOAuthConsumerSecret() {
        return this.OAuthConsumerSecret;
    }

    public Twitter getTwitter() {
        return this.twitter;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public CA_IA login() throws Exception {
        System.out.println("Autenticando Bot " + this.toString() + "...");
        final ConfigurationBuilder conf = new ConfigurationBuilder();
        conf.setDebugEnabled(true).setOAuthConsumerKey(this.getOAuthConsumerKey()).setOAuthConsumerSecret(this.getOAuthConsumerSecret()).setOAuthAccessToken(this.getOAuthAccessToken()).setOAuthAccessTokenSecret(this.getOAuthAccessTokenSecret());
        final TwitterFactory tf = new TwitterFactory(conf.build());
        this.twitter = tf.getInstance();
        this.twitter.verifyCredentials();
        System.out.println("Bot " + this.toString() + " autenticado com sucesso!");
        return this;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
