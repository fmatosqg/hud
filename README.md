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
