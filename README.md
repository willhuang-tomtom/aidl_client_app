Before running the app, you need to provide the username and password used to access Artifactory.
* File settings.gradle.kts
```
        maven {
            url = uri("https://repositories.tomtom.com/artifactory/maven-dev")
            credentials {
                username = "<USER NAME>"
                password = "<PASSWORD>"
            }
        }
```
* Steps to test
** Downlaod and install https://artifactory.tomtomgroup.com/ui/repos/tree/General/sdk-generic/adas-sdk/1.24.0-test064.14a29d55/adasis-demo-service-app-release.apk to emulator installed Android 13.0
** Build the app and install this app as a system app.
```
# Start the Android emulator with the specified AVD (Android Virtual Device) and make the system writable
/usr/local/pkg/android-sdk/emulator/emulator -avd Pixel_8_API_33 -writable-system

# Restart adb as root, remount the system and reboot the device to apply changes
adb root
adb remount
adb reboot

# Restart adb as root again after reboot and remount the system with read-write 
adb root
adb shell
mount -o rw,remount /system
exit

# Push the APK file to the /system/priv-app/ directory on the device 
adb push <this client app> /system/priv-app/client-app.apk

# Set the correct permissions for the APK file
adb shell chmod 644 /system/priv-app/client-app.apk

# Reboot the device to apply changes
adb reboot
```
