# Credits
- Free fonts from http://www.keshikan.net/fonts-e.html

# Setup and compile

On app folder run <code>keytool -genkey -v -keystore amazing-hud-key.keystore -alias AmazingHud -keyalg RSA -keysize 2048 -validity 10000</code>

Choose a store password and a key password.

Create file <code>app/secrets.gradle</code> with the following contents:

<code>
ext {

    secret = [
            storePassword: 'YOUR PASSWORD HERE',
            keyPassword  : 'YOUR OTHER PASSWORD HERE'
    ]
}

</code>

# Set up your device

This version looks good on devices with low resolution. Add this to the end of your config.txt  on the first partition (type msdos). 
Tested on preview 0.4.0


```
max_usb_current=1
hdmi_group=2
hdmi_mode=87
hdmi_cvt 800 480 60 6 0 0 0
```

# Build release for Android Things console 
- `./gradlew assembleRpi3Release`
- upload to https://partner.android.com/things/console/?pli=1#/
