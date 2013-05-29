#Poke-A-Droid
Poke-A-Droid is an Android application created to easily test an Android phone's lock security.
It was inspired by [p2p-adb](https://github.com/kosborn/p2p-adb) by Kyle Osborn and also uses some of his code and the AntiGuard APK.
The application was made as a proof-of-concept all-in-one android hacking app, as such it may be unstable or perform unexpectedly.

**Demo can be viewed [here](http://youtu.be/kXjgEzYRjT8)**


###Requirements
* USB On-The-Go cable/adapter
* Rooted phone
* Programmable USB-HID <-- Only for the bruteforcing of PIN codes


###Build notes
To compile the source for this project, you must download the latest OpenCV for Android SDK and move the files to the *opencv* directory from this repository.

###Usage
To use this application, you must have a USB On-The-Go adapter.

Connect two phones to each-other, with the attacking phone attached to either the host end of a USB OTG cable or the USB OTG adapter.
Download the Android gesture hash table from [here](http://www.android-forensics.com/tools/AndroidGestureSHA1.rar), extract and gzip it and move it to `<sdcard>/Android/data/com.os3.poke-a-droid/files/AndroidGestureSHA1.txt.gz`. (it's not included in the package as it's a bit big)

You will now be able to start the application and use all of the functions, apart from the brute-forcing option.
To use the brute-forcing option you must first configure a USB HID device to automatically enter 4 digit pin codes, example code for a Teensy 2 USB HID can be found [here](https://github.com/remydb/Poke-A-Droid/blob/master/teensy/usb_keyboard/example.c).
The steps for bruteforcing:
* Position attacking phone above screen of attacked phone with a good view of the screen and ONLY the screen
* Make sure neither phone moves, the image recognition is sensitive!
* Take calibration pictures for each of your attacked phone's states
* Plug in the USB HID and press the go button
* When a code is found it will be stored at: `<sdcard>/<picture dir>/PokeADroid/code.txt`
* The loop will keep going until you stop it (or until your battery dies..)
